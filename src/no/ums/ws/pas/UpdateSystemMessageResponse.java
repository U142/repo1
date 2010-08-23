
package no.ums.ws.pas;

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
 *         &lt;element name="UpdateSystemMessageResult" type="{http://ums.no/ws/pas/}UBBNEWS" minOccurs="0"/>
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
    "updateSystemMessageResult"
})
@XmlRootElement(name = "UpdateSystemMessageResponse")
public class UpdateSystemMessageResponse {

    @XmlElement(name = "UpdateSystemMessageResult")
    protected UBBNEWS updateSystemMessageResult;

    /**
     * Gets the value of the updateSystemMessageResult property.
     * 
     * @return
     *     possible object is
     *     {@link UBBNEWS }
     *     
     */
    public UBBNEWS getUpdateSystemMessageResult() {
        return updateSystemMessageResult;
    }

    /**
     * Sets the value of the updateSystemMessageResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBNEWS }
     *     
     */
    public void setUpdateSystemMessageResult(UBBNEWS value) {
        this.updateSystemMessageResult = value;
    }

}
