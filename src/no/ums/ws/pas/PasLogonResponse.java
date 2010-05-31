
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
 *         &lt;element name="PasLogonResult" type="{http://ums.no/ws/pas/}UPASLOGON" minOccurs="0"/>
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
    "pasLogonResult"
})
@XmlRootElement(name = "PasLogonResponse")
public class PasLogonResponse {

    @XmlElement(name = "PasLogonResult")
    protected UPASLOGON pasLogonResult;

    /**
     * Gets the value of the pasLogonResult property.
     * 
     * @return
     *     possible object is
     *     {@link UPASLOGON }
     *     
     */
    public UPASLOGON getPasLogonResult() {
        return pasLogonResult;
    }

    /**
     * Sets the value of the pasLogonResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPASLOGON }
     *     
     */
    public void setPasLogonResult(UPASLOGON value) {
        this.pasLogonResult = value;
    }

}
