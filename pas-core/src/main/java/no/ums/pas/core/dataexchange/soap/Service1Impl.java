
package no.ums.pas.core.dataexchange.soap;

import javax.jws.WebService;

import no.ums.pas.core.dataexchange.soap.no.ums.*;

@WebService(serviceName = "Service1", targetNamespace = "http://ums.no/", endpointInterface = "Core.DataExchange.SOAP.Service1Soap")
public class Service1Impl
    implements Service1Soap
{


    public ExecAlertResponse execAlert(ExecAlert ExecAlert) {
        throw new UnsupportedOperationException();
    }

    public ExecEventResponse execEvent(ExecEvent ExecEvent) {
        throw new UnsupportedOperationException();
    }

}
