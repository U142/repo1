
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
 *       &lt;attribute name="l_projectpk" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
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

    @XmlAttribute(name = "l_projectpk", required = true)
    protected long lProjectpk;

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

}
