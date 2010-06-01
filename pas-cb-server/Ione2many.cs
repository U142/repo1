using System;
using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Remoting.Channels;
using System.Runtime.Remoting.Channels.Http;
using CookComputing.XmlRpc;

namespace pas_cb_server
{
    #region Parameter structs
    public struct AREA
    {
        public COORDINATEPAIR[] coordinatepair;
        public int coordinatesystem;
        public int nrofcoordinatepair;
    }
    public struct COORDINATEPAIR
    {
        public double xcoordinate;
        public double ycoordinate;
    }
    public struct AREAIDLIST
    {
        public int nrofareaid;
        public int[] userareaid;
    }
    public struct PAGELIST
    {
        public int nrofpages;
        public PAGE[] page;
    }
    public struct PAGE
    {
        public int pagelength;
        public byte[] pagecontents;
    }
    [XmlRpcMissingMapping(MappingAction.Ignore)]
    public struct MESSAGEINFOLIST
    {
        public int nrofintervals;
        public INTERVALINFO[] intervalinfo;
    }
    [XmlRpcMissingMapping(MappingAction.Ignore)]
    public struct INTERVALINFO
    {
        public int? updatenr;
        public int? messagestatus;
        public string starttime;
        public string endtime;
        public int? schedulemethod;
        public int? immediatedisplay;
        public PAGELIST pagelist;
    }
    public struct CBECELLLIST
    {
        public int nrofcells;
        public CBECELLID[] cbecellid;
    }
    [XmlRpcMissingMapping(MappingAction.Ignore)]
    public struct CBECELLID
    {
        public int? ci;
        public int? lac;
        public string btsname;
        public int? reason;
    }
    public struct CBECCLIST
    {
        public int nrofccs;
        public CBECCID[] cbeccid;
    }
    public struct CBECCID
    {
        public string ccname;
    }
    #endregion

    #region Request structs
    public struct CBCNEWMSGCELLREQUEST
    {
        public int cbccberequesthandle;
        public CBECELLLIST cbecelllist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string starttime; /*OPTIONAL*/
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string endtime; /*OPTIONAL*/
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? recurrency; /*OPTIONAL*/
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string recurrencyendtime; /*OPTIONAL*/
        public int repetitioninterval;
        public int schedulemethod;
        public int messageid;
        public int datacodingscheme;
        public PAGELIST pagelist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? channelindicator; /*OPTIONAL*/
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? category; /*OPTIONAL*/
        public int displaymode;
        public int acceptunknowncells;
    }
    #endregion

    #region Response structs
    public struct CBCLOGINREQRESULT
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        public string messagetext;
    }
    public struct CBCNEWMSGREQRESULT
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        public string messagetext;
        public int messagehandle;
    }
    public struct CBCCHANGEREQRESULT
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        public string messagetext;
        public AREAIDLIST failedarealist;
    }
    public struct CBCCHANGEPASSWORDREQRESULT
    { 
        public int cbccberequesthandle; 
        public int cbccbestatuscode; 
        public int messageclass;
        public string messagetext;
    }
    public struct CBCNETWORKAVAILABILITYREQRESULT
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        public string messagetext;
        public int networkavailability;
    }
    [XmlRpcMissingMapping(MappingAction.Ignore)]
    public struct CBCNEWMSGCELLREQRESULT
    {
        public int? cbccberequesthandle;
        public int? cbccbestatuscode;
        public int? messageclass;
        public string messagetext;
        public int? messagehandle;
        public CBECELLLIST cbefailedcelllist;
    }
    [XmlRpcMissingMapping(MappingAction.Ignore)]
    public struct CBCINFOMSGREQRESULT 
    { 
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        public string messagetext;
        public AREA area;
        public AREAIDLIST areaidlist;
        public CBECELLLIST cbecelllist;
        public int? recurrency;
        public string recurrencyendtime;
        public int? repetitioninterval;
        public int? messageid;
        public int? datacodingscheme;
        public int? btspositionflag;
        public int? category;
        public int? displaymode;
        public int? channelindicator;
        public double? successpercentage;
        public MESSAGEINFOLIST messageinfolist;
    }
    #endregion

    // XML RPC Methods
    public interface Ione2many : IXmlRpcProxy
    {
        [XmlRpcMethod(StructParams = true)]
        CBCLOGINREQRESULT CBCLOGINREQUEST(int cbccberequesthandle, string infoprovname, string cbename, string password /*OPTIONAL*/);
        
        [XmlRpcMethod(StructParams = true)]
        CBCNEWMSGREQRESULT CBCNEWMSGREQUEST(int cbccberequesthandle, AREA area, string starttime /*OPTIONAL*/, string endtime /*OPTIONAL*/, int? recurrency /*OPTIONAL*/, string recurrencyendtime /*OPTIONAL*/, int repetitioninterval, int schedulemethod, int messageid, int datacodingscheme, PAGELIST pagelist, int btspositionflag, int? channelindicator /*OPTIONAL*/, int? category /*OPTIONAL*/, int? displaymode /*OPTIONAL*/);
        
        [XmlRpcMethod(StructParams = true)]
        CBCCHANGEREQRESULT CBCCHANGEREQUEST(int cbccberequesthandle, int messagehandle, PAGELIST pagelist, string starttime /*OPTIONAL*/, int schedulemethod);
        
        //[XmlRpcMethod(StructParams = true)]
        //void CBCINFOCMDREQUEST(int cbccberequesthandle, int cbccbeinforequesthandle);

        [XmlRpcMethod(StructParams = true)]
        CBCINFOMSGREQRESULT CBCINFOMSGREQUEST(int cbccberequesthandle, int messagehandle);

        //[XmlRpcMethod(StructParams = true)]
        //void CBCKILLREQUEST(int cbccberequesthandle, int messagehandle, string starttime /*OPTIONAL*/, int schedulemethod);

        //[XmlRpcMethod(StructParams = true)]
        //void CBCPREDEFAREAREQUEST(int cbccberequesthandle, AREA area, int btspositionflag);

        //[XmlRpcMethod(StructParams = true)]
        //void CBCREMOVEPREDEFAREAREQUEST(int cbccberequesthandle, int userareaid);

        //[XmlRpcMethod(StructParams = true)]
        //void CBCNEWMSGPREDEFAREAREQUEST(int cbccberequesthandle, AREAIDLIST areaidlist, string starttime /*OPTIONAL*/, string endtime /*OPTIONAL*/, int? recurrency /*OPTIONAL*/, string recurrencyendtime /*OPTIONAL*/, int repetitioninterval, int schedulemethod, int messageid, int datacodingscheme, PAGELIST pagelist, int? channelindicator /*OPTIONAL*/, int? category /*OPTIONAL*/, int? displaymode /*OPTIONAL*/);
        
        [XmlRpcMethod(StructParams = true)]
        CBCCHANGEPASSWORDREQRESULT CBCCHANGEPASSWORDREQUEST(int cbccberequesthandle, string oldpassword, string newpassword);

        [XmlRpcMethod]
        CBCNEWMSGCELLREQRESULT CBCNEWMSGCELLREQUEST(CBCNEWMSGCELLREQUEST p);

        //[XmlRpcMethod(StructParams = true)]
        //CBCNEWMSGCELLREQRESULT CBCNEWMSGCELLREQUEST(int cbccberequesthandle, CBECELLLIST cbecelllist, string starttime /*OPTIONAL*/, string endtime /*OPTIONAL*/, int? recurrency /*OPTIONAL*/, string recurrencyendtime /*OPTIONAL*/, int repetitioninterval, int schedulemethod, int messageid, int datacodingscheme, PAGELIST pagelist, int? channelindicator /*OPTIONAL*/, int? category /*OPTIONAL*/, int displaymode, int acceptunknowncells);

        //[XmlRpcMethod(StructParams = true)]
        //void CBCNEWMSGCCREQUEST(int cbccberequesthandle, CBECCLIST cbecclist, string starttime /*OPTIONAL*/, string endtime /*OPTIONAL*/, int? recurrency /*OPTIONAL*/, string recurrencyendtime /*OPTIONAL*/, int repetitioninterval, int schedulemethod, int messageid, int datacodingscheme, PAGELIST pagelist, int? channelindicator /*OPTIONAL*/, int? category /*OPTIONAL*/, int? displaymode /*OPTIONAL*/, int? acceptunknowncc /*OPTIONAL*/);

        //[XmlRpcMethod(StructParams = true)]
        //void CBCNEWMSGPLMNREQUEST(int cbccberequesthandle, string starttime /*OPTIONAL*/, string endtime /*OPTIONAL*/, int? recurrency /*OPTIONAL*/, string recurrencyendtime /*OPTIONAL*/, int repetitioninterval, int schedulemethod, int messageid, int datacodingscheme, PAGELIST pagelist, int? channelindicator /*OPTIONAL*/, int? category /*OPTIONAL*/, int? displaymode /*OPTIONAL*/);

        //[XmlRpcMethod(StructParams = true)]
        //void CBCINFOAREASREQUEST(int cbccberequesthandle, int? cbccbeinfoareastype /*OPTIONAL*/, int? cbccbeinfoareascoordinates /*OPTIONAL*/);

        [XmlRpcMethod(StructParams = true)]
        CBCNETWORKAVAILABILITYREQRESULT CBCNETWORKAVAILABILITYREQUEST(int cbccberequesthandle);

        //[XmlRpcMethod(StructParams = true)]
        //void CBCMSGNETWORKCELLCOUNTREQUEST(int cbccberequesthandle, int messagehandle);
        
    }

    // CB Methods
    public class CB_one2many
    {
        public static int CreateAlert() 
        {
            return Constant.OK; 
        }
        public static int UpdateAlert() 
        {
            return Constant.OK; 
        }
        public static int KillAlert() 
        {
            return Constant.OK; 
        }
        public static int GetAlertStatus()
        { 
            return Constant.OK; 
        }
    }
}
