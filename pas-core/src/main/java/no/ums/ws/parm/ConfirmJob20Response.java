
package no.ums.ws.parm;

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
 *         &lt;element name="ConfirmJob_2_0Result" type="{http://ums.no/ws/parm/}UConfirmJobResponse" minOccurs="0"/>
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
    "confirmJob20Result"
})
@XmlRootElement(name = "ConfirmJob_2_0Response")
public class ConfirmJob20Response {

    @XmlElement(name = "ConfirmJob_2_0Result")
    protected UConfirmJobResponse confirmJob20Result;

    /**
     * Gets the value of the confirmJob20Result property.
     * 
     * @return
     *     possible object is
     *     {@link UConfirmJobResponse }
     *     
     */
    public UConfirmJobResponse getConfirmJob20Result() {
        return confirmJob20Result;
    }

    /**
     * Sets the value of the confirmJob20Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link UConfirmJobResponse }
     *     
     */
    public void setConfirmJob20Result(UConfirmJobResponse value) {
        this.confirmJob20Result = value;
    }

}
