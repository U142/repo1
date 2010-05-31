
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AUDIO_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AUDIO_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_responsecode" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "AUDIO_RESPONSE", propOrder = {
    "nResponsecode",
    "szResponsetext"
})
public class AUDIORESPONSE {

    @XmlElement(name = "n_responsecode")
    protected int nResponsecode;
    @XmlElement(name = "sz_responsetext")
    protected String szResponsetext;

    /**
     * Gets the value of the nResponsecode property.
     * 
     */
    public int getNResponsecode() {
        return nResponsecode;
    }

    /**
     * Sets the value of the nResponsecode property.
     * 
     */
    public void setNResponsecode(int value) {
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
