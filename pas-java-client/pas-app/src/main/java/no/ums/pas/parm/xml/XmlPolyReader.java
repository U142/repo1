package no.ums.pas.parm.xml;

import no.ums.pas.cellbroadcast.Area;
import no.ums.pas.cellbroadcast.CBMessage;
import no.ums.pas.cellbroadcast.CCode;
import no.ums.pas.cellbroadcast.CountryCodes;
import no.ums.pas.parm.voobjects.AlertVO;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;



public class XmlPolyReader {

	// Denne er ny for cell broadcast
	public void readCellBroadcast(Element parentnode, AlertVO alert) {
		NodeList listOfCellBroadcasts = parentnode.getElementsByTagName("cellbroadcast");
		AlertVO a;
		for(int i=0;i< listOfCellBroadcasts.getLength();i++) {
			Element cellNode = (Element)listOfCellBroadcasts.item(i); 
			String alertpk = cellNode.getAttribute("l_alertpk");
			String id = cellNode.getAttribute("sz_id");
			String area = cellNode.getAttribute("sz_area");
			//a = getAlert(alertpk);
			
			if(alert != null) {
				alert.setArea(new Area(id,area));
				// Her hentes meldingene ut
				NodeList messages = cellNode.getElementsByTagName("message");
				ArrayList<Object> cbmessages = new ArrayList<Object>();
				for(int j=0;j<messages.getLength();j++) {
					Element message = (Element)messages.item(j);
					String lang = message.getAttribute("sz_lang");
					String text = message.getAttribute("sz_text");
					String cboadc = message.getAttribute("sz_cb_oadc");
					
					NodeList ccodes = message.getElementsByTagName("ccode");
					ArrayList<CCode> arrCcodes = new ArrayList<CCode>();
					
					for(int k=0;k<ccodes.getLength();k++) {
						
						if(ccodes.item(k).getNodeType() == Node.ELEMENT_NODE ) {
							Element ccode = (Element)ccodes.item(k);
							arrCcodes.add(CountryCodes.getCountryByCCode(ccode.getTextContent()));
						}
					}
					cbmessages.add(new CBMessage(lang, arrCcodes, text, cboadc));					 
				}
				alert.setCBMessages(cbmessages);
				//Element elm_local = (Element)message.item(0);
				//Element elm_international = (Element)message.item(1);
				
//				String[] local  = { elm_local.getAttribute("sz_ccode"), elm_local.getAttribute("sz_text") };
//				a.setLocalLanguage(local);
//				String[] international = { elm_international.getAttribute("sz_ccode"), elm_international.getAttribute("sz_text") };
//				a.setInternationalLanguage(international);
				
				/*
				 *
				 <cellbroadcast l_alertpk="a500000000304" sz_area="Test1 (ID 1)" sz_cb_oadc="HilsenSvein" sz_id="1">
					<message sz_lang="English" sz_text="english for iron baners">
						<ccode>-1</ccode>
					</message>
					<message sz_lang="Scandivaian" sz_text="noregs dilldall">
						<ccode>0047</ccode>
						<ccode>0045</ccode>
						<ccode>0046</ccode>
					</message>
					<message sz_lang="Ze Germanz" sz_text="Do hast meine lederhosen gestohlen">
						<ccode>0041</ccode>
					</messages>
				  </cellbroadcast>

				 * 
				 */
				
			}
		}
	}
	

}
