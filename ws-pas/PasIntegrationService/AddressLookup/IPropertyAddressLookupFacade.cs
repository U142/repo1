using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.pas.integration.AddressLookup
{
    public interface IPropertyAddressLookupFacade
    {
        /// <summary>
        /// Get recipient data based on property addresses.
        /// </summary>
        /// <param name="connectionString"></param>
        /// <param name="propertyAddresses"></param>
        /// <returns></returns>
        IEnumerable<RecipientData> GetMatchingPropertyAddresses(String ConnectionString, List<PropertyAddress> PropertyAddresses, SourceRegister sourceRegister);
        IEnumerable<PropertyAddress> GetNoNumbersFoundList();
    }
}
