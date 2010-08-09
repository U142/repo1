
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MDVSENDINGINFO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MDVSENDINGINFO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_refno" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_fields" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_sepused" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_namepos" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_addresspos" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_lastantsep" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_createdate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_createtime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_scheddate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_schedtime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_sendingname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_sendingstatus" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_companypk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_deptpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_nofax" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_removedup" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_group" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_groups" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_type" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="f_dynacall" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_addresstypes" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_userpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_profilepk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_maxchannels" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_queuestatus" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_totitem" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_processed" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_altjmp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_alloc" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_maxalloc" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_oadc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_qreftype" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_linktype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_resendrefno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_messagetext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_actionprofilename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MDVSENDINGINFO", propOrder = {
    "lRefno",
    "szFields",
    "szSepused",
    "lNamepos",
    "lAddresspos",
    "lLastantsep",
    "lCreatedate",
    "lCreatetime",
    "lScheddate",
    "lSchedtime",
    "szSendingname",
    "lSendingstatus",
    "lCompanypk",
    "lDeptpk",
    "lNofax",
    "lRemovedup",
    "lGroup",
    "szGroups",
    "lType",
    "fDynacall",
    "lAddresstypes",
    "lUserpk",
    "lProfilepk",
    "lMaxchannels",
    "lQueuestatus",
    "lTotitem",
    "lProcessed",
    "lAltjmp",
    "lAlloc",
    "lMaxalloc",
    "szOadc",
    "lQreftype",
    "lLinktype",
    "lResendrefno",
    "szMessagetext",
    "szActionprofilename"
})
public class MDVSENDINGINFO {

    @XmlElement(name = "l_refno")
    protected long lRefno;
    @XmlElement(name = "sz_fields")
    protected String szFields;
    @XmlElement(name = "sz_sepused")
    protected String szSepused;
    @XmlElement(name = "l_namepos")
    protected long lNamepos;
    @XmlElement(name = "l_addresspos")
    protected long lAddresspos;
    @XmlElement(name = "l_lastantsep")
    protected long lLastantsep;
    @XmlElement(name = "l_createdate")
    protected String lCreatedate;
    @XmlElement(name = "l_createtime")
    protected String lCreatetime;
    @XmlElement(name = "l_scheddate")
    protected String lScheddate;
    @XmlElement(name = "l_schedtime")
    protected String lSchedtime;
    @XmlElement(name = "sz_sendingname")
    protected String szSendingname;
    @XmlElement(name = "l_sendingstatus")
    protected long lSendingstatus;
    @XmlElement(name = "l_companypk")
    protected long lCompanypk;
    @XmlElement(name = "l_deptpk")
    protected long lDeptpk;
    @XmlElement(name = "l_nofax")
    protected long lNofax;
    @XmlElement(name = "l_removedup")
    protected long lRemovedup;
    @XmlElement(name = "l_group")
    protected long lGroup;
    @XmlElement(name = "sz_groups")
    protected String szGroups;
    @XmlElement(name = "l_type")
    protected long lType;
    @XmlElement(name = "f_dynacall")
    protected long fDynacall;
    @XmlElement(name = "l_addresstypes")
    protected long lAddresstypes;
    @XmlElement(name = "l_userpk")
    protected long lUserpk;
    @XmlElement(name = "l_profilepk")
    protected long lProfilepk;
    @XmlElement(name = "l_maxchannels")
    protected int lMaxchannels;
    @XmlElement(name = "l_queuestatus")
    protected long lQueuestatus;
    @XmlElement(name = "l_totitem")
    protected long lTotitem;
    @XmlElement(name = "l_processed")
    protected long lProcessed;
    @XmlElement(name = "l_altjmp")
    protected long lAltjmp;
    @XmlElement(name = "l_alloc")
    protected long lAlloc;
    @XmlElement(name = "l_maxalloc")
    protected long lMaxalloc;
    @XmlElement(name = "sz_oadc")
    protected String szOadc;
    @XmlElement(name = "l_qreftype")
    protected long lQreftype;
    @XmlElement(name = "l_linktype")
    protected int lLinktype;
    @XmlElement(name = "l_resendrefno")
    protected int lResendrefno;
    @XmlElement(name = "sz_messagetext")
    protected String szMessagetext;
    @XmlElement(name = "sz_actionprofilename")
    protected String szActionprofilename;

    /**
     * Gets the value of the lRefno property.
     * 
     */
    public long getLRefno() {
        return lRefno;
    }

    /**
     * Sets the value of the lRefno property.
     * 
     */
    public void setLRefno(long value) {
        this.lRefno = value;
    }

    /**
     * Gets the value of the szFields property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzFields() {
        return szFields;
    }

    /**
     * Sets the value of the szFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzFields(String value) {
        this.szFields = value;
    }

    /**
     * Gets the value of the szSepused property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSepused() {
        return szSepused;
    }

    /**
     * Sets the value of the szSepused property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSepused(String value) {
        this.szSepused = value;
    }

    /**
     * Gets the value of the lNamepos property.
     * 
     */
    public long getLNamepos() {
        return lNamepos;
    }

    /**
     * Sets the value of the lNamepos property.
     * 
     */
    public void setLNamepos(long value) {
        this.lNamepos = value;
    }

    /**
     * Gets the value of the lAddresspos property.
     * 
     */
    public long getLAddresspos() {
        return lAddresspos;
    }

    /**
     * Sets the value of the lAddresspos property.
     * 
     */
    public void setLAddresspos(long value) {
        this.lAddresspos = value;
    }

    /**
     * Gets the value of the lLastantsep property.
     * 
     */
    public long getLLastantsep() {
        return lLastantsep;
    }

    /**
     * Sets the value of the lLastantsep property.
     * 
     */
    public void setLLastantsep(long value) {
        this.lLastantsep = value;
    }

    /**
     * Gets the value of the lCreatedate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLCreatedate() {
        return lCreatedate;
    }

    /**
     * Sets the value of the lCreatedate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLCreatedate(String value) {
        this.lCreatedate = value;
    }

    /**
     * Gets the value of the lCreatetime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLCreatetime() {
        return lCreatetime;
    }

    /**
     * Sets the value of the lCreatetime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLCreatetime(String value) {
        this.lCreatetime = value;
    }

    /**
     * Gets the value of the lScheddate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLScheddate() {
        return lScheddate;
    }

    /**
     * Sets the value of the lScheddate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLScheddate(String value) {
        this.lScheddate = value;
    }

    /**
     * Gets the value of the lSchedtime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLSchedtime() {
        return lSchedtime;
    }

    /**
     * Sets the value of the lSchedtime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLSchedtime(String value) {
        this.lSchedtime = value;
    }

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
     * Gets the value of the lSendingstatus property.
     * 
     */
    public long getLSendingstatus() {
        return lSendingstatus;
    }

    /**
     * Sets the value of the lSendingstatus property.
     * 
     */
    public void setLSendingstatus(long value) {
        this.lSendingstatus = value;
    }

    /**
     * Gets the value of the lCompanypk property.
     * 
     */
    public long getLCompanypk() {
        return lCompanypk;
    }

    /**
     * Sets the value of the lCompanypk property.
     * 
     */
    public void setLCompanypk(long value) {
        this.lCompanypk = value;
    }

    /**
     * Gets the value of the lDeptpk property.
     * 
     */
    public long getLDeptpk() {
        return lDeptpk;
    }

    /**
     * Sets the value of the lDeptpk property.
     * 
     */
    public void setLDeptpk(long value) {
        this.lDeptpk = value;
    }

    /**
     * Gets the value of the lNofax property.
     * 
     */
    public long getLNofax() {
        return lNofax;
    }

    /**
     * Sets the value of the lNofax property.
     * 
     */
    public void setLNofax(long value) {
        this.lNofax = value;
    }

    /**
     * Gets the value of the lRemovedup property.
     * 
     */
    public long getLRemovedup() {
        return lRemovedup;
    }

    /**
     * Sets the value of the lRemovedup property.
     * 
     */
    public void setLRemovedup(long value) {
        this.lRemovedup = value;
    }

    /**
     * Gets the value of the lGroup property.
     * 
     */
    public long getLGroup() {
        return lGroup;
    }

    /**
     * Sets the value of the lGroup property.
     * 
     */
    public void setLGroup(long value) {
        this.lGroup = value;
    }

    /**
     * Gets the value of the szGroups property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzGroups() {
        return szGroups;
    }

    /**
     * Sets the value of the szGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzGroups(String value) {
        this.szGroups = value;
    }

    /**
     * Gets the value of the lType property.
     * 
     */
    public long getLType() {
        return lType;
    }

    /**
     * Sets the value of the lType property.
     * 
     */
    public void setLType(long value) {
        this.lType = value;
    }

    /**
     * Gets the value of the fDynacall property.
     * 
     */
    public long getFDynacall() {
        return fDynacall;
    }

    /**
     * Sets the value of the fDynacall property.
     * 
     */
    public void setFDynacall(long value) {
        this.fDynacall = value;
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
     * Gets the value of the lUserpk property.
     * 
     */
    public long getLUserpk() {
        return lUserpk;
    }

    /**
     * Sets the value of the lUserpk property.
     * 
     */
    public void setLUserpk(long value) {
        this.lUserpk = value;
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
     * Gets the value of the lMaxchannels property.
     * 
     */
    public int getLMaxchannels() {
        return lMaxchannels;
    }

    /**
     * Sets the value of the lMaxchannels property.
     * 
     */
    public void setLMaxchannels(int value) {
        this.lMaxchannels = value;
    }

    /**
     * Gets the value of the lQueuestatus property.
     * 
     */
    public long getLQueuestatus() {
        return lQueuestatus;
    }

    /**
     * Sets the value of the lQueuestatus property.
     * 
     */
    public void setLQueuestatus(long value) {
        this.lQueuestatus = value;
    }

    /**
     * Gets the value of the lTotitem property.
     * 
     */
    public long getLTotitem() {
        return lTotitem;
    }

    /**
     * Sets the value of the lTotitem property.
     * 
     */
    public void setLTotitem(long value) {
        this.lTotitem = value;
    }

    /**
     * Gets the value of the lProcessed property.
     * 
     */
    public long getLProcessed() {
        return lProcessed;
    }

    /**
     * Sets the value of the lProcessed property.
     * 
     */
    public void setLProcessed(long value) {
        this.lProcessed = value;
    }

    /**
     * Gets the value of the lAltjmp property.
     * 
     */
    public long getLAltjmp() {
        return lAltjmp;
    }

    /**
     * Sets the value of the lAltjmp property.
     * 
     */
    public void setLAltjmp(long value) {
        this.lAltjmp = value;
    }

    /**
     * Gets the value of the lAlloc property.
     * 
     */
    public long getLAlloc() {
        return lAlloc;
    }

    /**
     * Sets the value of the lAlloc property.
     * 
     */
    public void setLAlloc(long value) {
        this.lAlloc = value;
    }

    /**
     * Gets the value of the lMaxalloc property.
     * 
     */
    public long getLMaxalloc() {
        return lMaxalloc;
    }

    /**
     * Sets the value of the lMaxalloc property.
     * 
     */
    public void setLMaxalloc(long value) {
        this.lMaxalloc = value;
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
     * Gets the value of the lQreftype property.
     * 
     */
    public long getLQreftype() {
        return lQreftype;
    }

    /**
     * Sets the value of the lQreftype property.
     * 
     */
    public void setLQreftype(long value) {
        this.lQreftype = value;
    }

    /**
     * Gets the value of the lLinktype property.
     * 
     */
    public int getLLinktype() {
        return lLinktype;
    }

    /**
     * Sets the value of the lLinktype property.
     * 
     */
    public void setLLinktype(int value) {
        this.lLinktype = value;
    }

    /**
     * Gets the value of the lResendrefno property.
     * 
     */
    public int getLResendrefno() {
        return lResendrefno;
    }

    /**
     * Sets the value of the lResendrefno property.
     * 
     */
    public void setLResendrefno(int value) {
        this.lResendrefno = value;
    }

    /**
     * Gets the value of the szMessagetext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzMessagetext() {
        return szMessagetext;
    }

    /**
     * Sets the value of the szMessagetext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzMessagetext(String value) {
        this.szMessagetext = value;
    }

    /**
     * Gets the value of the szActionprofilename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzActionprofilename() {
        return szActionprofilename;
    }

    /**
     * Sets the value of the szActionprofilename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzActionprofilename(String value) {
        this.szActionprofilename = value;
    }

}
