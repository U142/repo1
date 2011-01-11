
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBASEND_TS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBASEND_TS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_status" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_ts" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_operator" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBASEND_TS", propOrder = {
    "lStatus",
    "lTs",
    "lOperator"
})
public class ULBASENDTS {

    @XmlElement(name = "l_status")
    protected int lStatus;
    @XmlElement(name = "l_ts")
    protected long lTs;
    @XmlElement(name = "l_operator")
    protected int lOperator;

    /**
     * Gets the value of the lStatus property.
     * 
     */
    public int getLStatus() {
        return lStatus;
    }

    /**
     * Sets the value of the lStatus property.
     * 
     */
    public void setLStatus(int value) {
        this.lStatus = value;
    }

    /**
     * Gets the value of the lTs property.
     * 
     */
    public long getLTs() {
        return lTs;
    }

    /**
     * Sets the value of the lTs property.
     * 
     */
    public void setLTs(long value) {
        this.lTs = value;
    }

    /**
     * Gets the value of the lOperator property.
     * 
     */
    public int getLOperator() {
        return lOperator;
    }

    /**
     * Sets the value of the lOperator property.
     * 
     */
    public void setLOperator(int value) {
        this.lOperator = value;
    }

}
