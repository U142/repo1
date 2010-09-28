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
using com.ums.ws.pas.admin;
using com.ums.ws.parm.admin;

public partial class user_admin : System.Web.UI.Page
{
    private PAOBJECT[] objects;
    private PAUser pau = null;

    List<com.ums.ws.pas.admin.UBBUSER> users;

    protected Table Table
    {
        get
        {
            return (Table)ViewState["table"];
        }
        set
        {
            ViewState["table"] = value;
        }
    }

    protected void Page_Load(object sender, EventArgs e)
    {
         com.ums.ws.pas.admin.ULOGONINFO li = ( com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
        
        if (li == null)
            Server.Transfer("logon.aspx");

        PasAdmin pa = new PasAdmin();
        pa.Url = ConfigurationSettings.AppSettings["PasAdmin"];
        GetUsersResponse res = pa.doGetUsers(Util.convertLogonInfoPasAdmin(li));
        if (res.successful)
        {
            com.ums.ws.pas.admin.UBBUSER[] ulist = res.user;
            buildTable(ulist);
        }

        if (!IsPostBack)
        {
            rad_regional.Attributes.Add("value", ConfigurationSettings.AppSettings["usertype_regional"]);
            rad_sregional.Attributes.Add("value", ConfigurationSettings.AppSettings["usertype_super_regional"]);
            rad_national.Attributes.Add("value", ConfigurationSettings.AppSettings["usertype_national"]);
            rad_administrator.Attributes.Add("value", ConfigurationSettings.AppSettings["usertype_administrator"]);

            PasAdmin pasadmin = new PasAdmin();
            pa.Url = ConfigurationSettings.AppSettings["PasAdmin"];

            GetRestrictionAreasResponse resp = pasadmin.doGetRestrictionAreas(li, com.ums.ws.pas.admin.PASHAPETYPES.PADEPARTMENTRESTRICTION);
            com.ums.ws.pas.admin.UDEPARTMENT[] departments = resp.restrictions;
            Session["regions"] = departments;
            for (int i = 0; i < departments.Length; ++i)
                if(departments[i].restrictionShapes[0].f_disabled == 0)
                    lst_regions.Items.Add(new ListItem(departments[i].sz_deptid, departments[i].l_deptpk.ToString()));
        }

    }
    private void buildTable(com.ums.ws.pas.admin.UBBUSER[] ulist)
    {
        TableHeaderRow hr = new TableHeaderRow();

        TableHeaderCell hc = new TableHeaderCell();
        hc.HorizontalAlign = HorizontalAlign.Left;
        hc.Text = "Username";
        hr.Cells.Add(hc);

        hc = new TableHeaderCell();
        hc.HorizontalAlign = HorizontalAlign.Left;
        hc.Text = "Full name";
        hr.Cells.Add(hc);

        hc = new TableHeaderCell();
        hc.HorizontalAlign = HorizontalAlign.Left;
        hc.Text = "User type";
        hr.Cells.Add(hc);

        hc = new TableHeaderCell();
        hc.HorizontalAlign = HorizontalAlign.Left;
        hc.Text = "Blocked";
        hr.Cells.Add(hc);
        tbl_users.Rows.Add(hr);

        TableRow tr;
        TableCell tc;

        IEnumerable<com.ums.ws.pas.admin.UBBUSER> sorter = ulist.OrderBy(user => user.f_disabled).ThenBy(user => user.sz_userid);
        Session["users"] = ulist;

        foreach (com.ums.ws.pas.admin.UBBUSER user in sorter)
        {
            tr = new TableRow();
            tc = new TableCell();
            LinkButton lb = new LinkButton();
            lb.Text = user.sz_userid;
            //tc.Text = user.sz_userid;
            lb.CommandArgument = user.l_userpk.ToString();
            lb.CausesValidation = false;
            lb.ID = "lb_view" + user.l_userpk.ToString();
            lb.Click += new EventHandler(this.btn_view_click);
            tc.Controls.Add(lb);
            tr.Cells.Add(tc);

            tc = new TableCell();
            tc.Text = user.sz_name;
            tr.Cells.Add(tc);

            tc = new TableCell();
            tc.Text = Util.userType(user.l_profilepk);
            tr.Cells.Add(tc);

            tc = new TableCell();
            tc.Text = (user.f_disabled == 1 ? "yes" : "no");
            tr.Cells.Add(tc);
            /*
            tc = new TableCell();
            Button btn_view = new Button();
            btn_view.CommandArgument = user.l_userpk.ToString();
            btn_view.ID = "btn_view" + user.l_userpk.ToString();
            btn_view.CausesValidation = false;
            btn_view.Text = "View";
            btn_view.Click += new EventHandler(this.btn_view_click);
            tc.Controls.Add(btn_view);
            tr.Cells.Add(tc);
            */

            tbl_users.Rows.Add(tr);
            //lst_users.Items.Add(new ListItem(user.sz_userid + "\t" + user.sz_name + "\t" + user.l_profilepk + "\t" + (user.f_disabled == 1 ? "yes" : "no"), user.l_userpk.ToString()));
        }
    }
    protected void btn_view_click(object sender, EventArgs e)
    {
        LinkButton btn_test = (LinkButton)sender;

        com.ums.ws.pas.admin.UBBUSER user = getSelectedUser(long.Parse(btn_test.CommandArgument));
        if (user != null)
        {
            deselect();
            txt_firstname.Text = user.sz_name;
            txt_username.Text = user.sz_userid;
            txt_organization.Text = user.sz_organization;
            selectType(user);
            chk_blocked.Checked = user.f_disabled == 1 ? true : false;
            if (user.f_disabled == 1)
            {
                if (user.l_disabled_reasoncode == com.ums.ws.pas.admin.BBUSER_BLOCK_REASONS.REACHED_RETRY_LIMIT)
                    RequiredFieldValidator2.Enabled = true;
                else
                    RequiredFieldValidator2.Enabled = false;
                txt_blocked.Text = Util.convertDate(user.l_disabled_timestamp).Substring(0, 10);
            }
            else
            {
                txt_blocked.Text = "";
                RequiredFieldValidator2.Enabled = false;
            }
            //lst_regions.SelectedValue = user.l_deptpk.ToString();
            for (int i = 0; i < lst_regions.Items.Count; ++i)
            {
                for (int j = 0; j < user.l_deptpklist.Length; ++j)
                {
                    if (lst_regions.Items[i].Value == user.l_deptpklist[j].ToString())
                        lst_regions.Items[i].Selected = true;
                }
            }
            selected.Text = user.l_userpk.ToString();
            
        }
        
    }
    protected void btn_save_Click(object sender, EventArgs e)
    {
        com.ums.ws.pas.admin.UBBUSER user;
        bool update = false;
        
        if (selected.Text.Length > 0)
        {
            user = getSelectedUser(int.Parse(selected.Text));
            update = true;
        }
        else
            user = getSelectedUser(0);

        if (user == null) // Not selected
            user = new com.ums.ws.pas.admin.UBBUSER();

        user.sz_name = txt_firstname.Text;
        user.sz_userid = txt_username.Text.ToUpper();
        user.sz_organization = txt_organization.Text;
        if (txt_password.Text.Length > 0)
        {
            user.sz_paspassword = txt_password.Text;
            user.sz_hash_paspwd = Helper.CreateSHA512Hash(txt_password.Text);
        }
        else
            user.sz_paspassword = "";

        // Organization?


        if (chk_blocked.Checked)
        {
            // Blocked dato?
            user.f_disabled = 1;
            if (txt_blocked_changed.Text.Equals("true"))
                user.l_disabled_reasoncode = com.ums.ws.pas.admin.BBUSER_BLOCK_REASONS.BLOCKED_BY_ADMIN;
            txt_blocked_changed.Text = "";
        }
        else
        {
            txt_blocked_changed.Text = "";
            user.f_disabled = 0;
        }

        if (rad_administrator.Checked)
        {
            // No regions allowed
            user.l_profilepk = int.Parse(ConfigurationSettings.AppSettings["admin_department"]);
        }
        else if (rad_national.Checked)
        {
            // All regions?
            user.l_profilepk = int.Parse(ConfigurationSettings.AppSettings["usertype_national"]);
        }
        else if (rad_sregional.Checked)
            user.l_profilepk = int.Parse(ConfigurationSettings.AppSettings["usertype_super_regional"]);
        else if (rad_regional.Checked)
        {
            // Only on region
            user.l_profilepk = int.Parse(ConfigurationSettings.AppSettings["usertype_regional"]);
            user.l_deptpk = int.Parse(lst_regions.SelectedValue);
        }

        com.ums.ws.pas.admin.UBBUSER[] users = (com.ums.ws.pas.admin.UBBUSER[])Session["users"];
        if (users == null)
            users = new com.ums.ws.pas.admin.UBBUSER[1];
        
        // Send med UBBUSER og restriction area kan bare sette departmentpk på bbuser forresten?
        com.ums.ws.pas.admin.ULOGONINFO li = ( com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
        PasAdmin pasadmin = new PasAdmin();
        pasadmin.Url = ConfigurationSettings.AppSettings["PasAdmin"];

        int[] regions = lst_regions.GetSelectedIndices();
        int[] regionpk = new int[regions.Length];

         StoreUserResponse res;
         if (user.l_profilepk == int.Parse(ConfigurationSettings.AppSettings["usertype_regional"])) // regional
         {
             res = pasadmin.doStoreUser(Util.convertLogonInfoPasAdmin(li), user, new int[] { user.l_deptpk });
         }
         else if (user.l_profilepk == int.Parse(ConfigurationSettings.AppSettings["usertype_super_regional"]) || user.l_profilepk == int.Parse(ConfigurationSettings.AppSettings["usertype_national"])) // super regional and national
         {
             for (int i = 0; i < regions.Length; ++i)
             {
                 regionpk[i] = int.Parse(lst_regions.Items[regions[i]].Value);
             }
             user.l_deptpk = regionpk[0];
             res = pasadmin.doStoreUser(Util.convertLogonInfoPasAdmin(li), user, regionpk);
         }
         else
         {
             user.l_deptpk = int.Parse(ConfigurationSettings.AppSettings["admin_department"]);
             //user.l_deptpk = 100000; // dummy department
             res = pasadmin.doStoreUser(Util.convertLogonInfoPasAdmin(li), user, new int[] { user.l_deptpk });
         }
            if (res.successful)
            {
                lbl_feedback.Text = "";

                user = res.user;
                if (update)
                {
                    for (int i = 0; i < users.Length; ++i)
                    {
                        if (users[i].l_userpk == user.l_userpk)
                            users[i] = user;
                    }
                    tbl_users.Rows.Clear();
                    Session["users"] = users;
                    buildTable(users);                    
                }
                else
                {
                    users[users.Length - 1] = user;
                    Session["users"] = users;
                    if (lst_users.SelectedIndex == -1)
                        lst_users.Items.Add(new ListItem(user.sz_userid + "\t" + user.sz_name + "\t" + user.l_profilepk + "\t" + (user.f_disabled == 1 ? "yes" : "no"), user.l_userpk.ToString()));
                    else
                    {
                        int i = 0;
                        i = lst_users.SelectedIndex;
                        lst_users.Items.Remove(lst_users.SelectedItem);
                        lst_users.Items.Insert(i, new ListItem(user.sz_userid + "\t" + user.sz_name + "\t" + user.l_profilepk + "\t" + (user.f_disabled == 1 ? "yes" : "no"), user.l_userpk.ToString()));
                    }
                    tbl_users.Rows.Clear();
                    buildTable(users);
                }
                deselect();
            }
            else
            {
                lbl_feedback.Text = res.reason;
                lbl_feedback.ForeColor = System.Drawing.Color.Red;
            }
            
        reset();
        resetRegions();
        RequiredFieldValidator2.Enabled = true;
        // user stored
        
    }

    protected void btn_create_Click(object sender, EventArgs e)
    {
        RequiredFieldValidator2.Enabled = true;
        reset();
    }

    private void reset()
    {
        txt_firstname.Text = "";
        txt_username.Text = "";
        txt_password.Text = "";
        txt_organization.Text = "";

        chk_blocked.Checked = false;
        txt_blocked.Text = "";

        rad_administrator.Checked = false;
        rad_national.Checked = false;
        rad_regional.Checked = true;
        rad_sregional.Checked = false;

        lst_regions.SelectedIndex = -1;
        lst_users.SelectedIndex = -1;
        selected.Text = "";
    }

    private void deselect()
    {
        lst_regions.SelectedIndex = -1;
    }

    private void selectAllRegions()
    {
        for (int i = 0; i < lst_regions.Items.Count; ++i)
            lst_regions.Items[i].Selected = true;
    }

    private com.ums.ws.pas.admin.UBBUSER getSelectedUser()
    {
        com.ums.ws.pas.admin.UBBUSER[] users = (com.ums.ws.pas.admin.UBBUSER[])Session["users"];
        if (users != null && lst_users.SelectedIndex != -1)
        {
            for (int i = 0; i < users.Length; ++i)
            {
                if (users[i].l_userpk == long.Parse(lst_users.SelectedValue))
                    return users[i];
            }
        }

        return null;
    }

    private com.ums.ws.pas.admin.UBBUSER getSelectedUser(long id)
    {
        com.ums.ws.pas.admin.UBBUSER[] users = (com.ums.ws.pas.admin.UBBUSER[])Session["users"];

        for (int i = 0; i < users.Length; ++i)
        {
            if (users[i].l_userpk == id)
                return users[i];
        }
        return null;
    }

    protected void fill_form(object sender, EventArgs e)
    {
        com.ums.ws.pas.admin.UBBUSER user = getSelectedUser();
        
        if (user != null)
        {
            deselect();
            txt_firstname.Text = user.sz_name;
            txt_username.Text = user.sz_userid;
            txt_organization.Text = user.sz_organization;
            selectType(user);
            chk_blocked.Checked = user.f_disabled==1?true:false;
            if (user.f_disabled == 1)
                txt_blocked.Text = Util.convertDate(user.l_disabled_timestamp).Substring(0, 10);
            else
                txt_blocked.Text = "";
            //lst_regions.SelectedValue = user.l_deptpk.ToString();
            for(int i=0;i<lst_regions.Items.Count;++i)
            {
                for (int j = 0; j < user.l_deptpklist.Length; ++j)
                {
                    if (lst_regions.Items[i].Value == user.l_deptpklist[j].ToString())
                        lst_regions.Items[i].Selected = true;
                }
            }
            selected.Text = user.l_userpk.ToString();
            
        }
    }

    private void selectType(com.ums.ws.pas.admin.UBBUSER user)
    {
        rad_regional.Checked = false;
        rad_sregional.Checked = false;
        rad_national.Checked = false;
        rad_administrator.Checked = false;

        if (long.Parse(ConfigurationSettings.AppSettings["usertype_national"]) == user.l_profilepk)
        {
            rad_national.Checked = true;
            req_regions.Enabled = true;
        }
        else if (long.Parse(ConfigurationSettings.AppSettings["usertype_super_regional"]) == user.l_profilepk)
        {
            rad_sregional.Checked = true;
            req_regions.Enabled = true;
        }
        else if (long.Parse(ConfigurationSettings.AppSettings["usertype_regional"]) == user.l_profilepk)
        {
            rad_regional.Checked = true;
            req_regions.Enabled = true; 
        }
        else if (long.Parse(ConfigurationSettings.AppSettings["usertype_administrator"]) == user.l_profilepk)
        {
            rad_administrator.Checked = true;
        }

        admin_Checked(this, null);

        
    }
    protected void region_select(object sender, EventArgs e)
    {
        if (Session["sregion"] != "true")
        {
            com.ums.ws.pas.admin.ULOGONINFO li = (com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];

            PasAdmin pa = new PasAdmin();
            pa.Url = ConfigurationSettings.AppSettings["PasAdmin"];
            int[] indices = lst_regions.GetSelectedIndices();
            com.ums.ws.pas.admin.UDEPARTMENT dept = null;
            com.ums.ws.pas.admin.UPolygon poly = null;
            List<com.ums.ws.pas.admin.UPolygon> comparepoly = new List<com.ums.ws.pas.admin.UPolygon>();
            com.ums.ws.pas.admin.UDEPARTMENT[] depts = (com.ums.ws.pas.admin.UDEPARTMENT[])Session["regions"];

            foreach (com.ums.ws.pas.admin.UDEPARTMENT d in depts)
                if (d.l_deptpk == int.Parse(lst_regions.SelectedValue))
                    dept = d;
            /*
            foreach (int ind in indices)
            {
                // Selected restriction area
                dept = depts[int.Parse(lst_regions.Items[ind].Value)];
                foreach (com.ums.ws.pas.admin.UShape shape in dept.restrictionShapes)
                {
                    poly = (com.ums.ws.pas.admin.UPolygon)shape;
                }
            }

            foreach (com.ums.ws.pas.admin.UDEPARTMENT comparedept in depts)
            {
                foreach (com.ums.ws.pas.admin.UShape shape in comparedept.restrictionShapes)
                {
                    comparepoly.Add((com.ums.ws.pas.admin.UPolygon)shape);
                }
            }*/

            FindPolysWithSharedBorderResponse res = pa.doFindPolysWithSharedBorder(li, dept, depts);
            if (res.successful)
            {
                lst_regions.Items.Clear();
                foreach (com.ums.ws.pas.admin.UDEPARTMENT region in res.deptlist)
                {
                    if(region.restrictionShapes[0].f_disabled == 0)
                        lst_regions.Items.Add(new ListItem(region.sz_deptid, region.l_deptpk.ToString()));
                }
                if(dept.restrictionShapes[0].f_disabled == 0)
                    lst_regions.SelectedValue = dept.l_deptpk.ToString();
            }
            Session["sregion"] = "true";
        }
    }

    protected void admin_Checked(object sender, EventArgs e)
    {
        if (rad_administrator.Checked)
        {
            resetRegions();
            lst_regions.SelectionMode = ListSelectionMode.Multiple;
            selectAllRegions();
            req_regions.Enabled = false;
            lst_regions.Enabled = false;
        }
        else if (rad_national.Checked)
        {
            resetRegions();
            lst_regions.Enabled = false;
            req_regions.Enabled = true;
            lst_regions.SelectionMode = ListSelectionMode.Multiple;
            selectAllRegions();
        }
        else if (rad_regional.Checked)
        {
            resetRegions();
            deselect();
            lst_regions.Enabled = true;
            req_regions.Enabled = true;
            lst_regions.SelectionMode = ListSelectionMode.Single;
        }
        else if (rad_sregional.Checked)
        {
            resetRegions();
            lst_regions.Enabled = true;
            lst_regions.SelectionMode = ListSelectionMode.Multiple;
        }
    }

    private void resetRegions()
    {
        lst_regions.Items.Clear();
        com.ums.ws.pas.admin.UDEPARTMENT[] depts = (com.ums.ws.pas.admin.UDEPARTMENT[])Session["regions"];
        foreach (com.ums.ws.pas.admin.UDEPARTMENT region in depts)
        {
            if (region.restrictionShapes[0].f_disabled == 0)
                lst_regions.Items.Add(new ListItem(region.sz_deptid, region.l_deptpk.ToString()));
        }
    }

    protected void reload_regions_click(object sender, EventArgs e)
    {
        Session.Remove("sregion");
        resetRegions();
    }

    protected void chk_blocked_Change(object sender, EventArgs e)
    {
        txt_blocked_changed.Text = "true";
    }
}
