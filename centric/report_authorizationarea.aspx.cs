using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.HtmlControls;

using com.ums.ws.pas;
using com.ums.ws.parm.admin;

public partial class report_authorizationarea : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        pasws pasws = new pasws();
        com.ums.ws.pas.ULOGONINFO l = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
        if (l == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            ParmAdmin pa = new ParmAdmin();
            com.ums.ws.parm.admin.UDEPARTMENT[] obj = pa.GetRestrictionAreas(Util.convertLogonInfoParmAdmin(l));
            Session["shapes"] = obj;
            for (int i = 0; i < obj.Length; ++i)
            {
                com.ums.ws.parm.admin.UShape[] shape = obj[i].restrictionShapes;
                lst_areas.Items.Add(new ListItem(obj[i].sz_deptid, obj[i].l_deptpk.ToString()));   
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
            for(int j=0; j< obj.Length;++j) {
                if (int.Parse(lst_areas.Items[lst_areas.GetSelectedIndices()[i]].Value) == obj[j].l_deptpk)
                {
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

                    txt.Text = "<applet name='MapImageDownload" + obj[j].l_deptpk + "' id='MapImageDownload" + obj[j].l_deptpk + "' width='80' height='30'> " +
                                    "<param name=lat value='" + lat + "' >" +
                                    "<param name=lon value='" + lon + "' >" +
                                    "<param name=jnlp_href value='javaapp/report_authorization_area.jnlp'>" +
                               "</applet>";
                    cell.Controls.Add(txt);
                    row.Cells.Add(cell);
                    tbl_output.Rows.Add(row);

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
}
