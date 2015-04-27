using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
 
using com.ums.UmsCommon;
 
using System.Configuration;
using System.Data.SqlClient;
using System.Data.Odbc;
using System.Xml.Serialization;



namespace com.ums.UmsCommon 
{
     
    public enum AddressType
    {
        StreetAddress,
        Cadastral_Unit_Norway,
        Cadastral_Unit_Sweden,
        VA_banken_Sweden
    }

    public class AddressFilterInfo
    {
        public int filterId { get; set; }
        public string filterName { get; set; }
        public string deptId { get; set; }
        public string description { get; set; }
        public List<AddressAssociatedWithFilter> addressForFilterlist { get; set; }
        public AddressType addressType { get; set; }
        public FILTEROPERATION FilterOp { get; set; }
        public DateTime lastupdatedDate { get; set; }
        public AddressFilterInfo() { }
        public AddressFilterInfo(string FilterName)
        {
            this.filterName = FilterName;
        }

     
    }
    public enum FILTEROPERATION
    {
        insert,
        update,
        delete,
    }

}
