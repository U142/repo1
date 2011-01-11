
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
 *         &lt;element name="TestResult" type="{http://ums.no/ws/pas/}UAddressList" minOccurs="0"/>
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
    "testResult"
})
@XmlRootElement(name = "TestResponse")
public class TestResponse {

    @XmlElement(name = "TestResult")
    protected UAddressList testResult;

    /**
     * Gets the value of the testResult property.
     * 
     * @return
     *     possible object is
     *     {@link UAddressList }
     *     
     */
    public UAddressList getTestResult() {
        return testResult;
    }

    /**
     * Sets the value of the testResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAddressList }
     *     
     */
    public void setTestResult(UAddressList value) {
        this.testResult = value;
    }

}
