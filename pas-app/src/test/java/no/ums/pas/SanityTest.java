package no.ums.pas;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import no.ums.pas.core.ws.vars;
import no.ums.ws.pas.Pasws;
import org.junit.Test;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class SanityTest {

    @Test
    public void verifyOneTimeKeyWorks() throws MalformedURLException {
    	try
    	{
	    	URL wsdl = new URL("https://secure.ums2.no/pas/ws_pasutv/ws/PAS.asmx");
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
	        new Pasws(wsdl, service).getPaswsSoap12().getOneTimeKey();
    	}
    	finally
    	{
    		
    	}
    }
}
