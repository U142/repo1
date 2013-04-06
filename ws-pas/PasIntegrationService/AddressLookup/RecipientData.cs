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

        public String AddressLine {
            get
            {
                return String.Format("{0}|{1}|{2}|{3}", Name, Address, Postno, PostPlace);
            }
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


        private List<Endpoint> _endpoints = new List<Endpoint>();

        /// <summary>
        /// The endpoints that was found.
        /// </summary>
        public List<Endpoint> Endpoints
        {
            get { return _endpoints != null ? _endpoints : new List<Endpoint>(); }
            set { _endpoints = value; }
        }

        private List<Endpoint> _duplicates = new List<Endpoint>();

        /// <summary>
        /// Endpoints that are omitted
        /// </summary>
        public List<Endpoint> Duplicates
        {
            get { return _duplicates; }
            set { _duplicates = value; }
        }


        /// <summary>
        /// Longitude, if available
        /// </summary>
        private double _lon;

        public double Lon
        {
            get { return _lon; }
            set { _lon = value; }
        }


        /// <summary>
        /// Latitude, if available
        /// </summary>
        private double _lat;

        public double Lat
        {
            get { return _lat; }
            set { _lat = value; }
        }


        /// <summary>
        /// true if company
        /// </summary>
        public bool Company { get; set; }

        public String Address { get; set; }

        public int Postno { get; set; }

        public String PostPlace { get; set; }

        /// <summary>
        /// hold kondmid
        /// </summary>
        public int KonDmid { get; set; }

        public bool NoRecipients { get; set; }
    }
}
