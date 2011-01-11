
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
 *         &lt;element name="filter" type="{http://ums.no/ws/pas/tas}ULBASTATISTICS_FILTER" minOccurs="0"/>
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
    "filter"
})
@XmlRootElement(name = "GetStatsCountriesPerTimeunit")
public class GetStatsCountriesPerTimeunit {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    protected ULBASTATISTICSFILTER filter;

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
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link ULBASTATISTICSFILTER }
     *     
     */
    public ULBASTATISTICSFILTER getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULBASTATISTICSFILTER }
     *     
     */
    public void setFilter(ULBASTATISTICSFILTER value) {
        this.filter = value;
    }

}
