
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UStatusListItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UStatusListItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_sendingtype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_totitem" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_altjmp" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_refno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_createdate" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_createtime" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_sendingname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_sendingstatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_groups" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_group" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_type" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_deptid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_projectpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_projectname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_createtimestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_updatetimestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UStatusListItem", propOrder = {
    "nSendingtype",
    "nTotitem",
    "nAltjmp",
    "nRefno",
    "nCreatedate",
    "nCreatetime",
    "szSendingname",
    "nSendingstatus",
    "szGroups",
    "nGroup",
    "nType",
    "nDeptpk",
    "szDeptid",
    "nProjectpk",
    "szProjectname",
    "nCreatetimestamp",
    "nUpdatetimestamp"
})
public class UStatusListItem {

    @XmlElement(name = "n_sendingtype")
    protected int nSendingtype;
    @XmlElement(name = "n_totitem")
    protected int nTotitem;
    @XmlElement(name = "n_altjmp")
    protected int nAltjmp;
    @XmlElement(name = "n_refno")
    protected int nRefno;
    @XmlElement(name = "n_createdate")
    protected int nCreatedate;
    @XmlElement(name = "n_createtime")
    protected int nCreatetime;
    @XmlElement(name = "sz_sendingname")
    protected String szSendingname;
    @XmlElement(name = "n_sendingstatus")
    protected int nSendingstatus;
    @XmlElement(name = "sz_groups")
    protected String szGroups;
    @XmlElement(name = "n_group")
    protected int nGroup;
    @XmlElement(name = "n_type")
    protected int nType;
    @XmlElement(name = "n_deptpk")
    protected int nDeptpk;
    @XmlElement(name = "sz_deptid")
    protected String szDeptid;
    @XmlElement(name = "n_projectpk")
    protected long nProjectpk;
    @XmlElement(name = "sz_projectname")
    protected String szProjectname;
    @XmlElement(name = "n_createtimestamp")
    protected long nCreatetimestamp;
    @XmlElement(name = "n_updatetimestamp")
    protected long nUpdatetimestamp;

    /**
     * Gets the value of the nSendingtype property.
     * 
     */
    public int getNSendingtype() {
        return nSendingtype;
    }

    /**
     * Sets the value of the nSendingtype property.
     * 
     */
    public void setNSendingtype(int value) {
        this.nSendingtype = value;
    }

    /**
     * Gets the value of the nTotitem property.
     * 
     */
    public int getNTotitem() {
        return nTotitem;
    }

    /**
     * Sets the value of the nTotitem property.
     * 
     */
    public void setNTotitem(int value) {
        this.nTotitem = value;
    }

    /**
     * Gets the value of the nAltjmp property.
     * 
     */
    public int getNAltjmp() {
        return nAltjmp;
    }

    /**
     * Sets the value of the nAltjmp property.
     * 
     */
    public void setNAltjmp(int value) {
        this.nAltjmp = value;
    }

    /**
     * Gets the value of the nRefno property.
     * 
     */
    public int getNRefno() {
        return nRefno;
    }

    /**
     * Sets the value of the nRefno property.
     * 
     */
    public void setNRefno(int value) {
        this.nRefno = value;
    }

    /**
     * Gets the value of the nCreatedate property.
     * 
     */
    public int getNCreatedate() {
        return nCreatedate;
    }

    /**
     * Sets the value of the nCreatedate property.
     * 
     */
    public void setNCreatedate(int value) {
        this.nCreatedate = value;
    }

    /**
     * Gets the value of the nCreatetime property.
     * 
     */
    public int getNCreatetime() {
        return nCreatetime;
    }

    /**
     * Sets the value of the nCreatetime property.
     * 
     */
    public void setNCreatetime(int value) {
        this.nCreatetime = value;
    }

    /**
     * Gets the value of the szSendingname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSendingname() {
        return szSendingname;
    }

    /**
     * Sets the value of the szSendingname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSendingname(String value) {
        this.szSendingname = value;
    }

    /**
     * Gets the value of the nSendingstatus property.
     * 
     */
    public int getNSendingstatus() {
        return nSendingstatus;
    }

    /**
     * Sets the value of the nSendingstatus property.
     * 
     */
    public void setNSendingstatus(int value) {
        this.nSendingstatus = value;
    }

    /**
     * Gets the value of the szGroups property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzGroups() {
        return szGroups;
    }

    /**
     * Sets the value of the szGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzGroups(String value) {
        this.szGroups = value;
    }

    /**
     * Gets the value of the nGroup property.
     * 
     */
    public int getNGroup() {
        return nGroup;
    }

    /**
     * Sets the value of the nGroup property.
     * 
     */
    public void setNGroup(int value) {
        this.nGroup = value;
    }

    /**
     * Gets the value of the nType property.
     * 
     */
    public int getNType() {
        return nType;
    }

    /**
     * Sets the value of the nType property.
     * 
     */
    public void setNType(int value) {
        this.nType = value;
    }

    /**
     * Gets the value of the nDeptpk property.
     * 
     */
    public int getNDeptpk() {
        return nDeptpk;
    }

    /**
     * Sets the value of the nDeptpk property.
     * 
     */
    public void setNDeptpk(int value) {
        this.nDeptpk = value;
    }

    /**
     * Gets the value of the szDeptid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzDeptid() {
        return szDeptid;
    }

    /**
     * Sets the value of the szDeptid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzDeptid(String value) {
        this.szDeptid = value;
    }

    /**
     * Gets the value of the nProjectpk property.
     * 
     */
    public long getNProjectpk() {
        return nProjectpk;
    }

    /**
     * Sets the value of the nProjectpk property.
     * 
     */
    public void setNProjectpk(long value) {
        this.nProjectpk = value;
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
     * Gets the value of the nCreatetimestamp property.
     * 
     */
    public long getNCreatetimestamp() {
        return nCreatetimestamp;
    }

    /**
     * Sets the value of the nCreatetimestamp property.
     * 
     */
    public void setNCreatetimestamp(long value) {
        this.nCreatetimestamp = value;
    }

    /**
     * Gets the value of the nUpdatetimestamp property.
     * 
     */
    public long getNUpdatetimestamp() {
        return nUpdatetimestamp;
    }

    /**
     * Sets the value of the nUpdatetimestamp property.
     * 
     */
    public void setNUpdatetimestamp(long value) {
        this.nUpdatetimestamp = value;
    }

}
