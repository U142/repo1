
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
 *         &lt;element name="ExecEventUpdateResult" type="{http://ums.no/ws/parm/admin/}UPAEVENTRESULT" minOccurs="0"/>
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
    "execEventUpdateResult"
})
@XmlRootElement(name = "ExecEventUpdateResponse")
public class ExecEventUpdateResponse {

    @XmlElement(name = "ExecEventUpdateResult")
    protected UPAEVENTRESULT execEventUpdateResult;

    /**
     * Gets the value of the execEventUpdateResult property.
     * 
     * @return
     *     possible object is
     *     {@link UPAEVENTRESULT }
     *     
     */
    public UPAEVENTRESULT getExecEventUpdateResult() {
        return execEventUpdateResult;
    }

    /**
     * Sets the value of the execEventUpdateResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPAEVENTRESULT }
     *     
     */
    public void setExecEventUpdateResult(UPAEVENTRESULT value) {
        this.execEventUpdateResult = value;
    }

}
