
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBASTATISTICS_FILTER complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBASTATISTICS_FILTER">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="countries" type="{http://ums.no/ws/pas/tas}ArrayOfULBACOUNTRY" minOccurs="0"/>
 *         &lt;element name="years_to_compare" type="{http://ums.no/ws/pas/tas}ArrayOfInt" minOccurs="0"/>
 *         &lt;element name="from_date" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="to_date" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="group_timeunit" type="{http://ums.no/ws/pas/tas}ULBAFILTER_STAT_TIMEUNIT"/>
 *         &lt;element name="stat_function" type="{http://ums.no/ws/pas/tas}ULBAFILTER_STAT_FUNCTION"/>
 *         &lt;element name="rowcount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBASTATISTICS_FILTER", propOrder = {
    "countries",
    "yearsToCompare",
    "fromDate",
    "toDate",
    "groupTimeunit",
    "statFunction",
    "rowcount"
})
public class ULBASTATISTICSFILTER {

    protected ArrayOfULBACOUNTRY countries;
    @XmlElement(name = "years_to_compare")
    protected ArrayOfInt yearsToCompare;
    @XmlElement(name = "from_date")
    protected long fromDate;
    @XmlElement(name = "to_date")
    protected long toDate;
    @XmlElement(name = "group_timeunit", required = true)
    protected ULBAFILTERSTATTIMEUNIT groupTimeunit;
    @XmlElement(name = "stat_function", required = true)
    protected ULBAFILTERSTATFUNCTION statFunction;
    protected int rowcount;

    /**
     * Gets the value of the countries property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBACOUNTRY }
     *     
     */
    public ArrayOfULBACOUNTRY getCountries() {
        return countries;
    }

    /**
     * Sets the value of the countries property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBACOUNTRY }
     *     
     */
    public void setCountries(ArrayOfULBACOUNTRY value) {
        this.countries = value;
    }

    /**
     * Gets the value of the yearsToCompare property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getYearsToCompare() {
        return yearsToCompare;
    }

    /**
     * Sets the value of the yearsToCompare property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setYearsToCompare(ArrayOfInt value) {
        this.yearsToCompare = value;
    }

    /**
     * Gets the value of the fromDate property.
     * 
     */
    public long getFromDate() {
        return fromDate;
    }

    /**
     * Sets the value of the fromDate property.
     * 
     */
    public void setFromDate(long value) {
        this.fromDate = value;
    }

    /**
     * Gets the value of the toDate property.
     * 
     */
    public long getToDate() {
        return toDate;
    }

    /**
     * Sets the value of the toDate property.
     * 
     */
    public void setToDate(long value) {
        this.toDate = value;
    }

    /**
     * Gets the value of the groupTimeunit property.
     * 
     * @return
     *     possible object is
     *     {@link ULBAFILTERSTATTIMEUNIT }
     *     
     */
    public ULBAFILTERSTATTIMEUNIT getGroupTimeunit() {
        return groupTimeunit;
    }

    /**
     * Sets the value of the groupTimeunit property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULBAFILTERSTATTIMEUNIT }
     *     
     */
    public void setGroupTimeunit(ULBAFILTERSTATTIMEUNIT value) {
        this.groupTimeunit = value;
    }

    /**
     * Gets the value of the statFunction property.
     * 
     * @return
     *     possible object is
     *     {@link ULBAFILTERSTATFUNCTION }
     *     
     */
    public ULBAFILTERSTATFUNCTION getStatFunction() {
        return statFunction;
    }

    /**
     * Sets the value of the statFunction property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULBAFILTERSTATFUNCTION }
     *     
     */
    public void setStatFunction(ULBAFILTERSTATFUNCTION value) {
        this.statFunction = value;
    }

    /**
     * Gets the value of the rowcount property.
     * 
     */
    public int getRowcount() {
        return rowcount;
    }

    /**
     * Sets the value of the rowcount property.
     * 
     */
    public void setRowcount(int value) {
        this.rowcount = value;
    }

}
