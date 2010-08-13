using System;
using System.Collections;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;

using System.Collections.Generic;

using com.ums.ws.pas;
using com.ums.ws.parm.admin;

public partial class area_edit : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        pasws pasws = new pasws();
        com.ums.ws.pas.ULOGONINFO logoninfo = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
        if (logoninfo == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            //UPASLOGON pl = pasws.PasLogon(logoninfo);
            //UDEPARTMENT[] depts = pl.departments;
            ParmAdmin pa = new ParmAdmin();
            PAOBJECT[] obj = pa.GetRegions(Util.convertLogonInfoParmAdmin(logoninfo));

            for (int i = 0; i < obj.Length; ++i)
            {
                
                string jall = "";
                com.ums.ws.parm.admin.UPolygon p = (com.ums.ws.parm.admin.UPolygon)obj[i].m_shape; ;
                for (int k = 0; k < p.polypoint.Length; ++k)
                {
                    jall += p.polypoint[k].lat;
                    if (k + 1 < p.polypoint.Length)
                        jall += "|";
                }

                jall += "¤";

                for (int k = 0; k < p.polypoint.Length; ++k)
                {
                    jall += p.polypoint[k].lon;
                    if (k + 1 < p.polypoint.Length)
                        jall += "|";

                }
                //txt_area.Text += "\n";
                lst_areas.Items.Add(new ListItem(obj[i].sz_name, jall));
            }
        }

        lst_areas.Attributes.Add("onChange", "javascript:setzShitz();");
    }
    protected void btn_save_Click(object sender, EventArgs e)
    {
        String[] l = txt_coor.Text.Split('¤');

        String[] lat = l[0].Split('|');
        String[] lon = l[1].Split('|');

        com.ums.ws.parm.admin.UPolygon p = new com.ums.ws.parm.admin.UPolygon();
        com.ums.ws.parm.admin.UPolypoint[] pp = new com.ums.ws.parm.admin.UPolypoint[lat.Length];

        for (int i = 0; i < lat.Length; ++i)
        {
            pp[i] = new com.ums.ws.parm.admin.UPolypoint();
            lat[i] = lat[i].Replace('.', ',');
            lon[i] = lon[i].Replace('.', ',');
            pp[i].lat = double.Parse(lat[i]);
            pp[i].lon = double.Parse(lon[i]);
        }
        p.polypoint = pp;

        ParmAdmin pa = new ParmAdmin();

        PAOBJECT obj = new PAOBJECT();
        obj.sz_name = txt_name.Text;
        obj.parmop = PARMOPERATION.insert;
        obj.l_deptpk = 1;
        obj.m_shape = p;

        com.ums.ws.pas.ULOGONINFO li = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];

        UPAOBJECTRESULT res = pa.ExecPAShapeUpdate(Util.convertLogonInfoParmAdmin(li),obj,PASHAPETYPES.PADEPARTMENTRESTRICTION);

        if (res != null)
            lst_areas.Items.Add(new ListItem(obj.sz_name, txt_coor.Text));
    }
}
