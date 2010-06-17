
package no.ums.ws.pas.tas;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "taswsSoap", targetNamespace = "http://ums.no/ws/pas/tas")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface TaswsSoap {


    /**
     * 
     * @param timefilterCount
     * @param timefilterRequestlog
     * @param logon
     * @return
     *     returns no.ums.ws.pas.tas.UTASUPDATES
     */
    @WebMethod(operationName = "GetContinentsAndCountries", action = "http://ums.no/ws/pas/tas/GetContinentsAndCountries")
    @WebResult(name = "GetContinentsAndCountriesResult", targetNamespace = "http://ums.no/ws/pas/tas")
    @RequestWrapper(localName = "GetContinentsAndCountries", targetNamespace = "http://ums.no/ws/pas/tas", className = "no.ums.ws.pas.tas.GetContinentsAndCountries")
    @ResponseWrapper(localName = "GetContinentsAndCountriesResponse", targetNamespace = "http://ums.no/ws/pas/tas", className = "no.ums.ws.pas.tas.GetContinentsAndCountriesResponse")
    public UTASUPDATES getContinentsAndCountries(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/tas")
        ULOGONINFO logon,
        @WebParam(name = "timefilter_count", targetNamespace = "http://ums.no/ws/pas/tas")
        long timefilterCount,
        @WebParam(name = "timefilter_requestlog", targetNamespace = "http://ums.no/ws/pas/tas")
        long timefilterRequestlog);

    /**
     * 
     * @param logon
     * @param country
     * @return
     *     returns no.ums.ws.pas.tas.UTASREQUEST
     */
    @WebMethod(operationName = "GetAdrCount", action = "http://ums.no/ws/pas/tas/GetAdrCount")
    @WebResult(name = "GetAdrCountResult", targetNamespace = "http://ums.no/ws/pas/tas")
    @RequestWrapper(localName = "GetAdrCount", targetNamespace = "http://ums.no/ws/pas/tas", className = "no.ums.ws.pas.tas.GetAdrCount")
    @ResponseWrapper(localName = "GetAdrCountResponse", targetNamespace = "http://ums.no/ws/pas/tas", className = "no.ums.ws.pas.tas.GetAdrCountResponse")
    public UTASREQUEST getAdrCount(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/tas")
        ULOGONINFO logon,
        @WebParam(name = "country", targetNamespace = "http://ums.no/ws/pas/tas")
        ArrayOfULBACOUNTRY country);

    /**
     * 
     * @param logon
     * @return
     *     returns no.ums.ws.pas.tas.ArrayOfUTASRESPONSENUMBER
     */
    @WebMethod(operationName = "GetResponseNumbers", action = "http://ums.no/ws/pas/tas/GetResponseNumbers")
    @WebResult(name = "GetResponseNumbersResult", targetNamespace = "http://ums.no/ws/pas/tas")
    @RequestWrapper(localName = "GetResponseNumbers", targetNamespace = "http://ums.no/ws/pas/tas", className = "no.ums.ws.pas.tas.GetResponseNumbers")
    @ResponseWrapper(localName = "GetResponseNumbersResponse", targetNamespace = "http://ums.no/ws/pas/tas", className = "no.ums.ws.pas.tas.GetResponseNumbersResponse")
    public ArrayOfUTASRESPONSENUMBER getResponseNumbers(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/tas")
        ULOGONINFO logon);

    /**
     * 
     * @param logon
     * @param filter
     * @return
     *     returns no.ums.ws.pas.tas.ArrayOfULBACOUNTRYSTATISTICS
     */
    @WebMethod(operationName = "GetStatsCountriesPerTimeunit", action = "http://ums.no/ws/pas/tas/GetStatsCountriesPerTimeunit")
    @WebResult(name = "GetStatsCountriesPerTimeunitResult", targetNamespace = "http://ums.no/ws/pas/tas")
    @RequestWrapper(localName = "GetStatsCountriesPerTimeunit", targetNamespace = "http://ums.no/ws/pas/tas", className = "no.ums.ws.pas.tas.GetStatsCountriesPerTimeunit")
    @ResponseWrapper(localName = "GetStatsCountriesPerTimeunitResponse", targetNamespace = "http://ums.no/ws/pas/tas", className = "no.ums.ws.pas.tas.GetStatsCountriesPerTimeunitResponse")
    public ArrayOfULBACOUNTRYSTATISTICS getStatsCountriesPerTimeunit(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/tas")
        ULOGONINFO logon,
        @WebParam(name = "filter", targetNamespace = "http://ums.no/ws/pas/tas")
        ULBASTATISTICSFILTER filter);

}
