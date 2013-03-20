using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.pas.integration.AddressLookup
{
    interface IFollowupLookupFacade
    {
        /// <summary>
        /// To being able to do a resend / followup alert, this method should be used for querying
        /// RecipientData from previous alerts.
        /// </summary>
        /// <param name="ConnectionString"></param>
        /// <param name="FollowupAlerts"></param>
        /// <returns></returns>
        IEnumerable<RecipientData> GetRecipientsFromEarlierAlerts(String ConnectionString, List<FollowupAlertObject> FollowupAlerts);
    }
}
