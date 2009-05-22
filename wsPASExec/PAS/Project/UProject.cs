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
    public class UProject
    {
        public UPROJECT_RESPONSE uproject(ref ULOGONINFO logon, ref UPROJECT_REQUEST req)
        {
            UPROJECT_RESPONSE response = new UPROJECT_RESPONSE();
            response.n_responsecode = -1;

            PASUmsDb db = new PASUmsDb();
            if (!db.CheckLogon(ref logon))
            {
                response.sz_responsetext = "Error in logon";
                return response;
            }

            try
            {
                string sz_operation = "insert";

                switch(sz_operation) {
	                case "insert":
		                req.n_projectpk = 0;
                        break;
                }

                OdbcDataReader dr = db.ExecReader(String.Format("sp_project '{0}', {1}, '{2}', {3}, {4}", sz_operation, req.n_projectpk, req.sz_name, 0, logon.l_deptpk), UmsDb.UREADER_AUTOCLOSE);

                while (dr.Read())
                {
                    response.n_projectpk = Int64.Parse(dr["l_projectpk"].ToString());
                    response.sz_name = dr["sz_name"].ToString();
                    response.n_createdtimestamp = Int64.Parse(dr["l_createtimestamp"].ToString());
                    response.n_updatedtimestamp = Int64.Parse(dr["l_updatetimestamp"].ToString());
                    response.sz_responsetext = "OK";
                    response.n_responsecode = 0;
                }
                dr.Close();
                db.close();
            }
            catch (Exception e)
            {
                ULog.error(0, "Could not create project", e.Message);
                response.sz_responsetext = e.Message;
                response.n_responsecode = -1;
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
