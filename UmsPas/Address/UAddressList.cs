using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Collections;
using System.Xml.Linq;
using com.ums.PAS.Maps;
using System.Collections.Generic;


namespace com.ums.PAS.Address
{
    /*
     * Used to estimate count of recipients before sending
     */
    public class UAdrCount
    {
        public int n_private_fixed = 0; //total private fixed addresses
        public int n_company_fixed = 0; //total company fixed addresses
        public int n_private_mobile = 0;
        public int n_company_mobile = 0;
        public int n_private_sms = 0;
        public int n_company_sms = 0;
        public int n_total_recipients = 0; //fixed+mobile+sms - duplicates
        public int n_private_nonumber = 0;
        public int n_company_nonumber = 0;
        public int n_private_fax = 0;
        public int n_company_fax = 0;
        public int n_duplicates = 0;
        
    }

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
        //public String sz_xycodes;
        public List<string> arr_xycodes = new List<string>();
        public UMapAddressParamsByQuality(String sz_postno, ref List<string> sz_xycodes) 
            : base()
        {
            this.sz_postno = sz_postno;
            this.arr_xycodes = sz_xycodes;
        }
        public UMapAddressParamsByQuality()
        {
            this.sz_postno = "0";
            
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
        public int DETAIL_THRESHOLD_LINES;
    }

    public class UGisImportResultLine
    {
        public bool isValid()
        {
            return b_isvalid;
        }
        public bool finalize()
        {
            b_isvalid = true;
            try
            {
                Int64 n_tmp = 0;
                n_tmp = Int64.Parse(municipalid);
                if (n_tmp <= 0)
                    b_isvalid = false;
                n_tmp = Int64.Parse(streetid);
                if (n_tmp <= 0)
                    b_isvalid = false;
                n_tmp = Int64.Parse(houseno);
                if (n_tmp <= 0)
                    b_isvalid = false;
            }
            catch (Exception)
            {
                b_isvalid = false;
            }
            return b_isvalid;
        }
        public String municipalid;
        public String streetid;
        public String houseno;
        public String letter;
        public String namefilter1;
        public String namefilter2;
        public int n_linenumber;
        public bool b_isvalid;
        public UAddressList list = new UAddressList(); //list of inhabitants of this address
    }

    /*
     * Collect all the lines from UGisSearchParams.FILE here.
     * All inhabitants under each FILE-line are listed in UGisResultLine.list
     */
    public class UGisImportResultsByStreetId : IAddressResults
    {
        //public UGisImportResultLine[] list;
        public List<UGisImportResultLine> list = new List<UGisImportResultLine>();
        public void addLine(ref UGisImportResultLine a)
        {
            list.Add(a);
        }
        public void finalize()
        {
        }
        /*public void finalize()
        {
            list = temp.Cast<UGisImportResultLine>().ToArray();
        }*/
    }

    /*collection of addresses*/
    public class UAddressList : IAddressResults
    {
        //protected ArrayList temp = new ArrayList();
        //public UAddress [] list;
        public List<UAddressBasics> list_basics = new List<UAddressBasics>();
        public List<UAddress> list = new List<UAddress>();
        public void addLine(ref UAddress adr)
        {
            list.Add(adr);
        }
        public void addLine(ref UAddressBasics adr)
        {
            list_basics.Add(adr);
        }
        public void finalize()
        {
            //list = temp.Cast<UAddress>().ToArray();
        }
    }

    /*address basics*/
    public class UAddressBasics
    {
        public UAddressBasics()
        {
            arrayindex = -1;
        }
        public String kondmid;
        public double lon;
        public double lat;
        public int hasfixed;
        public int hasmobile;
        public int bedrift;
        public int arrayindex; //used in Gemini import to reference a UAddress to a files linenumber
    }

    /*single address*/
    public class UAddress : UAddressBasics
    {
        //public String kondmid;
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
        //public double lon;
        //public double lat;
        public int gno;
        public int bno;
        //public int bedrift;
        public Int64 importid;
        public int streetid;
        public String xycode;
        //public int hasfixed;
        //public int hasmobile;
        public String municipalid;
        //public int arrayindex; //used in Gemini import to reference a UAddress to a files linenumber

        public UAddress()
            : base()
        {
        }
    }

    /*used in adress count*/
    public class UAdrcountCandidate
    {
        public UAdrcountCandidate()
        {
            add = 1;
        }
        public double lon;
        public double lat;
        public int bedrift;
        public bool hasfixed;
        public bool hasmobile;
        public int add;
    }



}
