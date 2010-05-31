
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
 *         &lt;element name="UpdateParmResult" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
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
    "updateParmResult"
})
@XmlRootElement(name = "UpdateParmResponse")
public class UpdateParmResponse {

    @XmlElement(name = "UpdateParmResult")
    protected byte[] updateParmResult;

    /**
     * Gets the value of the updateParmResult property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getUpdateParmResult() {
        return updateParmResult;
    }

    /**
     * Sets the value of the updateParmResult property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setUpdateParmResult(byte[] value) {
        this.updateParmResult = ((byte[]) value);
    }

}
