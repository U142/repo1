using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;

using com.ums.UmsCommon;
using com.ums.UmsParm;
using com.ums.UmsDbLib;
using System.Data.Odbc;

namespace com.ums.PAS.Project
{
    public class URefno
    {
        public UREFNO_RESPONSE getRefno(ref ULOGONINFO logon)
        {
            UREFNO_RESPONSE response = new UREFNO_RESPONSE();
            response.n_responsecode = -1;

            PASUmsDb db = new PASUmsDb();
            if (!db.CheckLogon(ref logon))
            {
                response.sz_responsetext = "Error in logon";
                return response;
            }

            try
            {
                OdbcDataReader dr = db.ExecReader("sp_refno_out", UmsDb.UREADER_AUTOCLOSE);

                while (dr.Read())
                {
                    response.n_refno = Int64.Parse(dr[0].ToString());
                    response.n_responsecode = 0;
                    response.sz_responsetext = "OK";
                }
                dr.Close();
                db.close();
            }
            catch (Exception e)
            {
                ULog.error(0, "Error getting refno", e.Message);
                response.n_responsecode = -1;
                response.sz_responsetext = e.Message;
            }
            return response;
        }

    }

    public class UREFNO_RESPONSE
    {
        public Int64 n_refno;
        public Int64 n_responsecode;
        public string sz_responsetext;
    }
}
