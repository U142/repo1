
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
 *         &lt;element name="GetAccessPermissionsResult" type="{http://ums.no/ws/parm/admin/}ArrayOfUBBUSER" minOccurs="0"/>
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
    "getAccessPermissionsResult"
})
@XmlRootElement(name = "GetAccessPermissionsResponse")
public class GetAccessPermissionsResponse {

    @XmlElement(name = "GetAccessPermissionsResult")
    protected ArrayOfUBBUSER getAccessPermissionsResult;

    /**
     * Gets the value of the getAccessPermissionsResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUBBUSER }
     *     
     */
    public ArrayOfUBBUSER getGetAccessPermissionsResult() {
        return getAccessPermissionsResult;
    }

    /**
     * Sets the value of the getAccessPermissionsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUBBUSER }
     *     
     */
    public void setGetAccessPermissionsResult(ArrayOfUBBUSER value) {
        this.getAccessPermissionsResult = value;
    }

}
