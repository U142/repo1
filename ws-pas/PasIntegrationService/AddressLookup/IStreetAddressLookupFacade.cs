using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.pas.integration.AddressLookup
{
    /// <summary>
    /// Facade interface for street address lookups.
    /// </summary>
    interface IStreetAddressLookupFacade
    {
        IEnumerable<RecipientData> GetMatchingStreetAddresses(String ConnectionString, List<StreetAddress> StreetAddresses);
        IEnumerable<StreetAddress> GetNoNumbersFoundList();
    }
}
