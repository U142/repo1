
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AlertResultLine complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AlertResultLine">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_alertpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_refno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_result_code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_extended_info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AlertResultLine", propOrder = {
    "lAlertpk",
    "lRefno",
    "szName",
    "szResult",
    "lResultCode",
    "szText",
    "szExtendedInfo"
})
public class AlertResultLine {

    @XmlElement(name = "l_alertpk")
    protected long lAlertpk;
    @XmlElement(name = "l_refno")
    protected int lRefno;
    @XmlElement(name = "sz_name")
    protected String szName;
    @XmlElement(name = "sz_result")
    protected String szResult;
    @XmlElement(name = "l_result_code")
    protected int lResultCode;
    @XmlElement(name = "sz_text")
    protected String szText;
    @XmlElement(name = "sz_extended_info")
    protected String szExtendedInfo;

    /**
     * Gets the value of the lAlertpk property.
     * 
     */
    public long getLAlertpk() {
        return lAlertpk;
    }

    /**
     * Sets the value of the lAlertpk property.
     * 
     */
    public void setLAlertpk(long value) {
        this.lAlertpk = value;
    }

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
     * Gets the value of the szName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzName() {
        return szName;
    }

    /**
     * Sets the value of the szName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzName(String value) {
        this.szName = value;
    }

    /**
     * Gets the value of the szResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzResult() {
        return szResult;
    }

    /**
     * Sets the value of the szResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzResult(String value) {
        this.szResult = value;
    }

    /**
     * Gets the value of the lResultCode property.
     * 
     */
    public int getLResultCode() {
        return lResultCode;
    }

    /**
     * Sets the value of the lResultCode property.
     * 
     */
    public void setLResultCode(int value) {
        this.lResultCode = value;
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

    /**
     * Gets the value of the szExtendedInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzExtendedInfo() {
        return szExtendedInfo;
    }

    /**
     * Sets the value of the szExtendedInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzExtendedInfo(String value) {
        this.szExtendedInfo = value;
    }

}
