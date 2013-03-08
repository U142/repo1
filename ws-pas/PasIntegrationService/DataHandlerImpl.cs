﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using com.ums.UmsParm;
using System.Data.Odbc;
using System.IO;
using com.ums.pas.integration.AddressLookup;


namespace com.ums.pas.integration
{


    public class DataHandlerImpl : IDataHandler
    {

        private PASUmsDb Database;

        #region IDataHandler Members


        IDictionary<AlertTarget, List<Recipient>> targets = new Dictionary<AlertTarget, List<Recipient>>();
        HashSet<Endpoint> AddedEndpoints = new HashSet<Endpoint>();

        protected int CountEndpoints(SendChannel byChannel)
        {
            int returnCount = 0;
            foreach(KeyValuePair<AlertTarget, List<Recipient>> kvp in targets)
            {
                foreach (Recipient Recipient in kvp.Value)
                {
                    foreach (Endpoint endPoint in Recipient.EndPoints)
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


        public void HandleAlert(AlertMqPayload Payload)
        {
            //PasIntegrationService.Default.DatabaseConnection
            String FolkeregDatabaseConnectionString = String.Format("DSN=vb_adr_{0}_reg; UID=sa; PWD=diginform", Payload.AccountDetails.StdCc);
            String NorwayDatabaseConnectionString = String.Format("DSN=vb_adr_{0}; UID=sa; PWD=diginform", Payload.AccountDetails.StdCc);


            Database = new PASUmsDb(System.Configuration.ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 10);

            foreach (AlertObject alertObject in Payload.AlertTargets.OfType<AlertObject>())
            {
                using (new TimeProfiler("AlertObject"))
                {
                    //check if endpoint is added, if not add it to targets.
                    //if (TryAddEndpoint(alertObject.Phone))
                    {
                        targets.Add(alertObject, new List<Recipient>()
                        {
                            new Recipient()
                            {
                                EndPoints = new List<Endpoint>()
                                        {
                                            alertObject.Phone,
                                        },
                                Attributes = new List<DataItem>()
                                {
                                    new DataItem("AlertObject", alertObject.Phone.Address),
                                }
                            }
                        });
                    }
                }
            }
            foreach (StoredList storedList in Payload.AlertTargets.OfType<StoredList>())
            {

            }
            foreach (StoredAddress storedAddress in Payload.AlertTargets.OfType<StoredAddress>())
            {

            }
            //foreach (StreetAddress streetAddress in Payload.AlertTargets.OfType<StreetAddress>())
            //{
            IStreetAddressLookupFacade streetLookupInterface = new StreetAddressLookupImpl();
            IEnumerable<String> streetAddressLookup = streetLookupInterface.GetMatchingStreetAddresses(
                                                        FolkeregDatabaseConnectionString,
                                                        Payload.AlertTargets.OfType<StreetAddress>().ToList());

            //}
            foreach (PropertyAddress propertyAddress in Payload.AlertTargets.OfType<PropertyAddress>())
            {

            }
            foreach (OwnerAddress ownerAddress in Payload.AlertTargets.OfType<OwnerAddress>())
            {

            }

            //now we have all data
            foreach (ChannelConfiguration channelConfig in Payload.ChannelConfigurations)
            {
                long Refno = Database.newRefno();

                //connect refno to AlertId (project)
                BBPROJECT project = new BBPROJECT();
                project.sz_projectpk = Payload.AlertId.Id.ToString();
                Database.linkRefnoToProject(ref project, Refno, 0, 0);

                if (channelConfig is VoiceConfiguration)
                {
                    //prepare voice alert
                    VoiceConfiguration voiceConfig = (VoiceConfiguration) channelConfig;
                    InsertMdvSendinginfoVoice(Refno, Payload.AccountDetails, channelConfig, Payload.AlertConfiguration);
                    InsertResched(Refno, voiceConfig);
                    InsertBbValid(Refno, voiceConfig.ValidDays);
                    InsertBbActionprofileSend(Refno, voiceConfig.VoiceProfilePk);
                    //if forced hidden number or no numbers assigned
                    InsertBbSendnum(Refno, voiceConfig.UseHiddenOriginAddress || Payload.AccountDetails.AvailableVoiceNumbers.Count == 0 ? "" : Payload.AccountDetails.AvailableVoiceNumbers.First());

                    CreateTtsBackboneAudioFiles(Refno, new List<String> { voiceConfig.BaseMessageContent }, Payload.AccountDetails.DefaultTtsLang);
                    
                    WriteVoiceBackboneFile(Payload.AlertConfiguration, Payload.Account, Payload.AccountDetails, Refno);
                }
                else if (channelConfig is SmsConfiguration)
                {
                    //prepare sms alert
                    //SMSQREF
                    //SMSQ
                    InsertSmsQref(Refno, Payload.AccountDetails, Payload.AlertConfiguration, (SmsConfiguration) channelConfig);
                    UpdateSmsQref(Refno, CountEndpoints(SendChannel.SMS));
                    InsertSmsQ(Refno, Payload.AccountDetails, Payload.AlertConfiguration);
                }
            }

            Database.close();

        }

        private void CreateTtsBackboneAudioFiles(long Refno, List<String> Messages, int LangPk)
        {
            try
            {
                int counter = 0;
                ITtsFacade ttsFacade = new TtsFacadeImpl();
                foreach (String Message in Messages)
                {
                    byte[] bytes = ttsFacade.ConvertTtsRaw(Message, LangPk);
                    String publishFile = System.Configuration.ConfigurationManager.AppSettings["BackboneEatPath"] + "\\" + GetVoiceFilenameFor(Refno, ++counter);
                    File.WriteAllBytes(publishFile, bytes);
                }
            }
            catch (Exception e)
            {
                String errorText = String.Format("Unable to create TTS on Refno={0}\n{1}", Refno, e);
                ULog.error(errorText);
                Console.WriteLine(errorText);
                throw e;
            }
        }

        private String GetVoiceFilenameFor(long Refno, int FileNo)
        {
            return String.Format(@"v{0}_{1}.raw", Refno, FileNo);
        }


        /// <summary>
        /// Write voice address file and move it to backbone eat path
        /// </summary>
        /// <param name="Refno"></param>
        private void WriteVoiceBackboneFile(AlertConfiguration AlertConfiguration, Account Account, AccountDetails AccountDetails, long Refno)
        {
            try
            {
                String tempFile = String.Format("{0}\\d{1}.tmp", System.Configuration.ConfigurationManager.AppSettings["BackboneEatPath"], Refno);
                String publishFile = String.Format("{0}\\d{1}.adr", System.Configuration.ConfigurationManager.AppSettings["BackboneEatPath"], Refno);
                TextWriter tw = new StreamWriter(tempFile, false, Encoding.GetEncoding("ISO-8859-1"));
                tw.WriteLine("/MDV");
                tw.WriteLine("/MPC");
                tw.WriteLine(String.Format("/Company={0}", Account.CompanyId));
                tw.WriteLine(String.Format("/Department={0}", Account.DepartmentId));
                tw.WriteLine(String.Format("/Pri={0}", AccountDetails.DeptPri));
                tw.WriteLine(String.Format("/Channels={0}", AccountDetails.MaxVoiceChannels));
                tw.WriteLine(String.Format("/SchedDate={0}", AlertConfiguration.StartImmediately ? DateTime.Now.ToString("yyyyMMdd") : AlertConfiguration.Scheduled.ToString("yyyyMMdd")));
                tw.WriteLine(String.Format("/SchedTime={0}", AlertConfiguration.StartImmediately ? DateTime.Now.ToString("HHmm") : AlertConfiguration.Scheduled.ToString("HHmm")));
                tw.WriteLine(String.Format("/Item=1"));
                tw.WriteLine(String.Format("/Name={0}", AlertConfiguration.AlertName));

                //Dynamic voice
                //foreach
                int counter = 0;
                //file exists
                tw.WriteLine(String.Format("/FILE={0}", GetVoiceFilenameFor(Refno, ++counter)));



                foreach (KeyValuePair<AlertTarget, List<Recipient>> kvp in targets)
                {
                    foreach (Recipient Recipient in kvp.Value)
                    {
                        //tw.WriteLine(String.Format("/PCODE {0}", ""));
                        if (AlertConfiguration.SimulationMode)
                        {
                            tw.Write(String.Format("/SIMU NA"));
                        }
                        else
                        {
                            tw.Write(String.Format("/DCALL NA"));
                        }
                        //iterate numbers
                        bool started = false;
                        foreach (Endpoint endPoint in Recipient.EndPoints)
                        {
                            tw.Write(String.Format("{0}{1}", started ? "," : " ", endPoint.Address));
                            started = true;
                        }
                        tw.WriteLine("");
                    }
                }

                tw.Close();
                File.Move(tempFile, publishFile);
            }
            catch (Exception e)
            {
                String errorText = String.Format("Unable to write backbone address file for refno {0}\n{1}", Refno, e);
                ULog.error(errorText);
                Console.WriteLine(errorText);
                throw e;
            }
        }


        /// <summary>
        /// Write mdvhist data to comply with GAS UI
        /// </summary>
        private void WriteToMdvhist()
        {
            //TODO
        }

        /// <summary>
        /// Write meta data to database to be able to lookup why a recipient was called (e.g. living on a streetaddress)
        /// </summary>
        private void WriteMetaData()
        {
            //TODO
        }

        private void InsertBbActionprofileSend(long Refno, int ProfilePk)
        {
            String Sql = String.Format("INSERT INTO BBACTIONPROFILESEND(l_refno, l_actionprofilepk) VALUES({0}, {1})",
                                        Refno, ProfilePk);
            Database.ExecNonQuery(Sql);
        }

        /// <summary>
        /// Voice insert origin number for voice alert.
        /// If blank is specified, backbone uses hidden number
        /// </summary>
        /// <param name="Refno"></param>
        /// <param name="number"></param>
        private void InsertBbSendnum(long Refno, String Number)
        {
            String Sql = String.Format("INSERT INTO BBSENDNUM(l_refno, sz_number) VALUES({0}, '{1}')", Refno, Number);
            Database.ExecNonQuery(Sql);
        }

        /// <summary>
        /// Voice Insert validity in days for callback
        /// </summary>
        /// <param name="Refno"></param>
        /// <param name="ValidDays"></param>
        private void InsertBbValid(long Refno, int ValidDays)
        {
            String Sql = String.Format("INSERT INTO BBVALID(l_valid, l_refno) VALUES({0}, {1})", ValidDays, Refno);
            Database.ExecNonQuery(Sql);
        }

        /// <summary>
        /// Voice Insert resched profile parameters, retries etc.
        /// </summary>
        /// <param name="Refno"></param>
        /// <param name="VoiceConfig"></param>
        private void InsertResched(long Refno, VoiceConfiguration VoiceConfig)
        {
            String Sql = String.Format("INSERT INTO BBDYNARESCHED(l_refno, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval) " +
                                        "VALUES({0}, {1}, {2}, {3}, {4}, {5}, {6})",
                                        Refno,
                                        VoiceConfig.Repeats,
                                        VoiceConfig.FrequencyMinutes,
                                        -1,
                                        -1,
                                        VoiceConfig.PauseAtTime,
                                        VoiceConfig.PauseDurationMinutes);
            Database.ExecNonQuery(Sql);
        }

        private void InsertSmsQ(long Refno, AccountDetails Account, AlertConfiguration AlertConfig)
        {
            int itemNumber = 0;
            foreach (KeyValuePair<AlertTarget, List<Recipient>> kvp in targets)
            {
                foreach (Recipient recipient in kvp.Value)
                {
                    foreach (Endpoint endPoint in recipient.EndPoints)
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


        private void InsertSmsQref(long Refno, AccountDetails Account, AlertConfiguration alertConfig, SmsConfiguration smsConfig)
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
                                        AccountDetails Account, 
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
