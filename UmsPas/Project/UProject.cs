using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Xml.Linq;

using com.ums.UmsParm;
using com.ums.UmsCommon;
using com.ums.UmsDbLib;
using System.Data.Odbc;

namespace com.ums.PAS.Project
{
    public class UProject : PASUmsDb
    {
        public UPROJECT_RESPONSE uproject(ref ULOGONINFO logon, ref UPROJECT_REQUEST req)
        {
            UPROJECT_RESPONSE response = new UPROJECT_RESPONSE();
            response.n_responsecode = -1;

            //PASUmsDb db = new PASUmsDb();
            /*FLYTT CheckLogon til asmx filene for felles sjekk
             * 
             * try
            {
                if (!db.CheckLogon(ref logon, true))
                {
                    response.sz_responsetext = "Error in logon";
                    return response;
                }
            }
            catch (Exception e)
            {
                throw e;
            }*/
            OdbcDataReader dr = null;
            try
            {
                string sz_operation = "insert";

                switch(sz_operation) {
	                case "insert":
		                req.n_projectpk = 0;
                        break;
                }

                req.sz_name = req.sz_name.Replace("'", "''");
                String szSQLProject = String.Format("sp_project '{0}', {1}, '{2}', {3}, {4}, {5}", sz_operation, req.n_projectpk, req.sz_name, 0, logon.l_deptpk, logon.l_userpk);
                dr = ExecReader(szSQLProject, UmsDb.UREADER_AUTOCLOSE);

                while (dr.Read())
                {
                    response.n_projectpk = Int64.Parse(dr["l_projectpk"].ToString());
                    response.sz_name = dr["sz_name"].ToString();
                    response.n_createdtimestamp = Int64.Parse(dr["l_createtimestamp"].ToString());
                    response.n_updatedtimestamp = Int64.Parse(dr["l_updatetimestamp"].ToString());
                    response.sz_responsetext = "OK";
                    response.n_responsecode = 0;
                }
                
            }
            catch (Exception e)
            {
                ULog.error(0, "Could not create project", e.Message);
                response.sz_responsetext = e.Message;
                response.n_responsecode = -1;
            }
            finally{
                if(dr!=null && !dr.IsClosed)
                    dr.Close();
            }
            return response;
        }
    }
    public class UPROJECT_REQUEST
    {
        public Int64 n_projectpk;
        public string sz_name;
    }
    public class UPROJECT_RESPONSE
    {
        public Int64 n_projectpk;
        public string sz_name;
        public Int64 n_createdtimestamp;
        public Int64 n_updatedtimestamp;
        public int n_responsecode;
        public string sz_responsetext;
    }

}
