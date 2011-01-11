
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UCONVERT_TTS_REQUEST complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UCONVERT_TTS_REQUEST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_langpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_dynfile" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UCONVERT_TTS_REQUEST", propOrder = {
    "nLangpk",
    "nDynfile",
    "szText"
})
public class UCONVERTTTSREQUEST {

    @XmlElement(name = "n_langpk")
    protected int nLangpk;
    @XmlElement(name = "n_dynfile")
    protected int nDynfile;
    @XmlElement(name = "sz_text")
    protected String szText;

    /**
     * Gets the value of the nLangpk property.
     * 
     */
    public int getNLangpk() {
        return nLangpk;
    }

    /**
     * Sets the value of the nLangpk property.
     * 
     */
    public void setNLangpk(int value) {
        this.nLangpk = value;
    }

    /**
     * Gets the value of the nDynfile property.
     * 
     */
    public int getNDynfile() {
        return nDynfile;
    }

    /**
     * Sets the value of the nDynfile property.
     * 
     */
    public void setNDynfile(int value) {
        this.nDynfile = value;
    }

    /**
     * Gets the value of the szText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzText() {
        return szText;
    }

    /**
     * Sets the value of the szText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzText(String value) {
        this.szText = value;
    }

}
