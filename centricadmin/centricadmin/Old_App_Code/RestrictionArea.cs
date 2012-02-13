using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

/// <summary>
/// Summary description for RestrictionArea
/// </summary>
public class RestrictionArea:PredefinedArea 
{
    private int l_deptpk;

    public RestrictionArea()
    {
       
    }
    public RestrictionArea(String name, int parent)
	{
		//
		// TODO: Add constructor logic here
		//
        Name = name;
        Parent = parent;
        
	}

    public int DepartmentPk { get { return l_deptpk; } set { l_deptpk = value; } }
}
