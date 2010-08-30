
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UBBUSER complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UBBUSER">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_userpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_surname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_comppk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_profilepk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_bdate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="f_disabled" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_logontries" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="f_nopasswd_change" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_ivrlogon" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_language" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_adrlistserie" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_adrlist_sort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_adrlist_search" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_statusserie" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_status_sort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_status_search" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="f_keepalive" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_recprpage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_audiosetup" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_status_owned_groups" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_paspassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_allow_nondept_resend" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_timezone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="f_blocklist_default" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="f_session_expires_sec" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_hash_paspwd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_deptpklist" type="{http://ums.no/ws/parm/admin/}ArrayOfInt" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UBBUSER", propOrder = {
    "lUserpk",
    "szUserid",
    "szPassword",
    "szName",
    "szSurname",
    "lComppk",
    "lDeptpk",
    "lProfilepk",
    "szBdate",
    "fDisabled",
    "lLogontries",
    "fNopasswdChange",
    "lIvrlogon",
    "lLanguage",
    "szAdrlistserie",
    "lAdrlistSort",
    "lAdrlistSearch",
    "szStatusserie",
    "lStatusSort",
    "lStatusSearch",
    "fKeepalive",
    "lRecprpage",
    "lAudiosetup",
    "lStatusOwnedGroups",
    "szEmail",
    "szPaspassword",
    "lAllowNondeptResend",
    "szTimezone",
    "fBlocklistDefault",
    "fSessionExpiresSec",
    "szHashPaspwd",
    "lDeptpklist"
})
public class UBBUSER {

    @XmlElement(name = "l_userpk")
    protected long lUserpk;
    @XmlElement(name = "sz_userid")
    protected String szUserid;
    @XmlElement(name = "sz_password")
    protected String szPassword;
    @XmlElement(name = "sz_name")
    protected String szName;
    @XmlElement(name = "sz_surname")
    protected String szSurname;
    @XmlElement(name = "l_comppk")
    protected int lComppk;
    @XmlElement(name = "l_deptpk")
    protected int lDeptpk;
    @XmlElement(name = "l_profilepk")
    protected long lProfilepk;
    @XmlElement(name = "sz_bdate")
    protected String szBdate;
    @XmlElement(name = "f_disabled")
    protected int fDisabled;
    @XmlElement(name = "l_logontries")
    protected int lLogontries;
    @XmlElement(name = "f_nopasswd_change")
    protected int fNopasswdChange;
    @XmlElement(name = "l_ivrlogon")
    protected int lIvrlogon;
    @XmlElement(name = "l_language")
    protected int lLanguage;
    @XmlElement(name = "sz_adrlistserie")
    protected String szAdrlistserie;
    @XmlElement(name = "l_adrlist_sort")
    protected int lAdrlistSort;
    @XmlElement(name = "l_adrlist_search")
    protected int lAdrlistSearch;
    @XmlElement(name = "sz_statusserie")
    protected String szStatusserie;
    @XmlElement(name = "l_status_sort")
    protected int lStatusSort;
    @XmlElement(name = "l_status_search")
    protected int lStatusSearch;
    @XmlElement(name = "f_keepalive")
    protected int fKeepalive;
    @XmlElement(name = "l_recprpage")
    protected int lRecprpage;
    @XmlElement(name = "l_audiosetup")
    protected int lAudiosetup;
    @XmlElement(name = "l_status_owned_groups")
    protected int lStatusOwnedGroups;
    @XmlElement(name = "sz_email")
    protected String szEmail;
    @XmlElement(name = "sz_paspassword")
    protected String szPaspassword;
    @XmlElement(name = "l_allow_nondept_resend")
    protected int lAllowNondeptResend;
    @XmlElement(name = "sz_timezone")
    protected String szTimezone;
    @XmlElement(name = "f_blocklist_default")
    protected int fBlocklistDefault;
    @XmlElement(name = "f_session_expires_sec")
    protected int fSessionExpiresSec;
    @XmlElement(name = "sz_hash_paspwd")
    protected String szHashPaspwd;
    @XmlElement(name = "l_deptpklist")
    protected ArrayOfInt lDeptpklist;

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
     * Gets the value of the szUserid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzUserid() {
        return szUserid;
    }

    /**
     * Sets the value of the szUserid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzUserid(String value) {
        this.szUserid = value;
    }

    /**
     * Gets the value of the szPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPassword() {
        return szPassword;
    }

    /**
     * Sets the value of the szPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPassword(String value) {
        this.szPassword = value;
    }

    /**
     * Gets the value of the szName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzName() {
        return szName;
    }

    /**
     * Sets the value of the szName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzName(String value) {
        this.szName = value;
    }

    /**
     * Gets the value of the szSurname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSurname() {
        return szSurname;
    }

    /**
     * Sets the value of the szSurname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSurname(String value) {
        this.szSurname = value;
    }

    /**
     * Gets the value of the lComppk property.
     * 
     */
    public int getLComppk() {
        return lComppk;
    }

    /**
     * Sets the value of the lComppk property.
     * 
     */
    public void setLComppk(int value) {
        this.lComppk = value;
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
     * Gets the value of the lProfilepk property.
     * 
     */
    public long getLProfilepk() {
        return lProfilepk;
    }

    /**
     * Sets the value of the lProfilepk property.
     * 
     */
    public void setLProfilepk(long value) {
        this.lProfilepk = value;
    }

    /**
     * Gets the value of the szBdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzBdate() {
        return szBdate;
    }

    /**
     * Sets the value of the szBdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzBdate(String value) {
        this.szBdate = value;
    }

    /**
     * Gets the value of the fDisabled property.
     * 
     */
    public int getFDisabled() {
        return fDisabled;
    }

    /**
     * Sets the value of the fDisabled property.
     * 
     */
    public void setFDisabled(int value) {
        this.fDisabled = value;
    }

    /**
     * Gets the value of the lLogontries property.
     * 
     */
    public int getLLogontries() {
        return lLogontries;
    }

    /**
     * Sets the value of the lLogontries property.
     * 
     */
    public void setLLogontries(int value) {
        this.lLogontries = value;
    }

    /**
     * Gets the value of the fNopasswdChange property.
     * 
     */
    public int getFNopasswdChange() {
        return fNopasswdChange;
    }

    /**
     * Sets the value of the fNopasswdChange property.
     * 
     */
    public void setFNopasswdChange(int value) {
        this.fNopasswdChange = value;
    }

    /**
     * Gets the value of the lIvrlogon property.
     * 
     */
    public int getLIvrlogon() {
        return lIvrlogon;
    }

    /**
     * Sets the value of the lIvrlogon property.
     * 
     */
    public void setLIvrlogon(int value) {
        this.lIvrlogon = value;
    }

    /**
     * Gets the value of the lLanguage property.
     * 
     */
    public int getLLanguage() {
        return lLanguage;
    }

    /**
     * Sets the value of the lLanguage property.
     * 
     */
    public void setLLanguage(int value) {
        this.lLanguage = value;
    }

    /**
     * Gets the value of the szAdrlistserie property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzAdrlistserie() {
        return szAdrlistserie;
    }

    /**
     * Sets the value of the szAdrlistserie property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzAdrlistserie(String value) {
        this.szAdrlistserie = value;
    }

    /**
     * Gets the value of the lAdrlistSort property.
     * 
     */
    public int getLAdrlistSort() {
        return lAdrlistSort;
    }

    /**
     * Sets the value of the lAdrlistSort property.
     * 
     */
    public void setLAdrlistSort(int value) {
        this.lAdrlistSort = value;
    }

    /**
     * Gets the value of the lAdrlistSearch property.
     * 
     */
    public int getLAdrlistSearch() {
        return lAdrlistSearch;
    }

    /**
     * Sets the value of the lAdrlistSearch property.
     * 
     */
    public void setLAdrlistSearch(int value) {
        this.lAdrlistSearch = value;
    }

    /**
     * Gets the value of the szStatusserie property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzStatusserie() {
        return szStatusserie;
    }

    /**
     * Sets the value of the szStatusserie property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzStatusserie(String value) {
        this.szStatusserie = value;
    }

    /**
     * Gets the value of the lStatusSort property.
     * 
     */
    public int getLStatusSort() {
        return lStatusSort;
    }

    /**
     * Sets the value of the lStatusSort property.
     * 
     */
    public void setLStatusSort(int value) {
        this.lStatusSort = value;
    }

    /**
     * Gets the value of the lStatusSearch property.
     * 
     */
    public int getLStatusSearch() {
        return lStatusSearch;
    }

    /**
     * Sets the value of the lStatusSearch property.
     * 
     */
    public void setLStatusSearch(int value) {
        this.lStatusSearch = value;
    }

    /**
     * Gets the value of the fKeepalive property.
     * 
     */
    public int getFKeepalive() {
        return fKeepalive;
    }

    /**
     * Sets the value of the fKeepalive property.
     * 
     */
    public void setFKeepalive(int value) {
        this.fKeepalive = value;
    }

    /**
     * Gets the value of the lRecprpage property.
     * 
     */
    public int getLRecprpage() {
        return lRecprpage;
    }

    /**
     * Sets the value of the lRecprpage property.
     * 
     */
    public void setLRecprpage(int value) {
        this.lRecprpage = value;
    }

    /**
     * Gets the value of the lAudiosetup property.
     * 
     */
    public int getLAudiosetup() {
        return lAudiosetup;
    }

    /**
     * Sets the value of the lAudiosetup property.
     * 
     */
    public void setLAudiosetup(int value) {
        this.lAudiosetup = value;
    }

    /**
     * Gets the value of the lStatusOwnedGroups property.
     * 
     */
    public int getLStatusOwnedGroups() {
        return lStatusOwnedGroups;
    }

    /**
     * Sets the value of the lStatusOwnedGroups property.
     * 
     */
    public void setLStatusOwnedGroups(int value) {
        this.lStatusOwnedGroups = value;
    }

    /**
     * Gets the value of the szEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzEmail() {
        return szEmail;
    }

    /**
     * Sets the value of the szEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzEmail(String value) {
        this.szEmail = value;
    }

    /**
     * Gets the value of the szPaspassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPaspassword() {
        return szPaspassword;
    }

    /**
     * Sets the value of the szPaspassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPaspassword(String value) {
        this.szPaspassword = value;
    }

    /**
     * Gets the value of the lAllowNondeptResend property.
     * 
     */
    public int getLAllowNondeptResend() {
        return lAllowNondeptResend;
    }

    /**
     * Sets the value of the lAllowNondeptResend property.
     * 
     */
    public void setLAllowNondeptResend(int value) {
        this.lAllowNondeptResend = value;
    }

    /**
     * Gets the value of the szTimezone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzTimezone() {
        return szTimezone;
    }

    /**
     * Sets the value of the szTimezone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzTimezone(String value) {
        this.szTimezone = value;
    }

    /**
     * Gets the value of the fBlocklistDefault property.
     * 
     */
    public int getFBlocklistDefault() {
        return fBlocklistDefault;
    }

    /**
     * Sets the value of the fBlocklistDefault property.
     * 
     */
    public void setFBlocklistDefault(int value) {
        this.fBlocklistDefault = value;
    }

    /**
     * Gets the value of the fSessionExpiresSec property.
     * 
     */
    public int getFSessionExpiresSec() {
        return fSessionExpiresSec;
    }

    /**
     * Sets the value of the fSessionExpiresSec property.
     * 
     */
    public void setFSessionExpiresSec(int value) {
        this.fSessionExpiresSec = value;
    }

    /**
     * Gets the value of the szHashPaspwd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzHashPaspwd() {
        return szHashPaspwd;
    }

    /**
     * Sets the value of the szHashPaspwd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzHashPaspwd(String value) {
        this.szHashPaspwd = value;
    }

    /**
     * Gets the value of the lDeptpklist property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getLDeptpklist() {
        return lDeptpklist;
    }

    /**
     * Sets the value of the lDeptpklist property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setLDeptpklist(ArrayOfInt value) {
        this.lDeptpklist = value;
    }

}
