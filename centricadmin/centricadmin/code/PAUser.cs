using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using centric.com.ums.ws.parm.admin;

/// <summary>
/// Summary description for User
/// </summary>
public class PAUser
{
    private long l_userpk;
    private string sz_userid;
    private string sz_firstname;
    private string sz_lastname;
    private string sz_password;
    private int l_type;
    private bool f_blocked;
    private DateTime dtm_blocked;
    private int l_deptpk;
    private List<PAOBJECT> arr_regions;
    
	public PAUser(string sz_userid, string sz_firstname, string sz_lastname, bool f_blocked)
	{
		//
		// TODO: Add constructor logic here
		//
        this.sz_userid = sz_userid;
        this.sz_firstname = sz_firstname;
        this.sz_lastname = sz_lastname;
        this.f_blocked = f_blocked;
        arr_regions = new List<PAOBJECT>();
	}

    public string UserId { get { return sz_userid; } set { sz_userid = value; } }
    public string FirstName { get { return sz_firstname; } set { sz_firstname = value; } }
    public string LastName { get { return sz_lastname; } set { sz_lastname = value; } }
    public string Password { get { return sz_password; } set { sz_password = value; } }
    public int Type { get { return l_type; } set { l_type = value; } }
    public bool Blocked { get { return f_blocked; } set { f_blocked = value; } }
    public DateTime DateBlocked { get { return dtm_blocked; } set { dtm_blocked = value; } }
    public int Department { get { return l_deptpk; } set { l_deptpk = value; } }
    public List<PAOBJECT> Regions { get { return arr_regions; } set { arr_regions = value; } }
    public long UserPK { get { return l_userpk; } set { l_userpk = value; } }

    public override string ToString()
    {
        return UserId + " " + FirstName + " " + LastName + " " + Blocked.ToString();
    }

}
