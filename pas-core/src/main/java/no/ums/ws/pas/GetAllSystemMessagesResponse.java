
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
 *         &lt;element name="GetAllSystemMessagesResult" type="{http://ums.no/ws/pas/}USYSTEMMESSAGES" minOccurs="0"/>
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
    "getAllSystemMessagesResult"
})
@XmlRootElement(name = "GetAllSystemMessagesResponse")
public class GetAllSystemMessagesResponse {

    @XmlElement(name = "GetAllSystemMessagesResult")
    protected USYSTEMMESSAGES getAllSystemMessagesResult;

    /**
     * Gets the value of the getAllSystemMessagesResult property.
     * 
     * @return
     *     possible object is
     *     {@link USYSTEMMESSAGES }
     *     
     */
    public USYSTEMMESSAGES getGetAllSystemMessagesResult() {
        return getAllSystemMessagesResult;
    }

    /**
     * Sets the value of the getAllSystemMessagesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link USYSTEMMESSAGES }
     *     
     */
    public void setGetAllSystemMessagesResult(USYSTEMMESSAGES value) {
        this.getAllSystemMessagesResult = value;
    }

}
