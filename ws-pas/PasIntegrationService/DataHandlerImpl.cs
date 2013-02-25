using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsDbLib;


namespace com.ums.pas.integration
{
    public class DataHandlerImpl : IDataHandler
    {

        #region IDataHandler Members



        public void HandleAlert(AlertMqPayload payload)
        {
            /**
             * get all targets of each type
             *  
             */

            foreach (Recipient recipient in payload.AlertTargets.OfType<Recipient>())
            {

            }
            foreach (StoredList storedList in payload.AlertTargets.OfType<StoredList>())
            {
            }
            foreach (StoredAddress storedAddress in payload.AlertTargets.OfType<StoredAddress>())
            {
            }
            foreach (StreetAddress streetAddress in payload.AlertTargets.OfType<StreetAddress>())
            {
            }
            foreach (PropertyAddress propertyAddress in payload.AlertTargets.OfType<PropertyAddress>())
            {
            }

        }



        #endregion
    }
}
