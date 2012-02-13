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

using System.Collections.Generic;

using centric.com.ums.ws.pas;

/// <summary>
/// Summary description for PredefinedArea
/// </summary>
public class PredefinedArea
{

    string sz_name;
    int n_parent;
    int l_messagepk;
    UPolygon polygon;
    // TODO: Bruk samme som polygonstruct

    public PredefinedArea()
    {

    }
    public PredefinedArea(String name, int parent, int messagepk)
	{
        sz_name = name;
        n_parent = parent;
        l_messagepk = messagepk;
		//
		// TODO: Add constructor logic here
		//
	} 
   
    public string Name {
        get { return sz_name; }
        set { sz_name = value; }
    }
   
    public int Parent
    {
        get { return n_parent; }
        set { n_parent = value; }
    }

    public int MessagePk { get { return l_messagepk; } set { l_messagepk = value; } }

    public UPolygon Polygon { get {return polygon;} set{polygon = value; }}
}
