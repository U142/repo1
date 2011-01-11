
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
 *         &lt;element name="ExecPAShapeUpdateResult" type="{http://ums.no/ws/parm/admin/}UPAOBJECTRESULT" minOccurs="0"/>
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
    "execPAShapeUpdateResult"
})
@XmlRootElement(name = "ExecPAShapeUpdateResponse")
public class ExecPAShapeUpdateResponse {

    @XmlElement(name = "ExecPAShapeUpdateResult")
    protected UPAOBJECTRESULT execPAShapeUpdateResult;

    /**
     * Gets the value of the execPAShapeUpdateResult property.
     * 
     * @return
     *     possible object is
     *     {@link UPAOBJECTRESULT }
     *     
     */
    public UPAOBJECTRESULT getExecPAShapeUpdateResult() {
        return execPAShapeUpdateResult;
    }

    /**
     * Sets the value of the execPAShapeUpdateResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPAOBJECTRESULT }
     *     
     */
    public void setExecPAShapeUpdateResult(UPAOBJECTRESULT value) {
        this.execPAShapeUpdateResult = value;
    }

}
