
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
 *         &lt;element name="GetNearestTestResult" type="{http://ums.no/ws/pas/}UGabResultFromPoint" minOccurs="0"/>
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
    "getNearestTestResult"
})
@XmlRootElement(name = "GetNearestTestResponse")
public class GetNearestTestResponse {

    @XmlElement(name = "GetNearestTestResult")
    protected UGabResultFromPoint getNearestTestResult;

    /**
     * Gets the value of the getNearestTestResult property.
     * 
     * @return
     *     possible object is
     *     {@link UGabResultFromPoint }
     *     
     */
    public UGabResultFromPoint getGetNearestTestResult() {
        return getNearestTestResult;
    }

    /**
     * Sets the value of the getNearestTestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UGabResultFromPoint }
     *     
     */
    public void setGetNearestTestResult(UGabResultFromPoint value) {
        this.getNearestTestResult = value;
    }

}
