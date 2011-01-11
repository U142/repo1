
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfUBBUSER complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUBBUSER">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UBBUSER" type="{http://ums.no/ws/parm/admin/}UBBUSER" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUBBUSER", propOrder = {
    "ubbuser"
})
public class ArrayOfUBBUSER {

    @XmlElement(name = "UBBUSER", nillable = true)
    protected List<UBBUSER> ubbuser;

    /**
     * Gets the value of the ubbuser property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ubbuser property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUBBUSER().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UBBUSER }
     * 
     * 
     */
    public List<UBBUSER> getUBBUSER() {
        if (ubbuser == null) {
            ubbuser = new ArrayList<UBBUSER>();
        }
        return this.ubbuser;
    }

}
