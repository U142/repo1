
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UBBMESSAGELISTFILTER complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UBBMESSAGELISTFILTER">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_timefilter" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UBBMESSAGELISTFILTER", propOrder = {
    "nTimefilter"
})
public class UBBMESSAGELISTFILTER {

    @XmlElement(name = "n_timefilter")
    protected long nTimefilter;

    /**
     * Gets the value of the nTimefilter property.
     * 
     */
    public long getNTimefilter() {
        return nTimefilter;
    }

    /**
     * Sets the value of the nTimefilter property.
     * 
     */
    public void setNTimefilter(long value) {
        this.nTimefilter = value;
    }

}
