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
        public static int CreateAlert(AlertInfo oAlert, Operator op, Operation operation)
        {
            CB_tmobile_defaults def = (CB_tmobile_defaults)op.GetDefaultValues(typeof(CB_tmobile_defaults));
            IBAG_Alert_Attributes t_alert = new IBAG_Alert_Attributes();
            IBAG_alert_info t_alert_info = new IBAG_alert_info();
            IBAG_Alert_Area t_alert_area = new IBAG_Alert_Area();
            List<IBAG_Alert_Area> t_alert_arealist = new List<IBAG_Alert_Area>();

            try
            {
                // update status to parsing
                if (Database.SetSendingStatus(op, oAlert.l_refno, Constant.PARSING) != Constant.OK)
                    return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);

                int l_reqno = Database.GetHandle(op);

                string sz_channel_category = Database.GetChannelName(op.l_operator, oAlert.alert_message.l_channel);
                if(sz_channel_category == null && operation != Operation.NEWPLMN_HEARTBEAT)
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage FAIELD (missing channel category name)"
                        , oAlert.l_refno
                        , op.sz_operatorname
                        , l_reqno), 2);
                    return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, -1, op.l_operator, LBATYPE.CB);
                }

                // Set alert attributes
                DateTime dtm_cap = Database.GetCreateTime(op, oAlert.l_refno);
                t_alert.IBAG_message_number = BitConverter.GetBytes(l_reqno);
                t_alert.IBAG_sent_date_time = dtm_cap;
                t_alert.IBAG_cap_sent_date_time = dtm_cap;
                t_alert.IBAG_cap_sent_date_timeSpecified = true;
                // from default values
                t_alert.IBAG_protocol_version = op.api_version.ToString(); //database
                t_alert.IBAG_sending_gateway_id = def.sz_sending_gateway_id;
                t_alert.IBAG_sender = def.sz_sender;
                t_alert.IBAG_cap_identifier = def.sz_cap_identifier + " " + dtm_cap.ToString();
                t_alert.IBAG_cap_alert_uri = def.sz_cap_alert_uri;

                // Default values which varies from test and normal messages
                switch (operation)
                {
                    case Operation.NEWPLMN_HEARTBEAT:
                        t_alert.IBAG_status = IBAG_status.NetworkTest;
                        t_alert.IBAG_message_type = IBAG_message_type.Alert;
                        t_alert_info.IBAG_gsm_repetition_period = (int)(oAlert.l_validity * 60 / 1.883);
                        t_alert_info.IBAG_umts_repetition_period = (int)(oAlert.l_validity * 60 / 1.883);
                        t_alert_info.IBAG_gsm_repetition_periodSpecified = true;
                        t_alert_info.IBAG_umts_repetition_periodSpecified = true;
                        break;
                    case Operation.NEWPLMN_TEST:
                        t_alert.IBAG_status = IBAG_status.Actual;
                        t_alert.IBAG_message_type = IBAG_message_type.Alert;
                        t_alert_info.IBAG_gsm_repetition_period = (int)(oAlert.l_validity * 60 / 1.883);
                        t_alert_info.IBAG_umts_repetition_period = (int)(oAlert.l_validity * 60 / 1.883);
                        t_alert_info.IBAG_gsm_repetition_periodSpecified = true;
                        t_alert_info.IBAG_umts_repetition_periodSpecified = true;
                        break;
                    case Operation.NEWAREA:
                    case Operation.NEWPLMN:
                    default:
                        t_alert.IBAG_status = IBAG_status.Actual;
                        t_alert.IBAG_message_type = IBAG_message_type.Alert;
                        t_alert_info.IBAG_gsm_repetition_period = oAlert.l_repetitioninterval;
                        t_alert_info.IBAG_umts_repetition_period = oAlert.l_repetitioninterval;
                        t_alert_info.IBAG_gsm_repetition_periodSpecified = true;
                        t_alert_info.IBAG_umts_repetition_periodSpecified = true;
                        break;
                }
                
                // Get alert area
                if (operation == Operation.NEWAREA)
                {
                    // Get polygon for area alerts
                    t_alert_area.IBAG_area_description = "Polygon";
                    t_alert_area.IBAG_polygon = new string[] { get_IBAG_polygon(oAlert, op) };
                }
                else if (
                    operation == Operation.NEWPLMN ||
                    operation == Operation.NEWPLMN_HEARTBEAT ||
                    operation == Operation.NEWPLMN_TEST)
                {
                    // Insert Netherlands Nationwide for all PLMN messages (live and tests)
                    t_alert_area.IBAG_area_description = "Netherlands Nationwide";
                    t_alert_area.IBAG_geocode = new string[] { "NL" };
                }
                t_alert_arealist.Add(t_alert_area);

                // Set alert info 
                t_alert_info.IBAG_expires_date_time = dtm_cap.AddMinutes(oAlert.l_validity);
                t_alert_info.IBAG_text_language = get_IBAG_text_language(oAlert, op);
                t_alert_info.IBAG_text_alert_message_length = oAlert.alert_message.sz_text.Length.ToString();
                t_alert_info.IBAG_text_alert_message = oAlert.alert_message.sz_text;
                if(sz_channel_category != null)
                    t_alert_info.IBAG_channel_category = sz_channel_category;
                // from default values
                t_alert_info.IBAG_priority = def.priority;
                t_alert_info.IBAG_prioritySpecified = true;
                t_alert_info.IBAG_category = def.category;
                t_alert_info.IBAG_severity = def.severity;
                t_alert_info.IBAG_urgency = def.urgency;
                t_alert_info.IBAG_certainty = def.certainty;
                t_alert_info.IBAG_event_code = def.event_code;
                t_alert_info.IBAG_response_type = def.response_type;
                t_alert_info.IBAG_response_typeSpecified = true;

                t_alert_info.IBAG_Alert_Area = t_alert_arealist.ToArray();
                t_alert.IBAG_alert_info = t_alert_info;

                switch(op.coordinate_type)
                {
                    case COORDINATESYSTEM.UTM31:
                        if (operation == Operation.NEWAREA)
                            t_alert_area.IBAG_utm_zone = "31U";
                        t_alert_info.IBAG_coordinate_system = IBAG_coordinate_system.UTM;
                        t_alert_info.IBAG_coordinate_systemSpecified = true;
                        break;
                    case COORDINATESYSTEM.UTM32:
                        if (operation == Operation.NEWAREA)
                            t_alert_area.IBAG_utm_zone = "32U";
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

                dump_request(t_alert, op, "NewMessage", oAlert.l_refno);
                if (!Settings.live)
                {
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE, "-1", t_alert_info.IBAG_expires_date_time.ToString("yyyyMMddHHmmss"));
                    return Constant.OK;
                }

                IBAG_Alert_Attributes t_alert_response = SendRequest(op, t_alert);
                if(t_alert_response != null)
                    dump_request(t_alert_response, op, "NewMessageResult", oAlert.l_refno);

                if (t_alert_response == null)
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage FAILED (response is null)"
                        , oAlert.l_refno
                        , op.sz_operatorname
                        , l_reqno), 2);
                    return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);
                }
                else if (t_alert_response.IBAG_message_type == IBAG_message_type.Ack)
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage OK (code={3})"
                        , oAlert.l_refno
                        , op.sz_operatorname
                        , l_reqno
                        , t_alert_response.IBAG_message_type), 0);
                    // ok, insert appropriate info in database
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE, BitConverter.ToInt32(t_alert_response.IBAG_referenced_message_number, 0).ToString(), t_alert_info.IBAG_expires_date_time.ToString("yyyyMMddHHmmss"));
                    return Constant.OK;
                }
                else
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage FAILED (code={3}, msg={4})"
                        , oAlert.l_refno
                        , op.sz_operatorname
                        , l_reqno
                        , t_alert_response.IBAG_message_type
                        , t_alert_response.IBAG_note.First()), 2);
                    return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, get_IBAG_response_code(t_alert_response.IBAG_response_code), op.l_operator, LBATYPE.CB);
                }
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("{0} (op={1}) NewMessage EXCEPTION (msg={2})", oAlert.l_refno, op.sz_operatorname, e.Message),
                    String.Format("{0} (op={1}) NewMessage EXCEPTION (msg={2})", oAlert.l_refno, op.sz_operatorname, e),
                    2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);
            }
        }
        public static int UpdateAlert(AlertInfo oAlert, Operator op)
        {
            CB_tmobile_defaults def = (CB_tmobile_defaults)op.GetDefaultValues(typeof(CB_tmobile_defaults));
            IBAG_Alert_Attributes t_alert = new IBAG_Alert_Attributes();

            try
            {
                // update status to parsing
                if (Database.SetSendingStatus(op, oAlert.l_refno, Constant.PARSING) != Constant.OK)
                    return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);

                int l_reqno = Database.GetHandle(op);

                DateTime dtm_cap = Database.GetCreateTime(op, oAlert.l_refno);
                t_alert.IBAG_message_number = BitConverter.GetBytes(l_reqno);
                t_alert.IBAG_referenced_message_number = BitConverter.GetBytes(int.Parse(Database.GetJobID(op, oAlert.l_refno)));
                t_alert.IBAG_sent_date_time = DateTime.Now;
                t_alert.IBAG_status = IBAG_status.Actual;
                t_alert.IBAG_message_type = IBAG_message_type.Update;
                t_alert.IBAG_cap_sent_date_time = DateTime.Now;
                t_alert.IBAG_cap_sent_date_timeSpecified = true;

                // based on default values:
                t_alert.IBAG_protocol_version = op.api_version.ToString(); //database
                t_alert.IBAG_sending_gateway_id = def.sz_sending_gateway_id;
                t_alert.IBAG_sender = def.sz_sender;
                t_alert.IBAG_cap_identifier = def.sz_cap_identifier + " " + DateTime.Now.ToString();
                t_alert.IBAG_referenced_message_cap_identifier = def.sz_cap_identifier + " " + dtm_cap.ToString();

                IBAG_alert_info t_alert_info = new IBAG_alert_info();
                // based on default values:
                t_alert_info.IBAG_priority = def.priority;
                t_alert_info.IBAG_prioritySpecified = true;
                t_alert_info.IBAG_category = def.category;
                t_alert_info.IBAG_severity = def.severity;
                t_alert_info.IBAG_urgency = def.urgency;
                t_alert_info.IBAG_certainty = def.certainty;
                t_alert_info.IBAG_event_code = def.event_code;

                t_alert_info.IBAG_text_language = get_IBAG_text_language(oAlert, op);
                t_alert_info.IBAG_text_alert_message_length = oAlert.alert_message.sz_text.Length.ToString();
                t_alert_info.IBAG_text_alert_message = oAlert.alert_message.sz_text;

                t_alert.IBAG_alert_info = t_alert_info;

                dump_request(t_alert, op, "UpdMessage", oAlert.l_refno);
                if (!Settings.live)
                {
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE);
                    return Constant.OK;
                }

                IBAG_Alert_Attributes t_alert_response = SendRequest(op, t_alert);
                if (t_alert_response != null)
                    dump_request(t_alert_response, op, "UpdMessageResult", oAlert.l_refno);

                if (t_alert_response == null)
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage FAILED (response is null)"
                        , oAlert.l_refno
                        , op.sz_operatorname
                        , l_reqno), 2);
                    return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);
                }
                else if (t_alert_response.IBAG_message_type == IBAG_message_type.Ack)
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) UpdateMessage OK (code={3})"
                        , oAlert.l_refno
                        , op.sz_operatorname
                        , l_reqno
                        , t_alert_response.IBAG_message_type), 0);
                    // ok, insert appropriate info in database
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE);
                    return Constant.OK;
                }
                else
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) UpdateMessage FAILED (code={3}, msg={4})"
                        , oAlert.l_refno
                        , op.sz_operatorname
                        , l_reqno
                        , t_alert_response.IBAG_message_type
                        , t_alert_response.IBAG_note.First()), 2);
                    return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, get_IBAG_response_code(t_alert_response.IBAG_response_code), op.l_operator, LBATYPE.CB);
                }
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("{0} (op={1}) UpdateMessage EXCEPTION (msg={2})", oAlert.l_refno, op.sz_operatorname, e.Message),
                    String.Format("{0} (op={1}) UpdateMessage EXCEPTION (msg={2})", oAlert.l_refno, op.sz_operatorname, e),
                    2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);
            }
        }
        public static int KillAlert(AlertInfo oAlert, Operator op, string sz_jobid)
        {
            CB_tmobile_defaults def = (CB_tmobile_defaults)op.GetDefaultValues(typeof(CB_tmobile_defaults));
            IBAG_Alert_Attributes t_alert = new IBAG_Alert_Attributes();

            try
            {
                int l_reqno = Database.GetHandle(op);

                DateTime dtm_cap = Database.GetCreateTime(op, oAlert.l_refno);
                t_alert.IBAG_message_number = BitConverter.GetBytes(l_reqno);
                t_alert.IBAG_referenced_message_number = BitConverter.GetBytes(int.Parse(sz_jobid));
                t_alert.IBAG_sent_date_time = DateTime.Now;
                t_alert.IBAG_status = IBAG_status.Actual;
                t_alert.IBAG_message_type = IBAG_message_type.Cancel;
                t_alert.IBAG_cap_sent_date_time = DateTime.Now;
                t_alert.IBAG_cap_sent_date_timeSpecified = true;

                // based on default values:
                t_alert.IBAG_protocol_version = op.api_version.ToString(); //database
                t_alert.IBAG_sending_gateway_id = def.sz_sending_gateway_id;
                t_alert.IBAG_sender = def.sz_sender;
                t_alert.IBAG_cap_identifier = def.sz_cap_identifier + " " + DateTime.Now.ToString();
                t_alert.IBAG_cap_alert_uri = def.sz_cap_alert_uri;
                t_alert.IBAG_referenced_message_cap_identifier = def.sz_cap_identifier + " " + dtm_cap.ToString();

                dump_request(t_alert, op, "KillMessage", oAlert.l_refno);
                if (!Settings.live)
                {
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.FINISHED);
                    return Constant.OK;
                }

                IBAG_Alert_Attributes t_alert_response = SendRequest(op, t_alert);
                if (t_alert_response != null)
                    dump_request(t_alert_response, op, "KillMessageResult", oAlert.l_refno);

                if (t_alert_response == null)
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage FAILED (response is null)"
                        , oAlert.l_refno
                        , op.sz_operatorname
                        , l_reqno), 2);
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.CANCELLING);
                    return Constant.RETRY;
                    //return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);
                }
                else if (t_alert_response.IBAG_message_type == IBAG_message_type.Ack)
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) KillMessage OK (code={3})"
                        , oAlert.l_refno
                        , op.sz_operatorname
                        , l_reqno
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
                        , l_reqno
                        , t_alert_response.IBAG_message_type
                        , t_alert_response.IBAG_note.First()), 2);
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.CANCELLING);
                    return Constant.RETRY;
                    //return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, get_IBAG_response_code(t_alert_response.IBAG_response_code), op.l_operator, LBATYPE.CB);
                }
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("{0} (op={1}) KillMessage EXCEPTION (msg={2})", oAlert.l_refno, op.sz_operatorname, e.Message),
                    String.Format("{0} (op={1}) KillMessage EXCEPTION (msg={2})", oAlert.l_refno, op.sz_operatorname, e),
                    2);
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CANCELLING);
                return Constant.RETRY;
                //return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);
            }
        }
        public static int GetAlertStatus(int l_refno, int l_status, byte[] message_number, Operator op, decimal l_expires_ts, bool b_report)
        {
            CB_tmobile_defaults def = (CB_tmobile_defaults)op.GetDefaultValues(typeof(CB_tmobile_defaults));
            IBAG_Alert_Attributes t_alert = new IBAG_Alert_Attributes();

            try
            {
                int l_reqno = Database.GetHandle(op);

                DateTime dtm_cap = Database.GetCreateTime(op, l_refno);
                t_alert.IBAG_message_number = BitConverter.GetBytes(l_reqno);
                t_alert.IBAG_referenced_message_number = message_number;
                t_alert.IBAG_sent_date_time = DateTime.Now;
                t_alert.IBAG_status = IBAG_status.Actual;
                t_alert.IBAG_message_type = IBAG_message_type.EMS;
                t_alert.IBAG_cap_sent_date_time = DateTime.Now;
                t_alert.IBAG_cap_sent_date_timeSpecified = true;

                // based on default values:
                t_alert.IBAG_protocol_version = op.api_version.ToString(); //database
                t_alert.IBAG_sending_gateway_id = def.sz_sending_gateway_id;
                t_alert.IBAG_sender = def.sz_sender;
                t_alert.IBAG_cap_identifier = def.sz_cap_identifier + " " + DateTime.Now.ToString();
                t_alert.IBAG_referenced_message_cap_identifier = def.sz_cap_identifier + " " + dtm_cap.ToString();

                dump_request(t_alert, op, "InfoMessage", l_refno);
                if (!Settings.live)
                {
                    Database.SetSendingStatus(op, l_refno, Constant.FINISHED);
                    return Constant.OK;
                }

                IBAG_Alert_Attributes t_alert_response = SendRequest(op, t_alert);
                if (t_alert_response != null)
                    dump_request(t_alert_response, op, "InfoMessageResult", l_refno);

                if (t_alert_response == null)
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage FAILED (response is null)"
                        , l_refno
                        , op.sz_operatorname
                        , l_reqno), 2);
                    return Constant.FAILED;
                }
                else if (t_alert_response.IBAG_message_type == IBAG_message_type.Report)
                {
                    float cb_percentage = 0;
                    int l_2gtotal = 0;
                    int l_2gok = 0;
                    int l_3gtotal = 0;
                    int l_3gok = 0;

                    foreach (IBAG_status_report report in t_alert_response.IBAG_status_report)
                    {
                        if (report.IBAG_network_type == IBAG_network_type.GSM)
                        {
                            l_2gtotal += report.IBAG_cell_count;
                            l_2gok += report.IBAG_cell_broadcast_info_count;
                        }
                        else if (report.IBAG_network_type == IBAG_network_type.UMTS)
                        {
                            l_3gtotal += report.IBAG_cell_count;
                            l_3gok += report.IBAG_cell_broadcast_info_count;
                        }
                    }
                    cb_percentage = ((float)l_2gok + (float)l_3gok) / ((float)l_2gtotal + (float)l_3gtotal) * 100;

                    Database.UpdateHistCell(b_report, l_refno, op.l_operator, cb_percentage, l_2gtotal, l_2gok, l_3gtotal, l_3gok);

                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) EMSMessage OK (handle={3}, success={4:0.00}%)"
                        , l_refno
                        , op.sz_operatorname
                        , BitConverter.ToInt32(t_alert.IBAG_message_number, 0)
                        , BitConverter.ToInt32(t_alert_response.IBAG_referenced_message_number, 0)
                        , cb_percentage), 0);
                    // ok, insert appropriate info in database
                    if (l_status != Constant.CBACTIVE && l_status != Constant.USERCANCELLED)
                        Database.SetSendingStatus(op, l_refno, Constant.CBACTIVE);

                    // set as finished if expiry date has passed
                    if (l_expires_ts <= decimal.Parse(DateTime.Now.ToString("yyyyMMddHHmmss")))
                        Database.SetSendingStatus(op, l_refno, Constant.FINISHED);

                    return Constant.OK;
                }
                else if (t_alert_response.IBAG_message_type == IBAG_message_type.Error && t_alert_response.IBAG_response_code == new string[] { "200" })
                {
                    Database.SetSendingStatus(op, l_refno, Constant.FINISHED);
                    return Constant.OK;
                }
                else
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) EMSMessage FAILED (handle={3}, code={4}, msg={5})"
                        , l_refno
                        , op.sz_operatorname
                        , BitConverter.ToInt32(t_alert.IBAG_message_number, 0)
                        , BitConverter.ToInt32(t_alert.IBAG_referenced_message_number, 0)
                        , t_alert_response.IBAG_message_type
                        , t_alert_response.IBAG_note.First()), 2);
                    return Constant.FAILED;
                }
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("{0} (op={1}) EMSMessage EXCEPTION (msg={2})", l_refno, op.sz_operatorname, e.Message),
                    String.Format("{0} (op={1}) EMSMessage EXCEPTION (msg={2})", l_refno, op.sz_operatorname, e),
                    2);
                return Database.UpdateTries(l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);
            }
        }

        private static void dump_request(object cap_request, Operator op, string method, int refno)
        {
            if (Settings.debug)
            {
                try
                {
                    //CREATE EMPTY xmlns
                    XmlSerializerNamespaces xmlnsEmpty = new XmlSerializerNamespaces();
                    xmlnsEmpty.Add("", "");
                    XmlSerializer s = new XmlSerializer(cap_request.GetType());
                    TextWriter w = new StringWriter(Encoding.UTF8);
                    s.Serialize(w, cap_request, xmlnsEmpty);

                    DebugLog.dump(w.ToString(), op, method, refno);
                }
                catch (Exception e)
                {
                    Log.WriteLog(
                        String.Format("{0} (op={1}) DebugDump {3} EXCEPTION (msg={2})", refno, op.sz_operatorname, e.Message, method),
                        String.Format("{0} (op={1}) DebugDump {3} EXCEPTION (msg={2})", refno, op.sz_operatorname, e, method),
                        2);
                }
            }
        }

        public class StringWriter : System.IO.StringWriter
        {
            public StringWriter(Encoding encoding)
            {
                _encoding = encoding;
            }

            Encoding _encoding;

            public override Encoding Encoding
            {
                get { return _encoding; }
            }
        }
        private static IBAG_Alert_Attributes SendRequest(Operator op, IBAG_Alert_Attributes parameters)
        {
            //CREATE EMPTY xmlns
            XmlSerializerNamespaces xmlnsEmpty = new XmlSerializerNamespaces();
            xmlnsEmpty.Add("", "");
            XmlSerializer s = new XmlSerializer(typeof(IBAG_Alert_Attributes));
            TextWriter w = new StringWriter(Encoding.UTF8);
            
            s.Serialize(w, parameters, xmlnsEmpty);

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
        private static string get_IBAG_polygon(AlertInfo oAlert, Operator op)
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
            ret.Add(ret.ElementAt(0)); // finnish with first coordinate to close the polygon

            return string.Join(" ", ret.ToArray());
        }
        private static int get_IBAG_response_code(string[] response_codes)
        {
            int ret = 0;

            try
            {
                if (response_codes.Length > 0)
                    ret = int.Parse(response_codes[0]);
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Exception getting response code: {0}", e.Message),
                    String.Format("Exception getting response code: {0}", e),
                    2);
            }

            return ret;
        }
    }

    [XmlRoot("OperatorDefaults")]
    public class CB_tmobile_defaults
    {
        [XmlElement("sending_gateway_id")]
        public string sz_sending_gateway_id = "";
        [XmlElement("sender")]
        public string sz_sender = "";
        [XmlElement("cap_identifier")]
        public string sz_cap_identifier = "";
        [XmlElement("cap_sender")]
        public string sz_cap_sender = "";
        [XmlElement("cap_alert_uri")]
        public string sz_cap_alert_uri = "";
        public IBAG_priority priority = IBAG_priority.Background;
        public IBAG_category category = IBAG_category.Geo;
        public IBAG_severity severity = IBAG_severity.Severe;
        public IBAG_urgency urgency = IBAG_urgency.Expected;
        public IBAG_event_code event_code = IBAG_event_code.CDW;
        public IBAG_certainty certainty = IBAG_certainty.Likely;
        public IBAG_response_type response_type = IBAG_response_type.Shelter;
    }
}
