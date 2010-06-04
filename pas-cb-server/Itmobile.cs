using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using System.Xml.Serialization;
using pas_cb_server.tmobile;
using com.ums.UmsCommon.CoorConvert;

namespace pas_cb_server
{
    public class CB_tmobile
    {
        public static int CreateAlert(AlertInfo oAlert, Operator op)
        {
            int ret = Constant.OK;
            
            IBAG_Alert_Attributes t_alert = new IBAG_Alert_Attributes();
            IBAG_Alert_AttributesIBAG_alert_info t_alert_info = new IBAG_Alert_AttributesIBAG_alert_info();
            IBAG_Alert_AttributesIBAG_alert_infoIBAG_Alert_Area t_alert_area = new IBAG_Alert_AttributesIBAG_alert_infoIBAG_Alert_Area();
            List<IBAG_Alert_AttributesIBAG_alert_infoIBAG_Alert_Area> t_alert_arealist = new List<IBAG_Alert_AttributesIBAG_alert_infoIBAG_Alert_Area>();

            List<string> poly = new List<string>();
            foreach(PolyPoint p in oAlert.alert_polygon)
            {
                poly.Add(String.Format("{0},{1}", p.x, p.y));
            }
            t_alert_area.IBAG_area_description = "adhoc-polygon";
            t_alert_area.IBAG_polygon = poly.ToArray();
            t_alert_arealist.Add(t_alert_area);

            t_alert_info.IBAG_priority = IBAG_Alert_AttributesIBAG_alert_infoIBAG_priority.Normal;
            t_alert_info.IBAG_prioritySpecified = true;
            t_alert_info.IBAG_category = IBAG_Alert_AttributesIBAG_alert_infoIBAG_category.Met;
            t_alert_info.IBAG_severity = IBAG_Alert_AttributesIBAG_alert_infoIBAG_severity.Severe;
            t_alert_info.IBAG_urgency = IBAG_Alert_AttributesIBAG_alert_infoIBAG_urgency.Expected;
            t_alert_info.IBAG_expires_date_time = DateTime.Now.AddMinutes(oAlert.l_validity);
            t_alert_info.IBAG_text_language = GetLanguageEnum(oAlert.alert_message.l_channel);
            t_alert_info.IBAG_text_alert_message_length = oAlert.alert_message.sz_text.Length.ToString();
            t_alert_info.IBAG_text_alert_message = oAlert.alert_message.sz_text;
            t_alert_info.IBAG_Alert_Area = t_alert_arealist.ToArray();
            
            t_alert.IBAG_protocol_version = "1.0"; //config på operatør
            t_alert.IBAG_sending_gateway_id = ""; //config
            t_alert.IBAG_message_number = ASCIIEncoding.ASCII.GetBytes(oAlert.l_refno.ToString());
            t_alert.IBAG_sender = ""; //config
            t_alert.IBAG_sent_date_time = DateTime.Now;
            t_alert.IBAG_status = IBAG_Alert_AttributesIBAG_status.Actual;
            t_alert.IBAG_message_type = IBAG_Alert_AttributesIBAG_message_type.Alert;
            t_alert.IBAG_cap_identifier = ""; //config
            t_alert.IBAG_cap_sent_date_time = DateTime.Now;
            t_alert.IBAG_cap_sent_date_timeSpecified = true;

            IBAG_Alert_Attributes t_alert_response = SendRequest(op.sz_url, t_alert);

            switch (t_alert_response.IBAG_message_type)
            {
                case IBAG_Alert_AttributesIBAG_message_type.Ack:
                    // ok, insert appropriate info in database
                    break;
                case IBAG_Alert_AttributesIBAG_message_type.Error:
                    // failed, return error and insert appropriate info in database
                    ret = Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 200, op.l_operator, LBATYPE.CB);
                    break;
            }

            return ret;
        }
        public static int UpdateAlert(AlertInfo oAlert, Operator op)
        {
            return Constant.OK;
        }
        public static int KillAlert(AlertInfo oAlert, Operator op)
        {
            return Constant.OK;
        }
        public static int GetAlertStatus(AlertInfo oAlert, Operator op)
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
        private static IBAG_Alert_AttributesIBAG_alert_infoIBAG_text_language GetLanguageEnum(int l_channel)
        {
            switch (l_channel)
            {
                case 4:
                    return IBAG_Alert_AttributesIBAG_alert_infoIBAG_text_language.Spanish;
                case 3:
                    return IBAG_Alert_AttributesIBAG_alert_infoIBAG_text_language.French;
                case 2:
                    return IBAG_Alert_AttributesIBAG_alert_infoIBAG_text_language.English;
                case 1:
                default:
                    return IBAG_Alert_AttributesIBAG_alert_infoIBAG_text_language.Dutch;
            }
        }
    }
}
