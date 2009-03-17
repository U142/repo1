using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Xml.Linq;

using System.Xml;
using System.Collections.Generic;

using libums2_csharp;
using com.ums.VB;

namespace com.ums.ws.infosentral
{
    /// <summary>
    /// Summary description for Infosentral
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/vb/", Description="This is a service for creating messages, linking them to an inbound number and setting redirect number. To get an account and a password, contact the UMS Sales office on telephone +47 23501600, or email us on info@ums.no")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Infosentral : System.Web.Services.WebService
    {

        [WebMethod(Description = "Returns information about stored messages")]
        public List<MessageInfo> getStoredMessageInfo(ACCOUNT account)
        {
            com.ums.VB.Infosentral inf = new com.ums.VB.Infosentral();
            return inf.getStoredMessageInfo(account);
        }
        [WebMethod(Description="Stores a message and returns the messagepk")]
        public long storeMessage(ACCOUNT account, string messageName, VOCFILE message)
        {
            com.ums.VB.Infosentral inf = new com.ums.VB.Infosentral();
            return inf.storeMessage(account, messageName, message);
        }
        [WebMethod(Description = "Attaches a message to a number")]
        public string attachMessage(ACCOUNT account, string number, long messagepk)
        {
            com.ums.VB.Infosentral inf = new com.ums.VB.Infosentral();
            inf.attatchMessage(account, number, messagepk);
            return "OK";
        }
        [WebMethod(Description = "Sets redirect number for an inbound number")]
        public string setRedirectNumber(ACCOUNT account, string inboundnumber, string dtmf, string redirectnumber)
        {
            com.ums.VB.Infosentral inf = new com.ums.VB.Infosentral();
            inf.setRedirectNumber(account, inboundnumber, dtmf, redirectnumber);
            return "OK";
        }
        /*
        [WebMethod(Description = "Returns all available Messages")]
        public List<MessageInfo> TESTgetStoredMessageInfo()
        {
            ACCOUNT account = new ACCOUNT();
            account.Company = "UMS";
            account.Department = "TEST";
            account.Password = "ums123";
            com.ums.VB.Infosentral inf = new com.ums.VB.Infosentral();
            return inf.getStoredMessageInfo(account);
        }*/

        /*
        [WebMethod]
        public long test()
        {
            com.ums.VB.Infosentral inf = new com.ums.VB.Infosentral();
            //inf.test();
            ACCOUNT acc = new ACCOUNT();
            acc.Company = "UMS";
            acc.Department = "TEST";
            acc.Password = "ums123";

            VOCFILE voc = new VOCFILE();
            voc.l_langpk = LANGUAGE.NORWEGIAN;
            voc.type = VOCTYPE.TTS;
            voc.sz_tts_string = "Nå må du klappe igjen smella di, du maser som et lokomotiv! As they say in english.";

            inf.attatchMessage(acc,"23501304",inf.storeMessage(acc, "ny wstest med aktivering av nummer", voc));
            return 29923;
        }
        [WebMethod]
        public string testattatchMessage()
        {
            com.ums.VB.Infosentral inf = new com.ums.VB.Infosentral();
            //inf.test();
            ACCOUNT acc = new ACCOUNT();
            acc.Company = "UMS";
            acc.Department = "TEST";
            acc.Password = "ums123";

            VOCFILE voc = new VOCFILE();
            voc.l_langpk = LANGUAGE.NORWEGIAN;
            voc.type = VOCTYPE.TTS;
            voc.sz_tts_string = "Nå må du klappe igjen smella di, du maser som et lokomotiv! As they say in english.";
           
            inf.attatchMessage(acc, "23501304", 30331);

            return "ninja";
        }
        [WebMethod]
        public void testsetRedirectNumber()
        {
            com.ums.VB.Infosentral inf = new com.ums.VB.Infosentral();
            //inf.test();
            ACCOUNT acc = new ACCOUNT();
            acc.Company = "UMS";
            acc.Department = "TEST";
            acc.Password = "ums123";

            VOCFILE voc = new VOCFILE();
            voc.l_langpk = LANGUAGE.NORWEGIAN;
            voc.type = VOCTYPE.TTS;
            voc.sz_tts_string = "Nå må du klappe igjen smella di, du maser som et lokomotiv! As they say in english.";
            
            inf.setRedirectNumber(acc, "23501304", "1", "92293390");
        }
        [WebMethod]
        public void soapExceptionTest()
        {

            com.ums.VB.Infosentral inf = new com.ums.VB.Infosentral();
            inf.testSoapException();
        }
        private SoapException raiseException(Exception e)
        {
            // Build the detail element of the SOAP fault.
            System.Xml.XmlDocument doc = new System.Xml.XmlDocument();
            System.Xml.XmlNode node = doc.CreateNode(XmlNodeType.Element,
                 SoapException.DetailElementName.Name, SoapException.DetailElementName.Namespace);

            // Build specific details for the SoapException.
            // Add first child of detail XML element.
            System.Xml.XmlNode details =
              doc.CreateNode(XmlNodeType.Element, "Error",
                             "http://ums.no/soapExceptionTest/");
            System.Xml.XmlNode detailsChild =
              doc.CreateNode(XmlNodeType.Element, "ErrorMessage",
                             "http://ums.no/soapExceptionTest/");
            details.AppendChild(detailsChild);

            // Add second child of detail XML element with an attribute.
            System.Xml.XmlNode details2 =
              doc.CreateNode(XmlNodeType.Element, "ErrorSource",
                             "http://tempuri.org/");
            XmlAttribute attr = doc.CreateAttribute("t", "attrName",
                                "http://tempuri.org/");
            attr.Value = "attrValue";
            details2.Attributes.Append(attr);

            // Append the two child elements to the detail node.
            node.AppendChild(details);
            node.AppendChild(details2);

            //Throw the exception    
            SoapException se = new SoapException("Mai messidj",
              SoapException.ClientFaultCode,
              Context.Request.Url.AbsoluteUri,
              node);

            return se;
        }*/
    }
}
