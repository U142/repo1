using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

/// <summary>
/// Summary description for SysMessage
/// </summary>
public class SysMessage:PredefinedText
{
    private DateTime dtm_act;
    private DateTime dtm_dact;
    private Operator m_operator;
    private Type m_type;

	public SysMessage()
	{
		//
		// TODO: Add constructor logic here
		//
	}

    public override string ToString()
    {
        return m_operator.Name + "\t" + m_type.Description + "\t" + (DateActivate == DateTime.MinValue ? "" : DateActivate.ToString("dd-MM-yyyy HH:mm")) + " - " + (DateDeactivate == DateTime.MinValue ? "" : DateDeactivate.ToString("dd-MM-yyyy HH:mm"));
    }

    public DateTime DateActivate { get { return dtm_act; } set { dtm_act = value; } }
    public DateTime DateDeactivate { get { return dtm_dact; } set { dtm_dact = value; } }
    public Operator Operator { get { return m_operator; } set { m_operator = value;} }
    public Type Type { get { return m_type; } set { m_type = value; } }
}