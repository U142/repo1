using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using umssettings;

namespace pas_cb_server
{
    public class Settings : _settings
    {
        // General (global settings used in the app)
        public static string sz_parsepath;
        public static string sz_dbconn;

        // Instanced settings
        public int l_comppk = 0;
        public int l_deptpk = 0;
        public long l_userpk = 0;

        public string sz_compid = "";
        public string sz_deptid = "";
        public string sz_userid = "";
        public string sz_password = "";

        public Operator[] operators;
     }

    public class Operator
    {
    }

    public class UserValues
    {
    }
}
