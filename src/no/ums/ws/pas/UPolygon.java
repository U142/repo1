
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPolygon complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPolygon">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/}UShape">
 *       &lt;sequence>
 *         &lt;element name="m_array_polypoints" type="{http://ums.no/ws/pas/}ArrayOfUPolypoint" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPolygon", propOrder = {
    "mArrayPolypoints"
})
public class UPolygon
    extends UShape
{

    @XmlElement(name = "m_array_polypoints")
    protected ArrayOfUPolypoint mArrayPolypoints;

    /**
     * Gets the value of the mArrayPolypoints property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUPolypoint }
     *     
     */
    public ArrayOfUPolypoint getMArrayPolypoints() {
        return mArrayPolypoints;
    }

    /**
     * Sets the value of the mArrayPolypoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUPolypoint }
     *     
     */
    public void setMArrayPolypoints(ArrayOfUPolypoint value) {
        this.mArrayPolypoints = value;
    }

}
