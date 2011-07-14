using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CookComputing.XmlRpc;

namespace xmlrpc_server_one2many
{
    // Parameter structs
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
        public string pagecontents;
        public int pagelength;
    }
    public struct MESSAGEINFOLIST
    {
        public int updatenr;
        public int messagestatus;
        public string starttime;
        public string endtime;
        public int schedulemethod;
        public PAGELIST pagelist;
    }
    public struct CBECELLLIST
    {
        public int nrofcells;
        public CBECELLID[] cbecellid;
    }
    public struct CBECELLID
    {
        public int ci;
        public int lac;
        public string btsname;
        public int reason;
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

    // Response structs
    public struct CBCLOGINREQRESULT
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        public string messagetext;
    }

    // Methods
    public interface Ione2many
    {
        [XmlRpcMethod("CBCLOGINREQUEST", StructParams = true)]
        CBCLOGINREQRESULT CBCLOGINREQUEST(int cbccberequesthandle, string infoprovname, string cbename, string password /*OPTIONAL*/);
        //[XmlRpcMethod]
        //void CBCNEWMSGREQUEST(int cbccberequesthandle, AREA area, string starttime /*OPTIONAL*/, string endtime /*OPTIONAL*/, int recurrency /*OPTIONAL*/, string recurrencyendtime /*OPTIONAL*/, int repetitioninterval, int schedulemethod, int messageid, int datacodingscheme, PAGELIST pagelist, int btspositionflag, int channelindicator /*OPTIONAL*/, int category /*OPTIONAL*/, int displaymode /*OPTIONAL*/);
        //[XmlRpcMethod]
        //void CBCCHANGEREQUEST(int cbccberequesthandle, int messagehandle, PAGELIST pagelist, string starttime /*OPTIONAL*/, int schedulemethod);
        //[XmlRpcMethod]
        //void CBCINFOCMDREQUEST(int cbccberequesthandle, int cbccbeinforequesthandle);
        //[XmlRpcMethod]
        //void CBCINFOMSGREQUEST(int cbccberequesthandle, int messagehandle);
        //[XmlRpcMethod]
        //void CBCKILLREQUEST(int cbccberequesthandle, int messagehandle, string starttime /*OPTIONAL*/, int schedulemethod);
        //[XmlRpcMethod]
        //void CBCPREDEFAREAREQUEST(int cbccberequesthandle, AREA area, int btspositionflag);
        //[XmlRpcMethod]
        //void CBCREMOVEPREDEFAREAREQUEST(int cbccberequesthandle, int userareaid);
        //[XmlRpcMethod]
        //void CBCNEWMSGPREDEFAREAREQUEST(int cbccberequesthandle, AREAIDLIST areaidlist, string starttime /*OPTIONAL*/, string endtime /*OPTIONAL*/, int recurrency /*OPTIONAL*/, string recurrencyendtime /*OPTIONAL*/, int repetitioninterval, int schedulemethod, int messageid, int datacodingscheme, PAGELIST pagelist, int channelindicator /*OPTIONAL*/, int category /*OPTIONAL*/, int displaymode /*OPTIONAL*/);
        //[XmlRpcMethod]
        //void CBCNEWMSGPLMNREQUEST(int cbccberequesthandle, string starttime /*OPTIONAL*/, string endtime /*OPTIONAL*/, int recurrency /*OPTIONAL*/, string recurrencyendtime /*OPTIONAL*/, int repetitioninterval, int schedulemethod, int messageid, int datacodingscheme, PAGELIST pagelist, int channelindicator /*OPTIONAL*/, int category /*OPTIONAL*/, int displaymode /*OPTIONAL*/);
        //[XmlRpcMethod]
        //void CBCCHANGEPASSWORDREQUEST(int cbccberequesthandle, string oldpassword, string newpassword);
        //[XmlRpcMethod]
        //void CBCNEWMSGCELLREQUEST(int cbccberequesthandle, CBECELLLIST cbecelllist, string starttime /*OPTIONAL*/, string endtime /*OPTIONAL*/, int recurrency /*OPTIONAL*/, string recurrencyendtime /*OPTIONAL*/, int repetitioninterval, int schedulemethod, int messageid, int datacodingscheme, PAGELIST pagelist, int channelindicator /*OPTIONAL*/, int category /*OPTIONAL*/, int displaymode /*OPTIONAL*/);
        //[XmlRpcMethod]
        //void CBCNEWMSGCCREQUEST(int cbccberequesthandle, CBECCLIST cbecclist, string starttime /*OPTIONAL*/, string endtime /*OPTIONAL*/, int recurrency /*OPTIONAL*/, string recurrencyendtime /*OPTIONAL*/, int repetitioninterval, int schedulemethod, int messageid, int datacodingscheme, PAGELIST pagelist, int channelindicator /*OPTIONAL*/, int category /*OPTIONAL*/, int displaymode /*OPTIONAL*/, int acceptunknowncc /*OPTIONAL*/);
        //[XmlRpcMethod]
        //void CBCINFOAREASREQUEST(int cbccberequesthandle, int cbccbeinfoareastype /*OPTIONAL*/, int cbccbeinfoareascoordinates /*OPTIONAL*/);
        //[XmlRpcMethod]
        //void CBCNETWORKAVAILABILITYREQUEST(int cbccberequesthandle);
        //[XmlRpcMethod]
        //void CBCMSGNETWORKCELLCOUNTREQUEST(int cbccberequesthandle, int messagehandle);
    }
}
