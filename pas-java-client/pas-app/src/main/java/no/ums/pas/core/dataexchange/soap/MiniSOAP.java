package no.ums.pas.core.dataexchange.soap;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

public abstract class MiniSOAP {

    private static final Log log = UmsLog.getLogger(MiniSOAP.class);

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
	    	  log.warn(e.getMessage(), e);
	    	  System.err.println(e);
	    	  throw e;
	      }
	      return true;
	}
	
	
}