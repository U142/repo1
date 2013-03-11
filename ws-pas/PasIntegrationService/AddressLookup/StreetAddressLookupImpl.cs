using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Odbc;

namespace com.ums.pas.integration.AddressLookup
{
    /// <summary>
    /// First implementation of street address lookup.
    /// Using code from jone-l for different type of lookups.
    /// Got the code from 
    /// git@github.com:jone-l/diverse.git/GISOptimization
    /// </summary>
    class StreetAddressLookupImpl : IStreetAddressLookupFacade
    {
        private String _connectionString;

        public String ConnectionString
        {
            get { return _connectionString; }
            set { _connectionString = value; }
        }

        #region IStreetAddressLookupFacade Members

        public IEnumerable<RecipientData> GetMatchingStreetAddresses(String connectionString, List<StreetAddress> streetAddresses)
        {
            if(streetAddresses.Count == 0)
            {
                return new List<RecipientData>();
            }
            ConnectionString = connectionString;
            return GetMatchingStreetAddressesUsingTempTbl(streetAddresses);
        }

        #endregion


        #region Impl_TempTbl
        public List<RecipientData> GetMatchingStreetAddressesUsingTempTbl(List<StreetAddress> streetAddresses)
        {
            //List<string> numbers = new List<string>();
            List<RecipientData> recipients = new List<RecipientData>();
            using (OdbcConnection Connection = new OdbcConnection(ConnectionString))
            using (OdbcCommand Command = Connection.CreateCommand())
            {
                DateTime start;
                TimeSpan duration;

                Connection.Open();

                start = DateTime.Now;
                Command.CommandText = "CREATE TABLE #SAMATCH(KOMMUNENR int, GATEKODE int, HUSNR int, OPPGANG varchar(5))";
                Command.ExecuteNonQuery();
                duration = DateTime.Now - start;
                Console.WriteLine("Create temp table took {0:0} ms", duration.TotalMilliseconds);

                start = DateTime.Now;
                Command.CommandText = "INSERT INTO #SAMATCH(KOMMUNENR, GATEKODE, HUSNR, OPPGANG) VALUES(?,?,?,?)";
                Command.Parameters.Add("knr", OdbcType.Int);
                Command.Parameters.Add("gatenr", OdbcType.Int);
                Command.Parameters.Add("husnr", OdbcType.Int);
                Command.Parameters.Add("husbokstav", OdbcType.VarChar, 5);
                Command.Prepare();

                foreach (StreetAddress sa in streetAddresses)
                {
                    int Knr;
                    if (int.TryParse(sa.MunicipalCode, out Knr))
                    {
                        Command.Parameters["knr"].Value = Knr;
                        Command.Parameters["gatenr"].Value = sa.StreetNo;
                        Command.Parameters["husnr"].Value = sa.HouseNo;
                        Command.Parameters["husbokstav"].Value = sa.Letter;

                        Command.ExecuteNonQuery();
                    }
                    else
                    {
                        Console.WriteLine("Failed to get kommunenr from {0}", sa);
                    }
                }
                duration = DateTime.Now - start;
                Console.WriteLine("Insert to temp table took {0:0} ms", duration.TotalMilliseconds);

                start = DateTime.Now;
                Command.CommandText = "SELECT * FROM #SAMATCH SA INNER JOIN ADR_KONSUM FR ON FR.KOMMUNENR=SA.KOMMUNENR AND FR.GATEKODE=SA.GATEKODE AND FR.HUSNR=SA.HUSNR AND ISNULL(FR.OPPGANG,'')=SA.OPPGANG";
                using (OdbcDataReader rs = Command.ExecuteReader())
                {
                    while (rs.Read())
                    {
                        if (!rs.IsDBNull(0))
                        {
                            RecipientData r = new RecipientData()
                            {


                                /*Attributes = new List<DataItem>()
                                {
                                    new DataItem()
                                    {
                                        Key = "NAVN",
                                        Value = rs.IsDBNull(rs.GetOrdinal("NAVN")) ? "" : rs.GetString(rs.GetOrdinal("NAVN"))
                                    },
                                },
                                */
                                AlertTarget = new StreetAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(),
                                                                rs.GetInt32(rs.GetOrdinal("GATEKDOE")),
                                                                rs.GetInt32(rs.GetOrdinal("HUSNR")),
                                                                rs.GetString(rs.GetOrdinal("OPPGANG")),
                                                                ""),
                                Name = rs.GetString(rs.GetOrdinal("NAVN")),

                                Endpoints = new List<Endpoint>()
                                {
                                    new Phone()
                                    {
                                        CanReceiveSms = true,
                                        Address = rs.IsDBNull(rs.GetOrdinal("MOBIL")) ? "" : rs.GetString(rs.GetOrdinal("MOBIL"))
                                    },
                                    new Phone()
                                    {
                                        CanReceiveSms = false,
                                        Address = rs.IsDBNull(rs.GetOrdinal("TELEFON")) ? "" : rs.GetString(rs.GetOrdinal("TELEFON"))
                                    },
                                }
                            };
                            recipients.Add(r);
                        }
                    }
                }
                duration = DateTime.Now - start;
                Console.WriteLine("Select took {0:0} ms", duration.TotalMilliseconds);

                start = DateTime.Now;
                Command.CommandText = "DROP TABLE #SAMATCH";
                Command.ExecuteNonQuery();
                duration = DateTime.Now - start;
                Console.WriteLine("Drop table took {0:0} ms", duration.TotalMilliseconds);

                Connection.Close();
            }

            return recipients;
        }
        #endregion
    }
}
