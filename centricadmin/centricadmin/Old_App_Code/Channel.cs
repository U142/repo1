using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

/// <summary>
/// Summary description for Channel
/// </summary>
public class Channel
{
    private string sz_name;
    private int l_channel;

	public Channel()
	{
		//
		// TODO: Add constructor logic here
		//
	}

    public Channel(string sz_name, int l_channel)
    {
        this.sz_name = sz_name;
        this.l_channel = l_channel;
    }

    public string Name { get { return sz_name; } set { sz_name = value; } }
    public int Id { get { return l_channel;} set { l_channel = value;} }

    public override string ToString()
    {
        return sz_name;
    }

}
