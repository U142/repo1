
package no.ums.ws.parm.admin;

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
 *         &lt;element name="GetUserRegionResult" type="{http://ums.no/ws/parm/admin/}ArrayOfCB_USER_REGION_RESPONSE" minOccurs="0"/>
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
    "getUserRegionResult"
})
@XmlRootElement(name = "GetUserRegionResponse")
public class GetUserRegionResponse {

    @XmlElement(name = "GetUserRegionResult")
    protected ArrayOfCBUSERREGIONRESPONSE getUserRegionResult;

    /**
     * Gets the value of the getUserRegionResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCBUSERREGIONRESPONSE }
     *     
     */
    public ArrayOfCBUSERREGIONRESPONSE getGetUserRegionResult() {
        return getUserRegionResult;
    }

    /**
     * Sets the value of the getUserRegionResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCBUSERREGIONRESPONSE }
     *     
     */
    public void setGetUserRegionResult(ArrayOfCBUSERREGIONRESPONSE value) {
        this.getUserRegionResult = value;
    }

}
