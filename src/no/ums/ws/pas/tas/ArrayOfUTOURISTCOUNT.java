
package no.ums.ws.pas.tas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfUTOURISTCOUNT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUTOURISTCOUNT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UTOURISTCOUNT" type="{http://ums.no/ws/pas/tas}UTOURISTCOUNT" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUTOURISTCOUNT", propOrder = {
    "utouristcount"
})
public class ArrayOfUTOURISTCOUNT {

    @XmlElement(name = "UTOURISTCOUNT", nillable = true)
    protected List<UTOURISTCOUNT> utouristcount;

    /**
     * Gets the value of the utouristcount property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the utouristcount property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUTOURISTCOUNT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UTOURISTCOUNT }
     * 
     * 
     */
    public List<UTOURISTCOUNT> getUTOURISTCOUNT() {
        if (utouristcount == null) {
            utouristcount = new ArrayList<UTOURISTCOUNT>();
        }
        return this.utouristcount;
    }

}
