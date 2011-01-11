
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PAOBJECT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PAOBJECT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parmop" type="{http://ums.no/ws/parm/admin/}PARMOPERATION"/>
 *         &lt;element name="l_temppk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_objectpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_deptpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_importpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_categorypk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_parent" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_postno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_place" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_metadata" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="b_isobjectfolder" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="l_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="m_shape" type="{http://ums.no/ws/parm/admin/}UPolygon" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PAOBJECT", propOrder = {
    "parmop",
    "lTemppk",
    "lObjectpk",
    "lDeptpk",
    "lImportpk",
    "szName",
    "szDescription",
    "lCategorypk",
    "lParent",
    "szAddress",
    "szPostno",
    "szPlace",
    "szPhone",
    "szMetadata",
    "bIsobjectfolder",
    "lTimestamp",
    "mShape"
})
public class PAOBJECT {

    @XmlElement(required = true)
    protected PARMOPERATION parmop;
    @XmlElement(name = "l_temppk")
    protected long lTemppk;
    @XmlElement(name = "l_objectpk")
    protected long lObjectpk;
    @XmlElement(name = "l_deptpk")
    protected long lDeptpk;
    @XmlElement(name = "l_importpk")
    protected long lImportpk;
    @XmlElement(name = "sz_name")
    protected String szName;
    @XmlElement(name = "sz_description")
    protected String szDescription;
    @XmlElement(name = "l_categorypk")
    protected long lCategorypk;
    @XmlElement(name = "l_parent")
    protected long lParent;
    @XmlElement(name = "sz_address")
    protected String szAddress;
    @XmlElement(name = "sz_postno")
    protected String szPostno;
    @XmlElement(name = "sz_place")
    protected String szPlace;
    @XmlElement(name = "sz_phone")
    protected String szPhone;
    @XmlElement(name = "sz_metadata")
    protected String szMetadata;
    @XmlElement(name = "b_isobjectfolder")
    protected boolean bIsobjectfolder;
    @XmlElement(name = "l_timestamp")
    protected long lTimestamp;
    @XmlElement(name = "m_shape")
    protected UPolygon mShape;

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
     * Gets the value of the lObjectpk property.
     * 
     */
    public long getLObjectpk() {
        return lObjectpk;
    }

    /**
     * Sets the value of the lObjectpk property.
     * 
     */
    public void setLObjectpk(long value) {
        this.lObjectpk = value;
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
     * Gets the value of the lImportpk property.
     * 
     */
    public long getLImportpk() {
        return lImportpk;
    }

    /**
     * Sets the value of the lImportpk property.
     * 
     */
    public void setLImportpk(long value) {
        this.lImportpk = value;
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
     * Gets the value of the lCategorypk property.
     * 
     */
    public long getLCategorypk() {
        return lCategorypk;
    }

    /**
     * Sets the value of the lCategorypk property.
     * 
     */
    public void setLCategorypk(long value) {
        this.lCategorypk = value;
    }

    /**
     * Gets the value of the lParent property.
     * 
     */
    public long getLParent() {
        return lParent;
    }

    /**
     * Sets the value of the lParent property.
     * 
     */
    public void setLParent(long value) {
        this.lParent = value;
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
     * Gets the value of the szPlace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPlace() {
        return szPlace;
    }

    /**
     * Sets the value of the szPlace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPlace(String value) {
        this.szPlace = value;
    }

    /**
     * Gets the value of the szPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPhone() {
        return szPhone;
    }

    /**
     * Sets the value of the szPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPhone(String value) {
        this.szPhone = value;
    }

    /**
     * Gets the value of the szMetadata property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzMetadata() {
        return szMetadata;
    }

    /**
     * Sets the value of the szMetadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzMetadata(String value) {
        this.szMetadata = value;
    }

    /**
     * Gets the value of the bIsobjectfolder property.
     * 
     */
    public boolean isBIsobjectfolder() {
        return bIsobjectfolder;
    }

    /**
     * Sets the value of the bIsobjectfolder property.
     * 
     */
    public void setBIsobjectfolder(boolean value) {
        this.bIsobjectfolder = value;
    }

    /**
     * Gets the value of the lTimestamp property.
     * 
     */
    public long getLTimestamp() {
        return lTimestamp;
    }

    /**
     * Sets the value of the lTimestamp property.
     * 
     */
    public void setLTimestamp(long value) {
        this.lTimestamp = value;
    }

    /**
     * Gets the value of the mShape property.
     * 
     * @return
     *     possible object is
     *     {@link UPolygon }
     *     
     */
    public UPolygon getMShape() {
        return mShape;
    }

    /**
     * Sets the value of the mShape property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPolygon }
     *     
     */
    public void setMShape(UPolygon value) {
        this.mShape = value;
    }

}
