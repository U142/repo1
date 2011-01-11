
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
 *         &lt;element name="GetSystemMessagesResult" type="{http://ums.no/ws/pas/}USYSTEMMESSAGES" minOccurs="0"/>
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
    "getSystemMessagesResult"
})
@XmlRootElement(name = "GetSystemMessagesResponse")
public class GetSystemMessagesResponse {

    @XmlElement(name = "GetSystemMessagesResult")
    protected USYSTEMMESSAGES getSystemMessagesResult;

    /**
     * Gets the value of the getSystemMessagesResult property.
     * 
     * @return
     *     possible object is
     *     {@link USYSTEMMESSAGES }
     *     
     */
    public USYSTEMMESSAGES getGetSystemMessagesResult() {
        return getSystemMessagesResult;
    }

    /**
     * Sets the value of the getSystemMessagesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link USYSTEMMESSAGES }
     *     
     */
    public void setGetSystemMessagesResult(USYSTEMMESSAGES value) {
        this.getSystemMessagesResult = value;
    }

}
