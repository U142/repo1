
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
 *         &lt;element name="InsertLBADurationResult" type="{http://ums.no/ws/pas/}ULBADURATION" minOccurs="0"/>
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
    "insertLBADurationResult"
})
@XmlRootElement(name = "InsertLBADurationResponse")
public class InsertLBADurationResponse {

    @XmlElement(name = "InsertLBADurationResult")
    protected ULBADURATION insertLBADurationResult;

    /**
     * Gets the value of the insertLBADurationResult property.
     * 
     * @return
     *     possible object is
     *     {@link ULBADURATION }
     *     
     */
    public ULBADURATION getInsertLBADurationResult() {
        return insertLBADurationResult;
    }

    /**
     * Sets the value of the insertLBADurationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULBADURATION }
     *     
     */
    public void setInsertLBADurationResult(ULBADURATION value) {
        this.insertLBADurationResult = value;
    }

}
