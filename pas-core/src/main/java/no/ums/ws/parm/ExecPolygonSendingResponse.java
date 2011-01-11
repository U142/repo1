
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
 *         &lt;element name="ExecPolygonSendingResult" type="{http://ums.no/ws/parm/}ExecResponse" minOccurs="0"/>
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
    "execPolygonSendingResult"
})
@XmlRootElement(name = "ExecPolygonSendingResponse")
public class ExecPolygonSendingResponse {

    @XmlElement(name = "ExecPolygonSendingResult")
    protected ExecResponse execPolygonSendingResult;

    /**
     * Gets the value of the execPolygonSendingResult property.
     * 
     * @return
     *     possible object is
     *     {@link ExecResponse }
     *     
     */
    public ExecResponse getExecPolygonSendingResult() {
        return execPolygonSendingResult;
    }

    /**
     * Sets the value of the execPolygonSendingResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecResponse }
     *     
     */
    public void setExecPolygonSendingResult(ExecResponse value) {
        this.execPolygonSendingResult = value;
    }

}
