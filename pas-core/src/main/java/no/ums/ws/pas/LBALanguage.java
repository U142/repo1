
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LBALanguage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LBALanguage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_textpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_cb_oadc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_otoa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="m_ccodes" type="{http://ums.no/ws/pas/}ArrayOfLBACCode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LBALanguage", propOrder = {
    "lTextpk",
    "szName",
    "szCbOadc",
    "szOtoa",
    "szText",
    "mCcodes"
})
public class LBALanguage {

    @XmlElement(name = "l_textpk")
    protected long lTextpk;
    @XmlElement(name = "sz_name")
    protected String szName;
    @XmlElement(name = "sz_cb_oadc")
    protected String szCbOadc;
    @XmlElement(name = "sz_otoa")
    protected String szOtoa;
    @XmlElement(name = "sz_text")
    protected String szText;
    @XmlElement(name = "m_ccodes")
    protected ArrayOfLBACCode mCcodes;

    /**
     * Gets the value of the lTextpk property.
     * 
     */
    public long getLTextpk() {
        return lTextpk;
    }

    /**
     * Sets the value of the lTextpk property.
     * 
     */
    public void setLTextpk(long value) {
        this.lTextpk = value;
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

    /**
     * Gets the value of the szCbOadc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzCbOadc() {
        return szCbOadc;
    }

    /**
     * Sets the value of the szCbOadc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzCbOadc(String value) {
        this.szCbOadc = value;
    }

    /**
     * Gets the value of the szOtoa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzOtoa() {
        return szOtoa;
    }

    /**
     * Sets the value of the szOtoa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzOtoa(String value) {
        this.szOtoa = value;
    }

    /**
     * Gets the value of the szText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzText() {
        return szText;
    }

    /**
     * Sets the value of the szText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzText(String value) {
        this.szText = value;
    }

    /**
     * Gets the value of the mCcodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLBACCode }
     *     
     */
    public ArrayOfLBACCode getMCcodes() {
        return mCcodes;
    }

    /**
     * Sets the value of the mCcodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLBACCode }
     *     
     */
    public void setMCcodes(ArrayOfLBACCode value) {
        this.mCcodes = value;
    }

}
