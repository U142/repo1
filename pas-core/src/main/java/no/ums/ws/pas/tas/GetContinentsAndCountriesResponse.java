
package no.ums.ws.pas.tas;

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
 *         &lt;element name="GetContinentsAndCountriesResult" type="{http://ums.no/ws/pas/tas}UTASUPDATES" minOccurs="0"/>
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
    "getContinentsAndCountriesResult"
})
@XmlRootElement(name = "GetContinentsAndCountriesResponse")
public class GetContinentsAndCountriesResponse {

    @XmlElement(name = "GetContinentsAndCountriesResult")
    protected UTASUPDATES getContinentsAndCountriesResult;

    /**
     * Gets the value of the getContinentsAndCountriesResult property.
     * 
     * @return
     *     possible object is
     *     {@link UTASUPDATES }
     *     
     */
    public UTASUPDATES getGetContinentsAndCountriesResult() {
        return getContinentsAndCountriesResult;
    }

    /**
     * Sets the value of the getContinentsAndCountriesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTASUPDATES }
     *     
     */
    public void setGetContinentsAndCountriesResult(UTASUPDATES value) {
        this.getContinentsAndCountriesResult = value;
    }

}
