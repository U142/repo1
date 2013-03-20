using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using log4net;
using System.Data.Odbc;

namespace com.ums.pas.integration.AddressLookup
{
    class FollowupLookupImpl : IFollowupLookupFacade
    {
        private static ILog log = LogManager.GetLogger(typeof(FollowupLookupImpl));

        #region IFollowupLookupFacade Members

        /// <summary>
        /// Connect to backbone and retrieve recipientdata from previous alerts.
        /// </summary>
        /// <param name="ConnectionString"></param>
        /// <param name="FollowupAlerts"></param>
        /// <returns></returns>
        public IEnumerable<RecipientData> GetRecipientsFromEarlierAlerts(String ConnectionString, List<FollowupAlertObject> FollowupAlerts)
        {
            if (FollowupAlerts.Count == 0)
            {
                log.Info("No followup alerts listed");
                return new List<RecipientData>();
            }

            List<RecipientData> recipients = new List<RecipientData>();
            using (OdbcConnection Connection = new OdbcConnection(ConnectionString))
            using (OdbcCommand Command = Connection.CreateCommand())
            {
                Connection.Open();

            }

            return recipients;
        }

        #endregion
    }
}
