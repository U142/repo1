
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULocationBasedAlert complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULocationBasedAlert">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}UShape">
 *       &lt;sequence>
 *         &lt;element name="l_alertpk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="m_languages" type="{http://ums.no/ws/parm/}ArrayOfLBALanguage" minOccurs="0"/>
 *         &lt;element name="n_expiry_minutes" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_requesttype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULocationBasedAlert", propOrder = {
    "lAlertpk",
    "mLanguages",
    "nExpiryMinutes",
    "nRequesttype"
})
public class ULocationBasedAlert
    extends UShape
{

    @XmlElement(name = "l_alertpk")
    protected String lAlertpk;
    @XmlElement(name = "m_languages")
    protected ArrayOfLBALanguage mLanguages;
    @XmlElement(name = "n_expiry_minutes")
    protected long nExpiryMinutes;
    @XmlElement(name = "n_requesttype")
    protected int nRequesttype;

    /**
     * Gets the value of the lAlertpk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLAlertpk() {
        return lAlertpk;
    }

    /**
     * Sets the value of the lAlertpk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLAlertpk(String value) {
        this.lAlertpk = value;
    }

    /**
     * Gets the value of the mLanguages property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLBALanguage }
     *     
     */
    public ArrayOfLBALanguage getMLanguages() {
        return mLanguages;
    }

    /**
     * Sets the value of the mLanguages property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLBALanguage }
     *     
     */
    public void setMLanguages(ArrayOfLBALanguage value) {
        this.mLanguages = value;
    }

    /**
     * Gets the value of the nExpiryMinutes property.
     * 
     */
    public long getNExpiryMinutes() {
        return nExpiryMinutes;
    }

    /**
     * Sets the value of the nExpiryMinutes property.
     * 
     */
    public void setNExpiryMinutes(long value) {
        this.nExpiryMinutes = value;
    }

    /**
     * Gets the value of the nRequesttype property.
     * 
     */
    public int getNRequesttype() {
        return nRequesttype;
    }

    /**
     * Sets the value of the nRequesttype property.
     * 
     */
    public void setNRequesttype(int value) {
        this.nRequesttype = value;
    }

}
