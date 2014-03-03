using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Collections;
using System.Xml.Linq;
using com.ums.PAS.Address;
using com.ums.UmsParm;


namespace com.ums.PAS.Address.gab
{
    public enum GABTYPE
    {
        House,
        Post,
        Street,
        Region,
    };

    public enum ADRSEARCHRESULT
    {
        HOUSENO_EXIST,
        HOUSENO_NOT_FOUND,
        STREET_NOT_FOUND,
        UNKOWN,
        EXCEPTION
    }


    /* search results from Ugland 
     The result may be of types
     * list of Postnumbers, Streets, regions or houses
     */
    public class UGabResult : IAddressResults
    {
        public float match;
        public string name;
        public string region;
        public string postno;
        public double lon, lat;
        public GABTYPE type;
        public UBoundingRect rect;
        public ADRSEARCHRESULT Result;
        public int scope;
        public int municipalid;
        public int streetid;
        public void finalize()
        {
        }
        public String toString()
        {
            return name;
        }
    }

    public class UGabResultFromPoint : UGabResult
    {
        public string no;
        public int distance;

    }

    /*
     * used as output for searching in remote GAB databases
     */
    public class UGabSearchResultList : IAddressResults
    {
        protected ArrayList temp = new ArrayList();
        public UGabResult[] list;
        public string sz_errortext;
        public string sz_exceptiontext;
        public bool b_haserror = false;
        public void setError(string text, string exception)
        {
            sz_errortext = exception;
            sz_exceptiontext = text;
            b_haserror = true;
        }

        public UGabSearchResultList()
            : base()
        {

        }
        public void addLine(ref UGabResult adr)
        {
            temp.Add(adr);
        }
        public void finalize()
        {
            list = temp.Cast<UGabResult>().ToArray();
        }
    }



}