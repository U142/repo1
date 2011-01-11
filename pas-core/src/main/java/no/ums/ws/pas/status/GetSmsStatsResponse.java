
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
 *         &lt;element name="GetSmsStatsResult" type="{http://ums.no/ws/pas/status}ArrayOfUSMSINSTATS" minOccurs="0"/>
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
    "getSmsStatsResult"
})
@XmlRootElement(name = "GetSmsStatsResponse")
public class GetSmsStatsResponse {

    @XmlElement(name = "GetSmsStatsResult")
    protected ArrayOfUSMSINSTATS getSmsStatsResult;

    /**
     * Gets the value of the getSmsStatsResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUSMSINSTATS }
     *     
     */
    public ArrayOfUSMSINSTATS getGetSmsStatsResult() {
        return getSmsStatsResult;
    }

    /**
     * Sets the value of the getSmsStatsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUSMSINSTATS }
     *     
     */
    public void setGetSmsStatsResult(ArrayOfUSMSINSTATS value) {
        this.getSmsStatsResult = value;
    }

}
