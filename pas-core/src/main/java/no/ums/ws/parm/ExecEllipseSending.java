
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
 *         &lt;element name="ellipse" type="{http://ums.no/ws/parm/}UELLIPSESENDING" minOccurs="0"/>
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
    "ellipse"
})
@XmlRootElement(name = "ExecEllipseSending")
public class ExecEllipseSending {

    protected UELLIPSESENDING ellipse;

    /**
     * Gets the value of the ellipse property.
     * 
     * @return
     *     possible object is
     *     {@link UELLIPSESENDING }
     *     
     */
    public UELLIPSESENDING getEllipse() {
        return ellipse;
    }

    /**
     * Sets the value of the ellipse property.
     * 
     * @param value
     *     allowed object is
     *     {@link UELLIPSESENDING }
     *     
     */
    public void setEllipse(UELLIPSESENDING value) {
        this.ellipse = value;
    }

}
