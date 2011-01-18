
package no.ums.ws.pas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfUCCMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUCCMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UCCMessage" type="{http://ums.no/ws/pas/}UCCMessage" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUCCMessage", propOrder = {
    "uccMessage"
})
public class ArrayOfUCCMessage {

    @XmlElement(name = "UCCMessage", nillable = true)
    protected List<UCCMessage> uccMessage;

    /**
     * Gets the value of the uccMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uccMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUCCMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UCCMessage }
     * 
     * 
     */
    public List<UCCMessage> getUCCMessage() {
        if (uccMessage == null) {
            uccMessage = new ArrayList<UCCMessage>();
        }
        return this.uccMessage;
    }

}
