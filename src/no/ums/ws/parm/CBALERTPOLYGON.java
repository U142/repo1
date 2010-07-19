
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_ALERT_POLYGON complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_ALERT_POLYGON">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}CB_SEND_BASE">
 *       &lt;sequence>
 *         &lt;element name="alertpolygon" type="{http://ums.no/ws/parm/}UPolygon" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="l_validity" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_ALERT_POLYGON", propOrder = {
    "alertpolygon"
})
public class CBALERTPOLYGON
    extends CBSENDBASE
{

    protected UPolygon alertpolygon;
    @XmlAttribute(name = "l_validity", required = true)
    protected int lValidity;

    /**
     * Gets the value of the alertpolygon property.
     * 
     * @return
     *     possible object is
     *     {@link UPolygon }
     *     
     */
    public UPolygon getAlertpolygon() {
        return alertpolygon;
    }

    /**
     * Sets the value of the alertpolygon property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPolygon }
     *     
     */
    public void setAlertpolygon(UPolygon value) {
        this.alertpolygon = value;
    }

    /**
     * Gets the value of the lValidity property.
     * 
     */
    public int getLValidity() {
        return lValidity;
    }

    /**
     * Sets the value of the lValidity property.
     * 
     */
    public void setLValidity(int value) {
        this.lValidity = value;
    }

}
