using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.HtmlControls;
using System.Configuration;

using com.ums.ws.pas;
using com.ums.ws.parm.admin;
using com.ums.ws.pas.admin;
using System.ServiceModel;

public partial class report_authorizationarea : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        paswsSoapClient pasws = new paswsSoapClient();
        pasws.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["Pas"]);

        com.ums.ws.pas.admin.ULOGONINFO l = (com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
        if (l == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            ParmAdmin pa = new ParmAdmin();
            pa.Url = ConfigurationManager.AppSettings["ParmAdmin"];

            com.ums.ws.parm.admin.UDEPARTMENT[] obj = pa.GetRestrictionAreas(Util.convertLogonInfoParmAdmin(l));
            Session["shapes"] = obj;
            IEnumerable<com.ums.ws.parm.admin.UDEPARTMENT> sorter = obj.OrderBy(area => area.sz_deptid);

            foreach (com.ums.ws.parm.admin.UDEPARTMENT dept in sorter)
            {
                com.ums.ws.parm.admin.UShape[] shape = dept.restrictionShapes;
                lst_areas.Items.Add(new ListItem(dept.sz_deptid, dept.l_deptpk.ToString()));   
            }
            fillDropDown();
        }
    }
    protected void btn_showClick(object sender, EventArgs e)
    {

        tbl_output.Rows.Clear();

        HtmlTableRow header = new HtmlTableRow();
        HtmlTableCell hc = new HtmlTableCell();
        Label lbl_header = new Label();
        lbl_header.Text = "Authorization area name:";
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        lbl_header = new Label();
        lbl_header.Text = "";
        hc = new HtmlTableCell();
        hc.Controls.Add(lbl_header);
        header.Cells.Add(hc);

        tbl_output.Rows.Add(header);


        com.ums.ws.parm.admin.UDEPARTMENT[] obj = (com.ums.ws.parm.admin.UDEPARTMENT[]) Session["shapes"];

        for (int i = 0; i < lst_areas.GetSelectedIndices().Length; ++i)
        {
            PasAdminSoapClient pasadmin = new PasAdminSoapClient();
            pasadmin.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["PasAdmin"]);
            IsShapeActiveInPeriodResponse res = pasadmin.doIsShapeActiveInPeriod((com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"], int.Parse(lst_areas.Items[lst_areas.GetSelectedIndices()[i]].Value), createTimestamp());
            if (res.successful)
            {
                for(int j=0; j< obj.Length;++j) {
                
                    if (int.Parse(lst_areas.Items[lst_areas.GetSelectedIndices()[i]].Value) == obj[j].l_deptpk)
                    {
                        bool add = true;
                        HtmlTableRow row = new HtmlTableRow();
                        Label txt = new Label();
                        txt.Text = obj[j].sz_deptid;
                        HtmlTableCell cell = new HtmlTableCell();
                        cell.Controls.Add(txt);
                        row.Cells.Add(cell);

                        cell = new HtmlTableCell();
                        txt = new Label();

                        String lat = "";
                        String lon = "";

                        com.ums.ws.parm.admin.UPolygon poly;

                        for (int k = 0; k < obj[j].restrictionShapes.Length; ++k)
                        {
                            poly = (com.ums.ws.parm.admin.UPolygon)obj[j].restrictionShapes[k];

                            if (poly.f_disabled == 1 && poly.l_disabled_timestamp < createTimestamp() + 100000000)
                                add = false;

                            for (int l = 0; l < poly.polypoint.Length; ++l)
                            {
                                lat += poly.polypoint[l].lat;
                                if (l + 1 < poly.polypoint.Length)
                                    lat += "|";
                                lon += poly.polypoint[l].lon;
                                if (l + 1 < poly.polypoint.Length)
                                    lon += "|";
                            }
                        }
                        txt.Text = "<input type=\"button\" onclick=\"javascript:launchApplet('" +
                                        "<applet name=MapImageDownload" + obj[j].l_deptpk + " id=MapImageDownload" + obj[j].l_deptpk + " width=1 height=1> " +
                                            "<param name=lat value=" + lat + " >" +
                                            "<param name=lon value=" + lon + " >" +
                                            "<param name=mapinfo value=" + ConfigurationManager.AppSettings["mapinfo"] + " >" +
                                            "<param name=w value=" + ConfigurationManager.AppSettings["w"] + " >" +
                                            "<param name=applet_width value=" + ConfigurationManager.AppSettings["applet_width"] + " >" +
                                            "<param name=applet_height value=" + ConfigurationManager.AppSettings["applet_height"] + " >" +
                                            "<param name=jnlp_href value=javaapp/report_authorization_area.jnlp>" +
                                       "</applet>" +
                                    "')\" value=\"Save\" />";
                        /*txt.Text = "<applet name='MapImageDownload" + obj[j].l_deptpk + "' id='MapImageDownload" + obj[j].l_deptpk + "' width='80' height='30'> " +
                                        "<param name=lat value='" + lat + "' >" +
                                        "<param name=lon value='" + lon + "' >" +
                                        "<param name=jnlp_href value='javaapp/report_authorization_area.jnlp'>" +
                                   "</applet>";*/
                        cell.Controls.Add(txt);
                        row.Cells.Add(cell);
                        if (add)
                            tbl_output.Rows.Add(row);

                    }
                }
            }
        }
    }

    private void fillDropDown()
    {
        /*
        System.Globalization.CultureInfo CI = new System.Globalization.CultureInfo("en-us", true);
        System.Threading.Thread.CurrentThread.CurrentUICulture = CI;
        DateTime month = Convert.ToDateTime("1/1/2000");
        
        for (int i = 0; i < 12; i++)
        {
            DateTime NextMont = month.AddMonths(i);
            ListItem list = new ListItem();
            list.Text = NextMont.ToString("MMMM");
            list.Value = NextMont.Month.ToString().PadLeft(2,'0');
            ddl_month.Items.Add(list);
        }
        */
        DateTime now = DateTime.Now;
        for (int i = 2010; i <= now.Year; i++)
        {
            ListItem list = new ListItem();
            list.Text = i.ToString();
            list.Value = i.ToString();
            ddl_year.Items.Add(list);
        }
    }
    
    private long createTimestamp()
    {
        String timestamp = "";
        timestamp = ddl_year.SelectedValue + ddl_month.SelectedValue;
        return long.Parse(timestamp.PadRight(14, '0'));
    }
}
