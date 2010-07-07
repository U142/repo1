
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for USYSTEMMESSAGES complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="USYSTEMMESSAGES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="news" type="{http://ums.no/ws/pas/}UBBNEWSLIST" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "USYSTEMMESSAGES", propOrder = {
    "lTimestamp",
    "news"
})
public class USYSTEMMESSAGES {

    @XmlElement(name = "l_timestamp")
    protected long lTimestamp;
    protected UBBNEWSLIST news;

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
     * Gets the value of the news property.
     * 
     * @return
     *     possible object is
     *     {@link UBBNEWSLIST }
     *     
     */
    public UBBNEWSLIST getNews() {
        return news;
    }

    /**
     * Sets the value of the news property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBNEWSLIST }
     *     
     */
    public void setNews(UBBNEWSLIST value) {
        this.news = value;
    }

}
