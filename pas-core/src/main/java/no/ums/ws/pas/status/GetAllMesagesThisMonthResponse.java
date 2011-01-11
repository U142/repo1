
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="GetAllMesagesThisMonthResult" type="{http://ums.no/ws/pas/status}ArrayOfCB_MESSAGE_MONTHLY_REPORT_RESPONSE" minOccurs="0"/>
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
    "getAllMesagesThisMonthResult"
})
@XmlRootElement(name = "GetAllMesagesThisMonthResponse")
public class GetAllMesagesThisMonthResponse {

    @XmlElement(name = "GetAllMesagesThisMonthResult")
    protected ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE getAllMesagesThisMonthResult;

    /**
     * Gets the value of the getAllMesagesThisMonthResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE }
     *     
     */
    public ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE getGetAllMesagesThisMonthResult() {
        return getAllMesagesThisMonthResult;
    }

    /**
     * Sets the value of the getAllMesagesThisMonthResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE }
     *     
     */
    public void setGetAllMesagesThisMonthResult(ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE value) {
        this.getAllMesagesThisMonthResult = value;
    }

}
