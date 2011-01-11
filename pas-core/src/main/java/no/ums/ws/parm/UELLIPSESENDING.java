
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UELLIPSESENDING complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UELLIPSESENDING">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}UMAPSENDING">
 *       &lt;sequence>
 *         &lt;element name="ellipse" type="{http://ums.no/ws/parm/}UEllipseDef" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UELLIPSESENDING", propOrder = {
    "ellipse"
})
public class UELLIPSESENDING
    extends UMAPSENDING
{

    protected UEllipseDef ellipse;

    /**
     * Gets the value of the ellipse property.
     * 
     * @return
     *     possible object is
     *     {@link UEllipseDef }
     *     
     */
    public UEllipseDef getEllipse() {
        return ellipse;
    }

    /**
     * Sets the value of the ellipse property.
     * 
     * @param value
     *     allowed object is
     *     {@link UEllipseDef }
     *     
     */
    public void setEllipse(UEllipseDef value) {
        this.ellipse = value;
    }

}
