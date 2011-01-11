
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
 *         &lt;element name="GetSendSettingsResult" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
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
    "getSendSettingsResult"
})
@XmlRootElement(name = "GetSendSettingsResponse")
public class GetSendSettingsResponse {

    @XmlElement(name = "GetSendSettingsResult")
    protected byte[] getSendSettingsResult;

    /**
     * Gets the value of the getSendSettingsResult property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getGetSendSettingsResult() {
        return getSendSettingsResult;
    }

    /**
     * Sets the value of the getSendSettingsResult property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setGetSendSettingsResult(byte[] value) {
        this.getSendSettingsResult = ((byte[]) value);
    }

}
