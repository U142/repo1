
package no.ums.ws.parm;

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
 *         &lt;element name="ConfirmJobResult" type="{http://ums.no/ws/parm/}UConfirmJobResponse" minOccurs="0"/>
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
    "confirmJobResult"
})
@XmlRootElement(name = "ConfirmJobResponse")
public class ConfirmJobResponse {

    @XmlElement(name = "ConfirmJobResult")
    protected UConfirmJobResponse confirmJobResult;

    /**
     * Gets the value of the confirmJobResult property.
     * 
     * @return
     *     possible object is
     *     {@link UConfirmJobResponse }
     *     
     */
    public UConfirmJobResponse getConfirmJobResult() {
        return confirmJobResult;
    }

    /**
     * Sets the value of the confirmJobResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UConfirmJobResponse }
     *     
     */
    public void setConfirmJobResult(UConfirmJobResponse value) {
        this.confirmJobResult = value;
    }

}
