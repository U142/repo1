using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Odbc;
using log4net;

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

        public List<PropertyAddress> NoNumbersFoundList { get; private set; }
        public IEnumerable<PropertyAddress> GetNoNumbersFoundList()
        {
            if (NoNumbersFoundList != null)
            {
                return NoNumbersFoundList;
            }
            throw new Exception("GetMatchingOwnerAddresses not yet executed");
        }

        public PropertyAddressLookupImpl()
        {
            NoNumbersFoundList = new List<PropertyAddress>();
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

                string collate = "";
                Command.CommandText = "SELECT collation_name FROM sys.databases WHERE name = '" + Command.Connection.Database + "'";
                var collation = Command.ExecuteScalar();
                if (collation != null)
                    collate = String.Format("COLLATE {0}", collation);

                start = DateTime.Now;
                //Command.CommandText = "CREATE TABLE #SAMATCH(KOMMUNENR int, GATEKODE int, HUSNR int, OPPGANG varchar(5))";
                Command.CommandText = String.Format("CREATE TABLE #SAMATCH(KOMMUNENR int, GNR int, BNR int, FNR int, SNR int, ATTRIBUTES varchar(8000) {0})", collate);
                Command.ExecuteNonQuery();
                duration = DateTime.Now - start;
                log.InfoFormat("Create temp table took {0:0} ms", duration.TotalMilliseconds);

                start = DateTime.Now;
                Command.CommandText = "INSERT INTO #SAMATCH(KOMMUNENR, GNR, BNR, FNR, SNR, ATTRIBUTES) VALUES(?,?,?,?,?,?)";
                Command.Parameters.Add("knr", OdbcType.Int);
                Command.Parameters.Add("gnr", OdbcType.Int);
                Command.Parameters.Add("bnr", OdbcType.Int);
                Command.Parameters.Add("fnr", OdbcType.Int);
                Command.Parameters.Add("snr", OdbcType.Int);
                Command.Parameters.Add("attr", OdbcType.VarChar, 8000);
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
                        Command.Parameters["snr"].Value = sa.Unr;

                        Command.Parameters["attr"].Value = DataItem.FromList(sa.Attributes);
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
                                    + "ISNULL(SA.KOMMUNENR,0) KOMMUNENR "
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
                                    + ",ISNULL(SA.GNR,0) GNR "
                                    + ",ISNULL(SA.BNR,0) BNR "
                                    + ",ISNULL(SA.FNR,0) FNR "
                                    + ",ISNULL(SA.SNR,0) SNR "
                                    + ",ISNULL(FR.KON_DMID,0) KON_DMID "
                                    + ",ISNULL(SA.ATTRIBUTES, '') ATTRIBUTES "
                                    + ",ISNULL(FR.KOMMUNENR,-1) NORECIPIENTS "
                                    + "FROM #SAMATCH SA LEFT OUTER JOIN ADR_INTEGRATION FR ON FR.KOMMUNENR=SA.KOMMUNENR "
                                    + "AND ISNULL(FR.GNR,0)=SA.GNR "
                                    + "AND ISNULL(FR.BNR,0)=SA.BNR "
                                    + "AND ISNULL(FR.FNR,0)=SA.FNR "
                                    + "AND ISNULL(FR.SNR,0)=SA.SNR ";

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
                                                                    rs.GetInt32(rs.GetOrdinal("SNR")),
                                                                    DataItem.FromString(rs.GetString(rs.GetOrdinal("ATTRIBUTES")))),
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
                            r.NoRecipients = rs.GetInt32(rs.GetOrdinal("NORECIPIENTS")) < 0;
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

                            if (r.NoRecipients)
                            {
                                if(!NoNumbersFoundList.Contains((PropertyAddress)r.AlertTarget))
                                    NoNumbersFoundList.Add((PropertyAddress)r.AlertTarget);
                            }
                            else
                            {
                                recipients.Add(r);
                            }
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
