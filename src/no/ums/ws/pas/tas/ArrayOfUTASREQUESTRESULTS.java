
package no.ums.ws.pas.tas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfUTASREQUESTRESULTS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUTASREQUESTRESULTS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UTASREQUESTRESULTS" type="{http://ums.no/ws/pas/tas}UTASREQUESTRESULTS" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUTASREQUESTRESULTS", propOrder = {
    "utasrequestresults"
})
public class ArrayOfUTASREQUESTRESULTS {

    @XmlElement(name = "UTASREQUESTRESULTS", nillable = true)
    protected List<UTASREQUESTRESULTS> utasrequestresults;

    /**
     * Gets the value of the utasrequestresults property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the utasrequestresults property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUTASREQUESTRESULTS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UTASREQUESTRESULTS }
     * 
     * 
     */
    public List<UTASREQUESTRESULTS> getUTASREQUESTRESULTS() {
        if (utasrequestresults == null) {
            utasrequestresults = new ArrayList<UTASREQUESTRESULTS>();
        }
        return this.utasrequestresults;
    }

}
