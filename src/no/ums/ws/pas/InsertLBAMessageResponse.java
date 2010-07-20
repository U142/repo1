
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
 *         &lt;element name="InsertLBAMessageResult" type="{http://ums.no/ws/pas/}ULBAMESSAGE" minOccurs="0"/>
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
    "insertLBAMessageResult"
})
@XmlRootElement(name = "InsertLBAMessageResponse")
public class InsertLBAMessageResponse {

    @XmlElement(name = "InsertLBAMessageResult")
    protected ULBAMESSAGE insertLBAMessageResult;

    /**
     * Gets the value of the insertLBAMessageResult property.
     * 
     * @return
     *     possible object is
     *     {@link ULBAMESSAGE }
     *     
     */
    public ULBAMESSAGE getInsertLBAMessageResult() {
        return insertLBAMessageResult;
    }

    /**
     * Sets the value of the insertLBAMessageResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULBAMESSAGE }
     *     
     */
    public void setInsertLBAMessageResult(ULBAMESSAGE value) {
        this.insertLBAMessageResult = value;
    }

}
