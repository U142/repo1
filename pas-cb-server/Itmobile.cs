using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using System.Xml.Serialization;

namespace pas_cb_server
{
    public class CB_tmobile
    {
        public static int CreateAlert()
        {
            while (CBServer.running)
            {
                IBAG_Alert_Attributes attr = new IBAG_Alert_Attributes();
                IBAG_Alert_AttributesIBAG_alert_info info = new IBAG_Alert_AttributesIBAG_alert_info();

                info.IBAG_priority = IBAG_Alert_AttributesIBAG_alert_infoIBAG_priority.HighPriority;
                info.IBAG_prioritySpecified = true;

                attr.IBAG_alert_info = info;
                attr.IBAG_message_number = System.Guid.NewGuid().ToByteArray();
                attr.IBAG_cap_sent_date_time = DateTime.Now;
                attr.IBAG_cap_sent_date_timeSpecified = true;
                attr.IBAG_sent_date_time = DateTime.Now;

                IBAG_Alert_Attributes res = SendRequest("http://localhost:8080", attr);

                Console.WriteLine("Req sent: {0}, Res sent: {1}, Cap sent: {2}, Message number: {3}, message type: {4}", attr.IBAG_sent_date_time, res.IBAG_sent_date_time, attr.IBAG_cap_sent_date_time, new Guid(res.IBAG_message_number).ToString("B"), res.IBAG_message_type);

                System.Threading.Thread.Sleep(5000);
            }

            return Constant.OK;
        }
        public static int UpdateAlert()
        {
            return Constant.OK;
        }
        public static int KillAlert()
        {
            return Constant.OK;
        }
        public static int GetAlertStatus()
        {
            return Constant.OK;
        }
        
        private static IBAG_Alert_Attributes SendRequest(string uri, IBAG_Alert_Attributes parameters)
        {
            XmlSerializer s = new XmlSerializer(typeof(IBAG_Alert_Attributes));
            TextWriter w = new StringWriter();
            s.Serialize(w, parameters);

            WebRequest webRequest = WebRequest.Create(uri);
            webRequest.Method = "POST";

            byte[] bytes = Encoding.ASCII.GetBytes(w.ToString());

            Stream os = null;
            try
            { // send the Post
                webRequest.ContentLength = bytes.Length;   //Count bytes to send
                os = webRequest.GetRequestStream();
                os.Write(bytes, 0, bytes.Length);         //Send it
            }
            catch (WebException ex)
            {
                Console.WriteLine("HttpPost: Request error " + ex.Message);
            }
            finally
            {
                if (os != null)
                {
                    os.Close();
                }
            }

            try
            { // get the response
                WebResponse webResponse = webRequest.GetResponse();
                if (webResponse == null)
                { return null; }
                StreamReader sr = new StreamReader(webResponse.GetResponseStream());
                return (IBAG_Alert_Attributes)s.Deserialize(sr);
            }
            catch (WebException ex)
            {
                Console.WriteLine("HttpPost: Response error " + ex.Message);
            }
            return null;
        } // end HttpPost 
    }
}
