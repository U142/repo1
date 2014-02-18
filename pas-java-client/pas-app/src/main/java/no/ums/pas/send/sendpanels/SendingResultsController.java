package no.ums.pas.send.sendpanels;

import javax.swing.JOptionPane;
import no.ums.pas.core.dataexchange.soap.SoapExecAlert.SnapAlertResults;
import no.ums.pas.send.sendpanels.SendingResultsView.ISendingResultsUpdate;

public class SendingResultsController implements ISendingResultsUpdate {

	
	private int result = JOptionPane.NO_OPTION;
	private SendingResultsModel model;
	
	public SendingResultsController() {
		
	}
	
	public void init(SendingResultsModel model, SnapAlertResults res) {
		this.model = model;
		model.setResText(res.toString(false));
	}
	
	@Override
	public void onYes() {
		// TODO Auto-generated method stub
		result = JOptionPane.YES_OPTION;
		model.setYesOption();
	}

	@Override
	public void onNo() {
		// TODO Auto-generated method stub
		result = JOptionPane.NO_OPTION;
		model.setNoOption();
	}
	
	public int getAnswer() { return result; }

}
