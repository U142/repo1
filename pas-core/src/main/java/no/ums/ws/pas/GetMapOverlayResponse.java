
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
 *         &lt;element name="GetMapOverlayResult" type="{http://ums.no/ws/pas/}UPASMap" minOccurs="0"/>
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
    "getMapOverlayResult"
})
@XmlRootElement(name = "GetMapOverlayResponse")
public class GetMapOverlayResponse {

    @XmlElement(name = "GetMapOverlayResult")
    protected UPASMap getMapOverlayResult;

    /**
     * Gets the value of the getMapOverlayResult property.
     * 
     * @return
     *     possible object is
     *     {@link UPASMap }
     *     
     */
    public UPASMap getGetMapOverlayResult() {
        return getMapOverlayResult;
    }

    /**
     * Sets the value of the getMapOverlayResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPASMap }
     *     
     */
    public void setGetMapOverlayResult(UPASMap value) {
        this.getMapOverlayResult = value;
    }

}
