
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPROJECT_REQUEST complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPROJECT_REQUEST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_projectpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPROJECT_REQUEST", propOrder = {
    "nProjectpk",
    "szName"
})
public class UPROJECTREQUEST {

    @XmlElement(name = "n_projectpk")
    protected long nProjectpk;
    @XmlElement(name = "sz_name")
    protected String szName;

    /**
     * Gets the value of the nProjectpk property.
     * 
     */
    public long getNProjectpk() {
        return nProjectpk;
    }

    /**
     * Sets the value of the nProjectpk property.
     * 
     */
    public void setNProjectpk(long value) {
        this.nProjectpk = value;
    }

    /**
     * Gets the value of the szName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzName() {
        return szName;
    }

    /**
     * Sets the value of the szName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzName(String value) {
        this.szName = value;
    }

}
