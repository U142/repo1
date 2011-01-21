﻿using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using System.Data.Odbc;
using com.ums.UmsParm;
using com.ums.UmsCommon;

namespace PasswordHasher
{
    class Hash
    {
        class Userinfo
        {
            public Int64 l_userpk;
            public String sz_password;
            public String sz_hashed = "";
        }
        static void Main(string[] args)
        {
            PASUmsDb db = new PASUmsDb("backbone_ibuki125", "sa", "diginform", 60);
            OdbcDataReader rs = db.ExecReader("SELECT l_userpk, sz_paspassword FROM BBUSER where sz_hash_paspwd is null and sz_paspassword is not null", PASUmsDb.UREADER_KEEPOPEN);
            ArrayList list = new ArrayList();
            while (rs.Read())
            {
                Userinfo info = new Userinfo();
                info.l_userpk = rs.GetInt64(0);
                info.sz_password = rs.GetString(1);
                list.Add(info);
            }
            rs.Close();
            foreach (Userinfo ui in list)
            {
                ui.sz_hashed = Helpers.CreateSHA512Hash(ui.sz_password);
            }
            foreach (Userinfo ui in list)
            {
                Console.WriteLine(ui.l_userpk + " " + ui.sz_password);
                //db.ExecNonQuery(String.Format("UPDATE BBUSER SET sz_hash_paspwd='{0}' WHERE l_userpk={1} AND sz_paspassword='{2}'",
                //                ui.sz_hashed, ui.l_userpk, ui.sz_password));
            }
            Console.ReadKey();
        }
    }
}
