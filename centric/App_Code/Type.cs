using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

/// <summary>
/// Summary description for Type
/// </summary>
public class Type
{
    private int l_id;
    private string sz_description;
	
    public Type()
	{
		//
		// TODO: Add constructor logic here
		//
	}
    public Type(int id, string description)
    {
        l_id = id;
        sz_description = description;
    }

    public override string ToString()
    {
        return sz_description;
    }

    public int Id { get { return l_id; } set { l_id = value; } }
    public string Description { get { return sz_description; } set { sz_description = value; } }

}
