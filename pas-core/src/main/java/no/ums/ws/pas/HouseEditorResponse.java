
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
 *         &lt;element name="HouseEditorResult" type="{http://ums.no/ws/pas/}UAddress" minOccurs="0"/>
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
    "houseEditorResult"
})
@XmlRootElement(name = "HouseEditorResponse")
public class HouseEditorResponse {

    @XmlElement(name = "HouseEditorResult")
    protected UAddress houseEditorResult;

    /**
     * Gets the value of the houseEditorResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAddress }
     *     
     */
    public UAddress getHouseEditorResult() {
        return houseEditorResult;
    }

    /**
     * Sets the value of the houseEditorResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAddress }
     *     
     */
    public void setHouseEditorResult(UAddress value) {
        this.houseEditorResult = value;
    }

}
