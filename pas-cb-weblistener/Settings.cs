using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using umssettings;

namespace pas_cb_weblistener
{
    public class Settings : _settings
    {
        public static int l_operator;
        public static string sz_dbconn;

        public static void init()
        {
            l_operator = Settings.GetInt("Operator");

            Settings.sz_dbconn = String.Format("DSN={0};UID={1};PWD={2};",
                Settings.GetString("DSN"),
                Settings.GetString("UID"),
                Settings.GetString("PWD"));
        }
    }
    public class Constant
    {
        // main return values for ParseXMLFile
        public const int OK = 0;
        public const int RETRY = -1;
        public const int FAILED = -2;

        // mainstatuses
        // common
        public const int QUEUE = 199;
        public const int PARSING = 200;
        public const int FAILEDRETRY = 290;
        // Alert
        public const int PREPARING = 300;
        public const int PROCESSINGSUB = 305;
        public const int PREPAREDNOCC = 310;
        public const int PREPARED = 311;
        public const int USERCONFIRMED = 320;
        public const int USERCANCELLED = 330;
        public const int SENDING = 340;
        // InternationalAlert
        public const int INTPREPARING = 400;
        public const int INTPROCESSINGSUB = 405;
        public const int INTPREPAREDNOCC = 410;
        public const int INTPREPARED = 411;
        public const int INTUSERCONFIRMED = 420;
        public const int INTUSERCANCELLED = 430;
        public const int INTSENDING = 440;
        // CellBroadcast
        public const int CBPREPARING = 500;
        public const int CBQUEUED = 510;
        public const int CBACTIVE = 540;
        public const int CBPAUSED = 590;

        public const int CANCELLING = 800;
        public const int FINISHED = 1000;
        public const int CANCELLED = 2000;

        // exceptions from AlertiX
        public const int EXC_executeAreaAlert = 42001;
        public const int EXC_prepareAreaAlert = 42002;
        public const int EXC_executeCustomAlert = 42003;
        public const int EXC_prepareCustomAlert = 42004;
        public const int EXC_executePreparedAlert = 42005;
        public const int EXC_cancelPreparedAlert = 42006;
        public const int EXC_executeIntAlert = 42007;
        public const int EXC_prepareIntAlert = 42008;
        public const int EXC_countByCC = 42009;
        public const int EXC_countryDistributionByCc = 42010;
        // errors from AlertiX
        public const int ERR_executeAreaAlert = 42011;
        public const int ERR_prepareAreaAlert = 42012;
        public const int ERR_executeCustomAlert = 42013;
        public const int ERR_prepareCustomAlert = 42014;
        public const int ERR_executePreparedAlert = 42015;
        public const int ERR_cancelPreparedAlert = 42016;
        public const int ERR_executeIntAlert = 42017;
        public const int ERR_prepareIntAlert = 42018;
        public const int ERR_countByCC = 42019;
        public const int ERR_countryDistributionByCc = 42020;
        // errors & exceptions from parsing
        public const int ERR_NOTAG_MSG = 42101;
        public const int ERR_NOATTR_AREA = 42102;
        public const int ERR_NOTAG_POLY = 42103;
        public const int EXC_GetAlertMsg = 42104;
        public const int ERR_NOTAG_CCODE = 42105;
        public const int ERR_EllipseToPoly = 42106;
        // job errors from AlertiX
        public const int ERR_JobError = 42201;

    }
}
