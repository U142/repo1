
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
 *         &lt;element name="GetAdrCountMunicipalResult" type="{http://ums.no/ws/parm/}UAdrCount" minOccurs="0"/>
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
    "getAdrCountMunicipalResult"
})
@XmlRootElement(name = "GetAdrCountMunicipalResponse")
public class GetAdrCountMunicipalResponse {

    @XmlElement(name = "GetAdrCountMunicipalResult")
    protected UAdrCount getAdrCountMunicipalResult;

    /**
     * Gets the value of the getAdrCountMunicipalResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAdrCount }
     *     
     */
    public UAdrCount getGetAdrCountMunicipalResult() {
        return getAdrCountMunicipalResult;
    }

    /**
     * Sets the value of the getAdrCountMunicipalResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAdrCount }
     *     
     */
    public void setGetAdrCountMunicipalResult(UAdrCount value) {
        this.getAdrCountMunicipalResult = value;
    }

}
