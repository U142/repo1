
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
 *         &lt;element name="logon" type="{http://ums.no/ws/parm/}ULOGONINFO"/>
 *         &lt;element name="s" type="{http://ums.no/ws/parm/}UMUNICIPALSENDING" minOccurs="0"/>
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
    "s"
})
@XmlRootElement(name = "GetAdrCountMunicipal")
public class GetAdrCountMunicipal {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    protected UMUNICIPALSENDING s;

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
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link UMUNICIPALSENDING }
     *     
     */
    public UMUNICIPALSENDING getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMUNICIPALSENDING }
     *     
     */
    public void setS(UMUNICIPALSENDING value) {
        this.s = value;
    }

}
