﻿using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Xml.Linq;

using com.ums.UmsCommon;
using com.ums.UmsParm;
using com.ums.UmsDbLib;
using System.Data.Odbc;

namespace com.ums.PAS.Project
{
    public class URefno
    {
        public REFNO_RESPONSE getRefno(ref ULOGONINFO logon)
        {
            REFNO_RESPONSE response = new REFNO_RESPONSE();
            response.n_responsecode = -1;

            OdbcDataReader dr = null;

            PASUmsDb db = new PASUmsDb();
            if (!db.CheckLogon(ref logon, true))
            {
                response.sz_responsetext = "Error in logon";
                return response;
            }

            try
            {
                dr = db.ExecReader("sp_refno_out", UmsDb.UREADER_AUTOCLOSE);

                while (dr.Read())
                {
                    response.n_refno = Int64.Parse(dr[0].ToString());
                    response.n_responsecode = 0;
                    response.sz_responsetext = "OK";
                }
            }
            catch (Exception e)
            {
                ULog.error(0, "Error getting refno", e.Message);
                response.n_responsecode = -1;
                response.sz_responsetext = e.Message;
            }
            finally {

                if (dr != null && !dr.IsClosed)
                    dr.Close();
                db.close();
            }
            return response;
        }

    }

    public class REFNO_RESPONSE
    {
        public Int64 n_refno;
        public Int64 n_responsecode;
        public string sz_responsetext;
    }
}
