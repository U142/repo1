using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Data.Odbc;
using com.ums.UmsFile;
using com.ums.UmsCommon;
//using com.ums.UmsDbLib;
using com.ums.UmsParm;

namespace PredefToPashape
{
    class FileToDb
    {
        static void Main(string[] args)
        {
            String szPath = "U:\\vb4utv\\predefined-areas\\";
            //find all objects and all alerts in DB with no ref to PASHAPE
            PASUmsDb db = new PASUmsDb("backbone_ibuki125", "sa", "diginform", 60);
            HashSet<Int64> hash_alerts = new HashSet<Int64>();
            HashSet<Int64> hash_objects = new HashSet<Int64>();

            String szSQL = String.Format("select l_alertpk from PAALERT ", //PA WHERE PA.l_alertpk not in (SELECT l_pk FROM PASHAPE WHERE l_type={0})",
                                        (int)PASHAPETYPES.PAALERT);

            //String szSQL = String.Format("select l_alertpk from PAALERT PA WHERE PA.l_alertpk={0}",
            //                            1000000000000297);
            OdbcDataReader rs = db.ExecReader(szSQL, PASUmsDb.UREADER_KEEPOPEN);
            while (rs.Read())
            {
                hash_alerts.Add(rs.GetInt64(0));
            }
            rs.Close();
            
            szSQL = String.Format("select l_objectpk from PAOBJECT ", //PO WHERE PO.l_objectpk not in (SELECT l_pk FROM PASHAPE WHERE l_type={0})",
                                    (int)PASHAPETYPES.PAOBJECT);
            rs = db.ExecReader(szSQL, PASUmsDb.UREADER_KEEPOPEN);
            while (rs.Read())
            {
                hash_objects.Add(rs.GetInt64(0));
            }
            rs.Close();
            
            //parse through all objects and alerts with no ref to PASHAPE, then insert into PASHAPE, based on path
            Boolean bChanged = false;

            HashSet<Int64>.Enumerator en = hash_alerts.GetEnumerator();
            while (en.MoveNext())
            {
                Int64 pk = en.Current;
                UFile f = new UFile(szPath, "a" + pk + ".xml");
                if (File.Exists(f.full()))
                {
                    //Console.WriteLine(f.full());
                    String s = File.ReadAllText(f.full(), Encoding.Default);
                    //Console.WriteLine(s);
                    //Byte[] encoded = Encoding.Unicode.GetBytes(s);
                    //s = Encoding.Unicode.GetString(encoded);
                    Console.WriteLine(s);
                    db.UpdatePAShape(pk, s, PASHAPETYPES.PAALERT, ref bChanged);
                }
            }
            en = hash_objects.GetEnumerator();
            {
                while (en.MoveNext())
                {
                    Int64 pk = en.Current;
                    UFile f = new UFile(szPath, "o" + pk + ".xml");
                    if (File.Exists(f.full()))
                    {
                        //Console.WriteLine(f.full());
                        String s = File.ReadAllText(f.full(), Encoding.Default);
                        //Console.WriteLine(s);
                        db.UpdatePAShape(pk, s, PASHAPETYPES.PAOBJECT, ref bChanged);
                    }
                }
            }
            //System.Threading.Thread.Sleep(3000);
            //Console.ReadKey();
        }
    }
}
