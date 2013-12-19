﻿using System;
using System.Linq;
using System.Web;
using System.Collections;
using System.Collections.Generic;
using System.Xml.Linq;
using System.Net;
using System.Xml;
using System.IO;
using System.Text;
using com.ums.UmsCommon;
using com.ums.PAS.Address;
using com.ums.UmsDbLib;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace com.ums.PAS.Address.gab
{
    /*UGland search parameters*/
    public class UGabSearchParams
    {
        public string sz_uid;
        public string sz_pwd;
        public int n_count = 20;
        public int n_sort = 1;
        public string sz_language;
        public string sz_address;
        public string sz_no;
        public string sz_postno;
        public string sz_postarea;
        public string sz_region;
        public string sz_country;

        public UGabSearchParams()
        {
            
        }
    }


    public class UGabFromPoint : IAddressSearch
    {
        protected ULOGONINFO m_logon;
        protected UMapPoint m_point;
        public UGabFromPoint(ref ULOGONINFO logon, ref UMapPoint p)
        {
            m_logon = logon;
            m_point = p;
        }
        public IAddressResults Find()
        {
            String lang = "1";
            if (m_logon.sz_stdcc.Equals("0047"))
                lang = "1";
            else if (m_logon.sz_stdcc.Equals("0046"))
                lang = "2";
            else if (m_logon.sz_stdcc.Equals("0045"))
                lang = "5";


            string sz_server = "http://api.fleximap.com/servlet/FlexiMap";
            string sz_params = String.Format(UCommon.UGlobalizationInfo,
                                "UID=" + "UMS" +
                                "&UPA=" + "MSG" +
                                "&Language=" + lang +
                                "&Sort=0" +
                                "&Count=1" +
                                "&Lon={0}" +
                                "&Lat={1}" +
                                "&Src=1",
                                m_point.lon,
                                m_point.lat);

            String sz_request = String.Format("{0}?{1}", sz_server, sz_params);

            string xmldata;
            try
            {
                UTF8Encoding enc = new UTF8Encoding();
                Byte[] bytes = Encoding.GetEncoding("iso-8859-1").GetBytes(sz_request);
                String sz_request_8859 = Encoding.GetEncoding("iso-8859-1").GetString(bytes);

                HttpWebRequest web = (HttpWebRequest)HttpWebRequest.Create(sz_server);
                byte[] postDataBytes = Encoding.GetEncoding("iso-8859-1").GetBytes(sz_params);
                // HTTP Request
                web.Method = "POST";
                web.ContentType = "application/x-www-form-urlencoded";
                web.ContentLength = postDataBytes.Length;
                Stream requestStream = web.GetRequestStream();
                requestStream.Write(postDataBytes, 0, postDataBytes.Length);
                requestStream.Close();

                HttpWebResponse response = (HttpWebResponse)web.GetResponse();
                StreamReader r = new StreamReader(response.GetResponseStream(), Encoding.GetEncoding("iso-8859-1"));
                xmldata = r.ReadToEnd();
                r.Close();
                response.Close();
            }
            catch (Exception)
            {
                throw;
            }

            Byte[] to_utf8 = Encoding.GetEncoding("utf-8").GetBytes(xmldata);
            string xmldata_utf8 = Encoding.UTF8.GetString(to_utf8);


            XmlDocument doc = new XmlDocument();
            try
            {
                doc.LoadXml(xmldata_utf8);
                return parse(ref doc);
            }
            catch (Exception)
            {
                //UGabSearchResultList list = new UGabSearchResultList();
                //list.setError(xmldata, e.Message);
                //return list;
                throw;
            }
        }

        public UGabResultFromPoint parse(ref XmlDocument doc)
        {
            UGabResultFromPoint res = new UGabResultFromPoint();
            XmlNodeList nl = doc.GetElementsByTagName("Street");
            if (nl.Count > 0)
            {
                XmlNode node_street = nl.Item(0);
                res.name = node_street.Attributes["Name"].Value;
                res.region = node_street.Attributes["Region"].Value;
                String StreetID = node_street.Attributes["Id"].Value;
                if (StreetID != null && StreetID.Length >= 5)
                {
                    //kan ikke sette streetid og kommunenr da verdien ikke har leading zero
                    res.municipalid = 0;
                    res.streetid = 0;
                }
                else
                {
                    res.municipalid = 0;
                    res.streetid = 0;
                }

                XmlNode node_house = node_street.FirstChild;
                if (node_house != null)
                {
                    res.no = node_house.Attributes["Name"].Value;
                    res.distance = int.Parse(node_house.Attributes["Distance"].Value);
                    res.lon = double.Parse(node_house.Attributes["Lon"].Value, UCommon.UGlobalizationInfo);
                    res.lat = double.Parse(node_house.Attributes["Lat"].Value, UCommon.UGlobalizationInfo);
                }
            }
            return res;
        }
    }


    /*
     * sz_url = "http://api.fleximap.com/servlet/FlexiMap"
        Dim sz_params
        sz_params = "UID=" & sz_userid & "&UPA=" & sz_passwd & "&Language=" & sz_language & "&Street=" & sz_address & "&House=" & sz_no & "&Region=" & sz_region & "&PostNo=" & sz_postno & "&Post=" & sz_postarea & "&count=" & n_count & "&Sort=" & n_sort & "&Unique=" & n_unique
        
     * input : searchparams and logoninfo
     * returns a collection of UGabResult (in a UGabSearchResultList)
     */

    public class UGabSearch : IAddressSearch
    {
        protected UGabSearchParams m_params;
        protected ULOGONINFO m_logoninfo;

        public UGabSearch(ref UGabSearchParams searchparams, ref ULOGONINFO logoninfo)
        {
            m_logoninfo = logoninfo;
            m_params = searchparams;
        }

        public IAddressResults Find()
        {
            //UGabSearchResultList list = new UGabSearchResultList();
            UmsDb db = new UmsDb();
            try
            {
                db.CheckLogon(ref m_logoninfo, true);
            }
            catch(Exception)
            {
                throw;
            }
            int n_unique = 0;
            if (m_params.sz_no.Length > 0)
                n_unique = 1;
            
            string sz_server;
            string sz_params;
            string authorizationHeader = ""; // For http authentication
            string verb = "POST";
            string postNoFilter = null;

            if(m_params.sz_country.Equals("SE")) {
                sz_server = "http://maps.metria.se/geokodning/Geocode";
                sz_params = "address=" + m_params.sz_address + " " + m_params.sz_no + ", " +
                                    m_params.sz_postno + " " + m_params.sz_postarea + " " +
                                    m_params.sz_region + ", Sverige&scheme=adress_WGS84";
                authorizationHeader = "Basic " + Convert.ToBase64String(Encoding.Default.GetBytes("umsas:Zyl00pon"));

            } /*else if(m_params.sz_country.Equals("NO")) {
                sz_server = "http://services2.geodataonline.no/arcgis/rest/services/Geosok/GeosokLokasjon/GeocodeServer/findAddressCandidates";
                sz_params = String.Format("StreetName={0}+{1}&Postal={2}&PostalArea={3}&Muni={4}&outFields=*&outSR=4326&searchExtent=&f=pjson&token={5}",
                    m_params.sz_address,
                    m_params.sz_no,
                    m_params.sz_postno,
                    m_params.sz_postarea,
                    m_params.sz_region,
                    "_rcLdtkkHFdW3CEZL8qr5GfSO_AjuMdPr3BvR0P4wp0BK0BZ2DX2pVztrTQF2thc8pyFtVT9CfxwtTpei7Wb5w..");
            }
            else if (m_params.sz_country.Equals("NO"))
            {
                    sz_server = "http://api.fleximap.com/servlet/FlexiMap";
                    sz_params = "UID=" + m_params.sz_uid +
                                "&UPA=" + m_params.sz_pwd +
                                "&Language=" + m_params.sz_language +
                                "&Street=" + m_params.sz_address +
                                "&House=" + m_params.sz_no +
                                "&Region=" + m_params.sz_region +
                                "&PostNo=" + m_params.sz_postno +
                                "&Post=" + m_params.sz_postarea +
                                "&count=" + m_params.n_count +
                                "&Sort=" + m_params.n_sort +
                                "&Unique=" + n_unique;
            }*/
            else if (m_params.sz_country.Equals("NO"))
            {
                sz_server = "http://ws.geodataonline.no/search/geodataservice/autocomplete";

                if (m_params.sz_address.Trim().Length > 0 ||
                    m_params.sz_no.Trim().Length > 0 ||
                    m_params.sz_postarea.Trim().Length > 0 ||
                    m_params.sz_region.Trim().Length > 0)
                {
                    // search address and region, skip postno (regardless of value)
                    sz_params = String.Format("token={0}&query={1}+{2}+{3}+{4}&format=3",
                        "QfhsKalPzFacjSQy6VBs74rrb-WZCPV5nVUB9Un663EusjKC7htdicDfwG-fDrBs",
                        m_params.sz_address,
                        m_params.sz_no,
                        m_params.sz_postarea,
                        m_params.sz_region);
                }
                else if (m_params.sz_postno.Trim().Length > 0)
                {
                    // only postno, search for that
                    sz_params = String.Format("token={0}&query={1}&format=3",
                        "QfhsKalPzFacjSQy6VBs74rrb-WZCPV5nVUB9Un663EusjKC7htdicDfwG-fDrBs",
                        m_params.sz_postno);
                }
                else
                {
                    sz_params = String.Format("token={0}&query=&format=3",
                        "QfhsKalPzFacjSQy6VBs74rrb-WZCPV5nVUB9Un663EusjKC7htdicDfwG-fDrBs");
                }

                if (m_params.sz_postno.Trim().Length > 0) // use postno filter for results
                {
                    int postno;
                    if(int.TryParse(m_params.sz_postno.Trim(), out postno))
                        postNoFilter = postno.ToString();
                    else
                        postNoFilter = m_params.sz_postno.Trim();
                }

                verb = "GET";
            }
            else if (m_params.sz_country.Equals("DK"))
            {
                // Search postno if address and house number is empty and either postno or postarea is specified
                if (m_params.sz_address.Trim().Length == 0
                    && m_params.sz_no.Trim().Length == 0
                    && (m_params.sz_postno.Trim().Length > 0 || m_params.sz_postarea.Trim().Length > 0))
                {
                    sz_server = "http://ums.maplytic.no/table/postzone.geojson"; //?postname=…&postcode=...&limit=10
                    sz_params = string.Format("limit=10{0}{1}"
                        , m_params.sz_postno.Length > 0 ? "&postnr=" + m_params.sz_postno : ""
                        , m_params.sz_postarea.Length > 0 ? "&postname=" + m_params.sz_postarea + "*" : "");
                }
                else // all other searches, use street search
                {
                    sz_server = "http://ums.maplytic.no/table/address.geojson";
                    sz_params = string.Format("limit=20{0}{1}{2}{3}"
                        , m_params.sz_address.Length > 0 ? "&street=" + GenerateWildcardString(m_params.sz_address) : ""
                        , m_params.sz_no.Length > 0 ? "&streetnr=" + m_params.sz_no : ""
                        , m_params.sz_postno.Length > 0 ? "&postnr=" + m_params.sz_postno : ""
                        , m_params.sz_postarea.Length > 0 ? "&postname=" + m_params.sz_postarea + "*" : "");
                }

                authorizationHeader = "Basic " + Convert.ToBase64String(Encoding.Default.GetBytes("ums:ums"));
                verb = "GET";
            }
            else
            {
                sz_server = "http://tasks.arcgisonline.com/ArcGIS/rest/services/Locators/TA_Address_EU/GeocodeServer/findAddressCandidates";
                sz_params = String.Format("Address={0}+{1}&Postcode={2}&City={3}&Country={4}&outFields=*&outSR=4326&searchExtent=&f=pjson",
                    m_params.sz_address,
                    m_params.sz_no,
                    m_params.sz_postno,
                    m_params.sz_postarea,
                    m_params.sz_country);
            }
            /*} else {
                    sz_server = "http://api.fleximap.com/servlet/FlexiMap";
                    sz_params = "UID=" + m_params.sz_uid +
                                "&UPA=" + m_params.sz_pwd +
                                "&Language=" + m_params.sz_language +
                                "&Street=" + m_params.sz_address +
                                "&House=" + m_params.sz_no +
                                "&Region=" + m_params.sz_region +
                                "&PostNo=" + m_params.sz_postno +
                                "&Post=" + m_params.sz_postarea +
                                "&count=" + m_params.n_count +
                                "&Sort=" + m_params.n_sort +
                                "&Unique=" + n_unique;
            }*/
            //Byte[] param_bytes = encoder.GetBytes(sz_params);
            //string sz_params_8859 = Encoding.GetEncoding("iso-8859-1").GetString(param_bytes);


            String sz_request = String.Format("{0}?{1}", sz_server, sz_params);

            string xmldata;
            try
            {
                if (verb.Equals("GET"))
                {
                    System.Net.WebRequest req = System.Net.WebRequest.Create(sz_request);
                    req.Headers["Authorization"] = authorizationHeader;
                    System.Net.WebResponse resp = req.GetResponse();
                    System.IO.StreamReader sr = new System.IO.StreamReader(resp.GetResponseStream());
                    xmldata = sr.ReadToEnd().Trim();
                }
                else
                {
                    UTF8Encoding enc = new UTF8Encoding();
                    Byte[] bytes = Encoding.GetEncoding("iso-8859-1").GetBytes(sz_request);
                    String sz_request_8859 = Encoding.GetEncoding("iso-8859-1").GetString(bytes);

                    HttpWebRequest web = (HttpWebRequest)HttpWebRequest.Create(sz_server);
                    byte[] postDataBytes = Encoding.GetEncoding("iso-8859-1").GetBytes(sz_params);
                    // HTTP Request
                    web.Method = "POST";
                    web.ContentType = "application/x-www-form-urlencoded";
                    web.ContentLength = postDataBytes.Length;
                    web.Referer = "https://secure.ums.no";
                    if (authorizationHeader.Length > 0)
                    {
                        web.Headers["Authorization"] = authorizationHeader;
                    }

                    Stream requestStream = web.GetRequestStream();
                    requestStream.Write(postDataBytes, 0, postDataBytes.Length);
                    requestStream.Close();

                    HttpWebResponse response = (HttpWebResponse)web.GetResponse();
                    StreamReader r = new StreamReader(response.GetResponseStream(), Encoding.GetEncoding("iso-8859-1"));
                    xmldata = r.ReadToEnd();
                    r.Close();
                    response.Close();
                }
            }
            catch (WebException e)
            {
                //403 forbidden may be returned from destination if adrsearch is too general.
                //This and other forbidden messages are described in StatusDescription.
                //mark the list with this error to show it properly in client
                HttpWebResponse wr = (HttpWebResponse)e.Response;
                UGabSearchResultList list = new UGabSearchResultList();
                list.setError(e.Message, wr.StatusDescription);
                return list;
            }
            catch (Exception e)
            {
                throw;
            }

            Byte[] to_utf8 = Encoding.GetEncoding("utf-8").GetBytes(xmldata);
            string xmldata_utf8 = Encoding.UTF8.GetString(to_utf8);


            XmlDocument doc = new XmlDocument();
            try
            {
                if (m_params.sz_country.Equals("SE"))
                {
                    doc.LoadXml(xmldata_utf8);
                    return parseSE(ref doc);
                }
                else if (m_params.sz_country.Equals("NO"))
                {
                    //doc.LoadXml(xmldata_utf8);
                    //return parse(ref doc);
                    return parseJSONGeoAutocomplete(xmldata_utf8, postNoFilter);
                }
                else if (m_params.sz_country.Equals("DK"))
                {
                    return parseJSONNordeca(xmldata_utf8);
                }
                else
                {
                    //doc.LoadXml(xmldata_utf8);
                    //return parse(ref doc);
                    return parseJSONArcGIS(xmldata_utf8);
                }
            }
            catch (Exception e)
            {
                //check response headers if something went wrong
                //throw e;
                UGabSearchResultList list = new UGabSearchResultList();
                //list.sz_errortext = xmldata_utf8;
                //list.sz_exceptiontext = e.Message;
                list.setError(xmldata, e.Message);
                //r.Close();
                //response.Close();
                return list;
            }
        }
        
        public UGabSearchResultList parse(ref XmlDocument doc)
        {
            UGabSearchResultList list = new UGabSearchResultList();
            XmlElement AdrList = doc.DocumentElement;

            XmlNodeList nl_region   = AdrList.GetElementsByTagName(GABTYPE.Region.ToString());
            XmlNodeList nl_street   = AdrList.GetElementsByTagName(GABTYPE.Street.ToString());
            XmlNodeList nl_post = AdrList.GetElementsByTagName(GABTYPE.Post.ToString());

            IEnumerator en;
            UGabResult item;
            XmlNode node;

            /*REGIONS*/
            en = nl_region.GetEnumerator();
            while (en.MoveNext())
            {
                node = (XmlNode)en.Current;
                item = new UGabResult();
                item.match = float.Parse(node.Attributes["Match"].Value, UCommon.UGlobalizationInfo);
                item.name = node.Attributes["Name"].Value; // node.Attributes["Name"].Value;
                item.region = node.Attributes["Name"].Value;
                item.lon = float.Parse(node.Attributes["Lon"].Value, UCommon.UGlobalizationInfo);
                item.lat = float.Parse(node.Attributes["Lat"].Value, UCommon.UGlobalizationInfo);
                item.type = GABTYPE.Region;
                list.addLine(ref item);
            }

            /*Post numbers*/
            en = nl_post.GetEnumerator();
            while (en.MoveNext())
            {
                node = (XmlNode)en.Current;
                item = new UGabResult();
                item.match = float.Parse(node.Attributes["Match"].Value, UCommon.UGlobalizationInfo);
                item.name = node.Attributes["PostNo"].Value;
                item.postno = node.Attributes["PostNo"].Value;
                item.region = node.Attributes["Name"].Value;
                item.lon = float.Parse(node.Attributes["Lon"].Value, UCommon.UGlobalizationInfo);
                item.lat = float.Parse(node.Attributes["Lat"].Value, UCommon.UGlobalizationInfo);
                item.type = GABTYPE.Post;
                list.addLine(ref item);
            }

            /*Streets*/
            en = nl_street.GetEnumerator();
            while (en.MoveNext())
            {
                node = (XmlNode)en.Current;
                /*check for specific houses*/
                if (node.HasChildNodes)
                {
                    IEnumerator en_houses = node.GetEnumerator();
                    while (en_houses.MoveNext())
                    {
                        XmlNode house = (XmlNode)en_houses.Current;
                        item = new UGabResult();
                        item.match = float.Parse(node.Attributes["Match"].Value, UCommon.UGlobalizationInfo);
                        item.name = node.Attributes["Name"].Value + " " + house.Attributes["Name"].Value; //Streetname + housenumber
                        item.region = node.Attributes["Region"].Value;
                        item.lon = float.Parse(house.Attributes["Lon"].Value, UCommon.UGlobalizationInfo);
                        item.lat = float.Parse(house.Attributes["Lat"].Value, UCommon.UGlobalizationInfo);
                        item.type = GABTYPE.House;
                        list.addLine(ref item);
                    }
                }
                else
                {
                    item = new UGabResult();
                    item.match = float.Parse(node.Attributes["Match"].Value, UCommon.UGlobalizationInfo);
                    item.name = node.Attributes["Name"].Value;
                    item.region = node.Attributes["Region"].Value;
                    item.lon = float.Parse(node.Attributes["Lon"].Value, UCommon.UGlobalizationInfo);
                    item.lat = float.Parse(node.Attributes["Lat"].Value, UCommon.UGlobalizationInfo);
                    item.type = GABTYPE.Street;
                    list.addLine(ref item);
                }

            }
            list.finalize();
            return list;

        }

        public UGabSearchResultList parseSE(ref XmlDocument doc)
        {
            UGabSearchResultList list = new UGabSearchResultList();
            XmlElement AdrList = doc.DocumentElement;

            XmlNodeList nl_location = AdrList.GetElementsByTagName("location");

            IEnumerator en;
            UGabResult item;
            XmlNode node;

            en = nl_location.GetEnumerator();
            while (en.MoveNext())
            {
                node = (XmlNode)en.Current;
                String[] address = node.Attributes["address"].Value.Split(',');
                address = removeLeadingTrailingSpaces(address);
                item = new UGabResult();
                item.match = float.Parse(node.Attributes["rank"].Value, UCommon.UGlobalizationInfo);
                item.name = address[0];
                item.postno = address.Length > 2 ? tryParsePostNo(address[(address.Length - 2)])[0] : ""; // 2nd to last
                try
                {
                    item.region = address.Length > 2 ? tryParsePostNo(address[(address.Length - 2)])[1] : address.Length > 1 ? address[0] : "";
                }
                catch (Exception e)
                {
                    item.region = address[(address.Length - 2)];
                }

                item.lon = float.Parse(node.Attributes["x"].Value, UCommon.UGlobalizationInfo);
                item.lat = float.Parse(node.Attributes["y"].Value, UCommon.UGlobalizationInfo);
                item.type = GABTYPE.Street;
                list.addLine(ref item);

            }

            list.finalize();
            return list;

        }

        public UGabSearchResultList parseJSONGeoAutocomplete(string jsonData, string postNoFilter)
        {
            UGabSearchResultList list = new UGabSearchResultList();

            JObject obj = JObject.Parse(jsonData);

            foreach (JToken token in obj.SelectToken("hits").Children())
            {
                try
                {
                    JToken location = token.SelectToken("location");
                    JToken numbers = token.SelectToken("numbers");

                    // house
                    if (numbers != null && numbers.HasValues)
                    {
                        foreach (JToken houses in numbers.Children())
                        {
                            UGabResult result = new UGabResult();

                            // Get general values
                            foreach (JProperty a in token.Children<JProperty>())
                            {
                                switch (a.Name)
                                {
                                    case "entry":
                                        result.name = a.Value.Value<string>();
                                        break;
                                    case "muni_id":
                                        result.municipalid = a.Value.Value<int>();
                                        break;
                                    case "muni_name":
                                        result.region = a.Value.Value<string>();
                                        break;
                                    case "post_id":
                                        result.postno = a.Value.Value<string>();
                                        break;
                                }
                            }

                            foreach (JProperty h in houses.Children<JProperty>())
                            {
                                // get each "house"
                                switch (h.Name)
                                {
                                    case "x":
                                        result.lon = h.Value.Value<double>();
                                        break;
                                    case "y":
                                        result.lat = h.Value.Value<double>();
                                        break;
                                    case "value": // value is house number
                                        result.name += " " + h.Value.Value<string>();
                                        break;
                                }
                            }

                            result.type = GABTYPE.House;

                            // convert coordinate
                            double lat = 0, lng = 0;
                            double y = result.lon;
                            double x = result.lat;
                            com.ums.UmsCommon.CoorConvert.UTM.UTM2LL(23, x, y, "33", 'V', ref lat, ref lng);
                            result.lon = lng;
                            result.lat = lat;

                            // check postno before adding
                            if (postNoFilter == null || result.postno.Equals(postNoFilter, StringComparison.OrdinalIgnoreCase))
                                list.addLine(ref result);
                        }
                    }
                    else if (location != null && location.HasValues)
                    {
                        UGabResult result = new UGabResult();
                        string post_name = "";
                        string entry = "";
                        string output = "";

                        foreach (JProperty coordinate in location.Children<JProperty>())
                        {
                            switch (coordinate.Name)
                            {
                                case "x":
                                    result.lon = coordinate.Value.Value<double>();
                                    break;
                                case "y":
                                    result.lat = coordinate.Value.Value<double>();
                                    break;
                            }
                        }

                        // Get general values
                        foreach (JProperty a in token.Children<JProperty>())
                        {
                            switch (a.Name)
                            {
                                case "entry":
                                    entry = a.Value.Value<string>();
                                    break;
                                case "output":
                                    output = a.Value.Value<string>();
                                    break;
                                case "muni_id":
                                    result.municipalid = a.Value.Value<int>();
                                    break;
                                case "muni_name":
                                    result.region = a.Value.Value<string>();
                                    break;
                                case "post_id":
                                    result.postno = a.Value.Value<string>();
                                    break;
                                case "post_name":
                                    post_name = a.Value.Value<string>();
                                    break;
                                case "type_id":
                                    switch (a.Value.Value<int>())
                                    {
                                        case 998:
                                            result.type = GABTYPE.Post;
                                            break;
                                        case 140:
                                            result.type = GABTYPE.Street;
                                            break;
                                        case 164:
                                        case 100:
                                        case 102:
                                        case 103:
                                        case 104:
                                        default:
                                            result.type = GABTYPE.Region;
                                            break;
                                    }
                                    break;
                            }

                            // reformat some results based on type
                            switch (result.type)
                            {
                                case GABTYPE.Post:
                                    result.region = output;
                                    result.name = result.postno;
                                    break;
                                case GABTYPE.House:
                                case GABTYPE.Region:
                                case GABTYPE.Street:
                                    result.name = output;
                                    break;
                            }
                        }

                        // convert coordinate
                        double lat = 0, lng = 0;
                        double y = result.lon;
                        double x = result.lat;
                        com.ums.UmsCommon.CoorConvert.UTM.UTM2LL(23, x, y, "33", 'V', ref lat, ref lng);
                        result.lon = lng;
                        result.lat = lat;

                        // check postno before adding
                        if(postNoFilter == null || result.postno.Equals(postNoFilter, StringComparison.OrdinalIgnoreCase))
                            list.addLine(ref result);
                    }
                }
                catch
                { 
                    // failed to get information about location, no big deal, just skip it
                }
            }

            list.finalize();

            return list;
        }

        public UGabSearchResultList parseJSONGeoData(string jsonData)
        {
            UGabSearchResultList list = new UGabSearchResultList();

            JObject obj = JObject.Parse(jsonData);

            foreach (JToken token in obj.SelectToken("candidates").Children())
            {
                try
                {
                    JToken location = token.SelectToken("location");
                    JToken attrib = token.SelectToken("attributes");

                    if (location.HasValues)
                    {
                        UGabResult result = new UGabResult();

                        result.match = float.Parse(token.SelectToken("score").ToString(), UCommon.UGlobalizationInfo);

                        foreach (JProperty coordinate in location.Children<JProperty>())
                        {
                            if (coordinate.Name == "x")
                                result.lon = double.Parse(coordinate.Value.ToString(), UCommon.UGlobalizationInfo);
                            else if (coordinate.Name == "y")
                                result.lat = double.Parse(coordinate.Value.ToString(), UCommon.UGlobalizationInfo);
                        }

                        if (attrib.HasValues)
                        {
                            foreach (JProperty a in attrib.Children<JProperty>())
                            {
                                switch (a.Name)
                                {
                                    case "Match_addr":
                                        result.name = a.Value.Value<string>().Split(',')[0];
                                        break;
                                    case "PostalCode":
                                        result.postno = a.Value.Value<string>();
                                        break;
                                    case "Municipality":
                                        result.region = a.Value.Value<string>();
                                        break;
                                }
                            }
                        }
                        else // no attributes found, return "raw" data
                        {
                            result.name = token.SelectToken("address").ToString();
                        }

                        list.addLine(ref result);
                    }
                }
                catch
                { 
                    // failed to get information about location, no big deal, just skip it
                }
            }
            list.finalize();

            return list;
        }

        public UGabSearchResultList parseJSONNordeca(string jsonData)
        {
            UGabSearchResultList list = new UGabSearchResultList();

            JObject obj = JObject.Parse(jsonData);

            foreach (JToken token in obj.SelectToken("features").Children())
            {
                try
                {
                    JToken location = token.SelectToken("geometry");
                    JToken attrib = token.SelectToken("properties");

                    if (location.HasValues)
                    {
                        UGabResult result = new UGabResult();

                        result.match = 0f;

                        JToken coordinate = location.SelectToken("coordinates");
                        
                        JToken first = coordinate.First;
                        result.lon = first.Value<float>();
                        
                        JToken next = first.Next;
                        result.lat = next.Value<float>();

                        //result.lon = coordinate.First.Value<float>();
                        //result.lat = coordinate.Next.Value<float>();
                        //result.lat = coordinate.Last.Value<float>();

                        if (attrib.HasValues)
                        {
                            string street = "";
                            string streetnr = "";

                            foreach (JProperty a in attrib.Children<JProperty>())
                            {
                                switch (a.Name)
                                {
                                    case "street":
                                        street = a.Value.Value<string>();
                                        break;
                                    case "streetnr":
                                        streetnr = a.Value.Value<string>();
                                        break;
                                    case "postnr":
                                        result.postno = a.Value.Value<string>();
                                        break;
                                    case "postname":
                                        result.region = a.Value.Value<string>();
                                        break;
                                    case "gid":
                                        result.streetid = a.Value.Value<int>();
                                        break;
                                }
                            }

                            if (streetnr.Trim().Length > 0)
                            {
                                result.type = GABTYPE.House;
                                result.name = string.Format("{0} {1}", street, streetnr);
                            }
                            else if (street.Trim().Length > 0)
                            {
                                result.type = GABTYPE.Street;
                                result.name = street;
                            }
                            else if (result.postno.Trim().Length > 0)
                            {
                                result.type = GABTYPE.Post;
                                result.name = result.postno;
                            }
                            else
                            {
                                result.type = GABTYPE.Region;
                                result.name = result.region;
                            }

                        }
                        else // no attributes found, return "raw" data
                        {
                            result.name = "NA";
                        }

                        list.addLine(ref result);
                    }
                }
                catch
                {
                    // failed to get information about location, no big deal, just skip it
                }
            }
            list.finalize();

            return list;
        }

        public UGabSearchResultList parseJSONArcGIS(string jsonData)
        {
            UGabSearchResultList list = new UGabSearchResultList();

            JObject obj = JObject.Parse(jsonData);

            foreach (JToken token in obj.SelectToken("candidates").Children())
            {
                try
                {
                    JToken location = token.SelectToken("location");
                    JToken attrib = token.SelectToken("attributes");

                    if (location.HasValues)
                    {
                        UGabResult result = new UGabResult();

                        result.match = float.Parse(token.SelectToken("score").ToString(), UCommon.UGlobalizationInfo);

                        foreach (JProperty coordinate in location.Children<JProperty>())
                        {
                            if (coordinate.Name == "x")
                                result.lon = double.Parse(coordinate.Value.ToString(), UCommon.UGlobalizationInfo);
                            else if (coordinate.Name == "y")
                                result.lat = double.Parse(coordinate.Value.ToString(), UCommon.UGlobalizationInfo);
                        }

                        if (attrib.HasValues)
                        {
                            foreach (JProperty a in attrib.Children<JProperty>())
                            {
                                switch (a.Name)
                                {
                                    case "Match_addr":
                                        result.name = a.Value.Value<string>().Split(',')[0];
                                        break;
                                    case "LeftPostcode":
                                        if (result.postno == null || result.postno.Length == 0)
                                            result.postno = a.Value.Value<string>();
                                        break;
                                    case "Postcode":
                                        if (result.postno == null || result.postno.Length == 0)
                                            result.postno = a.Value.Value<string>();
                                        break;
                                }
                            }
                        }
                        else // no attributes found, return "raw" data
                        {
                            result.name = token.SelectToken("address").ToString();
                        }

                        list.addLine(ref result);
                    }
                }
                catch
                {
                    // failed to get information about location, no big deal, just skip it
                }
            }
            list.finalize();

            return list;
        }

        private String[] tryParsePostNo(String postNoRegion)
        {
            String[] address = postNoRegion.Split(' ');
            address = removeLeadingTrailingSpaces(address);

            if (address.Length > 1)
            {
                int postno;
                bool success = int.TryParse(address[0], out postno);
                if (success)
                {
                    address[0] = postno.ToString();
                }
                else
                {
                    address[0] = "";
                }
            }
            return address;
        }

        private String[] removeLeadingTrailingSpaces(String[] address)
        {
            for (int i = 0; i < address.Length; i++)
            {
                address[i] = address[i].Trim();
            }
            return address;
        }

        private String GenerateWildcardString(String address)
        {
            string ret = "";
            string[] splitChars = new string[] { " ", ",", ".", "-" };
            string[] addressParts = address.Split(splitChars, StringSplitOptions.RemoveEmptyEntries);

            Dictionary<string, string> replaceList = new Dictionary<string, string>()
            {
                { "nordre", "n* " },
                { "sønder", "s* " },
                { "øster", "ø* " },
                { "vester", "v* " },
                { "gl", "g* " },
                { "gammel", "g* " }
            };

            foreach (string addressPart in addressParts)
            {
                if (replaceList.ContainsKey(addressPart.ToLower()))
                    ret += replaceList[addressPart.ToLower()];
                else
                    ret += addressPart.ToLower() + "* ";
            }

            if (ret.Trim().Length == 0)
                return address.Trim();
            else
                return ret.Trim();
        }
    }
}