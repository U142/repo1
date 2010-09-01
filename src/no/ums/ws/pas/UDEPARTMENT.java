
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UDEPARTMENT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UDEPARTMENT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_deptid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_stdcc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lbo" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="rbo" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="ubo" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="bbo" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="f_default" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="l_deptpri" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_maxalloc" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_userprofilename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_userprofiledesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_status" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_newsending" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_parm" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_fleetcontrol" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_lba" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_houseeditor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_addresstypes" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_defaultnumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="f_map" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_pas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="municipals" type="{http://ums.no/ws/pas/}ArrayOfUMunicipalDef" minOccurs="0"/>
 *         &lt;element name="restrictionShapes" type="{http://ums.no/ws/pas/}ArrayOfUShape" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UDEPARTMENT", propOrder = {
    "lDeptpk",
    "szDeptid",
    "szStdcc",
    "lbo",
    "rbo",
    "ubo",
    "bbo",
    "fDefault",
    "lDeptpri",
    "lMaxalloc",
    "szUserprofilename",
    "szUserprofiledesc",
    "lStatus",
    "lNewsending",
    "lParm",
    "lFleetcontrol",
    "lLba",
    "lHouseeditor",
    "lAddresstypes",
    "szDefaultnumber",
    "fMap",
    "lPas",
    "municipals",
    "restrictionShapes"
})
public class UDEPARTMENT {

    @XmlElement(name = "l_deptpk")
    protected int lDeptpk;
    @XmlElement(name = "sz_deptid")
    protected String szDeptid;
    @XmlElement(name = "sz_stdcc")
    protected String szStdcc;
    protected float lbo;
    protected float rbo;
    protected float ubo;
    protected float bbo;
    @XmlElement(name = "f_default")
    protected boolean fDefault;
    @XmlElement(name = "l_deptpri")
    protected int lDeptpri;
    @XmlElement(name = "l_maxalloc")
    protected int lMaxalloc;
    @XmlElement(name = "sz_userprofilename")
    protected String szUserprofilename;
    @XmlElement(name = "sz_userprofiledesc")
    protected String szUserprofiledesc;
    @XmlElement(name = "l_status")
    protected int lStatus;
    @XmlElement(name = "l_newsending")
    protected int lNewsending;
    @XmlElement(name = "l_parm")
    protected int lParm;
    @XmlElement(name = "l_fleetcontrol")
    protected int lFleetcontrol;
    @XmlElement(name = "l_lba")
    protected int lLba;
    @XmlElement(name = "l_houseeditor")
    protected int lHouseeditor;
    @XmlElement(name = "l_addresstypes")
    protected long lAddresstypes;
    @XmlElement(name = "sz_defaultnumber")
    protected String szDefaultnumber;
    @XmlElement(name = "f_map")
    protected int fMap;
    @XmlElement(name = "l_pas")
    protected int lPas;
    protected ArrayOfUMunicipalDef municipals;
    protected ArrayOfUShape restrictionShapes;

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
     * Gets the value of the szStdcc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzStdcc() {
        return szStdcc;
    }

    /**
     * Sets the value of the szStdcc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzStdcc(String value) {
        this.szStdcc = value;
    }

    /**
     * Gets the value of the lbo property.
     * 
     */
    public float getLbo() {
        return lbo;
    }

    /**
     * Sets the value of the lbo property.
     * 
     */
    public void setLbo(float value) {
        this.lbo = value;
    }

    /**
     * Gets the value of the rbo property.
     * 
     */
    public float getRbo() {
        return rbo;
    }

    /**
     * Sets the value of the rbo property.
     * 
     */
    public void setRbo(float value) {
        this.rbo = value;
    }

    /**
     * Gets the value of the ubo property.
     * 
     */
    public float getUbo() {
        return ubo;
    }

    /**
     * Sets the value of the ubo property.
     * 
     */
    public void setUbo(float value) {
        this.ubo = value;
    }

    /**
     * Gets the value of the bbo property.
     * 
     */
    public float getBbo() {
        return bbo;
    }

    /**
     * Sets the value of the bbo property.
     * 
     */
    public void setBbo(float value) {
        this.bbo = value;
    }

    /**
     * Gets the value of the fDefault property.
     * 
     */
    public boolean isFDefault() {
        return fDefault;
    }

    /**
     * Sets the value of the fDefault property.
     * 
     */
    public void setFDefault(boolean value) {
        this.fDefault = value;
    }

    /**
     * Gets the value of the lDeptpri property.
     * 
     */
    public int getLDeptpri() {
        return lDeptpri;
    }

    /**
     * Sets the value of the lDeptpri property.
     * 
     */
    public void setLDeptpri(int value) {
        this.lDeptpri = value;
    }

    /**
     * Gets the value of the lMaxalloc property.
     * 
     */
    public int getLMaxalloc() {
        return lMaxalloc;
    }

    /**
     * Sets the value of the lMaxalloc property.
     * 
     */
    public void setLMaxalloc(int value) {
        this.lMaxalloc = value;
    }

    /**
     * Gets the value of the szUserprofilename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzUserprofilename() {
        return szUserprofilename;
    }

    /**
     * Sets the value of the szUserprofilename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzUserprofilename(String value) {
        this.szUserprofilename = value;
    }

    /**
     * Gets the value of the szUserprofiledesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzUserprofiledesc() {
        return szUserprofiledesc;
    }

    /**
     * Sets the value of the szUserprofiledesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzUserprofiledesc(String value) {
        this.szUserprofiledesc = value;
    }

    /**
     * Gets the value of the lStatus property.
     * 
     */
    public int getLStatus() {
        return lStatus;
    }

    /**
     * Sets the value of the lStatus property.
     * 
     */
    public void setLStatus(int value) {
        this.lStatus = value;
    }

    /**
     * Gets the value of the lNewsending property.
     * 
     */
    public int getLNewsending() {
        return lNewsending;
    }

    /**
     * Sets the value of the lNewsending property.
     * 
     */
    public void setLNewsending(int value) {
        this.lNewsending = value;
    }

    /**
     * Gets the value of the lParm property.
     * 
     */
    public int getLParm() {
        return lParm;
    }

    /**
     * Sets the value of the lParm property.
     * 
     */
    public void setLParm(int value) {
        this.lParm = value;
    }

    /**
     * Gets the value of the lFleetcontrol property.
     * 
     */
    public int getLFleetcontrol() {
        return lFleetcontrol;
    }

    /**
     * Sets the value of the lFleetcontrol property.
     * 
     */
    public void setLFleetcontrol(int value) {
        this.lFleetcontrol = value;
    }

    /**
     * Gets the value of the lLba property.
     * 
     */
    public int getLLba() {
        return lLba;
    }

    /**
     * Sets the value of the lLba property.
     * 
     */
    public void setLLba(int value) {
        this.lLba = value;
    }

    /**
     * Gets the value of the lHouseeditor property.
     * 
     */
    public int getLHouseeditor() {
        return lHouseeditor;
    }

    /**
     * Sets the value of the lHouseeditor property.
     * 
     */
    public void setLHouseeditor(int value) {
        this.lHouseeditor = value;
    }

    /**
     * Gets the value of the lAddresstypes property.
     * 
     */
    public long getLAddresstypes() {
        return lAddresstypes;
    }

    /**
     * Sets the value of the lAddresstypes property.
     * 
     */
    public void setLAddresstypes(long value) {
        this.lAddresstypes = value;
    }

    /**
     * Gets the value of the szDefaultnumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzDefaultnumber() {
        return szDefaultnumber;
    }

    /**
     * Sets the value of the szDefaultnumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzDefaultnumber(String value) {
        this.szDefaultnumber = value;
    }

    /**
     * Gets the value of the fMap property.
     * 
     */
    public int getFMap() {
        return fMap;
    }

    /**
     * Sets the value of the fMap property.
     * 
     */
    public void setFMap(int value) {
        this.fMap = value;
    }

    /**
     * Gets the value of the lPas property.
     * 
     */
    public int getLPas() {
        return lPas;
    }

    /**
     * Sets the value of the lPas property.
     * 
     */
    public void setLPas(int value) {
        this.lPas = value;
    }

    /**
     * Gets the value of the municipals property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUMunicipalDef }
     *     
     */
    public ArrayOfUMunicipalDef getMunicipals() {
        return municipals;
    }

    /**
     * Sets the value of the municipals property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUMunicipalDef }
     *     
     */
    public void setMunicipals(ArrayOfUMunicipalDef value) {
        this.municipals = value;
    }

    /**
     * Gets the value of the restrictionShapes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUShape }
     *     
     */
    public ArrayOfUShape getRestrictionShapes() {
        return restrictionShapes;
    }

    /**
     * Sets the value of the restrictionShapes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUShape }
     *     
     */
    public void setRestrictionShapes(ArrayOfUShape value) {
        this.restrictionShapes = value;
    }

}
