
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
 *         &lt;element name="tas" type="{http://ums.no/ws/parm/}UTASSENDING" minOccurs="0"/>
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
    "tas"
})
@XmlRootElement(name = "ExecTasSending")
public class ExecTasSending {

    protected UTASSENDING tas;

    /**
     * Gets the value of the tas property.
     * 
     * @return
     *     possible object is
     *     {@link UTASSENDING }
     *     
     */
    public UTASSENDING getTas() {
        return tas;
    }

    /**
     * Sets the value of the tas property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTASSENDING }
     *     
     */
    public void setTas(UTASSENDING value) {
        this.tas = value;
    }

}
