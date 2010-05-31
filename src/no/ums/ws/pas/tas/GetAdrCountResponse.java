
package no.ums.ws.pas.tas;

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
 *         &lt;element name="GetAdrCountResult" type="{http://ums.no/ws/pas/tas}UTASREQUEST" minOccurs="0"/>
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
    "getAdrCountResult"
})
@XmlRootElement(name = "GetAdrCountResponse")
public class GetAdrCountResponse {

    @XmlElement(name = "GetAdrCountResult")
    protected UTASREQUEST getAdrCountResult;

    /**
     * Gets the value of the getAdrCountResult property.
     * 
     * @return
     *     possible object is
     *     {@link UTASREQUEST }
     *     
     */
    public UTASREQUEST getGetAdrCountResult() {
        return getAdrCountResult;
    }

    /**
     * Sets the value of the getAdrCountResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTASREQUEST }
     *     
     */
    public void setGetAdrCountResult(UTASREQUEST value) {
        this.getAdrCountResult = value;
    }

}
