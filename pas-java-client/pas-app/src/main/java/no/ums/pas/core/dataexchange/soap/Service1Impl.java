
package no.ums.pas.core.dataexchange.soap;

import no.ums.pas.core.dataexchange.soap.no.ums.ExecAlert;
import no.ums.pas.core.dataexchange.soap.no.ums.ExecAlertResponse;
import no.ums.pas.core.dataexchange.soap.no.ums.ExecEvent;
import no.ums.pas.core.dataexchange.soap.no.ums.ExecEventResponse;

import javax.jws.WebService;

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
