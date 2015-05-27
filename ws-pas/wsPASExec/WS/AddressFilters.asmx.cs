using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using com.ums.UmsCommon;
using com.ums.UmsDbLib;
using System.Data.Odbc;
using com.ums.address;
using System.Configuration;
using com.ums.UmsParm;
using com.ums.PAS.Address;

namespace com.ums.ws.addressfilters
{
    [WebService(Namespace = "http://ums.no/ws/addressfilters/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class AddressFilters : System.Web.Services.WebService
    {
        AddressFilterInfo info = new AddressFilterInfo();
        /// <summary>
        /// service is responsible for insert ,update,delete opration of filters
        /// </summary>
        /// <param name="operation"></param>
        /// <param name="logon"></param>
        /// <param name="info"></param>
        /// <returns></returns>
        [WebMethod]
        public AddressFilterInfo ExecUpdateAddressFilter(FILTEROPERATION operation, ULOGONINFO logon, AddressFilterInfo info)
        {
            return executeUpdateAddressFilter(operation, logon, info);
        }

        /// <summary>
        /// this service is responsible for getting list of filters on the basis of user's DeptID and Stdcc
        /// </summary>
        /// <param name="logon"></param>
        /// <returns></returns>
        [WebMethod]
        public List<AddressFilterInfo> GetListofAddressFilter(ULOGONINFO logon)
        {

            return GettListofAddressFilter(logon);
        }

        /// <summary>
        /// this method will retuen the actual list of address with full information form repective countries' database
        /// </summary>
        /// <param name="logon"></param>
        /// <param name="info"></param>
        /// <returns></returns>
        [WebMethod]
        public UAddressList GetActualAddressesOfFilter(ULOGONINFO logon, AddressFilterInfo info)
        {
            return GettActualAddressesOfFilter(logon, info);
        }
        /// <summary>
        /// this method will check if the filtername is already exist in DB in respective Dept. but this method is not used.
        /// </summary>
        /// <param name="logon"></param>
        /// <param name="info"></param>
        /// <returns></returns>
        [WebMethod]
        public Boolean isAddressFilterNameExists(ULOGONINFO logon, AddressFilterInfo info)
        {
            return isAddressFilterNameExistCheck(logon, info);
        }

        private AddressType getaddresstype(string type)
        {
            AddressType check;
            switch (type)
            {
                case "StreetAddress":

                    check = AddressType.StreetAddress;
                    break;

                case "Cadastral_Unit_Norway":

                    check = AddressType.Cadastral_Unit_Norway;
                    break;
                case "Cadastral_Unit_Sweden":
                    check = AddressType.Cadastral_Unit_Sweden;
                    break;
                case "VA_banken_Sweden":
                    check = AddressType.VA_banken_Sweden;
                    break;
                default:
                    check = AddressType.StreetAddress;
                    break;
            }
            return check;
        }


        private static UmsDb GetDatabaseInstance(string sz_stdcc, int n_deptpk, int timeout)
        {
            string sz_constring;
            int m_n_pastype;    //0=no db rights, 1=normal address, 2=folkereg address         

            if (!UCommon.USETTINGS.b_enable_adrdb)
                throw new UServerDeniedAddressDatabaseException();

            try
            {
                PASUmsDb db = new PASUmsDb();
                m_n_pastype = db.GetPasType(n_deptpk);
                if (m_n_pastype <= 0)
                    throw new ULogonFailedException();

                String dsn = "address_" + sz_stdcc;
                if (m_n_pastype == 2)
                    dsn += "_reg";
                sz_constring = ConfigurationManager.ConnectionStrings[dsn].ConnectionString;
                db.close();
                UmsDb db1 = new PASUmsDb(sz_constring, 200);
                return db1;
            }
            catch (Exception)
            {
                throw;
            }
        }

        private static UmsDb getCorrespondingCountryDataBase(ref ULOGONINFO logon)
        {
            try
            {
                UmsDb db = new UmsDb();
                ULOGONINFO logonInfo = new ULOGONINFO();
               

                db = new UmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 2000);

                string sql = string.Format("select distinct BC.sz_compid,BD.sz_deptid,BD.sz_password FROM BBCOMPANY BC, BBDEPARTMENT BD ,BBUSER BU WHERE BC.l_comppk=BU.l_comppk and BD.l_deptpk=BU.l_deptpk and BU.l_userpk='{0}' and BU.sz_hash_paspwd='{1}'", logon.l_userpk, logon.sz_password);
                db.CreateCommand(sql, 1);
                OdbcDataReader reader = db.ExecCommandReader();
                while (reader.Read())
                {
                    logon.sz_compid = reader.GetString(reader.GetOrdinal("sz_compid"));
                    logon.sz_deptid = reader.GetString(reader.GetOrdinal("sz_deptid"));
                    logon.sz_password = reader.GetString(reader.GetOrdinal("sz_password"));

                }
                db.CheckDepartmentLogonLiteral(ref logon);


                db = GetDatabaseInstance(logon.sz_stdcc, logon.l_deptpk, 2000);
                return db;
            }catch(Exception )
            {
                throw;
            }
        }

        private AddressFilterInfo executeUpdateAddressFilter(FILTEROPERATION operation, ULOGONINFO logon, AddressFilterInfo info)
        {
            UmsDb db = getCorrespondingCountryDataBase(ref logon);
            int check;
            string temp = "null";


            List<AddressAssociatedWithFilter> addresses = info.addressForFilterlist;

            try
            {
                if (info.filterName != null)
                {
                    info.description = ((info.description == null) ? temp : info.description);



                    switch (operation)
                    {
                        case (FILTEROPERATION.delete):
                            {
                                
                                //string sql = string.Format("sp_dml_Filters '{0}', '{1}', {2}         , '{3}'     , '{4}'         ,   '{5}'               ,'{6}'", FILTEROPERATION.delete.ToString(), logon.sz_deptid, info.filterId, info.filterName, info.description, info.addressType, System.DateTime.Now);
                                //db.CreateCommand(sql, 1);
                                //check = db.ExecCommand();
                                string sql = "sp_dml_Filters ?,?,?,?,?,?,?";
                                using (OdbcCommand cmd = db.CreateCommand(sql, 1))
                                {
                                    cmd.Parameters.Clear();
                                    cmd.Parameters.Add("operation", OdbcType.VarChar).Value = FILTEROPERATION.delete.ToString();
                                    cmd.Parameters.Add("deptId", OdbcType.VarChar).Value = logon.sz_deptid;
                                    cmd.Parameters.Add("filterId", OdbcType.VarChar).Value = info.filterId;
                                    cmd.Parameters.Add("filterName", OdbcType.VarChar).Value = info.filterName;
                                    cmd.Parameters.Add("f_Description", OdbcType.VarChar).Value = info.description;
                                    cmd.Parameters.Add("addressType", OdbcType.VarChar).Value = info.addressType;
                                    cmd.Parameters.Add("date", OdbcType.DateTime).Value = DateTime.Now;
                                    cmd.ExecuteNonQuery();

                                }
                                break;
                            }

                        case (FILTEROPERATION.update):
                            {
                                DateTime date = DateTime.Now;
                                 
                                string sql = "sp_dml_Filters ?,?,?,?,?,?,?";
                                using (OdbcCommand cmd = db.CreateCommand(sql, 1))
                                {
                                    cmd.Parameters.Clear();
                                    cmd.Parameters.Add("operation", OdbcType.VarChar).Value = FILTEROPERATION.update.ToString();
                                    cmd.Parameters.Add("deptId", OdbcType.VarChar).Value = logon.sz_deptid;
                                    cmd.Parameters.Add("filterId", OdbcType.VarChar).Value = info.filterId;
                                    cmd.Parameters.Add("filterName", OdbcType.VarChar).Value = info.filterName;
                                    cmd.Parameters.Add("f_Description", OdbcType.VarChar).Value = info.description;
                                    cmd.Parameters.Add("addressType", OdbcType.VarChar).Value = info.addressType;
                                    cmd.Parameters.Add("date", OdbcType.DateTime).Value = date;
                                    cmd.ExecuteNonQuery();
                                     
                                }

                                

                                if (info.addressForFilterlist.Count > 0)
                                {
                                     

                                    sql = "delete from ADDRESSASSOCIATEDwithFILTER where FilterId=?";
                                    using (OdbcCommand cmd = db.CreateCommand(sql, 1))
                                    {
                                        cmd.Parameters.Clear();
                                        cmd.Parameters.Add("filterid", OdbcType.Int).Value = info.filterId;
                                        check = cmd.ExecuteNonQuery();
                                    }
                                    if (check >= 0)
                                    {
                                        sql = "insert into ADDRESSASSOCIATEDwithFILTER values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
                                        for (int i = 0; i < info.addressForFilterlist.Count; i++)
                                        {
                                            using (OdbcCommand cmd = db.CreateCommand(sql, 1))
                                            {
                                                cmd.Parameters.Clear();
                                                cmd.Parameters.Add("filterid", OdbcType.Int).Value = info.filterId;
                                                cmd.Parameters.Add("MunicipalId", OdbcType.Int).Value = info.addressForFilterlist[i].municipalId;
                                                cmd.Parameters.Add("StreetId", OdbcType.Int).Value = info.addressForFilterlist[i].streetId;
                                                cmd.Parameters.Add("HouseNo", OdbcType.Int).Value = info.addressForFilterlist[i].houseNo;
                                                cmd.Parameters.Add("Sz_HouseLetter", OdbcType.VarChar).Value = ((info.addressForFilterlist[i].szHouseLetter == "") ? (object)DBNull.Value : info.addressForFilterlist[i].szHouseLetter);
                                                cmd.Parameters.Add("Sz_ApartmentId", OdbcType.VarChar).Value = ((info.addressForFilterlist[i].szApartmentId == "") ? (object)DBNull.Value : info.addressForFilterlist[i].szApartmentId);
                                                cmd.Parameters.Add("Gno", OdbcType.Int).Value = info.addressForFilterlist[i].gnrNumber;
                                                cmd.Parameters.Add("Bno", OdbcType.Int).Value = info.addressForFilterlist[i].bnrNumber;
                                                cmd.Parameters.Add("Fno", OdbcType.Int).Value = info.addressForFilterlist[i].fnrNumber;
                                                cmd.Parameters.Add("Sno", OdbcType.Int).Value = info.addressForFilterlist[i].snrNumber;
                                                cmd.Parameters.Add("Uno", OdbcType.Int).Value = info.addressForFilterlist[i].unrNumber;
                                                cmd.Parameters.Add("Se_CadId", OdbcType.Int).Value = info.addressForFilterlist[i].seCadId;
                                                cmd.Parameters.Add("Se_VaId", OdbcType.Int).Value = info.addressForFilterlist[i].seVaId;
                                                cmd.ExecuteNonQuery();
                                            }
                                        }
                                    }

                                }
                                info.lastupdatedDate = date;
                                break;
                            }




                            case (FILTEROPERATION.insert):
                            {
                                DateTime date = DateTime.Now;
                                int filterid = 0;
                                
                                string sql = "sp_dml_Filters ?,?,?,?,?,?,?";
                                using (OdbcCommand cmd = db.CreateCommand(sql, 1))
                                {
                                    cmd.Parameters.Clear();
                                    cmd.Parameters.Add("operation", OdbcType.VarChar).Value = FILTEROPERATION.insert;
                                    cmd.Parameters.Add("deptId", OdbcType.VarChar).Value = logon.sz_deptid;
                                    cmd.Parameters.Add("filterId", OdbcType.VarChar).Value = info.filterId;
                                    cmd.Parameters.Add("filterName", OdbcType.VarChar).Value = info.filterName;
                                    cmd.Parameters.Add("f_Description", OdbcType.VarChar).Value = info.description;
                                    cmd.Parameters.Add("addressType", OdbcType.VarChar).Value = info.addressType;
                                    cmd.Parameters.Add("date", OdbcType.DateTime).Value = date;

                                    var id = cmd.ExecuteScalar();
                                    filterid = Convert.ToInt32(id);
                                }
                                                               
                                 if (info.addressForFilterlist.Count > 0)
                                {                                                                                                         
                                        sql = "insert into ADDRESSASSOCIATEDwithFILTER values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
                                        for (int i = 0; i < info.addressForFilterlist.Count; i++)
                                        {
                                            using (OdbcCommand cmd = db.CreateCommand(sql, 1))
                                            {
                                                cmd.Parameters.Clear();
                                                cmd.Parameters.Add("filterid", OdbcType.Int).Value =filterid;
                                                cmd.Parameters.Add("MunicipalId", OdbcType.Int).Value = info.addressForFilterlist[i].municipalId;
                                                cmd.Parameters.Add("StreetId", OdbcType.Int).Value = info.addressForFilterlist[i].streetId;
                                                cmd.Parameters.Add("HouseNo", OdbcType.Int).Value = info.addressForFilterlist[i].houseNo;
                                                cmd.Parameters.Add("Sz_HouseLetter", OdbcType.VarChar).Value = ((info.addressForFilterlist[i].szHouseLetter == "") ? (object)DBNull.Value : info.addressForFilterlist[i].szHouseLetter);
                                                cmd.Parameters.Add("Sz_ApartmentId", OdbcType.VarChar).Value = ((info.addressForFilterlist[i].szApartmentId == "") ? (object)DBNull.Value : info.addressForFilterlist[i].szApartmentId);
                                                cmd.Parameters.Add("Gno", OdbcType.Int).Value = info.addressForFilterlist[i].gnrNumber;
                                                cmd.Parameters.Add("Bno", OdbcType.Int).Value = info.addressForFilterlist[i].bnrNumber;
                                                cmd.Parameters.Add("Fno", OdbcType.Int).Value = info.addressForFilterlist[i].fnrNumber;
                                                cmd.Parameters.Add("Sno", OdbcType.Int).Value = info.addressForFilterlist[i].snrNumber;
                                                cmd.Parameters.Add("Uno", OdbcType.Int).Value = info.addressForFilterlist[i].unrNumber;
                                                cmd.Parameters.Add("Se_CadId", OdbcType.Int).Value = info.addressForFilterlist[i].seCadId;
                                                cmd.Parameters.Add("Se_VaId", OdbcType.Int).Value = info.addressForFilterlist[i].seVaId;
                                                cmd.ExecuteNonQuery();
                                            }
                                        }
                                    }

                                info.filterId = filterid;
                                info.lastupdatedDate = date;
                                break;
                            }
                    }
                }
            }
            catch (Exception e)
            {
                e.ToString();
            }
            return info;

        }


        private List<AddressFilterInfo> GettListofAddressFilter(ULOGONINFO logon)
        {
            List<AddressFilterInfo> list = new List<AddressFilterInfo>();
            UmsDb db = getCorrespondingCountryDataBase(ref logon);

            string sql = string.Format("select * from ADDRESS_FILTERS where DeptId='{0}'", logon.sz_deptid);
            db.CreateCommand(sql, 1);
            if (logon.sz_deptid != null)
            {
                OdbcDataReader reader = db.ExecCommandReader();

                while (reader.Read())
                {
                    AddressFilterInfo info = new AddressFilterInfo();
                    info.filterId = reader.GetInt32(reader.GetOrdinal("FilterId"));
                    info.filterName = reader.GetString(reader.GetOrdinal("FilterName"));
                    info.lastupdatedDate = reader.GetDateTime(reader.GetOrdinal("LastUpdatedDate"));
                    info.description = reader.GetString(reader.GetOrdinal("F_Description"));
                    info.addressType = getaddresstype(reader.GetString(reader.GetOrdinal("AddressType")));
                    list.Add(info);
                }
            }
            return list;
        }



        private Boolean isAddressFilterNameExistCheck(ULOGONINFO logon, AddressFilterInfo info)
        {
            UmsDb db = getCorrespondingCountryDataBase(ref logon);
            db.CheckDepartmentLogonLiteral(ref logon);
            string sql = string.Format("select * from Filterinfo where DeptId={0} and FilterName={1}", logon.sz_deptid, info.filterName);

            OdbcDataReader reader = db.ExecCommandReader();
            if (reader.HasRows)
            {
                return true;
            }
            else { return false; }
        }



        private UAddressList GettActualAddressesOfFilter(ULOGONINFO logon, AddressFilterInfo info)
        {
            UmsDb db = null;
            OdbcDataReader reader = null;
            string sql;
 
            UAddressList list = new UAddressList();
            try
            {
                 db = getCorrespondingCountryDataBase(ref logon);
                //if database is of sweden
                 if (logon.sz_stdcc == "0046")
                 {
                      sql = string.Format("SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(NAVN, ' ') NAVN, isnull(ADRESSE, ' ')ADRESSE, isnull(HUSNR, 0) HUSNR, AK.sz_apartmentid,"
                + " isnull(OPPGANG, '') OPPGANG, isnull(POSTNR, '0')POSTNR, isnull(POSTSTED, '')POSTSTED, isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0')FØDTÅR, isnull(TELEFON, '')TELEFON, isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT,"
                + " isnull(l_importid, -1) l_importid, isnull(MOBIL, '')MOBIL, isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') XY_KODE, isnull(f_hasfixed, 0)f_hasfixed, isnull(f_hasmobile,0)f_hasmobile, isnull(f_hasdisabled,0) f_hasdisabled  FROM"
                + " ADR_KONSUM AK INNER JOIN Address_Filters AF ON AF.FilterId in({0})"
                + " INNER JOIN ADDRESSASSOCIATEDwithFILTER AAF ON"
                + " AF.FilterId = AAF.FilterId"
                + " AND AK.KOMMUNENR = AAF.municipalId"
                + " AND AK.GATEKODE = CASE WHEN (AAF.Se_CadId=0) THEN AK.GATEKODE ELSE AAF.Se_CadId END"
                + " AND AK.GATEKODE = CASE WHEN (AAF.Se_VaId=0) THEN AK.GATEKODE ELSE AAF.Se_VaId END"
                + " WHERE BEDRIFT IN (0,1) order by"
                + " GATEKODE, HUSNR", info.filterId);
                 }
                 else //if datavase is other than sweden
                 {
                        sql = string.Format("SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(NAVN, ' ') NAVN, isnull(ADRESSE, ' ')ADRESSE, isnull(HUSNR, 0) HUSNR, AK.sz_apartmentid,"
               + " isnull(OPPGANG, '') OPPGANG, isnull(POSTNR, '0')POSTNR, isnull(POSTSTED, '')POSTSTED, isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0')FØDTÅR, isnull(TELEFON, '')TELEFON, isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT,"
               + " isnull(l_importid, -1) l_importid, isnull(MOBIL, '')MOBIL, isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') XY_KODE, isnull(f_hasfixed, 0)f_hasfixed, isnull(f_hasmobile,0)f_hasmobile, isnull(f_hasdisabled,0) f_hasdisabled  FROM"
               + " ADR_KONSUM AK INNER JOIN Address_Filters AF ON AF.FilterId in({0})"
               + " INNER JOIN ADDRESSASSOCIATEDwithFILTER AAF ON"
               + " AF.FilterId = AAF.FilterId"
               + " AND AK.KOMMUNENR = AAF.municipalId"
               + " AND AK.GATEKODE = AAF.StreetId"
               + " AND AK.sz_apartmentid = isnull(AAF.Sz_ApartmentId, AK.sz_apartmentid)"
               + " AND AK.OPPGANG = isnull(AAF.Sz_HouseLetter, AK.OPPGANG)"
               + " AND AK.HUSNR = CASE WHEN(AAF.HouseNo=0) THEN AK.HUSNR ELSE AAF.HouseNo END"
               + " AND AK.BNR = CASE WHEN (AAF.Bno=0) THEN AK.BNR ELSE AAF.Bno END"
               + " AND AK.FNR = CASE WHEN (AAF.Fno=0) THEN AK.FNR ELSE AAF.Fno END"
               + " AND AK.SNR = CASE WHEN (AAF.Sno=0) THEN AK.SNR ELSE AAF.Sno END"
               + " AND AK.UNR = CASE WHEN (AAF.Uno=0) THEN AK.UNR ELSE AAF.Uno END"
               + " WHERE BEDRIFT IN (0,1) order by"
               + " GATEKODE, HUSNR", info.filterId);
                 }

                db.CreateCommand(sql, 1);
                reader = db.ExecCommandReader();
                while (reader.Read())
                {
                    UAddress address = new UAddress();
                    address.address = reader.GetString(reader.GetOrdinal("ADRESSE"));
                    address.bedrift = reader.GetInt16(reader.GetOrdinal("BEDRIFT"));
                    address.bno = reader.GetInt32(reader.GetOrdinal("BNR"));
                    address.gno = reader.GetInt32(reader.GetOrdinal("GNR"));
                    address.hasdisabled =Convert.ToInt32(reader.GetByte(reader.GetOrdinal("f_hasdisabled")));
                    address.hasfixed = Convert.ToInt32(reader.GetByte(reader.GetOrdinal("f_hasfixed")));
                    address.hasmobile = Convert.ToInt32(reader.GetByte(reader.GetOrdinal("f_hasmobile")));
                    address.mobile = reader.GetString(reader.GetOrdinal("MOBIL"));
                    address.municipalid = Convert.ToString(reader.GetInt32(reader.GetOrdinal("KOMMUNENR")));
                    address.lat = reader.GetDouble(reader.GetOrdinal("LAT"));
                    address.lon = reader.GetDouble(reader.GetOrdinal("LON"));
                    address.mobile = reader.GetString(reader.GetOrdinal("MOBIL"));
                    address.name = reader.GetString(reader.GetOrdinal("NAVN"));
                    address.houseno = reader.GetInt32(reader.GetOrdinal("HUSNR"));
                    address.postno = reader.GetString(reader.GetOrdinal("POSTNR"));
                    address.postarea = reader.GetString(reader.GetOrdinal("POSTSTED"));
                    address.streetid = reader.GetInt32(reader.GetOrdinal("GATEKODE"));
                    address.xycode = reader.GetString(reader.GetOrdinal("XY_KODE"));
                    address.importid = reader.GetInt32(reader.GetOrdinal("l_importid"));

                    UAddressBasics basic = new UAddressBasics();
                    basic.bedrift=reader.GetInt16(reader.GetOrdinal("BEDRIFT"));
                    basic.hasfixed = Convert.ToInt32(reader.GetByte(reader.GetOrdinal("f_hasfixed")));
                    basic.hasmobile = Convert.ToInt32(reader.GetByte(reader.GetOrdinal("f_hasmobile")));
                    basic.hasdisabled = Convert.ToInt32(reader.GetByte(reader.GetOrdinal("f_hasdisabled")));
                    basic.kondmid = Convert.ToString(reader.GetInt32(reader.GetOrdinal("KON_DMID")));
                    basic.lat=reader.GetDouble(reader.GetOrdinal("LAT"));
                    basic.lon = reader.GetDouble(reader.GetOrdinal("LON"));
                    list.addLine(address);
                    list.addLine(ref basic);
                }

               }
            catch (Exception ex)
            {
                ex.ToString();
            }
                
            finally 
            {
                if (reader != null)
                    reader.Close();
                if (db != null)
                    db.close();
                 
            }
            return list;
            
        }
    }
}
