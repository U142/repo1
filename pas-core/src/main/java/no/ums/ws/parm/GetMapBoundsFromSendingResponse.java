
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="GetMapBoundsFromSendingResult" type="{http://ums.no/ws/parm/}UMapBounds" minOccurs="0"/>
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
    "getMapBoundsFromSendingResult"
})
@XmlRootElement(name = "GetMapBoundsFromSendingResponse")
public class GetMapBoundsFromSendingResponse {

    @XmlElement(name = "GetMapBoundsFromSendingResult")
    protected UMapBounds getMapBoundsFromSendingResult;

    /**
     * Gets the value of the getMapBoundsFromSendingResult property.
     * 
     * @return
     *     possible object is
     *     {@link UMapBounds }
     *     
     */
    public UMapBounds getGetMapBoundsFromSendingResult() {
        return getMapBoundsFromSendingResult;
    }

    /**
     * Sets the value of the getMapBoundsFromSendingResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMapBounds }
     *     
     */
    public void setGetMapBoundsFromSendingResult(UMapBounds value) {
        this.getMapBoundsFromSendingResult = value;
    }

}
