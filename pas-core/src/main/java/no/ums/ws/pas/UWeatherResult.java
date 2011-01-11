
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UWeatherResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UWeatherResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="height" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="lon" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="lat" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="datetime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="localtime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="winddirection" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="windspeed" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="cloudcover" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="symbol" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="temperature" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="temperaturemax" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="temperaturemin" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UWeatherResult", propOrder = {
    "source",
    "height",
    "lon",
    "lat",
    "datetime",
    "localtime",
    "winddirection",
    "windspeed",
    "cloudcover",
    "symbol",
    "temperature",
    "temperaturemax",
    "temperaturemin"
})
public class UWeatherResult {

    protected String source;
    protected float height;
    protected double lon;
    protected double lat;
    protected long datetime;
    protected long localtime;
    protected float winddirection;
    protected float windspeed;
    protected int cloudcover;
    protected int symbol;
    protected float temperature;
    protected float temperaturemax;
    protected float temperaturemin;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the height property.
     * 
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     */
    public void setHeight(float value) {
        this.height = value;
    }

    /**
     * Gets the value of the lon property.
     * 
     */
    public double getLon() {
        return lon;
    }

    /**
     * Sets the value of the lon property.
     * 
     */
    public void setLon(double value) {
        this.lon = value;
    }

    /**
     * Gets the value of the lat property.
     * 
     */
    public double getLat() {
        return lat;
    }

    /**
     * Sets the value of the lat property.
     * 
     */
    public void setLat(double value) {
        this.lat = value;
    }

    /**
     * Gets the value of the datetime property.
     * 
     */
    public long getDatetime() {
        return datetime;
    }

    /**
     * Sets the value of the datetime property.
     * 
     */
    public void setDatetime(long value) {
        this.datetime = value;
    }

    /**
     * Gets the value of the localtime property.
     * 
     */
    public long getLocaltime() {
        return localtime;
    }

    /**
     * Sets the value of the localtime property.
     * 
     */
    public void setLocaltime(long value) {
        this.localtime = value;
    }

    /**
     * Gets the value of the winddirection property.
     * 
     */
    public float getWinddirection() {
        return winddirection;
    }

    /**
     * Sets the value of the winddirection property.
     * 
     */
    public void setWinddirection(float value) {
        this.winddirection = value;
    }

    /**
     * Gets the value of the windspeed property.
     * 
     */
    public float getWindspeed() {
        return windspeed;
    }

    /**
     * Sets the value of the windspeed property.
     * 
     */
    public void setWindspeed(float value) {
        this.windspeed = value;
    }

    /**
     * Gets the value of the cloudcover property.
     * 
     */
    public int getCloudcover() {
        return cloudcover;
    }

    /**
     * Sets the value of the cloudcover property.
     * 
     */
    public void setCloudcover(int value) {
        this.cloudcover = value;
    }

    /**
     * Gets the value of the symbol property.
     * 
     */
    public int getSymbol() {
        return symbol;
    }

    /**
     * Sets the value of the symbol property.
     * 
     */
    public void setSymbol(int value) {
        this.symbol = value;
    }

    /**
     * Gets the value of the temperature property.
     * 
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * Sets the value of the temperature property.
     * 
     */
    public void setTemperature(float value) {
        this.temperature = value;
    }

    /**
     * Gets the value of the temperaturemax property.
     * 
     */
    public float getTemperaturemax() {
        return temperaturemax;
    }

    /**
     * Sets the value of the temperaturemax property.
     * 
     */
    public void setTemperaturemax(float value) {
        this.temperaturemax = value;
    }

    /**
     * Gets the value of the temperaturemin property.
     * 
     */
    public float getTemperaturemin() {
        return temperaturemin;
    }

    /**
     * Sets the value of the temperaturemin property.
     * 
     */
    public void setTemperaturemin(float value) {
        this.temperaturemin = value;
    }

}
