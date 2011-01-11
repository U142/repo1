
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
 *         &lt;element name="InsertMessageLibResult" type="{http://ums.no/ws/pas/}UBBMESSAGE" minOccurs="0"/>
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
    "insertMessageLibResult"
})
@XmlRootElement(name = "InsertMessageLibResponse")
public class InsertMessageLibResponse {

    @XmlElement(name = "InsertMessageLibResult")
    protected UBBMESSAGE insertMessageLibResult;

    /**
     * Gets the value of the insertMessageLibResult property.
     * 
     * @return
     *     possible object is
     *     {@link UBBMESSAGE }
     *     
     */
    public UBBMESSAGE getInsertMessageLibResult() {
        return insertMessageLibResult;
    }

    /**
     * Sets the value of the insertMessageLibResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBMESSAGE }
     *     
     */
    public void setInsertMessageLibResult(UBBMESSAGE value) {
        this.insertMessageLibResult = value;
    }

}
