
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
 *         &lt;element name="GetCBStatusResult" type="{http://ums.no/ws/pas/status}CB_PROJECT_STATUS_RESPONSE" minOccurs="0"/>
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
    "getCBStatusResult"
})
@XmlRootElement(name = "GetCBStatusResponse")
public class GetCBStatusResponse {

    @XmlElement(name = "GetCBStatusResult")
    protected CBPROJECTSTATUSRESPONSE getCBStatusResult;

    /**
     * Gets the value of the getCBStatusResult property.
     * 
     * @return
     *     possible object is
     *     {@link CBPROJECTSTATUSRESPONSE }
     *     
     */
    public CBPROJECTSTATUSRESPONSE getGetCBStatusResult() {
        return getCBStatusResult;
    }

    /**
     * Sets the value of the getCBStatusResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBPROJECTSTATUSRESPONSE }
     *     
     */
    public void setGetCBStatusResult(CBPROJECTSTATUSRESPONSE value) {
        this.getCBStatusResult = value;
    }

}
