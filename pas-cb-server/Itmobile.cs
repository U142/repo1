using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using System.Xml.Serialization;
using System.Globalization;
using pas_cb_server.tmobile;

namespace pas_cb_server
{
    public class CB_tmobile
    {
        public static int CreateAlert(AlertInfo oAlert, Operator op)
        {
            IBAG_Alert_Attributes t_alert = new IBAG_Alert_Attributes();
            
            t_alert.IBAG_protocol_version = op.api_version.ToString(); //config på operatør
            t_alert.IBAG_sending_gateway_id = ""; //config
            t_alert.IBAG_message_number = ASCIIEncoding.ASCII.GetBytes(Database.GetHandle(op).ToString());
            t_alert.IBAG_sender = ""; //config
            t_alert.IBAG_sent_date_time = DateTime.Now;
            t_alert.IBAG_status = IBAG_status.Actual;
            t_alert.IBAG_message_type = IBAG_message_type.Alert;
            t_alert.IBAG_cap_identifier = ""; //config
            t_alert.IBAG_cap_sent_date_time = DateTime.Now;
            t_alert.IBAG_cap_sent_date_timeSpecified = true;

            IBAG_Alert_Area t_alert_area = new IBAG_Alert_Area();
            List<IBAG_Alert_Area> t_alert_arealist = new List<IBAG_Alert_Area>();
            t_alert_area.IBAG_area_description = "adhoc-polygon";
            t_alert_area.IBAG_polygon = get_IBAG_polygon(oAlert, op);
            t_alert_arealist.Add(t_alert_area);

            IBAG_alert_info t_alert_info = new IBAG_alert_info();
            t_alert_info.IBAG_priority = IBAG_priority.Normal;
            t_alert_info.IBAG_prioritySpecified = true;
            t_alert_info.IBAG_category = IBAG_category.Met;
            t_alert_info.IBAG_severity = IBAG_severity.Severe;
            t_alert_info.IBAG_urgency = IBAG_urgency.Expected;
            t_alert_info.IBAG_expires_date_time = DateTime.Now.AddMinutes(oAlert.l_validity);
            t_alert_info.IBAG_text_language = get_IBAG_text_language(oAlert, op);
            t_alert_info.IBAG_text_alert_message_length = oAlert.alert_message.sz_text.Length.ToString();
            t_alert_info.IBAG_text_alert_message = oAlert.alert_message.sz_text;
            t_alert_info.IBAG_Alert_Area = t_alert_arealist.ToArray();

            switch(op.coordinate_type)
            {
                case COORDINATESYSTEM.UTM31:
                case COORDINATESYSTEM.UTM32:
                    t_alert_info.IBAG_coordinate_system = IBAG_coordinate_system.UTM;
                    t_alert_info.IBAG_coordinate_systemSpecified = true;
                    break;
                case COORDINATESYSTEM.WGS84:
                    t_alert_info.IBAG_coordinate_system = IBAG_coordinate_system.WGS84;
                    t_alert_info.IBAG_coordinate_systemSpecified = true;
                    break;
                default:
                    t_alert_info.IBAG_coordinate_systemSpecified = false;
                    break;
            }
            
            IBAG_Alert_Attributes t_alert_response = SendRequest(op, t_alert);

            if (t_alert_response.IBAG_message_type == IBAG_message_type.Ack)
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage OK (code={3})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , oAlert.l_refno
                    , t_alert_response.IBAG_message_type), 0);
                // ok, insert appropriate info in database
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBPREPARING, ASCIIEncoding.ASCII.GetString(t_alert_response.IBAG_referenced_message_number));
                return Constant.OK;
            }
            else
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , oAlert.l_refno
                    , t_alert_response.IBAG_message_type
                    , t_alert_response.IBAG_note.First()), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 200, op.l_operator, LBATYPE.CB);
            }
        }
        public static int UpdateAlert(AlertInfo oAlert, Operator op)
        {
            IBAG_Alert_Attributes t_alert = new IBAG_Alert_Attributes();

            t_alert.IBAG_protocol_version = op.api_version.ToString(); //config på operatør
            t_alert.IBAG_sending_gateway_id = ""; //config
            t_alert.IBAG_message_number = ASCIIEncoding.ASCII.GetBytes(Database.GetHandle(op).ToString());
            t_alert.IBAG_sender = ""; //config
            t_alert.IBAG_sent_date_time = DateTime.Now;
            t_alert.IBAG_status = IBAG_status.Actual;
            t_alert.IBAG_message_type = IBAG_message_type.Update;
            t_alert.IBAG_referenced_message_number = ASCIIEncoding.ASCII.GetBytes(Database.GetJobID(op, oAlert.l_refno));
            t_alert.IBAG_cap_identifier = ""; //config
            t_alert.IBAG_cap_sent_date_time = DateTime.Now;
            t_alert.IBAG_cap_sent_date_timeSpecified = true;

            IBAG_alert_info t_alert_info = new IBAG_alert_info();
            t_alert_info.IBAG_priority = IBAG_priority.Normal;
            t_alert_info.IBAG_prioritySpecified = true;
            t_alert_info.IBAG_category = IBAG_category.Met;
            t_alert_info.IBAG_severity = IBAG_severity.Severe;
            t_alert_info.IBAG_urgency = IBAG_urgency.Expected;
            //t_alert_info.IBAG_expires_date_time = DateTime.Now.AddMinutes(oAlert.l_validity);
            t_alert_info.IBAG_text_language = get_IBAG_text_language(oAlert, op);
            t_alert_info.IBAG_text_alert_message_length = oAlert.alert_message.sz_text.Length.ToString();
            t_alert_info.IBAG_text_alert_message = oAlert.alert_message.sz_text;

            IBAG_Alert_Attributes t_alert_response = SendRequest(op, t_alert);

            if (t_alert_response.IBAG_message_type == IBAG_message_type.Ack)
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) UpdateMessage OK (code={3})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , oAlert.l_refno
                    , t_alert_response.IBAG_message_type), 0);
                // ok, insert appropriate info in database
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBPREPARING, ASCIIEncoding.ASCII.GetString(t_alert_response.IBAG_referenced_message_number));
                return Constant.OK;
            }
            else
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) UpdateMessage FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , oAlert.l_refno
                    , t_alert_response.IBAG_message_type
                    , t_alert_response.IBAG_note.First()), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 200, op.l_operator, LBATYPE.CB);
            }
        }
        public static int KillAlert(AlertInfo oAlert, Operator op)
        {
            IBAG_Alert_Attributes t_alert = new IBAG_Alert_Attributes();

            t_alert.IBAG_protocol_version = op.api_version.ToString(); //config på operatør
            t_alert.IBAG_sending_gateway_id = ""; //config
            t_alert.IBAG_message_number = ASCIIEncoding.ASCII.GetBytes(Database.GetHandle(op).ToString());
            t_alert.IBAG_referenced_message_number = ASCIIEncoding.ASCII.GetBytes(Database.GetJobID(op, oAlert.l_refno));
            t_alert.IBAG_sender = ""; //config
            t_alert.IBAG_sent_date_time = DateTime.Now;
            t_alert.IBAG_status = IBAG_status.Actual;
            t_alert.IBAG_message_type = IBAG_message_type.Cancel;
            t_alert.IBAG_cap_identifier = ""; //config
            t_alert.IBAG_cap_sent_date_time = DateTime.Now;
            t_alert.IBAG_cap_sent_date_timeSpecified = true;

            IBAG_Alert_Attributes t_alert_response = SendRequest(op, t_alert);

            if (t_alert_response.IBAG_message_type == IBAG_message_type.Ack)
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) KillMessage OK (code={3})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , oAlert.l_refno
                    , t_alert_response.IBAG_message_type), 0);
                // ok, insert appropriate info in database
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.FINISHED);
                return Constant.OK;
            }
            else
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) KillMessage FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , oAlert.l_refno
                    , t_alert_response.IBAG_message_type
                    , t_alert_response.IBAG_note.First()), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 200, op.l_operator, LBATYPE.CB);
            }
        }
        public static int GetAlertStatus(int l_refno, int l_status, byte[] message_number, Operator op)
        {
            IBAG_Alert_Attributes t_alert = new IBAG_Alert_Attributes();

            t_alert.IBAG_protocol_version = op.api_version.ToString(); //config på operatør
            t_alert.IBAG_sending_gateway_id = ""; //config
            t_alert.IBAG_message_number = ASCIIEncoding.ASCII.GetBytes(Database.GetHandle(op).ToString());
            t_alert.IBAG_referenced_message_number = message_number;
            t_alert.IBAG_sender = ""; //config
            t_alert.IBAG_sent_date_time = DateTime.Now;
            t_alert.IBAG_status = IBAG_status.Actual;
            t_alert.IBAG_message_type = IBAG_message_type.EMS;
            t_alert.IBAG_cap_identifier = ""; //config
            t_alert.IBAG_cap_sent_date_time = DateTime.Now;
            t_alert.IBAG_cap_sent_date_timeSpecified = true;

            IBAG_Alert_Attributes t_alert_response = SendRequest(op, t_alert);

            if (t_alert_response.IBAG_message_type == IBAG_message_type.Report)
            {
                float cb_percentage = 0;
                int cb_cells = 0;
                int cb_ok = 0;

                foreach (IBAG_status_report report in t_alert_response.IBAG_status_report)
                {
                    cb_cells += report.IBAG_cell_count;
                    cb_ok += report.IBAG_cell_broadcast_info_count;
                }

                cb_percentage = (float)cb_ok/(float)cb_cells;

                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) EMSMessage OK (handle={3}, success={4:0.00}%)"
                    , l_refno
                    , op.sz_operatorname
                    , ASCIIEncoding.ASCII.GetString(t_alert.IBAG_message_number)
                    , ASCIIEncoding.ASCII.GetString(t_alert_response.IBAG_referenced_message_number)
                    , cb_percentage), 0);
                // ok, insert appropriate info in database
                if (l_status != Constant.CBACTIVE && l_status != Constant.USERCANCELLED)
                    Database.SetSendingStatus(op, l_refno, Constant.CBACTIVE);
                return Constant.OK;
            }
            else
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) EMSMessage FAILED (handle={3}, code={4}, msg={5})"
                    , l_refno
                    , op.sz_operatorname
                    , ASCIIEncoding.ASCII.GetString(t_alert.IBAG_message_number)
                    , ASCIIEncoding.ASCII.GetString(t_alert.IBAG_referenced_message_number)
                    , t_alert_response.IBAG_message_type
                    , t_alert_response.IBAG_note.First()), 2);
                return Constant.FAILED;
            }
        }
        
        private static IBAG_Alert_Attributes SendRequest(Operator op, IBAG_Alert_Attributes parameters)
        {
            XmlSerializer s = new XmlSerializer(typeof(IBAG_Alert_Attributes));
            TextWriter w = new StringWriter();
            s.Serialize(w, parameters);

            WebRequest webRequest = WebRequest.Create(op.sz_url);
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

        private static IBAG_text_language get_IBAG_text_language(AlertInfo oAlert, Operator op)
        {
            switch (oAlert.alert_message.l_channel)
            {
                case 4:
                    return IBAG_text_language.Spanish;
                case 3:
                    return IBAG_text_language.French;
                case 2:
                    return IBAG_text_language.English;
                case 1:
                default:
                    return IBAG_text_language.Dutch;
            }
        }
        private static string[] get_IBAG_polygon(AlertInfo oAlert, Operator op)
        {
            List<string> ret = new List<string>();
            NumberFormatInfo coorformat = new NumberFormatInfo();
            coorformat.NumberDecimalSeparator = ".";
            coorformat.NumberGroupSeparator = "";

            double xcoord, ycoord;

            foreach (PolyPoint wgs84pt in oAlert.alert_polygon)
            {
                Tools.ConvertCoordinate(wgs84pt, out xcoord, out ycoord, op.coordinate_type);
                ret.Add(String.Format(coorformat, "{0}, {1}", xcoord, ycoord));
            }

            return ret.ToArray();
        }
    }
}
