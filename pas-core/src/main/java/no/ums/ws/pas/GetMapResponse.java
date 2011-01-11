
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
 *         &lt;element name="GetMapResult" type="{http://ums.no/ws/pas/}UPASMap" minOccurs="0"/>
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
    "getMapResult"
})
@XmlRootElement(name = "GetMapResponse")
public class GetMapResponse {

    @XmlElement(name = "GetMapResult")
    protected UPASMap getMapResult;

    /**
     * Gets the value of the getMapResult property.
     * 
     * @return
     *     possible object is
     *     {@link UPASMap }
     *     
     */
    public UPASMap getGetMapResult() {
        return getMapResult;
    }

    /**
     * Sets the value of the getMapResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPASMap }
     *     
     */
    public void setGetMapResult(UPASMap value) {
        this.getMapResult = value;
    }

}
