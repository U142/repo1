using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.HtmlControls;
using System.Configuration;

using centric.com.ums.ws.pas;
using centric.com.ums.ws.parm.admin;
using centric.com.ums.ws.pas.admin;
using System.ServiceModel;

public partial class report_authorizationarea : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        paswsSoapClient pasws = new paswsSoapClient();
        pasws.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["Pas"]);

        centric.com.ums.ws.pas.admin.ULOGONINFO l = (centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
        if (l == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            ParmAdminSoapClient pa = new ParmAdminSoapClient();
            pa.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["ParmAdmin"]);

            centric.com.ums.ws.parm.admin.UDEPARTMENT[] obj = pa.GetRestrictionAreas(Util.convertLogonInfoParmAdmin(l));
            Session["shapes"] = obj;
            IEnumerable<centric.com.ums.ws.parm.admin.UDEPARTMENT> sorter = obj.OrderBy(area => area.sz_deptid);

            foreach (centric.com.ums.ws.parm.admin.UDEPARTMENT dept in sorter)
            {
                centric.com.ums.ws.parm.admin.UShape[] shape = dept.restrictionShapes;
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


        centric.com.ums.ws.parm.admin.UDEPARTMENT[] obj = (centric.com.ums.ws.parm.admin.UDEPARTMENT[])Session["shapes"];

        for (int i = 0; i < lst_areas.GetSelectedIndices().Length; ++i)
        {
            PasAdminSoapClient pasadmin = new PasAdminSoapClient();
            pasadmin.Endpoint.Address = new EndpointAddress(ConfigurationManager.AppSettings["PasAdmin"]);
            IsShapeActiveInPeriodResponse res = pasadmin.doIsShapeActiveInPeriod((centric.com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"], int.Parse(lst_areas.Items[lst_areas.GetSelectedIndices()[i]].Value), createTimestamp());
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

                        centric.com.ums.ws.parm.admin.UPolygon poly;

                        for (int k = 0; k < obj[j].restrictionShapes.Length; ++k)
                        {
                            poly = (centric.com.ums.ws.parm.admin.UPolygon)obj[j].restrictionShapes[k];

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
                        /*
                        string archive = "javaapp/admin.jar, " +
                                        "javaapp/common-2.2.1.jar, javaapp/commons-lang-2.1.jar, javaapp/Core.jar, javaapp/geoapi-2.2.0.jar, javaapp/gt-api-2.5.5.jar, " + 
                                        "javaapp/gt-main-2.5.5.jar, javaapp/gt-metadata-2.5.5.jar, javaapp/gt-referencing-2.5.5.jar, javaapp/gt-shapefile-2.5.5.jar, javaapp/gt-wms-2.5.5.jar, " +
                                        "javaapp/gt-xml-2.5.5.jar, javaapp/Importer.jar, javaapp/jcommon-1.0.16.jar, javaapp/jdom-1.0.jar, javaapp/jfreechart-1.0.13.jar, javaapp/jts-1.9.jar, " +
                                        "javaapp/localization.jar, javaapp/ums-map-2.0-SNAPSHOT.jar, javaapp/no.ums.jar, javaapp/PAS.jar, javaapp/PASIcons.jar, javaapp/plugins.jar, " +
                                        "javaapp/roxes-win32forjava-1.0.2.jar, javaapp/Send.jar, javaapp/substance.3.3.0.1.jar, javaapp/substance.4.3.11.jar, javaapp/substance.5.0.01.jar, " +
                                        "javaapp/substance.jar, javaapp/UMS.jar, pas-app-2.0-SNAPSHOT-maps.jar, pas-admin-ui-2.0-SNAPSHOT.jar";
                        */

                        string archive = "javaapp/annotations-1.0.0.jar," +
                                        " javaapp/centric-admin-2.0-SNAPSHOT.jar," +
                                        " javaapp/commons-beanutils-1.7.0.jar," +
                                        " javaapp/commons-lang-2.3.jar," +
                                        " javaapp/commons-logging-1.0.3.jar," +
                                        " javaapp/commons-pool-1.5.3.jar," +
                                        " javaapp/geoapi-2.3-M1.jar," +
                                        " javaapp/geoapi-pending-2.3-M1.jar," +
                                        " javaapp/gt-api-2.6.5.jar," +
                                        " javaapp/gt-coverage-2.6.5.jar," +
                                        " javaapp/gt-cql-2.6.5.jar," +
                                        " javaapp/gt-main-2.6.5.jar," +
                                        " javaapp/gt-metadata-2.6.5.jar," +
                                        " javaapp/gt-referencing-2.6.5.jar," +
                                        " javaapp/gt-render-2.6.5.jar," +
                                        " javaapp/gt-shapefile-2.6.5.jar," +
                                        " javaapp/gt-wms-2.6.5.jar," +
                                        " javaapp/gt-xml-2.6.5.jar," +
                                        " javaapp/guava-r07.jar," +
                                        " javaapp/hamcrest-core-1.1.jar," +
                                        " javaapp/hamcrest-library-1.1.jar," +
                                        " javaapp/jaxb2-basics-0.6.0.jar," +
                                        " javaapp/jaxb2-basics-runtime-0.6.0.jar," +
                                        " javaapp/jaxb2-basics-tools-0.6.0.jar," +
                                        " javaapp/jcommon-1.0.16.jar," +
                                        " javaapp/jdom-1.0.jar," +
                                        " javaapp/jfreechart-1.0.13.jar," +
                                        " javaapp/jmock-2.5.1.jar," +
                                        " javaapp/jnlp-1.0.jar," +
                                        " javaapp/joda-time-1.6.2.jar," +
                                        " javaapp/jsr-275-1.0-beta-2.jar," +
                                        " javaapp/jsr305-1.3.9.jar," +
                                        " javaapp/jts-1.11.jar," +
                                        " javaapp/NLMapSearch-1.0.jar," +
                                        " javaapp/pas-admin-ui-2.0-SNAPSHOT.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-core.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-gps.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-importer.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-maps.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-parm.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-pas.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-send.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-sound.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-status.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-tas.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT-ums.jar," +
                                        " javaapp/pas-app-2.0-SNAPSHOT.jar," +
                                        " javaapp/pas-cellbroadcast-2.0-SNAPSHOT.jar," +
                                        " javaapp/pas-core-2.0-SNAPSHOT.jar," +
                                        " javaapp/pas-icons-2.0-SNAPSHOT.jar," +
                                        " javaapp/pas-lang-2.0-SNAPSHOT.jar," +
                                        " javaapp/substance-3.3.0.1.jar," +
                                        " javaapp/ums-log-2.0-SNAPSHOT.jar," +
                                        " javaapp/ums-map-2.0-SNAPSHOT.jar," +
                                        " javaapp/vecmath-1.3.2.jar," +
                                        " javaapp/ws-java-clients-1.2-SNAPSHOT.jar";


                        string code = "no/ums/adminui/pas/MapImageDownload.class";

                        string mainclass = "no.ums.adminui.pas.MapImageDownload";

                        txt.Text = "<input type=\"button\" onclick=\"javascript:launchApplet('" +
                                        "<applet name=MapImageDownload" + obj[j].l_deptpk + " id=MapImageDownload" + obj[j].l_deptpk + " width=1 height=1 archive=" + 
                                        getUrlEncodedDoubleQuote() + archive + getUrlEncodedDoubleQuote() +
                                            " code=" + code +
                                            " main-class=" + mainclass + " > " +
                                            "<param name=lat value=" + lat + " >" +
                                            "<param name=lon value=" + lon + " >" +
                                            "<param name=mapinfo value=" + ConfigurationManager.AppSettings["mapinfo"] + " >" +
                                            "<param name=w value=" + ConfigurationManager.AppSettings["w"] + " >" +
                                            "<param name=applet_width value=" + ConfigurationManager.AppSettings["applet_width"] + " >" +
                                            "<param name=applet_height value=" + ConfigurationManager.AppSettings["applet_height"] + " >" +
                                            //"<param name=jnlp_href value=javaapp/report_authorization_area.jnlp>" +
                                       "</applet>" +
                                    "')\" value=\"Save\" />";
                        cell.Controls.Add(txt);
                        row.Cells.Add(cell);
                        if (add)
                            tbl_output.Rows.Add(row);

                    }
                }
            }
        }
    }

    private string getUrlEncodedDoubleQuote()
    {
        return Server.HtmlEncode("\"");
    }

    private void fillDropDown()
    {
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
