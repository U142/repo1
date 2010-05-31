
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPASLOGON complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPASLOGON">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="f_granted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="l_userpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_comppk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_compid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_surname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_language" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nslookups" type="{http://ums.no/ws/pas/}ArrayOfUNSLOOKUP" minOccurs="0"/>
 *         &lt;element name="departments" type="{http://ums.no/ws/pas/}ArrayOfUDEPARTMENT" minOccurs="0"/>
 *         &lt;element name="uisettings" type="{http://ums.no/ws/pas/}UPASUISETTINGS" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPASLOGON", propOrder = {
    "fGranted",
    "lUserpk",
    "lComppk",
    "szUserid",
    "szCompid",
    "szName",
    "szSurname",
    "lLanguage",
    "nslookups",
    "departments",
    "uisettings"
})
public class UPASLOGON {

    @XmlElement(name = "f_granted")
    protected boolean fGranted;
    @XmlElement(name = "l_userpk")
    protected long lUserpk;
    @XmlElement(name = "l_comppk")
    protected int lComppk;
    @XmlElement(name = "sz_userid")
    protected String szUserid;
    @XmlElement(name = "sz_compid")
    protected String szCompid;
    @XmlElement(name = "sz_name")
    protected String szName;
    @XmlElement(name = "sz_surname")
    protected String szSurname;
    @XmlElement(name = "l_language")
    protected int lLanguage;
    protected ArrayOfUNSLOOKUP nslookups;
    protected ArrayOfUDEPARTMENT departments;
    protected UPASUISETTINGS uisettings;

    /**
     * Gets the value of the fGranted property.
     * 
     */
    public boolean isFGranted() {
        return fGranted;
    }

    /**
     * Sets the value of the fGranted property.
     * 
     */
    public void setFGranted(boolean value) {
        this.fGranted = value;
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
     * Gets the value of the szSurname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSurname() {
        return szSurname;
    }

    /**
     * Sets the value of the szSurname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSurname(String value) {
        this.szSurname = value;
    }

    /**
     * Gets the value of the lLanguage property.
     * 
     */
    public int getLLanguage() {
        return lLanguage;
    }

    /**
     * Sets the value of the lLanguage property.
     * 
     */
    public void setLLanguage(int value) {
        this.lLanguage = value;
    }

    /**
     * Gets the value of the nslookups property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUNSLOOKUP }
     *     
     */
    public ArrayOfUNSLOOKUP getNslookups() {
        return nslookups;
    }

    /**
     * Sets the value of the nslookups property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUNSLOOKUP }
     *     
     */
    public void setNslookups(ArrayOfUNSLOOKUP value) {
        this.nslookups = value;
    }

    /**
     * Gets the value of the departments property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUDEPARTMENT }
     *     
     */
    public ArrayOfUDEPARTMENT getDepartments() {
        return departments;
    }

    /**
     * Sets the value of the departments property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUDEPARTMENT }
     *     
     */
    public void setDepartments(ArrayOfUDEPARTMENT value) {
        this.departments = value;
    }

    /**
     * Gets the value of the uisettings property.
     * 
     * @return
     *     possible object is
     *     {@link UPASUISETTINGS }
     *     
     */
    public UPASUISETTINGS getUisettings() {
        return uisettings;
    }

    /**
     * Sets the value of the uisettings property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPASUISETTINGS }
     *     
     */
    public void setUisettings(UPASUISETTINGS value) {
        this.uisettings = value;
    }

}
