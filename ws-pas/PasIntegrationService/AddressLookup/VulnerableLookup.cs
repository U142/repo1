using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Odbc;

namespace com.ums.pas.integration.AddressLookup
{
    class VulnerableLookup
    {
        public DataItem Category { get; private set; }
        public DataItem Profession { get; private set; }

        public VulnerableLookup(int konDmid, string connectionString)
        {
            using (OdbcConnection conn = new OdbcConnection(connectionString))
            using (OdbcCommand cmd = conn.CreateCommand())
            {
                conn.Open();
                cmd.CommandText = @"select 
	                                    SC.sz_description sz_category,
	                                    SP.sz_description sz_profession
                                    from
	                                    ADR_EDITED AE
	                                    INNER JOIN ADR_EDITED_SO SO ON AE.KON_DMID = SO.KON_DMID
	                                    LEFT OUTER JOIN SO_CATEGORY SC ON SC.l_category=SO.l_category
	                                    LEFT OUTER JOIN SO_PROFESSION SP ON SP.l_profession=SO.l_profession
                                    where 
	                                    AE.KON_DMID=?
                                    union select 
	                                    SC.sz_description sz_category,
	                                    SP.sz_description sz_profession
                                    from
	                                    ADR_EDITED AE
	                                    INNER JOIN ADR_EDITED_SO SO ON
		                                    AE.l_parent_adr = SO.KON_DMID
	                                    LEFT OUTER JOIN SO_CATEGORY SC ON SC.l_category=SO.l_category
	                                    LEFT OUTER JOIN SO_PROFESSION SP ON SP.l_profession=SO.l_profession
                                    where 
	                                    AE.KON_DMID=?
                                    union select 
	                                    SC.sz_description sz_category,
	                                    SP.sz_description sz_profession
                                    from
	                                    ADR_EDITED AE
	                                    INNER JOIN ADR_EDITED_SO SO ON
		                                    AE.l_parent_comp = SO.KON_DMID
	                                    LEFT OUTER JOIN SO_CATEGORY SC ON SC.l_category=SO.l_category
	                                    LEFT OUTER JOIN SO_PROFESSION SP ON SP.l_profession=SO.l_profession
                                    where
	                                    AE.KON_DMID=?";
                cmd.Parameters.Add("KON_DMID1", OdbcType.Int).Value = konDmid;
                cmd.Parameters.Add("KON_DMID2", OdbcType.Int).Value = konDmid;
                cmd.Parameters.Add("KON_DMID3", OdbcType.Int).Value = konDmid;

                using (OdbcDataReader rs = cmd.ExecuteReader())
                {
                    if (rs.Read())
                    {
                        if (!rs.IsDBNull(0))
                            Category = new DataItem("Category", rs.GetString(0));
                        if (!rs.IsDBNull(1))
                            Profession = new DataItem("Profession", rs.GetString(1));
                    }
                }
            }
        }
    }
}
