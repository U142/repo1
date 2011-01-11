
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
 *         &lt;element name="UPostAudioResult" type="{http://ums.no/ws/pas/}AUDIO_RESPONSE" minOccurs="0"/>
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
    "uPostAudioResult"
})
@XmlRootElement(name = "UPostAudioResponse")
public class UPostAudioResponse {

    @XmlElement(name = "UPostAudioResult")
    protected AUDIORESPONSE uPostAudioResult;

    /**
     * Gets the value of the uPostAudioResult property.
     * 
     * @return
     *     possible object is
     *     {@link AUDIORESPONSE }
     *     
     */
    public AUDIORESPONSE getUPostAudioResult() {
        return uPostAudioResult;
    }

    /**
     * Sets the value of the uPostAudioResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link AUDIORESPONSE }
     *     
     */
    public void setUPostAudioResult(AUDIORESPONSE value) {
        this.uPostAudioResult = value;
    }

}
