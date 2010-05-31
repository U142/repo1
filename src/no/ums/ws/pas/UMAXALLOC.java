
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UMAXALLOC complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UMAXALLOC">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_refno" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_projectpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_maxalloc" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UMAXALLOC", propOrder = {
    "nRefno",
    "nProjectpk",
    "nMaxalloc"
})
public class UMAXALLOC {

    @XmlElement(name = "n_refno")
    protected long nRefno;
    @XmlElement(name = "n_projectpk")
    protected long nProjectpk;
    @XmlElement(name = "n_maxalloc")
    protected int nMaxalloc;

    /**
     * Gets the value of the nRefno property.
     * 
     */
    public long getNRefno() {
        return nRefno;
    }

    /**
     * Sets the value of the nRefno property.
     * 
     */
    public void setNRefno(long value) {
        this.nRefno = value;
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
     * Gets the value of the nMaxalloc property.
     * 
     */
    public int getNMaxalloc() {
        return nMaxalloc;
    }

    /**
     * Sets the value of the nMaxalloc property.
     * 
     */
    public void setNMaxalloc(int value) {
        this.nMaxalloc = value;
    }

}
