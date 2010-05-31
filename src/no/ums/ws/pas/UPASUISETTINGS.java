
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPASUISETTINGS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPASUISETTINGS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="initialized" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="sz_languageid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="f_mapinit_lbo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="f_mapinit_rbo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="f_mapinit_ubo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="f_mapinit_bbo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="b_autostart_fleetcontrol" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="b_autostart_parm" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="b_window_fullscreen" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="l_winpos_x" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_winpos_y" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_win_width" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_win_height" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_gis_max_for_details" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_skin_class" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_theme_class" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_watermark_class" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_buttonshaper_class" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_gradient_class" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_title_class" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_mapserver" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_wms_site" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_wms_layers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_wms_format" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_wms_username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_wms_password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_drag_mode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_email_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_emailserver" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_mailport" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_lba_update_percent" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPASUISETTINGS", propOrder = {
    "initialized",
    "szLanguageid",
    "fMapinitLbo",
    "fMapinitRbo",
    "fMapinitUbo",
    "fMapinitBbo",
    "bAutostartFleetcontrol",
    "bAutostartParm",
    "bWindowFullscreen",
    "lWinposX",
    "lWinposY",
    "lWinWidth",
    "lWinHeight",
    "lGisMaxForDetails",
    "szSkinClass",
    "szThemeClass",
    "szWatermarkClass",
    "szButtonshaperClass",
    "szGradientClass",
    "szTitleClass",
    "lMapserver",
    "szWmsSite",
    "szWmsLayers",
    "szWmsFormat",
    "szWmsUsername",
    "szWmsPassword",
    "lDragMode",
    "szEmailName",
    "szEmail",
    "szEmailserver",
    "lMailport",
    "lLbaUpdatePercent"
})
public class UPASUISETTINGS {

    protected boolean initialized;
    @XmlElement(name = "sz_languageid")
    protected String szLanguageid;
    @XmlElement(name = "f_mapinit_lbo")
    protected double fMapinitLbo;
    @XmlElement(name = "f_mapinit_rbo")
    protected double fMapinitRbo;
    @XmlElement(name = "f_mapinit_ubo")
    protected double fMapinitUbo;
    @XmlElement(name = "f_mapinit_bbo")
    protected double fMapinitBbo;
    @XmlElement(name = "b_autostart_fleetcontrol")
    protected boolean bAutostartFleetcontrol;
    @XmlElement(name = "b_autostart_parm")
    protected boolean bAutostartParm;
    @XmlElement(name = "b_window_fullscreen")
    protected boolean bWindowFullscreen;
    @XmlElement(name = "l_winpos_x")
    protected int lWinposX;
    @XmlElement(name = "l_winpos_y")
    protected int lWinposY;
    @XmlElement(name = "l_win_width")
    protected int lWinWidth;
    @XmlElement(name = "l_win_height")
    protected int lWinHeight;
    @XmlElement(name = "l_gis_max_for_details")
    protected int lGisMaxForDetails;
    @XmlElement(name = "sz_skin_class")
    protected String szSkinClass;
    @XmlElement(name = "sz_theme_class")
    protected String szThemeClass;
    @XmlElement(name = "sz_watermark_class")
    protected String szWatermarkClass;
    @XmlElement(name = "sz_buttonshaper_class")
    protected String szButtonshaperClass;
    @XmlElement(name = "sz_gradient_class")
    protected String szGradientClass;
    @XmlElement(name = "sz_title_class")
    protected String szTitleClass;
    @XmlElement(name = "l_mapserver")
    protected int lMapserver;
    @XmlElement(name = "sz_wms_site")
    protected String szWmsSite;
    @XmlElement(name = "sz_wms_layers")
    protected String szWmsLayers;
    @XmlElement(name = "sz_wms_format")
    protected String szWmsFormat;
    @XmlElement(name = "sz_wms_username")
    protected String szWmsUsername;
    @XmlElement(name = "sz_wms_password")
    protected String szWmsPassword;
    @XmlElement(name = "l_drag_mode")
    protected int lDragMode;
    @XmlElement(name = "sz_email_name")
    protected String szEmailName;
    @XmlElement(name = "sz_email")
    protected String szEmail;
    @XmlElement(name = "sz_emailserver")
    protected String szEmailserver;
    @XmlElement(name = "l_mailport")
    protected int lMailport;
    @XmlElement(name = "l_lba_update_percent")
    protected int lLbaUpdatePercent;

    /**
     * Gets the value of the initialized property.
     * 
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the value of the initialized property.
     * 
     */
    public void setInitialized(boolean value) {
        this.initialized = value;
    }

    /**
     * Gets the value of the szLanguageid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzLanguageid() {
        return szLanguageid;
    }

    /**
     * Sets the value of the szLanguageid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzLanguageid(String value) {
        this.szLanguageid = value;
    }

    /**
     * Gets the value of the fMapinitLbo property.
     * 
     */
    public double getFMapinitLbo() {
        return fMapinitLbo;
    }

    /**
     * Sets the value of the fMapinitLbo property.
     * 
     */
    public void setFMapinitLbo(double value) {
        this.fMapinitLbo = value;
    }

    /**
     * Gets the value of the fMapinitRbo property.
     * 
     */
    public double getFMapinitRbo() {
        return fMapinitRbo;
    }

    /**
     * Sets the value of the fMapinitRbo property.
     * 
     */
    public void setFMapinitRbo(double value) {
        this.fMapinitRbo = value;
    }

    /**
     * Gets the value of the fMapinitUbo property.
     * 
     */
    public double getFMapinitUbo() {
        return fMapinitUbo;
    }

    /**
     * Sets the value of the fMapinitUbo property.
     * 
     */
    public void setFMapinitUbo(double value) {
        this.fMapinitUbo = value;
    }

    /**
     * Gets the value of the fMapinitBbo property.
     * 
     */
    public double getFMapinitBbo() {
        return fMapinitBbo;
    }

    /**
     * Sets the value of the fMapinitBbo property.
     * 
     */
    public void setFMapinitBbo(double value) {
        this.fMapinitBbo = value;
    }

    /**
     * Gets the value of the bAutostartFleetcontrol property.
     * 
     */
    public boolean isBAutostartFleetcontrol() {
        return bAutostartFleetcontrol;
    }

    /**
     * Sets the value of the bAutostartFleetcontrol property.
     * 
     */
    public void setBAutostartFleetcontrol(boolean value) {
        this.bAutostartFleetcontrol = value;
    }

    /**
     * Gets the value of the bAutostartParm property.
     * 
     */
    public boolean isBAutostartParm() {
        return bAutostartParm;
    }

    /**
     * Sets the value of the bAutostartParm property.
     * 
     */
    public void setBAutostartParm(boolean value) {
        this.bAutostartParm = value;
    }

    /**
     * Gets the value of the bWindowFullscreen property.
     * 
     */
    public boolean isBWindowFullscreen() {
        return bWindowFullscreen;
    }

    /**
     * Sets the value of the bWindowFullscreen property.
     * 
     */
    public void setBWindowFullscreen(boolean value) {
        this.bWindowFullscreen = value;
    }

    /**
     * Gets the value of the lWinposX property.
     * 
     */
    public int getLWinposX() {
        return lWinposX;
    }

    /**
     * Sets the value of the lWinposX property.
     * 
     */
    public void setLWinposX(int value) {
        this.lWinposX = value;
    }

    /**
     * Gets the value of the lWinposY property.
     * 
     */
    public int getLWinposY() {
        return lWinposY;
    }

    /**
     * Sets the value of the lWinposY property.
     * 
     */
    public void setLWinposY(int value) {
        this.lWinposY = value;
    }

    /**
     * Gets the value of the lWinWidth property.
     * 
     */
    public int getLWinWidth() {
        return lWinWidth;
    }

    /**
     * Sets the value of the lWinWidth property.
     * 
     */
    public void setLWinWidth(int value) {
        this.lWinWidth = value;
    }

    /**
     * Gets the value of the lWinHeight property.
     * 
     */
    public int getLWinHeight() {
        return lWinHeight;
    }

    /**
     * Sets the value of the lWinHeight property.
     * 
     */
    public void setLWinHeight(int value) {
        this.lWinHeight = value;
    }

    /**
     * Gets the value of the lGisMaxForDetails property.
     * 
     */
    public int getLGisMaxForDetails() {
        return lGisMaxForDetails;
    }

    /**
     * Sets the value of the lGisMaxForDetails property.
     * 
     */
    public void setLGisMaxForDetails(int value) {
        this.lGisMaxForDetails = value;
    }

    /**
     * Gets the value of the szSkinClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSkinClass() {
        return szSkinClass;
    }

    /**
     * Sets the value of the szSkinClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSkinClass(String value) {
        this.szSkinClass = value;
    }

    /**
     * Gets the value of the szThemeClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzThemeClass() {
        return szThemeClass;
    }

    /**
     * Sets the value of the szThemeClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzThemeClass(String value) {
        this.szThemeClass = value;
    }

    /**
     * Gets the value of the szWatermarkClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzWatermarkClass() {
        return szWatermarkClass;
    }

    /**
     * Sets the value of the szWatermarkClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzWatermarkClass(String value) {
        this.szWatermarkClass = value;
    }

    /**
     * Gets the value of the szButtonshaperClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzButtonshaperClass() {
        return szButtonshaperClass;
    }

    /**
     * Sets the value of the szButtonshaperClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzButtonshaperClass(String value) {
        this.szButtonshaperClass = value;
    }

    /**
     * Gets the value of the szGradientClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzGradientClass() {
        return szGradientClass;
    }

    /**
     * Sets the value of the szGradientClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzGradientClass(String value) {
        this.szGradientClass = value;
    }

    /**
     * Gets the value of the szTitleClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzTitleClass() {
        return szTitleClass;
    }

    /**
     * Sets the value of the szTitleClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzTitleClass(String value) {
        this.szTitleClass = value;
    }

    /**
     * Gets the value of the lMapserver property.
     * 
     */
    public int getLMapserver() {
        return lMapserver;
    }

    /**
     * Sets the value of the lMapserver property.
     * 
     */
    public void setLMapserver(int value) {
        this.lMapserver = value;
    }

    /**
     * Gets the value of the szWmsSite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzWmsSite() {
        return szWmsSite;
    }

    /**
     * Sets the value of the szWmsSite property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzWmsSite(String value) {
        this.szWmsSite = value;
    }

    /**
     * Gets the value of the szWmsLayers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzWmsLayers() {
        return szWmsLayers;
    }

    /**
     * Sets the value of the szWmsLayers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzWmsLayers(String value) {
        this.szWmsLayers = value;
    }

    /**
     * Gets the value of the szWmsFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzWmsFormat() {
        return szWmsFormat;
    }

    /**
     * Sets the value of the szWmsFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzWmsFormat(String value) {
        this.szWmsFormat = value;
    }

    /**
     * Gets the value of the szWmsUsername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzWmsUsername() {
        return szWmsUsername;
    }

    /**
     * Sets the value of the szWmsUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzWmsUsername(String value) {
        this.szWmsUsername = value;
    }

    /**
     * Gets the value of the szWmsPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzWmsPassword() {
        return szWmsPassword;
    }

    /**
     * Sets the value of the szWmsPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzWmsPassword(String value) {
        this.szWmsPassword = value;
    }

    /**
     * Gets the value of the lDragMode property.
     * 
     */
    public int getLDragMode() {
        return lDragMode;
    }

    /**
     * Sets the value of the lDragMode property.
     * 
     */
    public void setLDragMode(int value) {
        this.lDragMode = value;
    }

    /**
     * Gets the value of the szEmailName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzEmailName() {
        return szEmailName;
    }

    /**
     * Sets the value of the szEmailName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzEmailName(String value) {
        this.szEmailName = value;
    }

    /**
     * Gets the value of the szEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzEmail() {
        return szEmail;
    }

    /**
     * Sets the value of the szEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzEmail(String value) {
        this.szEmail = value;
    }

    /**
     * Gets the value of the szEmailserver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzEmailserver() {
        return szEmailserver;
    }

    /**
     * Sets the value of the szEmailserver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzEmailserver(String value) {
        this.szEmailserver = value;
    }

    /**
     * Gets the value of the lMailport property.
     * 
     */
    public int getLMailport() {
        return lMailport;
    }

    /**
     * Sets the value of the lMailport property.
     * 
     */
    public void setLMailport(int value) {
        this.lMailport = value;
    }

    /**
     * Gets the value of the lLbaUpdatePercent property.
     * 
     */
    public int getLLbaUpdatePercent() {
        return lLbaUpdatePercent;
    }

    /**
     * Sets the value of the lLbaUpdatePercent property.
     * 
     */
    public void setLLbaUpdatePercent(int value) {
        this.lLbaUpdatePercent = value;
    }

}
