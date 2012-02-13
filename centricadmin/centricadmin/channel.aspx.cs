using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class channel : System.Web.UI.Page
{

    private List<Channel> cl;

    protected void Page_Load(object sender, EventArgs e)
    {
        channel_range.MaximumValue = Int32.MaxValue.ToString();
        if (Session["channel"] == null)
            cl = new List<Channel>();
        else
            cl = (List<Channel>)Session["channel"];
    }
    protected void btn_save_Click(object sender, EventArgs e)
    {
        Channel c = new Channel(txt_name.Text, int.Parse(txt_channel.Text));
        cl.Add(c);
        lst_channels.Items.Add(new ListItem(c.Name, c.Id.ToString()));
        txt_channel.Text = "";
        txt_name.Text = "";
        Session["channel"] = cl;
    }
    protected void btn_delete_Click(object sender, EventArgs e)
    {
        cl.RemoveAt(lst_channels.SelectedIndex);
        lst_channels.Items.Remove(lst_channels.SelectedItem);
    }
    protected void btn_create_Click(object sender, EventArgs e)
    {
        txt_channel.Text = "";
        txt_name.Text = "";
        lst_channels.SelectedItem.Selected = false;
    }

}
