using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Apache.NMS;
using Apache.NMS.Util;
using System.Threading;
using com.ums.UmsCommon;
using System.Configuration;

namespace com.ums.pas.integration
{
    class IntegrationMq 
    {
        Atomic<Boolean> _keepRunning = new Atomic<bool>();
        private Dictionary<string, AsyncMessageHelper> responseBuffer;


        public Atomic<Boolean> KeepRunning
        {
            get
            {
                return _keepRunning;
            }
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

            //keep on going until stopped
            while (KeepRunning.Value)
            {
                Uri connectionUri = null;
                //connect to activemq
                try
                {
                    connectionUri = new Uri(System.Configuration.ConfigurationManager.AppSettings["ActiveMqUri"]);
                }
                catch (Exception e)
                {
                    ULog.error(e.ToString());
                    Thread.Sleep(5000);
                    continue;
                }
                IConnectionFactory mqFactory = new NMSConnectionFactory(connectionUri);
                using (IConnection mqConnection = mqFactory.CreateConnection())
                using (ISession mqSession = mqConnection.CreateSession(AcknowledgementMode.ClientAcknowledge))
                {
                    IDestination destination = SessionUtil.GetDestination(mqSession, System.Configuration.ConfigurationManager.AppSettings["ActiveMqDestination"]);
                    Console.WriteLine("Using destination = {0}", destination);

                    using (IMessageConsumer mqConsumer = mqSession.CreateConsumer(destination))
                    {
                        mqConnection.Start();
                        //mqConsumer.Listener += new MessageListener(mqConsumer_Listener);
                        while (KeepRunning.Value)
                        {
                            IMessage message = mqConsumer.Receive(TimeSpan.FromSeconds(1.0));

                            if (message == null)
                            {
                                Console.WriteLine("No message received");
                            }
                            else if(message is IObjectMessage)
                            {
                                IObjectMessage objectMessage = (IObjectMessage)message;                                
                                if (objectMessage.Body is AlertMqPayload)
                                {
                                    AlertMqPayload payload = (AlertMqPayload)objectMessage.Body;

                                    Account account = payload.Account;
                                    if (payload.projectPk <= 0)
                                    {
                                        ULog.error("No projectpk specified for\n\n MessageId {1}", objectMessage.NMSMessageId);
                                    }
                                    else if (account == null)
                                    {
                                        ULog.error("No account specified for\n\n ProjectPk {0}\n\n MessageId {1}", payload.projectPk, objectMessage.NMSMessageId);
                                    }
                                    else if (payload.AlertTargets == null || payload.AlertTargets.Count == 0)
                                    {
                                        ULog.error("No alertTargets specified for\n\n ProjectPk {0}\n\n MessageId {1}", payload.projectPk, objectMessage.NMSMessageId);
                                    }
                                    else if (payload.ChannelConfigurations == null || payload.AlertTargets.Count == 0)
                                    {
                                        ULog.error("No channelConfigurations specified for\n\n ProjectPk {0}\n\n MessageId {1}", payload.projectPk, objectMessage.NMSMessageId);
                                    }
                                    else
                                    {
                                        String summary = "SUMMARY\n\n";
                                        //payload.alertTargets.ForEach(val => summary += Helpers.ToStringExtension(val) + "\n");
                                        //typeof(AlertTarget).Assembly.GetTypes().Where(type => type.IsSubclassOf(typeof(AlertTarget))).ToList().ForEach(val => payload.alertTargets.OfType<val>().Count());

                                        summary += "AdHoc Recipient = " + payload.AlertTargets.OfType<Recipient>().Count() + "\n";
                                        summary += "StoredAddress = " + payload.AlertTargets.OfType<StoredAddress>().Count() + "\n";
                                        summary += "StoredList = " + payload.AlertTargets.OfType<StoredList>().Count() + "\n";
                                        summary += "PropertyAddress = " + payload.AlertTargets.OfType<PropertyAddress>().Count() + "\n";
                                        summary += "StreetAddress = " + payload.AlertTargets.OfType<StreetAddress>().Count() + "\n\n";
                                        payload.ChannelConfigurations.ForEach(val => summary += "CONFIGURATION\n" + Helpers.ToStringExtension(val) + "\n");

                                        ULog.write("Received message from account Company/Department/User {0}/{1}/{2}\n\n ProjectPk {3}\n\n MessageId {4}\n\n{5}",
                                            payload.Account.companyId, payload.Account.departmentId, payload.Account.userId, payload.projectPk, objectMessage.NMSMessageId, summary);

                                        try
                                        {
                                            new DataHandlerImpl().HandleAlert(payload);
                                            message.Acknowledge();
                                        }
                                        catch (Exception e)
                                        {
                                            ULog.error("Failed to generate alert\n\n" + e.ToString());
                                            //increment tries here, finally ack the message to make it go away.
                                        }
                                    }

                                }
                                else
                                {
                                    ULog.error("Received message of incompatible type {0} with MessageId {1}", message, message.NMSMessageId);
                                    message.Acknowledge();
                                }

                            }
                        }
                        mqConnection.Close();
                    }
                }
            }
        }
    }
}
