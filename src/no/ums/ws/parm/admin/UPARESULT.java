
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPARESULT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPARESULT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPARESULT", propOrder = {
    "pk"
})
@XmlSeeAlso({
    UPAALERTRESTULT.class,
    UPAEVENTRESULT.class,
    UPAOBJECTRESULT.class
})
public class UPARESULT {

    protected long pk;

    /**
     * Gets the value of the pk property.
     * 
     */
    public long getPk() {
        return pk;
    }

    /**
     * Sets the value of the pk property.
     * 
     */
    public void setPk(long value) {
        this.pk = value;
    }

}
