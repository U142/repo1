
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zipfile" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="logoninfo" type="{http://ums.no/ws/parm/admin/}ULOGONINFO"/>
 *         &lt;element name="sz_filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_polyfilename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "zipfile",
    "logoninfo",
    "szFilename",
    "szPolyfilename"
})
@XmlRootElement(name = "UpdateParm")
public class UpdateParm {

    protected byte[] zipfile;
    @XmlElement(required = true)
    protected ULOGONINFO logoninfo;
    @XmlElement(name = "sz_filename")
    protected String szFilename;
    @XmlElement(name = "sz_polyfilename")
    protected String szPolyfilename;

    /**
     * Gets the value of the zipfile property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getZipfile() {
        return zipfile;
    }

    /**
     * Sets the value of the zipfile property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setZipfile(byte[] value) {
        this.zipfile = ((byte[]) value);
    }

    /**
     * Gets the value of the logoninfo property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getLogoninfo() {
        return logoninfo;
    }

    /**
     * Sets the value of the logoninfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setLogoninfo(ULOGONINFO value) {
        this.logoninfo = value;
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
     * Gets the value of the szPolyfilename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPolyfilename() {
        return szPolyfilename;
    }

    /**
     * Sets the value of the szPolyfilename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPolyfilename(String value) {
        this.szPolyfilename = value;
    }

}
