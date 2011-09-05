package no.ums.pas.send.cap;

import javax.swing.UIManager;

import no.ums.ws.common.parm.AlertInfoCertainty;
import no.ums.ws.common.parm.AlertInfoSeverity;
import no.ums.ws.common.parm.AlertInfoUrgency;
import no.ums.ws.common.parm.AlertScope;
import no.ums.ws.common.parm.AlertStatus;
import no.ums.ws.common.parm.ArrayOfAlertInfoCategory;
import no.ums.ws.common.parm.UMapSendingCapFields;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class ShowCapConfig {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		}
		catch(Exception e)
		{
			
		}
		CapConfigView view = new CapConfigView();
		UMapSendingCapFields cap = new UMapSendingCapFields();
		cap.setCertainty(AlertInfoCertainty.OBSERVED);
		cap.setStatus(AlertStatus.ACTUAL);
		cap.setScope(AlertScope.PUBLIC);
		cap.setNote("Notes");
		cap.setSource("Operator UMSPAS/RWTEST/MH");
		cap.setUrgency(AlertInfoUrgency.IMMEDIATE);
		cap.setCertainty(AlertInfoCertainty.OBSERVED);
		cap.setSeverity(AlertInfoSeverity.MODERATE);
		cap.setHeadline("Hvis kanal=SMS eller LBA, bruk innhold");
		cap.setDescription("Hvis PARM Alert, bruk alert-description");
		cap.setInstruction("");
		cap.setWeb("http://www.ums.no/");
		cap.setContact("");
		
		UMapSendingCapFields updated = view.edit(cap);
		System.exit(0);
		
	}

}
