
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UBBNEWSLIST complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UBBNEWSLIST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_timestamp_db" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="newslist" type="{http://ums.no/ws/pas/}ArrayOfUBBNEWS" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UBBNEWSLIST", propOrder = {
    "lTimestampDb",
    "newslist"
})
public class UBBNEWSLIST {

    @XmlElement(name = "l_timestamp_db")
    protected long lTimestampDb;
    protected ArrayOfUBBNEWS newslist;

    /**
     * Gets the value of the lTimestampDb property.
     * 
     */
    public long getLTimestampDb() {
        return lTimestampDb;
    }

    /**
     * Sets the value of the lTimestampDb property.
     * 
     */
    public void setLTimestampDb(long value) {
        this.lTimestampDb = value;
    }

    /**
     * Gets the value of the newslist property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUBBNEWS }
     *     
     */
    public ArrayOfUBBNEWS getNewslist() {
        return newslist;
    }

    /**
     * Sets the value of the newslist property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUBBNEWS }
     *     
     */
    public void setNewslist(ArrayOfUBBNEWS value) {
        this.newslist = value;
    }

}
