
package no.ums.ws.pas.tas;

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
 *         &lt;element name="GetResponseNumbersResult" type="{http://ums.no/ws/pas/tas}ArrayOfUTASRESPONSENUMBER" minOccurs="0"/>
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
    "getResponseNumbersResult"
})
@XmlRootElement(name = "GetResponseNumbersResponse")
public class GetResponseNumbersResponse {

    @XmlElement(name = "GetResponseNumbersResult")
    protected ArrayOfUTASRESPONSENUMBER getResponseNumbersResult;

    /**
     * Gets the value of the getResponseNumbersResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUTASRESPONSENUMBER }
     *     
     */
    public ArrayOfUTASRESPONSENUMBER getGetResponseNumbersResult() {
        return getResponseNumbersResult;
    }

    /**
     * Sets the value of the getResponseNumbersResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUTASRESPONSENUMBER }
     *     
     */
    public void setGetResponseNumbersResult(ArrayOfUTASRESPONSENUMBER value) {
        this.getResponseNumbersResult = value;
    }

}
