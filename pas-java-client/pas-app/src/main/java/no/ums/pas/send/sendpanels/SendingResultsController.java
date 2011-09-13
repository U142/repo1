package no.ums.pas.send.sendpanels;

import java.awt.Component;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.sun.java.swing.plaf.windows.resources.windows;

import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.dataexchange.soap.SoapExecAlert.SnapAlertResults;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.send.sendpanels.SendingResultsView.ISendingResultsUpdate;

public class SendingResultsController implements ISendingResultsUpdate {

	private final SendingResultsView dlg;
	private int result = JOptionPane.NO_OPTION;
	
	public SendingResultsController(Component parent, boolean modal, SnapAlertResults res) {
		dlg = new SendingResultsView(null, this);
		initializeGui(res);
		//dlg.initValues();
    	dlg.setLocationRelativeTo(parent);
    	dlg.setModal(modal);
    	dlg.setVisible(true);
	}
	
	public void initializeGui(SnapAlertResults res)
    {
		dlg.getLblTest().setText(res.toString(false));
    }
	
	@Override
	public void onYes() {
		// TODO Auto-generated method stub
		result = JOptionPane.YES_OPTION;
		dlg.dispose();
	}

	@Override
	public void onNo() {
		// TODO Auto-generated method stub
		result = JOptionPane.NO_OPTION;
		dlg.dispose();
	}
	
	public int getAnswer() { return result; }

}
