
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPASMap complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPASMap">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mapinfo" type="{http://ums.no/ws/pas/}UMapInfo" minOccurs="0"/>
 *         &lt;element name="image" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPASMap", propOrder = {
    "mapinfo",
    "image"
})
public class UPASMap {

    protected UMapInfo mapinfo;
    protected byte[] image;

    /**
     * Gets the value of the mapinfo property.
     * 
     * @return
     *     possible object is
     *     {@link UMapInfo }
     *     
     */
    public UMapInfo getMapinfo() {
        return mapinfo;
    }

    /**
     * Sets the value of the mapinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMapInfo }
     *     
     */
    public void setMapinfo(UMapInfo value) {
        this.mapinfo = value;
    }

    /**
     * Gets the value of the image property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Sets the value of the image property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setImage(byte[] value) {
        this.image = ((byte[]) value);
    }

}
