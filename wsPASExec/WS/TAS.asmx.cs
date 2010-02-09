using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using com.ums.PAS.TAS;
using com.ums.UmsCommon;
using com.ums.PAS.Address;

namespace com.ums.ws.pas.tas
{
    /// <summary>
    /// Summary description for TAS
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/pas/tas")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class tasws : System.Web.Services.WebService
    {
        [WebMethod]
        public UTASUPDATES GetContinentsAndCountries(ULOGONINFO logon, long timefilter_count, long timefilter_requestlog)
        {
            try
            {
                PercentProgress.SetPercentDelegate percentdelegate = PercentProgress.newDelegate();
                percentdelegate(ref logon, ProgressJobType.TAS_UPDATE, new PercentResult());
                UTas tas = new UTas(ref logon);
                return tas.GetContinentsAndCountries(timefilter_count, timefilter_requestlog);
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                PercentProgress.DeleteJob(ref logon, ProgressJobType.TAS_UPDATE);
            }

        }
        [WebMethod]
        public UTASREQUEST GetAdrCount(ULOGONINFO logon, List<ULBACOUNTRY> country)
        {
            UTas tas = new UTas(ref logon);
            return tas.PerformAdrCountByCountry(ref country, ref logon);
        }
        [WebMethod]
        public List<UTASRESPONSENUMBER> GetResponseNumbers(ULOGONINFO logon)
        {
            UTas tas = new UTas(ref logon);
            return tas.GetResponseNumbers(ref logon);
        }
    }
}
