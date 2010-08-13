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

public partial class user_admin : System.Web.UI.Page
{
    private PAOBJECT[] objects;
    private List<PAUser> users;
    private PAUser pau = null;

    List<UBBUSER> users;

    protected void Page_Load(object sender, EventArgs e)
    {
        com.ums.ws.pas.ULOGONINFO li = (com.ums.ws.pas.ULOGONINFO)Session["logoninfo"];
        
        if (li == null)
            Server.Transfer("logon.aspx");
        if (!IsPostBack)
        {
            //UPASLOGON pl = pws.PasLogon(li);
            //UDEPARTMENT[] depts = pl.departments;
            ParmAdmin pa = new ParmAdmin();
            objects = (PAOBJECT[])Session["objects"];
            if (objects == null)
            {
                objects = pa.GetRegions(Util.convertLogonInfoParmAdmin(li));
                Session["objects"] = objects;
            }
            if (users == null)
                users = (List<PAUser>)Session["users"];    

            for (int i = 0; i < objects.Length; ++i)
                lst_regions.Items.Add(new ListItem(objects[i].sz_name, objects[i].l_objectpk.ToString()));
        
        }
    }

    private UBBUSER getSelectedUser()
    {
        UBBUSER[] users = (UBBUSER[])Session["users"];
        for(int i=0;i<users.Length;++i) {
            if (users[i].l_userpk == long.Parse(lst_users.SelectedValue))
                return users[i];
        }
        return null;
    }

    protected void btn_save_Click(object sender, EventArgs e)
    {

        UBBUSER user = getSelectedUser();

        // sjekk om l_userpk finnes hvis ikke lag nytt objekt
        // Oprett user sett verdier for 

        if (user == null) // Not selected
            user = new UBBUSER();

        if (chk_blocked.Checked)
            DateTime.Parse(txt_blocked.Text);

        user.sz_name = txt_firstname.Text;
        user.sz_userid = txt_username.Text;
        if (txt_password.Text.Length > 0)
            user.sz_paspassword = txt_password.Text;
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
            user.l_profilepk = 7;
        else if(rad_national.Checked)
            user.l_profilepk = 5;
        else if (rad_sregional.Checked)
            user.l_profilepk = 3;
        else if (rad_regional.Checked)
            user.l_profilepk = 2;

        List<UBBUSER> users = new List<UBBUSER>();
        
        users.Add(user);
        Session["users"] = users;
        
        // Send med UBBUSER og restriction area kan bare sette departmentpk på bbuser forresten?

        deselect();
    }

    protected void btn_create_Click(object sender, EventArgs e)
    {
        deselect();
    }

    protected void btn_edit_Click(object sender, EventArgs e)
    {
        ListItem li = lst_users.SelectedItem;
        pau = null;
        
        for (int i = 0; i < users.Count; ++i)
            if (users[i].UserPK == long.Parse(li.Value))
                pau = users[i];
        
        if (pau != null)
        {
            txt_firstname.Text = pau.FirstName;
            txt_username.Text = pau.UserId;
            txt_password.Text = pau.Password;
            chk_blocked.Checked = pau.Blocked;
            if(pau.Blocked)
                txt_blocked.Text = pau.DateBlocked.ToString("dd-MM-yyyy");
            if (pau.Type == 0)
                rad_administrator.Checked = true;
            else if (pau.Type == 1)
                rad_national.Checked = true;
            else if (pau.Type == 2)
                rad_sregional.Checked = true;
            else if (pau.Type == 3)
                rad_regional.Checked = true;

            for(int i=0;i<lst_regions.Items.Count;++i)
                lst_regions.Items[i].Selected = false;

            for (int i = 0; i < pau.Regions.Count; ++i)
                for (int j = 0; j < lst_regions.Items.Count; ++j)                
                    if (pau.Regions[i].l_objectpk == long.Parse(lst_regions.Items[j].Value))
                        lst_regions.Items[i].Selected = true;
            
        }

        Session["pau"] = pau;
    }

    private void deselect()
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
    }

    protected void enableButtons(object sender, EventArgs e)
    {
        btn_delete.Enabled = true;
        btn_edit.Enabled = true;
    }
}
