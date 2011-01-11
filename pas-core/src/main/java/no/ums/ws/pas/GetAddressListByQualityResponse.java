
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
 *         &lt;element name="GetAddressListByQualityResult" type="{http://ums.no/ws/pas/}UAddressList" minOccurs="0"/>
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
    "getAddressListByQualityResult"
})
@XmlRootElement(name = "GetAddressListByQualityResponse")
public class GetAddressListByQualityResponse {

    @XmlElement(name = "GetAddressListByQualityResult")
    protected UAddressList getAddressListByQualityResult;

    /**
     * Gets the value of the getAddressListByQualityResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAddressList }
     *     
     */
    public UAddressList getGetAddressListByQualityResult() {
        return getAddressListByQualityResult;
    }

    /**
     * Sets the value of the getAddressListByQualityResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAddressList }
     *     
     */
    public void setGetAddressListByQualityResult(UAddressList value) {
        this.getAddressListByQualityResult = value;
    }

}
