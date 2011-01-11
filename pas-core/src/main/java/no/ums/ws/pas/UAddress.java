
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UAddress complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UAddress">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/}UAddressBasics">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="houseno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="letter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postarea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="region" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="bday" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mobile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="bno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="importid" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="streetid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="xycode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="municipalid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UAddress", propOrder = {
    "name",
    "address",
    "houseno",
    "letter",
    "postno",
    "postarea",
    "region",
    "bday",
    "number",
    "mobile",
    "gno",
    "bno",
    "importid",
    "streetid",
    "xycode",
    "municipalid"
})
public class UAddress
    extends UAddressBasics
{

    protected String name;
    protected String address;
    protected int houseno;
    protected String letter;
    protected String postno;
    protected String postarea;
    protected int region;
    protected String bday;
    protected String number;
    protected String mobile;
    protected int gno;
    protected int bno;
    protected long importid;
    protected int streetid;
    protected String xycode;
    protected String municipalid;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the houseno property.
     * 
     */
    public int getHouseno() {
        return houseno;
    }

    /**
     * Sets the value of the houseno property.
     * 
     */
    public void setHouseno(int value) {
        this.houseno = value;
    }

    /**
     * Gets the value of the letter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLetter() {
        return letter;
    }

    /**
     * Sets the value of the letter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLetter(String value) {
        this.letter = value;
    }

    /**
     * Gets the value of the postno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostno() {
        return postno;
    }

    /**
     * Sets the value of the postno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostno(String value) {
        this.postno = value;
    }

    /**
     * Gets the value of the postarea property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostarea() {
        return postarea;
    }

    /**
     * Sets the value of the postarea property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostarea(String value) {
        this.postarea = value;
    }

    /**
     * Gets the value of the region property.
     * 
     */
    public int getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     */
    public void setRegion(int value) {
        this.region = value;
    }

    /**
     * Gets the value of the bday property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBday() {
        return bday;
    }

    /**
     * Sets the value of the bday property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBday(String value) {
        this.bday = value;
    }

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the mobile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Sets the value of the mobile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobile(String value) {
        this.mobile = value;
    }

    /**
     * Gets the value of the gno property.
     * 
     */
    public int getGno() {
        return gno;
    }

    /**
     * Sets the value of the gno property.
     * 
     */
    public void setGno(int value) {
        this.gno = value;
    }

    /**
     * Gets the value of the bno property.
     * 
     */
    public int getBno() {
        return bno;
    }

    /**
     * Sets the value of the bno property.
     * 
     */
    public void setBno(int value) {
        this.bno = value;
    }

    /**
     * Gets the value of the importid property.
     * 
     */
    public long getImportid() {
        return importid;
    }

    /**
     * Sets the value of the importid property.
     * 
     */
    public void setImportid(long value) {
        this.importid = value;
    }

    /**
     * Gets the value of the streetid property.
     * 
     */
    public int getStreetid() {
        return streetid;
    }

    /**
     * Sets the value of the streetid property.
     * 
     */
    public void setStreetid(int value) {
        this.streetid = value;
    }

    /**
     * Gets the value of the xycode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXycode() {
        return xycode;
    }

    /**
     * Sets the value of the xycode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXycode(String value) {
        this.xycode = value;
    }

    /**
     * Gets the value of the municipalid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMunicipalid() {
        return municipalid;
    }

    /**
     * Sets the value of the municipalid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMunicipalid(String value) {
        this.municipalid = value;
    }

}
