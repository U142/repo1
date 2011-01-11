
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BBSENDNUM complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BBSENDNUM">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sz_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BBSENDNUM", propOrder = {
    "szNumber"
})
public class BBSENDNUM {

    @XmlElement(name = "sz_number")
    protected String szNumber;

    /**
     * Gets the value of the szNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzNumber() {
        return szNumber;
    }

    /**
     * Sets the value of the szNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzNumber(String value) {
        this.szNumber = value;
    }

}
