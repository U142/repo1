package no.ums.pas.core.dataexchange.soap;

import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.ums.errorhandling.Error;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import java.net.URL;




public class SoapExecEvent extends SoapExecAlert
{
	private String l_eventpk;
	
	public SoapExecEvent(String l_eventpk, String sz_sendfunction, UserInfo userinfo)
	throws Exception
	{
		super(sz_sendfunction, userinfo);
		super.sz_function = "ExecEvent";
		this.sz_sendfunction = sz_sendfunction;
		this.l_eventpk = l_eventpk;
		if(this.l_eventpk.startsWith("e"))
			this.l_eventpk = this.l_eventpk.substring(1);
	}
	
	protected void parseResultsTag(Node n)
	throws Exception
	{
		try
		{
			m_results.l_execpk = n.getAttributes().getNamedItem("l_eventpk").getNodeValue();
			m_results.sz_sendfunction = n.getAttributes().getNamedItem("sz_function").getNodeValue(); //.equalsIgnoreCase("true") ? true : false);
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	
	public boolean post(String s_asmx)
	{
		try
		{
	    	hd.addHeader("SOAPAction", sz_action); // = namespace/action
	
	    	Name bodyName = soapFactory.createName(sz_function, "", sz_namespace);
	        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);
	        
	        Name name = soapFactory.createName("l_eventpk");
	        SOAPElement symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(this.l_eventpk);
	        name = soapFactory.createName("l_comppk");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(new Integer(userinfo.get_comppk()).toString());
	        name = soapFactory.createName("l_userpk");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(new Integer(userinfo.get_userpk()).toString());
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
	        symbol.addTextNode(sz_sendfunction);
	        name = soapFactory.createName("sz_scheddate");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(sz_scheddate);
	        name = soapFactory.createName("sz_schedtime");
	        symbol = bodyElement.addChildElement(name);
	        symbol.addTextNode(sz_schedtime);
	        
//	    	URL url = new URL(s_asmx);
//	    	SOAPMessage replyMsg = connection.call(message, url);
	    	
//	    	System.out.println(replyMsg.getSOAPBody().getChildElements().hasNext());
//	    	replyMsg.writeTo(System.out);
//	    	System.out.println("");

	    	//<ExecAlertResponse xmlns="http://ums.no/"><ExecAlertResult><results xmlns="" f_simulation="True" l_alertpk="1000000000000305"><project l_projectpk="1000000000000285"><alert l_alertpk="1000000000000305" l_refno="89582" result="False" text="Error creating shape file for Location Based Alert">Object reference not set to an instance of an object.</alert><alert l_alertpk="1000000000000305" l_refno="89582" result="True" text="Voice Message Sent"/></project></results></ExecAlertResult></ExecAlertResponse>
	    	
	    	/*
	    	 * 
	    	 * Husk å legge til verifikasjon av passord i SendController før instansiering av SoapExecAlert
	    	 * Parse projectpk
	    	 * Parse for feil
	    	 * Vise statusrapport i vindu
	    	 * vinduet skal ha option for å åpne status direkte
	    	 */
	    	sz_confirm = "";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Error.getError().addError("Error", "An error occured when preparing the Event Sending", e, 1);
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
			Error.getError().addError("Error", "An error occured when executing Event Sending", e, 1);
			return false;
		}
		try
		{
	    	SOAPPart soappart = replyMsg.getSOAPPart();
	    	System.out.println(soappart.getTextContent());
	    	NodeList nl = soappart.getElementsByTagName("results");
	    	if(nl.getLength()>=1)
	    	{
	    		Node results = nl.item(0);
	    		parseResultsTag(results);
	    		try {
		    		Node project = results.getFirstChild();
	    			parseProjectTag(project);
	    		} catch(Exception e)
	    		{
	    			//no project tag, probably logon failure
	    			m_results.l_projectpk = "No Project";
	    		}	    		
	    		//NodeList alerts = project.getChildNodes();
	    		NodeList alerts = soappart.getElementsByTagName("alert");
	    		parseResults(alerts);	    		
	    	}
		}
		catch(Exception e)
		{
			Error.getError().addError("Error", "An error occured when parsing results for Event Sending.\nThe sending may have been sent.", e, 1);
			return false;
		}	
    	return true;
	}
}