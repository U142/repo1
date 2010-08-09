
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="req" type="{http://ums.no/ws/pas/status}CB_PROJECT_STATUS_REQUEST" minOccurs="0"/>
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
    "req"
})
@XmlRootElement(name = "GetCBStatus")
public class GetCBStatus {

    protected CBPROJECTSTATUSREQUEST req;

    /**
     * Gets the value of the req property.
     * 
     * @return
     *     possible object is
     *     {@link CBPROJECTSTATUSREQUEST }
     *     
     */
    public CBPROJECTSTATUSREQUEST getReq() {
        return req;
    }

    /**
     * Sets the value of the req property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBPROJECTSTATUSREQUEST }
     *     
     */
    public void setReq(CBPROJECTSTATUSREQUEST value) {
        this.req = value;
    }

}
