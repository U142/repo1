
package no.ums.ws.pas.status;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "PasStatus", targetNamespace = "http://ums.no/ws/pas/status", wsdlLocation = "http://localhost/WS/PasStatus.asmx?WSDL")
public class PasStatus
    extends Service
{

    private final static URL PASSTATUS_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://localhost/WS/PasStatus.asmx?WSDL");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        PASSTATUS_WSDL_LOCATION = url;
    }

    public PasStatus(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public PasStatus() {
        super(PASSTATUS_WSDL_LOCATION, new QName("http://ums.no/ws/pas/status", "PasStatus"));
    }

    /**
     * 
     * @return
     *     returns PasStatusSoap
     */
    @WebEndpoint(name = "PasStatusSoap")
    public PasStatusSoap getPasStatusSoap() {
        return (PasStatusSoap)super.getPort(new QName("http://ums.no/ws/pas/status", "PasStatusSoap"), PasStatusSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns PasStatusSoap
     */
    @WebEndpoint(name = "PasStatusSoap")
    public PasStatusSoap getPasStatusSoap(WebServiceFeature... features) {
        return (PasStatusSoap)super.getPort(new QName("http://ums.no/ws/pas/status", "PasStatusSoap"), PasStatusSoap.class, features);
    }

    /**
     * 
     * @return
     *     returns PasStatusSoap
     */
    @WebEndpoint(name = "PasStatusSoap12")
    public PasStatusSoap getPasStatusSoap12() {
        return (PasStatusSoap)super.getPort(new QName("http://ums.no/ws/pas/status", "PasStatusSoap12"), PasStatusSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns PasStatusSoap
     */
    @WebEndpoint(name = "PasStatusSoap12")
    public PasStatusSoap getPasStatusSoap12(WebServiceFeature... features) {
        return (PasStatusSoap)super.getPort(new QName("http://ums.no/ws/pas/status", "PasStatusSoap12"), PasStatusSoap.class, features);
    }

}
