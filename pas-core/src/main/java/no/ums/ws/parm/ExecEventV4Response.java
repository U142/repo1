
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
 *         &lt;element name="ExecEventV4Result" type="{http://ums.no/ws/parm/}ExecResponse" minOccurs="0"/>
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
    "execEventV4Result"
})
@XmlRootElement(name = "ExecEventV4Response")
public class ExecEventV4Response {

    @XmlElement(name = "ExecEventV4Result")
    protected ExecResponse execEventV4Result;

    /**
     * Gets the value of the execEventV4Result property.
     * 
     * @return
     *     possible object is
     *     {@link ExecResponse }
     *     
     */
    public ExecResponse getExecEventV4Result() {
        return execEventV4Result;
    }

    /**
     * Sets the value of the execEventV4Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecResponse }
     *     
     */
    public void setExecEventV4Result(ExecResponse value) {
        this.execEventV4Result = value;
    }

}
