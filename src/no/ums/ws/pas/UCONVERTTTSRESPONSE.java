
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UCONVERT_TTS_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UCONVERT_TTS_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_responsecode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_responsetext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_langpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="guid_sendingid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_dynfile" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="wav" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="sz_server_filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UCONVERT_TTS_RESPONSE", propOrder = {
    "nResponsecode",
    "szResponsetext",
    "nLangpk",
    "guidSendingid",
    "nDynfile",
    "wav",
    "szServerFilename"
})
public class UCONVERTTTSRESPONSE {

    @XmlElement(name = "n_responsecode")
    protected int nResponsecode;
    @XmlElement(name = "sz_responsetext")
    protected String szResponsetext;
    @XmlElement(name = "n_langpk")
    protected int nLangpk;
    @XmlElement(name = "guid_sendingid")
    protected String guidSendingid;
    @XmlElement(name = "n_dynfile")
    protected int nDynfile;
    protected byte[] wav;
    @XmlElement(name = "sz_server_filename")
    protected String szServerFilename;

    /**
     * Gets the value of the nResponsecode property.
     * 
     */
    public int getNResponsecode() {
        return nResponsecode;
    }

    /**
     * Sets the value of the nResponsecode property.
     * 
     */
    public void setNResponsecode(int value) {
        this.nResponsecode = value;
    }

    /**
     * Gets the value of the szResponsetext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzResponsetext() {
        return szResponsetext;
    }

    /**
     * Sets the value of the szResponsetext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzResponsetext(String value) {
        this.szResponsetext = value;
    }

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
     * Gets the value of the guidSendingid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuidSendingid() {
        return guidSendingid;
    }

    /**
     * Sets the value of the guidSendingid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuidSendingid(String value) {
        this.guidSendingid = value;
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
     * Gets the value of the wav property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getWav() {
        return wav;
    }

    /**
     * Sets the value of the wav property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setWav(byte[] value) {
        this.wav = ((byte[]) value);
    }

    /**
     * Gets the value of the szServerFilename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzServerFilename() {
        return szServerFilename;
    }

    /**
     * Sets the value of the szServerFilename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzServerFilename(String value) {
        this.szServerFilename = value;
    }

}
