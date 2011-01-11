
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="logoninfo" type="{http://ums.no/ws/pas/status}ULOGONINFO"/>
 *         &lt;element name="period" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
    "logoninfo",
    "period"
})
@XmlRootElement(name = "GetAllMesagesThisMonth")
public class GetAllMesagesThisMonth {

    @XmlElement(required = true)
    protected ULOGONINFO logoninfo;
    protected long period;

    /**
     * Gets the value of the logoninfo property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getLogoninfo() {
        return logoninfo;
    }

    /**
     * Sets the value of the logoninfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setLogoninfo(ULOGONINFO value) {
        this.logoninfo = value;
    }

    /**
     * Gets the value of the period property.
     * 
     */
    public long getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     */
    public void setPeriod(long value) {
        this.period = value;
    }

}
