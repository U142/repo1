using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.pas.integration.AddressLookup
{
    public interface IOwnerLookupFacade
    {
        IEnumerable<RecipientData> GetMatchingOwnerAddresses(String connectionString, List<OwnerAddress> ownerAddresses);
    }
}
