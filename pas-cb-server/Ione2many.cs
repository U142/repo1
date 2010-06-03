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

        [XmlRpcMethod]
        CBCNEWMSGREQRESULT CBC_NewMsg(CBCNEWMSGREQUEST req);

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

            CBCLOGINREQUEST req = new CBCLOGINREQUEST();
            cbc.Url = "http://localhost:5678/cbc/gateway";
            //cbc.Url = "http://92.65.238.116:8000/cbc/gateway";
            req.cbccberequesthandle = 10213;
            req.infoprovname = "ums";
            req.cbename = "cbeums";
            req.password = "12secret";

            CBCLOGINREQRESULT res = cbc.CBC_Login(req);

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
    }
}
