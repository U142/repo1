using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace umswhitelists
{
    public class _whitelists
    {
        public static HashSet<string> GetWhiteLists(string sz_compid, string sz_deptid, int l_operator)
        {
            XmlDocument oDoc = new XmlDocument();
            HashSet<string> ret = new HashSet<string>();

            try
            {
                oDoc.Load("WhiteLists.xml");
                if (oDoc != null)
                {
                    if (oDoc.SelectSingleNode("WhiteLists") != null)
                    {
                        // Global lists
                        foreach (XmlNode oList in oDoc.SelectSingleNode("WhiteLists").SelectNodes("list"))
                        {
                            if (oList.Attributes.GetNamedItem("operator") == null)
                                ret.Add(oList.Attributes.GetNamedItem("name").Value);
                            else if (oList.Attributes.GetNamedItem("operator").Value == l_operator.ToString())
                                ret.Add(oList.Attributes.GetNamedItem("name").Value);
                        }

                        // Check if company lists exist
                        if (oDoc.SelectSingleNode("WhiteLists").SelectSingleNode(sz_compid) != null)
                        {
                            // Company lists
                            foreach (XmlNode oList in oDoc.SelectSingleNode("WhiteLists").SelectSingleNode(sz_compid).SelectNodes("list"))
                            {
                                if (oList.Attributes.GetNamedItem("operator") == null)
                                    ret.Add(oList.Attributes.GetNamedItem("name").Value);
                                else if (oList.Attributes.GetNamedItem("operator").Value == l_operator.ToString())
                                    ret.Add(oList.Attributes.GetNamedItem("name").Value);
                            }

                            // Check if department lists exist
                            if (oDoc.SelectSingleNode("WhiteLists").SelectSingleNode(sz_compid).SelectSingleNode(sz_deptid) != null)
                            {
                                // Department lists
                                foreach (XmlNode oList in oDoc.SelectSingleNode("WhiteLists").SelectSingleNode(sz_compid).SelectSingleNode(sz_deptid).SelectNodes("list"))
                                {
                                    if (oList.Attributes.GetNamedItem("operator") == null)
                                        ret.Add(oList.Attributes.GetNamedItem("name").Value);
                                    else if (oList.Attributes.GetNamedItem("operator").Value == l_operator.ToString())
                                        ret.Add(oList.Attributes.GetNamedItem("name").Value);
                                }
                            }
                        }
                    }
                }
            }
            catch { }

            return ret;
        }
    }
}
