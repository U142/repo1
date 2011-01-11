
package no.ums.ws.pas.status;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPolygon complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPolygon">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/status}UShape">
 *       &lt;sequence>
 *         &lt;element name="polypoint" type="{http://ums.no/ws/pas/status}UPolypoint" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPolygon", propOrder = {
    "polypoint"
})
public class UPolygon
    extends UShape
{

    protected List<UPolypoint> polypoint;

    /**
     * Gets the value of the polypoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the polypoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolypoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UPolypoint }
     * 
     * 
     */
    public List<UPolypoint> getPolypoint() {
        if (polypoint == null) {
            polypoint = new ArrayList<UPolypoint>();
        }
        return this.polypoint;
    }

}
