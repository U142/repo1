
package no.ums.ws.parm;

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
 *         &lt;element name="GetEventListTestResult" type="{http://ums.no/ws/parm/}ArrayOfPAEVENT" minOccurs="0"/>
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
    "getEventListTestResult"
})
@XmlRootElement(name = "GetEventListTestResponse")
public class GetEventListTestResponse {

    @XmlElement(name = "GetEventListTestResult")
    protected ArrayOfPAEVENT getEventListTestResult;

    /**
     * Gets the value of the getEventListTestResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPAEVENT }
     *     
     */
    public ArrayOfPAEVENT getGetEventListTestResult() {
        return getEventListTestResult;
    }

    /**
     * Sets the value of the getEventListTestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPAEVENT }
     *     
     */
    public void setGetEventListTestResult(ArrayOfPAEVENT value) {
        this.getEventListTestResult = value;
    }

}
