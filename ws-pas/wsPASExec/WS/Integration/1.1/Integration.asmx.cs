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
using System.Xml.Serialization;
using com.ums.pas.integration.AddressLookup;
using com.ums.pas.integration.AddressCleanup;

namespace com.ums.ws.integration.v11
{
    /// <summary>
    /// Summary description for Integration
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/integration/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Integration : com.ums.ws.integration.Integration {

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
            List<VulnerableSubscriber> ret = new List<VulnerableSubscriber>();

            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            UmsDb folkeReg = new UmsDb(ConfigurationManager.ConnectionStrings["vulnerable"].ConnectionString, 120);

            if (Municipals == null || Municipals.Count == 0)
                throw new Exception("No municipals selected.");

            string SqlVulnerabilityCodes = Categories != null && Categories.Count > 0 ? String.Format("AND SO.l_category IN ({0})", String.Join(",", Categories.Select(code => code.ToString()).ToArray())) : "";
            string SqlCompanyCategories = Professions != null && Professions.Count > 0 ? String.Format("AND (SO.l_profession IN ({0}) OR SO.l_profession IS NULL)", String.Join(",", Professions.Select(category => category.ToString()).ToArray())) : "";

            Language = Language ?? ""; // set language to empty if it is null

            string SqlSelectFields = @"
                                        SC.l_category,
                                        ISNULL(SCL.sz_description, SC.sz_description) sz_category,
                                        SP.l_profession,
                                        ISNULL(SPL.sz_description, SP.sz_description) sz_profession,
                                        ISNULL(AE.NAVN, '') NAVN,
                                        ISNULL(AE.TELEFON, '') TELEFON,
                                        ISNULL(AE.MOBIL, '') MOBIL,
                                        ISNULL(AE.KOMMUNENR, 0) KOMMUNENR,                
                                        ISNULL(AE.GATEKODE, 0) GATEKODE,
                                        ISNULL(AE.HUSNR, 0) HUSNR,
                                        ISNULL(AE.OPPGANG, '') HUSBOKSTAV,
                                        ISNULL(AE.GNR, 0) GNR,
                                        ISNULL(AE.BNR, 0) BNR,
                                        ISNULL(AE.SNR, 0) SNR,
                                        ISNULL(AE.FNR, 0) FNR,
                                        ISNULL(AE.UNR, 0) UNR
                                        ";

            string Sql = String.Format(@"select AE.KON_DMID ID, {0}
                            from 
	                            ADR_EDITED AE
	                            INNER JOIN ADR_EDITED_SO SO ON
		                            AE.KON_DMID = SO.KON_DMID {2} {3}
	                            LEFT OUTER JOIN SO_CATEGORY SC ON SC.l_category=SO.l_category
	                            LEFT OUTER JOIN SO_PROFESSION SP ON SP.l_profession=SO.l_profession
                                LEFT OUTER JOIN SO_CATEGORY_LANG SCL ON SCL.l_category=SC.l_category AND SCL.sz_lang=?
                                LEFT OUTER JOIN SO_PROFESSION_LANG SPL ON SPL.l_profession=SP.l_profession AND SCL.sz_lang=?
                            where AE.KOMMUNENR IN ({1})
                            union select AP.KON_DMID ID, {0}
                            from
	                            ADR_EDITED AE
	                            INNER JOIN ADR_EDITED AP ON
		                            AE.l_parent_adr = AP.KON_DMID
	                            INNER JOIN ADR_EDITED_SO SO ON
		                            AP.KON_DMID = SO.KON_DMID {2} {3}
	                            LEFT OUTER JOIN SO_CATEGORY SC ON SC.l_category=SO.l_category
	                            LEFT OUTER JOIN SO_PROFESSION SP ON SP.l_profession=SO.l_profession
                                LEFT OUTER JOIN SO_CATEGORY_LANG SCL ON SCL.l_category=SC.l_category AND SCL.sz_lang=?
                                LEFT OUTER JOIN SO_PROFESSION_LANG SPL ON SPL.l_profession=SP.l_profession AND SCL.sz_lang=?
                            where AE.KOMMUNENR IN ({1})
                            order by SC.l_category, SP.l_profession, ID"
                , SqlSelectFields
                , String.Join(",", Municipals.Select(muni => muni.ToString()).ToArray())
                , SqlVulnerabilityCodes
                , SqlCompanyCategories);

            // Get list of subscribers
            using (OdbcCommand cmd = folkeReg.CreateCommand(Sql))
            {
                cmd.Parameters.Add("lang", OdbcType.VarChar).Value = Language;
                cmd.Parameters.Add("lang", OdbcType.VarChar).Value = Language;
                cmd.Parameters.Add("lang", OdbcType.VarChar).Value = Language;
                cmd.Parameters.Add("lang", OdbcType.VarChar).Value = Language;

                using (OdbcDataReader rs = cmd.ExecuteReader())
                {
                    int previousId = 0;
                    VulnerableSubscriber subscriber = null;

                    while (rs.Read())
                    {
                        if (rs.GetInt32(rs.GetOrdinal("ID")) != previousId)
                        {
                            subscriber = new VulnerableSubscriber();
                            subscriber.Endpoints = new List<Endpoint>();
                            subscriber.StreetAddresses = new List<StreetAddress>();
                            subscriber.PropertyAddresses = new List<PropertyAddress>();
                        }

                        subscriber.Name = rs.GetString(rs.GetOrdinal("NAVN"));

                        if (rs.GetString(rs.GetOrdinal("TELEFON")) != "")
                        {
                            Phone phone = new Phone() { Address = rs.GetString(rs.GetOrdinal("TELEFON")), CanReceiveSms = false };
                            if (!subscriber.Endpoints.Contains(phone))
                                subscriber.Endpoints.Add(phone);
                        }

                        if (rs.GetString(rs.GetOrdinal("MOBIL")) != "")
                        {
                            Phone phone = new Phone() { Address = rs.GetString(rs.GetOrdinal("MOBIL")), CanReceiveSms = true };
                            if (!subscriber.Endpoints.Contains(phone))
                                subscriber.Endpoints.Add(phone);
                        }

                        if (!rs.IsDBNull(rs.GetOrdinal("sz_category")) && !rs.IsDBNull(rs.GetOrdinal("l_category")))
                            subscriber.Category = new Category() { Id = rs.GetInt32(rs.GetOrdinal("l_category")), Name = rs.GetString(rs.GetOrdinal("sz_category")) };

                        if (!rs.IsDBNull(rs.GetOrdinal("sz_profession")) && !rs.IsDBNull(rs.GetOrdinal("l_profession")))
                            subscriber.Profession = new Profession() { Id = rs.GetInt32(rs.GetOrdinal("l_profession")), Name = rs.GetString(rs.GetOrdinal("sz_profession")) };

                        if (rs.GetInt32(rs.GetOrdinal("GATEKODE")) != 0) // streetaddress
                        {
                            subscriber.StreetAddresses.Add(new StreetAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GATEKODE")), rs.GetInt32(rs.GetOrdinal("HUSNR")), rs.GetString(rs.GetOrdinal("HUSBOKSTAV")), null, null));
                        }
                        else
                        {
                            subscriber.PropertyAddresses.Add(new PropertyAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GNR")), rs.GetInt32(rs.GetOrdinal("BNR")), rs.GetInt32(rs.GetOrdinal("FNR")), rs.GetInt32(rs.GetOrdinal("SNR")), null));
                        }

                        if (rs.GetInt32(rs.GetOrdinal("ID")) != previousId)
                            ret.Add(subscriber);

                        previousId = rs.GetInt32(rs.GetOrdinal("ID"));
                    }
                }
            }

            return ret;
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
            List<RegistryEntry> ret = new List<RegistryEntry>();

            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            UmsDb folkeReg = new UmsDb(ConfigurationManager.ConnectionStrings["vulnerable"].ConnectionString, 120);

            string sql = "";
            string searchMunicipalities = "";

            if (Municipalities == null || Municipalities.Count == 0)
                Municipalities = GetMunicipalities(logonInfo.l_deptpk).Select(m => m.Id).ToList();
            else
                Municipalities = Municipalities.Intersect(GetMunicipalities(logonInfo.l_deptpk).Select(m => m.Id).ToList()).ToList();

            // TODO: Check if municipalities is empty + cross reference with municipalities available for department
            foreach (int m in Municipalities)
                searchMunicipalities = string.Format("{0}{1}{2}", searchMunicipalities, searchMunicipalities.Length > 0 ? "," : "", m);

            switch (EntryType)
            {
                case v11.EntryType.PERSON:
                    sql = "exec sp_SearchPerson ?, ?";
                    break;
                case v11.EntryType.COMPANY:
                    sql = "exec sp_SearchCompany ?, ?, ?";
                    break;
                default:
                    throw new Exception("Unsupported entry type");
            }

            using (var cmd = folkeReg.CreateCommand(sql))
            {
                cmd.Parameters.Add("searchText", OdbcType.VarChar).Value = SearchText;
                cmd.Parameters.Add("municipalities", OdbcType.VarChar).Value = searchMunicipalities;

                if (EntryType == v11.EntryType.COMPANY)
                    cmd.Parameters.Add("language", OdbcType.VarChar).Value = Language == null ? "" : Language;

                using (var rs = cmd.ExecuteReader())
                {
                    RegistryEntry entry = null;

                    while (rs.Read())
                    {
                        // new entry, create and add to ret
                        if ((rs.GetInt32(rs.GetOrdinal("l_parent_adr")) == 0 && rs.GetInt32(rs.GetOrdinal("l_parent_comp")) == 0) || entry == null)
                        {
                            entry = new RegistryEntry();
                            ret.Add(entry);

                            entry.Id = rs.GetInt32(rs.GetOrdinal("KON_DMID"));
                            entry.Name = rs.GetString(rs.GetOrdinal("NAVN"));
                            entry.Address = rs.GetString(rs.GetOrdinal("ADRESSE"));
                            entry.ZipCode = rs.GetInt32(rs.GetOrdinal("POSTNR"));
                            entry.ZipArea = rs.GetString(rs.GetOrdinal("POSTSTED"));

                            if (!rs.IsDBNull(rs.GetOrdinal("l_category")) && !rs.IsDBNull(rs.GetOrdinal("sz_category")))
                                entry.Category = new Category() { Id = rs.GetInt32(rs.GetOrdinal("l_category")), Name = rs.GetString(rs.GetOrdinal("sz_category")), HasProfessions = !rs.IsDBNull(rs.GetOrdinal("l_profession")) };

                            if (!rs.IsDBNull(rs.GetOrdinal("l_profession")) && !rs.IsDBNull(rs.GetOrdinal("sz_profession")))
                                entry.Profession = new Profession() { Id = rs.GetInt32(rs.GetOrdinal("l_profession")), Name = rs.GetString(rs.GetOrdinal("sz_profession")) };

                            // property address
                            if (rs.GetInt32(rs.GetOrdinal("GATEKODE")) == 0 || (
                                    rs.GetInt32(rs.GetOrdinal("GATEKODE")) == rs.GetInt32(rs.GetOrdinal("GNR")) && 
                                    rs.GetInt32(rs.GetOrdinal("HUSNR")) == 0
                                )
                            ) 
                            {
                                entry.AddressDetails = new PropertyAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GNR")), rs.GetInt32(rs.GetOrdinal("BNR")), rs.GetInt32(rs.GetOrdinal("FNR")), rs.GetInt32(rs.GetOrdinal("SNR")), null);
                            }
                            else
                            {
                                entry.AddressDetails = new StreetAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GATEKODE")), rs.GetInt32(rs.GetOrdinal("HUSNR")), rs.GetString(rs.GetOrdinal("OPPGANG")), null, null);
                            }

                            // Add phone numbers
                            entry.Endpoints = new List<Endpoint>();

                            if (!rs.IsDBNull(rs.GetOrdinal("TELEFON")) && rs.GetString(rs.GetOrdinal("TELEFON")).Length > 0)
                                entry.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("TELEFON")), CanReceiveSms = false });
                            
                            if (!rs.IsDBNull(rs.GetOrdinal("MOBIL")) && rs.GetString(rs.GetOrdinal("MOBIL")).Length > 0)
                                entry.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("MOBIL")), CanReceiveSms = true });

                            // Add changed and changeddate if appropriate
                            entry.IsChanged = rs.GetBoolean(rs.GetOrdinal("IsChanged"));

                            if (!rs.IsDBNull(rs.GetOrdinal("Updated")) && rs.GetInt64(rs.GetOrdinal("Updated")) > 0)
                                entry.Updated = new DateTime(rs.GetInt64(rs.GetOrdinal("Updated")));
                        }
                        else if (rs.GetInt32(rs.GetOrdinal("l_parent_adr")) > 0) // additional address
                        {
                            if (entry.AdditionalAddresses == null)
                                entry.AdditionalAddresses = new List<AlertTarget>();

                            // property address
                            if (rs.GetInt32(rs.GetOrdinal("GATEKODE")) == 0 || (
                                    rs.GetInt32(rs.GetOrdinal("GATEKODE")) == rs.GetInt32(rs.GetOrdinal("GNR")) &&
                                    rs.GetInt32(rs.GetOrdinal("HUSNR")) == 0
                                )
                            ) 
                            {
                                entry.AdditionalAddresses.Add(new PropertyAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GNR")), rs.GetInt32(rs.GetOrdinal("BNR")), rs.GetInt32(rs.GetOrdinal("FNR")), rs.GetInt32(rs.GetOrdinal("SNR")), null));
                            }
                            else
                            {
                                entry.AdditionalAddresses.Add(new StreetAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GATEKODE")), rs.GetInt32(rs.GetOrdinal("HUSNR")), rs.GetString(rs.GetOrdinal("OPPGANG")), null, null));
                            }
                        }
                        else if (rs.GetInt32(rs.GetOrdinal("l_parent_comp")) > 0) // contact person
                        {
                            if (entry.ContactPersons == null)
                                entry.ContactPersons = new List<ContactPerson>();

                            ContactPerson cp = new ContactPerson();
                            cp.Id = rs.GetInt32(rs.GetOrdinal("KON_DMID"));
                            cp.FirstName = rs.GetString(rs.GetOrdinal("sz_firstname"));
                            cp.MiddleName = rs.GetString(rs.GetOrdinal("sz_midname"));
                            cp.LastName = rs.GetString(rs.GetOrdinal("sz_surname"));

                            cp.Endpoints = new List<Endpoint>();

                            if (!rs.IsDBNull(rs.GetOrdinal("TELEFON")) && rs.GetString(rs.GetOrdinal("TELEFON")).Length > 0)
                                cp.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("TELEFON")), CanReceiveSms = false });

                            if (!rs.IsDBNull(rs.GetOrdinal("MOBIL")) && rs.GetString(rs.GetOrdinal("MOBIL")).Length > 0)
                                cp.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("MOBIL")), CanReceiveSms = true });

                            entry.ContactPersons.Add(cp);
                        }
                    }
                }
            }

            return ret;
        }

        /// <summary>
        /// Get a single entry from the registry
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="ID">Unike ID for the entry</param>
        /// <param name="Language">Which language to return the vulnerable categories if appliccable</param>
        /// <returns>A entry from the additional registry with all additional addresses and contact persons</returns>
        [WebMethod(Description= "Get a single entry from the additional registry")]
        public RegistryEntry GetRegistryEntry(Account Account, int ID, string Language)
        {
            RegistryEntry entry = new RegistryEntry();

            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            UmsDb folkeReg = new UmsDb(ConfigurationManager.ConnectionStrings["vulnerable"].ConnectionString, 120);

            string sql = "sp_GetRegistryEntry ?, ?, ?";

            List<int> Municipalities = GetMunicipalities(logonInfo.l_deptpk).Select(m => m.Id).ToList();
            string searchMunicipalities = string.Join(",", Municipalities.Select(m => m.ToString()).ToArray());

            using (var cmd = folkeReg.CreateCommand(sql))
            {
                cmd.Parameters.Add("kon_dmid", OdbcType.Int).Value = ID;
                cmd.Parameters.Add("municipalities", OdbcType.VarChar).Value = searchMunicipalities;
                cmd.Parameters.Add("language", OdbcType.VarChar).Value = Language == null ? "" : Language;

                using (var rs = cmd.ExecuteReader())
                {
                    while (rs.Read())
                    {
                        // new entry, create and add to ret
                        if ((rs.GetInt32(rs.GetOrdinal("l_parent_adr")) == 0 && rs.GetInt32(rs.GetOrdinal("l_parent_comp")) == 0) || entry == null)
                        {
                            entry.Id = rs.GetInt32(rs.GetOrdinal("KON_DMID"));
                            entry.Name = rs.GetString(rs.GetOrdinal("NAVN"));
                            entry.Address = rs.GetString(rs.GetOrdinal("ADRESSE"));
                            entry.ZipCode = rs.GetInt32(rs.GetOrdinal("POSTNR"));
                            entry.ZipArea = rs.GetString(rs.GetOrdinal("POSTSTED"));

                            if (!rs.IsDBNull(rs.GetOrdinal("l_category")) && !rs.IsDBNull(rs.GetOrdinal("sz_category")))
                                entry.Category = new Category() { Id = rs.GetInt32(rs.GetOrdinal("l_category")), Name = rs.GetString(rs.GetOrdinal("sz_category")), HasProfessions = !rs.IsDBNull(rs.GetOrdinal("l_profession")) };

                            if (!rs.IsDBNull(rs.GetOrdinal("l_profession")) && !rs.IsDBNull(rs.GetOrdinal("sz_profession")))
                                entry.Profession = new Profession() { Id = rs.GetInt32(rs.GetOrdinal("l_profession")), Name = rs.GetString(rs.GetOrdinal("sz_profession")) };

                            // property address
                            if (rs.GetInt32(rs.GetOrdinal("GATEKODE")) == 0 || (
                                    rs.GetInt32(rs.GetOrdinal("GATEKODE")) == rs.GetInt32(rs.GetOrdinal("GNR")) &&
                                    rs.GetInt32(rs.GetOrdinal("HUSNR")) == 0
                                )
                            )
                            {
                                entry.AddressDetails = new PropertyAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GNR")), rs.GetInt32(rs.GetOrdinal("BNR")), rs.GetInt32(rs.GetOrdinal("FNR")), rs.GetInt32(rs.GetOrdinal("SNR")), null);
                            }
                            else
                            {
                                entry.AddressDetails = new StreetAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GATEKODE")), rs.GetInt32(rs.GetOrdinal("HUSNR")), rs.GetString(rs.GetOrdinal("OPPGANG")), null, null);
                            }

                            // Add phone numbers
                            entry.Endpoints = new List<Endpoint>();

                            if (!rs.IsDBNull(rs.GetOrdinal("TELEFON")) && rs.GetString(rs.GetOrdinal("TELEFON")).Length > 0)
                                entry.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("TELEFON")), CanReceiveSms = false });

                            if (!rs.IsDBNull(rs.GetOrdinal("MOBIL")) && rs.GetString(rs.GetOrdinal("MOBIL")).Length > 0)
                                entry.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("MOBIL")), CanReceiveSms = true });

                            // Add changed and changeddate if appropriate
                            entry.IsChanged = rs.GetBoolean(rs.GetOrdinal("IsChanged"));

                            if (!rs.IsDBNull(rs.GetOrdinal("Updated")) && rs.GetInt64(rs.GetOrdinal("Updated")) > 0)
                                entry.Updated = new DateTime(rs.GetInt64(rs.GetOrdinal("Updated")));
                            else
                                entry.Updated = null;
                        }
                        else if (rs.GetInt32(rs.GetOrdinal("l_parent_adr")) > 0) // additional address
                        {
                            if (entry.AdditionalAddresses == null)
                                entry.AdditionalAddresses = new List<AlertTarget>();

                            // property address
                            if (rs.GetInt32(rs.GetOrdinal("GATEKODE")) == 0 || (
                                    rs.GetInt32(rs.GetOrdinal("GATEKODE")) == rs.GetInt32(rs.GetOrdinal("GNR")) &&
                                    rs.GetInt32(rs.GetOrdinal("HUSNR")) == 0
                                )
                            )
                            {
                                entry.AdditionalAddresses.Add(new PropertyAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GNR")), rs.GetInt32(rs.GetOrdinal("BNR")), rs.GetInt32(rs.GetOrdinal("FNR")), rs.GetInt32(rs.GetOrdinal("SNR")), null));
                            }
                            else
                            {
                                entry.AdditionalAddresses.Add(new StreetAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GATEKODE")), rs.GetInt32(rs.GetOrdinal("HUSNR")), rs.GetString(rs.GetOrdinal("OPPGANG")), null, null));
                            }
                        }
                        else if (rs.GetInt32(rs.GetOrdinal("l_parent_comp")) > 0) // contact person
                        {
                            if (entry.ContactPersons == null)
                                entry.ContactPersons = new List<ContactPerson>();

                            ContactPerson cp = new ContactPerson();
                            cp.Id = rs.GetInt32(rs.GetOrdinal("KON_DMID"));
                            cp.FirstName = rs.GetString(rs.GetOrdinal("sz_firstname"));
                            cp.MiddleName = rs.GetString(rs.GetOrdinal("sz_midname"));
                            cp.LastName = rs.GetString(rs.GetOrdinal("sz_surname"));

                            cp.Endpoints = new List<Endpoint>();

                            if (!rs.IsDBNull(rs.GetOrdinal("TELEFON")) && rs.GetString(rs.GetOrdinal("TELEFON")).Length > 0)
                                cp.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("TELEFON")), CanReceiveSms = false });

                            if (!rs.IsDBNull(rs.GetOrdinal("MOBIL")) && rs.GetString(rs.GetOrdinal("MOBIL")).Length > 0)
                                cp.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("MOBIL")), CanReceiveSms = true });

                            entry.ContactPersons.Add(cp);
                        }
                    }
                }
            }

            return entry;
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
            List<RegistryEntry> ret = new List<RegistryEntry>();

            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            UmsDb folkeReg = new UmsDb(ConfigurationManager.ConnectionStrings["vulnerable"].ConnectionString, 120);

            string sql = "sp_GetAdditionalRegistry ?, ?, ? ,? ,?";

            if (Municipalities == null || Municipalities.Count == 0)
                Municipalities = GetMunicipalities(logonInfo.l_deptpk).Select(m => m.Id).ToList();
            else
                Municipalities = Municipalities.Intersect(GetMunicipalities(logonInfo.l_deptpk).Select(m => m.Id).ToList()).ToList();

            string searchMunicipalities = string.Join(",", Municipalities.Select(m => m.ToString()).ToArray());
            string searchCategories = Categories == null ? "" : string.Join(",", Categories.Select(c => c.ToString()).ToArray());
            string searchProfessions = Professions == null ? "" : string.Join(",", Professions.Select(p => p.ToString()).ToArray());

            using (var cmd = folkeReg.CreateCommand(sql))
            {
                cmd.Parameters.Add("municipalities", OdbcType.VarChar).Value = searchMunicipalities;
                cmd.Parameters.Add("categories", OdbcType.VarChar).Value = searchCategories;
                cmd.Parameters.Add("professions", OdbcType.VarChar).Value = searchProfessions;
                switch(EntryType)
                {
                    case v11.EntryType.PERSON:
                        cmd.Parameters.Add("bedrift", OdbcType.Int).Value = 0;
                        break;
                    case v11.EntryType.COMPANY:
                    default:
                        cmd.Parameters.Add("bedrift", OdbcType.Int).Value = 1;
                        break;
                }
                cmd.Parameters.Add("language", OdbcType.VarChar).Value = Language == null ? "" : Language;

                using (var rs = cmd.ExecuteReader())
                {
                    RegistryEntry entry = null;

                    while (rs.Read())
                    {
                        // new entry, create and add to ret
                        if ((rs.GetInt32(rs.GetOrdinal("l_parent_adr")) == 0 && rs.GetInt32(rs.GetOrdinal("l_parent_comp")) == 0) || entry == null)
                        {
                            entry = new RegistryEntry();
                            ret.Add(entry);

                            entry.Id = rs.GetInt32(rs.GetOrdinal("KON_DMID"));
                            entry.Name = rs.GetString(rs.GetOrdinal("NAVN"));
                            entry.Address = rs.GetString(rs.GetOrdinal("ADRESSE"));
                            entry.ZipCode = rs.GetInt32(rs.GetOrdinal("POSTNR"));
                            entry.ZipArea = rs.GetString(rs.GetOrdinal("POSTSTED"));

                            if (!rs.IsDBNull(rs.GetOrdinal("l_category")) && !rs.IsDBNull(rs.GetOrdinal("sz_category")))
                                entry.Category = new Category() { Id = rs.GetInt32(rs.GetOrdinal("l_category")), Name = rs.GetString(rs.GetOrdinal("sz_category")), HasProfessions = !rs.IsDBNull(rs.GetOrdinal("l_profession")) };

                            if (!rs.IsDBNull(rs.GetOrdinal("l_profession")) && !rs.IsDBNull(rs.GetOrdinal("sz_profession")))
                                entry.Profession = new Profession() { Id = rs.GetInt32(rs.GetOrdinal("l_profession")), Name = rs.GetString(rs.GetOrdinal("sz_profession")) };

                            // property address
                            if (rs.GetInt32(rs.GetOrdinal("GATEKODE")) == 0 || (
                                    rs.GetInt32(rs.GetOrdinal("GATEKODE")) == rs.GetInt32(rs.GetOrdinal("GNR")) &&
                                    rs.GetInt32(rs.GetOrdinal("HUSNR")) == 0
                                )
                            )
                            {
                                entry.AddressDetails = new PropertyAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GNR")), rs.GetInt32(rs.GetOrdinal("BNR")), rs.GetInt32(rs.GetOrdinal("FNR")), rs.GetInt32(rs.GetOrdinal("SNR")), null);
                            }
                            else
                            {
                                entry.AddressDetails = new StreetAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GATEKODE")), rs.GetInt32(rs.GetOrdinal("HUSNR")), rs.GetString(rs.GetOrdinal("OPPGANG")), null, null);
                            }

                            // Add phone numbers
                            entry.Endpoints = new List<Endpoint>();

                            if (!rs.IsDBNull(rs.GetOrdinal("TELEFON")) && rs.GetString(rs.GetOrdinal("TELEFON")).Length > 0)
                                entry.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("TELEFON")), CanReceiveSms = false });

                            if (!rs.IsDBNull(rs.GetOrdinal("MOBIL")) && rs.GetString(rs.GetOrdinal("MOBIL")).Length > 0)
                                entry.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("MOBIL")), CanReceiveSms = true });

                            // Add changed and changeddate if appropriate
                            entry.IsChanged = true;

                            if (!rs.IsDBNull(rs.GetOrdinal("Updated")) && rs.GetInt64(rs.GetOrdinal("Updated")) > 0)
                                entry.Updated = new DateTime(rs.GetInt64(rs.GetOrdinal("Updated")));
                        }
                        else if (rs.GetInt32(rs.GetOrdinal("l_parent_adr")) > 0) // additional address
                        {
                            if (entry.AdditionalAddresses == null)
                                entry.AdditionalAddresses = new List<AlertTarget>();

                            // property address
                            if (rs.GetInt32(rs.GetOrdinal("GATEKODE")) == 0 || (
                                    rs.GetInt32(rs.GetOrdinal("GATEKODE")) == rs.GetInt32(rs.GetOrdinal("GNR")) &&
                                    rs.GetInt32(rs.GetOrdinal("HUSNR")) == 0
                                )
                            )
                            {
                                entry.AdditionalAddresses.Add(new PropertyAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GNR")), rs.GetInt32(rs.GetOrdinal("BNR")), rs.GetInt32(rs.GetOrdinal("FNR")), rs.GetInt32(rs.GetOrdinal("SNR")), null));
                            }
                            else
                            {
                                entry.AdditionalAddresses.Add(new StreetAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(), rs.GetInt32(rs.GetOrdinal("GATEKODE")), rs.GetInt32(rs.GetOrdinal("HUSNR")), rs.GetString(rs.GetOrdinal("OPPGANG")), null, null));
                            }
                        }
                        else if (rs.GetInt32(rs.GetOrdinal("l_parent_comp")) > 0) // contact person
                        {
                            if (entry.ContactPersons == null)
                                entry.ContactPersons = new List<ContactPerson>();

                            ContactPerson cp = new ContactPerson();
                            cp.Id = rs.GetInt32(rs.GetOrdinal("KON_DMID"));
                            cp.FirstName = rs.GetString(rs.GetOrdinal("sz_firstname"));
                            cp.MiddleName = rs.GetString(rs.GetOrdinal("sz_midname"));
                            cp.LastName = rs.GetString(rs.GetOrdinal("sz_surname"));

                            cp.Endpoints = new List<Endpoint>();

                            if (!rs.IsDBNull(rs.GetOrdinal("TELEFON")) && rs.GetString(rs.GetOrdinal("TELEFON")).Length > 0)
                                cp.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("TELEFON")), CanReceiveSms = false });

                            if (!rs.IsDBNull(rs.GetOrdinal("MOBIL")) && rs.GetString(rs.GetOrdinal("MOBIL")).Length > 0)
                                cp.Endpoints.Add(new Phone() { Address = rs.GetString(rs.GetOrdinal("MOBIL")), CanReceiveSms = true });

                            entry.ContactPersons.Add(cp);
                        }
                    }
                }
            }

            return ret;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="account"></param>
        /// <returns></returns>
        [WebMethod(Description= @"Get available municipalities, message profiles, configuraiton profiles, ttslangauages and more for a give account")]
        public AccountInfo GetAccountInfo(Account Account)
        {
            AccountInfo ret = new AccountInfo();

            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            ret.Municipalities = GetMunicipalities(logonInfo.l_deptpk);

            return ret;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="Account"></param>
        /// <returns></returns>
        [WebMethod(Description= @"Get available categories")]
        public List<Category> GetCategories(Account Account, EntryType? EntryType, string Language)
        {
            List<Category> ret = new List<Category>();

            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            string sql;

            if (EntryType == null || EntryType.Value == com.ums.ws.integration.v11.EntryType.COMPANY)
            {
                sql = @"select 
	                        SC.l_category,
	                        isnull(SCL.sz_description, SC.sz_description) sz_description,
	                        SC.f_hasprofessions
                        from 
	                        SO_CATEGORY SC
	                        LEFT OUTER JOIN SO_CATEGORY_LANG SCL ON
		                        SC.l_category=SCL.l_category AND
		                        SCL.sz_lang = ?
                        where
	                        SC.f_company = 1
                        order by
                            SC.l_category";
            }
            else
            {
                throw new Exception("Entry type not supported");
            }

            UmsDb folkeReg = new UmsDb(ConfigurationManager.ConnectionStrings["vulnerable"].ConnectionString, 120);

            using (var cmd = folkeReg.CreateCommand(sql))
            {
                cmd.Parameters.Add("lang", OdbcType.VarChar).Value = Language == null ? "" : Language;

                using (var rs = cmd.ExecuteReader())
                {
                    while (rs.Read())
                        ret.Add(new Category() { Id = rs.GetInt32(rs.GetOrdinal("l_category")), Name = rs.GetString(rs.GetOrdinal("sz_description")), HasProfessions = rs.GetBoolean(rs.GetOrdinal("f_hasprofessions")) });
                }
            }

            return ret;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="Account"></param>
        /// <returns></returns>
        [WebMethod(Description = @"Get available professions")]
        public List<Profession> GetProfessions(Account Account, string Language)
        {
            List<Profession> ret = new List<Profession>();

            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            string sql = @"select 
	                    SP.l_profession,
	                    isnull(SPL.sz_description, SP.sz_description) sz_description
                    from 
	                    SO_PROFESSION SP
	                    LEFT OUTER JOIN SO_PROFESSION_LANG SPL ON
		                    SP.l_profession=SPL.l_profession AND
		                    SPL.sz_lang = ?
                    order by
                        SP.l_profession";

            UmsDb folkeReg = new UmsDb(ConfigurationManager.ConnectionStrings["vulnerable"].ConnectionString, 120);

            using (var cmd = folkeReg.CreateCommand(sql))
            {
                cmd.Parameters.Add("lang", OdbcType.VarChar).Value = Language == null ? "" : Language;

                using (var rs = cmd.ExecuteReader())
                {
                    while (rs.Read())
                        ret.Add(new Profession() { Id = rs.GetInt32(rs.GetOrdinal("l_profession")), Name = rs.GetString(rs.GetOrdinal("sz_description")) });
                }
            }

            return ret;
        }

        /// <summary>
        /// Match addresses without creating alert
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="AlertTargets">List of alert targets</param>
        /// <returns></returns>
        [WebMethod(Description = "Do a address lookup without generating an alert")]
        public List<AlertTargetData> GetPhoneNumbers(Account Account, List<AlertTarget> AlertTargets)
        {
            List<RecipientData> recipientDataList = new List<RecipientData>();

            string FolkeregDatabaseConnectionString = System.Configuration.ConfigurationManager.ConnectionStrings["adrdb_folkereg"].ConnectionString;
            string NorwayDatabaseConnectionString = System.Configuration.ConfigurationManager.ConnectionStrings["adrdb_regular"].ConnectionString;

            bool useDoubleAdrLookup = false;
            if (System.Configuration.ConfigurationManager.AppSettings["UseDoubleAdrLookup"] == "true") useDoubleAdrLookup = true;

            ULOGONINFO logonInfo = new ULOGONINFO();
            logonInfo.sz_compid = Account.CompanyId;
            logonInfo.sz_deptid = Account.DepartmentId;
            logonInfo.sz_password = Account.Password;

            UmsDb umsDb = new UmsDb();
            umsDb.CheckDepartmentLogonLiteral(ref logonInfo);

            List<StreetAddress> streetAddresses = AlertTargets.OfType<StreetAddress>().ToList();
            List<PropertyAddress> propertyAddresses = AlertTargets.OfType<PropertyAddress>().ToList();
            List<OwnerAddress> ownerAddresses = AlertTargets.OfType<OwnerAddress>().ToList();
            List<AlertObject> alertObjects = AlertTargets.OfType<AlertObject>().ToList();

            #region alertObject
            foreach (AlertObject alertObject in alertObjects)
            {
                recipientDataList.Add(new RecipientData()
                {
                    AlertTarget = alertObject,
                    Endpoints = alertObject.Endpoints,
                    Name = alertObject.Name,
                    Address = "",
                    Postno = 0,
                    PostPlace = "",
                });
            }
            #endregion
            #region streetAddress
            IStreetAddressLookupFacade streetLookupInterface = new StreetAddressLookupImpl();
            // Get Folkereg data
            IEnumerable<RecipientData> streetAddressLookup = streetLookupInterface.GetMatchingStreetAddresses(
                                                        FolkeregDatabaseConnectionString,
                                                        streetAddresses);
            
            if (useDoubleAdrLookup)
            {
                // Get Norway data
                IEnumerable<RecipientData> streetAddressLookup2 = streetLookupInterface.GetMatchingStreetAddresses(
                                                            NorwayDatabaseConnectionString,
                                                            streetAddresses);
                // Match lists
                IEnumerable<RecipientData> streetAddressLookupTotal = streetAddressLookup.Union(streetAddressLookup2);
                streetAddressLookup = streetAddressLookupTotal;
            }

            // Remove number not found that was found in one of the databases
            List<StreetAddress> saNumberNotFound = streetLookupInterface.GetNoNumbersFoundList().ToList();
            foreach (RecipientData rd in streetAddressLookup)
                if (saNumberNotFound.Contains((StreetAddress)rd.AlertTarget))
                    saNumberNotFound.Remove((StreetAddress)rd.AlertTarget);

            // Build complete list
            recipientDataList.AddRange(streetAddressLookup);
            foreach (StreetAddress sa in saNumberNotFound)
                recipientDataList.Add(new RecipientData()
                {
                    AlertTarget = sa,
                    Name = "",
                    NoRecipients = true,
                });
            #endregion
            #region propertyAddress
            IPropertyAddressLookupFacade propertyLookupInterface = new PropertyAddressLookupImpl();
            // Get Folkereg data
            IEnumerable<RecipientData> propertyLookup = propertyLookupInterface.GetMatchingPropertyAddresses(
                                                                            FolkeregDatabaseConnectionString,
                                                                            propertyAddresses);

            if (useDoubleAdrLookup)
            {
                // Get Norway data
                IEnumerable<RecipientData> propertyLookup2 = propertyLookupInterface.GetMatchingPropertyAddresses(
                                                                                NorwayDatabaseConnectionString,
                                                                                propertyAddresses);
                // Match lists
                IEnumerable<RecipientData> propertyLookupTotal = propertyLookup.Union(propertyLookup2);
                propertyLookup = propertyLookupTotal;
            }

            // Remove number not found that was found in one of the databases
            List<PropertyAddress> paNumberNotFound = propertyLookupInterface.GetNoNumbersFoundList().ToList();
            foreach (RecipientData rd in propertyLookup)
                if (paNumberNotFound.Contains((PropertyAddress)rd.AlertTarget))
                    paNumberNotFound.Remove((PropertyAddress)rd.AlertTarget);

            // Build complete list
            recipientDataList.AddRange(propertyLookup);
            foreach (PropertyAddress pa in paNumberNotFound)
                recipientDataList.Add(new RecipientData()
                {
                    AlertTarget = pa,
                    Name = "",
                    NoRecipients = true,
                });
            #endregion
            #region ownerAddress
            IOwnerLookupFacade ownerLookupInterface = new OwnerLookupImpl();
            IEnumerable<RecipientData> ownerLookup1 = ownerLookupInterface.GetMatchingOwnerAddresses(
                                                                                FolkeregDatabaseConnectionString,
                                                                                ownerAddresses);
            recipientDataList.AddRange(ownerLookup1);

            if (ownerLookupInterface.GetNoMatchList().Count() > 0)
            {
                IEnumerable<RecipientData> ownerLookup2 = ownerLookupInterface.GetMatchingOwnerAddresses(
                                                                                    NorwayDatabaseConnectionString,
                                                                                    ownerLookupInterface.GetNoMatchList().ToList());
                recipientDataList.AddRange(ownerLookup2);
                if (ownerLookupInterface.GetNoMatchList().Count() > 0)
                {
                    foreach (OwnerAddress ownerAddress in ownerLookupInterface.GetNoMatchList())
                    {
                        recipientDataList.Add(new RecipientData()
                        {
                            AlertTarget = ownerAddress,
                            Name = "",
                            NoRecipients = true,
                        });
                    }
                }
            }
            #endregion

            IDuplicateCleaner DuplicateCleaner = new DuplicateCleanerImpl();
            recipientDataList = DuplicateCleaner.DuplicateCleanup(recipientDataList,
                        (r, e) =>
                        {
                            //log.DebugFormat("Insert duplicate mark in database for {0} with endpoint {1}", r.Name, e.Address);
                        });

            List<AlertTargetData> ret = new List<AlertTargetData>();
            foreach (RecipientData rd in recipientDataList)
                ret.Add(new AlertTargetData()
                {
                    Name = rd.Name,
                    Endpoints = rd.Endpoints,
                    AlertTarget = rd.AlertTarget
                });

            return ret;
        }

        private List<Municipality> GetMunicipalities(int department)
        {
            List<Municipality> ret = new List<Municipality>();

            UmsDb folkeReg = new UmsDb(ConfigurationManager.ConnectionStrings["vulnerable"].ConnectionString, 120);

            string sql = "select D.l_municipalid, M.sz_name from DEPARTMENT_X_MUNICIPAL D INNER JOIN MUNICIPAL M ON D.l_municipalid=M.l_municipalid where D.l_deptpk=? order by M.sz_name";

            using (var cmd = folkeReg.CreateCommand(sql))
            {
                cmd.Parameters.Add("deptpk", OdbcType.Int).Value = department;

                using (var rs = cmd.ExecuteReader())
                {
                    while (rs.Read())
                        ret.Add(new Municipality() { Id = rs.GetInt32(rs.GetOrdinal("l_municipalid")), Name = rs.GetString(rs.GetOrdinal("sz_name")) });
                }
            }

            return ret;
        }
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class VulnerableSubscriber
    {
        public string Name;

        public List<StreetAddress> StreetAddresses;
        public List<PropertyAddress> PropertyAddresses;
        public List<Endpoint> Endpoints;

        public Category Category;
        public Profession Profession;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class RegistryEntry
    {
        public long Id;
        public bool IsChanged;
        public DateTime? Updated;
        public string Name;                 // company or person
        public string OrganizationNo;       // company only
        public string Address;              // string representation of main address
        public int ZipCode;                 // Zip code for the main address
        public string ZipArea;              // Zip area for the main address
        public AlertTarget AddressDetails;  // alerttarget for main address
        public List<Endpoint> Endpoints;

        public List<ContactPerson> ContactPersons;  // company only
        public List<AlertTarget> AdditionalAddresses;

        // initially only for companies
        public Category Category;
        public Profession Profession;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class ContactPerson
    {
        public long Id;
        public string FirstName;
        public string MiddleName;
        public string LastName;
        public List<Endpoint> Endpoints;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public enum EntryType
    {
        PERSON = 0,
        COMPANY = 1
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AccountInfo
    {
        public List<Municipality> Municipalities;
        public List<MessageProfile> MessageProfiles;
        public List<ConfigurationProfile> ConfigurationProfiles;
        public List<TtsLanguage> TtsLangauges;
        public List<String> SmsOriginators;
        public List<String> VoiceOriginators;
        public List<DataItem> Attribues;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class Municipality
    {
        public int Id;
        public string Name;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class MessageProfile
    {
        public int Id;
        public string Name;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class ConfigurationProfile
    {
        public int Id;
        public string Name;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class Category
    {
        public int Id;
        public string Name;
        public bool HasProfessions;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class Profession
    {
        public int Id;
        public string Name;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AlertTargetData
    {
        public string Name;
        public List<Endpoint> Endpoints;
        public AlertTarget AlertTarget;
    }
}
