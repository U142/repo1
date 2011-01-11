
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
 *         &lt;element name="GetAdrCountPolygonResult" type="{http://ums.no/ws/parm/}UAdrCount" minOccurs="0"/>
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
    "getAdrCountPolygonResult"
})
@XmlRootElement(name = "GetAdrCountPolygonResponse")
public class GetAdrCountPolygonResponse {

    @XmlElement(name = "GetAdrCountPolygonResult")
    protected UAdrCount getAdrCountPolygonResult;

    /**
     * Gets the value of the getAdrCountPolygonResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAdrCount }
     *     
     */
    public UAdrCount getGetAdrCountPolygonResult() {
        return getAdrCountPolygonResult;
    }

    /**
     * Sets the value of the getAdrCountPolygonResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAdrCount }
     *     
     */
    public void setGetAdrCountPolygonResult(UAdrCount value) {
        this.getAdrCountPolygonResult = value;
    }

}
