
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
 *         &lt;element name="country" type="{http://ums.no/ws/pas/tas}ArrayOfULBACOUNTRY" minOccurs="0"/>
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
    "country"
})
@XmlRootElement(name = "GetAdrCount")
public class GetAdrCount {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    protected ArrayOfULBACOUNTRY country;

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
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBACOUNTRY }
     *     
     */
    public ArrayOfULBACOUNTRY getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBACOUNTRY }
     *     
     */
    public void setCountry(ArrayOfULBACOUNTRY value) {
        this.country = value;
    }

}
