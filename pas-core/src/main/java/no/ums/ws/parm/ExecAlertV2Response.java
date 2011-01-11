
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
 *         &lt;element name="ExecAlertV2Result" type="{http://ums.no/ws/parm/}ExecResponse" minOccurs="0"/>
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
    "execAlertV2Result"
})
@XmlRootElement(name = "ExecAlertV2Response")
public class ExecAlertV2Response {

    @XmlElement(name = "ExecAlertV2Result")
    protected ExecResponse execAlertV2Result;

    /**
     * Gets the value of the execAlertV2Result property.
     * 
     * @return
     *     possible object is
     *     {@link ExecResponse }
     *     
     */
    public ExecResponse getExecAlertV2Result() {
        return execAlertV2Result;
    }

    /**
     * Sets the value of the execAlertV2Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecResponse }
     *     
     */
    public void setExecAlertV2Result(ExecResponse value) {
        this.execAlertV2Result = value;
    }

}
