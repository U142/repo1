
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBACOUNTRY complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBACOUNTRY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_cc" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_iso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weightpoint" type="{http://ums.no/ws/parm/}UMapPoint" minOccurs="0"/>
 *         &lt;element name="weightpoint_screen" type="{http://ums.no/ws/parm/}UMapPoint" minOccurs="0"/>
 *         &lt;element name="bounds" type="{http://ums.no/ws/parm/}UMapBounds" minOccurs="0"/>
 *         &lt;element name="l_continentpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_iso_numeric" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_touristcount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_oldestupdate" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_newestupdate" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="operators" type="{http://ums.no/ws/parm/}ArrayOfUTOURISTCOUNT" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBACOUNTRY", propOrder = {
    "lCc",
    "szIso",
    "szName",
    "weightpoint",
    "weightpointScreen",
    "bounds",
    "lContinentpk",
    "lIsoNumeric",
    "nTouristcount",
    "nOldestupdate",
    "nNewestupdate",
    "operators"
})
public class ULBACOUNTRY {

    @XmlElement(name = "l_cc")
    protected int lCc;
    @XmlElement(name = "sz_iso")
    protected String szIso;
    @XmlElement(name = "sz_name")
    protected String szName;
    protected UMapPoint weightpoint;
    @XmlElement(name = "weightpoint_screen")
    protected UMapPoint weightpointScreen;
    protected UMapBounds bounds;
    @XmlElement(name = "l_continentpk")
    protected int lContinentpk;
    @XmlElement(name = "l_iso_numeric")
    protected int lIsoNumeric;
    @XmlElement(name = "n_touristcount")
    protected int nTouristcount;
    @XmlElement(name = "n_oldestupdate")
    protected long nOldestupdate;
    @XmlElement(name = "n_newestupdate")
    protected long nNewestupdate;
    protected ArrayOfUTOURISTCOUNT operators;

    /**
     * Gets the value of the lCc property.
     * 
     */
    public int getLCc() {
        return lCc;
    }

    /**
     * Sets the value of the lCc property.
     * 
     */
    public void setLCc(int value) {
        this.lCc = value;
    }

    /**
     * Gets the value of the szIso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzIso() {
        return szIso;
    }

    /**
     * Sets the value of the szIso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzIso(String value) {
        this.szIso = value;
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
     * Gets the value of the lIsoNumeric property.
     * 
     */
    public int getLIsoNumeric() {
        return lIsoNumeric;
    }

    /**
     * Sets the value of the lIsoNumeric property.
     * 
     */
    public void setLIsoNumeric(int value) {
        this.lIsoNumeric = value;
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
     * Gets the value of the nOldestupdate property.
     * 
     */
    public long getNOldestupdate() {
        return nOldestupdate;
    }

    /**
     * Sets the value of the nOldestupdate property.
     * 
     */
    public void setNOldestupdate(long value) {
        this.nOldestupdate = value;
    }

    /**
     * Gets the value of the nNewestupdate property.
     * 
     */
    public long getNNewestupdate() {
        return nNewestupdate;
    }

    /**
     * Sets the value of the nNewestupdate property.
     * 
     */
    public void setNNewestupdate(long value) {
        this.nNewestupdate = value;
    }

    /**
     * Gets the value of the operators property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUTOURISTCOUNT }
     *     
     */
    public ArrayOfUTOURISTCOUNT getOperators() {
        return operators;
    }

    /**
     * Sets the value of the operators property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUTOURISTCOUNT }
     *     
     */
    public void setOperators(ArrayOfUTOURISTCOUNT value) {
        this.operators = value;
    }

}
