
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_PROJECT_STATUS_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_PROJECT_STATUS_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_db_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="project" type="{http://ums.no/ws/pas/status}BBPROJECT" minOccurs="0"/>
 *         &lt;element name="statuslist" type="{http://ums.no/ws/pas/status}ArrayOfCB_STATUS" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_PROJECT_STATUS_RESPONSE", propOrder = {
    "lDbTimestamp",
    "project",
    "statuslist"
})
public class CBPROJECTSTATUSRESPONSE {

    @XmlElement(name = "l_db_timestamp")
    protected long lDbTimestamp;
    protected BBPROJECT project;
    protected ArrayOfCBSTATUS statuslist;

    /**
     * Gets the value of the lDbTimestamp property.
     * 
     */
    public long getLDbTimestamp() {
        return lDbTimestamp;
    }

    /**
     * Sets the value of the lDbTimestamp property.
     * 
     */
    public void setLDbTimestamp(long value) {
        this.lDbTimestamp = value;
    }

    /**
     * Gets the value of the project property.
     * 
     * @return
     *     possible object is
     *     {@link BBPROJECT }
     *     
     */
    public BBPROJECT getProject() {
        return project;
    }

    /**
     * Sets the value of the project property.
     * 
     * @param value
     *     allowed object is
     *     {@link BBPROJECT }
     *     
     */
    public void setProject(BBPROJECT value) {
        this.project = value;
    }

    /**
     * Gets the value of the statuslist property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCBSTATUS }
     *     
     */
    public ArrayOfCBSTATUS getStatuslist() {
        return statuslist;
    }

    /**
     * Sets the value of the statuslist property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCBSTATUS }
     *     
     */
    public void setStatuslist(ArrayOfCBSTATUS value) {
        this.statuslist = value;
    }

}
