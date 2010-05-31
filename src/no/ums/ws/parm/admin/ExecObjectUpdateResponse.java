
package no.ums.ws.parm.admin;

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
 *         &lt;element name="ExecObjectUpdateResult" type="{http://ums.no/ws/parm/admin/}UPAOBJECTRESULT" minOccurs="0"/>
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
    "execObjectUpdateResult"
})
@XmlRootElement(name = "ExecObjectUpdateResponse")
public class ExecObjectUpdateResponse {

    @XmlElement(name = "ExecObjectUpdateResult")
    protected UPAOBJECTRESULT execObjectUpdateResult;

    /**
     * Gets the value of the execObjectUpdateResult property.
     * 
     * @return
     *     possible object is
     *     {@link UPAOBJECTRESULT }
     *     
     */
    public UPAOBJECTRESULT getExecObjectUpdateResult() {
        return execObjectUpdateResult;
    }

    /**
     * Sets the value of the execObjectUpdateResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPAOBJECTRESULT }
     *     
     */
    public void setExecObjectUpdateResult(UPAOBJECTRESULT value) {
        this.execObjectUpdateResult = value;
    }

}
