
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UMapInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UMapInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/}UMapBounds">
 *       &lt;sequence>
 *         &lt;element name="width" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="height" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="portrayal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mapstatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maptime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UMapInfo", propOrder = {
    "width",
    "height",
    "portrayal",
    "mapstatus",
    "maptime"
})
@XmlSeeAlso({
    UMapAddressParams.class,
    UMapInfoLayer.class
})
public class UMapInfo
    extends UMapBounds
{

    protected int width;
    protected int height;
    protected String portrayal;
    protected String mapstatus;
    protected String maptime;

    /**
     * Gets the value of the width property.
     * 
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     */
    public void setWidth(int value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     */
    public void setHeight(int value) {
        this.height = value;
    }

    /**
     * Gets the value of the portrayal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortrayal() {
        return portrayal;
    }

    /**
     * Sets the value of the portrayal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortrayal(String value) {
        this.portrayal = value;
    }

    /**
     * Gets the value of the mapstatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapstatus() {
        return mapstatus;
    }

    /**
     * Sets the value of the mapstatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapstatus(String value) {
        this.mapstatus = value;
    }

    /**
     * Gets the value of the maptime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaptime() {
        return maptime;
    }

    /**
     * Sets the value of the maptime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaptime(String value) {
        this.maptime = value;
    }

}
