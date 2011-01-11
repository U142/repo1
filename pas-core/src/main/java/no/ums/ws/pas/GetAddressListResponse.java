
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
 *         &lt;element name="GetAddressListResult" type="{http://ums.no/ws/pas/}UAddressList" minOccurs="0"/>
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
    "getAddressListResult"
})
@XmlRootElement(name = "GetAddressListResponse")
public class GetAddressListResponse {

    @XmlElement(name = "GetAddressListResult")
    protected UAddressList getAddressListResult;

    /**
     * Gets the value of the getAddressListResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAddressList }
     *     
     */
    public UAddressList getGetAddressListResult() {
        return getAddressListResult;
    }

    /**
     * Sets the value of the getAddressListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAddressList }
     *     
     */
    public void setGetAddressListResult(UAddressList value) {
        this.getAddressListResult = value;
    }

}
