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
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;

using com.ums.ws.pas;
using com.ums.ws.pas.admin;

public partial class predefine_text : System.Web.UI.Page
{

    private Hashtable ht;
    private Db db;
    private pasws pws;

    protected void Page_Load(object sender, EventArgs e)
    {
        ht = (Hashtable)Session["ht"];
        Db db = new Db();
        
        //Master.BodyTag.Attributes.Add("onunload", "setUnlock('page=predefine_text')");

        if (!IsPostBack)
        {
            com.ums.ws.pas.admin.ULOGONINFO l = (com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
            if (l == null)
                Server.Transfer("logoff.aspx");

            PasAdmin pa = new PasAdmin();
            //CheckAccessResponse ares = pa.doSetOccupied(l, ACCESSPAGE.PREDEFINEDTEXT, true);
            
            //if (ares.successful && ares.granted)
            //{

                btn_save.Attributes.Add("onclick", "findhead1('ctl00_body_Panel2')");

                ht = new Hashtable();

                Session.Add("ht", ht);


                UBBMESSAGELISTFILTER f = new UBBMESSAGELISTFILTER();
                //f.n_timefilter = long.Parse(DateTime.Now.ToString("yyyyMMddHHmmss"));
                f.n_timefilter = 0;

                pws = new pasws();



                UBBMESSAGELIST list = pws.GetMessageLibrary(Util.convertLogonInfoPas(l), f);
                //List<PredefinedText> pdt = db.getPredefinedText();

                for (int i = 0; i < list.list.Length; ++i)
                {
                    if (list.list[i].n_parentpk > 0)
                    {
                        TreeNode tn = getNode(TreeView1.Nodes, list.list[i].n_parentpk.ToString());
                        tn.ChildNodes.Add(new TreeNode(addJavaScript(list.list[i].sz_name, list.list[i].n_messagepk), list.list[i].n_messagepk.ToString()));
                    }
                    else
                        TreeView1.Nodes.Add(new TreeNode(addJavaScript(list.list[i].sz_name, list.list[i].n_messagepk), list.list[i].n_messagepk.ToString()));

                    ht.Add(list.list[i].n_messagepk, list.list[i]);
                }
                
            //}
            //else
                //Server.Transfer("Currently_busy.aspx");
        }
    }
    
    private char[] validateMessage(String text)
    {
        Regex re = Util.GSM_Alphabet_Regex;
        MatchCollection mc = re.Matches(text);
        char[] ret = new char[mc.Count];
        for(int i=0;i<mc.Count;++i)
            ret[i] = text.Substring(mc[i].Index,1).ToCharArray()[0];
        return ret;
    }

    protected void btn_save_Click(object sender, EventArgs e)
    {
        string parent = "";
        lbl_error.Text = "";
        UBBMESSAGE pdt = null;
        char[] invalid = validateMessage(txt_message.Text);
        if (invalid.Length > 0)
        {
            lbl_error.Text = "Invalid characters(comma separated): ";
            for (int i = 0; i < invalid.Length; ++i)
            {
                lbl_error.Text += invalid[i].ToString() + ", ";
            }
        }
        else
        {
            try
            {
                ht = (Hashtable)Session["ht"];

                if (txt_parent.Text.Equals("true"))
                    parent = txt_id.Text;
                else
                {
                    pdt = (UBBMESSAGE)ht[long.Parse(txt_id.Text)];
                }

                if (!parent.Equals("-1") && parent.Length > 0)
                {
                    int result;

                    if (!Int32.TryParse(parent, out result))
                        result = -1;

                    parent = result.ToString();
                }
                else if (parent.Length == 0)
                    parent = "-1";

                if (pdt == null)
                    pdt = new UBBMESSAGE();

                pdt.sz_name = txt_name.Text;
                UCCMessage message = new UCCMessage();
                message.l_cc = -1;
                pdt.f_template = 1;
                message.sz_message = txt_message.Text;
                pdt.ccmessage = new UCCMessage[] { message };

                //PredefinedText pdt = new PredefinedText(txt_message.Text, ht.Count, txt_name.Text, int.Parse(parent));



                if (pws == null)
                    pws = new pasws();
                com.ums.ws.pas.admin.ULOGONINFO l = (com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];

                long id = (long)pdt.n_messagepk;
                if (id == 0)
                    id = -1;
                pdt.n_messagepk = id;
                pdt.n_deptpk = -1;
                if (pdt.n_parentpk == 0)
                    pdt.n_parentpk = long.Parse(parent);
                pdt = pws.InsertMessageLibrary(Util.convertLogonInfoPas(l), pdt);


                id = (int)pdt.n_messagepk;
                //int id = db.addPredefinedText(pdt);

                TreeNode tn = getNode(TreeView1.Nodes, pdt.n_parentpk.ToString());

                if (tn == null)
                    tn = getNode(TreeView1.Nodes, pdt.n_messagepk.ToString());

                if (tn != null && txt_parent.Text.Equals("true"))
                {
                    tn.ChildNodes.Add(new TreeNode(addJavaScript(txt_name.Text, id), id.ToString()));
                    tn.Expand();
                    ht.Add(id, pdt);
                    txt_parent.Text = "";
                    txt_name.Text = "";
                    txt_message.Text = "";
                    txt_name.Enabled = false;
                    //txt_message.Enabled = false;
                    txt_message.Attributes.Add("onFocus", "javascript:this.blur();");
                    btn_save.Enabled = false;
                }
                else if (tn == null) // Root
                {
                    TreeView1.Nodes.Add(new TreeNode(addJavaScript(txt_name.Text, id), id.ToString()));
                    ht.Add(id, pdt);
                    txt_parent.Text = "";
                    txt_name.Text = "";
                    txt_message.Text = "";
                    txt_name.Enabled = false;
                    //txt_message.Enabled = false;
                    txt_message.Attributes.Add("onFocus", "javascript:this.blur();");
                    btn_save.Enabled = false;
                }
                else if (tn != null) //update
                {
                    ht.Remove(pdt.n_messagepk);
                    ht.Add(pdt.n_messagepk, pdt);
                    TreeNode tmptn = getNode(TreeView1.Nodes, pdt.n_messagepk.ToString());
                    tmptn.Text = addJavaScript(pdt.sz_name, id);
                    //TreeView1.Nodes.Add(new TreeNode(addJavaScript(txt_name.Text, id), id.ToString()));
                    txt_parent.Text = "";
                    //txt_message.Enabled = false;
                    txt_message.Attributes.Add("onFocus", "javascript:this.blur();");
                    txt_name.Enabled = false;
                    btn_save.Enabled = false;
                }
            }
            catch (Exception ex)
            {
                lbl_error.Text = ex.Message;
            }
        }
       
    }

    private TreeNode getNode(TreeNodeCollection nodes, string id)
    {
        return parseTree(nodes, id, false);
    }
    private void deleteNode(TreeNodeCollection nodes, string id)
    {
        parseTree(nodes, id, true);
    }

    private TreeNode parseTree(TreeNodeCollection nodes, string id, bool delete)
    {
        for (int i = 0; i < nodes.Count; ++i)
        {
            if (nodes[i].ChildNodes.Count > 0)
            {
                TreeNode tmptn = parseTree(nodes[i].ChildNodes, id, delete);
                if (tmptn != null)
                    return tmptn;
            }
            if (nodes[i].Value.Equals(id) && delete)
            {
                for (int j = 0; j < nodes[i].ChildNodes.Count; ++j)
                        deleteNode(nodes[i].ChildNodes, nodes[i].ChildNodes[j].Value);

                if (pws == null)
                    pws = new pasws();
                com.ums.ws.pas.admin.ULOGONINFO l = (com.ums.ws.pas.admin.ULOGONINFO)Session["logoninfo"];
                UBBMESSAGE pdt = (UBBMESSAGE)ht[long.Parse(id)];

                UBBMESSAGE ret = pws.DeleteMessageLibrary(Util.convertLogonInfoPas(l), pdt);
                if (ret != null)
                {
                    nodes.Remove(nodes[i]);
                    ht.Remove(long.Parse(id));
                }
            }
            else if (nodes[i].Value.Equals(id))
                return nodes[i];
        }
        return null;
    }

    protected void TreeView1_changed(object sender, EventArgs e)
    {
        UBBMESSAGE pdt = (UBBMESSAGE)ht[long.Parse(TreeView1.SelectedNode.Value)];
        txt_message.Text = pdt.ccmessage[0].sz_message;
        txt_name.Text = pdt.sz_name;
        //txt_message.Enabled = false;
        txt_message.Attributes.Add("onFocus", "javascript:this.blur();");
        txt_name.Enabled = false;
        btn_save.Enabled = false;
        lbl_error.Text = "";
    }

    protected void delete_click(object sender, EventArgs e)
    {
        if (txt_id.Text.Length > 0 && !txt_id.Text.Equals("-1"))
        {
            deleteNode(TreeView1.Nodes, txt_id.Text);
            txt_message.Enabled = false;
            txt_name.Enabled = false;
            txt_parent.Text = "";
            txt_id.Text = "";
            txt_message.Text = "";
            txt_name.Text = "";
            lbl_error.Text = "";
        }
        else
        {
            txt_message.Attributes.Add("onFocus", "javascript:this.blur();");
            txt_name.Enabled = false;
            btn_save.Enabled = false;
            lbl_error.Text = "Node must be selected";
        }
    }

    protected void new_click(object sender, EventArgs e)
    {
        txt_name.Text = "";
        txt_message.Text = "";
        txt_message.Attributes.Remove("onFocus");
        //txt_message.Enabled = true;
        txt_name.Enabled = true;
        btn_save.Enabled = true;
        txt_name.Focus();
        lbl_error.Text = "";
        txt_message.Attributes.Remove("onFocus");
    }

    protected void edit_click(object sender, EventArgs e)
    {
        if (txt_id.Text.Length > 0 && !txt_id.Text.Equals("-1"))
        {
            txt_parent.Text = "false";
            UBBMESSAGE pdt = (UBBMESSAGE)ht[long.Parse(txt_id.Text)];
            txt_name.Text = pdt.sz_name;
            txt_message.Text = pdt.ccmessage[0].sz_message;
            //txt_message.Enabled = true;
            txt_message.Attributes.Remove("onFocus");
            txt_name.Enabled = true;
            btn_save.Enabled = true;
            lbl_error.Text = "";
        }
        else
        {
            txt_message.Attributes.Add("onFocus", "javascript:this.blur();");
            txt_name.Enabled = false;
            btn_save.Enabled = false;
            lbl_error.Text = "Node must be selected";
        }
    }

    private string addJavaScript(string name, long id)
    {
        return "<span id='id" + id + "' oncontextmenu='return showmenuie5(event)' >" + name + "</span>";
    }
    private string removeJavaScript(string name)
    {
        string tmp = "";

        name = name.Remove(name.Length - 7);
        tmp = name.Substring(name.IndexOf("oncontextmenu='return showmenuie5(event)' >"));
        return tmp;
    }
    
}

