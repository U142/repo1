
package no.ums.ws.pas;

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
 *         &lt;element name="logon" type="{http://ums.no/ws/pas/}ULOGONINFO"/>
 *         &lt;element name="mapsending" type="{http://ums.no/ws/pas/}UMAPSENDING" minOccurs="0"/>
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
    "mapsending"
})
@XmlRootElement(name = "GetAdrCount")
public class GetAdrCount {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    protected UMAPSENDING mapsending;

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
     * Gets the value of the mapsending property.
     * 
     * @return
     *     possible object is
     *     {@link UMAPSENDING }
     *     
     */
    public UMAPSENDING getMapsending() {
        return mapsending;
    }

    /**
     * Sets the value of the mapsending property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMAPSENDING }
     *     
     */
    public void setMapsending(UMAPSENDING value) {
        this.mapsending = value;
    }

}
