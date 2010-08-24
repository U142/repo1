
package no.ums.ws.pas.status;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfULBASENDING complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfULBASENDING">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ULBASENDING" type="{http://ums.no/ws/pas/status}ULBASENDING" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfULBASENDING", propOrder = {
    "ulbasending"
})
public class ArrayOfULBASENDING {

    @XmlElement(name = "ULBASENDING", nillable = true)
    protected List<ULBASENDING> ulbasending;

    /**
     * Gets the value of the ulbasending property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ulbasending property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getULBASENDING().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ULBASENDING }
     * 
     * 
     */
    public List<ULBASENDING> getULBASENDING() {
        if (ulbasending == null) {
            ulbasending = new ArrayList<ULBASENDING>();
        }
        return this.ulbasending;
    }

}
