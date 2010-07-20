
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBADURATION complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBADURATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_durationpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_duration" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_interval" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_repetition" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBADURATION", propOrder = {
    "nDurationpk",
    "nDuration",
    "nInterval",
    "nRepetition",
    "nDeptpk"
})
public class ULBADURATION {

    @XmlElement(name = "n_durationpk")
    protected long nDurationpk;
    @XmlElement(name = "n_duration")
    protected int nDuration;
    @XmlElement(name = "n_interval")
    protected int nInterval;
    @XmlElement(name = "n_repetition")
    protected int nRepetition;
    @XmlElement(name = "n_deptpk")
    protected int nDeptpk;

    /**
     * Gets the value of the nDurationpk property.
     * 
     */
    public long getNDurationpk() {
        return nDurationpk;
    }

    /**
     * Sets the value of the nDurationpk property.
     * 
     */
    public void setNDurationpk(long value) {
        this.nDurationpk = value;
    }

    /**
     * Gets the value of the nDuration property.
     * 
     */
    public int getNDuration() {
        return nDuration;
    }

    /**
     * Sets the value of the nDuration property.
     * 
     */
    public void setNDuration(int value) {
        this.nDuration = value;
    }

    /**
     * Gets the value of the nInterval property.
     * 
     */
    public int getNInterval() {
        return nInterval;
    }

    /**
     * Sets the value of the nInterval property.
     * 
     */
    public void setNInterval(int value) {
        this.nInterval = value;
    }

    /**
     * Gets the value of the nRepetition property.
     * 
     */
    public int getNRepetition() {
        return nRepetition;
    }

    /**
     * Sets the value of the nRepetition property.
     * 
     */
    public void setNRepetition(int value) {
        this.nRepetition = value;
    }

    /**
     * Gets the value of the nDeptpk property.
     * 
     */
    public int getNDeptpk() {
        return nDeptpk;
    }

    /**
     * Sets the value of the nDeptpk property.
     * 
     */
    public void setNDeptpk(int value) {
        this.nDeptpk = value;
    }

}
