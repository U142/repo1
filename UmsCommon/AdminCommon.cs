using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace com.ums.UmsCommon 
{
    
    [XmlType(Namespace="http://ums.no/ws/common/admin")]
    public class UPASLOG
    {
        public Int64 l_id;
        public long l_userpk;
        public Int16 l_operation;
        public long l_timestamp;
        public String sz_desc;
    }

    [XmlType(Namespace="http://ums.no/ws/common/admin")]
    public class Response
    {
        // status
        public bool successful;
        public int errorCode;
        public string reason;
    }

    [XmlType(Namespace="http://ums.no/ws/common/admin")]
    public enum ACCESSPAGE
    {
        RESTRICTIONAREA = 1,
        PREDEFINEDTEXT = 2,
    }

    [XmlType(Namespace="http://ums.no/ws/common/admin")]
    public class LBAOPERATOR
    {
        public int l_operator;
        public String sz_operatorname;
    }

}
