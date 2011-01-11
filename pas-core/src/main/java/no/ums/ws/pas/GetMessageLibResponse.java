
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
 *         &lt;element name="GetMessageLibResult" type="{http://ums.no/ws/pas/}UBBMESSAGELIST" minOccurs="0"/>
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
    "getMessageLibResult"
})
@XmlRootElement(name = "GetMessageLibResponse")
public class GetMessageLibResponse {

    @XmlElement(name = "GetMessageLibResult")
    protected UBBMESSAGELIST getMessageLibResult;

    /**
     * Gets the value of the getMessageLibResult property.
     * 
     * @return
     *     possible object is
     *     {@link UBBMESSAGELIST }
     *     
     */
    public UBBMESSAGELIST getGetMessageLibResult() {
        return getMessageLibResult;
    }

    /**
     * Sets the value of the getMessageLibResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBMESSAGELIST }
     *     
     */
    public void setGetMessageLibResult(UBBMESSAGELIST value) {
        this.getMessageLibResult = value;
    }

}
