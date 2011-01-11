
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
 *         &lt;element name="ExecMunicipalSendingResult" type="{http://ums.no/ws/parm/}ExecResponse" minOccurs="0"/>
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
    "execMunicipalSendingResult"
})
@XmlRootElement(name = "ExecMunicipalSendingResponse")
public class ExecMunicipalSendingResponse {

    @XmlElement(name = "ExecMunicipalSendingResult")
    protected ExecResponse execMunicipalSendingResult;

    /**
     * Gets the value of the execMunicipalSendingResult property.
     * 
     * @return
     *     possible object is
     *     {@link ExecResponse }
     *     
     */
    public ExecResponse getExecMunicipalSendingResult() {
        return execMunicipalSendingResult;
    }

    /**
     * Sets the value of the execMunicipalSendingResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecResponse }
     *     
     */
    public void setExecMunicipalSendingResult(ExecResponse value) {
        this.execMunicipalSendingResult = value;
    }

}
