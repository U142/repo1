using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Collections;
using com.ums.UmsCommon;
using Apache.NMS;
using Apache.NMS.Util;
using com.ums.pas.integration;
using com.ums.UmsDbLib;
using com.ums.PAS.Project;
using System.Xml.Serialization;
using Apache.NMS.ActiveMQ;
using com.ums.UmsCommon.Audio;
using System.Data.Odbc;
using System.IO;

namespace com.ums.ws.integration
{
    /// <summary>
    /// external PARM Alert and Event execution
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/integration/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Integration : System.Web.Services.WebService
    {
        [XmlInclude(typeof(PropertyAddress))]
        [XmlInclude(typeof(StoredAddress))]
        [XmlInclude(typeof(StreetAddress))]
        [XmlInclude(typeof(OwnerAddress))]
        [XmlInclude(typeof(AlertObject))]
        //[XmlInclude(typeof(Recipient))]
        [XmlInclude(typeof(Phone))]
        [XmlInclude(typeof(AlertConfiguration))]
        [XmlInclude(typeof(ChannelConfiguration))]
        [XmlInclude(typeof(SmsConfiguration))]
        [XmlInclude(typeof(VoiceConfiguration))]

        [XmlInclude(typeof(AlertResponse))]
        [XmlInclude(typeof(DefaultResponse))]




        /// <summary>
        /// Convert text to speech using default department tts-language
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="Text"></param>
        /// <returns>the wav file</returns>
        [WebMethod(Description = @"<b>Convert text to speech using default department tts-language</b><br>Throws exception if it fails.")]
        public byte[] GetTextToSpeechWav(Account Account, String Text)
        {
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;
            
            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);
            int defaultLanguage = umsDb.GetDefaultTtsLanguage(logonInfo.l_deptpk);
            if (defaultLanguage <= 0)
            {
                throw new Exception(String.Format("No default TTS language set on department {0}", logonInfo.l_deptpk));
            }
            return GetTextToSpeechWavInLanguage(Account, Text, defaultLanguage);
        }

        [WebMethod(Description = @"<b>Convert text to speech using specified tts-language<br>Available Languages/Dialects may be obtained using GetTextToSpeechLanguages.</b><br>Throws exception if it fails.")]
        public byte[] GetTextToSpeechWavInLanguage(Account Account, String Text, int Language)
        {
            UCONVERT_TTS_REQUEST convertReq = new UCONVERT_TTS_REQUEST();
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            convertReq.sz_text = Text;
            convertReq.n_langpk = Language;
            String tempPath = System.Configuration.ConfigurationManager.AppSettings["sz_path_audiofiles"];
            String ttsPath = System.Configuration.ConfigurationManager.AppSettings["sz_path_tts_server"];
            UCONVERT_TTS_RESPONSE response = new UAudio().ConvertTTS(tempPath, ttsPath, convertReq);
            if (response.n_responsecode == 0)
            {
                return response.wav;
            }
            throw new Exception(response.sz_responsetext);
        }

        [WebMethod(Description = @"<b>Get available languages/dialects for use with text to speech</b>")]
        public List<TtsLanguage> GetTextToSpeechLanguages(Account Account)
        {
            List<TtsLanguage> listToReturn = new List<TtsLanguage>();
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);
            List<KeyValuePair<int, String>> list = umsDb.GetAvailableTtsLanguages(logonInfo.l_deptpk);
            foreach(KeyValuePair<int, String> pair in list)
            {
                listToReturn.Add(new TtsLanguage()
                {
                    LangId = pair.Key,
                    Name = pair.Value
                });
            }
            return listToReturn;
        }

        /// <summary>
        /// Save a template
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="MessageTemplate"></param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Save a template</b>")]
        public MessageTemplateResponse SaveTemplate(Account Account, MessageTemplate MessageTemplate)
        {
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            string TemplateFolder = Path.Combine(UCommon.UPATHS.sz_path_bbmessages, logonInfo.l_deptpk.ToString());
            if (!Directory.Exists(TemplateFolder))
                Directory.CreateDirectory(TemplateFolder);

            if (MessageTemplate.TemplateId != null && MessageTemplate.TemplateId.Id > 0)
            {
                using (OdbcCommand cmd = umsDb.CreateCommand("SELECT l_messagepk FROM BBMESSAGES WHERE l_deptpk=? AND l_messagepk=?"))
                {
                    cmd.Parameters.Add("deptpk", OdbcType.Int).Value = logonInfo.l_deptpk;
                    cmd.Parameters.Add("msgpk", OdbcType.BigInt).Value = MessageTemplate.TemplateId.Id;

                    using (OdbcDataReader rs = cmd.ExecuteReader())
                    {
                        if (!rs.Read())
                            return new MessageTemplateResponse() { Code = -1, Message = "Message ID not found for this department", TemplateId = MessageTemplate.TemplateId };
                    }
                }

                // update
                using (OdbcCommand cmd = umsDb.CreateCommand("UPDATE BBMESSAGES SET sz_name=? WHERE l_deptpk=? AND l_messagepk=?"))
                {
                    cmd.Parameters.Clear();
                    cmd.Parameters.Add("name", OdbcType.VarChar, 50).Value = MessageTemplate.Title;
                    cmd.Parameters.Add("deptpk", OdbcType.Int).Value = logonInfo.l_deptpk;
                    cmd.Parameters.Add("msgpk", OdbcType.BigInt).Value = MessageTemplate.TemplateId.Id;

                    String templateContent = String.Format("{0}\\{1}\\{2}.txt", UCommon.UPATHS.sz_path_bbmessages, logonInfo.l_deptpk, MessageTemplate.TemplateId.Id);

                    if (cmd.ExecuteNonQuery() > 0)
                    {
                        if (File.Exists(templateContent))
                            File.Delete(templateContent);

                        File.WriteAllText(templateContent, MessageTemplate.MessageText);
                    }
                    else
                        return new MessageTemplateResponse() { Code = -2, Message = "Failed to update message template", TemplateId = MessageTemplate.TemplateId };
                }
            }
            else
            {
                // new
                using (OdbcCommand cmd = umsDb.CreateCommand("sp_get_bbmessagepk"))
                {
                    var tmp = cmd.ExecuteScalar(); 
                    long templateId;
                    if (!long.TryParse(tmp.ToString(), out templateId))
                        return new MessageTemplateResponse() { Code = -4, Message = "Failed to get new message id", TemplateId = null };

                    MessageTemplate.TemplateId = new MessageTemplateId() { Id = templateId };

                    cmd.CommandText = "INSERT INTO BBMESSAGES(l_deptpk, l_type, sz_name, sz_description, l_messagepk, l_langpk, sz_filename) VALUES(?, 14, ?, '', ? ,6 , '')";
                    cmd.Parameters.Add("deptpk", OdbcType.Int).Value = logonInfo.l_deptpk;
                    cmd.Parameters.Add("name", OdbcType.VarChar, 50).Value = MessageTemplate.Title;
                    cmd.Parameters.Add("msgpk", OdbcType.BigInt).Value = MessageTemplate.TemplateId.Id;

                    if (cmd.ExecuteNonQuery() > 0)
                    {
                        String templateContent = String.Format("{0}\\{1}\\{2}.txt", UCommon.UPATHS.sz_path_bbmessages, logonInfo.l_deptpk, MessageTemplate.TemplateId.Id);

                        if (File.Exists(templateContent))
                            File.Delete(templateContent);

                        File.WriteAllText(templateContent, MessageTemplate.MessageText);
                    }
                    else
                        return new MessageTemplateResponse() { Code = -3, Message = "Failed to save message", TemplateId = MessageTemplate.TemplateId };
                }
            }

            return new MessageTemplateResponse() { Code = 0, Message = "", TemplateId = MessageTemplate.TemplateId };
        }

        /// <summary>
        /// Delete a Message template
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="TemplateId"></param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Delete a Message template</b>")]
        public MessageTemplateResponse DeleteTemplate(Account Account, MessageTemplateId TemplateId)
        {
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            using (OdbcCommand cmd = umsDb.CreateCommand("SELECT l_messagepk FROM BBMESSAGES WHERE l_deptpk=? AND l_messagepk=?"))
            {
                cmd.Parameters.Add("deptpk", OdbcType.Int).Value = logonInfo.l_deptpk;
                cmd.Parameters.Add("msgpk", OdbcType.BigInt).Value = TemplateId.Id;

                using (OdbcDataReader rs = cmd.ExecuteReader())
                {
                    if (!rs.Read())
                        return new MessageTemplateResponse() { Code = -1, Message = "Message ID not found for this department", TemplateId = TemplateId };
                }

                cmd.CommandText = "DELETE FROM BBMESSAGES WHERE l_deptpk=? AND l_messagepk=?";
                if (cmd.ExecuteNonQuery() > 0)
                {
                    String templateContent = String.Format("{0}\\{1}\\{2}.txt", UCommon.UPATHS.sz_path_bbmessages, logonInfo.l_deptpk, TemplateId.Id);
                    if (File.Exists(templateContent))
                        File.Delete(templateContent);
                }
            }

            return new MessageTemplateResponse() { Code = 0, Message = "", TemplateId = TemplateId };
        }

        /// <summary>
        /// Get a list of all message templates available for Account
        /// </summary>
        /// <param name="Account">The account</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get a list of all message templates available for Account</b>")]
        public List<MessageTemplateListItem> GetTemplates(Account Account, short messageType)
        {
            List<MessageTemplateListItem> templates = new List<MessageTemplateListItem>();

            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            string sql = "SELECT l_messagepk, isnull(sz_name, '') sz_name FROM BBMESSAGES WHERE l_deptpk=? AND l_type=?";

            using (OdbcCommand cmd = umsDb.CreateCommand(sql))
            {
                cmd.Parameters.Add("deptpk", OdbcType.Int).Value = logonInfo.l_deptpk;
                cmd.Parameters.Add("messagepk", OdbcType.SmallInt).Value = messageType;
                using (OdbcDataReader rs = cmd.ExecuteReader())
                {
                    while (rs.Read())
                    {
                        MessageTemplateListItem template = new MessageTemplateListItem();
                        template.TemplateId = new MessageTemplateId() { Id = rs.GetInt64(rs.GetOrdinal("l_messagepk")) };
                        template.Title = rs.GetString(rs.GetOrdinal("sz_name"));

                        templates.Add(template);
                    }
                }
            }

            return templates;
        }

        /// <summary>
        /// Get a template from id.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="TemplateId">Template ID</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get a template from id.</b>")]
        public MessageTemplate GetTemplate(Account Account, MessageTemplateId TemplateId)
        {
            MessageTemplate template = new MessageTemplate();

            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            string sql = "SELECT l_messagepk, isnull(sz_name, '') sz_name FROM BBMESSAGES where l_deptpk=? AND l_messagepk=?";

            using (OdbcCommand cmd = umsDb.CreateCommand(sql))
            {
                cmd.Parameters.Add("deptpk", OdbcType.Int).Value = logonInfo.l_deptpk;
                cmd.Parameters.Add("msgpk", OdbcType.BigInt).Value = TemplateId.Id;

                using (OdbcDataReader rs = cmd.ExecuteReader())
                {
                    if (rs.Read())
                    {
                        template.TemplateId = new MessageTemplateId() { Id = rs.GetInt64(rs.GetOrdinal("l_messagepk")) };
                        template.Title = rs.GetString(rs.GetOrdinal("sz_name"));
                        
                        String templateContent = String.Format("{0}\\{1}\\{2}.txt", UCommon.UPATHS.sz_path_bbmessages, logonInfo.l_deptpk, TemplateId.Id);
                        if(File.Exists(templateContent))
                            template.MessageText = File.ReadAllText(templateContent);
                    }
                    else
                        throw new Exception("Message ID not found for this department id");
                }
            }

            return template;
        }


        /// <summary>
        /// Sends a test alert to a specified phone number.
        /// Uses the StartAlert function to send an alert based on the input.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="Message">The message content</param>
        /// <param name="PhoneNumber">The phone number for testing</param>
        /// <param name="SendTo">Send via specified channels</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Sends a test alert to a specified phone number.</b>")]
        public AlertResponse StartTestAlert(Account Account, String Message, Endpoint Endpoint, SendChannel SendTo)
        {
            // TODO: Find a mechanism to avoid linking test-messages to the project, this for avoiding visibility.

            //for now, only accept phones
            if(!(Endpoint is Phone))
            {
                return AlertResponseFactory.Failed(-30, "Test alert endpoint must be a phone");
            }
            switch (SendTo)
            {
                case SendChannel.EMAIL:
                case SendChannel.LBA:
                case SendChannel.TAS:
                    return AlertResponseFactory.Failed(-404, "Selected channel not implemented {0}", SendTo.ToString());
            }


            Phone phone = (Phone) Endpoint;

            //if testing for sms, check that the number is capable of receiving sms
            if (SendTo.Equals(SendChannel.SMS) && !phone.CanReceiveSms)
            {
                return AlertResponseFactory.Failed(-31, "Test alert phone must be capable of receiving SMS");
            }

            //force can receive to false, as we're testing voice.
            if (SendTo.Equals(SendChannel.VOICE))
            {
                phone.CanReceiveSms = false;
            }

            AlertConfiguration alertConfiguration = new AlertConfiguration()
            {
                AlertName = String.Format("Test message to {0} via channel {1}", Endpoint.Address, SendTo.ToString()),
                Scheduled = new DateTime(),
                SendToAllChannels = false,
                SimulationMode = false,
                StartImmediately = true,
            };
            List<ChannelConfiguration> channelConfigurations = new List<ChannelConfiguration>();
            switch (SendTo)
            {
                case SendChannel.SMS:
                    channelConfigurations.Add(ChannelConfigurationFactory.newSmsConfiguration(Account.CompanyId.Substring(0, 1).ToUpper() + Account.CompanyId.Substring(1).ToLower(), Message, false));
                    break;
                case SendChannel.VOICE:
                    // Get default sender number
                    ULOGONINFO logonInfo = new ULOGONINFO();
                    logonInfo.sz_compid = Account.CompanyId;
                    logonInfo.sz_deptid = Account.DepartmentId;
                    logonInfo.sz_password = Account.Password;

                    UmsDb umsDb = new UmsDb();
                    umsDb.CheckDepartmentLogonLiteral(ref logonInfo);
                    string defaultNumber = umsDb.GetDefaultVoiceNumber(logonInfo.l_deptpk);

                    channelConfigurations.Add(ChannelConfigurationFactory.newVoiceConfiguration(1, 
                                                                                                1, 
                                                                                                -1, 
                                                                                                -1, 
                                                                                                7, 
                                                                                                true, 
                                                                                                -1,
                                                                                                defaultNumber != null ? false : true,
                                                                                                defaultNumber != null ? defaultNumber : "",
                                                                                                Message));
                    break;
            }
            List<AlertTarget> alertTargets = new List<AlertTarget>()
            {
                new AlertObject("Send Test", "", phone.Address, phone.CanReceiveSms),
            };
            return StartAlert(Account, alertConfiguration, channelConfigurations, alertTargets, true);
        }

        [WebMethod]
        public AlertResponse StartAlert(Account Account, AlertConfiguration AlertConfiguration, List<ChannelConfiguration> ChannelConfigurations, List<AlertTarget> AlertTargets, 
                                        bool IsTestAlert)
        {
            String ActiveMqUri = System.Configuration.ConfigurationManager.AppSettings["ActiveMqUri"];
            String ActiveMqDestination = System.Configuration.ConfigurationManager.AppSettings["ActiveMqDestination"];

            if (ActiveMqDestination == null || ActiveMqDestination.Length == 0 
                || ActiveMqUri == null || ActiveMqUri.Length == 0)
            {
                ULog.error("Active MQ setup is faulty");
                return AlertResponseFactory.Failed(-9, @"Active MQ destination not specified");
            }

            AlertResponse responseObject = new AlertResponse();
            Uri connectionUri = new Uri(ActiveMqUri);
            IConnectionFactory mqFactory = null;
            try
            {
                mqFactory = new ConnectionFactory(connectionUri);
            }
            catch (Exception e)
            {
                return AlertResponseFactory.Failed(-3, e.Message);
            }
            try
            {
                using (IConnection mqConnection = mqFactory.CreateConnection())
                using (ISession mqSession = mqConnection.CreateSession())
                {
                    IDestination destination = SessionUtil.GetDestination(mqSession, ActiveMqDestination);
                    using (IMessageProducer mqProducer = mqSession.CreateProducer())
                    {
                        //IMessage message = mqSession.CreateObjectMessage("tester");
                        AlertMqPayload payload = new AlertMqPayload();

                        ULOGONINFO logonInfo = new ULOGONINFO();
                        logonInfo.sz_compid = Account.CompanyId;
                        logonInfo.sz_deptid = Account.DepartmentId;
                        logonInfo.sz_password = Account.Password;

                        
                        UmsDb umsDb = new UmsDb();

                        umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

                        //fill internal account info
                        AccountDetails accountDetails = new AccountDetails();
                        accountDetails.Comppk = logonInfo.l_comppk;
                        accountDetails.Deptpk = logonInfo.l_deptpk;
                        accountDetails.DeptPri = logonInfo.l_deptpri;
                        //accountDetails.Userpk = logonInfo.l_userpk;
                        accountDetails.PrimarySmsServer = logonInfo.l_priserver;
                        accountDetails.SecondarySmsServer = logonInfo.l_altservers;
                        accountDetails.StdCc = logonInfo.sz_stdcc;
                        accountDetails.MaxVoiceChannels = umsDb.GetMaxAlloc(accountDetails.Deptpk);
                        accountDetails.AvailableVoiceNumbers = new List<String>() { umsDb.GetDefaultVoiceNumber(accountDetails.Deptpk) }; // only get default, no need to get full list
                        accountDetails.DefaultTtsLang = umsDb.GetDefaultTtsLanguage(accountDetails.Deptpk);

                        if (accountDetails.DefaultTtsLang <= 0)
                        {
                            AlertResponseFactory.Failed(-6, "No Text to Speech engine set to default on department {0}", accountDetails.Deptpk);
                        }
                        
                        payload.AccountDetails = accountDetails;

                        foreach (VoiceConfiguration voiceConfig in ChannelConfigurations.OfType<VoiceConfiguration>())
                        {
                            if (voiceConfig.BaseMessageContent.Length <= 0)
                            {
                                return AlertResponseFactory.Failed(-40, "No message content specified for the voice alert");
                            }
                            if (voiceConfig.UseDefaultVoiceProfile)
                            {
                                int tmpProfile = umsDb.GetDefaultVoiceProfile(accountDetails.Deptpk);
                                if (tmpProfile < 0)
                                {
                                    return AlertResponseFactory.Failed(-4, "No default voice profile set up for this department ({0})", accountDetails.Deptpk);
                                }
                                voiceConfig.VoiceProfilePk = tmpProfile;
                            }
                            else
                            {
                                if (!umsDb.ValidateUseOfProfile(voiceConfig.VoiceProfilePk, accountDetails.Deptpk))
                                {
                                    return AlertResponseFactory.Failed(-4, "You are not allowed to use the voice profile specified");
                                }
                            }
                            //set stop and pause based on config profile
                            int pauseAtTime, pauseDurationMinutes, validDays;
                            if (! IsTestAlert && umsDb.GetPauseValuesOfProfile(voiceConfig.VoiceProfilePk, out pauseAtTime, out pauseDurationMinutes, out validDays))
                            {
                                voiceConfig.PauseAtTime = pauseAtTime;
                                voiceConfig.PauseDurationMinutes = pauseDurationMinutes;
                                voiceConfig.ValidDays = validDays;
                            }

                            int dynVoice = umsDb.getNumDynfilesInProfile(voiceConfig.VoiceProfilePk);
                            if (dynVoice != 1)
                            {
                                return AlertResponseFactory.Failed(-5, "There are {0} dynamic audio-files in voice profile {1}, to send there can only be one.", dynVoice, voiceConfig.VoiceProfilePk);
                            }
                            //check if we need to extract default originating number for voice
                            if (!voiceConfig.UseHiddenOriginAddress && (voiceConfig.OriginAddress == null || voiceConfig.OriginAddress.Length == 0))
                            {
                                voiceConfig.OriginAddress = umsDb.GetDefaultOriginatingNumber(accountDetails.Deptpk);
                            }
                        }
                        foreach (SmsConfiguration smsConfig in ChannelConfigurations.OfType<SmsConfiguration>())
                        {
                            if (smsConfig.BaseMessageContent.Length <= 0)
                            {
                                return AlertResponseFactory.Failed(-41, "No message content specified for the sms alert");
                            }
                            if (smsConfig.BaseMessageContent.Length > 760)
                            {
                                return AlertResponseFactory.Failed(-42, "Message content of the SMS message was too long, max is 760 characters");
                            }
                            if (smsConfig.OriginAddress.Length <= 0)
                            {
                                return AlertResponseFactory.Failed(-43, "No originating address specified for the sms alert");
                            }
                        }

                        //Create and retrieve a project
                        UPROJECT_REQUEST req = new UPROJECT_REQUEST();
                        //req.sz_name = String.Format("ActiveMq {0}", DateTime.Now.ToString("yyyyMMdd HHmmss"));
                        req.sz_name = AlertConfiguration.AlertName;
                        payload.AlertId = new AlertId(new UProject().uproject(ref logonInfo, ref req).n_projectpk);

                        payload.Account.CompanyId = Account.CompanyId;
                        payload.Account.DepartmentId = Account.DepartmentId;
                        payload.Account.Password = Account.Password;

                        payload.AlertTargets = AlertTargets;
                        payload.AlertConfiguration = AlertConfiguration;
                        payload.ChannelConfigurations = ChannelConfigurations;

                        payload.IsTestAlert = IsTestAlert;

                        IObjectMessage message = mqSession.CreateObjectMessage(payload);
                        mqProducer.Send(destination, message);
                        responseObject.AlertId = payload.AlertId;
                        responseObject.Message = "OK";
                        responseObject.Description = String.Format("Message sent to Queue={0}", ActiveMqDestination);
                        return responseObject;
                    }
                }
            }
            catch (NMSConnectionException e)
            {
                return AlertResponseFactory.Failed(-1, e.Message);
            }
            catch (Exception e)
            {
                return AlertResponseFactory.Failed(-2, e.Message);
            }

        }


        [WebMethod]
        public DefaultResponse StopAlert(Account Account, AlertId AlertId)
        {
            DefaultResponse response = new DefaultResponse();

            response.Code = 0;
            response.Message = "";

            UmsDb umsDb = new UmsDb();
            UmsDb cancelDb = new UmsDb();
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);
            
            using (OdbcCommand cmdStopProject = cancelDb.CreateCommand("UPDATE BBPROJECT SET l_finished=8 WHERE l_projectpk=?"))
            {
                cmdStopProject.Parameters.Add("projectpk", OdbcType.BigInt).Value = AlertId.Id;
                if (cmdStopProject.ExecuteNonQuery() != 1)
                {
                    response.Code = -1;
                    response.Message = String.Format("Failed to stop alertid={0}", AlertId.Id);

                    return response;
                }
            }

            // Get all refnos corresponding to the alertid (projectpk)
            String Sql = String.Format("SELECT PR.l_refno, MI.l_type FROM BBPROJECT_X_REFNO PR INNER JOIN MDVSENDINGINFO MI ON PR.l_refno=MI.l_refno AND MI.l_deptpk=? WHERE PR.l_projectpk=?");
            using (OdbcCommand cmd = umsDb.CreateCommand(Sql))
            {
                cmd.Parameters.Add("dept", OdbcType.Int).Value = logonInfo.l_deptpk;
                cmd.Parameters.Add("projectpk", OdbcType.BigInt).Value = AlertId.Id;

                using (OdbcDataReader rs = cmd.ExecuteReader())
                {
                    if (rs.HasRows)
                    {
                        using (OdbcCommand cancelCmd = cancelDb.CreateCommand(""))
                        {
                            cancelCmd.Parameters.Add("refno", OdbcType.Int);
                            while (rs.Read())
                            {
                                int l_type = rs.GetInt32(rs.GetOrdinal("l_type"));

                                cancelCmd.Parameters["refno"].Value = rs.GetInt32(rs.GetOrdinal("l_refno"));

                                cancelCmd.CommandText = "INSERT INTO BBCANCEL(l_refno, l_item) VALUES(?, -1)";
                                if (cancelCmd.ExecuteNonQuery() != 1)
                                {
                                    // TODO: Set proper status code (-1 is probably in use)
                                    response.Code = -1;
                                    response.Message += String.Format("Failed to stop message with alertid={0} refno={1}", AlertId.Id, rs.GetInt32(0));
                                }

                                if (l_type == 1) // voice, set secheddate to now 0 to cancel immediately
                                {
                                    cancelCmd.CommandText = "UPDATE BBQREF SET l_startdate=0, l_starttime=0 WHERE l_refno=?";
                                    cancelCmd.ExecuteNonQuery();
                                }
                            }
                        }
                    }
                }
            }

            return response;
        }

        [WebMethod]
        public LogSummary testGetAlertLog(long Projectpk)
        {
            return GetAlertLog(new Account()
            {
                CompanyId = "UMS",
                DepartmentId = "DEVELOPMENT",
                Password = "ums123",
                /*CompanyId = "POWEL",
                DepartmentId = "DEV",
                Password = "dev123",*/
                /*CompanyId = "POWEL",
                DepartmentId = "TEST",
                Password = "raThU9Ha",*/
            },
            new AlertId(Projectpk));
        }

        [WebMethod]
        public List<AlertSummary> testGetAlerts(int StartAt, int PageSize)
        {
            Account account = new Account()
            {
                CompanyId = "UMS",
                DepartmentId = "DEVELOPMENT",
                Password = "ums123",
            };
            return GetAlerts(account, StartAt, PageSize);
        }


        /// <summary>
        /// Get object log for a previously sent alert.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="AlertId">The alert id</param>
        /// <param name="StatusCodeFilter">Status code for filtering</param>
        /// <param name="StartIndex">Start at (0 based index)</param>
        /// <param name="PageSize">Number of rows</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get object log for a previously sent alert.</b>")]
        public List<LogLineDetailed> GetAlertObjectLog(Account Account, AlertId AlertId, int StatusCodeFilter, int StartIndex, int PageSize)
        {
            List<LogLineDetailed> objectLog = new List<LogLineDetailed>();
            UmsDb umsDb = new UmsDb();
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            if(!umsDb.ValidateOwnerOfProject(AlertId.Id, logonInfo.l_deptpk))
                throw new Exception("Alert Log not found for the specified AlertId or wrong account used");

            string sql = "sp_getProjectStatus_pricall ?, ?";

            using (OdbcCommand cmd = umsDb.CreateCommand(sql))
            {
                // add parameters
                cmd.Parameters.Add("projectpkt", OdbcType.BigInt).Value = AlertId.Id;
                cmd.Parameters.Add("statuscodefilter", OdbcType.Int).Value = StatusCodeFilter;
                
                using (OdbcDataReader rs = cmd.ExecuteReader())
                {
                    bool isPricall = false;
                    long previousSource = 0;
                    LogLineDetailed line = null;
                    AlertObject alertObject = null;

                    while (rs.Read() && (PageSize == 0 || objectLog.Count <= PageSize + StartIndex) )
                    {
                        isPricall = !rs.IsDBNull(rs.GetOrdinal("pricall"));
                        long currentSource = (long)rs.GetDecimal(rs.GetOrdinal("l_alertsourcepk"));

                        if (line == null || previousSource != currentSource)
                        {
                            line = new LogLineDetailed();
                            line.LogLines = new List<LogLinePhone>();
                            alertObject = null; // reset alertoject
                            line.Name = rs.GetString(rs.GetOrdinal("name"));
                        }

                        DateTime timestamp;
                        DateTime.TryParseExact(rs.GetDecimal(rs.GetOrdinal("l_timestamp")).ToString(), "yyyyMMddHHmmss", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out timestamp);

                        // check pricall and use last pricall number if bbq number is PRICALL
                        string number = rs.GetString(rs.GetOrdinal("sz_number"));
                        if (isPricall && number.ToUpper() == "PRICALL")
                            number = rs.GetString(rs.GetOrdinal("pricall"));

                        LogLinePhone phoneLine = new LogLinePhone(number, rs.GetInt32(rs.GetOrdinal("l_type")), rs.GetInt32(rs.GetOrdinal("l_dst")), rs.GetInt32(rs.GetOrdinal("l_status")), rs.GetString(rs.GetOrdinal("sz_status")), timestamp, rs.GetInt32(rs.GetOrdinal("l_tries")), rs.GetInt32(rs.GetOrdinal("l_retries")));

                        // Alert Targets
                        switch (rs.GetByte(rs.GetOrdinal("alerttarget")))
                        {
                            case 1: // AlertObject
                                if (alertObject == null)
                                {
                                    alertObject = new AlertObject();
                                    alertObject.ExternalId = rs.GetString(rs.GetOrdinal("externalid"));

                                    alertObject.Name = line.Name;
                                }

                                Phone phone = new Phone();
                                phone.Address = isPricall ? rs.GetString(rs.GetOrdinal("pricall")) : phoneLine.Address; // use pricall no if it is pricall
                                phone.CanReceiveSms = isPricall ? false : phoneLine.CanReceiveSms; // pricall is voice and pr. def not capabale of receiving sms

                                alertObject.Endpoints.Add(phone);

                                line.AlertTarget = alertObject;
                                break;
                            case 2: // StreetAddress
                                StreetAddress streetAddress = new StreetAddress();
                                streetAddress.HouseNo = rs.GetInt32(rs.GetOrdinal("houseno"));
                                streetAddress.Letter = rs.GetString(rs.GetOrdinal("letter"));
                                streetAddress.MunicipalCode = rs.GetInt32(rs.GetOrdinal("municipalid")).ToString();
                                streetAddress.Oppgang = rs.GetString(rs.GetOrdinal("oppgang"));
                                streetAddress.StreetNo = rs.GetInt32(rs.GetOrdinal("streetid"));

                                line.AlertTarget = streetAddress;
                                break;
                            case 3: // PropertyAddress
                                PropertyAddress propertyAddress = new PropertyAddress();
                                propertyAddress.Bnr = rs.GetInt32(rs.GetOrdinal("bnr"));
                                propertyAddress.Fnr = rs.GetInt32(rs.GetOrdinal("fnr"));
                                propertyAddress.Gnr = rs.GetInt32(rs.GetOrdinal("gnr"));
                                propertyAddress.MunicipalCode = rs.GetInt32(rs.GetOrdinal("municipalid")).ToString();
                                propertyAddress.Unr = rs.GetInt32(rs.GetOrdinal("unr"));

                                line.AlertTarget = propertyAddress;
                                break;
                            case 4: // OwnerAddress
                                OwnerAddress ownerAddress = new OwnerAddress();

                                String[] ownerProperties = rs.GetString(rs.GetOrdinal("data")).Split('|');
                                if (ownerProperties.Count() == 6)
                                {
                                    ownerAddress.Adresselinje1 = ownerProperties[0];
                                    ownerAddress.Adresselinje2 = ownerProperties[1];
                                    ownerAddress.Adresselinje3 = ownerProperties[2];
                                    long eierId;
                                    if (long.TryParse(rs.GetString(rs.GetOrdinal("externalid")), out eierId))
                                        ownerAddress.EierId = eierId;
                                    ownerAddress.EierIdKode = (NorwayEierIdKode)Enum.Parse(typeof(NorwayEierIdKode), ownerProperties[3], true);
                                    ownerAddress.EierKategoriKode = (NorwayEierKategoriKode)Enum.Parse(typeof(NorwayEierKategoriKode), ownerProperties[4], true);
                                    ownerAddress.EierStatusKode = (NorwayEierStatusKode)Enum.Parse(typeof(NorwayEierStatusKode), ownerProperties[5], true);
                                }
                                else
                                {
                                    ownerAddress.EierIdKode = NorwayEierIdKode.ANNEN_PERSON;
                                    ownerAddress.EierKategoriKode = NorwayEierKategoriKode.IKKE_DEFINERT;
                                    ownerAddress.EierStatusKode = NorwayEierStatusKode.IKKE_DEFINERT;
                                }
                                ownerAddress.DateOfBirth = DateOfBirthToString(rs.GetInt32(rs.GetOrdinal("birthdate")));
                                ownerAddress.Navn = rs.GetString(rs.GetOrdinal("name"));
                                ownerAddress.Postnr = rs.GetInt32(rs.GetOrdinal("postno"));

                                line.AlertTarget = ownerAddress;
                                break;
                        }

                        //if(!isPricall || line.LogLines.Count==0) // add line if logline is not pricall or no lines have been added yet 
                        if(!line.LogLines.Contains(phoneLine))
                            line.LogLines.Add(phoneLine);

                        // Get attributes, listed as key=value and seperated with | ex: "Morten=Tester|Gate=Steinstemveien 20|"
                        List<DataItem> targetAttributes = new List<DataItem>();
                        String[] attributes = rs.GetString(rs.GetOrdinal("attributes")).Split(new String[] { "|" }, StringSplitOptions.RemoveEmptyEntries);
                        foreach (string attribute in attributes)
                        {
                            String[] attributePair = attribute.Split('=');
                            if(attributePair.Count() == 2)
                                targetAttributes.Add(new DataItem(attributePair[0], attributePair[1]));
                        }
                        if (targetAttributes.Count > 0)
                            line.AlertTarget.Attributes = targetAttributes;

                        if(previousSource != currentSource)
                            objectLog.Add(line);

                        previousSource = currentSource;
                    }
                }
            }

            if (StartIndex > 0) // remove beginning of list if startindex is not 0
                if (StartIndex < objectLog.Count)
                    objectLog.RemoveRange(0, StartIndex);
                else
                    objectLog.RemoveRange(0, objectLog.Count); // silly, but prevents exception
            if (PageSize > 0 && objectLog.Count > PageSize) // remove last object (has to insert one extra to make sure all LogLines are added
                objectLog.RemoveAt(objectLog.Count-1);

            return objectLog;
        }

        /// <summary>
        /// Search for a specific recipient in any alert, either by person name, org name or phone number
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="SearchText">Search by name or number</param>
        /// <param name="StartIndex">Start at index</param>
        /// <param name="PageSize">Number of rows</param>
        [WebMethod(Description = @"<b>Search for a specific recipient in any alert, either by person name, org name or phone number</b>")]
        public List<LogObject> GetObjectLog(Account Account, String SearchText, int StartIndex, int PageSize)
        {
            List<LogObject> objectLog = new List<LogObject>();

            UmsDb umsDb = new UmsDb();
            UmsDb alertMsgDb = new UmsDb();
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            string sql = "sp_searchProjectStatus ?, ?";

            using (OdbcCommand cmd = umsDb.CreateCommand(sql))
            {
                // add parameters
                cmd.Parameters.Add("deptpk", OdbcType.Int).Value = logonInfo.l_deptpk;
                cmd.Parameters.Add("searchtext", OdbcType.VarChar, 100).Value = SearchText;

                using (OdbcDataReader rs = cmd.ExecuteReader())
                {
                    long previousSource = 0;
                    LogObject line = null;
                    AlertObject alertObject = null;

                    while (rs.Read() && (PageSize == 0 || objectLog.Count <= PageSize + StartIndex))
                    {
                        long currentSource = rs.GetInt64(rs.GetOrdinal("l_alertsourcepk"));
                        long currentProject = (long)rs.GetDecimal(rs.GetOrdinal("l_projectpk"));

                        if (line == null || previousSource != currentSource)
                        {
                            DateTime createtimestamp;
                            DateTime.TryParseExact(rs.GetDecimal(rs.GetOrdinal("l_createtimestamp")).ToString(), "yyyyMMddHHmmss", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out createtimestamp);

                            string ttsMessage, smsMessage;
                            alertMsgDb.GetAlertMessage(currentProject, out ttsMessage, out smsMessage);

                            line = new LogObject()
                            { 
                                AlertId = new AlertId(currentProject),
                                Name = rs.GetString(rs.GetOrdinal("name")),
                                DateTime = createtimestamp,
                                SmsMessage = smsMessage,
                                TtsMessage = ttsMessage,
                                AlertTitle = rs.GetString(rs.GetOrdinal("alertname"))
                            };

                            line.LogLines = new List<LogLinePhone>();
                            alertObject = null; // reset alertoject
                        }

                        DateTime timestamp;
                        DateTime.TryParseExact(rs.GetDecimal(rs.GetOrdinal("l_timestamp")).ToString(), "yyyyMMddHHmmss", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out timestamp);

                        LogLinePhone phoneLine = new LogLinePhone(rs.GetString(rs.GetOrdinal("sz_number")), rs.GetInt32(rs.GetOrdinal("l_type")), rs.GetInt32(rs.GetOrdinal("l_dst")), rs.GetInt32(rs.GetOrdinal("l_status")), rs.GetString(rs.GetOrdinal("sz_status")), timestamp, -1, -1);

                        // Alert Targets
                        switch (rs.GetByte(rs.GetOrdinal("alerttarget")))
                        {
                            case 1: // AlertObject
                                if (alertObject == null)
                                {
                                    alertObject = new AlertObject();
                                    alertObject.ExternalId = rs.GetString(rs.GetOrdinal("externalid"));

                                    alertObject.Name = line.Name;
                                }

                                Phone phone = new Phone();
                                phone.Address = phoneLine.Address;
                                phone.CanReceiveSms = phone.CanReceiveSms;

                                alertObject.Endpoints.Add(phone);

                                line.AlertTarget = alertObject;
                                break;
                            case 2: // StreetAddress
                                StreetAddress streetAddress = new StreetAddress();
                                streetAddress.HouseNo = rs.GetInt32(rs.GetOrdinal("houseno"));
                                streetAddress.Letter = rs.GetString(rs.GetOrdinal("letter"));
                                streetAddress.MunicipalCode = rs.GetInt32(rs.GetOrdinal("municipalid")).ToString();
                                streetAddress.Oppgang = rs.GetString(rs.GetOrdinal("oppgang"));
                                streetAddress.StreetNo = rs.GetInt32(rs.GetOrdinal("streetid"));

                                line.AlertTarget = streetAddress;
                                break;
                            case 3: // PropertyAddress
                                PropertyAddress propertyAddress = new PropertyAddress();
                                propertyAddress.Bnr = rs.GetInt32(rs.GetOrdinal("bnr"));
                                propertyAddress.Fnr = rs.GetInt32(rs.GetOrdinal("fnr"));
                                propertyAddress.Gnr = rs.GetInt32(rs.GetOrdinal("gnr"));
                                propertyAddress.MunicipalCode = rs.GetInt32(rs.GetOrdinal("municipalid")).ToString();
                                propertyAddress.Unr = rs.GetInt32(rs.GetOrdinal("unr"));

                                line.AlertTarget = propertyAddress;
                                break;
                            case 4: // OwnerAddress
                                OwnerAddress ownerAddress = new OwnerAddress();

                                String[] ownerProperties = rs.GetString(rs.GetOrdinal("data")).Split('|');
                                if (ownerProperties.Count() == 6)
                                {
                                    ownerAddress.Adresselinje1 = ownerProperties[0];
                                    ownerAddress.Adresselinje2 = ownerProperties[1];
                                    ownerAddress.Adresselinje3 = ownerProperties[2];
                                    int eierId;
                                    if (int.TryParse(rs.GetString(rs.GetOrdinal("externalid")), out eierId))
                                        ownerAddress.EierId = eierId;
                                    ownerAddress.EierIdKode = (NorwayEierIdKode)Enum.Parse(typeof(NorwayEierIdKode), ownerProperties[3], true);
                                    ownerAddress.EierKategoriKode = (NorwayEierKategoriKode)Enum.Parse(typeof(NorwayEierKategoriKode), ownerProperties[4], true);
                                    ownerAddress.EierStatusKode = (NorwayEierStatusKode)Enum.Parse(typeof(NorwayEierStatusKode), ownerProperties[5], true);
                                }
                                else
                                {
                                    ownerAddress.EierIdKode = NorwayEierIdKode.ANNEN_PERSON;
                                    ownerAddress.EierKategoriKode = NorwayEierKategoriKode.IKKE_DEFINERT;
                                    ownerAddress.EierStatusKode = NorwayEierStatusKode.IKKE_DEFINERT;
                                }
                                ownerAddress.DateOfBirth = DateOfBirthToString(rs.GetInt32(rs.GetOrdinal("birthdate")));
                                ownerAddress.Navn = rs.GetString(rs.GetOrdinal("name"));
                                ownerAddress.Postnr = rs.GetInt32(rs.GetOrdinal("postno"));

                                line.AlertTarget = ownerAddress;
                                break;
                        }

                        line.LogLines.Add(phoneLine);

                        // Get attributes, listed as key=value and seperated with | ex: "Morten=Tester|Gate=Steinstemveien 20|"
                        List<DataItem> targetAttributes = new List<DataItem>();
                        String[] attributes = rs.GetString(rs.GetOrdinal("attributes")).Split(new String[] { "|" }, StringSplitOptions.RemoveEmptyEntries);
                        foreach (string attribute in attributes)
                        {
                            String[] attributePair = attribute.Split('=');
                            if (attributePair.Count() == 2)
                                targetAttributes.Add(new DataItem(attributePair[0], attributePair[1]));
                        }
                        if (targetAttributes.Count > 0)
                            line.AlertTarget.Attributes = targetAttributes;

                        if (previousSource != currentSource)
                            objectLog.Add(line);

                        previousSource = currentSource;
                    }
                }

                if (StartIndex > 0) // remove beginning of list if startindex is not 0
                    if (StartIndex < objectLog.Count)
                        objectLog.RemoveRange(0, StartIndex);
                    else
                        objectLog.RemoveRange(0, objectLog.Count); // silly, but prevents exception
                if (PageSize > 0 && objectLog.Count > PageSize) // remove last object (has to insert one extra to make sure all LogLines are added
                    objectLog.RemoveAt(objectLog.Count - 1);

                /*{
                    while (rs.Read())
                    {
                        
                        LogObject line = new LogObject();

                        DateTime timestamp;
                        DateTime.TryParseExact(rs.GetDecimal(rs.GetOrdinal("l_timestamp")).ToString(), "yyyyMMddHHmmss", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out timestamp);

                        line.AlertId = new AlertId() { Id = (long)rs.GetDecimal(rs.GetOrdinal("l_projectpk")) };
                        line.AlertMessage = "//TODO: get message content";
                        line.AlertTarget = new AlertObject();
                        line.AlertTitle = rs.GetString(rs.GetOrdinal("alertname"));
                        line.DateTime = timestamp;
                        line.ExternalId = rs.GetString(rs.GetOrdinal("externalid"));
                        line.Name = rs.GetString(rs.GetOrdinal("name"));
                        line.PhoneNumber = rs.GetString(rs.GetOrdinal("sz_number"));

                        int dst = rs.GetInt32(rs.GetOrdinal("l_dst"));
                        int Type = rs.GetInt32(rs.GetOrdinal("l_type"));

                        bool CanReceiveSms = false;

                        switch (Type)
                        {
                            case 1: // voice
                                line.StatusCode = dst;
                                break;
                            case 2: // sms
                                CanReceiveSms = true;

                                switch (dst)
                                {
                                    case 0: // delivered
                                        line.StatusCode = 2;
                                        break;
                                    case 2: // error
                                        line.StatusCode = 4;
                                        break;
                                    case -1:// undelivered
                                    case 1: // only used by some providers, but should still show as undelivered
                                        line.StatusCode = 3;
                                        break;
                                }
                                break;
                        }

                        switch (line.StatusCode)
                        {
                            case 1:
                                line.Status = "Confirmed";
                                break;
                            case 2:
                                line.Status = "Delivered";
                                break;
                            case 3:
                                line.Status = "Undelivered";
                                break;
                            case 4:
                                line.Status = "Error";
                                break;
                        } 
                        
                        line.ReasonCode = rs.GetInt32(rs.GetOrdinal("l_status"));
                        line.Reason = rs.GetString(rs.GetOrdinal("sz_status"));

                        // Alert Targets
                        switch (rs.GetByte(rs.GetOrdinal("alerttarget")))
                        {
                            case 1: // AlertObject
                                AlertObject alertObject = new AlertObject();
                                alertObject.ExternalId = rs.GetString(rs.GetOrdinal("externalid"));
                                alertObject.Name = line.Name;

                                Phone phone = new Phone();
                                phone.Address = line.PhoneNumber;
                                phone.CanReceiveSms = CanReceiveSms;

                                alertObject.Endpoints.Add(phone);

                                line.AlertTarget = alertObject;
                                break;
                            case 2: // StreetAddress
                                StreetAddress streetAddress = new StreetAddress();
                                streetAddress.HouseNo = rs.GetInt32(rs.GetOrdinal("houseno"));
                                streetAddress.Letter = rs.GetString(rs.GetOrdinal("letter"));
                                streetAddress.MunicipalCode = rs.GetInt32(rs.GetOrdinal("municipalid")).ToString();
                                streetAddress.Oppgang = rs.GetString(rs.GetOrdinal("oppgang"));
                                streetAddress.StreetNo = rs.GetInt32(rs.GetOrdinal("streetid"));

                                line.AlertTarget = streetAddress;
                                break;
                            case 3: // PropertyAddress
                                PropertyAddress propertyAddress = new PropertyAddress();
                                propertyAddress.Bnr = rs.GetInt32(rs.GetOrdinal("bnr"));
                                propertyAddress.Fnr = rs.GetInt32(rs.GetOrdinal("fnr"));
                                propertyAddress.Gnr = rs.GetInt32(rs.GetOrdinal("gnr"));
                                propertyAddress.MunicipalCode = rs.GetInt32(rs.GetOrdinal("municipalid")).ToString();
                                propertyAddress.Unr = rs.GetInt32(rs.GetOrdinal("unr"));

                                line.AlertTarget = propertyAddress;
                                break;
                            case 4: // OwnerAddress
                                OwnerAddress ownerAddress = new OwnerAddress();

                                String[] ownerProperties = rs.GetString(rs.GetOrdinal("data")).Split('|');
                                if (ownerProperties.Count() == 6)
                                {
                                    ownerAddress.Adresselinje1 = ownerProperties[0];
                                    ownerAddress.Adresselinje2 = ownerProperties[1];
                                    ownerAddress.Adresselinje3 = ownerProperties[2];
                                    int eierId;
                                    if (int.TryParse(rs.GetString(rs.GetOrdinal("externalid")), out eierId))
                                        ownerAddress.EierId = eierId;
                                    ownerAddress.EierIdKode = (NorwayEierIdKode)Enum.Parse(typeof(NorwayEierIdKode), ownerProperties[3], true);
                                    ownerAddress.EierKategoriKode = (NorwayEierKategoriKode)Enum.Parse(typeof(NorwayEierKategoriKode), ownerProperties[4], true);
                                    ownerAddress.EierStatusKode = (NorwayEierStatusKode)Enum.Parse(typeof(NorwayEierStatusKode), ownerProperties[5], true);
                                }
                                else
                                {
                                    ownerAddress.EierIdKode = NorwayEierIdKode.ANNEN_PERSON;
                                    ownerAddress.EierKategoriKode = NorwayEierKategoriKode.IKKE_DEFINERT;
                                    ownerAddress.EierStatusKode = NorwayEierStatusKode.IKKE_DEFINERT;
                                }
                                ownerAddress.DateOfBirth = rs.GetInt32(rs.GetOrdinal("birthdate"));
                                ownerAddress.Navn = rs.GetString(rs.GetOrdinal("name"));
                                ownerAddress.Postnr = rs.GetInt32(rs.GetOrdinal("postno"));

                                line.AlertTarget = ownerAddress;
                                break;
                        }

                        // Get attributes, listed as key=value and seperated with | ex: "Morten=Tester|Gate=Steinstemveien 20|"
                        List<DataItem> targetAttributes = new List<DataItem>();
                        String[] attributes = rs.GetString(rs.GetOrdinal("attributes")).Split(new String[] { "|" }, StringSplitOptions.RemoveEmptyEntries);
                        foreach (string attribute in attributes)
                        {
                            String[] attributePair = attribute.Split('=');
                            if (attributePair.Count() == 2)
                                targetAttributes.Add(new DataItem(attributePair[0], attributePair[1]));
                        }
                        if (targetAttributes.Count > 0)
                            line.AlertTarget.Attributes = targetAttributes;

                        objectLog.Add(line);
                    }
                }*/
            }

            return objectLog;
        }

        /// <summary>
        /// Get log of addresses where no telephone number was found for a previously sent alert.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="AlertId">Alert ID</param>
        /// <param name="StartIndex">Start at</param>
        /// <param name="PageSize">Number of rows</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get log of addresses where no telephone number was found for a previously sent alert.</b>")]
        public List<LogLineNotFound> GetAlertNumberNotFoundLog(Account Account, AlertId AlertId, int StartIndex, int PageSize)
        {
            /*
             * Get ADDRESS_SOURCE where there are no link to _ALERTS nor _DUPLICATES, if norecipients=1 then the AlertTarget didn't produce any inhabitants
             */
            UmsDb umsDb = new UmsDb();
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            if (StartIndex < 0)
            {
                throw new Exception("Invalid StartIndex, should be 0..n");
            }
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);
            if (!umsDb.ValidateOwnerOfProject(AlertId.Id, logonInfo.l_deptpk))
            {
                throw new Exception("Account has no access to the specified alert or the alert does not exist");
            }

            // select all records that have norecipients=1, this means that no persons are registered on the alerttarget and no name attached
            // select all records that have no relations to an alert nor was a duplicate, this means that the 
            String Sql = @"SELECT 
                                ISNULL(MAS.municipalid,0) municipalid 
                                ,ISNULL(MAS.streetid,0) streetid 
                                ,ISNULL(MAS.houseno,0) houseno 
                                ,ISNULL(MAS.letter,'') letter
                                ,ISNULL(MAS.oppgang,'') oppgang
                                ,ISNULL(MAS.gnr,0) gnr
                                ,ISNULL(MAS.bnr,0) bnr
                                ,ISNULL(MAS.fnr,0) fnr
                                ,ISNULL(MAS.snr,0) snr
                                ,ISNULL(MAS.unr,0) unr
                                ,ISNULL(MAS.alerttarget,0) alerttarget
                                ,ISNULL(MAS.birthdate,0) birthdate
                                ,ISNULL(MAS.name,'') name
                                ,ISNULL(MAS.iscompany,0) iscompany 
                                ,ISNULL(MAS.postno,0) postno
                                ,ISNULL(MAS.externalid,'') externalid
                                ,ISNULL(MAS.data,'') data
                                    ,ISNULL(MAS.attributes, '') attributes
                                ,MASA.l_alertsourcepk, MASAD.l_alertsourcepk 
                        FROM 
                        MDVHIST_ADDRESS_SOURCE MAS 
                        LEFT JOIN MDVHIST_ADDRESS_SOURCE_ALERTS MASA ON MASA.l_alertsourcepk=MAS.l_alertsourcepk
                        LEFT JOIN MDVHIST_ADDRESS_SOURCE_DUPLICATES MASAD ON MASAD.l_alertsourcepk=MAS.l_alertsourcepk
                        where 
                        (MASA.l_alertsourcepk IS NULL AND MASAD.l_alertsourcepk IS NULL) and
                            MAS.l_projectpk=?";
            List<LogLineNotFound> list = new List<LogLineNotFound>();

            int count = 0;
            using (OdbcCommand cmd = umsDb.CreateCommand(Sql))
            {
                cmd.Parameters.Add("projectpk", OdbcType.Numeric).Value = AlertId.Id;
                using (OdbcDataReader rs = cmd.ExecuteReader())
                {
                    while (rs.Read() && (PageSize == 0 || count < PageSize + StartIndex))
                    {
                        if (++count <= StartIndex)
                        {
                            continue;
                        }

                        list.Add(new LogLineNotFound()
                        {
                            Name = rs.GetString(rs.GetOrdinal("name")),
                            ExternalId = rs.GetString(rs.GetOrdinal("externalid")),
                            RequestedAlertTarget = AlertTargetHelpers.ReconstructAlertTarget(
                                                        rs.GetByte(rs.GetOrdinal("alerttarget")),
                                                        rs.GetByte(rs.GetOrdinal("iscompany")),
                                                        rs.GetString(rs.GetOrdinal("name")),
                                                        rs.GetInt32(rs.GetOrdinal("municipalid")),
                                                        rs.GetInt32(rs.GetOrdinal("streetid")),
                                                        rs.GetInt32(rs.GetOrdinal("houseno")),
                                                        rs.GetString(rs.GetOrdinal("letter")),
                                                        rs.GetString(rs.GetOrdinal("oppgang")),
                                                        rs.GetInt32(rs.GetOrdinal("gnr")),
                                                        rs.GetInt32(rs.GetOrdinal("bnr")),
                                                        rs.GetInt32(rs.GetOrdinal("fnr")),
                                                        rs.GetInt32(rs.GetOrdinal("snr")),
                                                        rs.GetInt32(rs.GetOrdinal("unr")),
                                                        rs.GetInt32(rs.GetOrdinal("postno")),
                                                        rs.GetString(rs.GetOrdinal("data")),
                                                        rs.GetInt32(rs.GetOrdinal("birthdate")),
                                                        rs.GetString(rs.GetOrdinal("attributes")),
                                                        rs.GetString(rs.GetOrdinal("externalid")),
                                                        new Phone()),

                        });
                    }
                }
            }
            return list;
        }

        /// <summary>
        /// Get status of a previously sent alert.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="AlertId">The alert id</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get status of a previously sent alert.</b>")]
        public LogSummary GetAlertLog(Account Account, AlertId AlertId)
        {
            UmsDb umsDb = new UmsDb();
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            String Sql = String.Format("SELECT "
            + "BP.l_projectpk, isnull(BP.sz_name,''), isnull(MDV.l_sendingstatus,1), isnull(MDV.l_scheddate,0), "
            + "isnull(MDV.l_schedtime,0), isnull(MDV.f_dynacall,1), isnull(SQ.l_status, 1), isnull(MDV.l_type,1), "
            + "isnull(SQ.l_proc, 0) SmsProc, isnull(SQ.l_items, 0) SmsItems, isnull(BQ.l_proc, 0) VoiceProc, "
            + "isnull(BQ.l_items, 0) VoiceItems, isnull(MDV.l_createdate,0), isnull(MDV.l_createtime,0), "
            + "isnull(MDV.l_refno, 0) IsProcessing, isnull(TTS.l_fileno,0) TtsFileno, isnull(TTS.sz_content,'') TtsContent, "
            + "isnull(SQ.sz_text, '') SmsContent, isnull(BP.l_finished, 0) l_finished "
            + "FROM BBPROJECT BP LEFT OUTER JOIN BBPROJECT_X_REFNO XR ON BP.l_projectpk=XR.l_projectpk "
            + "LEFT OUTER JOIN MDVSENDINGINFO MDV ON MDV.l_refno=XR.l_refno "
            + "LEFT OUTER JOIN SMSQREF SQ ON MDV.l_refno=SQ.l_refno "
            + "LEFT OUTER JOIN BBQREF BQ ON MDV.l_refno=BQ.l_refno "
            + "LEFT OUTER JOIN BBQREF_TTSREF TTS ON MDV.l_refno=TTS.l_refno "
            + "WHERE BP.l_deptpk={0} AND BP.l_projectpk={1}"
            , 
            logonInfo.l_deptpk, AlertId.Id);
            using (OdbcDataReader rs = umsDb.ExecReader(Sql, UmsDb.UREADER_AUTOCLOSE))
            {
                long prevProjectpk = -1;
                LogSummary currentSummary = new LogSummary();
                int worstStatus = 9;
                int VoiceProc = 0;
                while (rs.Read())
                {
                    long projectPk = rs.GetInt64(0);
                    if (!prevProjectpk.Equals(projectPk))
                    {
                        worstStatus = 9;
                        currentSummary = new LogSummary()
                        {
                            AlertId = new AlertId(rs.GetInt64(0)),
                            Exercise = rs.GetByte(5) != 1,
                            Title = rs.GetString(1),
                        };
                    }

                    int stopped = rs.GetInt32(rs.GetOrdinal("l_finished"));
                    SendChannel type = (SendChannel)Enum.ToObject(typeof(SendChannel), rs.GetInt32(7)); //1 = voice, 2 = sms
                    int status = stopped == 8 ? stopped : type.Equals(SendChannel.VOICE) ? rs.GetInt32(2) : (int)rs.GetByte(6);

                    int createDate = rs.GetInt32(12);
                    int createTime = rs.GetInt16(13);
                    int schedDate = rs.GetInt32(3);
                    int schedTime = rs.GetInt32(4);
                    int refno = rs.GetInt32(14);
                    int ttsFileno = rs.GetByte(15);
                    String ttsContent = rs.GetString(16);
                    String smsContent = rs.GetString(17);

                    bool isProcessing = refno > 0; //if record exist in MDVSENDINGINFO, the service have picked it up.
                    if (!isProcessing)
                    {
                        status = 1;
                    }

                    UmsDb dbCount = new UmsDb();
                    if (type.Equals(SendChannel.SMS))
                    {
                        currentSummary.SmsSent += rs.GetInt32(8);
                        currentSummary.SmsTotal += rs.GetInt32(9);
                        IDictionary<int, int> smsDeliveryStatus = dbCount.GetNumberOfSmsBasedOnDst(refno);
                        if (smsDeliveryStatus.ContainsKey(0))
                        {
                            currentSummary.SmsReceived = smsDeliveryStatus[0];
                        }
                        currentSummary.SmsMessage = smsContent;
                    }
                    else if (type.Equals(SendChannel.VOICE))
                    {
                        VoiceProc += rs.GetInt32(10);
                        currentSummary.VoiceTotal += rs.GetInt32(11);
                        IDictionary<int, int> voiceDeliveryStatus = dbCount.GetNumberOfVoiceBasedOnDst(refno);
                        if (voiceDeliveryStatus.ContainsKey(0))
                        {
                            currentSummary.VoiceAnswered = voiceDeliveryStatus[0];
                        }
                        if (voiceDeliveryStatus.ContainsKey(2))
                        {
                            currentSummary.VoiceUnanswered = voiceDeliveryStatus[2];
                        }
                        if (voiceDeliveryStatus.ContainsKey(3))
                        {
                            currentSummary.VoiceConfirmed = voiceDeliveryStatus[3];
                        }

                        currentSummary.VoiceMessage = ttsContent;

                        String wavFile = String.Format("{0}\\v{1}_{2}.wav", UCommon.UPATHS.sz_path_audiofiles, refno, ttsFileno);
                        try
                        {
                            currentSummary.VoiceAudio = File.ReadAllBytes(wavFile);
                        }
                        catch (Exception)
                        {
                            ULog.warning("Could not find audio file {0}", wavFile);
                        }

                    }
                    dbCount.close();



                    String schedStr = String.Format("{0:D8}{1:D4}", schedDate > 0 ? schedDate : createDate, schedDate > 0 ? schedTime : createTime);
                    DateTime scheduled = new DateTime();
                    if (isProcessing)
                    {
                        try
                        {
                            scheduled = DateTime.ParseExact(schedStr, "yyyyMMddHHmm", System.Globalization.CultureInfo.InvariantCulture);
                        }
                        catch (Exception)
                        {
                            scheduled = DateTime.ParseExact(String.Format("{0:D8}{1:D4}", createDate, createTime), "yyyyMMddHHmm", System.Globalization.CultureInfo.InvariantCulture);
                        }
                    }
                    currentSummary.StartDateTime = scheduled;
                    if (currentSummary != null && status < worstStatus)
                    {
                        currentSummary.ProgressStatus = GetOverallStatusFromStatuscode(status, scheduled);
                        currentSummary.Status = currentSummary.ProgressStatus.ToString();
                        worstStatus = status;
                    }


                    currentSummary.Errors = GetErrors(projectPk);

                    prevProjectpk = projectPk;
                }
                if (prevProjectpk > 0)
                {
                    return currentSummary;
                }
            }
            throw new Exception("Alert Log not found for the specified AlertId or wrong account used");
        }

        /// <summary>
        /// Get array of previously sent alerts. Newest first.
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="StartIndex">0 index</param>
        /// <param name="PageSize"></param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get array of previously sent alerts. Newest first. 0-index Start</b>")]
        public List<AlertSummary> GetAlerts(Account Account, int StartIndex, int PageSize)
        {
            if (StartIndex < 0)
            {
                throw new Exception("Error, StartIndex should be >=0");
            }
            List<AlertSummary> alertSummaryList = new List<AlertSummary>();
            UmsDb umsDb = new UmsDb();
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            String Sql = String.Format("SELECT "
                        + "BP.l_projectpk, isnull(BP.sz_name,''), isnull(MDV.l_sendingstatus,1), isnull(MDV.l_scheddate,0), "
                        + "isnull(MDV.l_schedtime,0), isnull(MDV.f_dynacall,1), isnull(SQ.l_status, 1), isnull(MDV.l_type,1), "
                        + "isnull(SQ.l_proc, 0) SmsProc, isnull(SQ.l_items, 0) SmsItems, isnull(BQ.l_proc, 0) VoiceProc, "
                        + "isnull(BQ.l_items, 0) VoiceItems, isnull(MDV.l_createdate,0), isnull(MDV.l_createtime,0), "
                        + "isnull(MDV.l_refno, 0) IsProcessing, isnull(BP.l_finished, 0) l_finished "
                        + "FROM BBPROJECT BP LEFT OUTER JOIN BBPROJECT_X_REFNO XR ON BP.l_projectpk=XR.l_projectpk "
                        + "LEFT OUTER JOIN MDVSENDINGINFO MDV ON MDV.l_refno=XR.l_refno "
                        + "LEFT OUTER JOIN SMSQREF SQ ON MDV.l_refno=SQ.l_refno "
                        + "LEFT OUTER JOIN BBQREF BQ ON MDV.l_refno=BQ.l_refno "
                        + "WHERE BP.l_deptpk={0} AND XR.l_type=0 "
                        + "ORDER BY BP.l_projectpk DESC, XR.l_refno DESC", logonInfo.l_deptpk, PageSize);
            OdbcDataReader rs = umsDb.ExecReader(Sql, UmsDb.UREADER_AUTOCLOSE);

            //int startAt = -1;//set to -1 as it's zero index

            long prevProjectpk = -1;
            AlertSummary currentSummary = null;
            int worstStatus = 9;
            int SmsItems = 0;
            int VoiceItems = 0;
            int SmsProc = 0;
            int VoiceProc = 0;
            int endAt = -1;
            int distinctProjectCount = 0;

            while (rs.Read())
            {
                long projectPk = rs.GetInt64(0);
                /*SendChannel type = (SendChannel)Enum.ToObject(typeof(SendChannel), rs.GetInt32(7)); //1 = voice, 2 = sms
                int status = type.Equals(SendChannel.VOICE) ? rs.GetInt32(2) : rs.GetByte(6);*/
                int stopped = rs.GetInt32(rs.GetOrdinal("l_finished"));
                SendChannel type = (SendChannel)Enum.ToObject(typeof(SendChannel), rs.GetInt32(7)); //1 = voice, 2 = sms
                int status = stopped == 8 ? stopped : type.Equals(SendChannel.VOICE) ? rs.GetInt32(2) : (int)rs.GetByte(6);

                SmsProc += rs.GetInt32(8);
                SmsItems += rs.GetInt32(9);
                VoiceProc += rs.GetInt32(10);
                VoiceItems += rs.GetInt32(11);

                int createDate = rs.GetInt32(12);
                int createTime = rs.GetInt16(13);
                int schedDate = rs.GetInt32(3);
                int schedTime = rs.GetInt32(4);
                bool isProcessing = rs.GetInt32(14) > 0; //if record exist in MDVSENDINGINFO, the service have picked it up.
                if (!isProcessing)
                {
                    status = 1;
                }

                String schedStr = String.Format("{0:D8}{1:D4}", schedDate > 0 ? schedDate : createDate, schedDate > 0 ? schedTime : createTime);
                DateTime scheduled = new DateTime();
                if (isProcessing)
                {
                    try
                    {
                        scheduled = DateTime.ParseExact(schedStr, "yyyyMMddHHmm", System.Globalization.CultureInfo.InvariantCulture);
                    }
                    catch (Exception)
                    {
                        scheduled = DateTime.ParseExact(String.Format("{0:D8}{1:D4}", createDate, createTime), "yyyyMMddHHmm", System.Globalization.CultureInfo.InvariantCulture);
                    }
                }

                if (!prevProjectpk.Equals(projectPk))
                {
                    ++distinctProjectCount;
                    if (distinctProjectCount < StartIndex + 1)
                    {
                        prevProjectpk = projectPk;
                        continue;
                    }

                    
                    if (++endAt >= PageSize)
                    {
                        break;
                    }

                    worstStatus = 9;
                    currentSummary = new AlertSummary()
                    {
                        AlertId = new AlertId(rs.GetInt64(0)),
                        Exercise = rs.GetByte(5) != 1,
                        StartDateTime = scheduled,
                        Title = rs.GetString(1),
                    };
                    alertSummaryList.Add(currentSummary);                    
                }
                if (currentSummary != null && status < worstStatus)
                {
                    currentSummary.ProgressStatus = GetOverallStatusFromStatuscode(status, scheduled);
                    currentSummary.Status = currentSummary.ProgressStatus.ToString();
                    worstStatus = status;
                }

                prevProjectpk = projectPk;
            }

            rs.Close();
            return alertSummaryList;
        }

        /// <summary>
        /// Convert status code to a more user friendly version
        /// </summary>
        /// <param name="statusCode"></param>
        /// <returns></returns>
        private AlertOverallStatus GetOverallStatusFromStatuscode(int alertStatusCode, DateTime scheduled)
        {
            switch (alertStatusCode)
            {
                case 1:
                    return AlertOverallStatus.NOT_PROCESSED;
                case 2:
                case 3:
                case 4:
                case 5:
                    if(scheduled > DateTime.Now)
                        return AlertOverallStatus.SCHEDULED;
                    return AlertOverallStatus.IN_PROGRESS;
                case 6:
                    return AlertOverallStatus.IN_PROGRESS;
                case 7:
                    return AlertOverallStatus.FINISHED;
                case 8:
                    return AlertOverallStatus.STOPPED;
            }
            return AlertOverallStatus.FAILED;
        }

        /// <summary>
        /// Converts status type (1, 2, 3, 4 / confirmed, delivered, undelivered, error) to sms dst (dbnull, 0, -1, 2 / NA, delivered, awayting status, error)
        /// </summary>
        /// <param name="statusType"></param>
        /// <returns></returns>
        private object ConvertStatusTypeToSmsDst(int statusType)
        {
            switch (statusType)
            {
                case 2:
                    return 0;
                case 3:
                    return -1;
                case 4:
                    return 2;
                case 1:
                default:
                    return DBNull.Value;
            }
        }

        /// <summary>
        /// Convert from int (yyyyMMdd) to string (ddMMyy)
        /// </summary>
        /// <param name="dateOfBirth"></param>
        /// <returns></returns>
        private string DateOfBirthToString(int dateOfBirth)
        {
            string tmp = dateOfBirth.ToString();
            if (tmp.Length == 8)
                return tmp.Substring(6, 2) + tmp.Substring(4, 2) + tmp.Substring(2, 2);
            else
                return "";
        }

        /// <summary>
        /// Get time profile for creating alert, this may be used in debugging to find bottlenecks
        /// in time consumption.
        /// Throws exception if logon fails.
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="AlertId"></param>
        /// <returns>A list of TimeProfiles attached to the specified AlertID</returns>
        [WebMethod(Description = @"<b>Get time profile for creating alert</b><br>This may be used in debugging to find bottlenecks in time consumption.")]
        public List<TimeProfile> GetTimeProfile(Account Account, AlertId AlertId)
        {
            UmsDb db = new UmsDb();
            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;
            db.CheckDepartmentLogonLiteral(ref logonInfo);

            return db.GetTimeProfiles(AlertId.Id);
        }

        private List<LogLinePhone> GetErrors(long alertPk)
        {
            List<LogLinePhone> errorList = new List<LogLinePhone>();

            UmsDb db = new UmsDb();
        
            string sql = "sp_getProjectStatus ?, 4";
            using (OdbcCommand cmd = db.CreateCommand(sql))
            {
                cmd.Parameters.Add("projectpk", OdbcType.BigInt).Value = alertPk;

                using (OdbcDataReader rs = cmd.ExecuteReader())
                {
                    while (rs.Read())
                    {
                        DateTime timestamp;
                        DateTime.TryParseExact(rs.GetDecimal(rs.GetOrdinal("l_timestamp")).ToString(), "yyyyMMddHHmmss", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out timestamp);

                        LogLinePhone phoneLine = new LogLinePhone(rs.GetString(rs.GetOrdinal("sz_number")), rs.GetInt32(rs.GetOrdinal("l_type")), rs.GetInt32(rs.GetOrdinal("l_dst")), rs.GetInt32(rs.GetOrdinal("l_status")), rs.GetString(rs.GetOrdinal("sz_status")), timestamp, -1, -1);
                        errorList.Add(phoneLine);
                    }
                }
            }

            return errorList;
        }
    }
}
