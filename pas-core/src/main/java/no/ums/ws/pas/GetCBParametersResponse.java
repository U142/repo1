
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
 *         &lt;element name="GetCBParametersResult" type="{http://ums.no/ws/pas/}ULBAPARAMETER" minOccurs="0"/>
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
    "getCBParametersResult"
})
@XmlRootElement(name = "GetCBParametersResponse")
public class GetCBParametersResponse {

    @XmlElement(name = "GetCBParametersResult")
    protected ULBAPARAMETER getCBParametersResult;

    /**
     * Gets the value of the getCBParametersResult property.
     * 
     * @return
     *     possible object is
     *     {@link ULBAPARAMETER }
     *     
     */
    public ULBAPARAMETER getGetCBParametersResult() {
        return getCBParametersResult;
    }

    /**
     * Sets the value of the getCBParametersResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULBAPARAMETER }
     *     
     */
    public void setGetCBParametersResult(ULBAPARAMETER value) {
        this.getCBParametersResult = value;
    }

}
