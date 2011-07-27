using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography;

using System.Xml;

using pasinvoke.pas.status;

namespace pasinvoke
{
    static class Utils
    {
        public static string CreateSHA512Hash(string input)
        {
            SHA512 sha = SHA512.Create();
            byte[] inputBytes = System.Text.Encoding.ASCII.GetBytes(input);
            byte[] hashBytes = sha.ComputeHash(inputBytes);

            // Convert the byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hashBytes.Length; i++)
            {
                sb.Append(hashBytes[i].ToString("x2"));
                // To force the hex string to lower-case letters instead of
                // upper-case, use he following line instead:
                // sb.Append(hashBytes[i].ToString("x2")); 
            }
            return sb.ToString();
        }

        public static List<MDVSENDINGINFO> Deserialize(XmlDataDocument xmldoc)
        {
            XmlNodeList nl = xmldoc.GetElementsByTagName("SENDING");
            List<MDVSENDINGINFO> mdvlist = new List<MDVSENDINGINFO>();
            MDVSENDINGINFO info;

            XmlNodeList lbanl = xmldoc.GetElementsByTagName("LBASEND");

            List<ULBASENDING> lbalist = new List<ULBASENDING>();
            ULBASENDING lbasend;

            foreach (XmlNode node in lbanl)
            {
                lbasend = new ULBASENDING();
                lbasend.l_refno = long.Parse(node.Attributes["l_refno"].Value);
                lbasend.l_status = int.Parse(node.Attributes["l_status"].Value);
                lbasend.l_items = int.Parse(node.Attributes["l_items"].Value);
                lbasend.l_proc = int.Parse(node.Attributes["l_proc"].Value);
                lbasend.sz_operator = node.Attributes["sz_operator"].Value;
                lbalist.Add(lbasend);
            }

            foreach (XmlNode node in nl)
            {
                info = createMDVSENDINFO(node);

                if (info.l_type == 4)
                {
                    foreach (ULBASENDING s in lbalist)
                    {
                        if (s.l_refno == info.l_refno)
                        {
                            info.sz_sendingname += " " + s.sz_operator;
                            info.l_sendingstatus = s.l_status;
                            info.l_totitem = s.l_items;
                            info.l_processed = s.l_proc;
                            mdvlist.Add(info);
                            info = createMDVSENDINFO(node);
                        }
                    }
                }
                else
                {
                    mdvlist.Add(info);
                }

            }

            
            return mdvlist;
        }

        private static MDVSENDINGINFO createMDVSENDINFO(XmlNode node)
        {
            MDVSENDINGINFO info = new MDVSENDINGINFO();
            info.sz_sendingname = node.Attributes["sz_sendingname"].Value;
            info.l_refno = long.Parse(node.Attributes["l_refno"].Value);
            info.l_group = long.Parse(node.Attributes["l_group"].Value);
            info.l_createdate = node.Attributes["l_createdate"].Value;
            info.l_createtime = node.Attributes["l_createtime"].Value;
            info.l_scheddate = node.Attributes["l_scheddate"].Value;
            info.l_schedtime = node.Attributes["l_schedtime"].Value;
            info.l_sendingstatus = long.Parse(node.Attributes["l_sendingstatus"].Value);
            info.l_companypk = long.Parse(node.Attributes["l_comppk"].Value);
            info.l_deptpk = long.Parse(node.Attributes["l_deptpk"].Value);
            info.l_type = long.Parse(node.Attributes["l_type"].Value);
            info.l_addresstypes = long.Parse(node.Attributes["l_addresstypes"].Value);
            info.l_profilepk = long.Parse(node.Attributes["l_profilepk"].Value);
            info.l_queuestatus = long.Parse(node.Attributes["l_queuestatus"].Value);
            info.l_totitem = long.Parse(node.Attributes["l_totitem"].Value);
            info.l_processed = long.Parse(node.Attributes["l_proc"].Value);
            info.l_altjmp = long.Parse(node.Attributes["l_altjmp"].Value);
            info.l_alloc = long.Parse(node.Attributes["l_alloc"].Value);
            info.l_maxalloc = long.Parse(node.Attributes["l_maxalloc"].Value);
            info.sz_oadc = node.Attributes["sz_oadc"].Value;
            info.l_qreftype = long.Parse(node.Attributes["l_qreftype"].Value);
            info.l_nofax = long.Parse(node.Attributes["l_nofax"].Value);
            info.l_linktype = int.Parse(node.Attributes["l_linktype"].Value);
            info.l_resendrefno = int.Parse(node.Attributes["l_resendrefno"].Value);
            info.sz_messagetext = node.Attributes["sz_messagetext"].Value;
            info.sz_actionprofilename = node.Attributes["sz_actionprofilename"].Value;
            info.sz_messagetext = node.Attributes["sz_messagetext"].Value;
            info.b_marked_as_cancelled = bool.Parse(node.Attributes["b_marked_as_cancelled"].Value);

            return info;
        }
    }
}
