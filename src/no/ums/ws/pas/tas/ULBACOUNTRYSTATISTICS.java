
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBACOUNTRYSTATISTICS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBACOUNTRYSTATISTICS">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/tas}ULBACOUNTRY">
 *       &lt;sequence>
 *         &lt;element name="statistics" type="{http://ums.no/ws/pas/tas}ArrayOfUTOURISTCOUNT" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBACOUNTRYSTATISTICS", propOrder = {
    "statistics"
})
public class ULBACOUNTRYSTATISTICS
    extends ULBACOUNTRY
{

    protected ArrayOfUTOURISTCOUNT statistics;

    /**
     * Gets the value of the statistics property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUTOURISTCOUNT }
     *     
     */
    public ArrayOfUTOURISTCOUNT getStatistics() {
        return statistics;
    }

    /**
     * Sets the value of the statistics property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUTOURISTCOUNT }
     *     
     */
    public void setStatistics(ArrayOfUTOURISTCOUNT value) {
        this.statistics = value;
    }

}
