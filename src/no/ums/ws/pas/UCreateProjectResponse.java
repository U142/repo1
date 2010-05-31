
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
 *         &lt;element name="UCreateProjectResult" type="{http://ums.no/ws/pas/}UPROJECT_RESPONSE" minOccurs="0"/>
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
    "uCreateProjectResult"
})
@XmlRootElement(name = "UCreateProjectResponse")
public class UCreateProjectResponse {

    @XmlElement(name = "UCreateProjectResult")
    protected UPROJECTRESPONSE uCreateProjectResult;

    /**
     * Gets the value of the uCreateProjectResult property.
     * 
     * @return
     *     possible object is
     *     {@link UPROJECTRESPONSE }
     *     
     */
    public UPROJECTRESPONSE getUCreateProjectResult() {
        return uCreateProjectResult;
    }

    /**
     * Sets the value of the uCreateProjectResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPROJECTRESPONSE }
     *     
     */
    public void setUCreateProjectResult(UPROJECTRESPONSE value) {
        this.uCreateProjectResult = value;
    }

}
