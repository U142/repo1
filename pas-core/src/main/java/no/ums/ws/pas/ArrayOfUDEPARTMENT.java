
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfUDEPARTMENT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUDEPARTMENT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UDEPARTMENT" type="{http://ums.no/ws/pas/}UDEPARTMENT" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUDEPARTMENT", propOrder = {
    "udepartment"
})
public class ArrayOfUDEPARTMENT {

    @XmlElement(name = "UDEPARTMENT", nillable = true)
    protected List<UDEPARTMENT> udepartment;

    /**
     * Gets the value of the udepartment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the udepartment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUDEPARTMENT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UDEPARTMENT }
     * 
     * 
     */
    public List<UDEPARTMENT> getUDEPARTMENT() {
        if (udepartment == null) {
            udepartment = new ArrayList<UDEPARTMENT>();
        }
        return this.udepartment;
    }

}
