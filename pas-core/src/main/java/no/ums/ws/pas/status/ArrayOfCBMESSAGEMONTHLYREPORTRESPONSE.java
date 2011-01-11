
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfCB_MESSAGE_MONTHLY_REPORT_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCB_MESSAGE_MONTHLY_REPORT_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CB_MESSAGE_MONTHLY_REPORT_RESPONSE" type="{http://ums.no/ws/pas/status}CB_MESSAGE_MONTHLY_REPORT_RESPONSE" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCB_MESSAGE_MONTHLY_REPORT_RESPONSE", propOrder = {
    "cbmessagemonthlyreportresponse"
})
public class ArrayOfCBMESSAGEMONTHLYREPORTRESPONSE {

    @XmlElement(name = "CB_MESSAGE_MONTHLY_REPORT_RESPONSE", nillable = true)
    protected List<CBMESSAGEMONTHLYREPORTRESPONSE> cbmessagemonthlyreportresponse;

    /**
     * Gets the value of the cbmessagemonthlyreportresponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cbmessagemonthlyreportresponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCBMESSAGEMONTHLYREPORTRESPONSE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CBMESSAGEMONTHLYREPORTRESPONSE }
     * 
     * 
     */
    public List<CBMESSAGEMONTHLYREPORTRESPONSE> getCBMESSAGEMONTHLYREPORTRESPONSE() {
        if (cbmessagemonthlyreportresponse == null) {
            cbmessagemonthlyreportresponse = new ArrayList<CBMESSAGEMONTHLYREPORTRESPONSE>();
        }
        return this.cbmessagemonthlyreportresponse;
    }

}
