
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
 *         &lt;element name="GetAdrCountTestsendingResult" type="{http://ums.no/ws/parm/}UAdrCount" minOccurs="0"/>
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
    "getAdrCountTestsendingResult"
})
@XmlRootElement(name = "GetAdrCountTestsendingResponse")
public class GetAdrCountTestsendingResponse {

    @XmlElement(name = "GetAdrCountTestsendingResult")
    protected UAdrCount getAdrCountTestsendingResult;

    /**
     * Gets the value of the getAdrCountTestsendingResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAdrCount }
     *     
     */
    public UAdrCount getGetAdrCountTestsendingResult() {
        return getAdrCountTestsendingResult;
    }

    /**
     * Sets the value of the getAdrCountTestsendingResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAdrCount }
     *     
     */
    public void setGetAdrCountTestsendingResult(UAdrCount value) {
        this.getAdrCountTestsendingResult = value;
    }

}
