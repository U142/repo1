
package no.ums.ws.parm;

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
 *         &lt;element name="send" type="{http://ums.no/ws/parm/}UTESTSENDING" minOccurs="0"/>
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
    "send"
})
@XmlRootElement(name = "ExecTestSending")
public class ExecTestSending {

    protected UTESTSENDING send;

    /**
     * Gets the value of the send property.
     * 
     * @return
     *     possible object is
     *     {@link UTESTSENDING }
     *     
     */
    public UTESTSENDING getSend() {
        return send;
    }

    /**
     * Sets the value of the send property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTESTSENDING }
     *     
     */
    public void setSend(UTESTSENDING value) {
        this.send = value;
    }

}
