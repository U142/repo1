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
                        Command.Parameters["fnr"].Value = 0;
                        Command.Parameters["unr"].Value = 0;

                        Command.ExecuteNonQuery();
                    }
                    else
                    {
                        log.InfoFormat("Failed to get kommunenr from {0}", sa);
                    }
                }
                duration = DateTime.Now - start;
                log.InfoFormat("Insert to temp table took {0:0} ms", duration.TotalMilliseconds);

                start = DateTime.Now;
                Command.CommandText = "SELECT DISTINCT * FROM #SAMATCH SA INNER JOIN ADR_INTEGRATION FR ON FR.KOMMUNENR=SA.KOMMUNENR AND FR.GNR=SA.GNR AND FR.BNR=SA.BNR"; 
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
                                                                    0,
                                                                    0),
                                Name = rs.GetString(rs.GetOrdinal("NAVN")),
                                Endpoints = new List<Endpoint>(),
                                Lon = rs.IsDBNull(rs.GetOrdinal("LAT")) ? 0 : rs.GetDouble(rs.GetOrdinal("LAT")),
                                Lat = rs.IsDBNull(rs.GetOrdinal("LON")) ? 0 : rs.GetDouble(rs.GetOrdinal("LON")),
                                Company = (rs.GetInt16(rs.GetOrdinal("BEDRIFT")) == 1),
                                Address = rs.GetString(rs.GetOrdinal("ADRESSE")),
                                Postno = rs.GetInt32(rs.GetOrdinal("POSTNR")),
                                PostPlace = rs.GetString(rs.GetOrdinal("POSTSTED")),

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
