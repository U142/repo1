
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBAHISTCC complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBAHISTCC">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_cc" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_delivered" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_expired" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_failed" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_unknown" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_submitted" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_queued" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_subscribers" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "ULBAHISTCC", propOrder = {
    "lCc",
    "lDelivered",
    "lExpired",
    "lFailed",
    "lUnknown",
    "lSubmitted",
    "lQueued",
    "lSubscribers",
    "lOperator"
})
public class ULBAHISTCC {

    @XmlElement(name = "l_cc")
    protected int lCc;
    @XmlElement(name = "l_delivered")
    protected int lDelivered;
    @XmlElement(name = "l_expired")
    protected int lExpired;
    @XmlElement(name = "l_failed")
    protected int lFailed;
    @XmlElement(name = "l_unknown")
    protected int lUnknown;
    @XmlElement(name = "l_submitted")
    protected int lSubmitted;
    @XmlElement(name = "l_queued")
    protected int lQueued;
    @XmlElement(name = "l_subscribers")
    protected int lSubscribers;
    @XmlElement(name = "l_operator")
    protected int lOperator;

    /**
     * Gets the value of the lCc property.
     * 
     */
    public int getLCc() {
        return lCc;
    }

    /**
     * Sets the value of the lCc property.
     * 
     */
    public void setLCc(int value) {
        this.lCc = value;
    }

    /**
     * Gets the value of the lDelivered property.
     * 
     */
    public int getLDelivered() {
        return lDelivered;
    }

    /**
     * Sets the value of the lDelivered property.
     * 
     */
    public void setLDelivered(int value) {
        this.lDelivered = value;
    }

    /**
     * Gets the value of the lExpired property.
     * 
     */
    public int getLExpired() {
        return lExpired;
    }

    /**
     * Sets the value of the lExpired property.
     * 
     */
    public void setLExpired(int value) {
        this.lExpired = value;
    }

    /**
     * Gets the value of the lFailed property.
     * 
     */
    public int getLFailed() {
        return lFailed;
    }

    /**
     * Sets the value of the lFailed property.
     * 
     */
    public void setLFailed(int value) {
        this.lFailed = value;
    }

    /**
     * Gets the value of the lUnknown property.
     * 
     */
    public int getLUnknown() {
        return lUnknown;
    }

    /**
     * Sets the value of the lUnknown property.
     * 
     */
    public void setLUnknown(int value) {
        this.lUnknown = value;
    }

    /**
     * Gets the value of the lSubmitted property.
     * 
     */
    public int getLSubmitted() {
        return lSubmitted;
    }

    /**
     * Sets the value of the lSubmitted property.
     * 
     */
    public void setLSubmitted(int value) {
        this.lSubmitted = value;
    }

    /**
     * Gets the value of the lQueued property.
     * 
     */
    public int getLQueued() {
        return lQueued;
    }

    /**
     * Sets the value of the lQueued property.
     * 
     */
    public void setLQueued(int value) {
        this.lQueued = value;
    }

    /**
     * Gets the value of the lSubscribers property.
     * 
     */
    public int getLSubscribers() {
        return lSubscribers;
    }

    /**
     * Sets the value of the lSubscribers property.
     * 
     */
    public void setLSubscribers(int value) {
        this.lSubscribers = value;
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
