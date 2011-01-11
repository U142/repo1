
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
 *         &lt;element name="GetLBAMessageLibraryResult" type="{http://ums.no/ws/pas/}ULBAMESSAGELIST" minOccurs="0"/>
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
    "getLBAMessageLibraryResult"
})
@XmlRootElement(name = "GetLBAMessageLibraryResponse")
public class GetLBAMessageLibraryResponse {

    @XmlElement(name = "GetLBAMessageLibraryResult")
    protected ULBAMESSAGELIST getLBAMessageLibraryResult;

    /**
     * Gets the value of the getLBAMessageLibraryResult property.
     * 
     * @return
     *     possible object is
     *     {@link ULBAMESSAGELIST }
     *     
     */
    public ULBAMESSAGELIST getGetLBAMessageLibraryResult() {
        return getLBAMessageLibraryResult;
    }

    /**
     * Sets the value of the getLBAMessageLibraryResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULBAMESSAGELIST }
     *     
     */
    public void setGetLBAMessageLibraryResult(ULBAMESSAGELIST value) {
        this.getLBAMessageLibraryResult = value;
    }

}
