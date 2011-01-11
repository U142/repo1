
package no.ums.ws.pas.tas;

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
 *         &lt;element name="logon" type="{http://ums.no/ws/pas/tas}ULOGONINFO"/>
 *         &lt;element name="timefilter_count" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timefilter_requestlog" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
    "logon",
    "timefilterCount",
    "timefilterRequestlog"
})
@XmlRootElement(name = "GetContinentsAndCountries")
public class GetContinentsAndCountries {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    @XmlElement(name = "timefilter_count")
    protected long timefilterCount;
    @XmlElement(name = "timefilter_requestlog")
    protected long timefilterRequestlog;

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
     * Gets the value of the timefilterCount property.
     * 
     */
    public long getTimefilterCount() {
        return timefilterCount;
    }

    /**
     * Sets the value of the timefilterCount property.
     * 
     */
    public void setTimefilterCount(long value) {
        this.timefilterCount = value;
    }

    /**
     * Gets the value of the timefilterRequestlog property.
     * 
     */
    public long getTimefilterRequestlog() {
        return timefilterRequestlog;
    }

    /**
     * Sets the value of the timefilterRequestlog property.
     * 
     */
    public void setTimefilterRequestlog(long value) {
        this.timefilterRequestlog = value;
    }

}
