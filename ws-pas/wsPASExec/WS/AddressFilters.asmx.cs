using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;

namespace wsPASExec.WS
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
        public AddressFilterInfo ExecUpdateAddressFilter(PARMOPERATION operation, ULOGONINFO logon, AddressFilterInfo info)
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
        public List<AddressInfo> GetActualAddressesOfFilter(ULOGONINFO logon, AddressFilterInfo info)
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

        private AddressFilterInfo executeUpdateAddressFilter(PARMOPERATION operation, ULOGONINFO logon, AddressFilterInfo info)
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
                    info.lastupdatedDate = ((info.lastupdatedDate == null) ? System.DateTime.Now : info.lastupdatedDate);

                    switch (operation)
                    {
                        case (PARMOPERATION.delete):
                            {
                                //  @operation, @deptId ,@filterId int,@filterName, @f_Description, @addressType varchar, @date ,@municipalId,@streetId,@houseNo,@sz_HouseLetter,@sz_ApartmentId,@gno,@bno,@fno,@sno,@se_CadId,@se_VaId  
                                string sql = string.Format("sp_dml_Filters '{0}', '{1}', {2}         , '{3}'     , '{4}'         ,   '{5}'               ,'{6}' ,  {7}       , {8}     ,  {9}   , '{10}'        , '{11}'        ,{12},{13},{14},{15},  {16},  {17}",
                                  PARMOPERATION.delete.ToString(), logon.sz_deptid, info.filterId, info.filterName, info.description, info.addressType, System.DateTime.Now, temp, temp, temp, temp, temp, temp, temp, temp, temp, temp, temp);
                                db.CreateCommand(sql, 1);
                                check = db.ExecCommand();
                                break;
                            }

                        case (PARMOPERATION.insert):
                            {
                                for (int i = 0; i < info.addressForFilterlist.Count; i++)
                                {


                                    info.addressForFilterlist[i].szHouseLetter = ((info.addressForFilterlist[i].szHouseLetter == null) ? temp : info.addressForFilterlist[i].szApartmentId);
                                    info.addressForFilterlist[i].szApartmentId = ((info.addressForFilterlist[i].szApartmentId == null) ? temp : info.addressForFilterlist[i].szApartmentId);


                                    //  @operation, @deptId ,@filterId int,@filterName, @f_Description, @addressType varchar, @date ,@municipalId,@streetId,@houseNo,@sz_HouseLetter,@sz_ApartmentId,@gno,@bno,@fno,@sno,@se_CadId,@se_VaId  
                                    string sql = String.Format("sp_dml_Filters '{0}', '{1}', {2}         , '{3}'     , '{4}'         ,   '{5}'               ,'{6}' ,  {7}       , {8}     ,  {9}   , '{10}'        , '{11}'        ,{12},{13},{14},{15},  {16},  {17}",
                                                             operation.ToString().ToLower(), logon.sz_deptid, info.filterId, info.filterName, info.description, info.addressType, info.lastupdatedDate, info.addressForFilterlist[i].municipalId, info.addressForFilterlist[i].streetId,
                                                             info.addressForFilterlist[i].houseNo, info.addressForFilterlist[i].szHouseLetter, info.addressForFilterlist[i].szApartmentId, info.addressForFilterlist[i].gnrNumber, info.addressForFilterlist[i].bnrNumber, info.addressForFilterlist[i].fnrNumber,
                                                             info.addressForFilterlist[i].snrNumber, info.addressForFilterlist[i].seCadId, info.addressForFilterlist[i].seVaId);

                                    db.CreateCommand(sql, 1);
                                    db.ExecCommand();

                                }
                                check = info.addressForFilterlist.Count;
                                break;
                            }
                        case (PARMOPERATION.update):
                            {
                                string sql = string.Format("sp_dml_Filters '{0}', '{1}', {2}         , '{3}'     , '{4}'         ,   '{5}'               ,'{6}' ,  {7}       , {8}     ,  {9}   , '{10}'        , '{11}'        ,{12},{13},{14},{15},  {16},  {17}",
                                   PARMOPERATION.update.ToString(), logon.sz_deptid, info.filterId, info.filterName, info.description, info.addressType, System.DateTime.Now, temp, temp, temp, temp, temp, temp, temp, temp, temp, temp, temp);
                                db.CreateCommand(sql, 1);
                                int inserted = db.ExecCommand();
                                if (inserted == 1)
                                {
                                    for (int i = 0; i < info.addressForFilterlist.Count; i++)
                                    {
                                        sql = String.Format("sp_dml_Filters '{0}', {1}, {2}, {3}, {4}, '{5}', {6}, {7}, '{8}', {9}, {10}, {11}, {12}, {13}, {14}, {15}, '{16}', '{17}', '{18}', {19}",
                                                                 PARMOPERATION.insert.ToString(), info.deptId, info.filterName, info.description, info.addressType, info.addressForFilterlist[i].municipalId, info.addressForFilterlist[i].streetId,
                                                             info.addressForFilterlist[i].houseNo, info.addressForFilterlist[i].szHouseLetter, info.addressForFilterlist[i].szApartmentId, info.addressForFilterlist[i].gnrNumber, info.addressForFilterlist[i].bnrNumber, info.addressForFilterlist[i].fnrNumber,
                                                             info.addressForFilterlist[i].snrNumber, info.addressForFilterlist[i].seCadId, info.addressForFilterlist[i].seVaId);

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
            return new AddressFilterInfo(info.filterName);

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



        private List<AddressInfo> GettActualAddressesOfFilter(ULOGONINFO logon, AddressFilterInfo info)
        {

            List<AddressInfo> addresses = new List<AddressInfo>();
            int? FilterId = null;
            UmsDb db = getCorrespondingCountryDataBase(ref logon);
            db.CheckDepartmentLogonLiteral(ref logon);
            string sql = string.Format("select * from ADDRESSASSOCIATEDwithFILTER ASS ,ADDRESS_FILTERS AF where AF.FilterId=ASS.FilterId and AF.FilterName='{0}' and AF.DeptId='{1}'", info.filterName, logon.sz_deptid);

            OdbcDataReader reader = db.ExecCommandReader();
            if (reader.Read())
            {
                FilterId = reader.GetInt32(reader.GetOrdinal("FilterId"));
                info.filterName = reader.GetString(reader.GetOrdinal("FilterName"));
                info.deptId = reader.GetString(reader.GetOrdinal("DeptId"));
                info.description = reader.GetString(reader.GetOrdinal("F_Description"));
                info.lastupdatedDate = reader.GetDateTime(reader.GetOrdinal("LastUpdatedDate"));

            }

            sql = string.Format("select * from ADDRESSASSOCIATEDwithFILTER where FilterId={0}", FilterId);
            reader = db.ExecCommandReader();
            while (reader.Read())
            {

            }
            return addresses;
        }

    }
}
