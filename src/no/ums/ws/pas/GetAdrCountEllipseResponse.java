
package no.ums.ws.pas;

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
 *         &lt;element name="GetAdrCountEllipseResult" type="{http://ums.no/ws/pas/}UAdrCount" minOccurs="0"/>
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
    "getAdrCountEllipseResult"
})
@XmlRootElement(name = "GetAdrCountEllipseResponse")
public class GetAdrCountEllipseResponse {

    @XmlElement(name = "GetAdrCountEllipseResult")
    protected UAdrCount getAdrCountEllipseResult;

    /**
     * Gets the value of the getAdrCountEllipseResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAdrCount }
     *     
     */
    public UAdrCount getGetAdrCountEllipseResult() {
        return getAdrCountEllipseResult;
    }

    /**
     * Sets the value of the getAdrCountEllipseResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAdrCount }
     *     
     */
    public void setGetAdrCountEllipseResult(UAdrCount value) {
        this.getAdrCountEllipseResult = value;
    }

}
