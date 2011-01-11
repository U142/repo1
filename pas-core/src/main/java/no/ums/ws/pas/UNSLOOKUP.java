
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UNSLOOKUP complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UNSLOOKUP">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sz_domain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_ip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_lastdatetime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="f_success" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UNSLOOKUP", propOrder = {
    "szDomain",
    "szIp",
    "lLastdatetime",
    "szLocation",
    "fSuccess"
})
public class UNSLOOKUP {

    @XmlElement(name = "sz_domain")
    protected String szDomain;
    @XmlElement(name = "sz_ip")
    protected String szIp;
    @XmlElement(name = "l_lastdatetime")
    protected long lLastdatetime;
    @XmlElement(name = "sz_location")
    protected String szLocation;
    @XmlElement(name = "f_success")
    protected boolean fSuccess;

    /**
     * Gets the value of the szDomain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzDomain() {
        return szDomain;
    }

    /**
     * Sets the value of the szDomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzDomain(String value) {
        this.szDomain = value;
    }

    /**
     * Gets the value of the szIp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzIp() {
        return szIp;
    }

    /**
     * Sets the value of the szIp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzIp(String value) {
        this.szIp = value;
    }

    /**
     * Gets the value of the lLastdatetime property.
     * 
     */
    public long getLLastdatetime() {
        return lLastdatetime;
    }

    /**
     * Sets the value of the lLastdatetime property.
     * 
     */
    public void setLLastdatetime(long value) {
        this.lLastdatetime = value;
    }

    /**
     * Gets the value of the szLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzLocation() {
        return szLocation;
    }

    /**
     * Sets the value of the szLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzLocation(String value) {
        this.szLocation = value;
    }

    /**
     * Gets the value of the fSuccess property.
     * 
     */
    public boolean isFSuccess() {
        return fSuccess;
    }

    /**
     * Sets the value of the fSuccess property.
     * 
     */
    public void setFSuccess(boolean value) {
        this.fSuccess = value;
    }

}
