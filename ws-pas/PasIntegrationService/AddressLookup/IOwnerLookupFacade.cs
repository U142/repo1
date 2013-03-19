using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.pas.integration.AddressLookup
{
    public interface IOwnerLookupFacade
    {
        /// <summary>
        /// Get a list of owners
        /// </summary>
        /// <param name="ConnectionString"></param>
        /// <param name="ownerAddresses"></param>
        /// <returns></returns>
        IEnumerable<RecipientData> GetMatchingOwnerAddresses(String ConnectionString, List<OwnerAddress> ownerAddresses);


        /// <summary>
        /// After GetMatchingoOwnerAddresses is executed, retrieve a list where there were no match.
        /// </summary>
        /// <returns></returns>
        IEnumerable<OwnerAddress> GetNoMatchList();
    }
}
