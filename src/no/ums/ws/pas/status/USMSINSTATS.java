
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for USMSINSTATS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="USMSINSTATS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_refno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_answercode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "USMSINSTATS", propOrder = {
    "lRefno",
    "lAnswercode",
    "szDescription",
    "lCount"
})
public class USMSINSTATS {

    @XmlElement(name = "l_refno")
    protected int lRefno;
    @XmlElement(name = "l_answercode")
    protected int lAnswercode;
    @XmlElement(name = "sz_description")
    protected String szDescription;
    @XmlElement(name = "l_count")
    protected int lCount;

    /**
     * Gets the value of the lRefno property.
     * 
     */
    public int getLRefno() {
        return lRefno;
    }

    /**
     * Sets the value of the lRefno property.
     * 
     */
    public void setLRefno(int value) {
        this.lRefno = value;
    }

    /**
     * Gets the value of the lAnswercode property.
     * 
     */
    public int getLAnswercode() {
        return lAnswercode;
    }

    /**
     * Sets the value of the lAnswercode property.
     * 
     */
    public void setLAnswercode(int value) {
        this.lAnswercode = value;
    }

    /**
     * Gets the value of the szDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzDescription() {
        return szDescription;
    }

    /**
     * Sets the value of the szDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzDescription(String value) {
        this.szDescription = value;
    }

    /**
     * Gets the value of the lCount property.
     * 
     */
    public int getLCount() {
        return lCount;
    }

    /**
     * Sets the value of the lCount property.
     * 
     */
    public void setLCount(int value) {
        this.lCount = value;
    }

}
