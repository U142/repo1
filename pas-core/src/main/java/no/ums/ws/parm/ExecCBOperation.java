
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
 *         &lt;element name="logon" type="{http://ums.no/ws/parm/}ULOGONINFO"/>
 *         &lt;element name="cb" type="{http://ums.no/ws/parm/}CB_OPERATION_BASE"/>
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
    "cb"
})
@XmlRootElement(name = "ExecCBOperation")
public class ExecCBOperation {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    @XmlElement(required = true, nillable = true)
    protected CBOPERATIONBASE cb;

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
     * Gets the value of the cb property.
     * 
     * @return
     *     possible object is
     *     {@link CBOPERATIONBASE }
     *     
     */
    public CBOPERATIONBASE getCb() {
        return cb;
    }

    /**
     * Sets the value of the cb property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBOPERATIONBASE }
     *     
     */
    public void setCb(CBOPERATIONBASE value) {
        this.cb = value;
    }

}
