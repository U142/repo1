
package no.ums.ws.pas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfUAddress complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUAddress">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UAddress" type="{http://ums.no/ws/pas/}UAddress" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUAddress", propOrder = {
    "uAddress"
})
public class ArrayOfUAddress {

    @XmlElement(name = "UAddress", nillable = true)
    protected List<UAddress> uAddress;

    /**
     * Gets the value of the uAddress property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uAddress property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UAddress }
     * 
     * 
     */
    public List<UAddress> getUAddress() {
        if (uAddress == null) {
            uAddress = new ArrayList<UAddress>();
        }
        return this.uAddress;
    }

}
