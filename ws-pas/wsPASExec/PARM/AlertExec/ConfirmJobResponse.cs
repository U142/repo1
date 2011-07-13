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
using System.Xml.Linq;

namespace com.ums.PARM.AlertExec
{
    public class UConfirmJobResponse
    {
        public int l_refno;
        public String sz_jobid;
        public int resultcode;
        public String resulttext;
    }
}
