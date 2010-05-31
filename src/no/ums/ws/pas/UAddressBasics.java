
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UAddressBasics complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UAddressBasics">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kondmid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lon" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="lat" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="hasfixed" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="hasmobile" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="bedrift" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="arrayindex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UAddressBasics", propOrder = {
    "kondmid",
    "lon",
    "lat",
    "hasfixed",
    "hasmobile",
    "bedrift",
    "arrayindex"
})
@XmlSeeAlso({
    UAddress.class
})
public class UAddressBasics {

    protected String kondmid;
    protected double lon;
    protected double lat;
    protected int hasfixed;
    protected int hasmobile;
    protected int bedrift;
    protected int arrayindex;

    /**
     * Gets the value of the kondmid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKondmid() {
        return kondmid;
    }

    /**
     * Sets the value of the kondmid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKondmid(String value) {
        this.kondmid = value;
    }

    /**
     * Gets the value of the lon property.
     * 
     */
    public double getLon() {
        return lon;
    }

    /**
     * Sets the value of the lon property.
     * 
     */
    public void setLon(double value) {
        this.lon = value;
    }

    /**
     * Gets the value of the lat property.
     * 
     */
    public double getLat() {
        return lat;
    }

    /**
     * Sets the value of the lat property.
     * 
     */
    public void setLat(double value) {
        this.lat = value;
    }

    /**
     * Gets the value of the hasfixed property.
     * 
     */
    public int getHasfixed() {
        return hasfixed;
    }

    /**
     * Sets the value of the hasfixed property.
     * 
     */
    public void setHasfixed(int value) {
        this.hasfixed = value;
    }

    /**
     * Gets the value of the hasmobile property.
     * 
     */
    public int getHasmobile() {
        return hasmobile;
    }

    /**
     * Sets the value of the hasmobile property.
     * 
     */
    public void setHasmobile(int value) {
        this.hasmobile = value;
    }

    /**
     * Gets the value of the bedrift property.
     * 
     */
    public int getBedrift() {
        return bedrift;
    }

    /**
     * Sets the value of the bedrift property.
     * 
     */
    public void setBedrift(int value) {
        this.bedrift = value;
    }

    /**
     * Gets the value of the arrayindex property.
     * 
     */
    public int getArrayindex() {
        return arrayindex;
    }

    /**
     * Sets the value of the arrayindex property.
     * 
     */
    public void setArrayindex(int value) {
        this.arrayindex = value;
    }

}
