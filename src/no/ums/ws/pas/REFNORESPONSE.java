
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for REFNO_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="REFNO_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_refno" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_responsecode" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_responsetext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "REFNO_RESPONSE", propOrder = {
    "nRefno",
    "nResponsecode",
    "szResponsetext"
})
public class REFNORESPONSE {

    @XmlElement(name = "n_refno")
    protected long nRefno;
    @XmlElement(name = "n_responsecode")
    protected long nResponsecode;
    @XmlElement(name = "sz_responsetext")
    protected String szResponsetext;

    /**
     * Gets the value of the nRefno property.
     * 
     */
    public long getNRefno() {
        return nRefno;
    }

    /**
     * Sets the value of the nRefno property.
     * 
     */
    public void setNRefno(long value) {
        this.nRefno = value;
    }

    /**
     * Gets the value of the nResponsecode property.
     * 
     */
    public long getNResponsecode() {
        return nResponsecode;
    }

    /**
     * Sets the value of the nResponsecode property.
     * 
     */
    public void setNResponsecode(long value) {
        this.nResponsecode = value;
    }

    /**
     * Gets the value of the szResponsetext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzResponsetext() {
        return szResponsetext;
    }

    /**
     * Sets the value of the szResponsetext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzResponsetext(String value) {
        this.szResponsetext = value;
    }

}
