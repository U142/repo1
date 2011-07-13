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
            //String szPath = "U:\\vb4utv\\predefined-areas\\";
            String szPath = "\\\\195.119.0.167\\broadcast_dialogic\\predefined-areas\\";
            //find all objects and all alerts in DB with no ref to PASHAPE
            PASUmsDb db = new PASUmsDb("backbone_aoba125", "sa", "diginform", 60);
            HashSet<Int64> hash_alerts = new HashSet<Int64>();
            HashSet<Int64> hash_objects = new HashSet<Int64>();

            //2000000000000304
            String szSQL = String.Format("select l_alertpk from PAALERT WHERE l_alertpk not in (SELECT l_pk FROM PASHAPE WHERE l_type={0})", //PA WHERE PA.l_alertpk not in (SELECT l_pk FROM PASHAPE WHERE l_type={0})",
                                        (int)PASHAPETYPES.PAALERT);

            //String szSQL = String.Format("select l_alertpk from PAALERT PA WHERE PA.l_alertpk={0}",
            //                            1000000000000297);
            OdbcDataReader rs = db.ExecReader(szSQL, PASUmsDb.UREADER_KEEPOPEN);
            while (rs.Read())
            {
                hash_alerts.Add(rs.GetInt64(0));
            }
            rs.Close();
            
            szSQL = String.Format("select l_objectpk from PAOBJECT WHERE l_objectpk=0", //PO WHERE PO.l_objectpk not in (SELECT l_pk FROM PASHAPE WHERE l_type={0})",
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
            int counter = 0;
            int counter_found = 0;
            while (en.MoveNext())
            {
                ++counter;
                Int64 pk = en.Current;
                UFile f = new UFile(szPath, "a" + pk + ".xml");
                if (File.Exists(f.full()))
                {
                    String s = File.ReadAllText(f.full(), Encoding.GetEncoding("iso-8859-1"));
                    db.UpdatePAShape(pk, s, PASHAPETYPES.PAALERT, ref bChanged);
                    ++counter_found;
                }
            }
            Console.WriteLine("ALERTS: {0}/{1} updated", counter_found, counter);

            counter = 0;
            counter_found = 0;
            en = hash_objects.GetEnumerator();
            {
                while (en.MoveNext())
                {
                    ++counter;
                    Int64 pk = en.Current;
                    UFile f = new UFile(szPath, "o" + pk + ".xml");
                    if (File.Exists(f.full()))
                    {
                        String s = File.ReadAllText(f.full(), Encoding.GetEncoding("iso-8859-1"));
                        //db.UpdatePAShape(pk, s, PASHAPETYPES.PAOBJECT, ref bChanged);
                        ++counter_found;
                    }
                }
            }
            Console.WriteLine("OBJECTS: {0}/{1} updated", counter_found, counter);
            //System.Threading.Thread.Sleep(3000);
            Console.ReadKey();
        }
    }
}
