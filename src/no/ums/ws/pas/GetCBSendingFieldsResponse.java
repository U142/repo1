
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="getCBSendingFieldsResult" type="{http://ums.no/ws/pas/}CB_MESSAGE_FIELDS" minOccurs="0"/>
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
    "getCBSendingFieldsResult"
})
@XmlRootElement(name = "getCBSendingFieldsResponse")
public class GetCBSendingFieldsResponse {

    protected CBMESSAGEFIELDS getCBSendingFieldsResult;

    /**
     * Gets the value of the getCBSendingFieldsResult property.
     * 
     * @return
     *     possible object is
     *     {@link CBMESSAGEFIELDS }
     *     
     */
    public CBMESSAGEFIELDS getGetCBSendingFieldsResult() {
        return getCBSendingFieldsResult;
    }

    /**
     * Sets the value of the getCBSendingFieldsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBMESSAGEFIELDS }
     *     
     */
    public void setGetCBSendingFieldsResult(CBMESSAGEFIELDS value) {
        this.getCBSendingFieldsResult = value;
    }

}
