
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UBBMESSAGE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UBBMESSAGE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_messagepk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_type" type="{http://ums.no/ws/pas/}UBBMODULEDEF"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_langpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="f_template" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_ivrcode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_parentpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_depth" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_categorypk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="audiostream" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="b_valid" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ccmessage" type="{http://ums.no/ws/pas/}ArrayOfUCCMessage" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UBBMESSAGE", propOrder = {
    "nMessagepk",
    "nDeptpk",
    "nType",
    "szName",
    "szDescription",
    "nLangpk",
    "szNumber",
    "fTemplate",
    "szFilename",
    "nIvrcode",
    "nParentpk",
    "nDepth",
    "nTimestamp",
    "nCategorypk",
    "szMessage",
    "audiostream",
    "bValid",
    "ccmessage"
})
public class UBBMESSAGE {

    @XmlElement(name = "n_messagepk")
    protected long nMessagepk;
    @XmlElement(name = "n_deptpk")
    protected int nDeptpk;
    @XmlElement(name = "n_type", required = true)
    protected UBBMODULEDEF nType;
    @XmlElement(name = "sz_name")
    protected String szName;
    @XmlElement(name = "sz_description")
    protected String szDescription;
    @XmlElement(name = "n_langpk")
    protected long nLangpk;
    @XmlElement(name = "sz_number")
    protected String szNumber;
    @XmlElement(name = "f_template")
    protected int fTemplate;
    @XmlElement(name = "sz_filename")
    protected String szFilename;
    @XmlElement(name = "n_ivrcode")
    protected int nIvrcode;
    @XmlElement(name = "n_parentpk")
    protected long nParentpk;
    @XmlElement(name = "n_depth")
    protected int nDepth;
    @XmlElement(name = "n_timestamp")
    protected long nTimestamp;
    @XmlElement(name = "n_categorypk")
    protected long nCategorypk;
    @XmlElement(name = "sz_message")
    protected String szMessage;
    protected byte[] audiostream;
    @XmlElement(name = "b_valid")
    protected boolean bValid;
    protected ArrayOfUCCMessage ccmessage;

    /**
     * Gets the value of the nMessagepk property.
     * 
     */
    public long getNMessagepk() {
        return nMessagepk;
    }

    /**
     * Sets the value of the nMessagepk property.
     * 
     */
    public void setNMessagepk(long value) {
        this.nMessagepk = value;
    }

    /**
     * Gets the value of the nDeptpk property.
     * 
     */
    public int getNDeptpk() {
        return nDeptpk;
    }

    /**
     * Sets the value of the nDeptpk property.
     * 
     */
    public void setNDeptpk(int value) {
        this.nDeptpk = value;
    }

    /**
     * Gets the value of the nType property.
     * 
     * @return
     *     possible object is
     *     {@link UBBMODULEDEF }
     *     
     */
    public UBBMODULEDEF getNType() {
        return nType;
    }

    /**
     * Sets the value of the nType property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBMODULEDEF }
     *     
     */
    public void setNType(UBBMODULEDEF value) {
        this.nType = value;
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
     * Gets the value of the nLangpk property.
     * 
     */
    public long getNLangpk() {
        return nLangpk;
    }

    /**
     * Sets the value of the nLangpk property.
     * 
     */
    public void setNLangpk(long value) {
        this.nLangpk = value;
    }

    /**
     * Gets the value of the szNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzNumber() {
        return szNumber;
    }

    /**
     * Sets the value of the szNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzNumber(String value) {
        this.szNumber = value;
    }

    /**
     * Gets the value of the fTemplate property.
     * 
     */
    public int getFTemplate() {
        return fTemplate;
    }

    /**
     * Sets the value of the fTemplate property.
     * 
     */
    public void setFTemplate(int value) {
        this.fTemplate = value;
    }

    /**
     * Gets the value of the szFilename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzFilename() {
        return szFilename;
    }

    /**
     * Sets the value of the szFilename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzFilename(String value) {
        this.szFilename = value;
    }

    /**
     * Gets the value of the nIvrcode property.
     * 
     */
    public int getNIvrcode() {
        return nIvrcode;
    }

    /**
     * Sets the value of the nIvrcode property.
     * 
     */
    public void setNIvrcode(int value) {
        this.nIvrcode = value;
    }

    /**
     * Gets the value of the nParentpk property.
     * 
     */
    public long getNParentpk() {
        return nParentpk;
    }

    /**
     * Sets the value of the nParentpk property.
     * 
     */
    public void setNParentpk(long value) {
        this.nParentpk = value;
    }

    /**
     * Gets the value of the nDepth property.
     * 
     */
    public int getNDepth() {
        return nDepth;
    }

    /**
     * Sets the value of the nDepth property.
     * 
     */
    public void setNDepth(int value) {
        this.nDepth = value;
    }

    /**
     * Gets the value of the nTimestamp property.
     * 
     */
    public long getNTimestamp() {
        return nTimestamp;
    }

    /**
     * Sets the value of the nTimestamp property.
     * 
     */
    public void setNTimestamp(long value) {
        this.nTimestamp = value;
    }

    /**
     * Gets the value of the nCategorypk property.
     * 
     */
    public long getNCategorypk() {
        return nCategorypk;
    }

    /**
     * Sets the value of the nCategorypk property.
     * 
     */
    public void setNCategorypk(long value) {
        this.nCategorypk = value;
    }

    /**
     * Gets the value of the szMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzMessage() {
        return szMessage;
    }

    /**
     * Sets the value of the szMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzMessage(String value) {
        this.szMessage = value;
    }

    /**
     * Gets the value of the audiostream property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getAudiostream() {
        return audiostream;
    }

    /**
     * Sets the value of the audiostream property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setAudiostream(byte[] value) {
        this.audiostream = ((byte[]) value);
    }

    /**
     * Gets the value of the bValid property.
     * 
     */
    public boolean isBValid() {
        return bValid;
    }

    /**
     * Sets the value of the bValid property.
     * 
     */
    public void setBValid(boolean value) {
        this.bValid = value;
    }

    /**
     * Gets the value of the ccmessage property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUCCMessage }
     *     
     */
    public ArrayOfUCCMessage getCcmessage() {
        return ccmessage;
    }

    /**
     * Sets the value of the ccmessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUCCMessage }
     *     
     */
    public void setCcmessage(ArrayOfUCCMessage value) {
        this.ccmessage = value;
    }

}
