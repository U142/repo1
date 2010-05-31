
package no.ums.ws.pas;

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
 *         &lt;element name="URefnoResult" type="{http://ums.no/ws/pas/}REFNO_RESPONSE" minOccurs="0"/>
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
    "uRefnoResult"
})
@XmlRootElement(name = "URefnoResponse")
public class URefnoResponse {

    @XmlElement(name = "URefnoResult")
    protected REFNORESPONSE uRefnoResult;

    /**
     * Gets the value of the uRefnoResult property.
     * 
     * @return
     *     possible object is
     *     {@link REFNORESPONSE }
     *     
     */
    public REFNORESPONSE getURefnoResult() {
        return uRefnoResult;
    }

    /**
     * Sets the value of the uRefnoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link REFNORESPONSE }
     *     
     */
    public void setURefnoResult(REFNORESPONSE value) {
        this.uRefnoResult = value;
    }

}
