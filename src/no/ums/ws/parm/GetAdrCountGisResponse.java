
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
 *         &lt;element name="GetAdrCountGisResult" type="{http://ums.no/ws/parm/}UAdrCount" minOccurs="0"/>
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
    "getAdrCountGisResult"
})
@XmlRootElement(name = "GetAdrCountGisResponse")
public class GetAdrCountGisResponse {

    @XmlElement(name = "GetAdrCountGisResult")
    protected UAdrCount getAdrCountGisResult;

    /**
     * Gets the value of the getAdrCountGisResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAdrCount }
     *     
     */
    public UAdrCount getGetAdrCountGisResult() {
        return getAdrCountGisResult;
    }

    /**
     * Sets the value of the getAdrCountGisResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAdrCount }
     *     
     */
    public void setGetAdrCountGisResult(UAdrCount value) {
        this.getAdrCountGisResult = value;
    }

}
