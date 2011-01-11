
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UTESTSENDING complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UTESTSENDING">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}UMAPSENDING">
 *       &lt;sequence>
 *         &lt;element name="numbers" type="{http://ums.no/ws/parm/}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UTESTSENDING", propOrder = {
    "numbers"
})
public class UTESTSENDING
    extends UMAPSENDING
{

    protected ArrayOfString numbers;

    /**
     * Gets the value of the numbers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getNumbers() {
        return numbers;
    }

    /**
     * Sets the value of the numbers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setNumbers(ArrayOfString value) {
        this.numbers = value;
    }

}
