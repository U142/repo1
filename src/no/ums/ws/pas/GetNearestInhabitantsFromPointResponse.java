
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="GetNearestInhabitantsFromPointResult" type="{http://ums.no/ws/pas/}UAddressList" minOccurs="0"/>
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
    "getNearestInhabitantsFromPointResult"
})
@XmlRootElement(name = "GetNearestInhabitantsFromPointResponse")
public class GetNearestInhabitantsFromPointResponse {

    @XmlElement(name = "GetNearestInhabitantsFromPointResult")
    protected UAddressList getNearestInhabitantsFromPointResult;

    /**
     * Gets the value of the getNearestInhabitantsFromPointResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAddressList }
     *     
     */
    public UAddressList getGetNearestInhabitantsFromPointResult() {
        return getNearestInhabitantsFromPointResult;
    }

    /**
     * Sets the value of the getNearestInhabitantsFromPointResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAddressList }
     *     
     */
    public void setGetNearestInhabitantsFromPointResult(UAddressList value) {
        this.getNearestInhabitantsFromPointResult = value;
    }

}
