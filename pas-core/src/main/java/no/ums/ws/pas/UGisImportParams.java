
package no.ums.ws.pas;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for UGisImportParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UGisImportParams">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="COL_MUNICIPAL" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="COL_NAMEFILTER1" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="COL_NAMEFILTER2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SKIPLINES" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SEPARATOR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FILE" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UGisImportParams", propOrder = {
    "colmunicipal",
    "colnamefilter1",
    "colnamefilter2",
    "skiplines",
    "separator",
    "file"
})
@XmlSeeAlso({
    UGisImportParamsByStreetId.class
})
public class UGisImportParams {

    @XmlElement(name = "COL_MUNICIPAL")
    protected int colmunicipal;
    @XmlElement(name = "COL_NAMEFILTER1")
    protected int colnamefilter1;
    @XmlElement(name = "COL_NAMEFILTER2")
    protected int colnamefilter2;
    @XmlElement(name = "SKIPLINES")
    protected int skiplines;
    @XmlElement(name = "SEPARATOR")
    protected String separator;
    @XmlElement(name = "FILE")
    protected byte[] file;

    /**
     * Gets the value of the colmunicipal property.
     * 
     */
    public int getCOLMUNICIPAL() {
        return colmunicipal;
    }

    /**
     * Sets the value of the colmunicipal property.
     * 
     */
    public void setCOLMUNICIPAL(int value) {
        this.colmunicipal = value;
    }

    /**
     * Gets the value of the colnamefilter1 property.
     * 
     */
    public int getCOLNAMEFILTER1() {
        return colnamefilter1;
    }

    /**
     * Sets the value of the colnamefilter1 property.
     * 
     */
    public void setCOLNAMEFILTER1(int value) {
        this.colnamefilter1 = value;
    }

    /**
     * Gets the value of the colnamefilter2 property.
     * 
     */
    public int getCOLNAMEFILTER2() {
        return colnamefilter2;
    }

    /**
     * Sets the value of the colnamefilter2 property.
     * 
     */
    public void setCOLNAMEFILTER2(int value) {
        this.colnamefilter2 = value;
    }

    /**
     * Gets the value of the skiplines property.
     * 
     */
    public int getSKIPLINES() {
        return skiplines;
    }

    /**
     * Sets the value of the skiplines property.
     * 
     */
    public void setSKIPLINES(int value) {
        this.skiplines = value;
    }

    /**
     * Gets the value of the separator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSEPARATOR() {
        return separator;
    }

    /**
     * Sets the value of the separator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSEPARATOR(String value) {
        this.separator = value;
    }

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFILE() {
        return file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFILE(byte[] value) {
        this.file = ((byte[]) value);
    }

}
