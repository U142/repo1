
package no.ums.ws.pas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCB_REACTION complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCB_REACTION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CB_REACTION" type="{http://ums.no/ws/pas/}CB_REACTION" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCB_REACTION", propOrder = {
    "cbreaction"
})
public class ArrayOfCBREACTION {

    @XmlElement(name = "CB_REACTION", nillable = true)
    protected List<CBREACTION> cbreaction;

    /**
     * Gets the value of the cbreaction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cbreaction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCBREACTION().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CBREACTION }
     * 
     * 
     */
    public List<CBREACTION> getCBREACTION() {
        if (cbreaction == null) {
            cbreaction = new ArrayList<CBREACTION>();
        }
        return this.cbreaction;
    }

}
