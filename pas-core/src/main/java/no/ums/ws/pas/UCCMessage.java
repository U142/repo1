
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UCCMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UCCMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sz_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_cc" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UCCMessage", propOrder = {
    "szMessage",
    "lCc"
})
public class UCCMessage {

    @XmlElement(name = "sz_message")
    protected String szMessage;
    @XmlElement(name = "l_cc")
    protected int lCc;

    /**
     * Gets the value of the szMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzMessage() {
        return szMessage;
    }

    /**
     * Sets the value of the szMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzMessage(String value) {
        this.szMessage = value;
    }

    /**
     * Gets the value of the lCc property.
     * 
     */
    public int getLCc() {
        return lCc;
    }

    /**
     * Sets the value of the lCc property.
     * 
     */
    public void setLCc(int value) {
        this.lCc = value;
    }

}
