
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l" type="{http://ums.no/ws/pas/}ULOGONINFO"/>
 *         &lt;element name="jobType" type="{http://ums.no/ws/pas/}ProgressJobType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "l",
    "jobType"
})
@XmlRootElement(name = "GetProgress")
public class GetProgress {

    @XmlElement(required = true)
    protected ULOGONINFO l;
    @XmlElement(required = true)
    protected ProgressJobType jobType;

    /**
     * Gets the value of the l property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getL() {
        return l;
    }

    /**
     * Sets the value of the l property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setL(ULOGONINFO value) {
        this.l = value;
    }

    /**
     * Gets the value of the jobType property.
     * 
     * @return
     *     possible object is
     *     {@link ProgressJobType }
     *     
     */
    public ProgressJobType getJobType() {
        return jobType;
    }

    /**
     * Sets the value of the jobType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProgressJobType }
     *     
     */
    public void setJobType(ProgressJobType value) {
        this.jobType = value;
    }

}
