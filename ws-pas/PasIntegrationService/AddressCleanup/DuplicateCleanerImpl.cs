using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.pas.integration.AddressLookup;
using log4net;

namespace com.ums.pas.integration.AddressCleanup
{
    public class DuplicateCleanerImpl : IDuplicateCleaner  
    {
        private static ILog log = LogManager.GetLogger(typeof(DuplicateCleanerImpl));

        public List<RecipientData> DuplicateCleanup(List<RecipientData> ListOfRecipients, Action<RecipientData, Endpoint> Callback)
        {
            List<RecipientData> workingSet = new List<RecipientData>();
            workingSet.AddRange(ListOfRecipients);

            //remove duplicates
            HashSet<Endpoint> endpointsAdded = new HashSet<Endpoint>();
            List<RecipientData> recipientsToRemove = new List<RecipientData>();
            for (int index = workingSet.Count - 1; index >= 0; index--)
            {
                RecipientData recipientData = workingSet[index];
                List<Endpoint> endpointsToRemove = new List<Endpoint>();
                bool endpointsDeducted = false;
                for (int epIndex = recipientData.Endpoints.Count - 1; epIndex >= 0; epIndex--)
                {
                    if (!endpointsAdded.Add(recipientData.Endpoints[epIndex]))
                    {
                        log.DebugFormat("Duplicate endpoint detected [{0}], removing from endpoints", recipientData.Endpoints[epIndex].Address);
                        endpointsToRemove.Add(recipientData.Endpoints[epIndex]);
                        endpointsDeducted = true;
                        if (Callback != null)
                        {
                            Callback(recipientData, recipientData.Endpoints[epIndex]);
                        }
                    }
                }
                //Add removed endpoint to duplicates list
                endpointsToRemove.ForEach(e =>
                    {
                        recipientData.Duplicates.Add(e);
                        recipientData.Endpoints.Remove(e);
                    });
                /*
                //If no endpoints because all were deducted, then.
                if (recipientData.Endpoints.Count == 0 && endpointsDeducted)
                {
                    //log.DebugFormat("Recipient [{0}] have no more endpoints, removing from list", recipientData.Name);
                    //recipientsToRemove.Add(recipientData);
                }*/
            }
            //recipientsToRemove.ForEach(r => workingSet.Remove(r));
            return workingSet;
        }
    }
}
