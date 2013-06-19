using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Odbc;
using System.Text.RegularExpressions;
using log4net;

namespace com.ums.pas.integration.AddressLookup
{
    public class OwnerLookupImpl : IOwnerLookupFacade
    {
        private static ILog log = LogManager.GetLogger(typeof(OwnerLookupImpl));

        public List<OwnerAddress> NoMatchList { get; private set; }

        #region IOwnerLookupFacade Members


        public IEnumerable<OwnerAddress> GetNoMatchList()
        {
            if (NoMatchList != null)
            {
                return NoMatchList;
            }
            throw new Exception("GetMatchingOwnerAddresses not yet executed");
        }


        public IEnumerable<RecipientData> GetMatchingOwnerAddresses(String ConnectionString, List<OwnerAddress> ownerAddresses)
        {
            NoMatchList = new List<OwnerAddress>();
            if (ownerAddresses.Count == 0)
            {
                log.Info("No owner addresses listed");
                return new List<RecipientData>();
            }
            List<RecipientData> recipients = new List<RecipientData>();
            using (OdbcConnection Connection = new OdbcConnection(ConnectionString))
            using (OdbcCommand CommandWithHouseNo = Connection.CreateCommand())
            using (OdbcCommand CommandNoHouse = Connection.CreateCommand())
            {

                Connection.Open();
                //Command.CommandText = string.Format("SELECT TELEFON FROM ADR_KONSUMENT_201320105 WHERE POSTNR=? and HUSNR=?");// and CONTAINS(NAVN, ?)");
                //Command.CommandText = string.Format("SELECT TELEFON FROM ADR_KONSUM WHERE HUSNR=? and CONTAINS(NAVN, ?)");
                CommandWithHouseNo.CommandText = string.Format("SELECT "
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
                                                    + ",ISNULL(FR.KON_DMID, 0) KON_DMID "
                                                    + "FROM ADR_KONSUM_SEARCH FR WHERE POSTNR=? and HUSNR=? and CONTAINS(NAVN, ?)");
                CommandWithHouseNo.Parameters.Add("postnr", OdbcType.Int);
                CommandWithHouseNo.Parameters.Add("husnr", OdbcType.Int);
                CommandWithHouseNo.Parameters.Add("search", OdbcType.VarChar);

                CommandNoHouse.CommandText = string.Format("SELECT "
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
                                    + ",ISNULL(FR.KON_DMID, 0) KON_DMID "
                                    + "FROM ADR_KONSUM_SEARCH FR WHERE POSTNR=? and CONTAINS(NAVN, ?)");

                CommandNoHouse.Parameters.Add("postnr", OdbcType.Int);
                CommandNoHouse.Parameters.Add("search", OdbcType.VarChar);

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

                        //Set current command depending on husnr having a value or not
                        OdbcCommand Command = (husnr.HasValue ? CommandWithHouseNo : CommandNoHouse);

                        if (owner.Postnr > 0)
                            Command.Parameters["postnr"].Value = owner.Postnr;
                        else
                            Command.Parameters["postnr"].Value = DBNull.Value;

                        /*if (husnr.HasValue)
                            Command.Parameters["husnr"].Value = husnr;
                        else
                            Command.Parameters["husnr"].Value = DBNull.Value;*/
                        if (husnr.HasValue)
                        {
                            Command.Parameters["husnr"].Value = husnr;
                        }

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
                                        //ret.Add(rs.GetString(0));
                                }
                                if (personsFound == 0)
                                {
                                    /*recipients.Add(new RecipientData()
                                    {
                                        AlertTarget = owner,
                                        Name = "No inhabitants found",
                                    });*/
                                    NoMatchList.Add(owner);
                                }
                            }
                            double dill = (DateTime.Now - start).TotalMilliseconds;
                            log.DebugFormat("Executing (postnr={1}, husnr={2}, search={3}) {0:0}ms", dill, Command.Parameters["postnr"].Value, husnr.HasValue ? Command.Parameters["husnr"].Value : "<omit>", Command.Parameters["search"].Value);
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
                        NoMatchList.Add(owner);
                        log.InfoFormat("Failed to look up owner '{0}' due to single name", owner.Navn);
                    }
                }
                log.InfoFormat("Found {0} owners based on {1} OwnerAddresses, owning {2} mobile and {3} fixed phones", recipients.Count, ownerAddresses.Count, mobilePhones, fixedPhones);

                Connection.Close();

                log.InfoFormat("Preparation: {0:0} ms\texecution: {1:0} ms", prepare, sql);
            }

            return recipients;

        }
        public IEnumerable<RecipientData> GetMatchingOwnerAddressesNew(String ConnectionString, List<OwnerAddress> ownerAddresses)
        {
            NoMatchList = new List<OwnerAddress>();
            if (ownerAddresses.Count == 0)
            {
                log.Info("No owner addresses listed");
                return new List<RecipientData>();
            }

            string selectFields = @"
                ISNULL(FR.KOMMUNENR,0) KOMMUNENR 
                ,ISNULL(FR.GATEKODE,0) GATEKODE 
                ,ISNULL(FR.HUSNR,0) HUSNR 
                ,ISNULL(FR.OPPGANG,'') OPPGANG 
                ,ISNULL(FR.NAVN,'') NAVN 
                ,ISNULL(FR.LAT,0) LAT 
                ,ISNULL(FR.LON,0) LON 
                ,ISNULL(FR.BEDRIFT,0) BEDRIFT 
                ,ISNULL(FR.ADRESSE,'') ADRESSE 
                ,ISNULL(FR.POSTNR,0) POSTNR 
                ,ISNULL(FR.POSTSTED,'') POSTSTED 
                ,ISNULL(FR.MOBIL,'') MOBIL 
                ,ISNULL(FR.TELEFON,'') TELEFON 
                ,ISNULL(FR.KON_DMID, 0) KON_DMID
                ,ISNULL(FR.FØDTÅR, 0) FØDTÅR";


            List<RecipientData> recipients = new List<RecipientData>();
            using (OdbcConnection Connection = new OdbcConnection(ConnectionString))
            using (OdbcCommand Command = Connection.CreateCommand())
            {
                Connection.Open();

                DateTime start;

                double prepare = 0;
                double sql = 0;
                int mobilePhones = 0;
                int fixedPhones = 0;

                foreach (OwnerAddress ownerAddress in ownerAddresses)
                {
                    start = DateTime.Now;
                    string searchString = GetSearchString(ownerAddress.Navn);
                    if (searchString != null)
                    {
                        DateTime DateOfBirth;
                        int? husnr = null;
                        int birthdate = 0;

                        husnr = GetHouseNr(ownerAddress.Adresselinje1 + ownerAddress.Adresselinje2);

                        if (ownerAddress.DateOfBirth == null)
                            ownerAddress.DateOfBirth = "";

                        if (ownerAddress.DateOfBirth.Length == 6 && DateTime.TryParseExact(ownerAddress.DateOfBirth, "ddMMyy", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out DateOfBirth))
                        {
                            // check if date of birth is valid. Only persons of age 16+ should be in the list, so if the date suggests person is <16, subtract 100 years
                            if (DateOfBirth > DateTime.Now.AddYears(-16))
                                DateOfBirth = DateOfBirth.AddYears(-100);
                            birthdate = int.Parse(DateOfBirth.ToString("yyyyMMdd"));
                        }
                        else if (ownerAddress.DateOfBirth.Length == 8 && DateTime.TryParseExact(ownerAddress.DateOfBirth, "yyyyMMdd", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out DateOfBirth))
                            birthdate = int.Parse(DateOfBirth.ToString("yyyyMMdd"));
                        else if (ownerAddress.DateOfBirth.Length == 8 && DateTime.TryParseExact(ownerAddress.DateOfBirth, "ddMMyyyy", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.AssumeLocal, out DateOfBirth))
                            birthdate = int.Parse(DateOfBirth.ToString("yyyyMMdd"));
                        else if (!int.TryParse(ownerAddress.DateOfBirth, out birthdate))
                            birthdate = 0;

                        // clear all parameters for each owner
                        Command.Parameters.Clear();

                        if (birthdate > 0 && husnr.HasValue) // got all fields, search for birtdate, name + postnr, husnr, name + postnr, husnr, birthdate
                        {
                            Command.CommandText = String.Format(@"
                                SELECT {0}
                                FROM ADR_KONSUM_SEARCH FR WHERE [FØDTÅR]=? and CONTAINS(NAVN, ?)
                                UNION SELECT {0}
                                FROM ADR_KONSUM_SEARCH FR WHERE [FØDTÅR] IS NULL and POSTNR=? and HUSNR=? and CONTAINS(NAVN, ?)
                                UNION SELECT {0}
                                FROM ADR_KONSUM_SEARCH FR WHERE [FØDTÅR]=? AND POSTNR=? AND HUSNR=?", selectFields);
                            Command.Parameters.Add("birthdate", OdbcType.Int).Value = birthdate;
                            Command.Parameters.Add("search", OdbcType.VarChar).Value = searchString;
                            Command.Parameters.Add("postnr", OdbcType.Int).Value = ownerAddress.Postnr;
                            Command.Parameters.Add("husnr", OdbcType.Int).Value = husnr;
                            Command.Parameters.Add("search", OdbcType.VarChar).Value = searchString;
                            Command.Parameters.Add("birthdate", OdbcType.Int).Value = birthdate;
                            Command.Parameters.Add("postnr", OdbcType.Int).Value = ownerAddress.Postnr;
                            Command.Parameters.Add("husnr", OdbcType.Int).Value = husnr;
                        }
                        else if (birthdate > 0) // no housenr, search for birthdate, name + posrnt, name
                        {
                            Command.CommandText = String.Format(@"
                                SELECT {0}
                                FROM ADR_KONSUM_SEARCH FR WHERE [FØDTÅR]=? and CONTAINS(NAVN, ?)
                                UNION SELECT {0}
                                FROM ADR_KONSUM_SEARCH FR WHERE [FØDTÅR] IS NULL and POSTNR=? and CONTAINS(NAVN, ?)", selectFields);
                            Command.Parameters.Add("birthdate", OdbcType.Int).Value = birthdate;
                            Command.Parameters.Add("search", OdbcType.VarChar).Value = searchString;
                            Command.Parameters.Add("postnr", OdbcType.Int).Value = ownerAddress.Postnr;
                            Command.Parameters.Add("search", OdbcType.VarChar).Value = searchString;
                        }
                        else if (husnr.HasValue) // no birthdate, search for postnr, husnr, name
                        {
                            Command.CommandText = String.Format(@"
                                SELECT {0}
                                FROM ADR_KONSUM_SEARCH FR WHERE POSTNR=? and HUSNR=? and CONTAINS(NAVN, ?)", selectFields);
                            Command.Parameters.Add("postnr", OdbcType.Int).Value = ownerAddress.Postnr;
                            Command.Parameters.Add("husnr", OdbcType.Int).Value = husnr;
                            Command.Parameters.Add("search", OdbcType.VarChar).Value = searchString;
                        }
                        else // no birthdate, no housenr, search only postnr, name
                        {
                            Command.CommandText = String.Format(@"
                                SELECT {0}
                                FROM ADR_KONSUM_SEARCH FR WHERE POSTNR=? and CONTAINS(NAVN, ?)", selectFields);
                            Command.Parameters.Add("postnr", OdbcType.Int).Value = ownerAddress.Postnr;
                            Command.Parameters.Add("search", OdbcType.VarChar).Value = searchString;
                        }

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
                                            AlertTarget = ownerAddress,

                                            Name = rs.GetString(rs.GetOrdinal("NAVN")),
                                            Endpoints = new List<Endpoint>(),
                                            Lon = rs.IsDBNull(rs.GetOrdinal("LAT")) ? 0 : rs.GetDouble(rs.GetOrdinal("LAT")),
                                            Lat = rs.IsDBNull(rs.GetOrdinal("LON")) ? 0 : rs.GetDouble(rs.GetOrdinal("LON")),
                                            Company = (rs.GetInt16(rs.GetOrdinal("BEDRIFT")) == 1),
                                            Address = rs.GetString(rs.GetOrdinal("ADRESSE")),
                                            Postno = Int32.Parse(rs.GetString(rs.GetOrdinal("POSTNR"))),
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
                                if (personsFound == 0)
                                {
                                    NoMatchList.Add(ownerAddress);
                                }
                            }
                            double dill = (DateTime.Now - start).TotalMilliseconds;
                            log.DebugFormat("Executing (postnr={1}, husnr={2}, search={3}) {0:0}ms", dill, Command.Parameters["postnr"].Value, husnr.HasValue ? Command.Parameters["husnr"].Value : "<omit>", Command.Parameters["search"].Value);
                            sql += dill;
                        }
                        catch (OdbcException ex)
                        {
                            Console.WriteLine("ODBC feil: {0}", ex);
                        }
                    }
                    else
                    {
                        NoMatchList.Add(ownerAddress);
                        log.InfoFormat("Failed to look up owner '{0}' due to single name", ownerAddress.Navn);
                    }
                }
                log.InfoFormat("Found {0} owners based on {1} OwnerAddresses, owning {2} mobile and {3} fixed phones", recipients.Count, ownerAddresses.Count, mobilePhones, fixedPhones);

                Connection.Close();

                log.InfoFormat("Preparation: {0:0} ms\texecution: {1:0} ms", prepare, sql);
            }

            return recipients;

        }



        #endregion

        /// <summary>
        /// Get fulltext index search string based on name
        /// </summary>
        /// <param name="name">The full name string</param>
        /// <returns>The first two names with more than 1 letter, NULL if not at least two names with 2+ characters is found</returns>
        public static string GetSearchString(string name)
        {
            string[] names = name.Split(new string[] { " " }, StringSplitOptions.RemoveEmptyEntries);

            int counter = 0;
            string search = "";

            foreach (string s in names)
            {
                if (s.Length > 1)
                {
                    if (search.Length > 0)
                    {
                        search += " and " + s;
                    }
                    else
                    {
                        search += s;
                    }

                    if (++counter == 2) // fount 2 names
                        return search;
                }
            }
            return null; // did not find 2 names
        }

        /// <summary>
        /// Find house number based on address string
        /// </summary>
        /// <param name="s">Full address string</param>
        /// <returns>Returns the first digit and any immediately following digits after a word boundry unless the address string contains postboks</returns>
        private static int? GetHouseNr(string s)
        {
            Match m = Regex.Match(s, @"\b\d+");
            if (m.Success && !Regex.IsMatch(s, @"postboks", RegexOptions.IgnoreCase))
            {
                int housenr;
                if (int.TryParse(m.Value, out housenr))
                    return housenr;
                else
                    return null;
            }
            else
                return null;
        }
    }
}
