
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfUWeatherResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUWeatherResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UWeatherResult" type="{http://ums.no/ws/pas/}UWeatherResult" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUWeatherResult", propOrder = {
    "uWeatherResult"
})
public class ArrayOfUWeatherResult {

    @XmlElement(name = "UWeatherResult", nillable = true)
    protected List<UWeatherResult> uWeatherResult;

    /**
     * Gets the value of the uWeatherResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uWeatherResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUWeatherResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UWeatherResult }
     * 
     * 
     */
    public List<UWeatherResult> getUWeatherResult() {
        if (uWeatherResult == null) {
            uWeatherResult = new ArrayList<UWeatherResult>();
        }
        return this.uWeatherResult;
    }

}
