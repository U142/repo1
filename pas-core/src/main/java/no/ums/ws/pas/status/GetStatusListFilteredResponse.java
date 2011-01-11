
package no.ums.ws.pas.status;

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
 *         &lt;element name="GetStatusListFilteredResult" type="{http://ums.no/ws/pas/status}UStatusListResults" minOccurs="0"/>
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
    "getStatusListFilteredResult"
})
@XmlRootElement(name = "GetStatusListFilteredResponse")
public class GetStatusListFilteredResponse {

    @XmlElement(name = "GetStatusListFilteredResult")
    protected UStatusListResults getStatusListFilteredResult;

    /**
     * Gets the value of the getStatusListFilteredResult property.
     * 
     * @return
     *     possible object is
     *     {@link UStatusListResults }
     *     
     */
    public UStatusListResults getGetStatusListFilteredResult() {
        return getStatusListFilteredResult;
    }

    /**
     * Sets the value of the getStatusListFilteredResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UStatusListResults }
     *     
     */
    public void setGetStatusListFilteredResult(UStatusListResults value) {
        this.getStatusListFilteredResult = value;
    }

}
