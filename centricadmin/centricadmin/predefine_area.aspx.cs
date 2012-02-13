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

public partial class predefine_area : System.Web.UI.Page
{
    private Hashtable ht;
    private Db db;

    protected void Page_Load(object sender, EventArgs e)
    {
        ht = (Hashtable)Session["ht"];
        Db db = new Db();

        if (!IsPostBack)
        {
            btn_save.Attributes.Add("onclick", "findhead1('body_Panel2')");

            ht = new Hashtable();

            Session.Add("ht", ht);

            List<PredefinedArea> pda = db.getPredefinedArea();

            for (int i = 0; i < pda.Count; ++i)
            {
                if (pda[i].Parent > 0)
                {
                    TreeNode tn = getNode(TreeView1.Nodes, pda[i].Parent.ToString());
                    tn.ChildNodes.Add(new TreeNode(addJavaScript(pda[i].Name, pda[i].MessagePk), pda[i].MessagePk.ToString()));
                }
                else
                    TreeView1.Nodes.Add(new TreeNode(addJavaScript(pda[i].Name, pda[i].MessagePk), pda[i].MessagePk.ToString()));

                ht.Add(pda[i].MessagePk, pda[i]);
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

        //if (txt_parent.Text.Equals("true"))
        if (db == null)
            db = new Db();

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


        //PredefinedText pdt = new PredefinedText(txt_message.Text, ht.Count, txt_name.Text, int.Parse(parent));
        PredefinedArea pda = new PredefinedArea(txt_name.Text, int.Parse(txt_id.Text), int.Parse(parent));

        TreeNode tn = getNode(TreeView1.Nodes, parent);

        if (tn != null && txt_parent.Text.Equals("true"))
        {
            int id = db.addPredefinedArea(pda);
            tn.ChildNodes.Add(new TreeNode(addJavaScript(txt_name.Text, id), id.ToString()));
            tn.Expand();
            ht.Add(id, pda);
        }
        else if (tn != null) //update
        {
            ((PredefinedText)ht[parent]).Name = txt_name.Text;
            tn.Text = txt_name.Text;
            txt_parent.Text = "";
        }
        else
        {
            int id = db.addPredefinedArea(pda);
            TreeView1.Nodes.Add(new TreeNode(addJavaScript(txt_name.Text, id), id.ToString()));
            ht.Add(id, pda);
        }


        // Store in database       

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
        PredefinedArea pda = (PredefinedArea)ht[Int32.Parse(TreeView1.SelectedNode.Value)];
        txt_name.Text = pda.Name;
    }

    protected void delete_click(object sender, EventArgs e)
    {
        deleteNode(TreeView1.Nodes, txt_id.Text);
    }

    protected void new_click(object sender, EventArgs e)
    {
        txt_name.Text = "";
        txt_name.Focus();
    }

    protected void edit_click(object sender, EventArgs e)
    {
        txt_parent.Text = "false";
        PredefinedArea pda = (PredefinedArea)ht[txt_id.Text];
        txt_name.Text = pda.Name;
    }

    private string addJavaScript(string name, int id)
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
