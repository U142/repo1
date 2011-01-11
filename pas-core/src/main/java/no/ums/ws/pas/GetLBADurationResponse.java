
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
 *         &lt;element name="GetLBADurationResult" type="{http://ums.no/ws/pas/}ArrayOfULBADURATION" minOccurs="0"/>
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
    "getLBADurationResult"
})
@XmlRootElement(name = "GetLBADurationResponse")
public class GetLBADurationResponse {

    @XmlElement(name = "GetLBADurationResult")
    protected ArrayOfULBADURATION getLBADurationResult;

    /**
     * Gets the value of the getLBADurationResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBADURATION }
     *     
     */
    public ArrayOfULBADURATION getGetLBADurationResult() {
        return getLBADurationResult;
    }

    /**
     * Sets the value of the getLBADurationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBADURATION }
     *     
     */
    public void setGetLBADurationResult(ArrayOfULBADURATION value) {
        this.getLBADurationResult = value;
    }

}
