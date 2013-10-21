using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.pas.integration.AddressLookup;

namespace com.ums.pas.integration.AddressCleanup
{
    public interface IDuplicateCleaner
    {
        /// <summary>
        /// Method for cleaning endpoint duplicates.
        /// The method should take advantage of endpoint equals comparison so that implemented endpoints will decide what to be equal.
        /// </summary>
        /// <param name="ListOfRecipients">List of recipients with contact endpoints</param>
        /// <returns>The cleaned list</returns>
        List<RecipientData> DuplicateCleanup(List<RecipientData> ListOfRecipients, Action<RecipientData, Endpoint> Callback);
    }
}
