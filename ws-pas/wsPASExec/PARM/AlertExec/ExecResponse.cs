using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;
using System.Xml;
using System.Collections;


namespace com.ums.PARM.AlertExec
{
    public class ExecResponse
    {

        public ExecResponse()
        {
            arr_temp_alertresults = new ArrayList();
        }

        public ExecResponse parseFromXml(ref XmlDocument doc, String sz_exec_pktype)
        {
            try
            {

                XmlElement results = doc.DocumentElement; //doc.GetElementById("results");
                this.l_execpk = results.GetAttribute(sz_exec_pktype);
                this.sz_function = results.GetAttribute("sz_function");
                XmlNodeList project = results.GetElementsByTagName("project");
                XmlNodeList alerts;
                if (project.Count >= 1)
                {
                    this.l_projectpk = project.Item(0).Attributes["l_projectpk"].Value;
                }
                alerts = results.GetElementsByTagName("alert");
                IEnumerator en = alerts.GetEnumerator();
                while (en.MoveNext())
                {
                    XmlNode node = (XmlNode)en.Current;
                    AlertResultLine line = new AlertResultLine();
                    line.l_alertpk = long.Parse(node.Attributes["l_alertpk"].Value);
                    line.l_refno = int.Parse(node.Attributes["l_refno"].Value);
                    line.sz_result = node.Attributes["result"].Value;
                    line.sz_name = node.Attributes["sz_name"].Value;
                    line.sz_text = node.Attributes["text"].Value;
                    try
                    {
                        line.sz_extended_info = node.FirstChild.Value;
                    }
                    catch (Exception)
                    {

                    }
                    this.AddResult(line);
                }
            }
            catch (Exception e)
            {
                AlertResultLine line = new AlertResultLine();
                line.l_alertpk = -1;
                line.l_refno = -1;
                line.sz_result = "Error";
                line.sz_text = e.Message;
                this.AddResult(line);
            }
            return this;
        }

        public String l_execpk; //either l_alertpk or l_eventpk
        public String sz_function;
        public String l_projectpk;

        //protected AlertResultLine [] arr_alertresults; // = new AlertResultLine();
        public AlertResultLine[] arr_alertresults;//getAlertResults() { return (AlertResultLine [])arr_temp_alertresults.ToArray(); }
        protected ArrayList arr_temp_alertresults;

        public void AddResult(AlertResultLine line)
        {
            arr_temp_alertresults.Add(line);
            arr_alertresults = arr_temp_alertresults.Cast<AlertResultLine>().ToArray();
        }

        public class AlertResultLine
        {
            public Int64 l_alertpk;
            public int l_refno;
            public String sz_name;
            public String sz_result; //true or false
            public int l_result_code;
            public String sz_text;
            public String sz_extended_info;

            public AlertResultLine()
                : base()
            {
            }
        }
    }
}
