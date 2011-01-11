
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
 *         &lt;element name="GetNearestGABFromPointResult" type="{http://ums.no/ws/pas/}UGabResultFromPoint" minOccurs="0"/>
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
    "getNearestGABFromPointResult"
})
@XmlRootElement(name = "GetNearestGABFromPointResponse")
public class GetNearestGABFromPointResponse {

    @XmlElement(name = "GetNearestGABFromPointResult")
    protected UGabResultFromPoint getNearestGABFromPointResult;

    /**
     * Gets the value of the getNearestGABFromPointResult property.
     * 
     * @return
     *     possible object is
     *     {@link UGabResultFromPoint }
     *     
     */
    public UGabResultFromPoint getGetNearestGABFromPointResult() {
        return getNearestGABFromPointResult;
    }

    /**
     * Sets the value of the getNearestGABFromPointResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UGabResultFromPoint }
     *     
     */
    public void setGetNearestGABFromPointResult(UGabResultFromPoint value) {
        this.getNearestGABFromPointResult = value;
    }

}
