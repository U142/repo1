
package no.ums.ws.parm;

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
 *         &lt;element name="ExecCBOperationResult" type="{http://ums.no/ws/parm/}CB_SENDING_RESPONSE" minOccurs="0"/>
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
    "execCBOperationResult"
})
@XmlRootElement(name = "ExecCBOperationResponse")
public class ExecCBOperationResponse {

    @XmlElement(name = "ExecCBOperationResult")
    protected CBSENDINGRESPONSE execCBOperationResult;

    /**
     * Gets the value of the execCBOperationResult property.
     * 
     * @return
     *     possible object is
     *     {@link CBSENDINGRESPONSE }
     *     
     */
    public CBSENDINGRESPONSE getExecCBOperationResult() {
        return execCBOperationResult;
    }

    /**
     * Sets the value of the execCBOperationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBSENDINGRESPONSE }
     *     
     */
    public void setExecCBOperationResult(CBSENDINGRESPONSE value) {
        this.execCBOperationResult = value;
    }

}
