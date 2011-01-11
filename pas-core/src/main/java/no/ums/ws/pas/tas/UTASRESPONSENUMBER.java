
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UTASRESPONSENUMBER complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UTASRESPONSENUMBER">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sz_responsenumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_refno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UTASRESPONSENUMBER", propOrder = {
    "szResponsenumber",
    "nRefno",
    "nTimestamp"
})
public class UTASRESPONSENUMBER {

    @XmlElement(name = "sz_responsenumber")
    protected String szResponsenumber;
    @XmlElement(name = "n_refno")
    protected int nRefno;
    @XmlElement(name = "n_timestamp")
    protected long nTimestamp;

    /**
     * Gets the value of the szResponsenumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzResponsenumber() {
        return szResponsenumber;
    }

    /**
     * Sets the value of the szResponsenumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzResponsenumber(String value) {
        this.szResponsenumber = value;
    }

    /**
     * Gets the value of the nRefno property.
     * 
     */
    public int getNRefno() {
        return nRefno;
    }

    /**
     * Sets the value of the nRefno property.
     * 
     */
    public void setNRefno(int value) {
        this.nRefno = value;
    }

    /**
     * Gets the value of the nTimestamp property.
     * 
     */
    public long getNTimestamp() {
        return nTimestamp;
    }

    /**
     * Sets the value of the nTimestamp property.
     * 
     */
    public void setNTimestamp(long value) {
        this.nTimestamp = value;
    }

}
