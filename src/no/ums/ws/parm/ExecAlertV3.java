
package no.ums.ws.parm;

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
 *         &lt;element name="l_alertpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
    "lAlertpk",
    "logon",
    "szFunction",
    "szScheddate",
    "szSchedtime"
})
@XmlRootElement(name = "ExecAlertV3")
public class ExecAlertV3 {

    @XmlElement(name = "l_alertpk")
    protected long lAlertpk;
    @XmlElement(required = true)
    protected ULOGONINFO logon;
    @XmlElement(name = "sz_function")
    protected String szFunction;
    @XmlElement(name = "sz_scheddate")
    protected String szScheddate;
    @XmlElement(name = "sz_schedtime")
    protected String szSchedtime;

    /**
     * Gets the value of the lAlertpk property.
     * 
     */
    public long getLAlertpk() {
        return lAlertpk;
    }

    /**
     * Sets the value of the lAlertpk property.
     * 
     */
    public void setLAlertpk(long value) {
        this.lAlertpk = value;
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
