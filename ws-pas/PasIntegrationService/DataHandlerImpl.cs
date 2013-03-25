using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using com.ums.UmsParm;
using System.Data.Odbc;
using System.IO;
using com.ums.pas.integration.AddressLookup;
using log4net;
using com.ums.pas.integration.TimeProfileDb;
using com.ums.pas.integration.AddressCleanup;


namespace com.ums.pas.integration
{


    public class DataHandlerImpl : IDataHandler
    {
        private static ILog log = LogManager.GetLogger(typeof(DataHandlerImpl));

        private PASUmsDb Database;
        private IDuplicateCleaner DuplicateCleaner = new DuplicateCleanerImpl();

        #region IDataHandler Members


        //IDictionary<AlertTarget, List<Recipient>> targets = new Dictionary<AlertTarget, List<Recipient>>();
        HashSet<Endpoint> AddedEndpoints = new HashSet<Endpoint>();
        ITimeProfilerCollector timeProfileCollector = new TimeProfileDb.TimeProfileImpl();


        List<RecipientData> recipientDataList = new List<RecipientData>();

        protected int CountEndpoints(SendChannel byChannel)
        {
            int returnCount = 0;
            foreach (RecipientData recipientData in recipientDataList)
            {
                foreach (Endpoint endPoint in recipientData.Endpoints)
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
                log.Warn("Duplicate number found");
                ++Duplicates;
            }
            return false;
        }


        public void HandleAlert(AlertMqPayload Payload)
        {
            //PasIntegrationService.Default.DatabaseConnection
            String folkeregConfig = System.Configuration.ConfigurationManager.ConnectionStrings["adrdb_folkereg"].ConnectionString;
            String regularConfig = System.Configuration.ConfigurationManager.ConnectionStrings["adrdb_regular"].ConnectionString;
            String FolkeregDatabaseConnectionString = String.Format(folkeregConfig, Payload.AccountDetails.StdCc);
            String NorwayDatabaseConnectionString = String.Format(regularConfig, Payload.AccountDetails.StdCc);


            Database = new PASUmsDb(System.Configuration.ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 10);

            List<AlertObject> alertObjectList = Payload.AlertTargets.OfType<AlertObject>().ToList();
            using (new TimeProfiler(Payload.AlertId.Id, String.Format("AlertObject {0} records", alertObjectList.Count), timeProfileCollector, new TimeProfilerCallbackImpl()))
            {
                foreach (AlertObject alertObject in alertObjectList)
                {
                    recipientDataList.Add(new RecipientData()
                    {
                        AlertTarget = alertObject,
                        Endpoints = alertObject.Endpoints,
                        Name = alertObject.Name,
                        Address = "",
                        Postno = 0,
                        PostPlace = "",
                    });                         
                }
            }

            List<FollowupAlertObject> followupAlertObjectList = Payload.AlertTargets.OfType<FollowupAlertObject>().ToList();
            using (new TimeProfiler(Payload.AlertId.Id, String.Format("FollowupAlertObject {0} records", followupAlertObjectList.Count), timeProfileCollector, new TimeProfilerCallbackImpl()))
            {
                foreach (FollowupAlertObject followUp in followupAlertObjectList)
                {
                    IFollowupLookupFacade followupLookupInterface = new FollowupLookupImpl();
                    IEnumerable<RecipientData> followupAddressLookup = followupLookupInterface.GetRecipientsFromEarlierAlerts(
                                                                                                                        System.Configuration.ConfigurationManager.ConnectionStrings["backbone"].ConnectionString,
                                                                                                                        followupAlertObjectList);
                    recipientDataList.AddRange(followupAddressLookup);              
                }
            }
        
            ///Functionality for stored lists and addresses are not in scope of first version.
            /*foreach (StoredList storedList in Payload.AlertTargets.OfType<StoredList>())
            {

            }
            foreach (StoredAddress storedAddress in Payload.AlertTargets.OfType<StoredAddress>())
            {

            }*/
            List<StreetAddress> streetAddressList = Payload.AlertTargets.OfType<StreetAddress>().ToList();
            using (new TimeProfiler(Payload.AlertId.Id, String.Format("StreetId {0} records", streetAddressList.Count), timeProfileCollector, new TimeProfilerCallbackImpl()))
            {
                IStreetAddressLookupFacade streetLookupInterface = new StreetAddressLookupImpl();
                IEnumerable<RecipientData> streetAddressLookup = streetLookupInterface.GetMatchingStreetAddresses(
                                                            FolkeregDatabaseConnectionString,
                                                            streetAddressList);
                recipientDataList.AddRange(streetAddressLookup);
            }
            List<PropertyAddress> propertyAddressList = Payload.AlertTargets.OfType<PropertyAddress>().ToList();
            using (new TimeProfiler(Payload.AlertId.Id, String.Format("PropertyAddress {0} records", propertyAddressList.Count), timeProfileCollector, new TimeProfilerCallbackImpl()))
            {
                IPropertyAddressLookupFacade propertyLookupInterface = new PropertyAddressLookupImpl();
                IEnumerable<RecipientData> propertyLookup = propertyLookupInterface.GetMatchingPropertyAddresses(
                                                                                FolkeregDatabaseConnectionString,
                                                                                propertyAddressList);
                recipientDataList.AddRange(propertyLookup);
            }

            try
            {
                //TODO - remove this try catch and implement other way of verifying owner address support.
                //if full text search is not activated on right database/view, this will crash.
                List<OwnerAddress> ownerAddressList = Payload.AlertTargets.OfType<OwnerAddress>().ToList();
                using (new TimeProfiler(Payload.AlertId.Id, String.Format("OwnerAddress {0} records", ownerAddressList.Count), timeProfileCollector, new TimeProfilerCallbackImpl()))
                {
                    log.InfoFormat("AlertId={0} - First owner run", Payload.AlertId.Id);
                    IOwnerLookupFacade ownerLookupInterface = new OwnerLookupImpl();
                    IEnumerable<RecipientData> ownerLookup1 = ownerLookupInterface.GetMatchingOwnerAddresses(
                                                                                        FolkeregDatabaseConnectionString,
                                                                                        Payload.AlertTargets.OfType<OwnerAddress>().ToList());
                    recipientDataList.AddRange(ownerLookup1);

                    if (ownerLookupInterface.GetNoMatchList().Count() > 0)
                    {
                        log.InfoFormat("AlertId={0} - Second owner run, {1} properties not found in first", Payload.AlertId.Id, ownerLookupInterface.GetNoMatchList().Count());
                        IEnumerable<RecipientData> ownerLookup2 = ownerLookupInterface.GetMatchingOwnerAddresses(
                                                                                            NorwayDatabaseConnectionString,
                                                                                            ownerLookupInterface.GetNoMatchList().ToList());
                        recipientDataList.AddRange(ownerLookup2);
                        if (ownerLookupInterface.GetNoMatchList().Count() > 0)
                        {
                            log.InfoFormat("AlertId={0} - Still {1} properties without owner, register empty recipient data for log purposes", Payload.AlertId.Id, ownerLookupInterface.GetNoMatchList().Count());
                            foreach (OwnerAddress ownerAddress in ownerLookupInterface.GetNoMatchList())
                            {
                                recipientDataList.Add(new RecipientData()
                                {
                                    AlertTarget = ownerAddress,
                                    Name = "<No inhabitants found>",
                                });
                            }
                        }
                    }

                }
            }
            catch (Exception e)
            {
                log.ErrorFormat("AlertId={0} - Error looking up owner address with error {1}", Payload.AlertId.Id, e.Message);
                ULog.error("Error looking up owner address with error {0}", e.ToString());
            }


            //now we have all data
            //do a duplicate cleanup
            recipientDataList = DuplicateCleaner.DuplicateCleanup(recipientDataList);
            

            foreach (ChannelConfiguration channelConfig in Payload.ChannelConfigurations)
            {
                int Refno = (int) Database.newRefno();

                //connect refno to AlertId (project)
                BBPROJECT project = new BBPROJECT();
                project.sz_projectpk = Payload.AlertId.Id.ToString();
                Database.linkRefnoToProject(ref project, Refno, 0, 0);
                if (channelConfig is VoiceConfiguration)
                {
                    VoiceConfiguration voiceConfig = (VoiceConfiguration)channelConfig;
                    using (new TimeProfiler(Payload.AlertId.Id, "Voice database/file inserts", timeProfileCollector, new TimeProfilerCallbackImpl()))
                    {
                        //prepare voice alert
                        InsertMdvSendinginfoVoice(Refno, Payload.AccountDetails, channelConfig, Payload.AlertConfiguration);
                        InsertResched(Refno, voiceConfig);
                        InsertBbValid(Refno, voiceConfig.ValidDays);
                        InsertBbActionprofileSend(Refno, voiceConfig.VoiceProfilePk);
                        //if forced hidden number or no numbers assigned
                        InsertBbSendnum(Refno, voiceConfig.UseHiddenOriginAddress || Payload.AccountDetails.AvailableVoiceNumbers.Count == 0 ? "" : Payload.AccountDetails.AvailableVoiceNumbers.First());
                    }
                    CreateTtsBackboneAudioFiles(Payload.AlertId, Refno, new List<String> { voiceConfig.BaseMessageContent }, Payload.AccountDetails.DefaultTtsLang);

                    int numberOfVoiceRecipients = WriteVoiceBackboneFile(Payload.AlertId, Payload.AlertConfiguration, Payload.Account, Payload.AccountDetails, Refno);
                    log.InfoFormat("AlertId={0} Created VOICE alert to {1} recipients", Payload.AlertId.Id, numberOfVoiceRecipients);
                }
                else if (channelConfig is SmsConfiguration)
                {
                    //prepare sms alert
                    //SMSQREF
                    //SMSQ
                    using (new TimeProfiler(Payload.AlertId.Id, "SMS database inserts", timeProfileCollector, new TimeProfilerCallbackImpl()))
                    {
                        InsertSmsQref(Refno, Payload.AccountDetails, Payload.AlertConfiguration, (SmsConfiguration)channelConfig);
                        UpdateSmsQref(Refno, CountEndpoints(SendChannel.SMS));
                        int numberOfSmsRecipients = InsertSmsQ(Refno, Payload.AccountDetails, Payload.AlertConfiguration);
                        log.InfoFormat("AlertId={0} Created SMS alert to {1} recipients", Payload.AlertId.Id, numberOfSmsRecipients);
                    }
                }
            }
            using (new TimeProfiler(Payload.AlertId.Id, "Write to MDVHIST", timeProfileCollector, new TimeProfilerCallbackImpl()))
            {
                WriteToMdvhist(recipientDataList);
            }
            using (new TimeProfiler(Payload.AlertId.Id, "Write Metadata to MDVHIST_ADDRESS_SOURCE", timeProfileCollector, new TimeProfilerCallbackImpl()))
            {
                WriteMetaData(Payload.AlertId, recipientDataList);
            }

            Database.close();

        }

        private void CreateTtsBackboneAudioFiles(AlertId AlertId, int Refno, List<String> Messages, int LangPk)
        {
            using (new TimeProfiler(AlertId.Id, "Text to Speech", timeProfileCollector, new TimeProfilerCallbackImpl()))
            {
                try
                {
                    int counter = 0;
                    ITtsFacade ttsFacade = new TtsFacadeImpl();
                    foreach (String Message in Messages)
                    {
                        AudioContent audioContent = ttsFacade.ConvertTtsRaw(Message, LangPk);
                        ++counter;
                        String publishFile = System.Configuration.ConfigurationManager.AppSettings["BackboneEatPath"] + "\\" + GetVoiceFilenameFor(Refno, counter, "raw");
                        File.WriteAllBytes(publishFile, audioContent.RawVersion);
                        String publishWave = System.Configuration.ConfigurationManager.AppSettings["BBMessages"] + "\\" + GetVoiceFilenameFor(Refno, counter, "wav");
                        File.WriteAllBytes(publishWave, audioContent.WavVersion);
                        Database.InsertTtsRef(Refno, counter, Message);
                    }
                }
                catch (Exception e)
                {
                    String errorText = String.Format("Unable to create TTS on Refno={0}\n{1}", Refno, e);
                    ULog.error(errorText);
                    log.Error(errorText);
                    throw e;
                }
            }
        }

        private String GetVoiceFilenameFor(long Refno, int FileNo, String extension)
        {
            return String.Format(@"v{0}_{1}.{2}", Refno, FileNo, extension);
        }


        /// <summary>
        /// Write voice address file and move it to backbone eat path
        /// </summary>
        /// <param name="Refno"></param>
        private int WriteVoiceBackboneFile(AlertId AlertId, AlertConfiguration AlertConfiguration, Account Account, AccountDetails AccountDetails, long Refno)
        {
            int voiceRecipients = 0;
            using (new TimeProfiler(AlertId.Id, "Write address file to backbone", timeProfileCollector, new TimeProfilerCallbackImpl()))
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
                    tw.WriteLine(String.Format("/FILE={0}", GetVoiceFilenameFor(Refno, ++counter, "raw")));

                    int itemCount = 0;
                    foreach (RecipientData recipientData in recipientDataList)
                    {
                        //TODO - now only fixed numbers should get voice, may change later
                        bool doContinue = false;
                        foreach (Endpoint endPoint in recipientData.Endpoints)
                        {
                            if (endPoint is Phone && !((Phone)endPoint).CanReceiveSms)
                            {
                                doContinue = true;
                                break;
                            }
                        }
                        //only sms numbers here, ignore.
                        if (!doContinue)
                        {
                            continue;
                        }

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
                        foreach (Endpoint endPoint in recipientData.Endpoints)
                        {
                            //only send to phones that cannot receive SMS
                            if (endPoint is Phone && !((Phone)endPoint).CanReceiveSms)
                            {
                                tw.Write(String.Format("{0}{1}", started ? "," : " ", endPoint.Address));
                                started = true;
                            }
                        }
                        tw.WriteLine("");
                        recipientData.AlertLink.Add(RecipientData.newRefnoItem(Refno, ++itemCount));
                        ++voiceRecipients;
                    }

                    tw.Close();
                    File.Move(tempFile, publishFile);
                }
                catch (Exception e)
                {
                    String errorText = String.Format("Unable to write backbone address file for refno {0}\n{1}", Refno, e);
                    ULog.error(errorText);
                    log.Error(errorText);
                    throw e;
                }
            }
            return voiceRecipients;
        }


        /// <summary>
        /// Write mdvhist data to comply with GAS UI
        /// </summary>
        private void WriteToMdvhist(List<RecipientData> recipientData)
        {
            string Sql = "INSERT INTO MDVHIST(l_refno, l_item, sz_fields, l_adrpk2, l_adrpk, l_xcoord, l_ycoord, sz_adrinfo, l_lon, l_lat, sz_pin1, sz_pin2, l_prioritized) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            using (OdbcCommand cmd = Database.CreateCommand(Sql))
            {
                cmd.Parameters.Add("l_refno", OdbcType.Int);
                cmd.Parameters.Add("l_item", OdbcType.Int);
                cmd.Parameters.Add("sz_fields", OdbcType.VarChar, 1000);
                cmd.Parameters.Add("l_adrpk2", OdbcType.Int);
                cmd.Parameters.Add("l_adrpk", OdbcType.Numeric);
                cmd.Parameters.Add("l_xcoord", OdbcType.Int);
                cmd.Parameters.Add("l_ycoord", OdbcType.Int);
                cmd.Parameters.Add("sz_adrinfo", OdbcType.VarChar, 255);
                cmd.Parameters.Add("l_lon", OdbcType.Double);
                cmd.Parameters.Add("l_lat", OdbcType.Double);
                cmd.Parameters.Add("sz_pin1", OdbcType.VarChar, 10);
                cmd.Parameters.Add("sz_pin2", OdbcType.VarChar, 10);
                cmd.Parameters.Add("l_prioritized", OdbcType.TinyInt);

                foreach (RecipientData data in recipientData)
                {
                    foreach(RecipientData.RefnoItem alertLink in data.AlertLink)
                    {
                            cmd.Parameters["l_refno"].Value = alertLink.Refno;
                            cmd.Parameters["l_item"].Value = alertLink.Item;
                            cmd.Parameters["sz_fields"].Value = "";
                            cmd.Parameters["l_adrpk2"].Value = data.KonDmid;
                            cmd.Parameters["l_adrpk"].Value = -1;
                            cmd.Parameters["l_xcoord"].Value = 0;
                            cmd.Parameters["l_ycoord"].Value = 0;
                            cmd.Parameters["sz_adrinfo"].Value = data.AddressLine;
                            cmd.Parameters["l_lon"].Value = data.Lon;
                            cmd.Parameters["l_lat"].Value = data.Lat;
                            cmd.Parameters["sz_pin1"].Value = "";
                            cmd.Parameters["sz_pin2"].Value = "";
                            cmd.Parameters["l_prioritized"].Value = 0;
                        
                            cmd.ExecuteNonQuery();
                    }
                }
            }
        }

        /// <summary>
        /// Write meta data to database to be able to lookup why a recipient was called (e.g. living on a streetaddress)
        /// </summary>
        private void WriteMetaData(AlertId AlertId, List<RecipientData> recipientData)
        {
            String Sql = "sp_ins_mdvAddressSource ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
            using (OdbcCommand cmd = Database.CreateCommand(Sql))
            {
                cmd.Parameters.Add("projectpk", OdbcType.Numeric);
                cmd.Parameters.Add("company", OdbcType.Int);
                cmd.Parameters.Add("Name", OdbcType.VarChar, 50);
                cmd.Parameters.Add("alertTarget", OdbcType.Int);
                cmd.Parameters.Add("municipalId", OdbcType.Int);
                cmd.Parameters.Add("streetId", OdbcType.Int);
                cmd.Parameters.Add("houseNo", OdbcType.Int);
                cmd.Parameters.Add("houseLetter", OdbcType.VarChar, 5);
                cmd.Parameters.Add("oppgang", OdbcType.VarChar, 5);
                cmd.Parameters.Add("gnr", OdbcType.Int);
                cmd.Parameters.Add("bnr", OdbcType.Int);
                cmd.Parameters.Add("fnr", OdbcType.Int);
                cmd.Parameters.Add("snr", OdbcType.Int);
                cmd.Parameters.Add("unr", OdbcType.Int);
                cmd.Parameters.Add("postnr", OdbcType.Int);
                cmd.Parameters.Add("data", OdbcType.VarChar, 1000);
                cmd.Parameters.Add("birthdate", OdbcType.Int);
                cmd.Parameters.Add("attr", OdbcType.VarChar, 8000);
                cmd.Parameters.Add("extid", OdbcType.VarChar, 50);

                foreach (RecipientData recipient in recipientData)
                {
                    //The recipient wasn't linked to any targets, add the recipient here with reference to project only, not refno/item.
                    if (recipient.AlertLink.Count == 0)
                    {
                    }

                    int streetid = 0, houseno = 0, gnr = 0, bnr = 0, fnr = 0, snr = 0, unr = 0, postnr = 0, birthdate = 0;
                    String municipalid = "0", letter = "", oppgang = "", data = "", externalId = "";
                    if (recipient.AlertTarget is StreetAddress)
                    {
                        StreetAddress streetAddress = (StreetAddress)recipient.AlertTarget;
                        municipalid = streetAddress.MunicipalCode;
                        streetid = streetAddress.StreetNo;
                        houseno = streetAddress.HouseNo;
                        letter = streetAddress.Letter;
                    }
                    else if (recipient.AlertTarget is PropertyAddress)
                    {
                        PropertyAddress propertyAddress = (PropertyAddress)recipient.AlertTarget;
                        municipalid = propertyAddress.MunicipalCode;
                        gnr = propertyAddress.Gnr;
                        bnr = propertyAddress.Bnr;
                        fnr = propertyAddress.Fnr;
                        unr = propertyAddress.Unr;

                    }
                    else if (recipient.AlertTarget is OwnerAddress)
                    {
                        DateTime DateOfBirth;

                        // TODO: insert alert target data
                        OwnerAddress ownerAddress = (OwnerAddress)recipient.AlertTarget;

                        if (ownerAddress.DateOfBirth.Length == 6 && DateTime.TryParseExact(ownerAddress.DateOfBirth, "ddMMyy", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out DateOfBirth))
                            birthdate = int.Parse(DateOfBirth.ToString("yyyyMMdd"));
                        else if (ownerAddress.DateOfBirth.Length == 8 && DateTime.TryParseExact(ownerAddress.DateOfBirth, "yyyyMMdd", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out DateOfBirth))
                            birthdate = int.Parse(DateOfBirth.ToString("yyyyMMdd"));
                        else if (ownerAddress.DateOfBirth.Length == 8 && DateTime.TryParseExact(ownerAddress.DateOfBirth, "ddMMyyyy", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out DateOfBirth))
                            birthdate = int.Parse(DateOfBirth.ToString("yyyyMMdd"));
                        else if (!int.TryParse(ownerAddress.DateOfBirth, out birthdate))
                            birthdate = 0;

                        postnr = ownerAddress.Postnr;
                        data =
                                    ownerAddress.Adresselinje1
                            + "|" + ownerAddress.Adresselinje2
                            + "|" + ownerAddress.Adresselinje3
                            + "|" + ownerAddress.EierIdKode.ToString()
                            + "|" + ownerAddress.EierKategoriKode.ToString()
                            + "|" + ownerAddress.EierStatusKode.ToString();
                        externalId = ownerAddress.EierId.ToString();
                    }
                    else if (recipient.AlertTarget is AlertObject)
                    {
                        // TODO: insert alert target data
                        externalId = ((AlertObject)recipient.AlertTarget).ExternalId;
                    }

                    if (municipalid == null || municipalid.Length == 0)
                    {
                        municipalid = "0";
                    }
                    // build attribute string, pipe-separated key=value pairs
                    /*StringBuilder customAttributes = new StringBuilder("");
                    foreach (DataItem attribute in recipient.AlertTarget.Attributes)
                    {
                        customAttributes.Append(attribute.Key.Replace("=", "-").Replace("|", "-"));
                        customAttributes.Append("=");
                        customAttributes.Append(attribute.Value.Replace("=", "-").Replace("|", "-"));
                        customAttributes.Append("|");
                    }*/

                    int tmp = 0;
                    long alertSourcePk = -1;

                    cmd.Parameters["projectpk"].Value = AlertId.Id;
                    cmd.Parameters["company"].Value = recipient.Company ? 1 : 0;
                    cmd.Parameters["Name"].Value = recipient.Name != null ? recipient.Name : "";
                    cmd.Parameters["alertTarget"].Value = AlertTarget.DiscriminatorValue(recipient.AlertTarget);
                    cmd.Parameters["municipalId"].Value = Int32.TryParse(municipalid, out tmp) ? Int32.Parse(municipalid) : 0;
                    cmd.Parameters["streetId"].Value = streetid;
                    cmd.Parameters["houseNo"].Value = houseno;
                    cmd.Parameters["houseLetter"].Value = letter != null ? letter : "";
                    cmd.Parameters["oppgang"].Value = oppgang != null ? oppgang : "";
                    cmd.Parameters["gnr"].Value = gnr;
                    cmd.Parameters["bnr"].Value = bnr;
                    cmd.Parameters["fnr"].Value = fnr;
                    cmd.Parameters["snr"].Value = snr;
                    cmd.Parameters["unr"].Value = unr;
                    cmd.Parameters["postnr"].Value = postnr;
                    cmd.Parameters["data"].Value = data != null ? data : "";
                    cmd.Parameters["birthdate"].Value = birthdate;
                    cmd.Parameters["attr"].Value = DataItem.FromList(recipient.AlertTarget.Attributes);
                    cmd.Parameters["extid"].Value = externalId == null ? (object)DBNull.Value : externalId;
                    using (OdbcDataReader rs = cmd.ExecuteReader())
                    {
                        if (rs.Read())
                        {
                            alertSourcePk = rs.GetInt64(0);
                        }
                    }

                    //insert alert links
                    Sql = "INSERT INTO MDVHIST_ADDRESS_SOURCE_ALERTS VALUES(?,?,?)";
                    using (OdbcCommand cmdLink = Database.CreateCommand(Sql))
                    {
                        cmdLink.Parameters.Add("alertSourcePk", OdbcType.Numeric);
                        cmdLink.Parameters.Add("refno", OdbcType.Int);
                        cmdLink.Parameters.Add("item", OdbcType.Int);

                        foreach (RecipientData.RefnoItem alertLink in recipient.AlertLink)
                        {
                            cmdLink.Parameters["alertSourcePk"].Value = alertSourcePk;
                            cmdLink.Parameters["refno"].Value = alertLink.Refno;
                            cmdLink.Parameters["item"].Value = alertLink.Item;
                            cmdLink.ExecuteNonQuery();
                        }
                    }
                }
            }
        }

        private void InsertBbActionprofileSend(int Refno, int ProfilePk)
        {
            String Sql = "INSERT INTO BBACTIONPROFILESEND(l_refno, l_actionprofilepk) VALUES(?, ?)";
            using (OdbcCommand cmd = Database.CreateCommand(Sql))
            {
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = Refno;
                cmd.Parameters.Add("l_actionprofilepk", OdbcType.Int).Value = ProfilePk;
                cmd.ExecuteNonQuery();
            }
        }

        /// <summary>
        /// Voice insert origin number for voice alert.
        /// If blank is specified, backbone uses hidden number
        /// </summary>
        /// <param name="Refno"></param>
        /// <param name="number"></param>
        private void InsertBbSendnum(int Refno, String Number)
        {
            String Sql = "INSERT INTO BBSENDNUM(l_refno, sz_number) VALUES(?, ?)";
            using (OdbcCommand cmd = Database.CreateCommand(Sql))
            {
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = Refno;
                cmd.Parameters.Add("sz_number", OdbcType.VarChar, 20).Value = Number;;
                cmd.ExecuteNonQuery();
            }
        }

        /// <summary>
        /// Voice Insert validity in days for callback
        /// </summary>
        /// <param name="Refno"></param>
        /// <param name="ValidDays"></param>
        private void InsertBbValid(long Refno, int ValidDays)
        {
            String Sql = "INSERT INTO BBVALID(l_valid, l_refno) VALUES(?, ?)";
            using (OdbcCommand cmd = Database.CreateCommand(Sql))
            {
                cmd.Parameters.Add("l_valid", OdbcType.Int).Value = ValidDays;
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = Refno;
                cmd.ExecuteNonQuery();
            }
        }

        /// <summary>
        /// Voice Insert resched profile parameters, retries etc.
        /// </summary>
        /// <param name="Refno"></param>
        /// <param name="VoiceConfig"></param>
        private void InsertResched(long Refno, VoiceConfiguration VoiceConfig)
        {
            String Sql = "INSERT INTO BBDYNARESCHED(l_refno, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval) "
                                        + "VALUES(?, ?, ?, ?, ?, ?, ?)";
            using (OdbcCommand cmd = Database.CreateCommand(Sql))
            {
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = Refno;
                cmd.Parameters.Add("l_retries", OdbcType.TinyInt).Value = VoiceConfig.Repeats;
                cmd.Parameters.Add("l_interval", OdbcType.SmallInt).Value = VoiceConfig.FrequencyMinutes;
                cmd.Parameters.Add("l_canceltime", OdbcType.SmallInt).Value = -1;
                cmd.Parameters.Add("l_canceldate", OdbcType.Int).Value = -1;
                cmd.Parameters.Add("l_pausetime", OdbcType.SmallInt).Value = VoiceConfig.PauseAtTime;
                cmd.Parameters.Add("l_pauseinterval", OdbcType.Int).Value = VoiceConfig.PauseDurationMinutes;

                cmd.ExecuteNonQuery();
            }
        }

        private int InsertSmsQ(long Refno, AccountDetails Account, AlertConfiguration AlertConfig)
        {
            int itemNumber = 0;
            String Sql = "INSERT INTO SMSQ(l_refno,l_item,l_server,l_tries,l_chanid,l_schedtime,sz_referenceid,sz_number,l_adrpk,l_concatproc) VALUES(?,?,?,?,?,?,?,?,?,?)";
            using (OdbcCommand cmd = Database.CreateCommand(Sql))
            {
                cmd.Parameters.Add("l_refno", OdbcType.Int);
                cmd.Parameters.Add("l_item", OdbcType.Int);
                cmd.Parameters.Add("l_server", OdbcType.Int);
                cmd.Parameters.Add("l_tries", OdbcType.Int);
                cmd.Parameters.Add("l_chanid", OdbcType.Int);
                cmd.Parameters.Add("l_schedtime", OdbcType.Numeric);
                cmd.Parameters.Add("sz_referenceid", OdbcType.VarChar, 20);
                cmd.Parameters.Add("sz_number", OdbcType.VarChar, 22);
                cmd.Parameters.Add("l_adrpk", OdbcType.Numeric);
                cmd.Parameters.Add("l_concatproc", OdbcType.TinyInt);

                foreach (RecipientData recipientData in recipientDataList)
                {
                    foreach (Endpoint endPoint in recipientData.Endpoints)
                    {
                        if (endPoint is Phone && ((Phone)endPoint).CanReceiveSms && endPoint.Address.Length > 0)
                        {
                            cmd.Parameters["l_refno"].Value = Refno;
                            cmd.Parameters["l_item"].Value = ++itemNumber;
                            cmd.Parameters["l_server"].Value = Account.PrimarySmsServer;
                            cmd.Parameters["l_tries"].Value = 0;
                            cmd.Parameters["l_chanid"].Value = 0;
                            cmd.Parameters["l_schedtime"].Value = AlertConfig.StartImmediately ? 0 : Int64.Parse(AlertConfig.Scheduled.ToString("yyyyMMddHHmmss"));
                            cmd.Parameters["sz_referenceid"].Value = DBNull.Value;
                            cmd.Parameters["sz_number"].Value = endPoint.Address;
                            cmd.Parameters["l_adrpk"].Value = -1;
                            cmd.Parameters["l_concatproc"].Value = DBNull.Value;

                            cmd.ExecuteNonQuery();
                            recipientData.AlertLink.Add(RecipientData.newRefnoItem(Refno, itemNumber));
                        }
                    }
                }

            }
            return itemNumber;
        }


        private void UpdateSmsQref(long Refno, int Items)
        {
            String Sql = "UPDATE SMSQREF SET l_items=?, l_status=? WHERE l_refno=?";

            using (OdbcCommand cmd = Database.CreateCommand(Sql))
            {
                cmd.Parameters.Add("l_items", OdbcType.Int).Value = Items == 0 ? 0 : Items;
                cmd.Parameters.Add("l_status", OdbcType.TinyInt).Value = Items == 0 ? 7 : 4;
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = Refno;

                cmd.ExecuteNonQuery();
            }
        }


        private void InsertSmsQref(long Refno, AccountDetails Account, AlertConfiguration alertConfig, SmsConfiguration smsConfig)
        {
            int group = 3;

            String Sql = "sp_sms_ins_smsqref_bcp_v2 ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
            using (OdbcCommand cmd = Database.CreateCommand(Sql))
            {
                cmd.Parameters.Add("l_projectpk", OdbcType.Numeric).Value = 0;
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = Refno;
                cmd.Parameters.Add("l_comppk", OdbcType.Int).Value = Account.Comppk;
                cmd.Parameters.Add("l_deptpk", OdbcType.Int).Value = Account.Deptpk;
                cmd.Parameters.Add("l_otoa", OdbcType.TinyInt).Value = 1;
                cmd.Parameters.Add("l_msgclass", OdbcType.TinyInt).Value = 1;
                cmd.Parameters.Add("l_pri", OdbcType.TinyInt).Value = Account.DeptPri;
                cmd.Parameters.Add("l_localsched", OdbcType.TinyInt).Value = 1;
                cmd.Parameters.Add("l_validitytime", OdbcType.Int).Value = 0;
                cmd.Parameters.Add("l_schedtime", OdbcType.Numeric).Value = alertConfig.StartImmediately ? Int64.Parse(DateTime.Now.ToString("yyyyMMddHHmmss")) : Int64.Parse(alertConfig.Scheduled.ToString("yyyyMMddHHmmss"));
                cmd.Parameters.Add("l_priserver", OdbcType.Int).Value = Account.PrimarySmsServer;
                cmd.Parameters.Add("l_altservers", OdbcType.Int).Value = Account.SecondarySmsServer;
                cmd.Parameters.Add("sz_tarifclass", OdbcType.VarChar, 20).Value = "";
                cmd.Parameters.Add("sz_oadc", OdbcType.VarChar, 22).Value = smsConfig.OriginAddress;
                cmd.Parameters.Add("sz_descriptor", OdbcType.VarChar, 100).Value = alertConfig.AlertName;
                cmd.Parameters.Add("f_simulation", OdbcType.Int).Value = (alertConfig.SimulationMode ? 1 : 0);
                cmd.Parameters.Add("l_parentrefno", OdbcType.Int).Value = 0;
                cmd.Parameters.Add("l_expecteditems", OdbcType.Int).Value = 0;
                cmd.Parameters.Add("sz_text", OdbcType.VarChar, 765).Value = smsConfig.BaseMessageContent;
                cmd.Parameters.Add("l_fromapplication", OdbcType.Int).Value = 13;;
                cmd.Parameters.Add("l_group", OdbcType.Int).Value = group;
                cmd.Parameters.Add("sz_sepused", OdbcType.VarChar, 10).Value = "|";
                cmd.Parameters.Add("l_lastantsep", OdbcType.Int).Value = 0;
                cmd.Parameters.Add("l_addresspos", OdbcType.Int).Value = 0;
                cmd.Parameters.Add("l_createdate", OdbcType.Int).Value = Int32.Parse(UCommon.UGetDateNow());
                cmd.Parameters.Add("l_createtime", OdbcType.Int).Value = Int32.Parse(UCommon.UGetTimeNow());
                cmd.Parameters.Add("l_userpk", OdbcType.Numeric).Value = -1; 
                cmd.Parameters.Add("l_nofax", OdbcType.Int).Value = 1;
                cmd.Parameters.Add("l_removedup", OdbcType.Int).Value = 1;
                cmd.Parameters.Add("sz_stdcc", OdbcType.VarChar, 6).Value = Account.StdCc;
                cmd.Parameters.Add("l_addresstypes", OdbcType.Int).Value = (long)(AdrTypes.SMS_PRIVATE | AdrTypes.SMS_COMPANY);
                cmd.Parameters.Add("f_dynacall", OdbcType.Int).Value = alertConfig.SimulationMode ? 2 : 1;

                cmd.ExecuteNonQuery();
            }
        }


        private void InsertMdvSendinginfoVoice(long Refno, 
                                        AccountDetails Account, 
                                        ChannelConfiguration ChannelConfig, 
                                        AlertConfiguration AlertConfig)
        {
            int noFax = 1;
            int group = 3;
            String Sql = "INSERT INTO MDVSENDINGINFO(sz_fields, sz_sepused, l_addresspos, l_lastantsep, l_refno, l_createdate, l_createtime, " +
                      "l_scheddate, l_schedtime, sz_sendingname, l_sendingstatus, l_companypk, l_deptpk, l_nofax, l_group, " +
                      "l_removedup, l_type, f_dynacall, l_addresstypes, l_userpk, l_maxchannels) " +
                      "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            using (OdbcCommand cmd = Database.CreateCommand(Sql))
            {
                cmd.Parameters.Add("sz_fields", OdbcType.VarChar, 500).Value = "";
                cmd.Parameters.Add("sz_sepused", OdbcType.VarChar, 10).Value = "|";
                cmd.Parameters.Add("l_addresspos", OdbcType.Int).Value = 0;
                cmd.Parameters.Add("l_lastantsep", OdbcType.Int).Value = 0;
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = Refno;
                cmd.Parameters.Add("l_createdate", OdbcType.Int).Value = Int32.Parse(UCommon.UGetDateNow());
                cmd.Parameters.Add("l_createtime", OdbcType.SmallInt).Value = Int32.Parse(UCommon.UGetTimeNow());
                cmd.Parameters.Add("l_scheddate", OdbcType.Int).Value = AlertConfig.StartImmediately ? Int64.Parse(DateTime.Now.ToString("yyyyMMdd")) : Int32.Parse(AlertConfig.Scheduled.ToString("yyyyMMdd"));
                cmd.Parameters.Add("l_schedtime", OdbcType.Int).Value = AlertConfig.StartImmediately ? Int64.Parse(DateTime.Now.ToString("HHmm")) : Int32.Parse(AlertConfig.Scheduled.ToString("HHmm"));
                cmd.Parameters.Add("sz_sendingname", OdbcType.VarChar, 255).Value = AlertConfig.AlertName;
                cmd.Parameters.Add("l_sendingstatus", OdbcType.Int).Value = 1;
                cmd.Parameters.Add("l_companypk", OdbcType.Int).Value = Account.Comppk;
                cmd.Parameters.Add("l_deptpk", OdbcType.Int).Value = Account.Deptpk;
                cmd.Parameters.Add("l_nofax", OdbcType.Int).Value = noFax;
                cmd.Parameters.Add("l_group", OdbcType.Int).Value = group;
                cmd.Parameters.Add("l_removedup", OdbcType.Int).Value = 1;
                cmd.Parameters.Add("l_type", OdbcType.Int).Value = 1; //voice
                cmd.Parameters.Add("f_dynacall", OdbcType.TinyInt).Value = AlertConfig.SimulationMode ? 2 : 1;
                cmd.Parameters.Add("l_addresstypes", OdbcType.Int).Value = (long)(AdrTypes.MOBILE_PRIVATE_AND_FIXED | AdrTypes.MOBILE_COMPANY_AND_FIXED);
                cmd.Parameters.Add("l_userpk", OdbcType.Numeric).Value = -1;
                cmd.Parameters.Add("l_maxchannels", OdbcType.Int).Value = Account.MaxVoiceChannels;

                cmd.ExecuteNonQuery();

            }
        }




        #endregion
    }
}
