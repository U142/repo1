
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfULBADURATION complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfULBADURATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ULBADURATION" type="{http://ums.no/ws/pas/}ULBADURATION" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfULBADURATION", propOrder = {
    "ulbaduration"
})
public class ArrayOfULBADURATION {

    @XmlElement(name = "ULBADURATION", nillable = true)
    protected List<ULBADURATION> ulbaduration;

    /**
     * Gets the value of the ulbaduration property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ulbaduration property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getULBADURATION().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ULBADURATION }
     * 
     * 
     */
    public List<ULBADURATION> getULBADURATION() {
        if (ulbaduration == null) {
            ulbaduration = new ArrayList<ULBADURATION>();
        }
        return this.ulbaduration;
    }

}
