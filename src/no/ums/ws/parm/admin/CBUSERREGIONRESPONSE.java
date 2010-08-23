
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_USER_REGION_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_USER_REGION_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="regionlist" type="{http://ums.no/ws/parm/admin/}ArrayOfPAOBJECT" minOccurs="0"/>
 *         &lt;element name="user" type="{http://ums.no/ws/parm/admin/}UBBUSER" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_USER_REGION_RESPONSE", propOrder = {
    "regionlist",
    "user"
})
public class CBUSERREGIONRESPONSE {

    protected ArrayOfPAOBJECT regionlist;
    protected UBBUSER user;

    /**
     * Gets the value of the regionlist property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPAOBJECT }
     *     
     */
    public ArrayOfPAOBJECT getRegionlist() {
        return regionlist;
    }

    /**
     * Sets the value of the regionlist property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPAOBJECT }
     *     
     */
    public void setRegionlist(ArrayOfPAOBJECT value) {
        this.regionlist = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link UBBUSER }
     *     
     */
    public UBBUSER getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBUSER }
     *     
     */
    public void setUser(UBBUSER value) {
        this.user = value;
    }

}
