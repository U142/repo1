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
        /// (DEPRECATED) Get a list of vulnerable subscribers for a set of municipalities. Can filter by vulnerability code and company category
        /// </summary>
        /// <param name="Account">Login credentials</param>
        /// <param name="Municipals">List of municipals to restrict search to</param>
        /// <param name="VulnerabilityCodes">List of vulnerability types (pricate, vulnerable, dangerous, sprinkler). If none are provided, all are selected</param>
        /// <param name="CompanyCategories">List of companies (hospitals, kindergarders etc.). If none are provided, all are selected</param>
        /// <returns>List of AlertObject where attributes are set with the code and categories</returns>
        [WebMethod(Description = @"Get a list of vulnerable subscribers for a set of municipailities")]
        public List<VulnerableSubscriber> GetVulnerableSubscribers(Account Account, List<int> Municipals, List<int> Categories, List<int> Professions, string Language)
        {
            return new Integration().GetVulnerableSubscribers(Account, Municipals, Categories, Professions, Language);
        }

        /// <summary>
        /// Searches the folkereg and additional registry for entries matching search text. Searches in name, address and phone number fields.
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="Municipalities">Filter on municipalities (if excluded, searches all available municipalities)</param>
        /// <param name="SearchText">Name, address or number</param>
        /// <param name="EntryType">Person or Company</param>
        /// <param name="Language">Which language to return category and professions (Norwegian if excluded)</param>
        /// <returns></returns>
        [WebMethod(Description = @"Search all available registries for a specific person, company, address or number")]
        public List<RegistryEntry> SearchRegistry(Account Account, List<int> Municipalities, String SearchText, EntryType EntryType, string Language)
        {
            return new Integration().SearchRegistry(Account, Municipalities, SearchText, EntryType, Language);
        }

        /// <summary>
        /// Get all additional entries.
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="Municipalities">Filter on municipalities (if excluded, searches all available municipalities)</param>
        /// <param name="Categories">Filter on categories</param>
        /// <param name="Professions">Filter on professions (only if profession is applicable for category)</param>
        /// <param name="EntryType">Person or Company</param>
        /// <param name="Language">Which languge to return category and professions (Norweian if excluded)</param>
        /// <returns></returns>
        [WebMethod(Description = @"Get all additional entries")]
        public List<RegistryEntry> GetAdditionalRegistry(Account Account, List<int> Municipalities, List<int> Categories, List<int> Professions, EntryType EntryType, string Language)
        {
            return new Integration().GetAdditionalRegistry(Account, Municipalities, Categories, Professions, EntryType, Language);
        }

        /// <summary>
        /// Get a single entry from the additional registry
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="ID">Unike ID for the entry</param>
        /// <param name="Language">Which language to return the vulnerable categories if appliccable</param>
        /// <returns>A entry from the additional registry with all additional addresses and contact persons</returns>
        [WebMethod(Description = "Get a single entry from the additional registry")]
        public RegistryEntry GetAdditionalRegistryEntry(Account Account, int ID, string Language)
        {
            return new Integration().GetAdditionalRegistryEntry(Account, ID, Language);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="account"></param>
        /// <returns></returns>
        [WebMethod(Description = @"Get available municipalities, message profiles, configuraiton profiles, ttslangauages and more for a give account")]
        public AccountInfo GetAccountInfo(Account Account)
        {
            return new Integration().GetAccountInfo(Account);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="Account"></param>
        /// <returns></returns>
        [WebMethod(Description = @"Get available categories")]
        public List<Category> GetCategories(Account Account, EntryType? EntryType, string Language)
        {
            return new Integration().GetCategories(Account, EntryType, Language);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="Account"></param>
        /// <returns></returns>
        [WebMethod(Description = @"Get available professions")]
        public List<Profession> GetProfessions(Account Account, string Language)
        {
            return new Integration().GetProfessions(Account, Language);
        }    
    }
}
