
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PAEVENT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PAEVENT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parmop" type="{http://ums.no/ws/parm/}PARMOPERATION"/>
 *         &lt;element name="l_temppk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_eventpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_parent" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_categorypk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="f_epi_lon" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="f_epi_lat" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="alerts" type="{http://ums.no/ws/parm/}ArrayOfPAALERT" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PAEVENT", propOrder = {
    "parmop",
    "lTemppk",
    "lEventpk",
    "lParent",
    "szName",
    "szDescription",
    "lCategorypk",
    "lTimestamp",
    "fEpiLon",
    "fEpiLat",
    "alerts"
})
public class PAEVENT {

    @XmlElement(required = true)
    protected PARMOPERATION parmop;
    @XmlElement(name = "l_temppk")
    protected long lTemppk;
    @XmlElement(name = "l_eventpk")
    protected long lEventpk;
    @XmlElement(name = "l_parent")
    protected long lParent;
    @XmlElement(name = "sz_name")
    protected String szName;
    @XmlElement(name = "sz_description")
    protected String szDescription;
    @XmlElement(name = "l_categorypk")
    protected long lCategorypk;
    @XmlElement(name = "l_timestamp")
    protected long lTimestamp;
    @XmlElement(name = "f_epi_lon")
    protected float fEpiLon;
    @XmlElement(name = "f_epi_lat")
    protected float fEpiLat;
    protected ArrayOfPAALERT alerts;

    /**
     * Gets the value of the parmop property.
     * 
     * @return
     *     possible object is
     *     {@link PARMOPERATION }
     *     
     */
    public PARMOPERATION getParmop() {
        return parmop;
    }

    /**
     * Sets the value of the parmop property.
     * 
     * @param value
     *     allowed object is
     *     {@link PARMOPERATION }
     *     
     */
    public void setParmop(PARMOPERATION value) {
        this.parmop = value;
    }

    /**
     * Gets the value of the lTemppk property.
     * 
     */
    public long getLTemppk() {
        return lTemppk;
    }

    /**
     * Sets the value of the lTemppk property.
     * 
     */
    public void setLTemppk(long value) {
        this.lTemppk = value;
    }

    /**
     * Gets the value of the lEventpk property.
     * 
     */
    public long getLEventpk() {
        return lEventpk;
    }

    /**
     * Sets the value of the lEventpk property.
     * 
     */
    public void setLEventpk(long value) {
        this.lEventpk = value;
    }

    /**
     * Gets the value of the lParent property.
     * 
     */
    public long getLParent() {
        return lParent;
    }

    /**
     * Sets the value of the lParent property.
     * 
     */
    public void setLParent(long value) {
        this.lParent = value;
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
     * Gets the value of the lCategorypk property.
     * 
     */
    public long getLCategorypk() {
        return lCategorypk;
    }

    /**
     * Sets the value of the lCategorypk property.
     * 
     */
    public void setLCategorypk(long value) {
        this.lCategorypk = value;
    }

    /**
     * Gets the value of the lTimestamp property.
     * 
     */
    public long getLTimestamp() {
        return lTimestamp;
    }

    /**
     * Sets the value of the lTimestamp property.
     * 
     */
    public void setLTimestamp(long value) {
        this.lTimestamp = value;
    }

    /**
     * Gets the value of the fEpiLon property.
     * 
     */
    public float getFEpiLon() {
        return fEpiLon;
    }

    /**
     * Sets the value of the fEpiLon property.
     * 
     */
    public void setFEpiLon(float value) {
        this.fEpiLon = value;
    }

    /**
     * Gets the value of the fEpiLat property.
     * 
     */
    public float getFEpiLat() {
        return fEpiLat;
    }

    /**
     * Sets the value of the fEpiLat property.
     * 
     */
    public void setFEpiLat(float value) {
        this.fEpiLat = value;
    }

    /**
     * Gets the value of the alerts property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPAALERT }
     *     
     */
    public ArrayOfPAALERT getAlerts() {
        return alerts;
    }

    /**
     * Sets the value of the alerts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPAALERT }
     *     
     */
    public void setAlerts(ArrayOfPAALERT value) {
        this.alerts = value;
    }

}
