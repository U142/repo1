
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UConfirmJobResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UConfirmJobResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_refno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_jobid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resultcode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="resulttext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UConfirmJobResponse", propOrder = {
    "lRefno",
    "szJobid",
    "resultcode",
    "resulttext"
})
public class UConfirmJobResponse {

    @XmlElement(name = "l_refno")
    protected int lRefno;
    @XmlElement(name = "sz_jobid")
    protected String szJobid;
    protected int resultcode;
    protected String resulttext;

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
     * Gets the value of the szJobid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzJobid() {
        return szJobid;
    }

    /**
     * Sets the value of the szJobid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzJobid(String value) {
        this.szJobid = value;
    }

    /**
     * Gets the value of the resultcode property.
     * 
     */
    public int getResultcode() {
        return resultcode;
    }

    /**
     * Sets the value of the resultcode property.
     * 
     */
    public void setResultcode(int value) {
        this.resultcode = value;
    }

    /**
     * Gets the value of the resulttext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResulttext() {
        return resulttext;
    }

    /**
     * Sets the value of the resulttext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResulttext(String value) {
        this.resulttext = value;
    }

}
