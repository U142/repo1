﻿using System;
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
        [XmlInclude(typeof(StoredAddress))]
        [XmlInclude(typeof(StreetAddress))]
        [XmlInclude(typeof(Recipient))]
        [XmlInclude(typeof(Phone))]
        [XmlInclude(typeof(AlertConfiguration))]
        [XmlInclude(typeof(ChannelConfiguration))]
        [XmlInclude(typeof(SmsConfiguration))]
        [XmlInclude(typeof(VoiceConfiguration))]

        [XmlInclude(typeof(AlertResponse))]
        [XmlInclude(typeof(DefaultResponse))]

        [WebMethod]
        public AlertResponse StartAlert(Account account, AlertConfiguration alertConfiguration, List<ChannelConfiguration> channelConfigurations, List<AlertTarget> alertTargets)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public AlertResponse StartFollowUpAlert(Account Account, AlertConfiguration alertConfiguration, String Message)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public DefaultResponse StopAlert(Account Account, AlertId AlertId)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public List<AlertSummary> GetAlerts(Account Account, int StartIndex, int PageSize)
        {
            throw new NotImplementedException();
        }


        [WebMethod]
        public AlertResponse ActiveMqTest()
        {
            AlertResponse responseObject = new AlertResponse();
            Uri connectionUri = new Uri(System.Configuration.ConfigurationManager.AppSettings["ActiveMqUri"]);
            IConnectionFactory mqFactory = new NMSConnectionFactory(connectionUri);
            try
            {
                using (IConnection mqConnection = mqFactory.CreateConnection())
                using (ISession mqSession = mqConnection.CreateSession())
                {
                    IDestination destination = SessionUtil.GetDestination(mqSession, System.Configuration.ConfigurationManager.AppSettings["ActiveMqDestination"]);
                    using (IMessageProducer mqProducer = mqSession.CreateProducer())
                    {
                        //IMessage message = mqSession.CreateObjectMessage("tester");
                        AlertMqPayload payload = new AlertMqPayload();

                        ULOGONINFO logonInfo = new ULOGONINFO();
                        logonInfo.sz_compid = "UMS";
                        logonInfo.sz_deptid = "TEST";
                        logonInfo.sz_userid = "MH";
                        logonInfo.sz_password = "mh123,11";

                        UPROJECT_REQUEST req = new UPROJECT_REQUEST();
                        req.sz_name = "ActiveMq";
                        payload.projectPk = new UProject().uproject(ref logonInfo, ref req).n_projectpk;
                        payload.Account.companyId = logonInfo.sz_compid;
                        payload.Account.departmentId = logonInfo.sz_deptid;
                        payload.Account.userId = logonInfo.sz_userid;
                        payload.Account.password = logonInfo.sz_password;

                        payload.AlertTargets.Add(AlertTargetFactory.newStreetAddress("1102", 123, 20, "", ""));
                        payload.AlertTargets.Add(AlertTargetFactory.newPropertyAddress("1102", 69, 2977, 0, 0));

                        payload.AlertConfiguration.Scheduled = new DateTime();
                        payload.AlertConfiguration.SimulationMode = true;
                        payload.AlertConfiguration.StartImmediately = true;
                        payload.AlertConfiguration.SendToAllChannels = false;

                        payload.ChannelConfigurations.Add(ChannelConfigurationFactory.newSmsConfiguration("98220213", "tester", false));
                        payload.ChannelConfigurations.Add(ChannelConfigurationFactory.newVoiceConfiguration(5, 10, 2100, 60 * 10, 2, true, 0, false, "23500801", "tester for voice"));


                        IObjectMessage message = mqSession.CreateObjectMessage(payload);
                        mqProducer.Send(destination, message);
                        responseObject.AlertId = new AlertId(payload.projectPk);
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


    }
}
