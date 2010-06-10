using System;
using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Remoting.Channels;
using System.Runtime.Remoting.Channels.Http;
using CookComputing.XmlRpc;
using System.Xml.Serialization;
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
            cbc.Url = op.sz_url;

            CBCLOGINREQRESULT loginres = cbc_login(cbc, op);
            if (loginres.cbccbestatuscode != 0) // login failed
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) (CreateAlert) Login FAILED (code={3}, msg={4})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , loginres.cbccberequesthandle
                    , loginres.cbccbestatuscode
                    , loginres.messagetext), 2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 200, op.l_operator, LBATYPE.CB);
            }

            // login OK, update status to parsing
            Database.SetSendingStatus(op, oAlert.l_refno, Constant.PARSING);

            CBCNEWMSGREQUEST newmsgreq = new CBCNEWMSGREQUEST();
            newmsgreq.cbccberequesthandle = Database.GetHandle(op);

            newmsgreq.area = get_area(oAlert, op);
            newmsgreq.pagelist = get_pagelist(oAlert, op);

            newmsgreq.datacodingscheme = 0;
            newmsgreq.displaymode = 0;
            newmsgreq.messageid = oAlert.alert_message.l_channel;

            newmsgreq.repetitioninterval = 11;

            newmsgreq.schedulemethod = 1;
            //newmsgreq.starttime = DateTime.Now.ToString("yyyyMMddHHmmss");
            newmsgreq.endtime = DateTime.Now.AddMinutes(10).ToString("yyyyMMddHHmmss");

            newmsgreq.recurrency = null;
            newmsgreq.recurrencyendtime = null;
            newmsgreq.channelindicator = null;
            newmsgreq.category = 0;

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
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBPREPARING, newmsgres.messagehandle.ToString());
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
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 200, op.l_operator, LBATYPE.CB);
            }
        }
        public static int UpdateAlert(AlertInfo oAlert, Operator op)
        {
            return Constant.OK;
        }
        public static int KillAlert(AlertInfo oAlert, Operator op)
        {
            return Constant.OK;
        }
        public static int GetAlertStatus(int l_refno, int l_msghandle, Operator op)
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));
            cbc.Url = op.sz_url;

            CBCLOGINREQRESULT loginres = cbc_login(cbc, op);
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

            CBCINFOMSGREQRESULT infores = cbc.CBC_InfoMsg(inforeq);
            if (infores.cbccbestatuscode == 0)
            {
                string sz_messagestatus = get_messagestatus(infores.messageinfolist.intervalinfo.First().messagestatus);
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) InfoMessage OK (handle={5}, status={6}, success%={7:0.00})"
                    , l_refno
                    , op.sz_operatorname
                    , infores.cbccberequesthandle
                    , infores.cbccbestatuscode
                    , infores.messagetext
                    , l_msghandle
                    , sz_messagestatus
                    , infores.successpercentage), 0);

                switch (infores.messageinfolist.intervalinfo.First().messagestatus)
                {
                    case 0:   // Processing
                        Database.SetSendingStatus(op, l_refno, Constant.CBPREPARING);
                        break;
                    case 10:  // Planned
                        Database.SetSendingStatus(op, l_refno, Constant.CBQUEUED);
                        break;
                    case 20:  // Starting
                    case 30:  // Running
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
                        }
                        Database.SetSendingStatus(op, l_refno, Constant.CBACTIVE);
                        break;
                    case 40:  // Killing
                        Database.SetSendingStatus(op, l_refno, Constant.CANCELLING);
                        break;
                    case 50:  // Recurring (paused)
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
            msg_page[0].pagelength = bytemsg.Length; //?

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
        /*private static CBECELLLIST get_celllist(AlertInfo oAlert, Operator op)
        {
            CBECELLID[] cellid = new CBECELLID[1];
            cellid[0] = new CBECELLID();
            cellid[0].btsname = "53477";
            cellid[0].ci = null;
            cellid[0].lac = null;
            cellid[0].reason = null;

            CBECELLLIST cells = new CBECELLLIST();
            cells.cbecellid = cellid;
            cells.nrofcells = cellid.Length;

            return cells;
        }*/
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
        private static CBCLOGINREQRESULT cbc_login(Ione2many cbc, Operator op)
        {
            CBCLOGINREQUEST loginreq = new CBCLOGINREQUEST();
            loginreq.cbccberequesthandle = Database.GetHandle(op);
            loginreq.infoprovname = op.sz_login_id;
            loginreq.cbename = op.sz_login_name;
            loginreq.password = op.sz_login_password;
            CBCLOGINREQRESULT loginres = cbc.CBC_Login(loginreq);

            return loginres;
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
    }
}
