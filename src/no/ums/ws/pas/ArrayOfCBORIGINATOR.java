
package no.ums.ws.pas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCB_ORIGINATOR complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCB_ORIGINATOR">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CB_ORIGINATOR" type="{http://ums.no/ws/pas/}CB_ORIGINATOR" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCB_ORIGINATOR", propOrder = {
    "cboriginator"
})
public class ArrayOfCBORIGINATOR {

    @XmlElement(name = "CB_ORIGINATOR", nillable = true)
    protected List<CBORIGINATOR> cboriginator;

    /**
     * Gets the value of the cboriginator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cboriginator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCBORIGINATOR().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CBORIGINATOR }
     * 
     * 
     */
    public List<CBORIGINATOR> getCBORIGINATOR() {
        if (cboriginator == null) {
            cboriginator = new ArrayList<CBORIGINATOR>();
        }
        return this.cboriginator;
    }

}
