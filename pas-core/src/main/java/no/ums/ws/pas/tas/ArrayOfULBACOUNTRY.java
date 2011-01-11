
package no.ums.ws.pas.tas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfULBACOUNTRY complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfULBACOUNTRY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ULBACOUNTRY" type="{http://ums.no/ws/pas/tas}ULBACOUNTRY" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfULBACOUNTRY", propOrder = {
    "ulbacountry"
})
public class ArrayOfULBACOUNTRY {

    @XmlElement(name = "ULBACOUNTRY", nillable = true)
    protected List<ULBACOUNTRY> ulbacountry;

    /**
     * Gets the value of the ulbacountry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ulbacountry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getULBACOUNTRY().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ULBACOUNTRY }
     * 
     * 
     */
    public List<ULBACOUNTRY> getULBACOUNTRY() {
        if (ulbacountry == null) {
            ulbacountry = new ArrayList<ULBACOUNTRY>();
        }
        return this.ulbacountry;
    }

}
