
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="gis" type="{http://ums.no/ws/parm/}UGISSENDING" minOccurs="0"/>
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
    "gis"
})
@XmlRootElement(name = "ExecGisSending")
public class ExecGisSending {

    protected UGISSENDING gis;

    /**
     * Gets the value of the gis property.
     * 
     * @return
     *     possible object is
     *     {@link UGISSENDING }
     *     
     */
    public UGISSENDING getGis() {
        return gis;
    }

    /**
     * Sets the value of the gis property.
     * 
     * @param value
     *     allowed object is
     *     {@link UGISSENDING }
     *     
     */
    public void setGis(UGISSENDING value) {
        this.gis = value;
    }

}
