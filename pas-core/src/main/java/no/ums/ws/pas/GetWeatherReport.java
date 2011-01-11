
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
 *         &lt;element name="l" type="{http://ums.no/ws/pas/}ULOGONINFO"/>
 *         &lt;element name="s" type="{http://ums.no/ws/pas/}UWeatherSearch" minOccurs="0"/>
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
    "l",
    "s"
})
@XmlRootElement(name = "GetWeatherReport")
public class GetWeatherReport {

    @XmlElement(required = true)
    protected ULOGONINFO l;
    protected UWeatherSearch s;

    /**
     * Gets the value of the l property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getL() {
        return l;
    }

    /**
     * Sets the value of the l property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setL(ULOGONINFO value) {
        this.l = value;
    }

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link UWeatherSearch }
     *     
     */
    public UWeatherSearch getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link UWeatherSearch }
     *     
     */
    public void setS(UWeatherSearch value) {
        this.s = value;
    }

}
