﻿using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Xml.Linq;
using com.ums.UmsDbLib;
using System.Data.Odbc;


namespace com.ums.PAS.Database
{
    public class UMAXALLOC
    {
        public Int64 n_refno;
        public Int64 n_projectpk;
        public int n_maxalloc;
    }

    public class USendDb : UmsDb
    {
        public USendDb()
            : base()
        {

        }

        public int setMaxAlloc(ref UMAXALLOC param)
        {
            int ret = -1;
            try
            {
                String szSQL = String.Format("sp_setmaxalloc {0}, {1}, {2}", param.n_projectpk, param.n_refno, param.n_maxalloc);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if(rs.NextResult())
                {
                    ret = rs.GetInt32(0);
                    param.n_maxalloc = ret;
                }
                else
                {

                }
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                this.close();
            }
            return ret;
        }
    }
}
