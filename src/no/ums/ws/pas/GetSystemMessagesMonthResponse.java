
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
 *         &lt;element name="GetSystemMessagesMonthResult" type="{http://ums.no/ws/pas/}USYSTEMMESSAGES" minOccurs="0"/>
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
    "getSystemMessagesMonthResult"
})
@XmlRootElement(name = "GetSystemMessagesMonthResponse")
public class GetSystemMessagesMonthResponse {

    @XmlElement(name = "GetSystemMessagesMonthResult")
    protected USYSTEMMESSAGES getSystemMessagesMonthResult;

    /**
     * Gets the value of the getSystemMessagesMonthResult property.
     * 
     * @return
     *     possible object is
     *     {@link USYSTEMMESSAGES }
     *     
     */
    public USYSTEMMESSAGES getGetSystemMessagesMonthResult() {
        return getSystemMessagesMonthResult;
    }

    /**
     * Sets the value of the getSystemMessagesMonthResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link USYSTEMMESSAGES }
     *     
     */
    public void setGetSystemMessagesMonthResult(USYSTEMMESSAGES value) {
        this.getSystemMessagesMonthResult = value;
    }

}
