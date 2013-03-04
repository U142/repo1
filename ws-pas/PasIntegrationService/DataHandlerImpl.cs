using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;


namespace com.ums.pas.integration
{
    public class DataHandlerImpl : IDataHandler
    {

        #region IDataHandler Members


        HashSet<string> AddedNumbers = new HashSet<string>();
        public int Duplicates { get; private set; }

        protected bool TryAddNumber(String number)
        {
            //TODO clean it first to avoid different spelling on the same number
            if (!AddedNumbers.Contains(number))
            {
                AddedNumbers.Add(number);
            }
            else
            {
                ULog.write("Duplicate number found");
                ++Duplicates;
            }
            return false;
        }


        public void HandleAlert(AlertMqPayload payload)
        {

            foreach (AlertObject alertObject in payload.AlertTargets.OfType<AlertObject>())
            {
                using (new TimeProfiler("AlertObject"))
                {
                    if (TryAddNumber(alertObject.Phone.Address))
                    {

                    }
                }
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
            foreach (OwnerAddress ownerAddress in payload.AlertTargets.OfType<OwnerAddress>())
            {

            }
        }





        #endregion
    }
}
