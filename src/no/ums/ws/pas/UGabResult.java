
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UGabResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UGabResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="match" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="region" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lon" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="lat" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="type" type="{http://ums.no/ws/pas/}GABTYPE"/>
 *         &lt;element name="rect" type="{http://ums.no/ws/pas/}UBoundingRect" minOccurs="0"/>
 *         &lt;element name="scope" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UGabResult", propOrder = {
    "match",
    "name",
    "region",
    "postno",
    "lon",
    "lat",
    "type",
    "rect",
    "scope"
})
@XmlSeeAlso({
    UGabResultFromPoint.class
})
public class UGabResult {

    protected float match;
    protected String name;
    protected String region;
    protected String postno;
    protected double lon;
    protected double lat;
    @XmlElement(required = true)
    protected GABTYPE type;
    protected UBoundingRect rect;
    protected int scope;

    /**
     * Gets the value of the match property.
     * 
     */
    public float getMatch() {
        return match;
    }

    /**
     * Sets the value of the match property.
     * 
     */
    public void setMatch(float value) {
        this.match = value;
    }

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
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegion(String value) {
        this.region = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link GABTYPE }
     *     
     */
    public GABTYPE getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link GABTYPE }
     *     
     */
    public void setType(GABTYPE value) {
        this.type = value;
    }

    /**
     * Gets the value of the rect property.
     * 
     * @return
     *     possible object is
     *     {@link UBoundingRect }
     *     
     */
    public UBoundingRect getRect() {
        return rect;
    }

    /**
     * Sets the value of the rect property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBoundingRect }
     *     
     */
    public void setRect(UBoundingRect value) {
        this.rect = value;
    }

    /**
     * Gets the value of the scope property.
     * 
     */
    public int getScope() {
        return scope;
    }

    /**
     * Sets the value of the scope property.
     * 
     */
    public void setScope(int value) {
        this.scope = value;
    }

}
