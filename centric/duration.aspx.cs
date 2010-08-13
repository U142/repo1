using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using System.Data;

using com.ums.ws.pas;


public partial class duration : System.Web.UI.Page
{
    DataTable dt;
    DataView dv;
    List<ULBADURATION> d;
    ULOGONINFO l;

    protected void Page_Load(object sender, EventArgs e)
    {
        duration_range.MaximumValue = Int32.MaxValue.ToString();
        interval_range.MaximumValue = Int32.MaxValue.ToString();
        repetition_range.MaximumValue = Int32.MaxValue.ToString();
        
        l = (ULOGONINFO)Session["logoninfo"];
        if (l == null)
            Server.Transfer("logoff.aspx");

        //dt = (DataTable)Session["datatable"];
        if (!IsPostBack)
        {
            pasws pws = new pasws();

            ULBADURATION[] tempd = pws.GetLBADuration((ULOGONINFO)Session["logoninfo"]);
            d = new List<ULBADURATION>(tempd);
            System.Collections.Generic.IComparer<ULBADURATION> comparer = new DurationComparer();
            d.Sort(comparer);
            //datagrid.DataSource = CreateDataSource();
            //datagrid.DataBind();
            Session["duration"] = d;
            for (int i = 0; i < d.Count; ++i)
                lst_duration.Items.Add(new ListItem(convert(d[i].n_duration), d[i].n_durationpk.ToString()));
        }

        if (Session["duration"] != null)
            d = (List<ULBADURATION>)Session["duration"];
        else
            d = new List<ULBADURATION>();

        

    }

    protected void edit_duration(object sender, EventArgs e)
    {
        txt_duration.Text = d[lst_duration.SelectedIndex].n_duration.ToString();
        txt_interval.Text = d[lst_duration.SelectedIndex].n_interval.ToString();
        txt_repetition.Text = d[lst_duration.SelectedIndex].n_repetition.ToString();

    }

    DataView CreateDataSource()
    {
        if(dt == null)
            dt = new DataTable();

        dt.Columns.Add(new DataColumn("Duration", typeof(Int32)));
        dt.Columns.Add(new DataColumn("Interval", typeof(Int32)));
        dt.Columns.Add(new DataColumn("Repetition", typeof(Int32)));

        
        Session["datatable"] = dt;
        dv = new DataView(dt);
        return dv;
    }

    protected void btn_save_Click(object sender, EventArgs e)
    {
        /*
        DataRow dr = dt.NewRow();
        dr[0] = int.Parse(txt_duration.Text);
        dr[1] = int.Parse(txt_interval.Text);
        dr[2] = int.Parse(txt_repetition.Text);
        dt.Rows.Add(dr);
        datagrid.DataSource = dt;
        datagrid.DataBind();
         */
        ULBADURATION lol = new ULBADURATION();
        lol.n_duration = int.Parse(txt_duration.Text);
        lol.n_interval = int.Parse(txt_interval.Text);
        lol.n_repetition = int.Parse(txt_repetition.Text);
        lol.n_deptpk = l.l_deptpk;

        int id;

        if ((int)lol.n_durationpk == 0)
            lol.n_durationpk = -1;

        id = (int)lol.n_durationpk;

        pasws pws = new pasws();
        lol = pws.InsertLBADuration(l, lol);
        d.Add(lol);
        d.Sort(new DurationComparer());
        Session["duration"] = d;
        lst_duration.Items.Clear();

        for (int i = 0; i < d.Count; ++i)
        {
            lst_duration.Items.Add(new ListItem(convert(d[i].n_duration), d[i].n_durationpk.ToString()));
        }

        clear();
    }

    protected void btn_delete_Click(object sender, EventArgs e)
    {
        d.RemoveAt(lst_duration.SelectedIndex);
        lst_duration.Items.Remove(lst_duration.SelectedItem);
        
    }
    
    protected void btn_create_Click(object sender, EventArgs e)
    {
        clear();
    }

    private void clear()
    {
        txt_duration.Text = "";
        txt_interval.Text = "";
        txt_repetition.Text = "";
    }

    private string convert(int l_duration)
    {
        if (l_duration < 60)
        {
            return l_duration.ToString() + " minute(s)";
        }
        else if (l_duration >= 1440)
        {
            int d = l_duration / 1440;
            int h = (l_duration - (d * 1440)) / 60;
            int m = l_duration - ((d * 1440) + h * 60);
            return d.ToString() + " day(s)" + (h > 0 ? " " + h + " hour(s)" : "") + (m > 0 ? " " + m + " minute(s)" : "");
        }
        else if (l_duration >= 60)
        {
            int h = l_duration / 60;
            int m = l_duration - (h * 60);
            return h.ToString() + " hour(s)" + (m > 0 ? " " + m + " minute(s)" : "");
        }

        return "";
    }
    
}

public class DurationComparer: System.Collections.Generic.IComparer<ULBADURATION>
{
    public int Compare(ULBADURATION x, ULBADURATION y)
    {
        if (x.n_duration > y.n_duration)
            return 1;
        if (x.n_duration < y.n_duration)
            return -1;
        else
            return 0;
    }
}
