
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UStatusItemSearchParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UStatusItemSearchParams">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="_l_projectpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="_l_item_filter" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="_l_date_filter" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="_l_time_filter" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="_l_refno_filter" type="{http://ums.no/ws/pas/status}ArrayOfLong" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UStatusItemSearchParams", propOrder = {
    "lProjectpk",
    "lItemFilter",
    "lDateFilter",
    "lTimeFilter",
    "lRefnoFilter"
})
public class UStatusItemSearchParams {

    @XmlElement(name = "_l_projectpk")
    protected long lProjectpk;
    @XmlElement(name = "_l_item_filter")
    protected int lItemFilter;
    @XmlElement(name = "_l_date_filter")
    protected int lDateFilter;
    @XmlElement(name = "_l_time_filter")
    protected int lTimeFilter;
    @XmlElement(name = "_l_refno_filter")
    protected ArrayOfLong lRefnoFilter;

    /**
     * Gets the value of the lProjectpk property.
     * 
     */
    public long getLProjectpk() {
        return lProjectpk;
    }

    /**
     * Sets the value of the lProjectpk property.
     * 
     */
    public void setLProjectpk(long value) {
        this.lProjectpk = value;
    }

    /**
     * Gets the value of the lItemFilter property.
     * 
     */
    public int getLItemFilter() {
        return lItemFilter;
    }

    /**
     * Sets the value of the lItemFilter property.
     * 
     */
    public void setLItemFilter(int value) {
        this.lItemFilter = value;
    }

    /**
     * Gets the value of the lDateFilter property.
     * 
     */
    public int getLDateFilter() {
        return lDateFilter;
    }

    /**
     * Sets the value of the lDateFilter property.
     * 
     */
    public void setLDateFilter(int value) {
        this.lDateFilter = value;
    }

    /**
     * Gets the value of the lTimeFilter property.
     * 
     */
    public int getLTimeFilter() {
        return lTimeFilter;
    }

    /**
     * Sets the value of the lTimeFilter property.
     * 
     */
    public void setLTimeFilter(int value) {
        this.lTimeFilter = value;
    }

    /**
     * Gets the value of the lRefnoFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLong }
     *     
     */
    public ArrayOfLong getLRefnoFilter() {
        return lRefnoFilter;
    }

    /**
     * Sets the value of the lRefnoFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLong }
     *     
     */
    public void setLRefnoFilter(ArrayOfLong value) {
        this.lRefnoFilter = value;
    }

}
