
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBACONTINENT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBACONTINENT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_continentpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_short" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weightpoint" type="{http://ums.no/ws/pas/tas}UMapPoint" minOccurs="0"/>
 *         &lt;element name="weightpoint_screen" type="{http://ums.no/ws/pas/tas}UMapPoint" minOccurs="0"/>
 *         &lt;element name="bounds" type="{http://ums.no/ws/pas/tas}UMapBounds" minOccurs="0"/>
 *         &lt;element name="countries" type="{http://ums.no/ws/pas/tas}ArrayOfULBACOUNTRY" minOccurs="0"/>
 *         &lt;element name="n_touristcount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_lastupdate" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBACONTINENT", propOrder = {
    "lContinentpk",
    "szShort",
    "szName",
    "weightpoint",
    "weightpointScreen",
    "bounds",
    "countries",
    "nTouristcount",
    "nLastupdate"
})
public class ULBACONTINENT {

    @XmlElement(name = "l_continentpk")
    protected int lContinentpk;
    @XmlElement(name = "sz_short")
    protected String szShort;
    @XmlElement(name = "sz_name")
    protected String szName;
    protected UMapPoint weightpoint;
    @XmlElement(name = "weightpoint_screen")
    protected UMapPoint weightpointScreen;
    protected UMapBounds bounds;
    protected ArrayOfULBACOUNTRY countries;
    @XmlElement(name = "n_touristcount")
    protected int nTouristcount;
    @XmlElement(name = "n_lastupdate")
    protected long nLastupdate;

    /**
     * Gets the value of the lContinentpk property.
     * 
     */
    public int getLContinentpk() {
        return lContinentpk;
    }

    /**
     * Sets the value of the lContinentpk property.
     * 
     */
    public void setLContinentpk(int value) {
        this.lContinentpk = value;
    }

    /**
     * Gets the value of the szShort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzShort() {
        return szShort;
    }

    /**
     * Sets the value of the szShort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzShort(String value) {
        this.szShort = value;
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
     * Gets the value of the weightpoint property.
     * 
     * @return
     *     possible object is
     *     {@link UMapPoint }
     *     
     */
    public UMapPoint getWeightpoint() {
        return weightpoint;
    }

    /**
     * Sets the value of the weightpoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMapPoint }
     *     
     */
    public void setWeightpoint(UMapPoint value) {
        this.weightpoint = value;
    }

    /**
     * Gets the value of the weightpointScreen property.
     * 
     * @return
     *     possible object is
     *     {@link UMapPoint }
     *     
     */
    public UMapPoint getWeightpointScreen() {
        return weightpointScreen;
    }

    /**
     * Sets the value of the weightpointScreen property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMapPoint }
     *     
     */
    public void setWeightpointScreen(UMapPoint value) {
        this.weightpointScreen = value;
    }

    /**
     * Gets the value of the bounds property.
     * 
     * @return
     *     possible object is
     *     {@link UMapBounds }
     *     
     */
    public UMapBounds getBounds() {
        return bounds;
    }

    /**
     * Sets the value of the bounds property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMapBounds }
     *     
     */
    public void setBounds(UMapBounds value) {
        this.bounds = value;
    }

    /**
     * Gets the value of the countries property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBACOUNTRY }
     *     
     */
    public ArrayOfULBACOUNTRY getCountries() {
        return countries;
    }

    /**
     * Sets the value of the countries property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBACOUNTRY }
     *     
     */
    public void setCountries(ArrayOfULBACOUNTRY value) {
        this.countries = value;
    }

    /**
     * Gets the value of the nTouristcount property.
     * 
     */
    public int getNTouristcount() {
        return nTouristcount;
    }

    /**
     * Sets the value of the nTouristcount property.
     * 
     */
    public void setNTouristcount(int value) {
        this.nTouristcount = value;
    }

    /**
     * Gets the value of the nLastupdate property.
     * 
     */
    public long getNLastupdate() {
        return nLastupdate;
    }

    /**
     * Sets the value of the nLastupdate property.
     * 
     */
    public void setNLastupdate(long value) {
        this.nLastupdate = value;
    }

}
