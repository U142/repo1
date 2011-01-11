
package no.ums.ws.pas;

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
 *         &lt;element name="DeleteLBAMessageResult" type="{http://ums.no/ws/pas/}ULBAMESSAGE" minOccurs="0"/>
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
    "deleteLBAMessageResult"
})
@XmlRootElement(name = "DeleteLBAMessageResponse")
public class DeleteLBAMessageResponse {

    @XmlElement(name = "DeleteLBAMessageResult")
    protected ULBAMESSAGE deleteLBAMessageResult;

    /**
     * Gets the value of the deleteLBAMessageResult property.
     * 
     * @return
     *     possible object is
     *     {@link ULBAMESSAGE }
     *     
     */
    public ULBAMESSAGE getDeleteLBAMessageResult() {
        return deleteLBAMessageResult;
    }

    /**
     * Sets the value of the deleteLBAMessageResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULBAMESSAGE }
     *     
     */
    public void setDeleteLBAMessageResult(ULBAMESSAGE value) {
        this.deleteLBAMessageResult = value;
    }

}
