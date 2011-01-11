
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BBPROJECT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BBPROJECT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="l_projectpk" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sz_projectname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="l_deptpk" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="l_userpk" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="l_finished" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BBPROJECT")
@XmlSeeAlso({
    UPROJECTFINISHEDRESPONSE.class
})
public class BBPROJECT {

    @XmlAttribute(name = "l_projectpk")
    protected String lProjectpk;
    @XmlAttribute(name = "sz_projectname")
    protected String szProjectname;
    @XmlAttribute(name = "l_deptpk", required = true)
    protected int lDeptpk;
    @XmlAttribute(name = "l_userpk", required = true)
    protected long lUserpk;
    @XmlAttribute(name = "l_finished", required = true)
    protected int lFinished;

    /**
     * Gets the value of the lProjectpk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLProjectpk() {
        return lProjectpk;
    }

    /**
     * Sets the value of the lProjectpk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLProjectpk(String value) {
        this.lProjectpk = value;
    }

    /**
     * Gets the value of the szProjectname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzProjectname() {
        return szProjectname;
    }

    /**
     * Sets the value of the szProjectname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzProjectname(String value) {
        this.szProjectname = value;
    }

    /**
     * Gets the value of the lDeptpk property.
     * 
     */
    public int getLDeptpk() {
        return lDeptpk;
    }

    /**
     * Sets the value of the lDeptpk property.
     * 
     */
    public void setLDeptpk(int value) {
        this.lDeptpk = value;
    }

    /**
     * Gets the value of the lUserpk property.
     * 
     */
    public long getLUserpk() {
        return lUserpk;
    }

    /**
     * Sets the value of the lUserpk property.
     * 
     */
    public void setLUserpk(long value) {
        this.lUserpk = value;
    }

    /**
     * Gets the value of the lFinished property.
     * 
     */
    public int getLFinished() {
        return lFinished;
    }

    /**
     * Sets the value of the lFinished property.
     * 
     */
    public void setLFinished(int value) {
        this.lFinished = value;
    }

}
