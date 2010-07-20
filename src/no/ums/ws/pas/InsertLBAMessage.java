
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
 *         &lt;element name="logon" type="{http://ums.no/ws/pas/}ULOGONINFO"/>
 *         &lt;element name="message" type="{http://ums.no/ws/pas/}ULBAMESSAGE" minOccurs="0"/>
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
    "logon",
    "message"
})
@XmlRootElement(name = "InsertLBAMessage")
public class InsertLBAMessage {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    protected ULBAMESSAGE message;

    /**
     * Gets the value of the logon property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getLogon() {
        return logon;
    }

    /**
     * Sets the value of the logon property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setLogon(ULOGONINFO value) {
        this.logon = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link ULBAMESSAGE }
     *     
     */
    public ULBAMESSAGE getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULBAMESSAGE }
     *     
     */
    public void setMessage(ULBAMESSAGE value) {
        this.message = value;
    }

}
