using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Odbc;
using System.Text.RegularExpressions;
using log4net;

namespace com.ums.pas.integration.AddressLookup
{
    class OwnerLookupImpl : IOwnerLookupFacade
    {
        private static ILog log = LogManager.GetLogger(typeof(OwnerLookupImpl));

        #region IOwnerLookupFacade Members

        public IEnumerable<RecipientData> GetMatchingOwnerAddresses(string connectionString, List<OwnerAddress> ownerAddresses)
        {
            if (ownerAddresses.Count == 0)
            {
                log.Info("No owner addresses listed");
                return new List<RecipientData>();
            }
            List<RecipientData> recipients = new List<RecipientData>();
            using (OdbcConnection Connection = new OdbcConnection(connectionString))
            using (OdbcCommand Command = Connection.CreateCommand())
            {

                Connection.Open();
                //Command.CommandText = string.Format("SELECT TELEFON FROM ADR_KONSUMENT_201320105 WHERE POSTNR=? and HUSNR=?");// and CONTAINS(NAVN, ?)");
                //Command.CommandText = string.Format("SELECT TELEFON FROM ADR_KONSUM WHERE HUSNR=? and CONTAINS(NAVN, ?)");
                Command.CommandText = string.Format("SELECT * FROM ADR_KONSUM_SEARCH WHERE POSTNR=? and HUSNR=? and CONTAINS(NAVN, ?)");
                Command.Parameters.Add("postnr", OdbcType.Int);
                Command.Parameters.Add("husnr", OdbcType.Int);
                Command.Parameters.Add("search", OdbcType.VarChar);

                DateTime start;

                double prepare = 0;
                double sql = 0;
                int mobilePhones = 0;
                int fixedPhones = 0;

                foreach (OwnerAddress owner in ownerAddresses)
                {
                    start = DateTime.Now;
                    string[] names = owner.Navn.Split(' ');
                    if (names.Length >= 2)
                    {
                        int? husnr = null;
                        husnr = GetHouseNr(owner.Adresselinje1 + owner.Adresselinje2);

                        if (owner.Postnr > 0)
                            Command.Parameters["postnr"].Value = owner.Postnr;
                        else
                            Command.Parameters["postnr"].Value = DBNull.Value;

                        if (husnr.HasValue)
                            Command.Parameters["husnr"].Value = husnr;
                        else
                            Command.Parameters["husnr"].Value = DBNull.Value;

                        Command.Parameters["search"].Value = names[0] + " and " + names[1];
                        prepare += (DateTime.Now - start).TotalMilliseconds;

                        try
                        {
                            start = DateTime.Now;
                            using (OdbcDataReader rs = Command.ExecuteReader())
                            {
                                int personsFound = 0;
                                while (rs.Read())
                                {
                                    ++personsFound;
                                    if (!rs.IsDBNull(0))
                                    {
                                        RecipientData r = new RecipientData()
                                        {
                                            AlertTarget = owner,

                                            Name = rs.GetString(rs.GetOrdinal("NAVN")),
                                            Endpoints = new List<Endpoint>(),
                                            Lon = rs.IsDBNull(rs.GetOrdinal("LAT")) ? 0 : rs.GetDouble(rs.GetOrdinal("LAT")),
                                            Lat = rs.IsDBNull(rs.GetOrdinal("LON")) ? 0 : rs.GetDouble(rs.GetOrdinal("LON")),
                                            Company = (rs.GetInt16(rs.GetOrdinal("BEDRIFT")) == 1),
                                            Address = rs.GetString(rs.GetOrdinal("ADRESSE")),
                                            Postno = Int32.Parse(rs.GetString(rs.GetOrdinal("POSTNR"))),
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
                                        //ret.Add(rs.GetString(0));
                                }
                                if (personsFound == 0)
                                {
                                    recipients.Add(new RecipientData()
                                    {
                                        AlertTarget = owner,
                                        Name = "No inhabitants found",
                                    });
                                }
                            }
                            double dill = (DateTime.Now - start).TotalMilliseconds;
                            log.InfoFormat("Executing (postnr={1}, husnr={2}, search={3}) {0:0}ms", dill, Command.Parameters["postnr"].Value, Command.Parameters["husnr"].Value, Command.Parameters["search"].Value);
                            //Console.WriteLine("Executing (postnr={1}, husnr={2}) {0:0}ms", dill, Command.Parameters["postnr"].Value, Command.Parameters["husnr"].Value);
                            //Console.WriteLine("Executing (husnr={1}, search={2}) {0:0}ms", dill, Command.Parameters["husnr"].Value, Command.Parameters["search"].Value);
                            sql += dill;
                        }
                        catch (OdbcException ex)
                        {
                            Console.WriteLine("ODBC feil: {0}", ex);
                        }
                    }
                    else
                    {
                        Console.WriteLine("Failed to look up '{0}'", owner.Navn);
                    }
                }
                log.InfoFormat("Found {0} recipients living on {1} PropertyAddresses, owning {2} mobile and {3} fixed phones", recipients.Count, ownerAddresses.Count, mobilePhones, fixedPhones);

                Connection.Close();

                log.InfoFormat("Preparation: {0:0} ms\texecution: {1:0} ms", prepare, sql);
            }

            return recipients;

        }

        #endregion

        private static int? GetHouseNr(string s)
        {
            Match m = Regex.Match(s, @"\d+");
            if (m.Success)
                return Convert.ToInt32(m.Value);
            else
                return null;
        }
    }
}
