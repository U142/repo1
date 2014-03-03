using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using UmsPas.be.wdmb.ws;
using System.Xml.Serialization;
using System.Xml;
using System.IO;
using System.Text;

namespace com.ums.PAS.Address.gab
{
    public class AddressSearchBE
    {
        public string login { get; private set; }
        public string password { get; private set; }
        public string serviceUrl { get; private set; }

        public AddressSearchBE()
        {
            login = "UMSTest";
            password = "UmS.T!st";
            serviceUrl = "https://ws.wdmb.be/ws/DTrix.asmx";
        }

       /* public List<UGabResult> getCities(string searchString, int count)
        {
            List<UGabResult> ret = new List<UGabResult>();

            dtrix adrSearch = new dtrix();
            adrSearch.login = login;
            adrSearch.password = password;
            adrSearch.service = "road65_quickzip";

            List<dtrixP> searchParams = new List<dtrixP>();

            searchParams.Add(new dtrixP() { name = "zipcity", Value = searchString });

            adrSearch.p = searchParams.ToArray();

            try
            {
                DTrix searchSvc = new DTrix();
                searchSvc.Url = serviceUrl;

                // Generate XML
                XmlSerializer s = new XmlSerializer(typeof(dtrix));
                TextWriter w = new StringWriter();

                s.Serialize(w, adrSearch);

                XmlDocument doc = new XmlDocument();
                doc.PreserveWhitespace = true;
                doc.LoadXml(w.ToString());

                XmlNode xmlInput = doc.SelectSingleNode("dtrix");
                XmlNode xmlResult = searchSvc.run(xmlInput);

                foreach (XmlNode result in xmlResult.SelectNodes("row"))
                {
                    ret.Add(new Tuple<string, string>(String.Format("{0} / {1}", result.Attributes["zipcode"].Value, result.Attributes["cityname"].Value), result.Attributes["zipcode"].Value));
                }
            }
            catch
            {
            }

            return ret;
        }

        public List<string, string> getStreets(string searchString, int count, string zipCode)
        {
            List<Tuple<string, string>> ret = new List<Tuple<string, string>>();

            dtrix adrSearch = new dtrix();
            adrSearch.login = login;
            adrSearch.password = password;
            adrSearch.service = "road65_quickstreet";

            List<dtrixP> searchParams = new List<dtrixP>();

            searchParams.Add(new dtrixP() { name = "zipcode", Value = zipCode });
            searchParams.Add(new dtrixP() { name = "streetname", Value = searchString.Trim() });

            adrSearch.p = searchParams.ToArray();

            try
            {
                DTrix searchSvc = new DTrix();
                searchSvc.Url = serviceUrl;

                // Generate XML
                XmlSerializer s = new XmlSerializer(typeof(dtrix));
                TextWriter w = new StringWriter();

                s.Serialize(w, adrSearch);

                XmlDocument doc = new XmlDocument();
                doc.PreserveWhitespace = true;
                doc.LoadXml(w.ToString());

                XmlNode xmlInput = doc.SelectSingleNode("dtrix");
                XmlNode xmlResult = searchSvc.run(xmlInput);

                foreach (XmlNode result in xmlResult.SelectNodes("row"))
                {
                    ret.Add(new Tuple<string, string>(result.Attributes["streetname"].Value, result.Attributes["street_id"].Value));
                }
            }
            catch
            {
            }

            return ret;
        }

        /// <summary>
        /// Get cities either starting with text or for a 4 digit zip code
        /// </summary>
        /// <param name="searchString"></param>
        /// <param name="count"></param>
        /// <returns></returns>
        public List<UGabResult> getCitiesFull(string searchString, int count)
        {
            throw new NotImplementedException("This method should not be used...");

            List<UGabResult> ret = new List<UGabResult>();

            dtrix adrSearch = new dtrix();
            adrSearch.login = login;
            adrSearch.password = password;
            adrSearch.service = "road65";

            List<dtrixP> searchParams = new List<dtrixP>();
            int zip;
            if (searchString.Trim().Length == 4 && int.TryParse(searchString.Trim(), out zip))
                searchParams.Add(new dtrixP() { name = "zipcode", Value = searchString.Trim() });
            else
                searchParams.Add(new dtrixP() { name = "city", Value = searchString });
            searchParams.Add(new dtrixP() { name = "breakwithattributes", Value = "1" });
            searchParams.Add(new dtrixP() { name = "interactive", Value = "1" });

            adrSearch.p = searchParams.ToArray();

            try
            {
                DTrix searchSvc = new DTrix();
                searchSvc.Url = serviceUrl;

                // Generate XML
                XmlSerializer s = new XmlSerializer(typeof(dtrix));
                TextWriter w = new StringWriter();

                s.Serialize(w, adrSearch);

                XmlDocument doc = new XmlDocument();
                doc.PreserveWhitespace = true;
                doc.LoadXml(w.ToString());

                XmlNode xmlInput = doc.SelectSingleNode("dtrix");
                XmlNode xmlResult = searchSvc.run(xmlInput);

                road65 searchResult;

                if ((searchResult = ConvertNode<road65>(xmlResult)) != null)
                {
                    if (searchResult.info_type == info_type.BREAK && searchResult.@break.type == breaktype.cities)
                    {
                        foreach (breakLine line in searchResult.@break.line)
                        {
                            // TODO: split STATUS to obtain language and filter on language (if possible)

                            if (line.zipcode.Trim().Length > 0 && line.city.Trim().Length > 0)
                            {
                                ret.Add(new Tuple<string, string>(String.Format("{0} / {1}", line.zipcode, line.city), line.zipcode));
                            }
                        }
                    }
                }
            }
            catch
            {
            }

            return ret;
        }

        /// <summary>
        /// Get streets beginning with text based on a zip code
        /// </summary>
        /// <param name="searchString"></param>
        /// <param name="count"></param>
        /// <param name="zipCode"></param>
        /// <returns></returns>
        public List<Tuple<string, string>> getStreetsFull(string searchString, int count, string zipCode)
        {
            throw new NotImplementedException("This method should not be used...");

            List<Tuple<string, string>> ret = new List<Tuple<string, string>>();

            dtrix adrSearch = new dtrix();
            adrSearch.login = login;
            adrSearch.password = password;
            adrSearch.service = "road65";

            List<dtrixP> searchParams = new List<dtrixP>();
            searchParams.Add(new dtrixP() { name = "zipcode", Value = zipCode });
            searchParams.Add(new dtrixP() { name = "breakwithattributes", Value = "1" });
            searchParams.Add(new dtrixP() { name = "interactive", Value = "1" });
            searchParams.Add(new dtrixP() { name = "streetname", Value = searchString.Trim() });

            adrSearch.p = searchParams.ToArray();

            try
            {
                DTrix searchSvc = new DTrix();
                searchSvc.Url = serviceUrl;

                // Generate XML
                XmlSerializer s = new XmlSerializer(typeof(dtrix));
                TextWriter w = new StringWriter();

                s.Serialize(w, adrSearch);

                XmlDocument doc = new XmlDocument();
                doc.PreserveWhitespace = true;
                doc.LoadXml(w.ToString());

                XmlNode xmlInput = doc.SelectSingleNode("dtrix");
                XmlNode xmlResult = searchSvc.run(xmlInput);

                road65 searchResult;

                if ((searchResult = ConvertNode<road65>(xmlResult)) != null)
                {
                    if (searchResult.info_type == info_type.BREAK && searchResult.@break.type == breaktype.streets)
                    {
                        foreach (breakLine line in searchResult.@break.line)
                        {
                            // TODO: split STATUS to obtain language and filter on language (if possible)

                            if (line.zipcode.Trim().Length > 0 && line.city.Trim().Length > 0 && line.zipcode.Equals(zipCode))
                            {
                                ret.Add(new Tuple<string, string>(line.streetname, line.streetname));
                            }
                        }
                    }
                    else if (searchResult.info_type == info_type.DONE)
                    {
                        if (searchResult.result != null)
                        {
                            ret.Add(new Tuple<string, string>(searchResult.result.streetname, searchResult.result.street_id));
                        }
                    }
                }
            }
            catch
            {
            }

            return ret;
        }*/

        /// <summary>
        /// Look up an address and retrieve geocoding and street id etc. also used to determine if an address exists
        /// </summary>
        /// <param name="zip"></param>
        /// <param name="streetname"></param>
        /// <param name="houseno"></param>
        /// <returns></returns>
        public UGabResult houseExist(string zip, string city, string streetname, string houseno)
        {
            dtrix adrSearch = new dtrix();
            adrSearch.login = login;
            adrSearch.password = password;
            adrSearch.service = "road65";

            List<dtrixP> searchParams = new List<dtrixP>();
            searchParams.Add(new dtrixP() { name = "zipcode", Value = zip });
            searchParams.Add(new dtrixP() { name = "breakwithattributes", Value = "1" });
            searchParams.Add(new dtrixP() { name = "interactive", Value = "1" });
            searchParams.Add(new dtrixP() { name = "streetname", Value = streetname });
            searchParams.Add(new dtrixP() { name = "city", Value = city });
            searchParams.Add(new dtrixP() { name = "house_number", Value = houseno });

            adrSearch.p = searchParams.ToArray();

            try
            {
                DTrix searchSvc = new DTrix();
                searchSvc.Url = serviceUrl;

                // Generate XML
                XmlSerializer s = new XmlSerializer(typeof(dtrix));
                TextWriter w = new StringWriter();

                s.Serialize(w, adrSearch);

                XmlDocument doc = new XmlDocument();
                doc.PreserveWhitespace = true;
                doc.LoadXml(w.ToString());

                XmlNode xmlInput = doc.SelectSingleNode("dtrix");
                XmlNode xmlResult = searchSvc.run(xmlInput);

                road65 searchResult;

                if ((searchResult = ConvertNode<road65>(xmlResult)) != null)
                {
                    if (searchResult.info_type == info_type.DONE)
                    {
                        if (searchResult.result != null)
                        {
                            if (searchResult.result.r65_extra1.Equals("Housenumber exists", StringComparison.OrdinalIgnoreCase))
                            {
                                double lat, lon;

                                if (GetHouseLatLon(xmlResult, out lat, out lon))
                                {
                                    return new UGabResult()
                                    {
                                       
                                    
                                        streetid = int.Parse(searchResult.result.street_id),
                                        postno = searchResult.result.zipcode,
                                        municipalid = int.Parse(searchResult.result.city_id),
                                        lat = lat,
                                        lon = lon
                                    };
                                }
                            }
                            else if (searchResult.result.r65_extra1.Equals("Housenumber is in range", StringComparison.OrdinalIgnoreCase))
                            {
                                return new UGabResult()
                                {
                                    Result = ADRSEARCHRESULT.HOUSENO_NOT_FOUND
                                };
                            }
                            else if (searchResult.result.r65_extra1.Equals("Housenumber is not in range", StringComparison.OrdinalIgnoreCase))
                            {
                                return new UGabResult()
                                {
                                    Result = ADRSEARCHRESULT.HOUSENO_NOT_FOUND
                                };
                            }
                        }
                    }
                    else if (searchResult.info_type == info_type.BREAK)
                    {
                        return new UGabResult()
                        {
                            Result = ADRSEARCHRESULT.STREET_NOT_FOUND
                        };
                    }
                }
            }
            catch
            {
                return new UGabResult()
                {
                    Result = ADRSEARCHRESULT.EXCEPTION
                };
            }

            // no hits for whatever reason
            return new UGabResult()
            {
                Result = ADRSEARCHRESULT.UNKOWN
            };
        }

        /// <summary>
        /// Convert an XML node to a class
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="node"></param>
        /// <returns></returns>
        private static T ConvertNode<T>(XmlNode node) where T : class
        {
            try
            {
                MemoryStream stm = new MemoryStream();
                StreamWriter stw = new StreamWriter(stm);
                stw.Write(node.OuterXml);
                stw.Flush();

                stm.Position = 0;

                XmlSerializer ser = new XmlSerializer(typeof(T));
                T result = (ser.Deserialize(stm) as T);

                return result;
            }
            catch (Exception e)
            {
                return null;
            }
        }


        /// <summary>
        /// Retrieve latitude and longitude for an address
        /// </summary>
        /// <param name="xmlresult"></param>
        /// <param name="lat"></param>
        /// <param name="lon"></param>
        /// <returns></returns>
        private bool GetHouseLatLon(XmlNode xmlresult, out double lat, out double lon)
        {
            lat = 0;
            lon = 0;

            try
            {
                lon = double.Parse(xmlresult.SelectSingleNode("result/r65_extraInfo/geoHouseInfo/longitude").InnerText, System.Globalization.CultureInfo.InvariantCulture);
                lat = double.Parse(xmlresult.SelectSingleNode("result/r65_extraInfo/geoHouseInfo/latitude").InnerText, System.Globalization.CultureInfo.InvariantCulture);

                return true;
            }
            catch
            {
                return false;
            }
        }
    }
}