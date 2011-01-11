
package no.ums.ws.pas.status;

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
 *         &lt;element name="logon" type="{http://ums.no/ws/pas/status}ULOGONINFO"/>
 *         &lt;element name="l_projectpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
    "lProjectpk"
})
@XmlRootElement(name = "MarkProjectAsFinished")
public class MarkProjectAsFinished {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    @XmlElement(name = "l_projectpk")
    protected long lProjectpk;

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

}
