
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfCB_USER_REGION_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCB_USER_REGION_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CB_USER_REGION_RESPONSE" type="{http://ums.no/ws/parm/admin/}CB_USER_REGION_RESPONSE" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCB_USER_REGION_RESPONSE", propOrder = {
    "cbuserregionresponse"
})
public class ArrayOfCBUSERREGIONRESPONSE {

    @XmlElement(name = "CB_USER_REGION_RESPONSE", nillable = true)
    protected List<CBUSERREGIONRESPONSE> cbuserregionresponse;

    /**
     * Gets the value of the cbuserregionresponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cbuserregionresponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCBUSERREGIONRESPONSE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CBUSERREGIONRESPONSE }
     * 
     * 
     */
    public List<CBUSERREGIONRESPONSE> getCBUSERREGIONRESPONSE() {
        if (cbuserregionresponse == null) {
            cbuserregionresponse = new ArrayList<CBUSERREGIONRESPONSE>();
        }
        return this.cbuserregionresponse;
    }

}
