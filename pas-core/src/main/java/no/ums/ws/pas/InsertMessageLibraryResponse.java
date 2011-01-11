
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
 *         &lt;element name="InsertMessageLibraryResult" type="{http://ums.no/ws/pas/}UBBMESSAGE" minOccurs="0"/>
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
    "insertMessageLibraryResult"
})
@XmlRootElement(name = "InsertMessageLibraryResponse")
public class InsertMessageLibraryResponse {

    @XmlElement(name = "InsertMessageLibraryResult")
    protected UBBMESSAGE insertMessageLibraryResult;

    /**
     * Gets the value of the insertMessageLibraryResult property.
     * 
     * @return
     *     possible object is
     *     {@link UBBMESSAGE }
     *     
     */
    public UBBMESSAGE getInsertMessageLibraryResult() {
        return insertMessageLibraryResult;
    }

    /**
     * Sets the value of the insertMessageLibraryResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBMESSAGE }
     *     
     */
    public void setInsertMessageLibraryResult(UBBMESSAGE value) {
        this.insertMessageLibraryResult = value;
    }

}
