
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfUBBMESSAGE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUBBMESSAGE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UBBMESSAGE" type="{http://ums.no/ws/pas/}UBBMESSAGE" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUBBMESSAGE", propOrder = {
    "ubbmessage"
})
public class ArrayOfUBBMESSAGE {

    @XmlElement(name = "UBBMESSAGE", nillable = true)
    protected List<UBBMESSAGE> ubbmessage;

    /**
     * Gets the value of the ubbmessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ubbmessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUBBMESSAGE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UBBMESSAGE }
     * 
     * 
     */
    public List<UBBMESSAGE> getUBBMESSAGE() {
        if (ubbmessage == null) {
            ubbmessage = new ArrayList<UBBMESSAGE>();
        }
        return this.ubbmessage;
    }

}
