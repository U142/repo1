
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UTASUPDATES complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UTASUPDATES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="continents" type="{http://ums.no/ws/pas/tas}ArrayOfULBACONTINENT" minOccurs="0"/>
 *         &lt;element name="request_updates" type="{http://ums.no/ws/pas/tas}ArrayOfUTASREQUESTRESULTS" minOccurs="0"/>
 *         &lt;element name="n_serverclock" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UTASUPDATES", propOrder = {
    "continents",
    "requestUpdates",
    "nServerclock"
})
public class UTASUPDATES {

    protected ArrayOfULBACONTINENT continents;
    @XmlElement(name = "request_updates")
    protected ArrayOfUTASREQUESTRESULTS requestUpdates;
    @XmlElement(name = "n_serverclock")
    protected long nServerclock;

    /**
     * Gets the value of the continents property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBACONTINENT }
     *     
     */
    public ArrayOfULBACONTINENT getContinents() {
        return continents;
    }

    /**
     * Sets the value of the continents property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBACONTINENT }
     *     
     */
    public void setContinents(ArrayOfULBACONTINENT value) {
        this.continents = value;
    }

    /**
     * Gets the value of the requestUpdates property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUTASREQUESTRESULTS }
     *     
     */
    public ArrayOfUTASREQUESTRESULTS getRequestUpdates() {
        return requestUpdates;
    }

    /**
     * Sets the value of the requestUpdates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUTASREQUESTRESULTS }
     *     
     */
    public void setRequestUpdates(ArrayOfUTASREQUESTRESULTS value) {
        this.requestUpdates = value;
    }

    /**
     * Gets the value of the nServerclock property.
     * 
     */
    public long getNServerclock() {
        return nServerclock;
    }

    /**
     * Sets the value of the nServerclock property.
     * 
     */
    public void setNServerclock(long value) {
        this.nServerclock = value;
    }

}
