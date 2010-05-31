
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULOGONINFO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULOGONINFO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_userpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_comppk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_deptid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_compid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_deptpri" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_priserver" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_altservers" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_stdcc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="jobid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULOGONINFO", propOrder = {
    "lUserpk",
    "lComppk",
    "lDeptpk",
    "szDeptid",
    "szUserid",
    "szCompid",
    "szPassword",
    "lDeptpri",
    "lPriserver",
    "lAltservers",
    "szStdcc",
    "jobid"
})
public class ULOGONINFO {

    @XmlElement(name = "l_userpk")
    protected long lUserpk;
    @XmlElement(name = "l_comppk")
    protected int lComppk;
    @XmlElement(name = "l_deptpk")
    protected int lDeptpk;
    @XmlElement(name = "sz_deptid")
    protected String szDeptid;
    @XmlElement(name = "sz_userid")
    protected String szUserid;
    @XmlElement(name = "sz_compid")
    protected String szCompid;
    @XmlElement(name = "sz_password")
    protected String szPassword;
    @XmlElement(name = "l_deptpri")
    protected int lDeptpri;
    @XmlElement(name = "l_priserver")
    protected int lPriserver;
    @XmlElement(name = "l_altservers")
    protected int lAltservers;
    @XmlElement(name = "sz_stdcc")
    protected String szStdcc;
    protected String jobid;

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
     * Gets the value of the lComppk property.
     * 
     */
    public int getLComppk() {
        return lComppk;
    }

    /**
     * Sets the value of the lComppk property.
     * 
     */
    public void setLComppk(int value) {
        this.lComppk = value;
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
     * Gets the value of the szDeptid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzDeptid() {
        return szDeptid;
    }

    /**
     * Sets the value of the szDeptid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzDeptid(String value) {
        this.szDeptid = value;
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
     * Gets the value of the szCompid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzCompid() {
        return szCompid;
    }

    /**
     * Sets the value of the szCompid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzCompid(String value) {
        this.szCompid = value;
    }

    /**
     * Gets the value of the szPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPassword() {
        return szPassword;
    }

    /**
     * Sets the value of the szPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPassword(String value) {
        this.szPassword = value;
    }

    /**
     * Gets the value of the lDeptpri property.
     * 
     */
    public int getLDeptpri() {
        return lDeptpri;
    }

    /**
     * Sets the value of the lDeptpri property.
     * 
     */
    public void setLDeptpri(int value) {
        this.lDeptpri = value;
    }

    /**
     * Gets the value of the lPriserver property.
     * 
     */
    public int getLPriserver() {
        return lPriserver;
    }

    /**
     * Sets the value of the lPriserver property.
     * 
     */
    public void setLPriserver(int value) {
        this.lPriserver = value;
    }

    /**
     * Gets the value of the lAltservers property.
     * 
     */
    public int getLAltservers() {
        return lAltservers;
    }

    /**
     * Sets the value of the lAltservers property.
     * 
     */
    public void setLAltservers(int value) {
        this.lAltservers = value;
    }

    /**
     * Gets the value of the szStdcc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzStdcc() {
        return szStdcc;
    }

    /**
     * Sets the value of the szStdcc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzStdcc(String value) {
        this.szStdcc = value;
    }

    /**
     * Gets the value of the jobid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobid() {
        return jobid;
    }

    /**
     * Sets the value of the jobid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobid(String value) {
        this.jobid = value;
    }

}
