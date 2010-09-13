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

using com.ums.ws.pas.admin;
using com.ums.ws.parm.admin;

public partial class area_edit : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        
        com.ums.ws.pas.admin.ULOGONINFO logoninfo = (com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
        if (logoninfo == null)
            Server.Transfer("logon.aspx");

        userid.Attributes.Add("value", logoninfo.l_userpk.ToString());
        compid.Attributes.Add("value", logoninfo.l_comppk.ToString());
        deptid.Attributes.Add("value", logoninfo.l_deptpk.ToString());
        password.Attributes.Add("value", logoninfo.sz_password);
        session.Attributes.Add("value", logoninfo.sessionid);

        //Master.BodyTag.Attributes.Add("onbeforeunload", "setUnlock('page=area_edit')");

        lbl_error.ForeColor = System.Drawing.Color.Red;

        if (!IsPostBack)
        {
            PasAdmin pa = new PasAdmin();
            
            GetRestrictionAreasResponse res = pa.doGetRestrictionAreas(logoninfo, com.ums.ws.pas.admin.PASHAPETYPES.PADEPARTMENTRESTRICTION);
            com.ums.ws.pas.admin.UDEPARTMENT[] obj = res.restrictions;
            Session["restrictions"] = obj;
            buildTable(obj);
        }
        else
        {
            com.ums.ws.pas.admin.UDEPARTMENT[] obj = (com.ums.ws.pas.admin.UDEPARTMENT[])Session["restrictions"];
            buildTable(obj);
        }
            //UPASLOGON pl = pasws.PasLogon(logoninfo);
            //UDEPARTMENT[] depts = pl.departments;
           
/*
            for (int i = 0; i < obj.Length; ++i)
            {
                string jall = "";
                com.ums.ws.parm.admin.UShape[] shape = obj[i].restrictionShapes;

                for (int j = 0; j < shape.Length; ++j)
                {
                    com.ums.ws.parm.admin.UPolygon p = (com.ums.ws.parm.admin.UPolygon)shape[j];
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
                    lst_areas.Items.Add(new ListItem(obj[i].sz_deptid, jall));
                }
            }
        }

        lst_areas.Attributes.Add("onChange", "javascript:setShape();");
 * */
    }

    private void buildTable(com.ums.ws.pas.admin.UDEPARTMENT[] obj)
    {
        tbl_areas.Rows.Clear();

        TableHeaderRow hr = new TableHeaderRow();

        TableHeaderCell hc = new TableHeaderCell();
        hc.HorizontalAlign = HorizontalAlign.Left;
        hc.Text = "Authorization Area name";
        hr.Cells.Add(hc);

        hc = new TableHeaderCell();
        hc.HorizontalAlign = HorizontalAlign.Left;
        hc.Text = "Obsolete";
        hr.Cells.Add(hc);

        tbl_areas.Rows.Add(hr);

        TableRow tr;
        TableCell tc;
        for (int i = 0; i < obj.Length; ++i)
        {
            string jall = "";
            com.ums.ws.pas.admin.UShape[] shape = obj[i].restrictionShapes;

            for (int j = 0; j < shape.Length; ++j)
            {
                com.ums.ws.pas.admin.UPolygon p = (com.ums.ws.pas.admin.UPolygon)shape[j];
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
                tr = new TableRow();
                tc = new TableCell();
                HyperLink lb = new HyperLink();
                lb.Text = obj[i].sz_deptid;
                //tc.Text = ulist[i].sz_userid;
                //lb.CommandArgument = obj[i].l_deptpk.ToString();
                //lb.OnClientClick = "";
                //lb.Attributes.Add("onClick", "javascript:setShape(" + jall + ");return false;");
                lb.NavigateUrl = "javascript:setShape('" + "ctl00_tb_" + obj[i].l_deptpk.ToString() + "'," + obj[i].l_deptpk.ToString() + ", " + obj[i].restrictionShapes[0].f_disabled + ", '" + ((Util.convertDate(obj[i].restrictionShapes[0].l_disabled_timestamp)).Length > 10 ? (Util.convertDate(obj[i].restrictionShapes[0].l_disabled_timestamp)).Substring(0, 10) : Util.convertDate(obj[i].restrictionShapes[0].l_disabled_timestamp)) + "', '" + obj[i].sz_deptid + "')";
                //lb.CausesValidation = false;
                lb.ID = "lb_view" + obj[i].l_deptpk.ToString();
                //lb.Click += new EventHandler(this.btn_view_click);
                //lb.Attributes.Add("onclick", "javascript:setShape(" + jall + ");");
                tc.Controls.Add(lb);
                TextBox tb = new TextBox();
                //tb.Attributes.Add("runat", "server");
                tb.ID = "tb_" + obj[i].l_deptpk.ToString();
                tb.Style.Add("visibility", "hidden");
                tb.Text = jall;
                Form.Controls.Add(tb);
                tr.Cells.Add(tc);


                tc = new TableCell();
                tc.Text = obj[i].restrictionShapes[j].f_disabled==1?"Yes":"No";
                tr.Cells.Add(tc);

                tbl_areas.Rows.Add(tr);
                //lst_areas.Items.Add(new ListItem(obj[i].sz_deptid, jall));
            }


            //lst_users.Items.Add(new ListItem(ulist[i].sz_userid + "\t" + ulist[i].sz_name + "\t" + ulist[i].l_profilepk + "\t" + (ulist[i].f_disabled == 1 ? "yes" : "no"), ulist[i].l_userpk.ToString()));
        }
    }

    protected void btn_save_Click(object sender, EventArgs e)
    {
        com.ums.ws.pas.admin.ULOGONINFO li = ( com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];

        if (txt_id.Text.Length > 0)
        {
            // Only allow update of obsolete
            com.ums.ws.pas.admin.UDEPARTMENT[] obj = (com.ums.ws.pas.admin.UDEPARTMENT[])Session["restrictions"];
            com.ums.ws.pas.admin.PasAdmin pa = new com.ums.ws.pas.admin.PasAdmin();
            SetPAShapeObsoleteResponse res;
            for (int i = 0; i < obj.Length; ++i)
            {
                if (obj[i].l_deptpk == int.Parse(txt_id.Text) && chk_obsolete.Checked)
                {
                    obj[i].restrictionShapes[0].f_disabled = 1;
                    res = pa.doSetPAShapeObsolete(li, obj[i], obj[i].restrictionShapes[0]);
                    if (res.successful)
                    {
                        if (res.shape.f_disabled == 1)
                        {
                            chk_obsolete.Enabled = false;
                            txt_obsolete.Text = Util.convertDate(res.shape.l_disabled_timestamp).Substring(0, 10);
                            txt_name.Text = obj[i].sz_deptid;
                            obj[i].restrictionShapes[0] = res.shape;
                            txt_name.Enabled = false;
                            chk_obsolete.Enabled = false;
                        }
                        else
                        {
                            chk_obsolete.Enabled = true;
                            chk_obsolete.Checked = false;
                            txt_obsolete.Text = "";
                            obj[i].restrictionShapes[0] = res.shape;
                            txt_name.Text = obj[i].sz_deptid;
                            txt_name.Enabled = false;
                        }
                        lbl_error.Text = "";
                    }
                    else
                    {
                        lbl_error.Text = res.reason;
                        obj[i].restrictionShapes[0].f_disabled = 0;
                    }
                }
            }
            tbl_areas.Rows.Clear();
            buildTable(obj);

        }
        else
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

            obj.m_shape = p;


            obj.l_deptpk = li.l_deptpk;

            UPAOBJECTRESULT res = pa.ExecPAShapeUpdate(Util.convertLogonInfoParmAdmin(li), obj, com.ums.ws.parm.admin.PASHAPETYPES.PADEPARTMENTRESTRICTION);

            if (res != null)
            {
                com.ums.ws.pas.admin.UDEPARTMENT[] deptlist = (com.ums.ws.pas.admin.UDEPARTMENT[])Session["restrictions"];
                //lst_areas.Items.Add(new ListItem(obj.sz_name, txt_coor.Text));
                PasAdmin pasa = new PasAdmin();
                GetSingleRestricionResponse response = pasa.doGetSingleRestricion(li, res.pk);
                if (response.successful)
                {
                    com.ums.ws.pas.admin.UDEPARTMENT[] tmp = new com.ums.ws.pas.admin.UDEPARTMENT[deptlist.Length + 1];
                    deptlist.CopyTo(tmp, 0);
                    tmp[tmp.Length - 1] = response.restriction;
                    Session["restrictions"] = tmp;
                    buildTable(tmp);

                    txt_name.Text = "";
                    txt_name.Enabled = false;
                    btn_draw.Disabled = true;
                }
            }
            else
            {
                lbl_error.ForeColor = System.Drawing.Color.Red;
                lbl_error.Text = "Error saving shape";
            }
            CheckAccessResponse resa = Util.setOccupied((com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"], ACCESSPAGE.RESTRICTIONAREA, false);
            if (!resa.successful)
            {
                lbl_error.Text = "Successfully stored, but could not unlock database, access is still restricted to current user";
            }
        }
       
        
    }
    protected void btn_create_click(object sender, EventArgs e)
    {
        CheckAccessResponse res = Util.setOccupied((com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"], ACCESSPAGE.RESTRICTIONAREA, true);
        if (res.successful && res.granted)
        {
            txt_name.Text = "";
            txt_name.Enabled = true;
            txt_obsolete.Text = "";
            txt_obsolete_holder.Text = "";
            txt_timestamp.Text = "";
            txt_id.Text = "";
            chk_obsolete.Checked = false;
            btn_draw.Disabled = false;
            chk_obsolete.Enabled = true;
            //tbl_areas.Rows.Clear();
        }
        else
            lbl_error.Text = res.reason;
    }
}
