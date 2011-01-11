package no.ums.pas.core.dataexchange.soap;

import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.ums.errorhandling.Error;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;



public class SoapExecAlert extends MiniSOAP
{
	public SnapAlertResults newSnapAlertResult() { return new SnapAlertResults(); }
	// main results tag in xml response
	public class SnapAlertResults
	{
		// results pr alert sent
		protected class ExecAlertResult
		{
			public String l_alertpk, text, description, sz_alertname;
			public int n_refno;
			boolean f_result;
			protected ExecAlertResult(String l_alertpk, String sz_alertname, String l_refno, String result, String text, String description)
			{
				this.l_alertpk = l_alertpk;
				this.sz_alertname = sz_alertname;
				try
				{
					this.n_refno   = Integer.parseInt(l_refno);
				}
				catch(Exception e) { }
				try
				{
					this.f_result    = Boolean.parseBoolean(result);
				} 
				catch(Exception e) { }
				this.text	   = text;
				this.description= description;
				if(this.text==null)
					this.text = "";
				if(this.description==null)
					this.description = "";
			}
			public String toString()
			{
				// String s = "l_alertpk = " + l_alertpk + "\n";
				// s += "f_result = " + f_result + "\n";
				String c;
				c = (f_result ? "green" : "red");
				String s = "<font color=\"" + c + "\">" + text;
				if(description.length()>0)
					s		+= " - " + description;
				s		+= "</font>";
				return s;
			}
			public String toStringHeading()
			{
				String ret = "<u>";
				if(n_refno>0)
					ret += PAS.l("common_refno") + n_refno;
				if(sz_alertname.length()>0)
					ret += " [" + sz_alertname + "]";
				ret += "</u>";
				return ret;
			}
		}
		public String l_execpk;
		// boolean f_simulation;
		//String sz_function; // live, simulation, test
		public String sz_sendfunction = "Unknown function";
		public String l_projectpk;
		public void setProjectPk(String l_projectpk)
		{
			this.l_projectpk = l_projectpk;
			if(this.l_projectpk!=null)
				f_project_exists = true;
		}
		public boolean f_project_exists = false;
		boolean f_has_one_or_more_success = false;
		public boolean hasProject() { return f_project_exists; }
		public boolean hasOneOrMoreSuccess() { //return f_has_one_or_more_success; }
			for(int i=0; i < m_arr_alerts.size(); i++)
			{
				if(m_arr_alerts.get(i).f_result==true)
					return true;
			}
			return false;
		}
		// collection of ExecAlertResult objects
		ArrayList<ExecAlertResult> m_arr_alerts = new ArrayList<ExecAlertResult>();
		public String getProjectpk() { return l_projectpk; }
		public String getExecPk() { return l_execpk; }
		// public boolean getSimulation() { return f_simulation; }
		public String getFunction() { return sz_function; }
		public String getSendFunction() { return sz_sendfunction; }
		
		public void addExecAlertResult(String l_alertpk, String sz_alertname, String l_refno, String result, String text, String description)
		{
			if(result.equalsIgnoreCase("true"))
				f_has_one_or_more_success = true;
			m_arr_alerts.add(new ExecAlertResult(l_alertpk, sz_alertname, l_refno, result, text, description));
		}
		public int getAlertResultsSize() { return m_arr_alerts.size(); }
		public ExecAlertResult getAlertResult(int i) { return (ExecAlertResult)m_arr_alerts.get(i); }
		public String toString(boolean b_openstatus_question)
		{
			String sz_showfunc;
			if(sz_sendfunction.equals("live"))
				sz_showfunc = PAS.l("main_sending_live");
			else if(sz_sendfunction.equals("simulate"))
				sz_showfunc = PAS.l("main_sending_simulated");
			else if(sz_sendfunction.equals("test"))
				sz_showfunc = PAS.l("main_sending_test");
			else
				sz_showfunc = PAS.l("main_sending_unknown");
			
			String sz_result = 	"<html>"+
								"<b><u>" + PAS.l("projectdlg_project") + " ["+
								l_projectpk+
								"] [" + sz_showfunc + "]" +
								"</u></b><br>";
			Iterator it = m_arr_alerts.iterator();
			String sz_current_alertpk = "-1";
			int l_current_refno = -1;
			while(it.hasNext())
			{
				ExecAlertResult res = (ExecAlertResult)it.next();
				if(!sz_current_alertpk.equalsIgnoreCase(res.l_alertpk)
					|| l_current_refno!=res.n_refno) // only
																		// write
																		// the
																		// same
																		// Alertpk/Refno
																		// once
				{
					sz_result += "<br>";
					sz_result += res.toStringHeading();
					sz_current_alertpk = res.l_alertpk;					
					sz_result += "<br>";
				}
				l_current_refno = res.n_refno;
				sz_result += res.toString();
				sz_result += "<br>";
			}
								
			if(hasProject() && PAS.get_pas().get_statuscontroller().get_sendinglist().size() < 1 && hasOneOrMoreSuccess() && !getSendFunction().equals("test") && b_openstatus_question)
				sz_result +=		"<br>" + PAS.l("main_sending_open_status");
			
			sz_result += "</html>";
			return sz_result;

		}
		public String toString()
		{
			return toString(true);
		}
	}
	
	private String l_alertpk;
	//protected boolean f_simulation;
	protected String sz_sendfunction;
	protected UserInfo userinfo;
	protected String sz_confirm;
	protected String sz_scheddate;
	protected String sz_schedtime;
	protected SnapAlertResults m_results = new SnapAlertResults();
	
	public SnapAlertResults getResults() { return m_results; }
	
	
	public SoapExecAlert(String l_alertpk, String sz_sendfunction, UserInfo userinfo)
	throws Exception
	{
		super();
		if(l_alertpk.startsWith("a"))
			l_alertpk = l_alertpk.substring(1);
		super.sz_action = "http://ums.no/ExecAlert";
		super.sz_function = "ExecAlert";
		super.sz_namespace = "http://ums.no/";
		this.l_alertpk = l_alertpk;
		// this.f_simulation = f_simulation;
		this.sz_sendfunction = sz_sendfunction;
		this.userinfo = userinfo;
	}
	protected SoapExecAlert(String sz_function, UserInfo userinfo)
	throws Exception
	{
		super();
		super.sz_action = "http://ums.no/ExecEvent";
		super.sz_function = "ExecEvent";
		super.sz_namespace = "http://ums.no/";
		// this.f_simulation = f_simulation;
		this.sz_function = sz_function;
		this.userinfo = userinfo;
	}
	
// <results xmlns="" f_simulation="True" l_alertpk="1000000000000305"><project
// l_projectpk="1000000000000285"><alert l_alertpk="1000000000000305"
// l_refno="89582" result="False" text="Error creating shape file for Location
// Based Alert">Object reference not set to an instance of an
// object.</alert><alert l_alertpk="1000000000000305" l_refno="89582"
// result="True" text="Voice Message Sent"/></project></results>
	protected void parseResults(NodeList project) {
		for(int i=0; i < project.getLength(); i++)
		{
			String l_alertpk, sz_alertname, l_refno, result, text, description;
			Node n = project.item(i);
			try
			{
				l_alertpk = n.getAttributes().getNamedItem("l_alertpk").getNodeValue();
			}
			catch(Exception e)
			{
				l_alertpk = "<PARSE ERROR>";
			}
			try
			{
				sz_alertname = n.getAttributes().getNamedItem("sz_name").getNodeValue();
			}
			catch(Exception e)
			{
				sz_alertname = "";
			}
			try
			{
				l_refno   = n.getAttributes().getNamedItem("l_refno").getNodeValue();
			}
			catch(Exception e)
			{
				l_refno = "<PARSE ERROR>";
			}
			try
			{
				result	 = n.getAttributes().getNamedItem("result").getNodeValue();
			}
			catch(Exception e)
			{
				result = "<PARSE ERROR>";
			}
			try
			{
				text		 = n.getAttributes().getNamedItem("text").getNodeValue();
			}
			catch(Exception e)
			{
				text = "";
			}
			try
			{
				description = n.getFirstChild().getNodeValue();
			}
			catch(Exception e)
			{
				description = "";
			}
			m_results.addExecAlertResult(l_alertpk, sz_alertname, l_refno, result, text, description);
		}
	}
	
	
	protected void parseResultsTag(Node n)
	throws Exception
	{
		try
		{
			m_results.l_execpk = n.getAttributes().getNamedItem("l_alertpk").getNodeValue();
			m_results.sz_sendfunction = n.getAttributes().getNamedItem("sz_function").getNodeValue(); // .equalsIgnoreCase("true")
																									// ?
																									// true
																									// :
																									// false);
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	protected void parseProjectTag(Node n)
	throws Exception
	{
		try
		{
			m_results.l_projectpk = n.getAttributes().getNamedItem("l_projectpk").getNodeValue();
			m_results.f_project_exists = true; 
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	public boolean post(String s_asmx, String sz_confirm_password, String sz_scheddate, String sz_schedtime)
	{
		this.sz_confirm = sz_confirm_password;
		this.sz_scheddate = sz_scheddate;
		this.sz_schedtime = sz_schedtime;
		return post(s_asmx);
	}
	public boolean post(String s_asmx)
	{
		try
		{
	    	hd.addHeader("SOAPAction", sz_action); // = namespace/action
	
	    	Name bodyName = soapFactory.createName(sz_function, "", sz_namespace);
	        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);
	        
	        Name name = soapFactory.createName("l_alertpk");
	        SOAPElement symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(this.l_alertpk);
	        name = soapFactory.createName("l_comppk");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(new Integer(userinfo.get_comppk()).toString());
	        name = soapFactory.createName("l_userpk");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(userinfo.get_userpk());
	        name = soapFactory.createName("l_deptpk");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(new Integer(userinfo.get_current_department().get_deptpk()).toString());
	        name = soapFactory.createName("sz_compid");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(userinfo.get_compid());
	        name = soapFactory.createName("sz_userid");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(userinfo.get_userid());
	        name = soapFactory.createName("sz_deptid");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(userinfo.get_current_department().get_deptid());
	        name = soapFactory.createName("sz_password");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(sz_confirm);
	        name = soapFactory.createName("sz_function");
	        symbol = bodyElement.addChildElement(name);
	        //symbol.addTextNode((f_simulation ? "true" : "false"));
	        symbol.addTextNode(sz_sendfunction);
	        name = soapFactory.createName("sz_scheddate");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(sz_scheddate);
	        name = soapFactory.createName("sz_schedtime");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(sz_schedtime);
	        
	    	// <ExecAlertResponse
			// xmlns="http://ums.no/"><ExecAlertResult><results xmlns=""
			// f_simulation="True" l_alertpk="1000000000000305"><project
			// l_projectpk="1000000000000285"><alert
			// l_alertpk="1000000000000305" l_refno="89582" result="False"
			// text="Error creating shape file for Location Based Alert">Object
			// reference not set to an instance of an object.</alert><alert
			// l_alertpk="1000000000000305" l_refno="89582" result="True"
			// text="Voice Message
			// Sent"/></project></results></ExecAlertResult></ExecAlertResponse>
	    	
	    	/*
			 * 
			 * Husk å legge til verifikasjon av passord i SendController før
			 * instansiering av SoapExecAlert Parse projectpk Parse for feil
			 * Vise statusrapport i vindu vinduet skal ha option for å åpne
			 * status direkte
			 */
	    	sz_confirm = "";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Error.getError().addError(PAS.l("common_error"), "An error occured when preparing the Alert Sending", e, 1);
			return false;
		}
		SOAPMessage replyMsg = null;
		try
		{
	    	URL url = new URL(s_asmx);
	    	replyMsg = connection.call(message, url);			
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_error"), "An error occured when executing Alert Sending", e, 1);
			return false;
		}
		try
		{
	    	System.out.println(replyMsg.getSOAPBody().getChildElements().hasNext());
	    	replyMsg.writeTo(System.out);
	    	System.out.println("");
	    	SOAPPart soappart = replyMsg.getSOAPPart();
	    	NodeList nl = soappart.getElementsByTagName("results");
	    	if(nl.getLength()==1)
	    	{
	    		Node results = nl.item(0);
	    		parseResultsTag(results);
	    		// Node project = results.getFirstChild();
	    		try {
		    		Node project = results.getFirstChild();
	    			parseProjectTag(project);
	    			parseResults(project.getChildNodes());
	    		} 
	    		catch(Exception e)
	    		{
	    			// no project tag, probably logon failure
	    			m_results.l_projectpk = "No Project";
		    		NodeList alerts = soappart.getElementsByTagName("alert");
		    		parseResults(alerts);	    		
	    		}	    		
	    		// NodeList alerts = project.getChildNodes();
	    	}			
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_error"), "An error occured when parsing results for Alert Sending.\nThe sending may have been sent.", e, 1);
			return false;
		}
    	return true;
	}
	
}