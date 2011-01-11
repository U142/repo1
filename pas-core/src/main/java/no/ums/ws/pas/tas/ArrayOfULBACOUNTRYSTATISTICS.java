
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfULBACOUNTRYSTATISTICS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfULBACOUNTRYSTATISTICS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ULBACOUNTRYSTATISTICS" type="{http://ums.no/ws/pas/tas}ULBACOUNTRYSTATISTICS" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfULBACOUNTRYSTATISTICS", propOrder = {
    "ulbacountrystatistics"
})
public class ArrayOfULBACOUNTRYSTATISTICS {

    @XmlElement(name = "ULBACOUNTRYSTATISTICS", nillable = true)
    protected List<ULBACOUNTRYSTATISTICS> ulbacountrystatistics;

    /**
     * Gets the value of the ulbacountrystatistics property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ulbacountrystatistics property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getULBACOUNTRYSTATISTICS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ULBACOUNTRYSTATISTICS }
     * 
     * 
     */
    public List<ULBACOUNTRYSTATISTICS> getULBACOUNTRYSTATISTICS() {
        if (ulbacountrystatistics == null) {
            ulbacountrystatistics = new ArrayList<ULBACOUNTRYSTATISTICS>();
        }
        return this.ulbacountrystatistics;
    }

}
