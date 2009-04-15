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
using System.Collections.Generic;


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


    public class UGisImportParams
    {
        public int COL_MUNICIPAL;
        public int COL_NAMEFILTER1;
        public int COL_NAMEFILTER2;
        public int SKIPLINES;
        public String SEPARATOR;
        public byte [] FILE;
        
    }
    public class UGisImportParamsByStreetId : UGisImportParams
    {
        public int COL_STREETID;
        public int COL_HOUSENO;
        public int COL_LETTER;
    }

    public class UGisImportResultLine
    {
        public String municipalid;
        public String streetid;
        public String houseno;
        public String letter;
        public String namefilter1;
        public String namefilter2;
        public UAddressList list; //list of inhabitants of this address
    }

    /*
     * Collect all the lines from UGisSearchParams.FILE here.
     * All inhabitants under each FILE-line are listed in UGisResultLine.list
     */
    public class UGisImportResultsByStreetId : IAddressResults
    {
        UGisImportResultLine[] list;
        protected List<UGisImportResultLine> temp = new List<UGisImportResultLine>();
        public void addLine(ref UGisImportResultLine a)
        {
            temp.Add(a);
        }
        public void finalize()
        {
            list = temp.Cast<UGisImportResultLine>().ToArray();
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
