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

        [XmlRpcMethod]
        CBCNEWMSGPLMNREQRESULT CBC_NewMsgPLMN(CBCNEWMSGPLMNREQUEST req);

        [XmlRpcMethod]
        CBCCHANGEREQRESULT CBC_ChangeMsg(CBCCHANGEREQUEST req);

        [XmlRpcMethod]
        CBCKILLREQRESULT CBC_KillMsg(CBCKILLREQUEST req);

        [XmlRpcMethod]
        CBCINFOMSGREQRESULT CBC_InfoMsg(CBCINFOMSGREQUEST req);
    }

    // CB Methods
    public class CB_one2many
    {
        public static int CreateAlert(AlertInfo oAlert, Operator op)
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));

            CBCLOGINREQUEST loginreq = new CBCLOGINREQUEST();
            //cbc.Url = "http://localhost:5678/cbc/gateway";
            //cbc.Url = "http://92.65.238.116:8000/cbc/gateway";
            //loginreq.infoprovname = "ums";
            //loginreq.cbename = "cbeums";
            //loginreq.password = "12secret";
            cbc.Url = op.sz_url;

            loginreq.cbccberequesthandle = Database.GetHandle(op);
            loginreq.infoprovname = op.sz_login_id;
            loginreq.cbename = op.sz_login_name;
            loginreq.password = op.sz_login_password;
            CBCLOGINREQRESULT loginres = cbc.CBC_Login(loginreq);

            Log.WriteLog(String.Format("{0} (op={1}) (req={2}) LOGIN (code={3}, msg={4})"
                , oAlert.l_refno
                , op.sz_operatorname
                , loginres.cbccberequesthandle
                , loginres.cbccbestatuscode
                , loginres.messagetext), 9);

            if (loginres.cbccbestatuscode != 0) // login failed
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 200, op.l_operator, LBATYPE.CB);

            //CBCNEWMSGREQUEST newmsgreq = new CBCNEWMSGREQUEST();
            CBCNEWMSGCELLREQUEST newmsgreq = new CBCNEWMSGCELLREQUEST();
            newmsgreq.cbccberequesthandle = Database.GetHandle(op);

            //newmsgreq.area = get_area(oAlert, op);
            newmsgreq.cbecelllist = get_celllist(oAlert, op);
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

            //CBCNEWMSGREQRESULT newmsgres = cbc.CBC_NewMsg(newmsgreq);
            CBCNEWMSGCELLREQRESULT newmsgres = cbc.CBC_NewMsgCell(newmsgreq);

            Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NEW_MESSAGE (code={3}, msg={4}, handle{5})"
                , oAlert.l_refno
                , op.sz_operatorname
                , newmsgres.cbccberequesthandle
                , newmsgres.cbccbestatuscode
                , newmsgres.messagetext
                , newmsgres.messagehandle), 9);

            if (newmsgres.cbccbestatuscode != 0)
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 200, op.l_operator, LBATYPE.CB);

            return Constant.OK;
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
            AREADATACoordinatepair msg_poly = new AREADATACoordinatepair();
            msg_poly.COORDINATEPAIR = get_coordinatepair(oAlert, op);

            AREADATA msg_area = new AREADATA();
            msg_area.coordinatepair = msg_poly;
            msg_area.nrofcoordinatepair = oAlert.alert_polygon.Count;
            msg_area.coordinatesystem = 0; // CBC v1.3 only support one co-ordinate system

            return msg_area;
        }
        private static CBECELLLIST get_celllist(AlertInfo oAlert, Operator op)
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
        }
        private static COORDINATEPAIR[] get_coordinatepair(AlertInfo oAlert, Operator op)
        {
            List<COORDINATEPAIR> ret = new List<COORDINATEPAIR>();

            double xcoord, ycoord;

            foreach (PolyPoint wgs84pt in oAlert.alert_polygon)
            {
                // convert from wgs84 to COORDINATE-type
                Tools.ConvertCoordinate(wgs84pt, out xcoord, out ycoord, op.coordinate_type);

                COORDINATEPAIR retpair = new COORDINATEPAIR();
                retpair.xcoordinate = xcoord;
                retpair.ycoordinate = ycoord;

                ret.Add(retpair);
            }

            return ret.ToArray();
        }
    }
}
