
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
 *         &lt;element name="StoreUserResult" type="{http://ums.no/ws/parm/admin/}UBBUSER" minOccurs="0"/>
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
    "storeUserResult"
})
@XmlRootElement(name = "StoreUserResponse")
public class StoreUserResponse {

    @XmlElement(name = "StoreUserResult")
    protected UBBUSER storeUserResult;

    /**
     * Gets the value of the storeUserResult property.
     * 
     * @return
     *     possible object is
     *     {@link UBBUSER }
     *     
     */
    public UBBUSER getStoreUserResult() {
        return storeUserResult;
    }

    /**
     * Sets the value of the storeUserResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBUSER }
     *     
     */
    public void setStoreUserResult(UBBUSER value) {
        this.storeUserResult = value;
    }

}
