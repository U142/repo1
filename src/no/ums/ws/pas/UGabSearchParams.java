
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UGabSearchParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UGabSearchParams">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sz_uid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_pwd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_sort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_language" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_no" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_postno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_postarea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_region" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UGabSearchParams", propOrder = {
    "szUid",
    "szPwd",
    "nCount",
    "nSort",
    "szLanguage",
    "szAddress",
    "szNo",
    "szPostno",
    "szPostarea",
    "szRegion",
    "szCountry"
})
public class UGabSearchParams {

    @XmlElement(name = "sz_uid")
    protected String szUid;
    @XmlElement(name = "sz_pwd")
    protected String szPwd;
    @XmlElement(name = "n_count")
    protected int nCount;
    @XmlElement(name = "n_sort")
    protected int nSort;
    @XmlElement(name = "sz_language")
    protected String szLanguage;
    @XmlElement(name = "sz_address")
    protected String szAddress;
    @XmlElement(name = "sz_no")
    protected String szNo;
    @XmlElement(name = "sz_postno")
    protected String szPostno;
    @XmlElement(name = "sz_postarea")
    protected String szPostarea;
    @XmlElement(name = "sz_region")
    protected String szRegion;
    @XmlElement(name = "sz_country")
    protected String szCountry;

    /**
     * Gets the value of the szUid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzUid() {
        return szUid;
    }

    /**
     * Sets the value of the szUid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzUid(String value) {
        this.szUid = value;
    }

    /**
     * Gets the value of the szPwd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPwd() {
        return szPwd;
    }

    /**
     * Sets the value of the szPwd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPwd(String value) {
        this.szPwd = value;
    }

    /**
     * Gets the value of the nCount property.
     * 
     */
    public int getNCount() {
        return nCount;
    }

    /**
     * Sets the value of the nCount property.
     * 
     */
    public void setNCount(int value) {
        this.nCount = value;
    }

    /**
     * Gets the value of the nSort property.
     * 
     */
    public int getNSort() {
        return nSort;
    }

    /**
     * Sets the value of the nSort property.
     * 
     */
    public void setNSort(int value) {
        this.nSort = value;
    }

    /**
     * Gets the value of the szLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzLanguage() {
        return szLanguage;
    }

    /**
     * Sets the value of the szLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzLanguage(String value) {
        this.szLanguage = value;
    }

    /**
     * Gets the value of the szAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzAddress() {
        return szAddress;
    }

    /**
     * Sets the value of the szAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzAddress(String value) {
        this.szAddress = value;
    }

    /**
     * Gets the value of the szNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzNo() {
        return szNo;
    }

    /**
     * Sets the value of the szNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzNo(String value) {
        this.szNo = value;
    }

    /**
     * Gets the value of the szPostno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPostno() {
        return szPostno;
    }

    /**
     * Sets the value of the szPostno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPostno(String value) {
        this.szPostno = value;
    }

    /**
     * Gets the value of the szPostarea property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPostarea() {
        return szPostarea;
    }

    /**
     * Sets the value of the szPostarea property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPostarea(String value) {
        this.szPostarea = value;
    }

    /**
     * Gets the value of the szRegion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzRegion() {
        return szRegion;
    }

    /**
     * Sets the value of the szRegion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzRegion(String value) {
        this.szRegion = value;
    }

    /**
     * Gets the value of the szCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzCountry() {
        return szCountry;
    }

    /**
     * Sets the value of the szCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzCountry(String value) {
        this.szCountry = value;
    }

}
