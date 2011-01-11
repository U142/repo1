
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UMapAddressParamsByQuality complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UMapAddressParamsByQuality">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/}UMapAddressParams">
 *       &lt;sequence>
 *         &lt;element name="sz_postno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arr_xycodes" type="{http://ums.no/ws/pas/}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UMapAddressParamsByQuality", propOrder = {
    "szPostno",
    "arrXycodes"
})
public class UMapAddressParamsByQuality
    extends UMapAddressParams
{

    @XmlElement(name = "sz_postno")
    protected String szPostno;
    @XmlElement(name = "arr_xycodes")
    protected ArrayOfString arrXycodes;

    /**
     * Gets the value of the szPostno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPostno() {
        return szPostno;
    }

    /**
     * Sets the value of the szPostno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPostno(String value) {
        this.szPostno = value;
    }

    /**
     * Gets the value of the arrXycodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getArrXycodes() {
        return arrXycodes;
    }

    /**
     * Sets the value of the arrXycodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setArrXycodes(ArrayOfString value) {
        this.arrXycodes = value;
    }

}
