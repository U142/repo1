
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
 *         &lt;element name="search" type="{http://ums.no/ws/pas/}UGisImportParamsByStreetId" minOccurs="0"/>
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
    "search"
})
@XmlRootElement(name = "GetGisByStreetId")
public class GetGisByStreetId {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    protected UGisImportParamsByStreetId search;

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
     * Gets the value of the search property.
     * 
     * @return
     *     possible object is
     *     {@link UGisImportParamsByStreetId }
     *     
     */
    public UGisImportParamsByStreetId getSearch() {
        return search;
    }

    /**
     * Sets the value of the search property.
     * 
     * @param value
     *     allowed object is
     *     {@link UGisImportParamsByStreetId }
     *     
     */
    public void setSearch(UGisImportParamsByStreetId value) {
        this.search = value;
    }

}
