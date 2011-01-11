
package no.ums.ws.parm;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for UMAPSENDING complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UMAPSENDING">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sz_sendingname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_profilepk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_scheddate" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_schedtime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="oadc" type="{http://ums.no/ws/parm/}BBSENDNUM"/>
 *         &lt;element name="n_validity" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_reschedpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_sendingtype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_projectpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_refno" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_dynvoc" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_retries" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_interval" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_canceltime" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_canceldate" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_pausetime" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_pauseinterval" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_addresstypes" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="logoninfo" type="{http://ums.no/ws/parm/}ULOGONINFO"/>
 *         &lt;element name="mapbounds" type="{http://ums.no/ws/parm/}UMapBounds" minOccurs="0"/>
 *         &lt;element name="sz_function" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_lba_oadc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="m_lba" type="{http://ums.no/ws/parm/}ULocationBasedAlert" minOccurs="0"/>
 *         &lt;element name="sz_sms_oadc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_sms_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_sms_expirytime_minutes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="b_resend" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="n_resend_refno" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="resend_statuscodes" type="{http://ums.no/ws/parm/}ArrayOfLong" minOccurs="0"/>
 *         &lt;element name="n_send_channels" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_maxchannels" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UMAPSENDING", propOrder = {
    "szSendingname",
    "nProfilepk",
    "nScheddate",
    "nSchedtime",
    "oadc",
    "nValidity",
    "nReschedpk",
    "nSendingtype",
    "nProjectpk",
    "nRefno",
    "nDynvoc",
    "nRetries",
    "nInterval",
    "nCanceltime",
    "nCanceldate",
    "nPausetime",
    "nPauseinterval",
    "nAddresstypes",
    "logoninfo",
    "mapbounds",
    "szFunction",
    "szLbaOadc",
    "mLba",
    "szSmsOadc",
    "szSmsMessage",
    "nSmsExpirytimeMinutes",
    "bResend",
    "nResendRefno",
    "resendStatuscodes",
    "nSendChannels",
    "nMaxchannels"
})
@XmlSeeAlso({
    UPOLYGONSENDING.class,
    UTASSENDING.class,
    UMUNICIPALSENDING.class,
    UTESTSENDING.class,
    UELLIPSESENDING.class,
    UGISSENDING.class
})
public class UMAPSENDING {

    @XmlElement(name = "sz_sendingname")
    protected String szSendingname;
    @XmlElement(name = "n_profilepk")
    protected long nProfilepk;
    @XmlElement(name = "n_scheddate")
    protected long nScheddate;
    @XmlElement(name = "n_schedtime")
    protected long nSchedtime;
    @XmlElement(required = true)
    protected BBSENDNUM oadc;
    @XmlElement(name = "n_validity")
    protected int nValidity;
    @XmlElement(name = "n_reschedpk")
    protected long nReschedpk;
    @XmlElement(name = "n_sendingtype")
    protected int nSendingtype;
    @XmlElement(name = "n_projectpk")
    protected long nProjectpk;
    @XmlElement(name = "n_refno")
    protected long nRefno;
    @XmlElement(name = "n_dynvoc")
    protected int nDynvoc;
    @XmlElement(name = "n_retries")
    protected int nRetries;
    @XmlElement(name = "n_interval")
    protected int nInterval;
    @XmlElement(name = "n_canceltime")
    protected int nCanceltime;
    @XmlElement(name = "n_canceldate")
    protected int nCanceldate;
    @XmlElement(name = "n_pausetime")
    protected int nPausetime;
    @XmlElement(name = "n_pauseinterval")
    protected int nPauseinterval;
    @XmlElement(name = "n_addresstypes")
    protected long nAddresstypes;
    @XmlElement(required = true)
    protected ULOGONINFO logoninfo;
    protected UMapBounds mapbounds;
    @XmlElement(name = "sz_function")
    protected String szFunction;
    @XmlElement(name = "sz_lba_oadc")
    protected String szLbaOadc;
    @XmlElement(name = "m_lba")
    protected ULocationBasedAlert mLba;
    @XmlElement(name = "sz_sms_oadc")
    protected String szSmsOadc;
    @XmlElement(name = "sz_sms_message")
    protected String szSmsMessage;
    @XmlElement(name = "n_sms_expirytime_minutes")
    protected int nSmsExpirytimeMinutes;
    @XmlElement(name = "b_resend")
    protected boolean bResend;
    @XmlElement(name = "n_resend_refno")
    protected long nResendRefno;
    @XmlElement(name = "resend_statuscodes")
    protected ArrayOfLong resendStatuscodes;
    @XmlElement(name = "n_send_channels")
    protected int nSendChannels;
    @XmlElement(name = "n_maxchannels")
    protected int nMaxchannels;

    /**
     * Gets the value of the szSendingname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSendingname() {
        return szSendingname;
    }

    /**
     * Sets the value of the szSendingname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSendingname(String value) {
        this.szSendingname = value;
    }

    /**
     * Gets the value of the nProfilepk property.
     * 
     */
    public long getNProfilepk() {
        return nProfilepk;
    }

    /**
     * Sets the value of the nProfilepk property.
     * 
     */
    public void setNProfilepk(long value) {
        this.nProfilepk = value;
    }

    /**
     * Gets the value of the nScheddate property.
     * 
     */
    public long getNScheddate() {
        return nScheddate;
    }

    /**
     * Sets the value of the nScheddate property.
     * 
     */
    public void setNScheddate(long value) {
        this.nScheddate = value;
    }

    /**
     * Gets the value of the nSchedtime property.
     * 
     */
    public long getNSchedtime() {
        return nSchedtime;
    }

    /**
     * Sets the value of the nSchedtime property.
     * 
     */
    public void setNSchedtime(long value) {
        this.nSchedtime = value;
    }

    /**
     * Gets the value of the oadc property.
     * 
     * @return
     *     possible object is
     *     {@link BBSENDNUM }
     *     
     */
    public BBSENDNUM getOadc() {
        return oadc;
    }

    /**
     * Sets the value of the oadc property.
     * 
     * @param value
     *     allowed object is
     *     {@link BBSENDNUM }
     *     
     */
    public void setOadc(BBSENDNUM value) {
        this.oadc = value;
    }

    /**
     * Gets the value of the nValidity property.
     * 
     */
    public int getNValidity() {
        return nValidity;
    }

    /**
     * Sets the value of the nValidity property.
     * 
     */
    public void setNValidity(int value) {
        this.nValidity = value;
    }

    /**
     * Gets the value of the nReschedpk property.
     * 
     */
    public long getNReschedpk() {
        return nReschedpk;
    }

    /**
     * Sets the value of the nReschedpk property.
     * 
     */
    public void setNReschedpk(long value) {
        this.nReschedpk = value;
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
     * Gets the value of the nDynvoc property.
     * 
     */
    public int getNDynvoc() {
        return nDynvoc;
    }

    /**
     * Sets the value of the nDynvoc property.
     * 
     */
    public void setNDynvoc(int value) {
        this.nDynvoc = value;
    }

    /**
     * Gets the value of the nRetries property.
     * 
     */
    public int getNRetries() {
        return nRetries;
    }

    /**
     * Sets the value of the nRetries property.
     * 
     */
    public void setNRetries(int value) {
        this.nRetries = value;
    }

    /**
     * Gets the value of the nInterval property.
     * 
     */
    public int getNInterval() {
        return nInterval;
    }

    /**
     * Sets the value of the nInterval property.
     * 
     */
    public void setNInterval(int value) {
        this.nInterval = value;
    }

    /**
     * Gets the value of the nCanceltime property.
     * 
     */
    public int getNCanceltime() {
        return nCanceltime;
    }

    /**
     * Sets the value of the nCanceltime property.
     * 
     */
    public void setNCanceltime(int value) {
        this.nCanceltime = value;
    }

    /**
     * Gets the value of the nCanceldate property.
     * 
     */
    public int getNCanceldate() {
        return nCanceldate;
    }

    /**
     * Sets the value of the nCanceldate property.
     * 
     */
    public void setNCanceldate(int value) {
        this.nCanceldate = value;
    }

    /**
     * Gets the value of the nPausetime property.
     * 
     */
    public int getNPausetime() {
        return nPausetime;
    }

    /**
     * Sets the value of the nPausetime property.
     * 
     */
    public void setNPausetime(int value) {
        this.nPausetime = value;
    }

    /**
     * Gets the value of the nPauseinterval property.
     * 
     */
    public int getNPauseinterval() {
        return nPauseinterval;
    }

    /**
     * Sets the value of the nPauseinterval property.
     * 
     */
    public void setNPauseinterval(int value) {
        this.nPauseinterval = value;
    }

    /**
     * Gets the value of the nAddresstypes property.
     * 
     */
    public long getNAddresstypes() {
        return nAddresstypes;
    }

    /**
     * Sets the value of the nAddresstypes property.
     * 
     */
    public void setNAddresstypes(long value) {
        this.nAddresstypes = value;
    }

    /**
     * Gets the value of the logoninfo property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getLogoninfo() {
        return logoninfo;
    }

    /**
     * Sets the value of the logoninfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setLogoninfo(ULOGONINFO value) {
        this.logoninfo = value;
    }

    /**
     * Gets the value of the mapbounds property.
     * 
     * @return
     *     possible object is
     *     {@link UMapBounds }
     *     
     */
    public UMapBounds getMapbounds() {
        return mapbounds;
    }

    /**
     * Sets the value of the mapbounds property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMapBounds }
     *     
     */
    public void setMapbounds(UMapBounds value) {
        this.mapbounds = value;
    }

    /**
     * Gets the value of the szFunction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzFunction() {
        return szFunction;
    }

    /**
     * Sets the value of the szFunction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzFunction(String value) {
        this.szFunction = value;
    }

    /**
     * Gets the value of the szLbaOadc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzLbaOadc() {
        return szLbaOadc;
    }

    /**
     * Sets the value of the szLbaOadc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzLbaOadc(String value) {
        this.szLbaOadc = value;
    }

    /**
     * Gets the value of the mLba property.
     * 
     * @return
     *     possible object is
     *     {@link ULocationBasedAlert }
     *     
     */
    public ULocationBasedAlert getMLba() {
        return mLba;
    }

    /**
     * Sets the value of the mLba property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULocationBasedAlert }
     *     
     */
    public void setMLba(ULocationBasedAlert value) {
        this.mLba = value;
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

    /**
     * Gets the value of the nSmsExpirytimeMinutes property.
     * 
     */
    public int getNSmsExpirytimeMinutes() {
        return nSmsExpirytimeMinutes;
    }

    /**
     * Sets the value of the nSmsExpirytimeMinutes property.
     * 
     */
    public void setNSmsExpirytimeMinutes(int value) {
        this.nSmsExpirytimeMinutes = value;
    }

    /**
     * Gets the value of the bResend property.
     * 
     */
    public boolean isBResend() {
        return bResend;
    }

    /**
     * Sets the value of the bResend property.
     * 
     */
    public void setBResend(boolean value) {
        this.bResend = value;
    }

    /**
     * Gets the value of the nResendRefno property.
     * 
     */
    public long getNResendRefno() {
        return nResendRefno;
    }

    /**
     * Sets the value of the nResendRefno property.
     * 
     */
    public void setNResendRefno(long value) {
        this.nResendRefno = value;
    }

    /**
     * Gets the value of the resendStatuscodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLong }
     *     
     */
    public ArrayOfLong getResendStatuscodes() {
        return resendStatuscodes;
    }

    /**
     * Sets the value of the resendStatuscodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLong }
     *     
     */
    public void setResendStatuscodes(ArrayOfLong value) {
        this.resendStatuscodes = value;
    }

    /**
     * Gets the value of the nSendChannels property.
     * 
     */
    public int getNSendChannels() {
        return nSendChannels;
    }

    /**
     * Sets the value of the nSendChannels property.
     * 
     */
    public void setNSendChannels(int value) {
        this.nSendChannels = value;
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

}
