
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
 *         &lt;element name="ConvertTTSResult" type="{http://ums.no/ws/pas/}UCONVERT_TTS_RESPONSE" minOccurs="0"/>
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
    "convertTTSResult"
})
@XmlRootElement(name = "ConvertTTSResponse")
public class ConvertTTSResponse {

    @XmlElement(name = "ConvertTTSResult")
    protected UCONVERTTTSRESPONSE convertTTSResult;

    /**
     * Gets the value of the convertTTSResult property.
     * 
     * @return
     *     possible object is
     *     {@link UCONVERTTTSRESPONSE }
     *     
     */
    public UCONVERTTTSRESPONSE getConvertTTSResult() {
        return convertTTSResult;
    }

    /**
     * Sets the value of the convertTTSResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UCONVERTTTSRESPONSE }
     *     
     */
    public void setConvertTTSResult(UCONVERTTTSRESPONSE value) {
        this.convertTTSResult = value;
    }

}
