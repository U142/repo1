using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Remoting;
using System.Runtime.Remoting.Channels;
using System.Runtime.Remoting.Channels.Http;
using CookComputing.XmlRpc;

namespace xmlrpc_server_one2many
{
    public class one2many : MarshalByRefObject, Ione2many
    {
        public CBCLOGINREQRESULT CBCLOGINREQUEST(int cbccberequesthandle, string infoprovname, string cbename, string password)
        {
            CBCLOGINREQRESULT ret;

            ret.cbccberequesthandle = cbccberequesthandle;
            ret.cbccbestatuscode = 0; // success
            ret.messageclass = 0;
            ret.messagetext = "Success";

            

            return ret;
        }
    }

    class XMLRPCServer
    {
        static void Main(string[] args)
        {
            IDictionary props = new Hashtable();
            props["name"] = "MyHttpChannel";
            props["port"] = 8765;
            HttpChannel channel = new HttpChannel(
               props,
               null,
               new XmlRpcServerFormatterSinkProvider()
            );
            ChannelServices.RegisterChannel(channel, false);

            RemotingConfiguration.RegisterWellKnownServiceType(
              typeof(one2many),
              "one2many.rem",
              WellKnownObjectMode.Singleton);
            Console.WriteLine("Press <ENTER> to shutdown");
            Console.ReadLine();
        }
    }
}
