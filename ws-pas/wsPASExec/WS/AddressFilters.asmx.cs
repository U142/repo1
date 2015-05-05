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

        [WebMethod]
        public AddressFilterInfo ExecUpdateAddressFilter(FILTEROPERATION operation, ULOGONINFO logon, AddressFilterInfo info)
        {
            return executeUpdateAddressFilter(operation, logon, info);
        }


        [WebMethod]
        public List<AddressFilterInfo> GetListofAddressFilter(ULOGONINFO logon)
        {

            return GettListofAddressFilter(logon);
        }
        [WebMethod]
        public Boolean isAddressFilterNameExists(ULOGONINFO logon, AddressFilterInfo info)
        {
            return true;
        }


        [WebMethod]
        public UAddressList GetActualAddressesOfFilter(ULOGONINFO logon, AddressFilterInfo info)
        {
            return GettActualAddressesOfFilter(logon, info);
        }

        private int getaddresstype(AddressType type)
        {
            int check;
            switch (type)
            {
                case AddressType.StreetAddress:

                    check = 1;
                    break;

                case AddressType.Cadastral_Unit_Norway:

                    check = 2;
                    break;
                case AddressType.Cadastral_Unit_Sweden:
                    check = 3;
                    break;
                case AddressType.VA_banken_Sweden:
                    check = 4;
                    break;
                default:
                    check = 1;
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
                UmsDb db1 = new PASUmsDb(sz_constring, 20000);
                //OdbcConnection conn = new OdbcConnection(sz_constring);
                //conn.Open();
                //db1.SetConn(conn);
                return db1;
            }
            catch (Exception)
            {
                throw;
            }
        }

        private static UmsDb getCorrespondingCountryDataBase(ref ULOGONINFO logon)
        {
            UmsDb db = new UmsDb();
            ULOGONINFO logonInfo = new ULOGONINFO();


            db = new UmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 20000);

            string sql = string.Format("select distinct BC.sz_compid,BD.sz_deptid,BD.sz_password FROM BBCOMPANY BC, BBDEPARTMENT BD ,BBUSER BU WHERE BC.l_comppk=BU.l_comppk and BD.l_deptpk=BU.l_deptpk and BU.sz_userid='{0}' and BU.sz_hash_paspwd='{1}'", logon.sz_userid, logon.sz_password);
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
                    info.addressType = ((info.addressType == null) ? AddressType.StreetAddress : info.addressType);
                   // info.lastupdatedDate = ((info.lastupdatedDate == null) ? System.DateTime.Now : info.lastupdatedDate);

                    switch (operation)
                    {
                        case (FILTEROPERATION.delete):
                            {
                                //  @operation, @deptId ,@filterId int,@filterName, @f_Description, @addressType varchar, @date ,@municipalId,@streetId,@houseNo,@sz_HouseLetter,@sz_ApartmentId,@gno,@bno,@fno,@sno,@se_CadId,@se_VaId  
                                string sql = string.Format("sp_dml_Filters '{0}', '{1}', {2}         , '{3}'     , '{4}'         ,   '{5}'               ,'{6}' ,  {7}       , {8}     ,  {9}   , '{10}'        , '{11}'        ,{12},{13},{14},{15},  {16},  {17},{18}",
                                  FILTEROPERATION.delete.ToString(), logon.sz_deptid, info.filterId, info.filterName, info.description, info.addressType, System.DateTime.Now, temp, temp, temp, temp, temp, temp, temp, temp, temp, temp, temp,temp);
                                db.CreateCommand(sql, 1);
                                check = db.ExecCommand();
                                break;
                            }

                        case (FILTEROPERATION.insert):
                            {
                                string sql = String.Format("sp_Insert_Address_Filters '{0}', '{1}', '{2}' , '{3}'  , '{4}' ",
                                                             logon.sz_deptid, info.filterName, info.description, info.addressType, System.DateTime.Now);
                                db.CreateCommand(sql, 1);
                                db.ExecCommand();

                               
                                int filterid=0;
                                DateTime date = new DateTime();
                                sql = string.Format(" select FilterId,LastUpdatedDate from Address_Filters  where DeptId='{0}' and FilterName='{1}'", logon.sz_deptid, info.filterName);
                                db.CreateCommand(sql, 1);
                                OdbcDataReader reader = db.ExecCommandReader();
                                if (reader.Read())
                                {
                                    filterid = reader.GetInt32(reader.GetOrdinal("FilterId"));
                                    date = reader.GetDateTime(reader.GetOrdinal("LastUpdatedDate"));
                                }
                               // int filterid = db.ExecScalarcommand(sql);
                                if (filterid != 0)
                                {
                                    for (int i = 0; i < info.addressForFilterlist.Count; i++)
                                    {


                                        info.addressForFilterlist[i].szHouseLetter = ((info.addressForFilterlist[i].szHouseLetter == null) ? temp : info.addressForFilterlist[i].szHouseLetter);

                                        info.addressForFilterlist[i].szApartmentId = ((info.addressForFilterlist[i].szApartmentId == null) ? temp : info.addressForFilterlist[i].szApartmentId);


                                        //  @operation, @deptId ,@filterId int,@filterName, @f_Description, @addressType varchar, @date ,@municipalId,@streetId,@houseNo,@sz_HouseLetter,@sz_ApartmentId,@gno,@bno,@fno,@sno,@se_CadId,@se_VaId  
                                        sql = String.Format("sp_dml_Filters '{0}', '{1}', {2}         , '{3}'     , '{4}'         ,   '{5}'               ,'{6}' ,  {7}       , {8}     ,  {9}   , '{10}'        , '{11}'        ,{12},{13},{14},{15},  {16},  {17},{18}",
                                                                operation.ToString().ToLower(), logon.sz_deptid, filterid, info.filterName, info.description, info.addressType, System.DateTime.Now, info.addressForFilterlist[i].municipalId, info.addressForFilterlist[i].streetId,
                                                                info.addressForFilterlist[i].houseNo, info.addressForFilterlist[i].szHouseLetter, info.addressForFilterlist[i].szApartmentId, info.addressForFilterlist[i].gnrNumber, info.addressForFilterlist[i].bnrNumber, info.addressForFilterlist[i].fnrNumber,
                                                                info.addressForFilterlist[i].snrNumber, info.addressForFilterlist[i].unrNumber, info.addressForFilterlist[i].seCadId, info.addressForFilterlist[i].seVaId);

                                        db.CreateCommand(sql, 1);
                                        db.ExecCommand();

                                    }
                                    check = info.addressForFilterlist.Count;
                                }
                               
                                info.filterId = filterid;
                                info.lastupdatedDate = date;
                                break;
                            }
                        case (FILTEROPERATION.update):
                            {
                                string sql = string.Format("sp_dml_Filters '{0}', '{1}', {2}         , '{3}'     , '{4}'         ,   '{5}'               ,'{6}' ,  {7}       , {8}     ,  {9}   , '{10}'        , '{11}'        ,{12},{13},{14},{15},  {16},  {17},{18}",
                                   FILTEROPERATION.update.ToString(), logon.sz_deptid, info.filterId, info.filterName, info.description, info.addressType, System.DateTime.Now, temp, temp, temp, temp, temp, temp, temp, temp, temp, temp, temp,temp);
                                db.CreateCommand(sql, 1);
                                int inserted = db.ExecCommand();
                                if (inserted!=0)
                                {
                                    for (int i = 0; i < info.addressForFilterlist.Count; i++)
                                    {
                                        sql = String.Format("sp_dml_Filters '{0}', '{1}', {2}         , '{3}'     , '{4}'         ,   '{5}'               ,'{6}' ,  {7}       , {8}     ,  {9}   , '{10}'        , '{11}'        ,{12},{13},{14},{15},  {16},  {17},{18}",
                                                                 FILTEROPERATION.insert.ToString().ToLower(), logon.sz_deptid, info.filterId, info.filterName, info.description, info.addressType, System.DateTime.Now, info.addressForFilterlist[i].municipalId, info.addressForFilterlist[i].streetId,
                                                                info.addressForFilterlist[i].houseNo, info.addressForFilterlist[i].szHouseLetter, info.addressForFilterlist[i].szApartmentId, info.addressForFilterlist[i].gnrNumber, info.addressForFilterlist[i].bnrNumber, info.addressForFilterlist[i].fnrNumber,
                                                                info.addressForFilterlist[i].snrNumber, info.addressForFilterlist[i].unrNumber, info.addressForFilterlist[i].seCadId, info.addressForFilterlist[i].seVaId);

                                        db.CreateCommand(sql, 1);
                                        db.ExecCommand();

                                    }
                                    check = info.addressForFilterlist.Count;
                                }
                                break;
                            }
                    }
                }
                else
                {
                    throw new Exception("deptid and filtername must be provided");
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
                    // info.AddressType = getaddresstype(reader.GetOrdinal("AddressType"));
                    list.Add(info);
                }
            }
            return list;
        }



        //public Boolean isAddressFilterNameExists(ULOGONINFO logon, AddressFilterInfo info)
        //{
        //    UmsDb db = getCorrespondingCountryDataBase(ref logon);
        //    db.CheckDepartmentLogonLiteral(ref logon);
        //    string sql = string.Format("select * from Filterinfo where DeptId={0} and FilterName={1}", logon.l_deptpk,info.FilterName);

        //    OdbcDataReader reader = db.ExecCommandReader();
        //    if (reader.HasRows)
        //    {
        //        return true;
        //    }
        //    else { return false; }
        //}



        private UAddressList GettActualAddressesOfFilter(ULOGONINFO logon, AddressFilterInfo info)
        {    

         
 
            UAddressList list = new UAddressList();
            try
            {
           
            UmsDb db = getCorrespondingCountryDataBase(ref logon);

            string sql = string.Format("SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(NAVN, ' ') NAVN, isnull(ADRESSE, ' ')ADRESSE, isnull(HUSNR, 0) HUSNR, AK.sz_apartmentid,"
+ " isnull(OPPGANG, '') OPPGANG, isnull(POSTNR, '0')POSTNR, isnull(POSTSTED, '')POSTSTED, isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0')FØDTÅR, isnull(TELEFON, '')TELEFON, isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT,"
+ " isnull(l_importid, -1) l_importid, isnull(MOBIL, '')MOBIL, isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') XY_KODE, isnull(f_hasfixed, 0)f_hasfixed, isnull(f_hasmobile,0)f_hasmobile, isnull(f_hasdisabled,0) f_hasdisabled  FROM"
+ " ADR_KONSUM AK INNER JOIN Address_Filters AF ON AF.FilterId in({0})"
+ " INNER JOIN ADDRESSASSOCIATEDwithFILTER AAF ON"
+ " AF.FilterId = AAF.FilterId"
+ " AND AK.KOMMUNENR = AAF.municipalId"
+ " AND AK.GATEKODE = AAF.StreetId"
+ " AND AK.HUSNR = isnull(AAF.HouseNo, AK.HUSNR)"
+ " AND AK.sz_apartmentid = isnull(AAF.Sz_ApartmentId, AK.sz_apartmentid)"
+ " AND AK.OPPGANG = isnull(AAF.Sz_HouseLetter, AK.OPPGANG)"
+ " WHERE BEDRIFT IN (0,1) order by"
+ " GATEKODE, HUSNR", info.filterId);
               
            db.CreateCommand(sql, 1);
                OdbcDataReader reader = db.ExecCommandReader();
                while (reader.Read())
                {
                    UAddress address = new UAddress();
                    var r = reader["LAT"].GetType();
                      address.address = reader.GetString(reader.GetOrdinal("ADRESSE"));
                    
                    address.bedrift = reader.GetInt16(reader.GetOrdinal("BEDRIFT"));
                    address.bno = reader.GetInt32(reader.GetOrdinal("BNR"));
                    address.gno = reader.GetInt32(reader.GetOrdinal("GNR"));
                    address.hasfixed = reader.GetInt16(reader.GetOrdinal("f_hasfixed"));
                    
                    address.hasmobile = reader.GetInt16(reader.GetOrdinal("f_hasmobile"));
                    address.mobile = reader.GetString(reader.GetOrdinal("MOBIL"));
                    address.municipalid = Convert.ToString(reader.GetInt32(reader.GetOrdinal("KOMMUNENR")));
                    address.lat = reader.GetDouble(reader.GetOrdinal("LAT"));
                    address.lon = reader.GetDouble(reader.GetOrdinal("LON"));
                    address.mobile = reader.GetString(reader.GetOrdinal("MOBIL"));
                    address.name = reader.GetString(reader.GetOrdinal("NAVN"));
                    address.postno = reader.GetString(reader.GetOrdinal("POSTNR"));
                    address.postarea = reader.GetString(reader.GetOrdinal("POSTSTED"));
                    address.streetid = reader.GetInt32(reader.GetOrdinal("GATEKODE"));
                    address.xycode = reader.GetString(reader.GetOrdinal("XY_KODE"));
                    address.importid = reader.GetInt32(reader.GetOrdinal("l_importid"));
                    list.addLine(address);
                }

                
            }
            catch (Exception ex)
            {
                ex.ToString();
            }
            return list;
        }
    }
}
