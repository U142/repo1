
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfUSMSINSTATS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUSMSINSTATS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="USMSINSTATS" type="{http://ums.no/ws/pas/status}USMSINSTATS" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUSMSINSTATS", propOrder = {
    "usmsinstats"
})
public class ArrayOfUSMSINSTATS {

    @XmlElement(name = "USMSINSTATS", nillable = true)
    protected List<USMSINSTATS> usmsinstats;

    /**
     * Gets the value of the usmsinstats property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usmsinstats property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUSMSINSTATS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link USMSINSTATS }
     * 
     * 
     */
    public List<USMSINSTATS> getUSMSINSTATS() {
        if (usmsinstats == null) {
            usmsinstats = new ArrayList<USMSINSTATS>();
        }
        return this.usmsinstats;
    }

}
