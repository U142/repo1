using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using com.ums.UmsParm;


namespace com.ums.pas.integration
{


    public class DataHandlerImpl : IDataHandler
    {

        private PASUmsDb Database;

        #region IDataHandler Members


        IDictionary<AlertTarget, List<Endpoint>> targets = new Dictionary<AlertTarget, List<Endpoint>>();
        HashSet<Endpoint> AddedEndpoints = new HashSet<Endpoint>();

        protected int CountEndpoints(SendChannel byChannel)
        {
            int returnCount = 0;
            foreach(KeyValuePair<AlertTarget, List<Endpoint>> kvp in targets)
            {
                foreach (Endpoint endPoint in kvp.Value)
                {
                    //if(endPoint is Phone &&
                    switch (byChannel)
                    {
                        case SendChannel.VOICE:
                            returnCount += (endPoint is Phone ? 1 : 0);
                            break;
                        case SendChannel.SMS:
                            returnCount += (endPoint is Phone && ((Phone)endPoint).CanReceiveSms ? 1 : 0);
                            break;
                    }
                }
            }
            return returnCount;
        }

        public int Duplicates { get; private set; }

        protected bool TryAddEndpoint(Endpoint Endpoint)
        {
            //TODO clean it first to avoid different spelling on the same number
            if (!AddedEndpoints.Contains(Endpoint))
            {
                AddedEndpoints.Add(Endpoint);
                return true;
            }
            else
            {
                ULog.write("Duplicate number found");
                ++Duplicates;
            }
            return false;
        }


        public void HandleAlert(AlertMqPayload payload)
        {
            Database = new PASUmsDb();

            foreach (AlertObject alertObject in payload.AlertTargets.OfType<AlertObject>())
            {
                using (new TimeProfiler("AlertObject"))
                {
                    //check if endpoint is added, if not add it to targets.
                    if (TryAddEndpoint(alertObject.Phone))
                    {
                        targets.Add(alertObject, new List<Endpoint>()
                        {
                            alertObject.Phone,
                        });
                    }
                }
            }
            foreach (StoredList storedList in payload.AlertTargets.OfType<StoredList>())
            {

            }
            foreach (StoredAddress storedAddress in payload.AlertTargets.OfType<StoredAddress>())
            {

            }
            foreach (StreetAddress streetAddress in payload.AlertTargets.OfType<StreetAddress>())
            {

            }
            foreach (PropertyAddress propertyAddress in payload.AlertTargets.OfType<PropertyAddress>())
            {

            }
            foreach (OwnerAddress ownerAddress in payload.AlertTargets.OfType<OwnerAddress>())
            {

            }

            //now we have all data
            foreach (ChannelConfiguration channelConfig in payload.ChannelConfigurations)
            {
                long Refno = Database.newRefno();

                //connect refno to AlertId (project)
                BBPROJECT project = new BBPROJECT();
                project.sz_projectpk = payload.AlertId.Id.ToString();
                Database.linkRefnoToProject(ref project, Refno, 0, 0);

                if (channelConfig is VoiceConfiguration)
                {
                    //prepare voice alert
                    //BBRESCHEDPROFILE
                    //BBVALID
                    //BBSENDNUM
                    //BBACTIONPROFILESEND
                    InsertMdvSendinginfoVoice(Refno, payload.AccountInvoicing, channelConfig, payload.AlertConfiguration);


                }
                else if (channelConfig is SmsConfiguration)
                {
                    //prepare sms alert
                    //SMSQREF
                    //SMSQ
                    InsertSmsQref(Refno, payload.AccountInvoicing, payload.AlertConfiguration, (SmsConfiguration) channelConfig);
                    UpdateSmsQref(Refno, CountEndpoints(SendChannel.SMS));
                    InsertSmsQ(Refno, payload.AccountInvoicing, payload.AlertConfiguration);
                }
            }

            Database.close();

        }


        private void InsertSmsQ(long Refno, AccountInvoicing Account, AlertConfiguration AlertConfig)
        {
            int itemNumber = 0;
            foreach (KeyValuePair<AlertTarget, List<Endpoint>> kvp in targets)
            {
                foreach (Endpoint endPoint in kvp.Value)
                {
                    if (endPoint is Phone && ((Phone)endPoint).CanReceiveSms)
                    {
                        String Sql = String.Format("INSERT INTO SMSQ(l_refno,l_item,l_server,l_tries,l_chanid,l_schedtime,sz_number,l_adrpk) VALUES({0}, {1}, {2}, {3}, {4}, {5}, {6}, {7})",
                                                                    Refno,
                                                                    ++itemNumber,
                                                                    Account.PrimarySmsServer,
                                                                    0,
                                                                    0,
                                                                    AlertConfig.StartImmediately ? "0" : AlertConfig.Scheduled.ToString("yyyyMMddHHmmss"),
                                                                    endPoint.Address,
                                                                    -1);
                        Database.ExecNonQuery(Sql);

                    }
                }
            }

        }


        private void UpdateSmsQref(long Refno, int Items)
        {
            String Sql = String.Empty;
            if (Items == 0)
            {
                Sql = String.Format("UPDATE SMSQREF SET l_items=0, l_status=7 WHERE l_refno={0}", Refno);
            }
            else
            {
                Sql = String.Format("UPDATE SMSQREF SET l_items={0}, l_status=4 WHERE l_refno={1}", Items, Refno);
            }
            Database.ExecNonQuery(Sql);
        }


        private void InsertSmsQref(long Refno, AccountInvoicing Account, AlertConfiguration alertConfig, SmsConfiguration smsConfig)
        {
            String Sql = String.Format("sp_sms_ins_smsqref_bcp_v2 {0}, {1}, {2}, {3}, {4}, {5}," +
                    "{6}, {7}, {8}, {9}, {10}, {11}, '{12}', '{13}', '{14}', {15}," +
                    "{16}, {17}, '{18}', {19}, {20}, '{21}', {22}, {23}, {24}, {25}," +
                    "{26}, {27}, {28}, '{29}', {30}, {31}",
                            0, //0 projectpk
                            Refno, //1
                            Account.Comppk,
                            Account.Deptpk,
                            1, //4
                            1, //5
                            Account.DeptPri, //6
                            1, //7
                            0, //8
                            alertConfig.StartImmediately ? "0" : alertConfig.Scheduled.ToString("yyyyMMddHHmmss"), //9
                            Account.PrimarySmsServer, //10
                            Account.SecondarySmsServer, //11
                            "", //12
                            smsConfig.OriginAddress.Replace("'", "''"), //13
                            alertConfig.AlertName.Replace("'", "''"), //14
                            (alertConfig.SimulationMode ? 1 : 0), //15
                            0,//parent refno //16
                            0,//expected items //17
                            smsConfig.BaseMessageContent.Replace("'", "''"), //18
                            13, //19
                            1, //20
                            "|", //21
                            0, //22
                            0, //23
                            UCommon.UGetDateNow(), //24
                            UCommon.UGetTimeNow(), //25
                            Account.Userpk, //26
                            1, //27
                            1, //28
                            Account.StdCc.Replace("'", "''"), //29
                            (long)(AdrTypes.SMS_PRIVATE | AdrTypes.SMS_COMPANY), //30
                            alertConfig.SimulationMode ? 2 : 1); //31
            Database.ExecNonQuery(Sql);
        }


        private void InsertMdvSendinginfoVoice(long Refno, 
                                        AccountInvoicing Account, 
                                        ChannelConfiguration ChannelConfig, 
                                        AlertConfiguration AlertConfig)
        {
            String Sql = String.Format("INSERT INTO MDVSENDINGINFO(sz_fields, sz_sepused, l_addresspos, l_lastantsep, l_refno, l_createdate, l_createtime, " +
                      "l_scheddate, l_schedtime, sz_sendingname, l_sendingstatus, l_companypk, l_deptpk, l_nofax, l_group, " +
                      "l_removedup, l_type, f_dynacall, l_addresstypes, l_userpk, l_maxchannels) " +
                      "VALUES('{0}', '{1}', {2}, {3}, {4}, {5}, {6}, {7}, {8}, '{9}', {10}, {11}, {12}, {13}, " +
                      "{14}, {15}, {16}, {17}, {18}, {19}, {20})",
                "",
                "|",
                0,
                0,
                Refno,
                UCommon.UGetDateNow(),
                UCommon.UGetTimeNow(),
                AlertConfig.StartImmediately ? "0" : AlertConfig.Scheduled.ToString("yyyyMMdd"),
                AlertConfig.StartImmediately ? "0" : AlertConfig.Scheduled.ToString("HHmm"),
                AlertConfig.AlertName.Replace("'", "''"),
                1,
                Account.Comppk,
                Account.Deptpk,
                1,
                1,
                1,
                1, //voice
                AlertConfig.SimulationMode ? 2 : 1,
                (long)(AdrTypes.MOBILE_PRIVATE_AND_FIXED | AdrTypes.MOBILE_COMPANY_AND_FIXED),
                Account.Userpk,
                360);
            Database.ExecNonQuery(Sql);
        }




        #endregion
    }
}
