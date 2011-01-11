
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UGISSENDING complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UGISSENDING">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}UMAPSENDING">
 *       &lt;sequence>
 *         &lt;element name="gis" type="{http://ums.no/ws/parm/}ArrayOfUGisRecord" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UGISSENDING", propOrder = {
    "gis"
})
public class UGISSENDING
    extends UMAPSENDING
{

    protected ArrayOfUGisRecord gis;

    /**
     * Gets the value of the gis property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUGisRecord }
     *     
     */
    public ArrayOfUGisRecord getGis() {
        return gis;
    }

    /**
     * Sets the value of the gis property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUGisRecord }
     *     
     */
    public void setGis(ArrayOfUGisRecord value) {
        this.gis = value;
    }

}
