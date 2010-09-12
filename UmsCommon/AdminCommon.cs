using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.UmsCommon 
{
    public class UPASLOG
    {
        public Int64 l_id;
        public long l_userpk;
        public Int16 l_operation;
        public long l_timestamp;
        public String sz_desc;
    }

    public class Response
    {
        // status
        public bool successful;
        public int errorCode;
        public string reason;
    }

    public enum ACCESSPAGE
    {
        RESTRICTIONAREA = 1,
        PREDEFINEDTEXT = 2,
    }

    public class LBAOPERATOR
    {
        public int l_operator;
        public String sz_operatorname;
    }

}
