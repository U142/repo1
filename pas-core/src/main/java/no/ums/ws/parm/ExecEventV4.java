
package no.ums.ws.parm;

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
 *         &lt;element name="l_eventpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="logon" type="{http://ums.no/ws/parm/}ULOGONINFO"/>
 *         &lt;element name="sz_function" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_scheddate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_schedtime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "lEventpk",
    "logon",
    "szFunction",
    "szScheddate",
    "szSchedtime"
})
@XmlRootElement(name = "ExecEventV4")
public class ExecEventV4 {

    @XmlElement(name = "l_eventpk")
    protected long lEventpk;
    @XmlElement(required = true)
    protected ULOGONINFO logon;
    @XmlElement(name = "sz_function")
    protected String szFunction;
    @XmlElement(name = "sz_scheddate")
    protected String szScheddate;
    @XmlElement(name = "sz_schedtime")
    protected String szSchedtime;

    /**
     * Gets the value of the lEventpk property.
     * 
     */
    public long getLEventpk() {
        return lEventpk;
    }

    /**
     * Sets the value of the lEventpk property.
     * 
     */
    public void setLEventpk(long value) {
        this.lEventpk = value;
    }

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
     * Gets the value of the szFunction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzFunction() {
        return szFunction;
    }

    /**
     * Sets the value of the szFunction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzFunction(String value) {
        this.szFunction = value;
    }

    /**
     * Gets the value of the szScheddate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzScheddate() {
        return szScheddate;
    }

    /**
     * Sets the value of the szScheddate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzScheddate(String value) {
        this.szScheddate = value;
    }

    /**
     * Gets the value of the szSchedtime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSchedtime() {
        return szSchedtime;
    }

    /**
     * Sets the value of the szSchedtime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSchedtime(String value) {
        this.szSchedtime = value;
    }

}
