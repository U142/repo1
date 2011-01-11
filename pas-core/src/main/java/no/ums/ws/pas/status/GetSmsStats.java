
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
 *         &lt;element name="n_refno" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
    "nRefno"
})
@XmlRootElement(name = "GetSmsStats")
public class GetSmsStats {

    @XmlElement(required = true)
    protected ULOGONINFO logoninfo;
    @XmlElement(name = "n_refno")
    protected long nRefno;

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
     * Gets the value of the nRefno property.
     * 
     */
    public long getNRefno() {
        return nRefno;
    }

    /**
     * Sets the value of the nRefno property.
     * 
     */
    public void setNRefno(long value) {
        this.nRefno = value;
    }

}
