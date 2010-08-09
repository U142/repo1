
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_PROJECT_STATUS_REQUEST complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_PROJECT_STATUS_REQUEST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="logon" type="{http://ums.no/ws/pas/status}ULOGONINFO"/>
 *         &lt;element name="l_projectpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_timefilter" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_PROJECT_STATUS_REQUEST", propOrder = {
    "logon",
    "lProjectpk",
    "lTimefilter"
})
public class CBPROJECTSTATUSREQUEST {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    @XmlElement(name = "l_projectpk")
    protected long lProjectpk;
    @XmlElement(name = "l_timefilter")
    protected long lTimefilter;

    /**
     * Gets the value of the logon property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getLogon() {
        return logon;
    }

    /**
     * Sets the value of the logon property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setLogon(ULOGONINFO value) {
        this.logon = value;
    }

    /**
     * Gets the value of the lProjectpk property.
     * 
     */
    public long getLProjectpk() {
        return lProjectpk;
    }

    /**
     * Sets the value of the lProjectpk property.
     * 
     */
    public void setLProjectpk(long value) {
        this.lProjectpk = value;
    }

    /**
     * Gets the value of the lTimefilter property.
     * 
     */
    public long getLTimefilter() {
        return lTimefilter;
    }

    /**
     * Sets the value of the lTimefilter property.
     * 
     */
    public void setLTimefilter(long value) {
        this.lTimefilter = value;
    }

}
