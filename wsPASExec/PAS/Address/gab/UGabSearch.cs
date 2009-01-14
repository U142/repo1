using System;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Collections;
using System.Xml.Linq;
using System.Net;
using System.Xml;
using System.IO;
using System.Text;
using com.ums.UmsCommon;
using com.ums.PAS.Address;


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

            int n_unique = 0;
            if (m_params.sz_no.Length > 0)
                n_unique = 1;
            

            string sz_server = "http://api.fleximap.com/servlet/FlexiMap";
            string sz_params = "UID=" + m_params.sz_uid +
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
            //Byte[] param_bytes = encoder.GetBytes(sz_params);
            //string sz_params_8859 = Encoding.GetEncoding("iso-8859-1").GetString(param_bytes);


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
            catch (Exception e)
            {
                throw e;
            }

            Byte[] to_utf8 = Encoding.GetEncoding("utf-8").GetBytes(xmldata);
            string xmldata_utf8 = Encoding.UTF8.GetString(to_utf8);


            XmlDocument doc = new XmlDocument();
            try
            {
                doc.LoadXml(xmldata_utf8);
                return parse(ref doc);
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
                item.name = node.Attributes["Name"].Value;
                item.postno = node.Attributes["PostNo"].Value;
                item.region = node.Attributes["PostNo"].Value;
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
    }


}