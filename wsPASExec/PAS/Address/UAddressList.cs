using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Collections;
using System.Xml.Linq;
using com.ums.PAS.Maps;


namespace com.ums.PAS.Address
{
    /*
     * used as in parameter for searching in local address databases
     */
    public class UMapAddressParams : UMapInfo
    {
        public UMapAddressParams()
            : base()
        {

        }
    }

    public class UMapDistanceParams
    {
        public double lon;
        public double lat;
        public double distance;
        public UMapDistanceParams()
            : base()
        {
        }
    }

    public class UMapAddressParamsByQuality : UMapAddressParams
    {
        public String sz_postno;
        public String sz_xycodes;
        public UMapAddressParamsByQuality(String sz_postno, String sz_xycodes) 
            : base()
        {
            this.sz_postno = sz_postno;
            this.sz_xycodes = sz_xycodes;
        }
    }

    /*collection of addresses*/
    public class UAddressList : IAddressResults
    {
        protected ArrayList temp = new ArrayList();
        public UAddress [] list;
        public void addLine(ref UAddress adr)
        {
            temp.Add(adr);
        }
        public void finalize()
        {
            list = temp.Cast<UAddress>().ToArray();
        }
    }


    /*single address*/
    public class UAddress
    {
        public String kondmid;
        public String name;
        public String address;
        public int houseno;
        public String letter;
        public String postno;
        public String postarea;
        public int region;
        public String bday;
        public String number;
        public String mobile;
        public double lon;
        public double lat;
        public int gno;
        public int bno;
        public int bedrift;
        public Int64 importid;
        public int streetid;
        public String xycode;
        public int hasfixed;
        public int hasmobile;

        public UAddress()
        {

        }
    }



}
