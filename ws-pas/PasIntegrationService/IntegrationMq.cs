using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Apache.NMS;
using Apache.NMS.Util;
using System.Threading;
using com.ums.UmsCommon;
using System.Configuration;
using Apache.NMS.ActiveMQ;
using System.Xml.Serialization;
using System.IO;
using log4net;

namespace com.ums.pas.integration
{
    class IntegrationMq 
    {
        private static ILog log = LogManager.GetLogger(typeof(IntegrationMq));
        Atomic<Boolean> _keepRunning = new Atomic<bool>();
        private Dictionary<string, AsyncMessageHelper> responseBuffer;

        /// <summary>
        /// Signal to false if stop is requested
        /// </summary>
        public Atomic<Boolean> KeepRunning
        {
            get
            {
                return _keepRunning;
            }
        }


        private Atomic<Boolean> _isRunning = new Atomic<bool>(false);

        /// <summary>
        /// Will be set to true when Queue starts up.
        /// Sets to false when Queue ends after KeepRunning signalling
        /// </summary>
        public Atomic<Boolean> IsRunning
        {
            get { return _isRunning; }
            set { _isRunning = value; }
        }



        public IntegrationMq()
        {
            this.responseBuffer = new Dictionary<string, AsyncMessageHelper>();
        }

        /// <summary>
        /// Helper class which assists with keeping track of message timeouts, blocking the sending thread and carrying messages through the async eventing back
        /// to the synchronous call.
        /// </summary>
        private class AsyncMessageHelper : IDisposable
        {
            public IMessage Message { get; set; }
            public AutoResetEvent Trigger { get; private set; }

            public AsyncMessageHelper()
            {
                this.Trigger = new AutoResetEvent(false);
            }

            #region IDisposable Members

            public void Dispose()
            {
                this.Trigger.Dispose();
                this.Message = null;
            }

            #endregion
        }

        void mqConsumer_Listener(IMessage message)
        {
            // Look for an async helper with the same correlation ID.
            AsyncMessageHelper asyncMessageHelper;
            lock (this.responseBuffer)
            {
                // If no async helper with the same correlation ID exists, then we've received some erranious message that we don't care about.
                if (!this.responseBuffer.TryGetValue(message.NMSCorrelationID, out asyncMessageHelper))
                    return;
            }

            // Set the Message property so we can access it in the send method.
            asyncMessageHelper.Message = message;

            // Fire the trigger so that the send method stops blocking and continues on its way.
            asyncMessageHelper.Trigger.Set();
        }

        public void startUpQueue()
        {
            KeepRunning.GetAndSet(true);
            IsRunning.GetAndSet(true);

            //keep on going until stopped
            while (KeepRunning.Value)
            {
                Uri connectionUri = null;
                //connect to activemq
                try
                {
                    //System.Configuration.ConfigurationManager.AppSettings["ActiveMqUri"];
                    connectionUri = new Uri(System.Configuration.ConfigurationManager.AppSettings["ActiveMqUri"]);
                    String logStr = String.Format("Connecting to ActiveMqUri={0}", connectionUri);
                    ULog.write(logStr);
                    log.Info(logStr);
                }
                catch (Exception e)
                {
                    ULog.error(e.ToString());
                    log.Error(e.ToString());
                    Thread.Sleep(5000);
                    continue;
                }
                IConnectionFactory mqFactory = null;
                try
                {
                    mqFactory = new ConnectionFactory(connectionUri);
                }
                catch (Exception e)
                {
                    ULog.error(e.ToString());
                    Thread.Sleep(5000);
                    continue;
                }
                using (IConnection mqConnection = mqFactory.CreateConnection())
                using (ISession mqSession = mqConnection.CreateSession(AcknowledgementMode.ClientAcknowledge))
                {
                    String mqDestination = System.Configuration.ConfigurationManager.AppSettings["ActiveMqDestination"];
                        //PasIntegrationService.Default.ActiveMqDestination;
                    IDestination destination = SessionUtil.GetDestination(mqSession, mqDestination);
                    log.InfoFormat("Using destination = {0}", destination);
                    String connString = ConfigurationManager.ConnectionStrings["backbone"].ConnectionString;
                    String dbConn = String.Format("Using database {0}", connString.Remove(connString.LastIndexOf("PWD") + 4));
                    log.Info(dbConn);

                    using (IMessageConsumer mqConsumer = mqSession.CreateConsumer(destination))
                    {
                        mqConnection.Start();
                        //mqConsumer.Listener += new MessageListener(mqConsumer_Listener);
                        while (KeepRunning.Value)
                        {
                            IMessage message = mqConsumer.Receive(TimeSpan.FromSeconds(1.0));

                            if (message == null)
                            {
                                //Console.WriteLine("No message received");
                            }
                            else if(message is IObjectMessage)
                            {
                                IObjectMessage objectMessage = (IObjectMessage)message;                                
                                if (objectMessage.Body is AlertMqPayload)
                                {
                                    String logMsg = String.Format("Received new ActiveMq message");
                                    log.Info(logMsg);
                                    ULog.write(logMsg);
                                    AlertMqPayload payload = (AlertMqPayload)objectMessage.Body;
                                    XmlSerializer xmlSerializer = new XmlSerializer(typeof(AlertMqPayload));

                                    xmlSerializer.Serialize(new FileStream(String.Format("{0}\\projectpk_{1}.xml", System.Configuration.ConfigurationManager.AppSettings["TempPath"], payload.AlertId.Id), FileMode.Create), payload);

                                    Account account = payload.Account;
                                    if (payload.AlertId.Id <= 0)
                                    {
                                        logMsg = String.Format("No projectpk specified for\n\n MessageId {0}", objectMessage.NMSMessageId);
                                        ULog.error(logMsg);
                                    }
                                    else if (account == null)
                                    {
                                        logMsg = String.Format("No account specified for\n\n ProjectPk {0}\n\n MessageId {1}", payload.AlertId.Id, objectMessage.NMSMessageId);
                                        ULog.error(logMsg);
                                    }
                                    else if (payload.AlertTargets == null || payload.AlertTargets.Count == 0)
                                    {
                                        logMsg = String.Format("No alertTargets specified for\n\n ProjectPk {0}\n\n MessageId {1}", payload.AlertId.Id, objectMessage.NMSMessageId);
                                        ULog.error(logMsg);
                                    }
                                    else if (payload.ChannelConfigurations == null || payload.AlertTargets.Count == 0)
                                    {
                                        logMsg = String.Format("No channelConfigurations specified for\n\n ProjectPk {0}\n\n MessageId {1}", payload.AlertId.Id, objectMessage.NMSMessageId);
                                        ULog.error(logMsg);
                                    }
                                    else
                                    {
                                        String summary = "SUMMARY\n\n";
                                        //payload.alertTargets.ForEach(val => summary += Helpers.ToStringExtension(val) + "\n");
                                        //typeof(AlertTarget).Assembly.GetTypes().Where(type => type.IsSubclassOf(typeof(AlertTarget))).ToList().ForEach(val => payload.alertTargets.OfType<val>().Count());
                                        summary += "AdHoc AlertObject = " + payload.AlertTargets.OfType<AlertObject>().Count() + "\n";
                                        //summary += "AdHoc Recipient = " + payload.AlertTargets.OfType<Recipient>().Count() + "\n";
                                        summary += "StoredAddress = " + payload.AlertTargets.OfType<StoredAddress>().Count() + "\n";
                                        summary += "StoredList = " + payload.AlertTargets.OfType<StoredList>().Count() + "\n";
                                        summary += "PropertyAddress = " + payload.AlertTargets.OfType<PropertyAddress>().Count() + "\n";
                                        summary += "StreetAddress = " + payload.AlertTargets.OfType<StreetAddress>().Count() + "\n";
                                        summary += "OwnerAddress = " + payload.AlertTargets.OfType<OwnerAddress>().Count() + "\n\n";
                                        try
                                        {
                                            payload.ChannelConfigurations.ForEach(val => summary += "CONFIGURATION\n" + StringHelpers.ToStringExtension(val) + "\n");
                                        }
                                        catch (Exception e)
                                        {
                                            summary += "Unable to append channel configurations to summary";
                                        }
                                        logMsg = String.Format("Received message from account Company/Department {0}/{1}\n\n ProjectPk {2}\n\n MessageId {3}\n\n{4}",
                                            payload.Account.CompanyId, payload.Account.DepartmentId, payload.AlertId.Id, objectMessage.NMSMessageId, summary);
                                        ULog.write(logMsg);

                                        try
                                        {
                                            ITimeProfilerCollector collector = new TimeProfilerCollector();
                                            using (new TimeProfiler(payload.AlertId.Id, "Handle entire alert", collector))
                                            {
                                                new DataHandlerImpl().HandleAlert(payload);
                                            }
                                        }
                                        catch (Exception e)
                                        {
                                            String errorText = String.Format("Failed to generate alert\n\n" + e.ToString());
                                            ULog.error(errorText);
                                            log.Info(errorText);
                                            //increment tries here, finally ack the message to make it go away.
                                        }
                                    }
                                    logMsg = String.Format("Message acknowledged");
                                    log.Info(logMsg);
                                    ULog.write(logMsg);
                                    message.Acknowledge();

                                }
                                else
                                {
                                    String errorText = String.Format("Received message of incompatible type {0} with MessageId {1}", message, message.NMSMessageId);
                                    log.Error(errorText);
                                    ULog.error(errorText);
                                    message.Acknowledge();
                                }

                            }
                        }
                        mqConnection.Close();
                    }
                }
            }
            IsRunning.GetAndSet(false);
        }
    }
}
