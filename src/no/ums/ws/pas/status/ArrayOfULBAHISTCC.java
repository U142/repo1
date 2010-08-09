
package no.ums.ws.pas.status;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfULBAHISTCC complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfULBAHISTCC">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ULBAHISTCC" type="{http://ums.no/ws/pas/status}ULBAHISTCC" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfULBAHISTCC", propOrder = {
    "ulbahistcc"
})
public class ArrayOfULBAHISTCC {

    @XmlElement(name = "ULBAHISTCC", nillable = true)
    protected List<ULBAHISTCC> ulbahistcc;

    /**
     * Gets the value of the ulbahistcc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ulbahistcc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getULBAHISTCC().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ULBAHISTCC }
     * 
     * 
     */
    public List<ULBAHISTCC> getULBAHISTCC() {
        if (ulbahistcc == null) {
            ulbahistcc = new ArrayList<ULBAHISTCC>();
        }
        return this.ulbahistcc;
    }

}
