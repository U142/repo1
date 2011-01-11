
package no.ums.pas.core.dataexchange.soap.no.ums;

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
 *         &lt;element name="result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="result_code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extended_info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "result",
    "resultCode",
    "text",
    "extendedInfo"
})
public class AlertResultLine {

    @XmlElement(name = "l_alertpk")
    protected long lAlertpk;
    @XmlElement(name = "l_refno")
    protected int lRefno;
    protected String result;
    @XmlElement(name = "result_code")
    protected int resultCode;
    protected String text;
    @XmlElement(name = "extended_info")
    protected String extendedInfo;

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
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResult(String value) {
        this.result = value;
    }

    /**
     * Gets the value of the resultCode property.
     * 
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     */
    public void setResultCode(int value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Gets the value of the extendedInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtendedInfo() {
        return extendedInfo;
    }

    /**
     * Sets the value of the extendedInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtendedInfo(String value) {
        this.extendedInfo = value;
    }

}
