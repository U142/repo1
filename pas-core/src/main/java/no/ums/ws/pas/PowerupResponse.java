
package no.ums.ws.pas;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PowerupResult" type="{http://ums.no/ws/pas/}UPOWERUP_RESPONSE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "powerupResult"
})
@XmlRootElement(name = "PowerupResponse")
public class PowerupResponse {

    @XmlElement(name = "PowerupResult", required = true)
    protected UPOWERUPRESPONSE powerupResult;

    /**
     * Gets the value of the powerupResult property.
     * 
     * @return
     *     possible object is
     *     {@link UPOWERUPRESPONSE }
     *     
     */
    public UPOWERUPRESPONSE getPowerupResult() {
        return powerupResult;
    }

    /**
     * Sets the value of the powerupResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPOWERUPRESPONSE }
     *     
     */
    public void setPowerupResult(UPOWERUPRESPONSE value) {
        this.powerupResult = value;
    }

}
