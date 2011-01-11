
package no.ums.ws.pas.tas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfUTASRESPONSENUMBER complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUTASRESPONSENUMBER">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UTASRESPONSENUMBER" type="{http://ums.no/ws/pas/tas}UTASRESPONSENUMBER" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUTASRESPONSENUMBER", propOrder = {
    "utasresponsenumber"
})
public class ArrayOfUTASRESPONSENUMBER {

    @XmlElement(name = "UTASRESPONSENUMBER", nillable = true)
    protected List<UTASRESPONSENUMBER> utasresponsenumber;

    /**
     * Gets the value of the utasresponsenumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the utasresponsenumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUTASRESPONSENUMBER().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UTASRESPONSENUMBER }
     * 
     * 
     */
    public List<UTASRESPONSENUMBER> getUTASRESPONSENUMBER() {
        if (utasresponsenumber == null) {
            utasresponsenumber = new ArrayList<UTASRESPONSENUMBER>();
        }
        return this.utasresponsenumber;
    }

}
