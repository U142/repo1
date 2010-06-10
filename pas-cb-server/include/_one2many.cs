namespace pas_cb_server.one2many {

    using CookComputing.XmlRpc;

    public partial class CBCCHANGEPASSWORDREQUEST 
    {
        public int cbccberequesthandle;
        public string oldpassword;
        public string newpassword;
    }
    
    public partial class CBCMSGNETWORKCELLCOUNTREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbecbcstatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        public int cellcount2gtotal;
        public int cellcount2gsuccess;
        public int cellcount3gtotal;
        public int cellcount3gsuccess;
    }
    
    public partial class CBEFAILEDCCLIST 
    {
        public int nrofcc;
        public CBECCID[] CBECCID;
    }
    
    public partial class CBECCID 
    {
        public string ccname;
    }
    
    public partial class CBCNEWMSGCCREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? messagehandle;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public CBEFAILEDCCLIST cbefailedcclist;
    }
    
    public partial class CBCNETWORKAVAILABILITYREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbecbcstatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        public int networkavailability;
    }
    
    public partial class AREAINFO 
    {
        public int id;
        public int type;
        public string name;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public AREADATA areadata;
    }
    
    public partial class AREADATA 
    {
        public int coordinatesystem;
        public int nrofcoordinatepair;
        public COORDINATEPAIR[] coordinatepair;
    }
    
    public partial class COORDINATEPAIR 
    {
        public double xcoordinate;
        public double ycoordinate;
    }
    
    public partial class AREAINFOLIST 
    {
        public int nrofareas;
        public AREAINFO[] areainfo;
    }
    
    public partial class CBCINFOAREASREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbecbcstatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        public AREAINFOLIST areainfolist;
    }
    
    public partial class CBEFAILEDCELLLIST 
    {
        public int nrofcells;
        public CBECELLID[] cbecellid;
    }

    [XmlRpcMissingMapping(MappingAction.Ignore)]
    public partial class CBECELLID 
    {
        public int? ci;
        public int? lac;
        public string btsname;
        public int? reason;
    }
    
    public partial class CBCNEWMSGCELLREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? messagehandle;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public CBEFAILEDCELLLIST cbefailedcelllist;
    }
    
    public partial class CBCCHANGEPASSWORDREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
    }
    
    public partial class CBCNEWMSGPREDEFAREAREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? messagehandle;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public FAILEDAREALIST failedarealist;
    }
    
    public partial class FAILEDAREALIST 
    {
        public int nrofareaid;
        public string userareaid;
    }
    
    public partial class CBCREMOVEPREDEFAREAREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
    }
    
    public partial class CBCPREDEFAREAREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? areaid;
    }
    
    public partial class CBCKILLREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public FAILEDAREALIST failedarealist;
    }
    
    public partial class INTERVALINFO 
    {
        public int updatenr;
        public int messagestatus;
        public string starttime;
        public string endtime;
        public int schedulemethod;
        public int immediatedisplay;
        public PAGELISTDATA pagelist;
    }

    public partial class PAGELISTDATA
    {
        public int nrofpages;
        public PAGEDATA[] page;
    }

    public partial class PAGEDATA 
    {
        public byte[] pagecontents;
        public int pagelength;
    }

    [XmlRpcMissingMapping(MappingAction.Ignore)]
    public partial class MESSAGEINFOLIST 
    {
        public int nrofintervals;
        public INTERVALINFO[] intervalinfo;
    }
    
    public partial class CBCINFOMSGREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public AREADATA area;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public AREAIDLIST areaidlist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public CBECELLLIST cbecelllist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? recurrency;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string recurrencyendtime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? repetitioninterval;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? messageid;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? datacodingscheme;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? btspositionflag;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? category;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? displaymode;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? channelindicator;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public double? successpercentage;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public MESSAGEINFOLIST messageinfolist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public CBECCLIST cbecclist;
    }
    
    public partial class AREAIDLIST 
    {
        public int nrofareaid;
        public string userareaid;
    }

    public partial class CBECELLLIST
    {
        public int nrofcells;
        public CBECELLID[] cbecellid;
    }

    public partial class CBECCLIST 
    {
        public int nrofcc;
        public CBECCID[] cbeccid;
    }
    
    public partial class CBCINFOCMDREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? userareaid;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? messagehandle;
    }
    
    public partial class CBCCHANGEREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public FAILEDAREALIST failedarealist;
    }
    
    public partial class CBCNEWMSGREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? messagehandle;
    }

    public partial class CBCLOGINREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        public string messagetext;
    }
    
    public partial class CBCMSGNETWORKCELLCOUNTREQUEST 
    {
        public int cbccberequesthandle;
        public int messagehandle;
    }
    
    public partial class CBCNEWMSGCCREQUEST 
    {
        public int cbccberequesthandle;
        public CBECCLIST cbecclist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string starttime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string endtime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? recurrency;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string recurrencyendtime;
        public int repetitioninterval;
        public int schedulemethod;
        public int messageid;
        public int datacodingscheme;
        public PAGELISTDATA pagelist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? channelindicator;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? category;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? displaymode;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? acceptunknowncc;
    }
    
    public partial class CBCNETWORKAVAILABILITYREQUEST 
    {
        public int cbccberequesthandle;
    }
    
    public partial class CBCINFOAREASREQUEST 
    {
        public int cbccberequesthandle;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? type;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? coordinates;
    }
    
    public partial class CBCNEWMSGCELLREQUEST 
    {
        public int cbccberequesthandle;
        public CBECELLLIST cbecelllist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string starttime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string endtime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? recurrency;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string recurrencyendtime;
        public int repetitioninterval;
        public int schedulemethod;
        public int messageid;
        public int datacodingscheme;
        public PAGELISTDATA pagelist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? channelindicator;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? category;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? displaymode;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? acceptunknowncells;
    }
    
    public partial class CBCNEWMSGPREDEFAREAREQUEST 
    {
        public int cbccberequesthandle;
        public AREAIDLIST areaidlist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string starttime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string endtime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? recurrency;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string recurrencyendtime;
        public int repetitioninterval;
        public int schedulemethod;
        public int messageid;
        public int datacodingscheme;
        public PAGELISTDATA pagelist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? channelindicator;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? category;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? displaymode;
    }
    
    public partial class CBCREMOVEPREDEFAREAREQUEST 
    {
        public int cbccberequesthandle;
        public int userareaid;
    }
    
    public partial class CBCPREDEFAREAREQUEST 
    {
        public int cbccberequesthandle;
        public AREADATA area;
        public int btspositionflag;
    }
    
    public partial class CBCKILLREQUEST 
    {
        public int cbccberequesthandle;
        public int messagehandle;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string starttime;
        public int schedulemethod;
    }
    
    public partial class CBCINFOMSGREQUEST 
    {
        public int cbccberequesthandle;
        public int messagehandle;
    }
    
    public partial class CBCINFOCMDREQUEST 
    {
        public int cbccberequesthandle;
        public int cbccbeinforequesthandle;
    }
    
    public partial class CBCCHANGEREQUEST 
    {
        public int cbccberequesthandle;
        public int messagehandle;
        public PAGELISTDATA pagelist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string starttime;
        public int schedulemethod;
    }
    
    public partial class CBCNEWMSGREQUEST 
    {
        public int cbccberequesthandle;
        public AREADATA area;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string starttime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string endtime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? recurrency;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string recurrencyendtime;
        public int repetitioninterval;
        public int schedulemethod;
        public int messageid;
        public int datacodingscheme;
        public PAGELISTDATA pagelist;
        public int btspositionflag;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? channelindicator;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? category;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? displaymode;
    }
    
    public partial class CBCLOGINREQUEST 
    {
        public int cbccberequesthandle;
        public string infoprovname;
        public string cbename;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string password;
    }
    
    public partial class CBCNEWMSGPLMNREQUEST 
    {
        public int cbccberequesthandle;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string starttime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string endtime;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? recurrency;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string recurrencyendtime;
        public int repetitioninterval;
        public int schedulemethod;
        public int messageid;
        public int datacodingscheme;
        public PAGELISTDATA pagelist;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? channelindicator;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? category;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int? displaymode;
    }
    
    public partial class CBCNEWMSGPLMNREQRESULT 
    {
        public int cbccberequesthandle;
        public int cbccbestatuscode;
        public int messageclass;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public string messagetext;
        [XmlRpcMissingMapping(MappingAction.Ignore)]
        public int messagehandle;
    }
}
