
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
 *         &lt;element name="DeleteMessageLibraryResult" type="{http://ums.no/ws/pas/}UBBMESSAGE" minOccurs="0"/>
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
    "deleteMessageLibraryResult"
})
@XmlRootElement(name = "DeleteMessageLibraryResponse")
public class DeleteMessageLibraryResponse {

    @XmlElement(name = "DeleteMessageLibraryResult")
    protected UBBMESSAGE deleteMessageLibraryResult;

    /**
     * Gets the value of the deleteMessageLibraryResult property.
     * 
     * @return
     *     possible object is
     *     {@link UBBMESSAGE }
     *     
     */
    public UBBMESSAGE getDeleteMessageLibraryResult() {
        return deleteMessageLibraryResult;
    }

    /**
     * Sets the value of the deleteMessageLibraryResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBMESSAGE }
     *     
     */
    public void setDeleteMessageLibraryResult(UBBMESSAGE value) {
        this.deleteMessageLibraryResult = value;
    }

}
