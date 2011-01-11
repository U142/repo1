
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UEllipseDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UEllipseDef">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="center" type="{http://ums.no/ws/pas/}UMapPoint" minOccurs="0"/>
 *         &lt;element name="radius" type="{http://ums.no/ws/pas/}UMapPoint" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UEllipseDef", propOrder = {
    "center",
    "radius"
})
public class UEllipseDef {

    protected UMapPoint center;
    protected UMapPoint radius;

    /**
     * Gets the value of the center property.
     * 
     * @return
     *     possible object is
     *     {@link UMapPoint }
     *     
     */
    public UMapPoint getCenter() {
        return center;
    }

    /**
     * Sets the value of the center property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMapPoint }
     *     
     */
    public void setCenter(UMapPoint value) {
        this.center = value;
    }

    /**
     * Gets the value of the radius property.
     * 
     * @return
     *     possible object is
     *     {@link UMapPoint }
     *     
     */
    public UMapPoint getRadius() {
        return radius;
    }

    /**
     * Sets the value of the radius property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMapPoint }
     *     
     */
    public void setRadius(UMapPoint value) {
        this.radius = value;
    }

}
