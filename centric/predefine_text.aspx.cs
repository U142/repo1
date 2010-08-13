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

using com.ums.ws.pas;

public partial class predefine_text : System.Web.UI.Page
{

    private Hashtable ht;
    private Db db;
    private pasws pws;

    protected void Page_Load(object sender, EventArgs e)
    {
        ht = (Hashtable)Session["ht"];
        Db db = new Db();

        if (!IsPostBack)
        {
            btn_save.Attributes.Add("onclick", "findhead1('ctl00_body_Panel2')");
            
            ht = new Hashtable();

            Session.Add("ht", ht);
            

            UBBMESSAGELISTFILTER f = new UBBMESSAGELISTFILTER();
            //f.n_timefilter = long.Parse(DateTime.Now.ToString("yyyyMMddHHmmss"));
            f.n_timefilter = 0;

            pws = new pasws();

            ULOGONINFO l = (ULOGONINFO)Session["logoninfo"];
            if (l == null)
                Server.Transfer("logoff.aspx");
            
            ULBAMESSAGELIST list = pws.GetLBAMessageLibrary(l, f);
            //List<PredefinedText> pdt = db.getPredefinedText();

            for (int i = 0; i < list.list.Length; ++i)
            {
                if (list.list[i].n_parentpk > 0)
                {
                    TreeNode tn = getNode(TreeView1.Nodes, list.list[i].n_parentpk.ToString());
                    tn.ChildNodes.Add(new TreeNode(addJavaScript(list.list[i].sz_message, list.list[i].n_messagepk), list.list[i].n_messagepk.ToString()));
                }
                else
                    TreeView1.Nodes.Add(new TreeNode(addJavaScript(list.list[i].sz_message, list.list[i].n_messagepk), list.list[i].n_messagepk.ToString()));

                ht.Add(list.list[i].n_messagepk, list.list[i]);
            }

            

            foreach (TreeNode node in TreeView1.Nodes)
            {

                //new TreeNode = new TreeNode("test" + i++);
            }
        }
    }

    protected void btn_save_Click(object sender, EventArgs e)
    {
        string parent;
        

        parent = txt_id.Text;

        if (!parent.Equals("-1") && parent.Length > 0)
        {
            int result;

            if (!Int32.TryParse(parent, out result))
                result = -1;

            parent = result.ToString();
        }
        else if (parent.Length == 0)
            parent = "-1";

        ULBAMESSAGE pdt = new ULBAMESSAGE();
        pdt.sz_name = txt_name.Text; 
        pdt.sz_message = txt_message.Text;
        
        //PredefinedText pdt = new PredefinedText(txt_message.Text, ht.Count, txt_name.Text, int.Parse(parent));

        TreeNode tn = getNode(TreeView1.Nodes, parent);

        if (pws == null)
            pws = new pasws();
        ULOGONINFO l = (ULOGONINFO)Session["logoninfo"];

        int id = (int)pdt.n_messagepk;
        if (id == 0)
            id = -1;
        pdt.n_messagepk = id;
        pdt.n_deptpk = l.l_deptpk;
        pdt.n_parentpk = long.Parse(parent);
            pdt = pws.InsertLBAMessage(l, pdt);


            id = (int)pdt.n_messagepk;
        //int id = db.addPredefinedText(pdt);

        if (tn != null && txt_parent.Text.Equals("true"))
        {
            tn.ChildNodes.Add(new TreeNode(addJavaScript(txt_name.Text, id), id.ToString()));
            tn.Expand();
            ht.Add(id, pdt);
            txt_parent.Text = "";
            txt_name.Text = "";
            txt_message.Text = "";
        }
        else if (tn != null) //update
        {
            ((PredefinedText)ht[parent]).Text = txt_message.Text;
            ((PredefinedText)ht[parent]).Name = txt_name.Text;
            TreeView1.Nodes.Add(new TreeNode(addJavaScript(txt_name.Text, id), id.ToString()));
            ht.Add(id, pdt);
            tn.Text = txt_name.Text;
            txt_parent.Text = "";
            txt_name.Text = "";
            txt_message.Text = "";
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
                nodes.Remove(nodes[i]);
                ht.Remove(id);
            }
            else if (nodes[i].Value.Equals(id))
                return nodes[i];
        }
        return null;
    }

    protected void TreeView1_changed(object sender, EventArgs e)
    {
        ULBAMESSAGE pdt = (ULBAMESSAGE)ht[long.Parse(TreeView1.SelectedNode.Value)];
        txt_message.Text = pdt.sz_message;
        txt_name.Text = pdt.sz_name;
    }

    protected void delete_click(object sender, EventArgs e)
    {
        if (pws == null)
            pws = new pasws();
        ULOGONINFO l = (ULOGONINFO)Session["logoninfo"];
        ULBAMESSAGE pdt = (ULBAMESSAGE)ht[long.Parse(txt_id.Text)];

        ULBAMESSAGE ret = pws.DeleteLBAMessage(l, pdt);
        if(ret != null)
            deleteNode(TreeView1.Nodes, txt_id.Text);
    }

    protected void new_click(object sender, EventArgs e)
    {
        txt_name.Text = "";
        txt_message.Text = "";
        txt_name.Focus();
    }

    protected void edit_click(object sender, EventArgs e)
    {
        txt_parent.Text = "false";
        PredefinedText pdt = (PredefinedText)ht[txt_id.Text];
        txt_name.Text = pdt.Name;
        txt_message.Text = pdt.Text;
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

