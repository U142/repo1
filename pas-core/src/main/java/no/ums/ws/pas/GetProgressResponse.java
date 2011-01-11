
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
 *         &lt;element name="GetProgressResult" type="{http://ums.no/ws/pas/}PercentResult" minOccurs="0"/>
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
    "getProgressResult"
})
@XmlRootElement(name = "GetProgressResponse")
public class GetProgressResponse {

    @XmlElement(name = "GetProgressResult")
    protected PercentResult getProgressResult;

    /**
     * Gets the value of the getProgressResult property.
     * 
     * @return
     *     possible object is
     *     {@link PercentResult }
     *     
     */
    public PercentResult getGetProgressResult() {
        return getProgressResult;
    }

    /**
     * Sets the value of the getProgressResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link PercentResult }
     *     
     */
    public void setGetProgressResult(PercentResult value) {
        this.getProgressResult = value;
    }

}
