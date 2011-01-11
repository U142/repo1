
package no.ums.ws.parm;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_eventpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_comppk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_userpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_compid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_deptid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_function" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_scheddate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_schedtime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "lEventpk",
    "lComppk",
    "lDeptpk",
    "lUserpk",
    "szCompid",
    "szUserid",
    "szDeptid",
    "szPassword",
    "szFunction",
    "szScheddate",
    "szSchedtime"
})
@XmlRootElement(name = "ExecEvent")
public class ExecEvent {

    @XmlElement(name = "l_eventpk")
    protected long lEventpk;
    @XmlElement(name = "l_comppk")
    protected int lComppk;
    @XmlElement(name = "l_deptpk")
    protected int lDeptpk;
    @XmlElement(name = "l_userpk")
    protected long lUserpk;
    @XmlElement(name = "sz_compid")
    protected String szCompid;
    @XmlElement(name = "sz_userid")
    protected String szUserid;
    @XmlElement(name = "sz_deptid")
    protected String szDeptid;
    @XmlElement(name = "sz_password")
    protected String szPassword;
    @XmlElement(name = "sz_function")
    protected String szFunction;
    @XmlElement(name = "sz_scheddate")
    protected String szScheddate;
    @XmlElement(name = "sz_schedtime")
    protected String szSchedtime;

    /**
     * Gets the value of the lEventpk property.
     * 
     */
    public long getLEventpk() {
        return lEventpk;
    }

    /**
     * Sets the value of the lEventpk property.
     * 
     */
    public void setLEventpk(long value) {
        this.lEventpk = value;
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
     * Gets the value of the szScheddate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzScheddate() {
        return szScheddate;
    }

    /**
     * Sets the value of the szScheddate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzScheddate(String value) {
        this.szScheddate = value;
    }

    /**
     * Gets the value of the szSchedtime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSchedtime() {
        return szSchedtime;
    }

    /**
     * Sets the value of the szSchedtime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSchedtime(String value) {
        this.szSchedtime = value;
    }

}
