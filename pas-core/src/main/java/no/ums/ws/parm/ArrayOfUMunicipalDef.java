
package no.ums.ws.parm;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfUMunicipalDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfUMunicipalDef">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UMunicipalDef" type="{http://ums.no/ws/parm/}UMunicipalDef" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUMunicipalDef", propOrder = {
    "uMunicipalDef"
})
public class ArrayOfUMunicipalDef {

    @XmlElement(name = "UMunicipalDef", nillable = true)
    protected List<UMunicipalDef> uMunicipalDef;

    /**
     * Gets the value of the uMunicipalDef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uMunicipalDef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUMunicipalDef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UMunicipalDef }
     * 
     * 
     */
    public List<UMunicipalDef> getUMunicipalDef() {
        if (uMunicipalDef == null) {
            uMunicipalDef = new ArrayList<UMunicipalDef>();
        }
        return this.uMunicipalDef;
    }

}
