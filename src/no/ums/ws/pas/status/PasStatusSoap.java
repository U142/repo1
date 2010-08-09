
package no.ums.ws.pas.status;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "PasStatusSoap", targetNamespace = "http://ums.no/ws/pas/status")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface PasStatusSoap {


    /**
     * 
     * @param nRefno
     * @param logoninfo
     * @return
     *     returns no.ums.ws.pas.status.ArrayOfUSMSINSTATS
     */
    @WebMethod(operationName = "GetSmsStats", action = "http://ums.no/ws/pas/status/GetSmsStats")
    @WebResult(name = "GetSmsStatsResult", targetNamespace = "http://ums.no/ws/pas/status")
    @RequestWrapper(localName = "GetSmsStats", targetNamespace = "http://ums.no/ws/pas/status", className = "no.ums.ws.pas.status.GetSmsStats")
    @ResponseWrapper(localName = "GetSmsStatsResponse", targetNamespace = "http://ums.no/ws/pas/status", className = "no.ums.ws.pas.status.GetSmsStatsResponse")
    public ArrayOfUSMSINSTATS getSmsStats(
        @WebParam(name = "logoninfo", targetNamespace = "http://ums.no/ws/pas/status")
        ULOGONINFO logoninfo,
        @WebParam(name = "n_refno", targetNamespace = "http://ums.no/ws/pas/status")
        long nRefno);

    /**
     * 
     * @param logoninfo
     * @return
     *     returns no.ums.ws.pas.status.UStatusListResults
     */
    @WebMethod(operationName = "GetStatusList", action = "http://ums.no/ws/pas/status/GetStatusList")
    @WebResult(name = "GetStatusListResult", targetNamespace = "http://ums.no/ws/pas/status")
    @RequestWrapper(localName = "GetStatusList", targetNamespace = "http://ums.no/ws/pas/status", className = "no.ums.ws.pas.status.GetStatusList")
    @ResponseWrapper(localName = "GetStatusListResponse", targetNamespace = "http://ums.no/ws/pas/status", className = "no.ums.ws.pas.status.GetStatusListResponse")
    public UStatusListResults getStatusList(
        @WebParam(name = "logoninfo", targetNamespace = "http://ums.no/ws/pas/status")
        ULOGONINFO logoninfo);

    /**
     * 
     * @param req
     * @return
     *     returns no.ums.ws.pas.status.CBPROJECTSTATUSRESPONSE
     */
    @WebMethod(operationName = "GetCBStatus", action = "http://ums.no/ws/pas/status/GetCBStatus")
    @WebResult(name = "GetCBStatusResult", targetNamespace = "http://ums.no/ws/pas/status")
    @RequestWrapper(localName = "GetCBStatus", targetNamespace = "http://ums.no/ws/pas/status", className = "no.ums.ws.pas.status.GetCBStatus")
    @ResponseWrapper(localName = "GetCBStatusResponse", targetNamespace = "http://ums.no/ws/pas/status", className = "no.ums.ws.pas.status.GetCBStatusResponse")
    public CBPROJECTSTATUSRESPONSE getCBStatus(
        @WebParam(name = "req", targetNamespace = "http://ums.no/ws/pas/status")
        CBPROJECTSTATUSREQUEST req);

    /**
     * 
     * @param logoninfo
     * @param search
     * @return
     *     returns byte[]
     */
    @WebMethod(operationName = "GetStatusItems", action = "http://ums.no/ws/pas/status/GetStatusItems")
    @WebResult(name = "GetStatusItemsResult", targetNamespace = "http://ums.no/ws/pas/status")
    @RequestWrapper(localName = "GetStatusItems", targetNamespace = "http://ums.no/ws/pas/status", className = "no.ums.ws.pas.status.GetStatusItems")
    @ResponseWrapper(localName = "GetStatusItemsResponse", targetNamespace = "http://ums.no/ws/pas/status", className = "no.ums.ws.pas.status.GetStatusItemsResponse")
    public byte[] getStatusItems(
        @WebParam(name = "logoninfo", targetNamespace = "http://ums.no/ws/pas/status")
        ULOGONINFO logoninfo,
        @WebParam(name = "search", targetNamespace = "http://ums.no/ws/pas/status")
        UStatusItemSearchParams search);

    /**
     * 
     * @return
     *     returns byte[]
     */
    @WebMethod(operationName = "GetStatusItemsTest", action = "http://ums.no/ws/pas/status/GetStatusItemsTest")
    @WebResult(name = "GetStatusItemsTestResult", targetNamespace = "http://ums.no/ws/pas/status")
    @RequestWrapper(localName = "GetStatusItemsTest", targetNamespace = "http://ums.no/ws/pas/status", className = "no.ums.ws.pas.status.GetStatusItemsTest")
    @ResponseWrapper(localName = "GetStatusItemsTestResponse", targetNamespace = "http://ums.no/ws/pas/status", className = "no.ums.ws.pas.status.GetStatusItemsTestResponse")
    public byte[] getStatusItemsTest();

}
