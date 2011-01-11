
package no.ums.ws.parm;

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
 *         &lt;element name="LBAResendResult" type="{http://ums.no/ws/parm/}ExecResponse" minOccurs="0"/>
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
    "lbaResendResult"
})
@XmlRootElement(name = "LBAResendResponse")
public class LBAResendResponse {

    @XmlElement(name = "LBAResendResult")
    protected ExecResponse lbaResendResult;

    /**
     * Gets the value of the lbaResendResult property.
     * 
     * @return
     *     possible object is
     *     {@link ExecResponse }
     *     
     */
    public ExecResponse getLBAResendResult() {
        return lbaResendResult;
    }

    /**
     * Sets the value of the lbaResendResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecResponse }
     *     
     */
    public void setLBAResendResult(ExecResponse value) {
        this.lbaResendResult = value;
    }

}
