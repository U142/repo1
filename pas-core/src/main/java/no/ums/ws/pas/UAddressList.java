
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UAddressList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UAddressList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="list_basics" type="{http://ums.no/ws/pas/}ArrayOfUAddressBasics" minOccurs="0"/>
 *         &lt;element name="list" type="{http://ums.no/ws/pas/}ArrayOfUAddress" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UAddressList", propOrder = {
    "listBasics",
    "list"
})
public class UAddressList {

    @XmlElement(name = "list_basics")
    protected ArrayOfUAddressBasics listBasics;
    protected ArrayOfUAddress list;

    /**
     * Gets the value of the listBasics property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUAddressBasics }
     *     
     */
    public ArrayOfUAddressBasics getListBasics() {
        return listBasics;
    }

    /**
     * Sets the value of the listBasics property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUAddressBasics }
     *     
     */
    public void setListBasics(ArrayOfUAddressBasics value) {
        this.listBasics = value;
    }

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUAddress }
     *     
     */
    public ArrayOfUAddress getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUAddress }
     *     
     */
    public void setList(ArrayOfUAddress value) {
        this.list = value;
    }

}
