
package no.ums.pas.core.dataexchange.soap.no.ums;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfAlertResultLine complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfAlertResultLine">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AlertResultLine" type="{http://ums.no/}AlertResultLine" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfAlertResultLine", propOrder = {
    "alertResultLine"
})
public class ArrayOfAlertResultLine {

    @XmlElement(name = "AlertResultLine", required = true, nillable = true)
    protected List<AlertResultLine> alertResultLine;

    /**
     * Gets the value of the alertResultLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alertResultLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlertResultLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AlertResultLine }
     * 
     * 
     */
    public List<AlertResultLine> getAlertResultLine() {
        if (alertResultLine == null) {
            alertResultLine = new ArrayList<AlertResultLine>();
        }
        return this.alertResultLine;
    }

}
