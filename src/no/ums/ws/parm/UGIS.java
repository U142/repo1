
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UGIS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UGIS">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}UShape">
 *       &lt;sequence>
 *         &lt;element name="m_gis" type="{http://ums.no/ws/parm/}ArrayOfUGisRecord" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UGIS", propOrder = {
    "mGis"
})
public class UGIS
    extends UShape
{

    @XmlElement(name = "m_gis")
    protected ArrayOfUGisRecord mGis;

    /**
     * Gets the value of the mGis property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUGisRecord }
     *     
     */
    public ArrayOfUGisRecord getMGis() {
        return mGis;
    }

    /**
     * Sets the value of the mGis property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUGisRecord }
     *     
     */
    public void setMGis(ArrayOfUGisRecord value) {
        this.mGis = value;
    }

}
