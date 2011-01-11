
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfUMapPoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUMapPoint">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UMapPoint" type="{http://ums.no/ws/pas/}UMapPoint" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUMapPoint", propOrder = {
    "uMapPoint"
})
public class ArrayOfUMapPoint {

    @XmlElement(name = "UMapPoint", nillable = true)
    protected List<UMapPoint> uMapPoint;

    /**
     * Gets the value of the uMapPoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uMapPoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUMapPoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UMapPoint }
     * 
     * 
     */
    public List<UMapPoint> getUMapPoint() {
        if (uMapPoint == null) {
            uMapPoint = new ArrayList<UMapPoint>();
        }
        return this.uMapPoint;
    }

}
