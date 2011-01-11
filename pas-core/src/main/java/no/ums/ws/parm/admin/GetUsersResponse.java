
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
 *         &lt;element name="GetUsersResult" type="{http://ums.no/ws/parm/admin/}ArrayOfUBBUSER" minOccurs="0"/>
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
    "getUsersResult"
})
@XmlRootElement(name = "GetUsersResponse")
public class GetUsersResponse {

    @XmlElement(name = "GetUsersResult")
    protected ArrayOfUBBUSER getUsersResult;

    /**
     * Gets the value of the getUsersResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUBBUSER }
     *     
     */
    public ArrayOfUBBUSER getGetUsersResult() {
        return getUsersResult;
    }

    /**
     * Sets the value of the getUsersResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUBBUSER }
     *     
     */
    public void setGetUsersResult(ArrayOfUBBUSER value) {
        this.getUsersResult = value;
    }

}
