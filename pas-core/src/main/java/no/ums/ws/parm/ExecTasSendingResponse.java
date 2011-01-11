
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
 *         &lt;element name="ExecTasSendingResult" type="{http://ums.no/ws/parm/}ExecResponse" minOccurs="0"/>
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
    "execTasSendingResult"
})
@XmlRootElement(name = "ExecTasSendingResponse")
public class ExecTasSendingResponse {

    @XmlElement(name = "ExecTasSendingResult")
    protected ExecResponse execTasSendingResult;

    /**
     * Gets the value of the execTasSendingResult property.
     * 
     * @return
     *     possible object is
     *     {@link ExecResponse }
     *     
     */
    public ExecResponse getExecTasSendingResult() {
        return execTasSendingResult;
    }

    /**
     * Sets the value of the execTasSendingResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecResponse }
     *     
     */
    public void setExecTasSendingResult(ExecResponse value) {
        this.execTasSendingResult = value;
    }

}
