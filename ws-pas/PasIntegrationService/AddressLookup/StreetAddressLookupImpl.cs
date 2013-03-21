using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Odbc;
using log4net;

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
        private static ILog log = LogManager.GetLogger(typeof(StreetAddressLookupImpl));
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
            if (streetAddresses.Count == 0)
            {
                log.Info("No street addresses listed");
                return new List<RecipientData>();
            }
            List<RecipientData> recipients = new List<RecipientData>();
            using (OdbcConnection Connection = new OdbcConnection(ConnectionString))
            using (OdbcCommand Command = Connection.CreateCommand())
            {
                DateTime start;
                TimeSpan duration;

                Connection.Open();

                start = DateTime.Now;
                Command.CommandText = "CREATE TABLE #SAMATCH(KOMMUNENR int, GATEKODE int, HUSNR int, OPPGANG varchar(5) COLLATE Latin1_General_100_CI_AI)";
                Command.ExecuteNonQuery();
                duration = DateTime.Now - start;
                log.InfoFormat("Create temp table took {0:0} ms", duration.TotalMilliseconds);

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
                        log.WarnFormat("Failed to get kommunenr from {0}", sa);
                    }
                }
                duration = DateTime.Now - start;
                log.InfoFormat("Insert to temp table took {0:0} ms", duration.TotalMilliseconds);

                start = DateTime.Now;
                Command.CommandText = "SELECT DISTINCT "
                                    + "ISNULL(FR.KOMMUNENR,0) KOMMUNENR "
                                    + ",ISNULL(FR.GATEKODE,0) GATEKODE "
                                    + ",ISNULL(FR.HUSNR,0) HUSNR "
                                    + ",ISNULL(FR.OPPGANG,'') OPPGANG "
                                    + ",ISNULL(FR.NAVN,'') NAVN "
                                    + ",ISNULL(FR.LAT,0) LAT "
                                    + ",ISNULL(FR.LON,0) LON "
                                    + ",ISNULL(FR.BEDRIFT,0) BEDRIFT "
                                    + ",ISNULL(FR.ADRESSE,'') ADRESSE "
                                    + ",ISNULL(FR.POSTNR,0) POSTNR "
                                    + ",ISNULL(FR.POSTSTED,'') POSTSTED "
                                    + ",ISNULL(FR.MOBIL,'') MOBIL "
                                    + ",ISNULL(FR.TELEFON,'') TELEFON "
                                    + ",ISNULL(FR.KON_DMID,0) KON_DMID "
                                    + "FROM #SAMATCH SA INNER JOIN ADR_INTEGRATION FR ON FR.KOMMUNENR=SA.KOMMUNENR AND isnull(FR.GATEKODE,0)=SA.GATEKODE AND isnull(FR.HUSNR,0)=SA.HUSNR AND ISNULL(FR.OPPGANG,'')=SA.OPPGANG";
                int mobilePhones = 0;
                int fixedPhones = 0;

                using (OdbcDataReader rs = Command.ExecuteReader())
                {
                    while (rs.Read())
                    {
                        if (!rs.IsDBNull(0))
                        {
                            RecipientData r = new RecipientData()
                            {
                                AlertTarget = new StreetAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(),
                                                                rs.GetInt32(rs.GetOrdinal("GATEKODE")),
                                                                rs.GetInt32(rs.GetOrdinal("HUSNR")),
                                                                rs.GetString(rs.GetOrdinal("OPPGANG")),
                                                                ""),
                                Name = rs.GetString(rs.GetOrdinal("NAVN")),
                                Endpoints = new List<Endpoint>(),
                                Lon = rs.IsDBNull(rs.GetOrdinal("LAT")) ? 0 : rs.GetDouble(rs.GetOrdinal("LAT")),
                                Lat = rs.IsDBNull(rs.GetOrdinal("LON")) ? 0 : rs.GetDouble(rs.GetOrdinal("LON")),
                                Company = (rs.GetInt16(rs.GetOrdinal("BEDRIFT")) == 1),
                                Address = rs.GetString(rs.GetOrdinal("ADRESSE")),
                                Postno = rs.GetInt32(rs.GetOrdinal("POSTNR")),
                                PostPlace = rs.GetString(rs.GetOrdinal("POSTSTED")),
                                KonDmid = rs.GetInt32(rs.GetOrdinal("KON_DMID")),

                            };
                            if (!rs.IsDBNull(rs.GetOrdinal("MOBIL")) && rs["MOBIL"].ToString().Length > 0)
                            {
                                r.Endpoints.Add(new Phone()
                                {
                                    CanReceiveSms = true,
                                    Address = rs["MOBIL"].ToString(),
                                });
                                ++mobilePhones;
                            }
                            if (!rs.IsDBNull(rs.GetOrdinal("TELEFON")) && rs["TELEFON"].ToString().Length > 0)
                            {
                                r.Endpoints.Add(new Phone()
                                {
                                    CanReceiveSms = false,
                                    Address = rs["TELEFON"].ToString(),
                                });
                                ++fixedPhones;
                            }
                            
                            recipients.Add(r);
                        }
                    }
                }

                log.InfoFormat("Found {0} recipients living on {1} StreetAddresses, owning {2} mobile and {3} fixed phones", recipients.Count, streetAddresses.Count, mobilePhones, fixedPhones);


                duration = DateTime.Now - start;
                log.InfoFormat("Select took {0:0} ms", duration.TotalMilliseconds);

                start = DateTime.Now;
                Command.CommandText = "DROP TABLE #SAMATCH";
                Command.ExecuteNonQuery();
                duration = DateTime.Now - start;
                log.InfoFormat("Drop table took {0:0} ms", duration.TotalMilliseconds);

                Connection.Close();
            }

            return recipients;
        }
        #endregion
    }
}
