
package no.ums.ws.parm.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfPAALERT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfPAALERT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PAALERT" type="{http://ums.no/ws/parm/admin/}PAALERT" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPAALERT", propOrder = {
    "paalert"
})
public class ArrayOfPAALERT {

    @XmlElement(name = "PAALERT", nillable = true)
    protected List<PAALERT> paalert;

    /**
     * Gets the value of the paalert property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paalert property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPAALERT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PAALERT }
     * 
     * 
     */
    public List<PAALERT> getPAALERT() {
        if (paalert == null) {
            paalert = new ArrayList<PAALERT>();
        }
        return this.paalert;
    }

}
