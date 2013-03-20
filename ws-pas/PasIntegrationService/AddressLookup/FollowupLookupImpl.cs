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

                foreach(FollowupAlertObject followUp in FollowupAlerts)
                {
                    Command.CommandText = "";
                    Command.Parameters.Add("l_projectpk", OdbcType.Numeric).Value = followUp.AlertId.Id;

                    recipients.Add(new RecipientData()
                    {
                        Address = "",
                        AlertTarget = null,
                        Company = false,
                        Endpoints = null,
                        Lat = 0,
                        Lon = 0,
                        Name = "",
                        Postno = 1234,
                        PostPlace = "",
                    });
                }
            }

            return recipients;

            /*
select 
	BP.l_projectpk, 
	XP.l_refno,
	ALERTS.l_item,
	ADRSOURCE.*,
	MDVHIST.*,
	l_type=1,
	BBHIST.sz_number

from

	BBPROJECT BP, 
	BBPROJECT_X_REFNO XP,
	MDVHIST_ADDRESS_SOURCE_ALERTS ALERTS 
	JOIN BBHIST ON ALERTS.l_refno=BBHIST.l_refno AND ALERTS.l_item=BBHIST.l_item
	JOIN MDVHIST ON ALERTS.l_refno=MDVHIST.l_refno AND ALERTS.l_item=MDVHIST.l_item,
	MDVHIST_ADDRESS_SOURCE ADRSOURCE

where
	BP.l_projectpk=XP.l_projectpk
	and XP.l_refno=ALERTS.l_refno
	and ALERTS.l_alertsourcepk=ADRSOURCE.l_alertsourcepk
	and BP.l_projectpk = 433

union

select 
	BP.l_projectpk, 
	XP.l_refno,
	ALERTS.l_item,
	ADRSOURCE.*,
	MDVHIST.*,
	l_type=2,
	SMSHIST.sz_number

from

	BBPROJECT BP, 
	BBPROJECT_X_REFNO XP,
	MDVHIST_ADDRESS_SOURCE_ALERTS ALERTS 
	JOIN SMSHIST ON ALERTS.l_refno=SMSHIST.l_refno AND ALERTS.l_item=SMSHIST.l_item
	JOIN MDVHIST ON ALERTS.l_refno=MDVHIST.l_refno AND ALERTS.l_item=MDVHIST.l_item,
	MDVHIST_ADDRESS_SOURCE ADRSOURCE

where
	BP.l_projectpk=XP.l_projectpk
	and XP.l_refno=ALERTS.l_refno
	and ALERTS.l_alertsourcepk=ADRSOURCE.l_alertsourcepk
	and BP.l_projectpk = 433             
             */

        }

        #endregion
    }
}
