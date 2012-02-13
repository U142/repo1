using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;

/// <summary>
/// Summary description for PredefinedText
/// </summary>
public class PredefinedText
{
    private string text;
    private string sz_name;
    private int n_parent;
    private int n_message_id;

    public PredefinedText()
    {

    }

    public PredefinedText(string text, int messageid, string name, int parent)
    {
        this.text = text;
        this.sz_name = name;
        this.n_message_id = messageid;
        this.n_parent = parent;
    }
    public string Name
    {
        get { return sz_name; }
        set { sz_name = value; }
    }
    public string Text
    {
        get { return text; }
        set { text = value; }
    }
    public int Parent
    {
        get { return n_parent; }
        set { n_parent = value; }
    }
    public int MessageId
    {
        get { return n_message_id; }
        set { n_message_id = value; }
    }
}
