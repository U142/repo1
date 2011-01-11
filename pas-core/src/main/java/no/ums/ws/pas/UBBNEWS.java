
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UBBNEWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UBBNEWS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_newspk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_created" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_validms" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_type" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_incident_start" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_incident_end" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="f_active" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_severity" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_operator" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_errorcode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_userpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_timestamp_db" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="newstext" type="{http://ums.no/ws/pas/}UBBNEWSTEXT" minOccurs="0"/>
 *         &lt;element name="sz_operatorname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UBBNEWS", propOrder = {
    "lNewspk",
    "lCreated",
    "lValidms",
    "lType",
    "lIncidentStart",
    "lIncidentEnd",
    "fActive",
    "lDeptpk",
    "lSeverity",
    "lOperator",
    "lErrorcode",
    "lUserpk",
    "lTimestampDb",
    "szUserid",
    "newstext",
    "szOperatorname"
})
public class UBBNEWS {

    @XmlElement(name = "l_newspk")
    protected long lNewspk;
    @XmlElement(name = "l_created")
    protected long lCreated;
    @XmlElement(name = "l_validms")
    protected int lValidms;
    @XmlElement(name = "l_type")
    protected long lType;
    @XmlElement(name = "l_incident_start")
    protected long lIncidentStart;
    @XmlElement(name = "l_incident_end")
    protected long lIncidentEnd;
    @XmlElement(name = "f_active")
    protected int fActive;
    @XmlElement(name = "l_deptpk")
    protected int lDeptpk;
    @XmlElement(name = "l_severity")
    protected int lSeverity;
    @XmlElement(name = "l_operator")
    protected int lOperator;
    @XmlElement(name = "l_errorcode")
    protected int lErrorcode;
    @XmlElement(name = "l_userpk")
    protected long lUserpk;
    @XmlElement(name = "l_timestamp_db")
    protected long lTimestampDb;
    @XmlElement(name = "sz_userid")
    protected String szUserid;
    protected UBBNEWSTEXT newstext;
    @XmlElement(name = "sz_operatorname")
    protected String szOperatorname;

    /**
     * Gets the value of the lNewspk property.
     * 
     */
    public long getLNewspk() {
        return lNewspk;
    }

    /**
     * Sets the value of the lNewspk property.
     * 
     */
    public void setLNewspk(long value) {
        this.lNewspk = value;
    }

    /**
     * Gets the value of the lCreated property.
     * 
     */
    public long getLCreated() {
        return lCreated;
    }

    /**
     * Sets the value of the lCreated property.
     * 
     */
    public void setLCreated(long value) {
        this.lCreated = value;
    }

    /**
     * Gets the value of the lValidms property.
     * 
     */
    public int getLValidms() {
        return lValidms;
    }

    /**
     * Sets the value of the lValidms property.
     * 
     */
    public void setLValidms(int value) {
        this.lValidms = value;
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
     * Gets the value of the lIncidentStart property.
     * 
     */
    public long getLIncidentStart() {
        return lIncidentStart;
    }

    /**
     * Sets the value of the lIncidentStart property.
     * 
     */
    public void setLIncidentStart(long value) {
        this.lIncidentStart = value;
    }

    /**
     * Gets the value of the lIncidentEnd property.
     * 
     */
    public long getLIncidentEnd() {
        return lIncidentEnd;
    }

    /**
     * Sets the value of the lIncidentEnd property.
     * 
     */
    public void setLIncidentEnd(long value) {
        this.lIncidentEnd = value;
    }

    /**
     * Gets the value of the fActive property.
     * 
     */
    public int getFActive() {
        return fActive;
    }

    /**
     * Sets the value of the fActive property.
     * 
     */
    public void setFActive(int value) {
        this.fActive = value;
    }

    /**
     * Gets the value of the lDeptpk property.
     * 
     */
    public int getLDeptpk() {
        return lDeptpk;
    }

    /**
     * Sets the value of the lDeptpk property.
     * 
     */
    public void setLDeptpk(int value) {
        this.lDeptpk = value;
    }

    /**
     * Gets the value of the lSeverity property.
     * 
     */
    public int getLSeverity() {
        return lSeverity;
    }

    /**
     * Sets the value of the lSeverity property.
     * 
     */
    public void setLSeverity(int value) {
        this.lSeverity = value;
    }

    /**
     * Gets the value of the lOperator property.
     * 
     */
    public int getLOperator() {
        return lOperator;
    }

    /**
     * Sets the value of the lOperator property.
     * 
     */
    public void setLOperator(int value) {
        this.lOperator = value;
    }

    /**
     * Gets the value of the lErrorcode property.
     * 
     */
    public int getLErrorcode() {
        return lErrorcode;
    }

    /**
     * Sets the value of the lErrorcode property.
     * 
     */
    public void setLErrorcode(int value) {
        this.lErrorcode = value;
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
     * Gets the value of the lTimestampDb property.
     * 
     */
    public long getLTimestampDb() {
        return lTimestampDb;
    }

    /**
     * Sets the value of the lTimestampDb property.
     * 
     */
    public void setLTimestampDb(long value) {
        this.lTimestampDb = value;
    }

    /**
     * Gets the value of the szUserid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzUserid() {
        return szUserid;
    }

    /**
     * Sets the value of the szUserid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzUserid(String value) {
        this.szUserid = value;
    }

    /**
     * Gets the value of the newstext property.
     * 
     * @return
     *     possible object is
     *     {@link UBBNEWSTEXT }
     *     
     */
    public UBBNEWSTEXT getNewstext() {
        return newstext;
    }

    /**
     * Sets the value of the newstext property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBNEWSTEXT }
     *     
     */
    public void setNewstext(UBBNEWSTEXT value) {
        this.newstext = value;
    }

    /**
     * Gets the value of the szOperatorname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzOperatorname() {
        return szOperatorname;
    }

    /**
     * Sets the value of the szOperatorname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzOperatorname(String value) {
        this.szOperatorname = value;
    }

}
