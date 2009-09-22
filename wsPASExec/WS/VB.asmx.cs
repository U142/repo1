using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using com.ums.PAS.Database;

namespace com.ums.ws.voicebroadcast
{
    /// <summary>
    /// Summary description for VB
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/vb/address/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class VB : System.Web.Services.WebService
    {
        [WebMethod]
        public void test()
        {

        }
    }
}
