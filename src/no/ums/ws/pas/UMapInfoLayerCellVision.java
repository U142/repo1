
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UMapInfoLayerCellVision complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UMapInfoLayerCellVision">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/}UMapInfoLayer">
 *       &lt;sequence>
 *         &lt;element name="sz_jobid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="layers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_request" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="styles" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SRS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UMapInfoLayerCellVision", propOrder = {
    "szJobid",
    "layers",
    "szRequest",
    "version",
    "styles",
    "srs"
})
public class UMapInfoLayerCellVision
    extends UMapInfoLayer
{

    @XmlElement(name = "sz_jobid")
    protected String szJobid;
    protected String layers;
    @XmlElement(name = "sz_request")
    protected String szRequest;
    protected String version;
    protected String styles;
    @XmlElement(name = "SRS")
    protected String srs;

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
     * Gets the value of the layers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayers() {
        return layers;
    }

    /**
     * Sets the value of the layers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayers(String value) {
        this.layers = value;
    }

    /**
     * Gets the value of the szRequest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzRequest() {
        return szRequest;
    }

    /**
     * Sets the value of the szRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzRequest(String value) {
        this.szRequest = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the styles property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyles() {
        return styles;
    }

    /**
     * Sets the value of the styles property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyles(String value) {
        this.styles = value;
    }

    /**
     * Gets the value of the srs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSRS() {
        return srs;
    }

    /**
     * Sets the value of the srs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSRS(String value) {
        this.srs = value;
    }

}
