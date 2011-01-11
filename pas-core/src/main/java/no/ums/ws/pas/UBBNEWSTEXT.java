
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UBBNEWSTEXT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UBBNEWSTEXT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_langpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_news" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UBBNEWSTEXT", propOrder = {
    "lLangpk",
    "szNews"
})
public class UBBNEWSTEXT {

    @XmlElement(name = "l_langpk")
    protected long lLangpk;
    @XmlElement(name = "sz_news")
    protected String szNews;

    /**
     * Gets the value of the lLangpk property.
     * 
     */
    public long getLLangpk() {
        return lLangpk;
    }

    /**
     * Sets the value of the lLangpk property.
     * 
     */
    public void setLLangpk(long value) {
        this.lLangpk = value;
    }

    /**
     * Gets the value of the szNews property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzNews() {
        return szNews;
    }

    /**
     * Sets the value of the szNews property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzNews(String value) {
        this.szNews = value;
    }

}
