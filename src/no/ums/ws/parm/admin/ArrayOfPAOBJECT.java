
package no.ums.ws.parm.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfPAOBJECT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfPAOBJECT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PAOBJECT" type="{http://ums.no/ws/parm/admin/}PAOBJECT" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPAOBJECT", propOrder = {
    "paobject"
})
public class ArrayOfPAOBJECT {

    @XmlElement(name = "PAOBJECT", nillable = true)
    protected List<PAOBJECT> paobject;

    /**
     * Gets the value of the paobject property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paobject property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPAOBJECT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PAOBJECT }
     * 
     * 
     */
    public List<PAOBJECT> getPAOBJECT() {
        if (paobject == null) {
            paobject = new ArrayList<PAOBJECT>();
        }
        return this.paobject;
    }

}
