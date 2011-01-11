
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfUPolypoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUPolypoint">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UPolypoint" type="{http://ums.no/ws/pas/}UPolypoint" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUPolypoint", propOrder = {
    "uPolypoint"
})
public class ArrayOfUPolypoint {

    @XmlElement(name = "UPolypoint", nillable = true)
    protected List<UPolypoint> uPolypoint;

    /**
     * Gets the value of the uPolypoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uPolypoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUPolypoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UPolypoint }
     * 
     * 
     */
    public List<UPolypoint> getUPolypoint() {
        if (uPolypoint == null) {
            uPolypoint = new ArrayList<UPolypoint>();
        }
        return this.uPolypoint;
    }

}
