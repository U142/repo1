
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfULBACONTINENT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfULBACONTINENT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ULBACONTINENT" type="{http://ums.no/ws/pas/tas}ULBACONTINENT" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfULBACONTINENT", propOrder = {
    "ulbacontinent"
})
public class ArrayOfULBACONTINENT {

    @XmlElement(name = "ULBACONTINENT", nillable = true)
    protected List<ULBACONTINENT> ulbacontinent;

    /**
     * Gets the value of the ulbacontinent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ulbacontinent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getULBACONTINENT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ULBACONTINENT }
     * 
     * 
     */
    public List<ULBACONTINENT> getULBACONTINENT() {
        if (ulbacontinent == null) {
            ulbacontinent = new ArrayList<ULBACONTINENT>();
        }
        return this.ulbacontinent;
    }

}
