
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
 *         &lt;element name="GetGisByStreetIdResult" type="{http://ums.no/ws/pas/}UGisImportResultsByStreetId" minOccurs="0"/>
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
    "getGisByStreetIdResult"
})
@XmlRootElement(name = "GetGisByStreetIdResponse")
public class GetGisByStreetIdResponse {

    @XmlElement(name = "GetGisByStreetIdResult")
    protected UGisImportResultsByStreetId getGisByStreetIdResult;

    /**
     * Gets the value of the getGisByStreetIdResult property.
     * 
     * @return
     *     possible object is
     *     {@link UGisImportResultsByStreetId }
     *     
     */
    public UGisImportResultsByStreetId getGetGisByStreetIdResult() {
        return getGisByStreetIdResult;
    }

    /**
     * Sets the value of the getGisByStreetIdResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UGisImportResultsByStreetId }
     *     
     */
    public void setGetGisByStreetIdResult(UGisImportResultsByStreetId value) {
        this.getGisByStreetIdResult = value;
    }

}
