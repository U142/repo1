using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.IO;
using System.Text.RegularExpressions;
using System.Web.Services.Protocols;
using System.Xml.Serialization;

namespace ums.ws.tcs
{
    public class Number
    {
        static readonly Regex All_Numeric_Regex = new Regex("[^0-9]");
        static readonly Regex GSM_Regular_Regex = new Regex("[^a-zA-Z0-9 .∆_ΦΓΛΩΠΨΣΘΞ@£$¥èéùìòÇØøÅåÆæßÉÄÖÑÜ§¿äöñüà+,/:;<=>?¡*!#¤%&'()\"-]");

        private static bool isMSISDN(string Number)
        {
            bool ret = false;

            if (Number.Length==10 && Number.StartsWith("47") && AllNumeric(Number))
                ret = true;

            return ret;
        }
        private static bool AllNumeric(string inputString)
        {
            if (All_Numeric_Regex.IsMatch(inputString))
            {
                return false;
            }
            return true;
        }
        public static int GetOtoa(string Oadc)
        {
            int ret = 1; // default alphanumeric

            if (GSM_Regular_Regex.IsMatch(Oadc))
            {
                // Non-GSM characters found in sender address, return error
                ret = -1;
            }
            else
            {
                if (Oadc.Length >= 2)
                    if (Oadc.StartsWith("+") && AllNumeric(Oadc.Substring(1)))
                        Oadc = Oadc.Replace("+", "00");

                if (AllNumeric(Oadc))	// originating address is numeric, must format correctly for IPX
                {
                    if (Oadc.Length >= 7 && Oadc.Length <= 16) // MSISDN (max 16)
                    {
                        ret = 2;
                    }
                    else if (Oadc.Length <= 11) // Shortnumber (send as alphanumeric, max 11 characters)
                    {
                        //ret = 0; // shortnumber, try to avoid usage
                        ret = 1;
                    }
                    else
                    {
                        ret = -1;
                    }
                }
                else if (Oadc.Length <= 11) // Alphanumeric (max 11)
                {
                    ret = 1;
                }
                else
                {
                    ret = -1;
                }
            }

            return ret;
        }
        public static ERROR CheckMessage(Message message, UserValues uv)
        {
            ERROR ret = ERROR.success;

            if (message==null || message.text.Trim() == "") // empty message not allowed
                return ERROR.sending_nomessage;

            if (message.text.Length > umssettings._settings.GetValue("MaxMessageLength", 160)) // 160 default, 500 max (AlertiX)
                return ERROR.sending_messagetoolong;

            if (GSM_Regular_Regex.IsMatch(message.text)) // contains non-gsm characters
                return ERROR.sending_messageinvalidgsm;

            if (message.mode == ExecuteMode.LIVE && !uv.f_live)
                return ERROR.sending_nosend;

            if (message.mode == ExecuteMode.SIMULATE && !uv.f_simulate)
                return ERROR.sending_nosimulate;

            return ret;
        }

        public static HashSet<string> parse(List<string> number, List<string> invalidNumbers)
        {
            HashSet<string> ret = new HashSet<string>();

            try
            {
                foreach (string s in number)
                {
                    string addNumber = s;

                    // allow 0047xxxxxxxx and +47xxxxxxxx in addition to 47xxxxxxxx
                    if ((s.StartsWith("0047") && s.Length == 12) || (s.StartsWith("+47") && s.Length == 11))
                    {
                        addNumber = s.Substring(s.Length - 8);
                    }

                    if (isMSISDN(addNumber))
                    {
                        ret.Add(addNumber);
                   }
                    else
                    {
                        // use original number in invalidNumbers list
                        invalidNumbers.Add(s);
                    }
                }
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }
                        
            return ret;
        }

        public static List<string> StreamToList(byte[] file)
        {
            List<string> number = new List<string>();

            try
            {
                Stream s = new MemoryStream(file);
                StreamReader f = new StreamReader(s);

                while (!f.EndOfStream)
                {
                    number.Add(f.ReadLine());
                }

                f.Close();
                s.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return number;
        }

        public static string SerializeList(List<string> numbers)
        {
            XmlSerializer s = new XmlSerializer(typeof(List<string>));
            TextWriter w = new StringWriter();
            s.Serialize(w, numbers);
            return w.ToString();
        }
        public static List<string> DeserializeList(string xml)
        {
            XmlSerializer s = new XmlSerializer(typeof(List<string>));
            TextReader r = new StringReader(xml);
            return (List<string>)s.Deserialize(r);
        }
    }
}
