
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="e" type="{http://ums.no/ws/parm/admin/}UEllipse" minOccurs="0"/>
 *         &lt;element name="p" type="{http://ums.no/ws/parm/admin/}UPolygon" minOccurs="0"/>
 *         &lt;element name="s" type="{http://ums.no/ws/parm/admin/}UGeminiStreet" minOccurs="0"/>
 *         &lt;element name="l" type="{http://ums.no/ws/parm/admin/}ULocationBasedAlert" minOccurs="0"/>
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
    "e",
    "p",
    "s",
    "l"
})
@XmlRootElement(name = "Defines")
public class Defines {

    protected UEllipse e;
    protected UPolygon p;
    protected UGeminiStreet s;
    protected ULocationBasedAlert l;

    /**
     * Gets the value of the e property.
     * 
     * @return
     *     possible object is
     *     {@link UEllipse }
     *     
     */
    public UEllipse getE() {
        return e;
    }

    /**
     * Sets the value of the e property.
     * 
     * @param value
     *     allowed object is
     *     {@link UEllipse }
     *     
     */
    public void setE(UEllipse value) {
        this.e = value;
    }

    /**
     * Gets the value of the p property.
     * 
     * @return
     *     possible object is
     *     {@link UPolygon }
     *     
     */
    public UPolygon getP() {
        return p;
    }

    /**
     * Sets the value of the p property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPolygon }
     *     
     */
    public void setP(UPolygon value) {
        this.p = value;
    }

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link UGeminiStreet }
     *     
     */
    public UGeminiStreet getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link UGeminiStreet }
     *     
     */
    public void setS(UGeminiStreet value) {
        this.s = value;
    }

    /**
     * Gets the value of the l property.
     * 
     * @return
     *     possible object is
     *     {@link ULocationBasedAlert }
     *     
     */
    public ULocationBasedAlert getL() {
        return l;
    }

    /**
     * Sets the value of the l property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULocationBasedAlert }
     *     
     */
    public void setL(ULocationBasedAlert value) {
        this.l = value;
    }

}
