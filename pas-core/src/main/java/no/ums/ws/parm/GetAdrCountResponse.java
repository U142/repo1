
package no.ums.ws.parm;

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
 *         &lt;element name="GetAdrCountResult" type="{http://ums.no/ws/parm/}UAdrCount" minOccurs="0"/>
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
    "getAdrCountResult"
})
@XmlRootElement(name = "GetAdrCountResponse")
public class GetAdrCountResponse {

    @XmlElement(name = "GetAdrCountResult")
    protected UAdrCount getAdrCountResult;

    /**
     * Gets the value of the getAdrCountResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAdrCount }
     *     
     */
    public UAdrCount getGetAdrCountResult() {
        return getAdrCountResult;
    }

    /**
     * Sets the value of the getAdrCountResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAdrCount }
     *     
     */
    public void setGetAdrCountResult(UAdrCount value) {
        this.getAdrCountResult = value;
    }

}
