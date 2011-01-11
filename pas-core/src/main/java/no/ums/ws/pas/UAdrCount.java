
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UAdrCount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UAdrCount">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_private_fixed" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_company_fixed" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_private_mobile" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_company_mobile" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_private_sms" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_company_sms" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_total_recipients" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_private_nonumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_company_nonumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_private_fax" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_company_fax" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_duplicates" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UAdrCount", propOrder = {
    "nPrivateFixed",
    "nCompanyFixed",
    "nPrivateMobile",
    "nCompanyMobile",
    "nPrivateSms",
    "nCompanySms",
    "nTotalRecipients",
    "nPrivateNonumber",
    "nCompanyNonumber",
    "nPrivateFax",
    "nCompanyFax",
    "nDuplicates"
})
public class UAdrCount {

    @XmlElement(name = "n_private_fixed")
    protected int nPrivateFixed;
    @XmlElement(name = "n_company_fixed")
    protected int nCompanyFixed;
    @XmlElement(name = "n_private_mobile")
    protected int nPrivateMobile;
    @XmlElement(name = "n_company_mobile")
    protected int nCompanyMobile;
    @XmlElement(name = "n_private_sms")
    protected int nPrivateSms;
    @XmlElement(name = "n_company_sms")
    protected int nCompanySms;
    @XmlElement(name = "n_total_recipients")
    protected int nTotalRecipients;
    @XmlElement(name = "n_private_nonumber")
    protected int nPrivateNonumber;
    @XmlElement(name = "n_company_nonumber")
    protected int nCompanyNonumber;
    @XmlElement(name = "n_private_fax")
    protected int nPrivateFax;
    @XmlElement(name = "n_company_fax")
    protected int nCompanyFax;
    @XmlElement(name = "n_duplicates")
    protected int nDuplicates;

    /**
     * Gets the value of the nPrivateFixed property.
     * 
     */
    public int getNPrivateFixed() {
        return nPrivateFixed;
    }

    /**
     * Sets the value of the nPrivateFixed property.
     * 
     */
    public void setNPrivateFixed(int value) {
        this.nPrivateFixed = value;
    }

    /**
     * Gets the value of the nCompanyFixed property.
     * 
     */
    public int getNCompanyFixed() {
        return nCompanyFixed;
    }

    /**
     * Sets the value of the nCompanyFixed property.
     * 
     */
    public void setNCompanyFixed(int value) {
        this.nCompanyFixed = value;
    }

    /**
     * Gets the value of the nPrivateMobile property.
     * 
     */
    public int getNPrivateMobile() {
        return nPrivateMobile;
    }

    /**
     * Sets the value of the nPrivateMobile property.
     * 
     */
    public void setNPrivateMobile(int value) {
        this.nPrivateMobile = value;
    }

    /**
     * Gets the value of the nCompanyMobile property.
     * 
     */
    public int getNCompanyMobile() {
        return nCompanyMobile;
    }

    /**
     * Sets the value of the nCompanyMobile property.
     * 
     */
    public void setNCompanyMobile(int value) {
        this.nCompanyMobile = value;
    }

    /**
     * Gets the value of the nPrivateSms property.
     * 
     */
    public int getNPrivateSms() {
        return nPrivateSms;
    }

    /**
     * Sets the value of the nPrivateSms property.
     * 
     */
    public void setNPrivateSms(int value) {
        this.nPrivateSms = value;
    }

    /**
     * Gets the value of the nCompanySms property.
     * 
     */
    public int getNCompanySms() {
        return nCompanySms;
    }

    /**
     * Sets the value of the nCompanySms property.
     * 
     */
    public void setNCompanySms(int value) {
        this.nCompanySms = value;
    }

    /**
     * Gets the value of the nTotalRecipients property.
     * 
     */
    public int getNTotalRecipients() {
        return nTotalRecipients;
    }

    /**
     * Sets the value of the nTotalRecipients property.
     * 
     */
    public void setNTotalRecipients(int value) {
        this.nTotalRecipients = value;
    }

    /**
     * Gets the value of the nPrivateNonumber property.
     * 
     */
    public int getNPrivateNonumber() {
        return nPrivateNonumber;
    }

    /**
     * Sets the value of the nPrivateNonumber property.
     * 
     */
    public void setNPrivateNonumber(int value) {
        this.nPrivateNonumber = value;
    }

    /**
     * Gets the value of the nCompanyNonumber property.
     * 
     */
    public int getNCompanyNonumber() {
        return nCompanyNonumber;
    }

    /**
     * Sets the value of the nCompanyNonumber property.
     * 
     */
    public void setNCompanyNonumber(int value) {
        this.nCompanyNonumber = value;
    }

    /**
     * Gets the value of the nPrivateFax property.
     * 
     */
    public int getNPrivateFax() {
        return nPrivateFax;
    }

    /**
     * Sets the value of the nPrivateFax property.
     * 
     */
    public void setNPrivateFax(int value) {
        this.nPrivateFax = value;
    }

    /**
     * Gets the value of the nCompanyFax property.
     * 
     */
    public int getNCompanyFax() {
        return nCompanyFax;
    }

    /**
     * Sets the value of the nCompanyFax property.
     * 
     */
    public void setNCompanyFax(int value) {
        this.nCompanyFax = value;
    }

    /**
     * Gets the value of the nDuplicates property.
     * 
     */
    public int getNDuplicates() {
        return nDuplicates;
    }

    /**
     * Sets the value of the nDuplicates property.
     * 
     */
    public void setNDuplicates(int value) {
        this.nDuplicates = value;
    }

}
