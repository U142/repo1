
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
 *         &lt;element name="GetStatusListResult" type="{http://ums.no/ws/pas/status}UStatusListResults" minOccurs="0"/>
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
    "getStatusListResult"
})
@XmlRootElement(name = "GetStatusListResponse")
public class GetStatusListResponse {

    @XmlElement(name = "GetStatusListResult")
    protected UStatusListResults getStatusListResult;

    /**
     * Gets the value of the getStatusListResult property.
     * 
     * @return
     *     possible object is
     *     {@link UStatusListResults }
     *     
     */
    public UStatusListResults getGetStatusListResult() {
        return getStatusListResult;
    }

    /**
     * Sets the value of the getStatusListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UStatusListResults }
     *     
     */
    public void setGetStatusListResult(UStatusListResults value) {
        this.getStatusListResult = value;
    }

}
