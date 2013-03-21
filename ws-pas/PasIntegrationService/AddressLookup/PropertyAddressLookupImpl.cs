using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using log4net;
using System.Data.Odbc;

namespace com.ums.pas.integration.AddressLookup
{
    class PropertyAddressLookupImpl : IPropertyAddressLookupFacade
    {
        private static ILog log = LogManager.GetLogger(typeof(PropertyAddressLookupImpl));
        private String _connectionString;

        public String ConnectionString
        {
            get { return _connectionString; }
            set { _connectionString = value; }
        }


        #region IPropertyAddressLookupFacade Members
        public IEnumerable<RecipientData> GetMatchingPropertyAddresses(string connectionString, List<PropertyAddress> propertyAddresses)
        {
            ConnectionString = connectionString;
            if(propertyAddresses.Count == 0)
            {
                return new List<RecipientData>();
            }
            return GetMatchingPropertyAddressesUsingTempTbl(propertyAddresses);
        }

        #endregion

        #region Impl_TempTbl
        public List<RecipientData> GetMatchingPropertyAddressesUsingTempTbl(List<PropertyAddress> propertyAddresses)
        {
            if (propertyAddresses.Count == 0)
            {
                log.Info("No property addresses listed");
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
                //Command.CommandText = "CREATE TABLE #SAMATCH(KOMMUNENR int, GATEKODE int, HUSNR int, OPPGANG varchar(5))";
                Command.CommandText = "CREATE TABLE #SAMATCH(KOMMUNENR int, GNR int, BNR int, FNR int, UNR int)";
                Command.ExecuteNonQuery();
                duration = DateTime.Now - start;
                log.InfoFormat("Create temp table took {0:0} ms", duration.TotalMilliseconds);

                start = DateTime.Now;
                Command.CommandText = "INSERT INTO #SAMATCH(KOMMUNENR, GNR, BNR, FNR, UNR) VALUES(?,?,?,?,?)";
                Command.Parameters.Add("knr", OdbcType.Int);
                Command.Parameters.Add("gnr", OdbcType.Int);
                Command.Parameters.Add("bnr", OdbcType.Int);
                Command.Parameters.Add("fnr", OdbcType.Int);
                Command.Parameters.Add("unr", OdbcType.Int);
                Command.Prepare();


                foreach (PropertyAddress sa in propertyAddresses)
                {
                    int Knr;
                    if (int.TryParse(sa.MunicipalCode, out Knr))
                    {
                        Command.Parameters["knr"].Value = sa.MunicipalCode;
                        Command.Parameters["gnr"].Value = sa.Gnr;
                        Command.Parameters["bnr"].Value = sa.Bnr;
                        Command.Parameters["fnr"].Value = sa.Fnr;
                        Command.Parameters["unr"].Value = sa.Unr;

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
                                    + ",ISNULL(FR.GNR,0) GNR "
                                    + ",ISNULL(FR.BNR,0) BNR "
                                    + ",ISNULL(FR.FNR,0) FNR "
                                    + ",ISNULL(FR.UNR,0) UNR "
                                    + ",ISNULL(FR.KON_DMID,0) KON_DMID "
                                    + "FROM #SAMATCH SA INNER JOIN ADR_INTEGRATION FR ON FR.KOMMUNENR=SA.KOMMUNENR "
                                    + "AND ISNULL(FR.GNR,0)=SA.GNR "
                                    + "AND ISNULL(FR.BNR,0)=SA.BNR "
                                    + "AND ISNULL(FR.FNR,0)=SA.FNR "
                                    + "AND ISNULL(FR.UNR,0)=SA.UNR ";

                //TODO: add filter for fnr and unr
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
                                AlertTarget = new PropertyAddress(rs.GetInt32(rs.GetOrdinal("KOMMUNENR")).ToString(),
                                                                    rs.GetInt32(rs.GetOrdinal("GNR")),
                                                                    rs.GetInt32(rs.GetOrdinal("BNR")),
                                                                    rs.GetInt32(rs.GetOrdinal("FNR")),
                                                                    rs.GetInt32(rs.GetOrdinal("UNR"))),
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

                log.InfoFormat("Found {0} recipients living on {1} PropertyAddresses, owning {2} mobile and {3} fixed phones", recipients.Count, propertyAddresses.Count, mobilePhones, fixedPhones);

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
