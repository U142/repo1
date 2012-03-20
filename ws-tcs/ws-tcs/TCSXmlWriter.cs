using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;

namespace ums_tcs_lib
{
    enum TCS_ERROR
    {
        SUCCESS,
        ERROR,
        EXISTS,
        UNDEFINED,
        NOTIPLEMENTED,
        EMPTY
    }

    class TCSXmlWriter
    {
        private static string getSendFQFN(string path, int refno)
        {
            if (Directory.Exists(path) == false)
                throw new Exception("Path does not exist.");
            if (path.EndsWith("\\") || path.EndsWith("/"))
                return String.Format("{0}TAS_SEND_{1}.tmp", path, refno);
            return String.Format("{0}\\TAS_SEND_{1}.tmp", path, refno);
        }

        private static string getConfirmFQFN(string path, string jobid, int refno)
        {
            if (Directory.Exists(path) == false)
                throw new Exception("Path does not exists.");
            if (path.EndsWith("\\") || path.EndsWith("/"))
                return String.Format("{0}TAS_CONFIRM_{1}.{2}.tmp", path, jobid, refno);
            return String.Format("{0}\\TAS_CONFIRM_{1}.{2}.tmp", path, jobid, refno);
        }

        private static string getFQFN(string path)
        {
            if (Directory.Exists(path) == false)
                throw new Exception("Path does not exist.");
            if (path.EndsWith("\\") || path.EndsWith("/"))
                return String.Format("{0}TAS_Admin_{1}.tmp", path, System.Guid.NewGuid().ToString());
            return String.Format("{0}\\TAS_Admin_{1}.tmp", path, System.Guid.NewGuid().ToString());
        }

        public static void TCSListCreate(
            string path,
            string listname,
            decimal listpk,
            int comppk,
            int deptpk)
        {
            String FQFN = getFQFN(path);
            XmlTextWriter xml = new XmlTextWriter(FQFN, Encoding.UTF8);
            xml.WriteStartDocument();
            {
                xml.WriteWhitespace("\r\n");
                xml.WriteStartElement("TAS");
                {
                    xml.WriteStartAttribute("operation");
                    xml.WriteString("CreateList");
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_listpk");
                    xml.WriteString(listpk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_comppk");
                    xml.WriteString(comppk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_deptpk");
                    xml.WriteString(deptpk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteWhitespace("\r\n  ");
                    xml.WriteStartElement("whitelist");
                    {
                        xml.WriteStartAttribute("name");
                        xml.WriteString(listname);
                        xml.WriteEndAttribute();
                    }//</whitelist>
                    xml.WriteEndElement();
                    xml.WriteWhitespace("\r\n");
                }//</TAS>
                xml.WriteEndElement();
            }
            xml.WriteEndDocument();
            xml.Close();
            String FFQFN = FQFN.Replace(".tmp", ".xml");
            File.Move(FQFN, FFQFN);
        }

        public static void TCSListDelete(
            string path,
            string listname,
            decimal listpk,
            int comppk,
            int deptpk)
        {
            String FQFN = getFQFN(path);
            XmlTextWriter xml = new XmlTextWriter(FQFN, Encoding.UTF8);
            xml.WriteStartDocument();
            {
                xml.WriteWhitespace("\r\n");
                xml.WriteStartElement("TAS");
                {
                    xml.WriteStartAttribute("operation");
                    xml.WriteString("DeleteList");
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_listpk");
                    xml.WriteString(listpk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_comppk");
                    xml.WriteString(comppk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_deptpk");
                    xml.WriteString(deptpk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteWhitespace("\r\n  ");
                    xml.WriteStartElement("whitelist");
                    {
                        xml.WriteStartAttribute("name");
                        xml.WriteString(listname);
                        xml.WriteEndAttribute();
                    }//</whitelist>
                    xml.WriteEndElement();
                    xml.WriteWhitespace("\r\n");
                }//</TAS>
                xml.WriteEndElement();
            }
            xml.WriteEndDocument();
            xml.Close();
            String FFQFN = FQFN.Replace(".tmp", ".xml");
            File.Move(FQFN, FFQFN);
        }

        public static void TCSListUpdate(
            string path,
            HashSet<string> add,
            HashSet<string> rem,
            string listname,
            decimal listpk,
            int comppk,
            int deptpk)
        {
            if (add == null && rem == null)
                throw new Exception("Both lists are null");
            if (add.Count == 0 && rem.Count == 0)
                throw new Exception("Both lists are empty");

            String FQFN = getFQFN(path);
            XmlTextWriter xml = new XmlTextWriter(FQFN, Encoding.UTF8);

            xml.WriteStartDocument();
            {
                xml.WriteWhitespace("\r\n");
                xml.WriteStartElement("TAS");
                {
                    xml.WriteStartAttribute("operation");
                    xml.WriteString("UpdateWhitelist");
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_listpk");
                    xml.WriteString(listpk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_comppk");
                    xml.WriteString(comppk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_deptpk");
                    xml.WriteString(deptpk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteWhitespace("\r\n  ");
                    xml.WriteStartElement("whitelist");
                    {
                        xml.WriteStartAttribute("name");
                        xml.WriteString(listname);
                        xml.WriteEndAttribute();
                        foreach (string s in add)
                        {
                            xml.WriteWhitespace("\r\n    ");
                            xml.WriteStartElement("add");
                            xml.WriteString(s);
                            xml.WriteEndElement();
                        }
                        foreach (string s in rem)
                        {
                            xml.WriteWhitespace("\r\n    ");
                            xml.WriteStartElement("rem");
                            xml.WriteString(s);
                            xml.WriteEndElement();
                        }
                        xml.WriteWhitespace("\r\n  ");
                    }//</whitelist>
                    xml.WriteEndElement();
                    xml.WriteWhitespace("\r\n");
                }//</TAS>
                xml.WriteEndElement();
            }
            xml.WriteEndDocument();
            xml.Close();
            String FFQFN = FQFN.Replace(".tmp", ".xml");
            File.Move(FQFN, FFQFN);
        }

        public static void TCSSendLBA(
            string path,
            int refno,
            int comppk,
            int deptpk,
            int simulate,
            int validity,
            int requesttype,
            int otoa,
            string text,
            string oadc,
            HashSet<int> cc,
            HashSet<string> lists)
        {
            String FQFN = getSendFQFN(path, refno);
            XmlTextWriter xml = new XmlTextWriter(FQFN, Encoding.UTF8);
            xml.WriteStartDocument();
            {
                xml.WriteWhitespace("\r\n");
                xml.WriteStartElement("LBA");
                {
                    xml.WriteStartAttribute("operation");
                    xml.WriteString("SendInternational");
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_refno");
                    xml.WriteString(refno.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_comppk");
                    xml.WriteString(comppk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_deptpk");
                    xml.WriteString(deptpk.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("f_simulation");
                    xml.WriteString(simulate.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_validity");
                    xml.WriteString(validity.ToString());
                    xml.WriteEndAttribute();
                    xml.WriteStartAttribute("l_requesttype");
                    xml.WriteString(requesttype.ToString());
                    xml.WriteEndAttribute();

                    xml.WriteWhitespace("\r\n  ");
                    xml.WriteStartElement("textmessages");
                    {
                        xml.WriteWhitespace("\r\n    ");
                        xml.WriteStartElement("message");
                        {
                            xml.WriteStartAttribute("sz_text");
                            xml.WriteString(text);
                            xml.WriteEndAttribute();
                            xml.WriteStartAttribute("sz_cb_oadc");
                            xml.WriteString(oadc);
                            xml.WriteEndAttribute();
                            xml.WriteStartAttribute("l_oatoa");
                            xml.WriteString(otoa.ToString());
                            xml.WriteEndAttribute();
                            foreach (int i in cc)
                            {
                                xml.WriteWhitespace("\r\n      ");
                                xml.WriteStartElement("ccode");
                                xml.WriteString(i.ToString());
                                xml.WriteEndElement();
                            }
                            xml.WriteWhitespace("\r\n    ");
                        }//<message>
                        xml.WriteEndElement();
                        xml.WriteWhitespace("\r\n  ");
                    }//<textmessags>
                    xml.WriteEndElement();
                    xml.WriteWhitespace("\r\n  ");
                    xml.WriteStartElement("whitelists");
                    {
                        foreach (string s in lists)
                        {
                            xml.WriteWhitespace("\r\n    ");
                            xml.WriteStartElement("whitelist");
                            {
                                xml.WriteStartAttribute("name");
                                xml.WriteString(s);
                                xml.WriteEndAttribute();
                            }//</whitelist>
                            xml.WriteEndElement();
                        }
                        xml.WriteWhitespace("\r\n  ");
                    }//</whitelists>
                    xml.WriteEndElement();
                    xml.WriteWhitespace("\r\n");
                }//</LBA>
                xml.WriteEndElement();
            }
            xml.WriteEndDocument();
            xml.Close();
            String FFQFN = FQFN.Replace(".tmp", ".xml");
            File.Move(FQFN, FFQFN);
        }

        public static void TCSConfirmLBA(
            string path,
            int refno,
            int comppk,
            int deptpk,
            int simulate,
            int oper,
            string jobid,
            bool cancel)
        {
            String FQFN = getConfirmFQFN(path, jobid, refno);
            XmlTextWriter xml = new XmlTextWriter(FQFN, Encoding.UTF8);
            xml.WriteStartDocument();
            xml.WriteWhitespace("\r\n");
            xml.WriteStartElement("LBA");
            xml.WriteStartAttribute("operation");
            if (cancel)
                xml.WriteString("CancelInternational");
            else
                xml.WriteString("ConfirmInternational");
            xml.WriteEndAttribute();
            xml.WriteStartAttribute("l_refno");
            xml.WriteString(refno.ToString());
            xml.WriteEndAttribute();
            xml.WriteStartAttribute("sz_jobid");
            xml.WriteString(jobid);
            xml.WriteEndAttribute();
            xml.WriteStartAttribute("f_simulation");
            xml.WriteString(simulate.ToString());
            xml.WriteEndAttribute();
            xml.WriteStartAttribute("l_operator");
            xml.WriteString(oper.ToString());
            xml.WriteEndAttribute();
            xml.WriteStartAttribute("l_deptpk");
            xml.WriteString(deptpk.ToString());
            xml.WriteEndAttribute();
            xml.WriteStartAttribute("l_comppk");
            xml.WriteString(comppk.ToString());
            xml.WriteEndAttribute();
            xml.WriteEndElement();
            xml.WriteEndDocument();
            xml.Close();
            String FFQFN = FQFN.Replace(".tmp", ".xml");
            File.Move(FQFN, FFQFN);
        }
    }
}