
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
 *         &lt;element name="searchparams" type="{http://ums.no/ws/pas/}UMapAddressParams" minOccurs="0"/>
 *         &lt;element name="logoninfo" type="{http://ums.no/ws/pas/}ULOGONINFO"/>
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
    "searchparams",
    "logoninfo"
})
@XmlRootElement(name = "GetAddressList")
public class GetAddressList {

    protected UMapAddressParams searchparams;
    @XmlElement(required = true)
    protected ULOGONINFO logoninfo;

    /**
     * Gets the value of the searchparams property.
     * 
     * @return
     *     possible object is
     *     {@link UMapAddressParams }
     *     
     */
    public UMapAddressParams getSearchparams() {
        return searchparams;
    }

    /**
     * Sets the value of the searchparams property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMapAddressParams }
     *     
     */
    public void setSearchparams(UMapAddressParams value) {
        this.searchparams = value;
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
