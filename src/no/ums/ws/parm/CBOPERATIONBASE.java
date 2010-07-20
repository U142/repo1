
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_OPERATION_BASE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_OPERATION_BASE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="l_refno" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="l_projectpk" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="l_comppk" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="l_deptpk" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="l_userpk" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_OPERATION_BASE")
@XmlSeeAlso({
    CBALERTKILL.class,
    CBSENDBASE.class
})
public abstract class CBOPERATIONBASE {

    @XmlAttribute(name = "l_refno", required = true)
    protected long lRefno;
    @XmlAttribute(name = "l_projectpk", required = true)
    protected long lProjectpk;
    @XmlAttribute(name = "l_comppk", required = true)
    protected int lComppk;
    @XmlAttribute(name = "l_deptpk", required = true)
    protected int lDeptpk;
    @XmlAttribute(name = "l_userpk", required = true)
    protected long lUserpk;

    /**
     * Gets the value of the lRefno property.
     * 
     */
    public long getLRefno() {
        return lRefno;
    }

    /**
     * Sets the value of the lRefno property.
     * 
     */
    public void setLRefno(long value) {
        this.lRefno = value;
    }

    /**
     * Gets the value of the lProjectpk property.
     * 
     */
    public long getLProjectpk() {
        return lProjectpk;
    }

    /**
     * Sets the value of the lProjectpk property.
     * 
     */
    public void setLProjectpk(long value) {
        this.lProjectpk = value;
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

}
