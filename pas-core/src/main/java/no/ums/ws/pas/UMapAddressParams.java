
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UMapAddressParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UMapAddressParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/}UMapInfo">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UMapAddressParams")
@XmlSeeAlso({
    UMapAddressParamsByQuality.class
})
public class UMapAddressParams
    extends UMapInfo
{


}
