
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfUStatusListItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUStatusListItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UStatusListItem" type="{http://ums.no/ws/pas/status}UStatusListItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUStatusListItem", propOrder = {
    "uStatusListItem"
})
public class ArrayOfUStatusListItem {

    @XmlElement(name = "UStatusListItem", nillable = true)
    protected List<UStatusListItem> uStatusListItem;

    /**
     * Gets the value of the uStatusListItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uStatusListItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUStatusListItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UStatusListItem }
     * 
     * 
     */
    public List<UStatusListItem> getUStatusListItem() {
        if (uStatusListItem == null) {
            uStatusListItem = new ArrayList<UStatusListItem>();
        }
        return this.uStatusListItem;
    }

}
