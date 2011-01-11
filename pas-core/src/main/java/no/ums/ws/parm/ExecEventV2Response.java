
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
 *         &lt;element name="ExecEventV2Result" type="{http://ums.no/ws/parm/}ExecResponse" minOccurs="0"/>
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
    "execEventV2Result"
})
@XmlRootElement(name = "ExecEventV2Response")
public class ExecEventV2Response {

    @XmlElement(name = "ExecEventV2Result")
    protected ExecResponse execEventV2Result;

    /**
     * Gets the value of the execEventV2Result property.
     * 
     * @return
     *     possible object is
     *     {@link ExecResponse }
     *     
     */
    public ExecResponse getExecEventV2Result() {
        return execEventV2Result;
    }

    /**
     * Sets the value of the execEventV2Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecResponse }
     *     
     */
    public void setExecEventV2Result(ExecResponse value) {
        this.execEventV2Result = value;
    }

}
