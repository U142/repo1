using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.pas.integration.AddressLookup
{
    /// <summary>
    /// Use this to populate info about each recipient.
    /// This is based on how the recipient was found (the target).
    /// </summary>
    public class RecipientData
    {
        public RecipientData()
        {
            AlertLink = new List<RefnoItem>();
        }
        public class RefnoItem
        {
            public long Refno { get; set; }
            public int Item { get; set; }
        }

        public static RefnoItem newRefnoItem(long Refno, int Item)
        {
            return new RefnoItem()
            {
                Refno = Refno,
                Item = Item,
            };
        }

        /// <summary>
        /// how the recipient was found
        /// </summary>
        public AlertTarget AlertTarget { get; set; }

        /// <summary>
        /// Link to alerts where this Recipient is contacted.
        /// Attach to make sure that meta data is written for each recipient
        /// </summary>
        public List<RefnoItem> AlertLink { get; set; }

        /// <summary>
        /// Person/Organization name
        /// </summary>
        public String Name { get; set; }

        /// <summary>
        /// The endpoints that was found.
        /// </summary>
        public List<Endpoint> Endpoints { get; set; }

    }
}
