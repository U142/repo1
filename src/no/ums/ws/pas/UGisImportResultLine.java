
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UGisImportResultLine complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UGisImportResultLine">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="municipalid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="streetid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="houseno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="letter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="namefilter1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="namefilter2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_linenumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="b_isvalid" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="list" type="{http://ums.no/ws/pas/}UAddressList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UGisImportResultLine", propOrder = {
    "municipalid",
    "streetid",
    "houseno",
    "letter",
    "namefilter1",
    "namefilter2",
    "nLinenumber",
    "bIsvalid",
    "list"
})
public class UGisImportResultLine {

    protected String municipalid;
    protected String streetid;
    protected String houseno;
    protected String letter;
    protected String namefilter1;
    protected String namefilter2;
    @XmlElement(name = "n_linenumber")
    protected int nLinenumber;
    @XmlElement(name = "b_isvalid")
    protected boolean bIsvalid;
    protected UAddressList list;

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

    /**
     * Gets the value of the streetid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreetid() {
        return streetid;
    }

    /**
     * Sets the value of the streetid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreetid(String value) {
        this.streetid = value;
    }

    /**
     * Gets the value of the houseno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHouseno() {
        return houseno;
    }

    /**
     * Sets the value of the houseno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHouseno(String value) {
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
     * Gets the value of the namefilter1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamefilter1() {
        return namefilter1;
    }

    /**
     * Sets the value of the namefilter1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamefilter1(String value) {
        this.namefilter1 = value;
    }

    /**
     * Gets the value of the namefilter2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamefilter2() {
        return namefilter2;
    }

    /**
     * Sets the value of the namefilter2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamefilter2(String value) {
        this.namefilter2 = value;
    }

    /**
     * Gets the value of the nLinenumber property.
     * 
     */
    public int getNLinenumber() {
        return nLinenumber;
    }

    /**
     * Sets the value of the nLinenumber property.
     * 
     */
    public void setNLinenumber(int value) {
        this.nLinenumber = value;
    }

    /**
     * Gets the value of the bIsvalid property.
     * 
     */
    public boolean isBIsvalid() {
        return bIsvalid;
    }

    /**
     * Sets the value of the bIsvalid property.
     * 
     */
    public void setBIsvalid(boolean value) {
        this.bIsvalid = value;
    }

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link UAddressList }
     *     
     */
    public UAddressList getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAddressList }
     *     
     */
    public void setList(UAddressList value) {
        this.list = value;
    }

}
