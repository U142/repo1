
package no.ums.ws.parm;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfUGisRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUGisRecord">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UGisRecord" type="{http://ums.no/ws/parm/}UGisRecord" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUGisRecord", propOrder = {
    "uGisRecord"
})
public class ArrayOfUGisRecord {

    @XmlElement(name = "UGisRecord", nillable = true)
    protected List<UGisRecord> uGisRecord;

    /**
     * Gets the value of the uGisRecord property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uGisRecord property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUGisRecord().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UGisRecord }
     * 
     * 
     */
    public List<UGisRecord> getUGisRecord() {
        if (uGisRecord == null) {
            uGisRecord = new ArrayList<UGisRecord>();
        }
        return this.uGisRecord;
    }

}
