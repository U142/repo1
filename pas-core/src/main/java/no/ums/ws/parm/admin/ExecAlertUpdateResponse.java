
package no.ums.ws.parm.admin;

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
 *         &lt;element name="ExecAlertUpdateResult" type="{http://ums.no/ws/parm/admin/}UPAALERTRESTULT" minOccurs="0"/>
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
    "execAlertUpdateResult"
})
@XmlRootElement(name = "ExecAlertUpdateResponse")
public class ExecAlertUpdateResponse {

    @XmlElement(name = "ExecAlertUpdateResult")
    protected UPAALERTRESTULT execAlertUpdateResult;

    /**
     * Gets the value of the execAlertUpdateResult property.
     * 
     * @return
     *     possible object is
     *     {@link UPAALERTRESTULT }
     *     
     */
    public UPAALERTRESTULT getExecAlertUpdateResult() {
        return execAlertUpdateResult;
    }

    /**
     * Sets the value of the execAlertUpdateResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPAALERTRESTULT }
     *     
     */
    public void setExecAlertUpdateResult(UPAALERTRESTULT value) {
        this.execAlertUpdateResult = value;
    }

}
