using System;
using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Remoting.Channels;
using System.Runtime.Remoting.Channels.Http;
using CookComputing.XmlRpc;
using System.Xml.Serialization;
using System.Xml;
using System.Net;
using System.IO;
using pas_cb_server.one2many;

namespace pas_cb_server
{
    // XML RPC Methods
    public interface Ione2many : IXmlRpcProxy
    {
        [XmlRpcMethod("CBCLOGINREQUEST")]
        CBCLOGINREQRESULT CBC_Login(CBCLOGINREQUEST req);

        [XmlRpcMethod("CBCNEWMSGREQUEST")]
        CBCNEWMSGREQRESULT CBC_NewMsg(CBCNEWMSGREQUEST req);

        [XmlRpcMethod("CBCNEWMSGCELLREQUEST")]
        CBCNEWMSGCELLREQRESULT CBC_NewMsgCell(CBCNEWMSGCELLREQUEST req);

        [XmlRpcMethod("CBCNEWMSGPLMNREQUEST")]
        CBCNEWMSGPLMNREQRESULT CBC_NewMsgPLMN(CBCNEWMSGPLMNREQUEST req);

        [XmlRpcMethod("CBCCHANGEREQUEST")]
        CBCCHANGEREQRESULT CBC_ChangeMsg(CBCCHANGEREQUEST req);

        [XmlRpcMethod("CBCKILLREQUEST")]
        CBCKILLREQRESULT CBC_KillMsg(CBCKILLREQUEST req);

        [XmlRpcMethod("CBCINFOMSGREQUEST")]
        CBCINFOMSGREQRESULT CBC_InfoMsg(CBCINFOMSGREQUEST req);

        [XmlRpcMethod("CBCMSGNETWORKCELLCOUNTREQUEST")]
        CBCMSGNETWORKCELLCOUNTREQRESULT CBC_MsgNetworkCellCount(CBCMSGNETWORKCELLCOUNTREQUEST req);
    }

    // CB Methods
    public class CB_one2many
    {
        public static int CreateAlert(AlertInfo oAlert, Operator op)
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));
            CB_one2many_defaults def = (CB_one2many_defaults)op.GetDefaultValues(typeof(CB_one2many_defaults));
            cbc.Url = op.sz_url;

            CBCLOGINREQRESULT loginres = cbc_login(cbc, op, oAlert.l_refno);
            if (loginres.cbccbestatuscode != 0) // login failed
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) (CreateAlert) Login FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , loginres.cbccberequesthandle
                    , loginres.cbccbestatuscode
                    , loginres.messagetext), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, loginres.cbccbestatuscode, op.l_operator, LBATYPE.CB);
            }

            // login OK, update status to parsing
            Database.SetSendingStatus(op, oAlert.l_refno, Constant.PARSING);

            CBCNEWMSGREQUEST newmsgreq = new CBCNEWMSGREQUEST();
            newmsgreq.cbccberequesthandle = Database.GetHandle(op);

            newmsgreq.area = get_area(oAlert, op);
            newmsgreq.pagelist = get_pagelist(oAlert, op);
            newmsgreq.messageid = oAlert.alert_message.l_channel; // channel
            //newmsgreq.starttime = DateTime.Now.ToString("yyyyMMddHHmmss");
            newmsgreq.endtime = DateTime.Now.AddMinutes(oAlert.l_validity).ToString("yyyyMMddHHmmss");

            // default values from config
            newmsgreq.datacodingscheme = def.l_datacodingscheme;
            newmsgreq.displaymode = def.l_displaymode;
            newmsgreq.repetitioninterval = def.l_repetitioninterval;
            newmsgreq.schedulemethod = def.l_schedulemethod;
            newmsgreq.recurrency = def.l_recurrency;
            newmsgreq.recurrencyendtime = def.recurrencyendtime;
            newmsgreq.channelindicator = def.l_channelindicator;
            newmsgreq.category = def.l_category;

            if (Settings.debug)
            {
                dump_request(newmsgreq, op, "NewMessage", oAlert.l_refno);
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE, "-1", newmsgreq.endtime);
                return Constant.OK;
            }

            CBCNEWMSGREQRESULT newmsgres = cbc.CBC_NewMsg(newmsgreq);

            if (newmsgres.cbccbestatuscode == 0)
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage OK (handle={5})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , newmsgres.cbccberequesthandle
                    , newmsgres.cbccbestatuscode
                    , newmsgres.messagetext
                    , newmsgres.messagehandle), 0);
                // update database
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE, newmsgres.messagehandle.ToString(), newmsgreq.endtime);
                return Constant.OK;
            }
            else
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , newmsgres.cbccberequesthandle
                    , newmsgres.cbccbestatuscode
                    , newmsgres.messagetext
                    , newmsgres.messagehandle), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, newmsgres.cbccbestatuscode, op.l_operator, LBATYPE.CB);
            }
        }
        public static int CreateAlertPLMN(AlertInfo oAlert, Operator op)
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));
            CB_one2many_defaults def = (CB_one2many_defaults)op.GetDefaultValues(typeof(CB_one2many_defaults));
            cbc.Url = op.sz_url;

            CBCLOGINREQRESULT loginres = cbc_login(cbc, op, oAlert.l_refno);
            if (loginres.cbccbestatuscode != 0) // login failed
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) (CreateAlertPLMN) Login FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , loginres.cbccberequesthandle
                    , loginres.cbccbestatuscode
                    , loginres.messagetext), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, loginres.cbccbestatuscode, op.l_operator, LBATYPE.CB);
            }

            // login OK, update status to parsing
            Database.SetSendingStatus(op, oAlert.l_refno, Constant.PARSING);

            CBCNEWMSGPLMNREQUEST newmsgreq = new CBCNEWMSGPLMNREQUEST();
            newmsgreq.cbccberequesthandle = Database.GetHandle(op);

            newmsgreq.pagelist = get_pagelist(oAlert, op);
            newmsgreq.messageid = oAlert.alert_message.l_channel; // channel
            //newmsgreq.starttime = DateTime.Now.ToString("yyyyMMddHHmmss");
            newmsgreq.endtime = DateTime.Now.AddMinutes(oAlert.l_validity).ToString("yyyyMMddHHmmss");

            // default values from config
            newmsgreq.datacodingscheme = def.l_datacodingscheme;
            newmsgreq.displaymode = def.l_displaymode;
            newmsgreq.repetitioninterval = def.l_repetitioninterval;
            newmsgreq.schedulemethod = def.l_schedulemethod;
            newmsgreq.recurrency = def.l_recurrency;
            newmsgreq.recurrencyendtime = def.recurrencyendtime;
            newmsgreq.channelindicator = def.l_channelindicator;
            newmsgreq.category = def.l_category;

            if (Settings.debug)
            {
                dump_request(newmsgreq, op, "NewMessagePLMN", oAlert.l_refno);
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE, "-1", newmsgreq.endtime);
                return Constant.OK;
            }

            CBCNEWMSGPLMNREQRESULT newmsgres = cbc.CBC_NewMsgPLMN(newmsgreq);

            if (newmsgres.cbccbestatuscode == 0)
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessagePLMN OK (handle={5})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , newmsgres.cbccberequesthandle
                    , newmsgres.cbccbestatuscode
                    , newmsgres.messagetext
                    , newmsgres.messagehandle), 0);
                // update database
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE, newmsgres.messagehandle.ToString(), newmsgreq.endtime);
                return Constant.OK;
            }
            else
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessagePLMN FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , newmsgres.cbccberequesthandle
                    , newmsgres.cbccbestatuscode
                    , newmsgres.messagetext
                    , newmsgres.messagehandle), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, newmsgres.cbccbestatuscode, op.l_operator, LBATYPE.CB);
            }
        }
        public static int UpdateAlert(AlertInfo oAlert, Operator op)
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));
            CB_one2many_defaults def = (CB_one2many_defaults)op.GetDefaultValues(typeof(CB_one2many_defaults));
            cbc.Url = op.sz_url;

            CBCLOGINREQRESULT loginres = cbc_login(cbc, op, oAlert.l_refno);
            if (loginres.cbccbestatuscode != 0) // login failed
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) (UpdateAlert) Login FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , loginres.cbccberequesthandle
                    , loginres.cbccbestatuscode
                    , loginres.messagetext), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, loginres.cbccbestatuscode, op.l_operator, LBATYPE.CB);
            }

            // login OK, update status to parsing
            Database.SetSendingStatus(op, oAlert.l_refno, Constant.PARSING);

            CBCCHANGEREQUEST changereq = new CBCCHANGEREQUEST();
            changereq.cbccberequesthandle = Database.GetHandle(op);
            changereq.messagehandle = int.Parse(Database.GetJobID(op, oAlert.l_refno));

            changereq.pagelist = get_pagelist(oAlert, op);
            //newmsgreq.starttime = DateTime.Now.ToString("yyyyMMddHHmmss");

            changereq.schedulemethod = def.l_schedulemethod;

            if (Settings.debug)
            {
                dump_request(changereq, op, "UpdMessage", oAlert.l_refno);
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE);
                return Constant.OK;
            }

            CBCCHANGEREQRESULT changeres = cbc.CBC_ChangeMsg(changereq);

            if (changeres.cbccbestatuscode == 0)
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) ChangeMessage OK (handle={5})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , changeres.cbccberequesthandle
                    , changeres.cbccbestatuscode
                    , changeres.messagetext
                    , changereq.messagehandle), 0);
                // update database
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE);
                return Constant.OK;
            }
            else
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) ChangeMessage FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , changeres.cbccberequesthandle
                    , changeres.cbccbestatuscode
                    , changeres.messagetext
                    , changereq.messagehandle), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, changeres.cbccbestatuscode, op.l_operator, LBATYPE.CB);
            }
        }
        public static int KillAlert(AlertInfo oAlert, Operator op)
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));
            CB_one2many_defaults def = (CB_one2many_defaults)op.GetDefaultValues(typeof(CB_one2many_defaults));
            cbc.Url = op.sz_url;

            CBCLOGINREQRESULT loginres = cbc_login(cbc, op, oAlert.l_refno);
            if (loginres.cbccbestatuscode != 0) // login failed
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) (KillAlert) Login FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , loginres.cbccberequesthandle
                    , loginres.cbccbestatuscode
                    , loginres.messagetext), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, loginres.cbccbestatuscode, op.l_operator, LBATYPE.CB);
            }

            CBCKILLREQUEST killreq = new CBCKILLREQUEST();
            killreq.cbccberequesthandle = Database.GetHandle(op);
            killreq.messagehandle = int.Parse(Database.GetJobID(op, oAlert.l_refno)); // get handle

            killreq.schedulemethod = def.l_schedulemethod;

            if (Settings.debug)
            {
                dump_request(killreq, op, "KillMessage", oAlert.l_refno);
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.FINISHED);
                return Constant.OK;
            }
            
            CBCKILLREQRESULT killres = cbc.CBC_KillMsg(killreq);

            if (killres.cbccbestatuscode == 0)
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) KillMessage OK (handle={3})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , killres.cbccberequesthandle
                    , killreq.messagehandle), 0);
                // update database
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.USERCANCELLED);
                return Constant.OK;
            }
            else
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) KillMessage FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , killres.cbccberequesthandle
                    , killres.cbccbestatuscode
                    , killres.messagetext), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, killres.cbccbestatuscode, op.l_operator, LBATYPE.CB);
            }
        }
        public static int GetAlertStatus(int l_refno, int l_status, int l_msghandle, Operator op, decimal l_expires_ts) // expires not in use
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));
            CB_one2many_defaults def = (CB_one2many_defaults)op.GetDefaultValues(typeof(CB_one2many_defaults));
            cbc.Url = op.sz_url;

            CBCLOGINREQRESULT loginres = cbc_login(cbc, op, l_refno);
            if (loginres.cbccbestatuscode != 0) // login failed
            {
                Log.WriteLog(String.Format("{0} {1} (op={2}) (req={3}) (GetAlertStatus) Login FAILED (code={4}, msg={5})"
                    , l_refno
                    , l_msghandle
                    , op.sz_operatorname
                    , loginres.cbccberequesthandle
                    , loginres.cbccbestatuscode
                    , loginres.messagetext), 2);
                return Constant.FAILED;
            }

            CBCINFOMSGREQUEST inforeq = new CBCINFOMSGREQUEST();
            inforeq.cbccberequesthandle = Database.GetHandle(op);
            inforeq.messagehandle = l_msghandle;

            if (Settings.debug)
            {
                dump_request(inforeq, op, "InfoMessage", l_refno);
                Database.SetSendingStatus(op, l_refno, Constant.FINISHED);
                return Constant.OK;
            }

            CBCINFOMSGREQRESULT infores = cbc.CBC_InfoMsg(inforeq);
            if (infores.cbccbestatuscode == 0)
            {
                string sz_messagestatus = get_messagestatus(infores.messageinfolist.intervalinfo.Last().messagestatus);
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) InfoMessage OK (handle={5}, status={6}, success={7:0.00}%, expires={8:G}, updates={9}, message=\"{10}\")"
                    , l_refno
                    , op.sz_operatorname
                    , infores.cbccberequesthandle
                    , infores.cbccbestatuscode
                    , infores.messagetext
                    , l_msghandle
                    , sz_messagestatus
                    , infores.successpercentage
                    , DateTime.ParseExact(infores.messageinfolist.intervalinfo.Last().endtime, "yyyyMMddHHmmss",System.Globalization.CultureInfo.InvariantCulture)
                    , infores.messageinfolist.nrofintervals
                    , ASCIIEncoding.ASCII.GetString(infores.messageinfolist.intervalinfo.Last().pagelist.page.Last().pagecontents, 0, infores.messageinfolist.intervalinfo.Last().pagelist.page.Last().pagelength)), 0);

                switch (infores.messageinfolist.intervalinfo.Last().messagestatus)
                {
                    case 0:   // Processing
                        if (l_status != Constant.CBPREPARING)
                            Database.SetSendingStatus(op, l_refno, Constant.CBPREPARING);
                        break;
                    case 10:  // Planned
                        if (l_status != Constant.CBQUEUED)
                            Database.SetSendingStatus(op, l_refno, Constant.CBQUEUED);
                        break;
                    case 20:  // Starting
                    case 30:  // Running
                        if (op.api_version >= new Version(2, 5))
                        {
                            CBCMSGNETWORKCELLCOUNTREQUEST r_cellcount = new CBCMSGNETWORKCELLCOUNTREQUEST();
                            r_cellcount.cbccberequesthandle = Database.GetHandle(op);
                            r_cellcount.messagehandle = l_msghandle;

                            CBCMSGNETWORKCELLCOUNTREQRESULT cellcount = cbc.CBC_MsgNetworkCellCount(r_cellcount);
                            if (cellcount.cbecbcstatuscode == 0)
                            {
                                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) CellCount OK (handle={3}, 2gSuccess={4}, 2gTotal={5}, 3gSuccess={6}, 3gTotal={7})"
                                    , l_refno
                                    , op.sz_operatorname
                                    , infores.cbccberequesthandle
                                    , l_msghandle
                                    , cellcount.cellcount2gsuccess
                                    , cellcount.cellcount2gtotal
                                    , cellcount.cellcount3gsuccess
                                    , cellcount.cellcount3gtotal), 0);
                                Database.UpdateHistCell(l_refno, op.l_operator, cellcount.cellcount2gtotal, cellcount.cellcount2gsuccess, cellcount.cellcount3gtotal, cellcount.cellcount3gsuccess);
                            }
                        }
                        if (l_status != Constant.CBACTIVE && l_status != Constant.USERCANCELLED)
                            Database.SetSendingStatus(op, l_refno, Constant.CBACTIVE);
                        break;
                    case 40:  // Killing
                        if (l_status != Constant.CANCELLING)
                            Database.SetSendingStatus(op, l_refno, Constant.CANCELLING);
                        break;
                    case 50:  // Recurring (paused)
                        if(l_status != Constant.CBPAUSED)
                            Database.SetSendingStatus(op, l_refno, Constant.CBPAUSED);
                        break;
                    case 100: // Killed
                    case 110: // Expired
                    case 120: // Error
                    case 130: // Disabled
                    case 140: // Deleted (PLNM override)
                        Database.SetSendingStatus(op, l_refno, Constant.FINISHED);
                        break;
                }
                return Constant.OK;
            }
            else
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) InfoMessage FAILED (code={3}, msg={4}, handle={5})"
                    , l_refno
                    , op.sz_operatorname
                    , infores.cbccberequesthandle
                    , infores.cbccbestatuscode
                    , infores.messagetext
                    , l_msghandle), 2);
                return Constant.FAILED;
            }
        }

        private static PAGELISTDATA get_pagelist(AlertInfo oAlert, Operator op)
        {
            byte[] bytemsg = System.Text.Encoding.ASCII.GetBytes(oAlert.alert_message.sz_text);

            // only 1 page pr. alert
            PAGEDATA[] msg_page = new PAGEDATA[1];
            msg_page[0] = new PAGEDATA();
            msg_page[0].pagecontents = bytemsg;
            msg_page[0].pagelength = bytemsg.Length;

            PAGELISTDATA msg_pagelist = new PAGELISTDATA();
            msg_pagelist.nrofpages = msg_page.Length;
            msg_pagelist.page = msg_page;

            return msg_pagelist;
        }
        private static AREADATA get_area(AlertInfo oAlert, Operator op)
        {
            AREADATA msg_area = new AREADATA();
            msg_area.coordinatepair = get_coordinatepair(oAlert, op);
            msg_area.nrofcoordinatepair = oAlert.alert_polygon.Count;
            msg_area.coordinatesystem = 0; // CBC v1.3 only support one co-ordinate system

            return msg_area;
        }
        private static COORDINATEPAIR[] get_coordinatepair(AlertInfo oAlert, Operator op)
        {
            List<COORDINATEPAIR> ret = new List<COORDINATEPAIR>();

            double xcoord, ycoord;

            foreach (PolyPoint wgs84pt in oAlert.alert_polygon)
            {
                // convert from wgs84 to COORDINATE-type
                if(op.coordinate_type == COORDINATESYSTEM.WGS84) // one2many uses lat as x (swap)
                    Tools.ConvertCoordinate(wgs84pt, out ycoord, out xcoord, op.coordinate_type);
                else
                    Tools.ConvertCoordinate(wgs84pt, out xcoord, out ycoord, op.coordinate_type);

                COORDINATEPAIR retpair = new COORDINATEPAIR();
                retpair.xcoordinate = xcoord;
                retpair.ycoordinate = ycoord;

                ret.Add(retpair);
            }

            return ret.ToArray();
        }
        private static CBCLOGINREQRESULT cbc_login(Ione2many cbc, Operator op, int l_refno)
        {
            CBCLOGINREQUEST loginreq = new CBCLOGINREQUEST();
            loginreq.cbccberequesthandle = Database.GetHandle(op);
            loginreq.infoprovname = op.sz_login_id;
            loginreq.cbename = op.sz_login_name;
            loginreq.password = op.sz_login_password;

            if (Settings.debug)
            {
                dump_request(loginreq, op, "Login", l_refno);
                return new CBCLOGINREQRESULT();
            }
            else
            {
                return cbc.CBC_Login(loginreq);
            }
        }
        private static String get_messagestatus(Int32 messagestatus)
        {
            switch (messagestatus)
            {
                case 0: return "Processing";
                case 10: return "Planned";
                case 20: return "Starting";
                case 30: return "Running";
                case 40: return "Killing";
                case 50: return "Recurring";
                case 100: return "Finished";
                case 110: return "Skipped";
                case 120: return "Error";
                case 130: return "Disabled";
                case 140: return "Deleted";
                default: return "UNDEFINED";
            }
        }
        private static void dump_request(object xmlrpcrequest, Operator op, string method, int refno)
        {
            XmlRpcSerializer ser = new XmlRpcSerializer();
            XmlRpcRequest req = new XmlRpcRequest();

            req.method = xmlrpcrequest.GetType().Name;
            req.args = new object[] { xmlrpcrequest };

            Stream st = new MemoryStream();
            ser.SerializeRequest(st, req);
            st.Seek(0, SeekOrigin.Begin);

            StreamReader sr = new StreamReader(st);

            DebugLog.dump(sr.ReadToEnd(), op, method, refno);
        }
    }

    [XmlRoot("OperatorDefaults")]
    public class CB_one2many_defaults
    {
       [XmlElement("datacodingscheme")]
        public int l_datacodingscheme = 0;
        [XmlElement("displaymode")]
        public int l_displaymode = 0;
        [XmlElement("repetitioninterval")]
        public int l_repetitioninterval = 11;
        [XmlElement("schedulemethod")]
        public int l_schedulemethod = 1;
        [XmlElement("recurrency")]
        public int? l_recurrency = null;
        [XmlElement("recurrencyendtime")]
        public string recurrencyendtime = null;
        [XmlElement("channelindicator")]
        public int? l_channelindicator = null;
        [XmlElement("category")]
        public int l_category = 0;
        [XmlElement("testchannel")]
        public int l_testchannel = 500;
    }
}
