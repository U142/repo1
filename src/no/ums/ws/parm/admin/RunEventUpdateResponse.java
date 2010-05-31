
package no.ums.ws.parm.admin;

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
 *         &lt;element name="RunEventUpdateResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="logon" type="{http://ums.no/ws/parm/admin/}ULOGONINFO"/>
 *         &lt;element name="ev" type="{http://ums.no/ws/parm/admin/}PAEVENT" minOccurs="0"/>
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
    "runEventUpdateResult",
    "logon",
    "ev"
})
@XmlRootElement(name = "RunEventUpdateResponse")
public class RunEventUpdateResponse {

    @XmlElement(name = "RunEventUpdateResult")
    protected boolean runEventUpdateResult;
    @XmlElement(required = true)
    protected ULOGONINFO logon;
    protected PAEVENT ev;

    /**
     * Gets the value of the runEventUpdateResult property.
     * 
     */
    public boolean isRunEventUpdateResult() {
        return runEventUpdateResult;
    }

    /**
     * Sets the value of the runEventUpdateResult property.
     * 
     */
    public void setRunEventUpdateResult(boolean value) {
        this.runEventUpdateResult = value;
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
     * Gets the value of the ev property.
     * 
     * @return
     *     possible object is
     *     {@link PAEVENT }
     *     
     */
    public PAEVENT getEv() {
        return ev;
    }

    /**
     * Sets the value of the ev property.
     * 
     * @param value
     *     allowed object is
     *     {@link PAEVENT }
     *     
     */
    public void setEv(PAEVENT value) {
        this.ev = value;
    }

}
