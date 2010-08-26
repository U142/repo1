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

    protected void Page_Load(object sender, EventArgs e)
    {
        com.ums.ws.pas.ULOGONINFO li = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
        
        if (li == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            PasAdmin pa = new PasAdmin();
            ParmAdmin parma = new ParmAdmin();

            GetUsersResponse res = pa.doGetUsers(Util.convertLogonInfoPasAdmin(li));
            if (res.successful)
            {
                com.ums.ws.pas.admin.UBBUSER[] ulist = res.user;
                Session["users"] = ulist;
                for (int i = 0; i < ulist.Length; ++i)
                    lst_users.Items.Add(new ListItem(ulist[i].sz_userid + "\t" + ulist[i].sz_name + "\t" + ulist[i].l_profilepk + "\t" + (ulist[i].f_disabled == 1 ? "yes" : "no"), ulist[i].l_userpk.ToString()));

                com.ums.ws.parm.admin.UDEPARTMENT[] departments = parma.GetRestrictionAreas(Util.convertLogonInfoParmAdmin(li));
                Session["regions"] = departments;
                for (int i = 0; i < departments.Length; ++i)
                    lst_regions.Items.Add(new ListItem(departments[i].sz_deptid, departments[i].l_deptpk.ToString()));
            }
        }
    }

    protected void btn_save_Click(object sender, EventArgs e)
    {

        com.ums.ws.pas.admin.UBBUSER user = getSelectedUser();

        // sjekk om l_userpk finnes hvis ikke lag nytt objekt
        // Oprett user sett verdier for 

        if (user == null) // Not selected
            user = new com.ums.ws.pas.admin.UBBUSER();

        /*
        if (chk_blocked.Checked)
            DateTime.Parse(txt_blocked.Text);
        */

        user.sz_name = txt_firstname.Text;
        user.sz_userid = txt_username.Text.ToUpper();
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
        }
        else
        {
            user.f_disabled = 0;
        }

        if (rad_administrator.Checked)
        {
            // No regions allowed
            user.l_profilepk = 7;
        }
        else if (rad_national.Checked)
        {
            // All regions?
            user.l_profilepk = 5;
        }
        else if (rad_sregional.Checked)
            user.l_profilepk = 3;
        else if (rad_regional.Checked)
        {
            // Only on region
            user.l_profilepk = 2;
            user.l_deptpk = int.Parse(lst_regions.SelectedValue);
        }

        com.ums.ws.pas.admin.UBBUSER[] users = (com.ums.ws.pas.admin.UBBUSER[])Session["users"];
        if (users == null)
            users = new com.ums.ws.pas.admin.UBBUSER[1];
        
        // Send med UBBUSER og restriction area kan bare sette departmentpk på bbuser forresten?
        com.ums.ws.pas.ULOGONINFO li = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
        PasAdmin pasadmin = new PasAdmin();

        int[] regions = lst_regions.GetSelectedIndices();
        int[] regionpk = new int[regions.Length];
        
        for(int i=0;i<regions.Length;++i) {
            regionpk[i] = int.Parse(lst_regions.Items[regions[i]].Value);
        }

        StoreUserResponse res = pasadmin.doStoreUser(Util.convertLogonInfoPasAdmin(li), user, regionpk);

        if (res.successful)
        {
            user = res.user;
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
            deselect();
        }
        else
        {
            lbl_feedback.Text = res.reason;
            lbl_feedback.ForeColor = System.Drawing.Color.Red;
        }
            
        reset();
        // user stored
        
    }

    protected void btn_create_Click(object sender, EventArgs e)
    {
        reset();
    }

    private void reset()
    {
        txt_firstname.Text = "";
        txt_username.Text = "";
        txt_password.Text = "";

        chk_blocked.Checked = false;
        txt_blocked.Text = "";

        rad_administrator.Checked = false;
        rad_national.Checked = false;
        rad_regional.Checked = false;
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

    protected void fill_form(object sender, EventArgs e)
    {
        com.ums.ws.pas.admin.UBBUSER user = getSelectedUser();
        
        if (user != null)
        {
            deselect();
            txt_firstname.Text = user.sz_name;
            txt_username.Text = user.sz_userid;
            selectType(user);
            chk_blocked.Checked = user.f_disabled==1?true:false;
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

        switch (user.l_profilepk)
        {
            case 2: // regional
                rad_regional.Checked = true;
                req_regions.Enabled = true;
                break;
            case 3:
                rad_sregional.Checked = true;                
                req_regions.Enabled = true;
                break;
            case 5:
                rad_national.Checked = true;
                req_regions.Enabled = true;
                break;
            case 7:
                rad_administrator.Checked = true;
                req_regions.Enabled = false;
                break;
        }
        admin_Checked(this, null);

        
    }

    protected void admin_Checked(object sender, EventArgs e)
    {
        if (rad_administrator.Checked)
        {
            deselect();
            lst_regions.Enabled = false;
        }
        else if (rad_national.Checked)
        {
            lst_regions.Enabled = false;
            lst_regions.SelectionMode = ListSelectionMode.Multiple;
            selectAllRegions();
        }
        else if (rad_regional.Checked)
        {
            deselect();
            lst_regions.Enabled = true;
            lst_regions.SelectionMode = ListSelectionMode.Single;
        }
        else
        {
            lst_regions.Enabled = true;
            lst_regions.SelectionMode = ListSelectionMode.Multiple;
        }
    }
}
