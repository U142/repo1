
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_ALERT_PLMN complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_ALERT_PLMN">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}CB_SEND_BASE">
 *       &lt;sequence>
 *         &lt;element name="alertplmn" type="{http://ums.no/ws/parm/}UPLMN" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_ALERT_PLMN", propOrder = {
    "alertplmn"
})
public class CBALERTPLMN
    extends CBSENDBASE
{

    protected UPLMN alertplmn;

    /**
     * Gets the value of the alertplmn property.
     * 
     * @return
     *     possible object is
     *     {@link UPLMN }
     *     
     */
    public UPLMN getAlertplmn() {
        return alertplmn;
    }

    /**
     * Sets the value of the alertplmn property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPLMN }
     *     
     */
    public void setAlertplmn(UPLMN value) {
        this.alertplmn = value;
    }

}
