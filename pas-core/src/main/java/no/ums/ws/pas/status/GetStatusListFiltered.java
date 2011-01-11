
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
 *         &lt;element name="filter" type="{http://ums.no/ws/pas/status}UDATAFILTER"/>
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
    "filter"
})
@XmlRootElement(name = "GetStatusListFiltered")
public class GetStatusListFiltered {

    @XmlElement(required = true)
    protected ULOGONINFO logoninfo;
    @XmlElement(required = true)
    protected UDATAFILTER filter;

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
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link UDATAFILTER }
     *     
     */
    public UDATAFILTER getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link UDATAFILTER }
     *     
     */
    public void setFilter(UDATAFILTER value) {
        this.filter = value;
    }

}
