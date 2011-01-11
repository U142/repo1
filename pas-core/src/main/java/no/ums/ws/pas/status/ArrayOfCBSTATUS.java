
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfCB_STATUS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCB_STATUS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CB_STATUS" type="{http://ums.no/ws/pas/status}CB_STATUS" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCB_STATUS", propOrder = {
    "cbstatus"
})
public class ArrayOfCBSTATUS {

    @XmlElement(name = "CB_STATUS", nillable = true)
    protected List<CBSTATUS> cbstatus;

    /**
     * Gets the value of the cbstatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cbstatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCBSTATUS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CBSTATUS }
     * 
     * 
     */
    public List<CBSTATUS> getCBSTATUS() {
        if (cbstatus == null) {
            cbstatus = new ArrayList<CBSTATUS>();
        }
        return this.cbstatus;
    }

}
