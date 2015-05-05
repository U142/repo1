using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Xml.Serialization;

namespace com.ums.UmsCommon 
{
   
    public class AddressAssociatedWithFilter
    {
        public int filterId { get; set; }
        public int municipalId { get; set; }
        public int streetId { get; set; }
        public int houseNo { get; set; }
        public string szHouseLetter { get; set; }
        public string szApartmentId { get; set; }
        public int gnrNumber { get; set; }
        public int bnrNumber { get; set; }
        public int fnrNumber { get; set; }
        public int snrNumber { get; set; }
        public int unrNumber { get; set; }
        public int seCadId { get; set; }
        public int seVaId { get; set; }
        


       

    }
}