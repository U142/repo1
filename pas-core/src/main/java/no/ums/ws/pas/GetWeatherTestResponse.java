
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
 *         &lt;element name="GetWeatherTestResult" type="{http://ums.no/ws/pas/}UWeatherReportResults" minOccurs="0"/>
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
    "getWeatherTestResult"
})
@XmlRootElement(name = "GetWeatherTestResponse")
public class GetWeatherTestResponse {

    @XmlElement(name = "GetWeatherTestResult")
    protected UWeatherReportResults getWeatherTestResult;

    /**
     * Gets the value of the getWeatherTestResult property.
     * 
     * @return
     *     possible object is
     *     {@link UWeatherReportResults }
     *     
     */
    public UWeatherReportResults getGetWeatherTestResult() {
        return getWeatherTestResult;
    }

    /**
     * Sets the value of the getWeatherTestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UWeatherReportResults }
     *     
     */
    public void setGetWeatherTestResult(UWeatherReportResults value) {
        this.getWeatherTestResult = value;
    }

}
