
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
 *         &lt;element name="GetRestrictionAreasResult" type="{http://ums.no/ws/parm/admin/}ArrayOfUDEPARTMENT" minOccurs="0"/>
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
    "getRestrictionAreasResult"
})
@XmlRootElement(name = "GetRestrictionAreasResponse")
public class GetRestrictionAreasResponse {

    @XmlElement(name = "GetRestrictionAreasResult")
    protected ArrayOfUDEPARTMENT getRestrictionAreasResult;

    /**
     * Gets the value of the getRestrictionAreasResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUDEPARTMENT }
     *     
     */
    public ArrayOfUDEPARTMENT getGetRestrictionAreasResult() {
        return getRestrictionAreasResult;
    }

    /**
     * Sets the value of the getRestrictionAreasResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUDEPARTMENT }
     *     
     */
    public void setGetRestrictionAreasResult(ArrayOfUDEPARTMENT value) {
        this.getRestrictionAreasResult = value;
    }

}
