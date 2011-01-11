
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfLBACCode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfLBACCode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LBACCode" type="{http://ums.no/ws/parm/}LBACCode" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfLBACCode", propOrder = {
    "lbacCode"
})
public class ArrayOfLBACCode {

    @XmlElement(name = "LBACCode", nillable = true)
    protected List<LBACCode> lbacCode;

    /**
     * Gets the value of the lbacCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lbacCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLBACCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LBACCode }
     * 
     * 
     */
    public List<LBACCode> getLBACCode() {
        if (lbacCode == null) {
            lbacCode = new ArrayList<LBACCode>();
        }
        return this.lbacCode;
    }

}
