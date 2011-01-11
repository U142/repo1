
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PAALERT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PAALERT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parmop" type="{http://ums.no/ws/parm/}PARMOPERATION"/>
 *         &lt;element name="l_temppk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_alertpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_parent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_profilepk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_schedpk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_oadc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_validity" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_addresstypes" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_timestamp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="f_locked" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_areaid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_function" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="m_shape" type="{http://ums.no/ws/parm/}UShape" minOccurs="0"/>
 *         &lt;element name="m_lba_shape" type="{http://ums.no/ws/parm/}UShape" minOccurs="0"/>
 *         &lt;element name="n_sendingtype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_maxchannels" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_requesttype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_expiry" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_sms_oadc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_sms_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PAALERT", propOrder = {
    "parmop",
    "lTemppk",
    "lAlertpk",
    "lParent",
    "szName",
    "szDescription",
    "lProfilepk",
    "lSchedpk",
    "szOadc",
    "lValidity",
    "lAddresstypes",
    "lTimestamp",
    "fLocked",
    "szAreaid",
    "nFunction",
    "mShape",
    "mLbaShape",
    "nSendingtype",
    "nMaxchannels",
    "nRequesttype",
    "nExpiry",
    "szSmsOadc",
    "szSmsMessage"
})
public class PAALERT {

    @XmlElement(required = true)
    protected PARMOPERATION parmop;
    @XmlElement(name = "l_temppk")
    protected long lTemppk;
    @XmlElement(name = "l_alertpk")
    protected long lAlertpk;
    @XmlElement(name = "l_parent")
    protected String lParent;
    @XmlElement(name = "sz_name")
    protected String szName;
    @XmlElement(name = "sz_description")
    protected String szDescription;
    @XmlElement(name = "l_profilepk")
    protected long lProfilepk;
    @XmlElement(name = "l_schedpk")
    protected String lSchedpk;
    @XmlElement(name = "sz_oadc")
    protected String szOadc;
    @XmlElement(name = "l_validity")
    protected long lValidity;
    @XmlElement(name = "l_addresstypes")
    protected long lAddresstypes;
    @XmlElement(name = "l_timestamp")
    protected String lTimestamp;
    @XmlElement(name = "f_locked")
    protected long fLocked;
    @XmlElement(name = "sz_areaid")
    protected String szAreaid;
    @XmlElement(name = "n_function")
    protected int nFunction;
    @XmlElement(name = "m_shape")
    protected UShape mShape;
    @XmlElement(name = "m_lba_shape")
    protected UShape mLbaShape;
    @XmlElement(name = "n_sendingtype")
    protected int nSendingtype;
    @XmlElement(name = "n_maxchannels")
    protected int nMaxchannels;
    @XmlElement(name = "n_requesttype")
    protected int nRequesttype;
    @XmlElement(name = "n_expiry")
    protected int nExpiry;
    @XmlElement(name = "sz_sms_oadc")
    protected String szSmsOadc;
    @XmlElement(name = "sz_sms_message")
    protected String szSmsMessage;

    /**
     * Gets the value of the parmop property.
     * 
     * @return
     *     possible object is
     *     {@link PARMOPERATION }
     *     
     */
    public PARMOPERATION getParmop() {
        return parmop;
    }

    /**
     * Sets the value of the parmop property.
     * 
     * @param value
     *     allowed object is
     *     {@link PARMOPERATION }
     *     
     */
    public void setParmop(PARMOPERATION value) {
        this.parmop = value;
    }

    /**
     * Gets the value of the lTemppk property.
     * 
     */
    public long getLTemppk() {
        return lTemppk;
    }

    /**
     * Sets the value of the lTemppk property.
     * 
     */
    public void setLTemppk(long value) {
        this.lTemppk = value;
    }

    /**
     * Gets the value of the lAlertpk property.
     * 
     */
    public long getLAlertpk() {
        return lAlertpk;
    }

    /**
     * Sets the value of the lAlertpk property.
     * 
     */
    public void setLAlertpk(long value) {
        this.lAlertpk = value;
    }

    /**
     * Gets the value of the lParent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLParent() {
        return lParent;
    }

    /**
     * Sets the value of the lParent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLParent(String value) {
        this.lParent = value;
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
     * Gets the value of the szDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzDescription() {
        return szDescription;
    }

    /**
     * Sets the value of the szDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzDescription(String value) {
        this.szDescription = value;
    }

    /**
     * Gets the value of the lProfilepk property.
     * 
     */
    public long getLProfilepk() {
        return lProfilepk;
    }

    /**
     * Sets the value of the lProfilepk property.
     * 
     */
    public void setLProfilepk(long value) {
        this.lProfilepk = value;
    }

    /**
     * Gets the value of the lSchedpk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLSchedpk() {
        return lSchedpk;
    }

    /**
     * Sets the value of the lSchedpk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLSchedpk(String value) {
        this.lSchedpk = value;
    }

    /**
     * Gets the value of the szOadc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzOadc() {
        return szOadc;
    }

    /**
     * Sets the value of the szOadc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzOadc(String value) {
        this.szOadc = value;
    }

    /**
     * Gets the value of the lValidity property.
     * 
     */
    public long getLValidity() {
        return lValidity;
    }

    /**
     * Sets the value of the lValidity property.
     * 
     */
    public void setLValidity(long value) {
        this.lValidity = value;
    }

    /**
     * Gets the value of the lAddresstypes property.
     * 
     */
    public long getLAddresstypes() {
        return lAddresstypes;
    }

    /**
     * Sets the value of the lAddresstypes property.
     * 
     */
    public void setLAddresstypes(long value) {
        this.lAddresstypes = value;
    }

    /**
     * Gets the value of the lTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLTimestamp() {
        return lTimestamp;
    }

    /**
     * Sets the value of the lTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLTimestamp(String value) {
        this.lTimestamp = value;
    }

    /**
     * Gets the value of the fLocked property.
     * 
     */
    public long getFLocked() {
        return fLocked;
    }

    /**
     * Sets the value of the fLocked property.
     * 
     */
    public void setFLocked(long value) {
        this.fLocked = value;
    }

    /**
     * Gets the value of the szAreaid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzAreaid() {
        return szAreaid;
    }

    /**
     * Sets the value of the szAreaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzAreaid(String value) {
        this.szAreaid = value;
    }

    /**
     * Gets the value of the nFunction property.
     * 
     */
    public int getNFunction() {
        return nFunction;
    }

    /**
     * Sets the value of the nFunction property.
     * 
     */
    public void setNFunction(int value) {
        this.nFunction = value;
    }

    /**
     * Gets the value of the mShape property.
     * 
     * @return
     *     possible object is
     *     {@link UShape }
     *     
     */
    public UShape getMShape() {
        return mShape;
    }

    /**
     * Sets the value of the mShape property.
     * 
     * @param value
     *     allowed object is
     *     {@link UShape }
     *     
     */
    public void setMShape(UShape value) {
        this.mShape = value;
    }

    /**
     * Gets the value of the mLbaShape property.
     * 
     * @return
     *     possible object is
     *     {@link UShape }
     *     
     */
    public UShape getMLbaShape() {
        return mLbaShape;
    }

    /**
     * Sets the value of the mLbaShape property.
     * 
     * @param value
     *     allowed object is
     *     {@link UShape }
     *     
     */
    public void setMLbaShape(UShape value) {
        this.mLbaShape = value;
    }

    /**
     * Gets the value of the nSendingtype property.
     * 
     */
    public int getNSendingtype() {
        return nSendingtype;
    }

    /**
     * Sets the value of the nSendingtype property.
     * 
     */
    public void setNSendingtype(int value) {
        this.nSendingtype = value;
    }

    /**
     * Gets the value of the nMaxchannels property.
     * 
     */
    public int getNMaxchannels() {
        return nMaxchannels;
    }

    /**
     * Sets the value of the nMaxchannels property.
     * 
     */
    public void setNMaxchannels(int value) {
        this.nMaxchannels = value;
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

    /**
     * Gets the value of the nExpiry property.
     * 
     */
    public int getNExpiry() {
        return nExpiry;
    }

    /**
     * Sets the value of the nExpiry property.
     * 
     */
    public void setNExpiry(int value) {
        this.nExpiry = value;
    }

    /**
     * Gets the value of the szSmsOadc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSmsOadc() {
        return szSmsOadc;
    }

    /**
     * Sets the value of the szSmsOadc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSmsOadc(String value) {
        this.szSmsOadc = value;
    }

    /**
     * Gets the value of the szSmsMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSmsMessage() {
        return szSmsMessage;
    }

    /**
     * Sets the value of the szSmsMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSmsMessage(String value) {
        this.szSmsMessage = value;
    }

}
