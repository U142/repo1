
package no.ums.ws.pas.status;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfULBAHISTCELL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfULBAHISTCELL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ULBAHISTCELL" type="{http://ums.no/ws/pas/status}ULBAHISTCELL" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfULBAHISTCELL", propOrder = {
    "ulbahistcell"
})
public class ArrayOfULBAHISTCELL {

    @XmlElement(name = "ULBAHISTCELL", nillable = true)
    protected List<ULBAHISTCELL> ulbahistcell;

    /**
     * Gets the value of the ulbahistcell property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ulbahistcell property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getULBAHISTCELL().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ULBAHISTCELL }
     * 
     * 
     */
    public List<ULBAHISTCELL> getULBAHISTCELL() {
        if (ulbahistcell == null) {
            ulbahistcell = new ArrayList<ULBAHISTCELL>();
        }
        return this.ulbahistcell;
    }

}
