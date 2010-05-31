package no.ums.pas.core.dataexchange.soap;

import java.io.*;
import java.net.*;
import javax.xml.soap.*;
import java.util.*;
import org.w3c.dom.*;

public abstract class MiniSOAP {
	protected SOAPFactory soapFactory;
	protected SOAPConnectionFactory soapConnectionFactory;
	protected MessageFactory messageFactory = MessageFactory.newInstance();
	protected SOAPConnection connection;
	protected SOAPHeader header;
	protected SOAPMessage message;
	protected SOAPBody body;
	protected MimeHeaders hd;
	
	protected String sz_action;
	protected String sz_namespace;
	protected String sz_function;
	
	public MiniSOAP()
	throws Exception
	{
		try
		{
			init();
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	protected abstract void parseResults(org.w3c.dom.NodeList alerts);
	public abstract boolean post(String s_asmx);
	
	protected boolean init()
	throws Exception
	{
	    try {

	    	soapConnectionFactory = SOAPConnectionFactory.newInstance();
	    	connection = soapConnectionFactory.createConnection();
	    	soapFactory = SOAPFactory.newInstance();
	    	message = messageFactory.createMessage();
	    	header = message.getSOAPHeader( );
	    	body = message.getSOAPBody();
	    	hd = message.getMimeHeaders();

	    	
	    	hd.addHeader("Content-Type", "text/xml; charset=UTF-8");
	      }
	      catch (Exception e) {
	    	  e.printStackTrace();
	    	  System.err.println(e);
	    	  throw e;
	      }
	      return true;
	}
	
	
}