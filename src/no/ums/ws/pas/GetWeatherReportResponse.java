
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
 *         &lt;element name="GetWeatherReportResult" type="{http://ums.no/ws/pas/}UWeatherReportResults" minOccurs="0"/>
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
    "getWeatherReportResult"
})
@XmlRootElement(name = "GetWeatherReportResponse")
public class GetWeatherReportResponse {

    @XmlElement(name = "GetWeatherReportResult")
    protected UWeatherReportResults getWeatherReportResult;

    /**
     * Gets the value of the getWeatherReportResult property.
     * 
     * @return
     *     possible object is
     *     {@link UWeatherReportResults }
     *     
     */
    public UWeatherReportResults getGetWeatherReportResult() {
        return getWeatherReportResult;
    }

    /**
     * Sets the value of the getWeatherReportResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UWeatherReportResults }
     *     
     */
    public void setGetWeatherReportResult(UWeatherReportResults value) {
        this.getWeatherReportResult = value;
    }

}
