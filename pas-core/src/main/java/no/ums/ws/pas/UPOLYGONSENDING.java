
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPOLYGONSENDING complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPOLYGONSENDING">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/}UMAPSENDING">
 *       &lt;sequence>
 *         &lt;element name="polygonpoints" type="{http://ums.no/ws/pas/}ArrayOfUMapPoint" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPOLYGONSENDING", propOrder = {
    "polygonpoints"
})
public class UPOLYGONSENDING
    extends UMAPSENDING
{

    protected ArrayOfUMapPoint polygonpoints;

    /**
     * Gets the value of the polygonpoints property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUMapPoint }
     *     
     */
    public ArrayOfUMapPoint getPolygonpoints() {
        return polygonpoints;
    }

    /**
     * Sets the value of the polygonpoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUMapPoint }
     *     
     */
    public void setPolygonpoints(ArrayOfUMapPoint value) {
        this.polygonpoints = value;
    }

}
