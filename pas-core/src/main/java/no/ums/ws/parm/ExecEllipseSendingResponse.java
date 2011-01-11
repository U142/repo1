
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
 *         &lt;element name="ExecEllipseSendingResult" type="{http://ums.no/ws/parm/}ExecResponse" minOccurs="0"/>
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
    "execEllipseSendingResult"
})
@XmlRootElement(name = "ExecEllipseSendingResponse")
public class ExecEllipseSendingResponse {

    @XmlElement(name = "ExecEllipseSendingResult")
    protected ExecResponse execEllipseSendingResult;

    /**
     * Gets the value of the execEllipseSendingResult property.
     * 
     * @return
     *     possible object is
     *     {@link ExecResponse }
     *     
     */
    public ExecResponse getExecEllipseSendingResult() {
        return execEllipseSendingResult;
    }

    /**
     * Sets the value of the execEllipseSendingResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecResponse }
     *     
     */
    public void setExecEllipseSendingResult(ExecResponse value) {
        this.execEllipseSendingResult = value;
    }

}
