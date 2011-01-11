
package no.ums.ws.pas.tas;

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
 *         &lt;element name="GetStatsCountriesPerTimeunitResult" type="{http://ums.no/ws/pas/tas}ArrayOfULBACOUNTRYSTATISTICS" minOccurs="0"/>
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
    "getStatsCountriesPerTimeunitResult"
})
@XmlRootElement(name = "GetStatsCountriesPerTimeunitResponse")
public class GetStatsCountriesPerTimeunitResponse {

    @XmlElement(name = "GetStatsCountriesPerTimeunitResult")
    protected ArrayOfULBACOUNTRYSTATISTICS getStatsCountriesPerTimeunitResult;

    /**
     * Gets the value of the getStatsCountriesPerTimeunitResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBACOUNTRYSTATISTICS }
     *     
     */
    public ArrayOfULBACOUNTRYSTATISTICS getGetStatsCountriesPerTimeunitResult() {
        return getStatsCountriesPerTimeunitResult;
    }

    /**
     * Sets the value of the getStatsCountriesPerTimeunitResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBACOUNTRYSTATISTICS }
     *     
     */
    public void setGetStatsCountriesPerTimeunitResult(ArrayOfULBACOUNTRYSTATISTICS value) {
        this.getStatsCountriesPerTimeunitResult = value;
    }

}
