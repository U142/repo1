
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AUDIO_REQUEST complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AUDIO_REQUEST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_refno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_param" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_filetype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="wav" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="sz_tts_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_langpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_messagepk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AUDIO_REQUEST", propOrder = {
    "nRefno",
    "nParam",
    "nFiletype",
    "wav",
    "szTtsText",
    "nLangpk",
    "szFilename",
    "nMessagepk",
    "nDeptpk"
})
public class AUDIOREQUEST {

    @XmlElement(name = "n_refno")
    protected int nRefno;
    @XmlElement(name = "n_param")
    protected int nParam;
    @XmlElement(name = "n_filetype")
    protected int nFiletype;
    protected byte[] wav;
    @XmlElement(name = "sz_tts_text")
    protected String szTtsText;
    @XmlElement(name = "n_langpk")
    protected int nLangpk;
    @XmlElement(name = "sz_filename")
    protected String szFilename;
    @XmlElement(name = "n_messagepk")
    protected int nMessagepk;
    @XmlElement(name = "n_deptpk")
    protected int nDeptpk;

    /**
     * Gets the value of the nRefno property.
     * 
     */
    public int getNRefno() {
        return nRefno;
    }

    /**
     * Sets the value of the nRefno property.
     * 
     */
    public void setNRefno(int value) {
        this.nRefno = value;
    }

    /**
     * Gets the value of the nParam property.
     * 
     */
    public int getNParam() {
        return nParam;
    }

    /**
     * Sets the value of the nParam property.
     * 
     */
    public void setNParam(int value) {
        this.nParam = value;
    }

    /**
     * Gets the value of the nFiletype property.
     * 
     */
    public int getNFiletype() {
        return nFiletype;
    }

    /**
     * Sets the value of the nFiletype property.
     * 
     */
    public void setNFiletype(int value) {
        this.nFiletype = value;
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
     * Gets the value of the szTtsText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzTtsText() {
        return szTtsText;
    }

    /**
     * Sets the value of the szTtsText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzTtsText(String value) {
        this.szTtsText = value;
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
     * Gets the value of the nMessagepk property.
     * 
     */
    public int getNMessagepk() {
        return nMessagepk;
    }

    /**
     * Sets the value of the nMessagepk property.
     * 
     */
    public void setNMessagepk(int value) {
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

}
