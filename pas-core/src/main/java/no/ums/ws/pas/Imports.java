
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for imports complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="imports">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l1" type="{http://ums.no/ws/pas/}LBALanguage" minOccurs="0"/>
 *         &lt;element name="l2" type="{http://ums.no/ws/pas/}LBACCode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imports", propOrder = {
    "l1",
    "l2"
})
public class Imports {

    protected LBALanguage l1;
    protected LBACCode l2;

    /**
     * Gets the value of the l1 property.
     * 
     * @return
     *     possible object is
     *     {@link LBALanguage }
     *     
     */
    public LBALanguage getL1() {
        return l1;
    }

    /**
     * Sets the value of the l1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link LBALanguage }
     *     
     */
    public void setL1(LBALanguage value) {
        this.l1 = value;
    }

    /**
     * Gets the value of the l2 property.
     * 
     * @return
     *     possible object is
     *     {@link LBACCode }
     *     
     */
    public LBACCode getL2() {
        return l2;
    }

    /**
     * Sets the value of the l2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link LBACCode }
     *     
     */
    public void setL2(LBACCode value) {
        this.l2 = value;
    }

}
