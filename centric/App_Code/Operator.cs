using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

/// <summary>
/// Summary description for Operator
/// </summary>
public class Operator
{
    private int l_id;
    private string sz_name;

	public Operator()
	{
		//
		// TODO: Add constructor logic here
		//
	}
    public Operator(int id, string name)
    {
        l_id = id;
        sz_name = name;
    }

    public override string ToString()
    {
        return sz_name;
    }

    public int Id { get { return l_id; } set { l_id = value;} }
    public string Name { get { return sz_name;} set { sz_name = value;} }
}
