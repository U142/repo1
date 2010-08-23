
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetOperatorPerformanceThisMonthResult" type="{http://ums.no/ws/pas/status}ArrayOfCB_MESSAGE_MONTHLY_REPORT_RESPONSE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getOperatorPerformanceThisMonthResult"
})
@XmlRootElement(name = "GetOperatorPerformanceThisMonthResponse")
public class GetOperatorPerformanceThisMonthResponse {

    @XmlElement(name = "GetOperatorPerformanceThisMonthResult")
    protected ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE getOperatorPerformanceThisMonthResult;

    /**
     * Gets the value of the getOperatorPerformanceThisMonthResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE }
     *     
     */
    public ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE getGetOperatorPerformanceThisMonthResult() {
        return getOperatorPerformanceThisMonthResult;
    }

    /**
     * Sets the value of the getOperatorPerformanceThisMonthResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE }
     *     
     */
    public void setGetOperatorPerformanceThisMonthResult(ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE value) {
        this.getOperatorPerformanceThisMonthResult = value;
    }

}
