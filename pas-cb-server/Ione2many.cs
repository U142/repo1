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

        [XmlRpcMethod("CBCLOGOUTREQUEST")]
        CBCLOGOUTREQRESULT CBC_Logout(CBCLOGOUTREQUEST req);

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
        public static int CreateAlert(AlertInfo oAlert, Operator op, Operation operation)
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));
            CB_one2many_defaults def = (CB_one2many_defaults)op.GetDefaultValues(typeof(CB_one2many_defaults));
            cbc.Url = op.sz_url;

            int l_logincode;
            if (!cbc_login(cbc, op, oAlert.l_refno, "CreateAlert", out l_logincode))
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, l_logincode, op.l_operator, LBATYPE.CB);
            
            try // login OK, send message
            {
                // update status to parsing
                if (Database.SetSendingStatus(op, oAlert.l_refno, Constant.PARSING) != Constant.OK)
                    return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);

                switch (operation) // new area or nationwide
                {
                    case Operation.NEWAREA:
                        return cbc_newalert(cbc, op, oAlert, def);
                    case Operation.NEWPLMN:
                    case Operation.NEWPLMN_TEST:
                    case Operation.NEWPLMN_HEARTBEAT:
                        return cbc_newalert_plmn(cbc, op, oAlert, def);
                    default:
                        return Constant.FAILED;
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
            finally
            {
                // always log out
                cbc_logout(cbc, op, oAlert.l_refno);
            }
        }
        public static int UpdateAlert(AlertInfo oAlert, Operator op)
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));
            CB_one2many_defaults def = (CB_one2many_defaults)op.GetDefaultValues(typeof(CB_one2many_defaults));
            cbc.Url = op.sz_url;

            int l_logincode;
            if (!cbc_login(cbc, op, oAlert.l_refno, "UpdateAlert", out l_logincode))
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, l_logincode, op.l_operator, LBATYPE.CB);

            try // update message
            {
                // login OK, update status to parsing
                if (Database.SetSendingStatus(op, oAlert.l_refno, Constant.PARSING) != Constant.OK)
                    return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);

                CBCCHANGEREQUEST changereq = new CBCCHANGEREQUEST();
                changereq.cbccberequesthandle = Database.GetHandle(op);
                changereq.messagehandle = int.Parse(Database.GetJobID(op, oAlert.l_refno));

                changereq.pagelist = get_pagelist(oAlert, op);
                //newmsgreq.starttime = DateTime.Now.ToString("yyyyMMddHHmmss");

                changereq.schedulemethod = def.l_schedulemethod;

                dump_request(changereq, op, "UpdMessage", oAlert.l_refno);
                if (!Settings.live)
                {
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE);
                    return Constant.OK;
                }

                CBCCHANGEREQRESULT changeres = cbc.CBC_ChangeMsg(changereq);
                dump_request(changeres, op, "UpdMessageResult", oAlert.l_refno);

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
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("{0} (op={1}) ChangeMessage EXCEPTION (msg={2})", oAlert.l_refno, op.sz_operatorname, e.Message),
                    String.Format("{0} (op={1}) ChangeMessage EXCEPTION (msg={2})", oAlert.l_refno, op.sz_operatorname, e),
                    2);
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB);
            }
            finally
            {
                // always log out
                cbc_logout(cbc, op, oAlert.l_refno);
            }
        }
        public static int KillAlert(AlertInfo oAlert, Operator op, string sz_jobid)
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));
            CB_one2many_defaults def = (CB_one2many_defaults)op.GetDefaultValues(typeof(CB_one2many_defaults));
            cbc.Url = op.sz_url;

            int l_logincode;
            if (!cbc_login(cbc, op, oAlert.l_refno, "KillAlert", out l_logincode))
                return Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, l_logincode, op.l_operator, LBATYPE.CB);

            try
            {
                CBCKILLREQUEST killreq = new CBCKILLREQUEST();
                killreq.cbccberequesthandle = Database.GetHandle(op);
                killreq.messagehandle = int.Parse(sz_jobid); // get handle

                killreq.schedulemethod = def.l_schedulemethod;

                dump_request(killreq, op, "KillMessage", oAlert.l_refno);
                if (!Settings.live)
                {
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.FINISHED);
                    return Constant.OK;
                }

                CBCKILLREQRESULT killres = cbc.CBC_KillMsg(killreq);
                dump_request(killres, op, "KillMessageResult", oAlert.l_refno);

                if (killres.cbccbestatuscode == 0)
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) KillMessage OK (handle={3})"
                        , oAlert.l_refno
                        , op.sz_operatorname
                        , killres.cbccberequesthandle
                        , killreq.messagehandle), 0);
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.CANCELLED);
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
                    Database.SetSendingStatus(op, oAlert.l_refno, Constant.CANCELLING);
                    return Constant.RETRY;
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
            }
            finally
            {
                // always log out
                cbc_logout(cbc, op, oAlert.l_refno);
            }
        }
        public static int GetAlertStatus(int l_refno, int l_status, int l_msghandle, Operator op, decimal l_expires_ts) // expires not in use
        {
            Ione2many cbc = (Ione2many)XmlRpcProxyGen.Create(typeof(Ione2many));
            CB_one2many_defaults def = (CB_one2many_defaults)op.GetDefaultValues(typeof(CB_one2many_defaults));
            cbc.Url = op.sz_url;
            string sz_method = "";

            int l_logincode;
            if (!cbc_login(cbc, op, l_refno, "GetAlertStatus", out l_logincode))
                return Constant.FAILED;

            try // get status
            {
                if (op.api_version >= new Version(2, 4)) // use cell count
                {
                    sz_method = "CellCount";
                    return cbc_cellcount(cbc, op, l_refno, l_msghandle, l_status, l_expires_ts);
                }
                else // use infomsg
                {
                    sz_method = "InfoMessage";
                    return cbc_infomsg(cbc, op, l_refno, l_msghandle, l_status);
                }
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("{0} (op={1}) ({3}) EXCEPTION (msg={2})", l_refno, op.sz_operatorname, e.Message, sz_method),
                    String.Format("{0} (op={1}) ({3}) EXCEPTION (msg={2})", l_refno, op.sz_operatorname, e, sz_method),
                    2);
                return Constant.FAILED;
            }
            finally
            {
                // always log out
                cbc_logout(cbc, op, l_refno);
            }
        }

        private static PAGELISTDATA get_pagelist(AlertInfo oAlert, Operator op)
        {
            List<string> gsmmsglist = Tools.SplitMessage(oAlert.alert_message.sz_text, 93, 0);

            PAGEDATA[] msg_page = new PAGEDATA[gsmmsglist.Count];
            int iPage = 0;
            
            foreach (string gsmmsg in gsmmsglist)
            {
                string tmp = gsmmsg;
                if (tmp.Length < 93) // pad with CR if < 1 page
                    tmp = tmp.PadRight(93, '\r');

                byte[] bytemsg = Tools.encodegsm(tmp);

                msg_page[iPage] = new PAGEDATA();
                msg_page[iPage].pagecontents = bytemsg;
                msg_page[iPage].pagelength = bytemsg.Length;
                iPage++;
            }

            PAGELISTDATA msg_pagelist = new PAGELISTDATA();
            msg_pagelist.nrofpages = msg_page.Length;
            msg_pagelist.page = msg_page;

            return msg_pagelist;
        }
        private static AREADATA get_area(AlertInfo oAlert, Operator op)
        {
            AREADATA msg_area = new AREADATA();
            msg_area.coordinatepair = get_coordinatepair(oAlert, op);
            //msg_area.nrofcoordinatepair = oAlert.alert_polygon.Count + 1; // coordinate pair 1 starts and ends the list, and needs to be included in the count as well
            msg_area.nrofcoordinatepair = msg_area.coordinatepair.Count();
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
            ret.Add(ret.ElementAt(0)); // finnish with first coordinate to close the polygon

            return ret.ToArray();
        }
        private static bool cbc_login(Ione2many cbc, Operator op, int l_refno, string sz_method, out int l_cbccbestatuscode)
        {
            l_cbccbestatuscode = -1;

            try // run login, return if it fails with exception or failed login
            {
                CBCLOGINREQRESULT loginres = new CBCLOGINREQRESULT();
                CBCLOGINREQUEST loginreq = new CBCLOGINREQUEST();
                
                loginreq.cbccberequesthandle = Database.GetHandle(op);
                loginreq.infoprovname = op.sz_login_id;
                loginreq.cbename = op.sz_login_name;
                loginreq.password = Convert.ToBase64String(Encoding.ASCII.GetBytes(op.sz_login_password));

                dump_request(loginreq, op, "Login", l_refno);

                if (Settings.live)
                {
                    loginres = cbc.CBC_Login(loginreq);
                    dump_request(loginres, op, "LoginResult", l_refno);
                }

                if (loginres.cbccbestatuscode != 0) // login failed
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (req={2}) ({5}) Login FAILED (code={3}, msg={4})"
                        , l_refno
                        , op.sz_operatorname
                        , loginres.cbccberequesthandle
                        , loginres.cbccbestatuscode
                        , loginres.messagetext
                        , sz_method), 2);
                    return false;
                }
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("{0} (op={1}) ({3}) Login EXCEPTION (msg={2})", l_refno, op.sz_operatorname, e.Message, sz_method),
                    String.Format("{0} (op={1}) ({3}) Login EXCEPTION (msg={2})", l_refno, op.sz_operatorname, e, sz_method),
                    2);
                return false;
            }

            return true; // login successful
        }
        private static void cbc_logout(Ione2many cbc, Operator op, int l_refno)
        {
            CBCLOGOUTREQRESULT res;

            try
            {
                CBCLOGOUTREQUEST logoutreq = new CBCLOGOUTREQUEST();
                logoutreq.cbccberequesthandle = Database.GetHandle(op);

                dump_request(logoutreq, op, "Logout", l_refno);

                if (Settings.live)
                {
                    res = cbc.CBC_Logout(logoutreq);
                    dump_request(res, op, "LogoutResult", l_refno);
                }
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("{0} (op={1}) Logout EXCEPTION (msg={2})", l_refno, op.sz_operatorname, e.Message),
                    String.Format("{0} (op={1}) Logout EXCEPTION (msg={2})", l_refno, op.sz_operatorname, e),
                    2);
            }
        }
        private static int cbc_newalert(Ione2many cbc, Operator op, AlertInfo oAlert, CB_one2many_defaults def)
        {
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

            dump_request(newmsgreq, op, "NewMessage", oAlert.l_refno);
            if (!Settings.live)
            {
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE, "-1", newmsgreq.endtime);
                return Constant.OK;
            }

            CBCNEWMSGREQRESULT newmsgres = cbc.CBC_NewMsg(newmsgreq);
            dump_request(newmsgres, op, "NewMessageResult", oAlert.l_refno);

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
            else if (newmsgres.cbccbestatuscode == 1029) // no cells in area
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessage OK (no cells in area) (handle={5})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , newmsgres.cbccberequesthandle
                    , newmsgres.cbccbestatuscode
                    , newmsgres.messagetext
                    , newmsgres.messagehandle), 0);
                // update database
                Database.UpdateHistCell(oAlert.l_refno, op.l_operator, 100);
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.FINISHED, newmsgres.messagehandle.ToString(), newmsgreq.endtime);
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
        private static int cbc_newalert_plmn(Ione2many cbc, Operator op, AlertInfo oAlert, CB_one2many_defaults def)
        {
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

            dump_request(newmsgreq, op, "NewMessagePLMN", oAlert.l_refno);
            if (!Settings.live)
            {
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.CBACTIVE, "-1", newmsgreq.endtime);
                return Constant.OK;
            }

            CBCNEWMSGPLMNREQRESULT newmsgres = cbc.CBC_NewMsgPLMN(newmsgreq);
            dump_request(newmsgreq, op, "NewMessagePLMNResult", oAlert.l_refno);

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
            else if (newmsgres.cbccbestatuscode == 1029) // no cells in area
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) NewMessagePLMN OK (no cells in area) (handle={5})"
                    , oAlert.l_refno
                    , op.sz_operatorname
                    , newmsgres.cbccberequesthandle
                    , newmsgres.cbccbestatuscode
                    , newmsgres.messagetext
                    , newmsgres.messagehandle), 0);
                // update database
                Database.UpdateHistCell(oAlert.l_refno, op.l_operator, 100);
                Database.SetSendingStatus(op, oAlert.l_refno, Constant.FINISHED, newmsgres.messagehandle.ToString(), newmsgreq.endtime);
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
        private static int cbc_cellcount(Ione2many cbc, Operator op, int l_refno, int l_msghandle, int l_status, decimal l_expires_ts)
        {
            CBCMSGNETWORKCELLCOUNTREQUEST r_cellcount = new CBCMSGNETWORKCELLCOUNTREQUEST();
            r_cellcount.cbccberequesthandle = Database.GetHandle(op);
            r_cellcount.messagehandle = l_msghandle;

            dump_request(r_cellcount, op, "CellCount", l_refno);
            if (!Settings.live)
            {
                Database.SetSendingStatus(op, l_refno, Constant.FINISHED);
                return Constant.OK;
            }

            CBCMSGNETWORKCELLCOUNTREQRESULT cellcount = cbc.CBC_MsgNetworkCellCount(r_cellcount);
            dump_request(cellcount, op, "CellCountResult", l_refno);
            if (cellcount.cbccbestatuscode == 0)
            {
                float cb_percentage = 0;
                int l_2gtotal = 0;
                int l_2gok = 0;
                int l_3gtotal = 0;
                int l_3gok = 0;

                l_2gtotal += cellcount.cellcount2gtotal;
                l_2gok += cellcount.cellcount2gsuccess;

                l_3gtotal += cellcount.cellcount3gtotal;
                l_3gok += cellcount.cellcount3gsuccess;

                cb_percentage = ((float)l_2gok + (float)l_3gok) / ((float)l_2gtotal + (float)l_3gtotal) * 100;

                Database.UpdateHistCell(l_refno, op.l_operator, cb_percentage, cellcount.cellcount2gtotal, cellcount.cellcount2gsuccess, cellcount.cellcount3gtotal, cellcount.cellcount3gsuccess);

                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) CellCount OK (handle={3}, success={4:0.00}%)"
                    , l_refno
                    , op.sz_operatorname
                    , r_cellcount.cbccberequesthandle
                    , l_msghandle
                    , cb_percentage), 0);

                // ok, insert appropriate info in database
                if (l_status != Constant.CBACTIVE && l_status != Constant.USERCANCELLED)
                    Database.SetSendingStatus(op, l_refno, Constant.CBACTIVE);

                // set as finished if expiry date has passed
                if (l_expires_ts <= decimal.Parse(DateTime.Now.ToString("yyyyMMddHHmmss")))
                    Database.SetSendingStatus(op, l_refno, Constant.FINISHED);

                return Constant.OK;
            }
            else
            {
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) CellCount FAILED (code={3}, msg={4}, handle={5})"
                    , l_refno
                    , op.sz_operatorname
                    , cellcount.cbccberequesthandle
                    , cellcount.cbccbestatuscode
                    , cellcount.messagetext
                    , l_msghandle), 2);
                return Constant.FAILED;
            }
        }
        private static int cbc_infomsg(Ione2many cbc, Operator op, int l_refno, int l_msghandle, int l_status)
        {
            CBCINFOMSGREQUEST inforeq = new CBCINFOMSGREQUEST();
            inforeq.cbccberequesthandle = Database.GetHandle(op);
            inforeq.messagehandle = l_msghandle;

            dump_request(inforeq, op, "InfoMessage", l_refno);
            if (!Settings.live)
            {
                Database.SetSendingStatus(op, l_refno, Constant.FINISHED);
                return Constant.OK;
            }

            CBCINFOMSGREQRESULT infores = cbc.CBC_InfoMsg(inforeq);
            dump_request(infores, op, "InfoMessageResult", l_refno);

            if (infores.cbccbestatuscode == 0)
            {
                string sz_messagestatus = get_messagestatus(infores.messageinfolist.intervalinfo.Last().messagestatus);
                Log.WriteLog(String.Format("{0} (op={1}) (req={2}) InfoMessage OK (handle={5}, status={6}, success={7:0.00}%)"
                    , l_refno
                    , op.sz_operatorname
                    , infores.cbccberequesthandle
                    , infores.cbccbestatuscode
                    , infores.messagetext
                    , l_msghandle
                    , sz_messagestatus
                    , infores.successpercentage), 0);

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
                        Database.UpdateHistCell(l_refno, op.l_operator, (float)infores.successpercentage, -1, -1, -1, -1, -1, -1);
                        if (l_status != Constant.CBACTIVE && l_status != Constant.USERCANCELLED)
                            Database.SetSendingStatus(op, l_refno, Constant.CBACTIVE);
                        break;
                    case 40:  // Killing
                        if (l_status != Constant.CANCELLING)
                            Database.SetSendingStatus(op, l_refno, Constant.CANCELLING);
                        break;
                    case 50:  // Recurring (paused)
                        if (l_status != Constant.CBPAUSED)
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
            if (Settings.debug)
            {
                try
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
                catch (Exception e)
                {
                    Log.WriteLog(
                        String.Format("{0} (op={1}) DebugDump {3} EXCEPTION (msg={2})", refno, op.sz_operatorname, e.Message, method),
                        String.Format("{0} (op={1}) DebugDump {3} EXCEPTION (msg={2})", refno, op.sz_operatorname, e, method),
                        2);
                }
            }
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
    }
}
