
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfCB_RISK complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCB_RISK">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CB_RISK" type="{http://ums.no/ws/pas/}CB_RISK" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCB_RISK", propOrder = {
    "cbrisk"
})
public class ArrayOfCBRISK {

    @XmlElement(name = "CB_RISK", nillable = true)
    protected List<CBRISK> cbrisk;

    /**
     * Gets the value of the cbrisk property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cbrisk property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCBRISK().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CBRISK }
     * 
     * 
     */
    public List<CBRISK> getCBRISK() {
        if (cbrisk == null) {
            cbrisk = new ArrayList<CBRISK>();
        }
        return this.cbrisk;
    }

}
