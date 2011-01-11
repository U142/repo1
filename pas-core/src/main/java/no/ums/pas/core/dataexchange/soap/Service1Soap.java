
package no.ums.pas.core.dataexchange.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import no.ums.pas.core.dataexchange.soap.no.ums.*;

@WebService(name = "Service1Soap", targetNamespace = "http://ums.no/")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface Service1Soap {


    @WebMethod(operationName = "ExecAlert", action = "http://ums.no/ExecAlert")
    @WebResult(name = "ExecAlertResponse", targetNamespace = "http://ums.no/")
    public ExecAlertResponse execAlert(
        @WebParam(name = "ExecAlert", targetNamespace = "http://ums.no/")
        ExecAlert ExecAlert);

    @WebMethod(operationName = "ExecEvent", action = "http://ums.no/ExecEvent")
    @WebResult(name = "ExecEventResponse", targetNamespace = "http://ums.no/")
    public ExecEventResponse execEvent(
        @WebParam(name = "ExecEvent", targetNamespace = "http://ums.no/")
        ExecEvent ExecEvent);

}
