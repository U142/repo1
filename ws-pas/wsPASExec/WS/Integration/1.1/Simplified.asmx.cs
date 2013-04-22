using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using com.ums.UmsCommon;
using com.ums.pas.integration;
using com.ums.UmsDbLib;
using com.ums.PAS.Project;
using System.Data.Odbc;
using System.Configuration;

namespace com.ums.ws.integration.v11
{
    /// <summary>
    /// Summary description for Simplified
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/integration/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Simplified : com.ums.ws.integration.Simplified {
        /// <summary>
        /// Get a list of vulnerable subscribers for a set of municipalities. Can filter by vulnerability code and company category
        /// </summary>
        /// <param name="Account">Login credentials</param>
        /// <param name="Municipals">List of municipals to restrict search to</param>
        /// <param name="VulnerabilityCodes">List of vulnerability types (pricate, vulnerable, dangerous, sprinkler). If none are provided, all are selected</param>
        /// <param name="CompanyCategories">List of companies (hospitals, kindergarders etc.). If none are provided, all are selected</param>
        /// <returns>List of AlertObject where attributes are set with the code and categories</returns>
        [WebMethod(Description = @"Get a list of vulnerable subscribers for a set of municipailities")]
        public List<VulnerableSubscriber> GetVulnerableSubscribers(Account Account, List<int> Municipals, List<int> Categories, List<int> Professions)
        {
            return new Integration().GetVulnerableSubscribers(Account, Municipals, Categories, Professions);
        }
    }
}
