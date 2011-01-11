
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPOWERUP_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPOWERUP_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_max_logontries" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPOWERUP_RESPONSE", propOrder = {
    "lMaxLogontries"
})
public class UPOWERUPRESPONSE {

    @XmlElement(name = "l_max_logontries")
    protected int lMaxLogontries;

    /**
     * Gets the value of the lMaxLogontries property.
     * 
     */
    public int getLMaxLogontries() {
        return lMaxLogontries;
    }

    /**
     * Sets the value of the lMaxLogontries property.
     * 
     */
    public void setLMaxLogontries(int value) {
        this.lMaxLogontries = value;
    }

}
