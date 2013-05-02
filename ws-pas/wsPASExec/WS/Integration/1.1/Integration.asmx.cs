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
        /// Get a list of vulnerable subscribers for a set of municipalities. Can filter by vulnerability code and company category
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
            string SqlCompanyCategories = Professions != null && Professions.Count > 0 ? String.Format("AND SO.l_profession IN ({0})", String.Join(",", Professions.Select(category => category.ToString()).ToArray())) : "";

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
                            order by ID"
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
    public class Category
    {
        public int Id;
        public string Name;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class Profession
    {
        public int Id;
        public string Name;
    }
}
