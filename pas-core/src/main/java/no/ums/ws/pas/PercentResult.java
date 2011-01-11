
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PercentResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PercentResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_percent" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_totalrecords" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_currentrecord" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PercentResult", propOrder = {
    "nPercent",
    "nTotalrecords",
    "nCurrentrecord"
})
public class PercentResult {

    @XmlElement(name = "n_percent")
    protected int nPercent;
    @XmlElement(name = "n_totalrecords")
    protected int nTotalrecords;
    @XmlElement(name = "n_currentrecord")
    protected int nCurrentrecord;

    /**
     * Gets the value of the nPercent property.
     * 
     */
    public int getNPercent() {
        return nPercent;
    }

    /**
     * Sets the value of the nPercent property.
     * 
     */
    public void setNPercent(int value) {
        this.nPercent = value;
    }

    /**
     * Gets the value of the nTotalrecords property.
     * 
     */
    public int getNTotalrecords() {
        return nTotalrecords;
    }

    /**
     * Sets the value of the nTotalrecords property.
     * 
     */
    public void setNTotalrecords(int value) {
        this.nTotalrecords = value;
    }

    /**
     * Gets the value of the nCurrentrecord property.
     * 
     */
    public int getNCurrentrecord() {
        return nCurrentrecord;
    }

    /**
     * Sets the value of the nCurrentrecord property.
     * 
     */
    public void setNCurrentrecord(int value) {
        this.nCurrentrecord = value;
    }

}
