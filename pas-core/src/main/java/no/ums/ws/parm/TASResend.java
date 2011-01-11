
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
 *         &lt;element name="refno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="lbaoperator" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="logoninfo" type="{http://ums.no/ws/parm/}ULOGONINFO"/>
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
    "refno",
    "lbaoperator",
    "logoninfo"
})
@XmlRootElement(name = "TASResend")
public class TASResend {

    protected int refno;
    protected int lbaoperator;
    @XmlElement(required = true)
    protected ULOGONINFO logoninfo;

    /**
     * Gets the value of the refno property.
     * 
     */
    public int getRefno() {
        return refno;
    }

    /**
     * Sets the value of the refno property.
     * 
     */
    public void setRefno(int value) {
        this.refno = value;
    }

    /**
     * Gets the value of the lbaoperator property.
     * 
     */
    public int getLbaoperator() {
        return lbaoperator;
    }

    /**
     * Sets the value of the lbaoperator property.
     * 
     */
    public void setLbaoperator(int value) {
        this.lbaoperator = value;
    }

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

}
