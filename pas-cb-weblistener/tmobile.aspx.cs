using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Xml.Serialization;
using System.IO;
using System.Text;
using pas_cb_server.tmobile;

namespace pas_cb_weblistener
{
    public partial class tmobile : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            try
            {
                Settings.init();

                string filename = Request.RawUrl;
                
                XmlSerializer sr = new XmlSerializer(typeof(IBAG_Alert_Attributes));
                TextReader r = new StreamReader(Request.InputStream);

                IBAG_Alert_Attributes req = (IBAG_Alert_Attributes)sr.Deserialize(r);
                IBAG_Alert_Attributes res = new IBAG_Alert_Attributes();

                res.IBAG_message_number = BitConverter.GetBytes(Database.GetHandle(Settings.l_operator));
                res.IBAG_sent_date_time = DateTime.Now;
                res.IBAG_cap_sent_date_time = req.IBAG_cap_sent_date_time;
                res.IBAG_cap_sent_date_timeSpecified = req.IBAG_cap_sent_date_timeSpecified;
                res.IBAG_referenced_message_number = req.IBAG_message_number;

                switch (req.IBAG_message_type)
                {
                    case IBAG_message_type.TransmissionControlCease:
                        res.IBAG_message_type = Cease(Settings.l_operator);
                        break;
                    case IBAG_message_type.TransmissionControlResume:
                        res.IBAG_message_type = Resume(Settings.l_operator);
                        break;
                    case IBAG_message_type.Report:
                        res.IBAG_message_type = Report(Settings.l_operator, req);
                        break;
                }

                TextWriter w = new StringWriter();
                sr.Serialize(w, res);

                byte[] msg = Encoding.ASCII.GetBytes(w.ToString());

                using (Stream s = Response.OutputStream)
                    s.Write(msg, 0, msg.Length);
            }
            catch (Exception ex)
            {
                Log.WriteLog(
                    String.Format("Failed to handle request {0}", ex.Message), 
                    String.Format("Failed to handle request {0}", ex),
                    2);
            }
        }

        private static IBAG_message_type Cease(int l_operator)
        {
            // disable operator in DB
            if (Database.DisableOperator(l_operator) == Constant.OK)
                return IBAG_message_type.Ack;
            else
                return IBAG_message_type.Error;
        }
        private static IBAG_message_type Resume(int l_operator)
        {
            // enable operator in DB
            if (Database.EnableOperator(l_operator) == Constant.OK)
                return IBAG_message_type.Ack;
            else
                return IBAG_message_type.Error;
        }
        private static IBAG_message_type Report(int l_operator, IBAG_Alert_Attributes t_alert_response)
        {
            // update cellhist for sending
            try
            {
                float cb_percentage = 0;
                int l_2gtotal = 0;
                int l_2gok = 0;
                int l_3gtotal = 0;
                int l_3gok = 0;
                int l_4gtotal = 0;
                int l_4gok = 0;
                int l_refno = 0;

                l_refno = Database.GetRefno(BitConverter.ToInt32(t_alert_response.IBAG_referenced_message_number, 0).ToString(), l_operator);

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

                if (Database.UpdateHistCell(l_refno, l_operator, cb_percentage, l_2gtotal, l_2gok, l_3gtotal, l_3gok, l_4gtotal, l_4gok) == Constant.OK)
                    return IBAG_message_type.Ack;
                else
                    return IBAG_message_type.Error;
            }
            catch
            {
                // something went wrong, return error
                return IBAG_message_type.Error;
            }
        }
    }
}
