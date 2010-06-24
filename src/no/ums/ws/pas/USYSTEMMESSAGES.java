
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for USYSTEMMESSAGES complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="USYSTEMMESSAGES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "USYSTEMMESSAGES", propOrder = {
    "lTimestamp"
})
public class USYSTEMMESSAGES {

    @XmlElement(name = "l_timestamp")
    protected long lTimestamp;

    /**
     * Gets the value of the lTimestamp property.
     * 
     */
    public long getLTimestamp() {
        return lTimestamp;
    }

    /**
     * Sets the value of the lTimestamp property.
     * 
     */
    public void setLTimestamp(long value) {
        this.lTimestamp = value;
    }

}
