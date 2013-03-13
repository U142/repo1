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
        [XmlInclude(typeof(Recipient))]
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


        [WebMethod]
        public AlertResponse StartAlert(Account Account, AlertConfiguration AlertConfiguration, List<ChannelConfiguration> ChannelConfigurations, List<AlertTarget> AlertTargets)
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
                        accountDetails.AvailableVoiceNumbers = umsDb.GetAvailableVoiceNumbers(accountDetails.Deptpk);
                        accountDetails.DefaultTtsLang = umsDb.GetDefaultTtsLanguage(accountDetails.Deptpk);

                        if (accountDetails.DefaultTtsLang <= 0)
                        {
                            AlertResponseFactory.Failed(-6, "No Text to Speech engine set to default on department {0}", accountDetails.Deptpk);
                        }
                        
                        payload.AccountDetails = accountDetails;

                        foreach (VoiceConfiguration voiceConfig in ChannelConfigurations.OfType<VoiceConfiguration>())
                        {
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
                            int dynVoice = umsDb.getNumDynfilesInProfile(voiceConfig.VoiceProfilePk);
                            if (dynVoice != 1)
                            {
                                return AlertResponseFactory.Failed(-5, "There are {0} dynamic audio-files in voice profile {1}, to send there can only be one.", dynVoice, voiceConfig.VoiceProfilePk);
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
        public AlertResponse StartFollowUpAlert(Account Account, AlertConfiguration AlertConfiguration, String Message)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public DefaultResponse StopAlert(Account Account, AlertId AlertId)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public List<AlertSummary> TestGetAlerts()
        {
            return GetAlerts(new Account()
            {
                CompanyId = "UMS",
                DepartmentId = "DEVELOPMENT",
                Password = "ums123",
            }, 0, 50);
        }

        /// <summary>
        /// Get array of previously sent alerts
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="StartIndex"></param>
        /// <param name="PageSize"></param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get array of previously sent alerts.</b>")]
        public List<AlertSummary> GetAlerts(Account Account, int StartIndex, int PageSize)
        {
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
                        + "isnull(MDV.l_refno, 0) IsProcessing "
                        + "FROM BBPROJECT BP LEFT OUTER JOIN BBPROJECT_X_REFNO XR ON BP.l_projectpk=XR.l_projectpk "
                        + "LEFT OUTER JOIN MDVSENDINGINFO MDV ON MDV.l_refno=XR.l_refno "
                        + "LEFT OUTER JOIN SMSQREF SQ ON MDV.l_refno=SQ.l_refno "
                        + "LEFT OUTER JOIN BBQREF BQ ON MDV.l_refno=BQ.l_refno "
                        + "WHERE BP.l_deptpk={0} "
                        + "ORDER BY BP.l_projectpk, XR.l_refno", logonInfo.l_deptpk);
            OdbcDataReader rs = umsDb.ExecReader(Sql, UmsDb.UREADER_AUTOCLOSE);
            long prevProjectpk = -1;
            AlertSummary currentSummary = null;
            int worstStatus = 8;
            int SmsItems = 0;
            int VoiceItems = 0;
            int SmsProc = 0;
            int VoiceProc = 0;
            while (rs.Read())
            {
                long projectPk = rs.GetInt64(0);
                SendChannel type = (SendChannel)Enum.ToObject(typeof(SendChannel), rs.GetInt32(7)); //1 = voice, 2 = sms
                int status = type.Equals(SendChannel.VOICE) ? rs.GetInt32(2) : rs.GetByte(6);
                SmsProc += rs.GetInt32(8);
                SmsItems += rs.GetInt32(9);
                VoiceProc += rs.GetInt32(10);
                VoiceItems += rs.GetInt32(11);

                int createDate = rs.GetInt32(12);
                int createTime = rs.GetInt32(13);
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
                    worstStatus = 8;
                    currentSummary = new AlertSummary()
                    {
                        AlertId = new AlertId(rs.GetInt64(0)),
                        Exercise = rs.GetByte(5) != 1,
                        StartDateTime = scheduled,
                        Title = rs.GetString(1),
                    };
                    alertSummaryList.Add(currentSummary);                    
                }
                if (status < worstStatus)
                {
                    currentSummary.Status = status.ToString();
                    currentSummary.DeliveryStatus = GetOverallStatusFromStatuscode(status);
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
        private AlertOverallStatus GetOverallStatusFromStatuscode(int alertStatusCode)
        {
            switch (alertStatusCode)
            {
                case 1:
                    return AlertOverallStatus.NOT_PROCESSED;
                case 2:
                case 3:
                case 4:
                case 5:
                    return AlertOverallStatus.SCHEDULED;
                case 6:
                    return AlertOverallStatus.IN_PROGRESS;
                case 7:
                    return AlertOverallStatus.FINISHED;
            }
            return AlertOverallStatus.FAILED;
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



    }
}
