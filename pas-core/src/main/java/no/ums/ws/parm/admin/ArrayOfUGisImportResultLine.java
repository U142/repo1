
package no.ums.ws.parm.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfUGisImportResultLine complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUGisImportResultLine">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UGisImportResultLine" type="{http://ums.no/ws/parm/admin/}UGisImportResultLine" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUGisImportResultLine", propOrder = {
    "uGisImportResultLine"
})
public class ArrayOfUGisImportResultLine {

    @XmlElement(name = "UGisImportResultLine", nillable = true)
    protected List<UGisImportResultLine> uGisImportResultLine;

    /**
     * Gets the value of the uGisImportResultLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uGisImportResultLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUGisImportResultLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UGisImportResultLine }
     * 
     * 
     */
    public List<UGisImportResultLine> getUGisImportResultLine() {
        if (uGisImportResultLine == null) {
            uGisImportResultLine = new ArrayList<UGisImportResultLine>();
        }
        return this.uGisImportResultLine;
    }

}
