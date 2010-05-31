
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
 *         &lt;element name="TestGabSearchResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "testGabSearchResult"
})
@XmlRootElement(name = "TestGabSearchResponse")
public class TestGabSearchResponse {

    @XmlElement(name = "TestGabSearchResult")
    protected String testGabSearchResult;

    /**
     * Gets the value of the testGabSearchResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestGabSearchResult() {
        return testGabSearchResult;
    }

    /**
     * Sets the value of the testGabSearchResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestGabSearchResult(String value) {
        this.testGabSearchResult = value;
    }

}
