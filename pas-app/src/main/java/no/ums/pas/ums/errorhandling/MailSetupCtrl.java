package no.ums.pas.ums.errorhandling;

import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.parm.xml.XmlWriter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class MailSetupCtrl implements ActionListener {
	/**
	 * @param args
	 */
	
	private MailSetupGUI gui;
	private MailAccount account;
	private boolean returnValue;
	
	public MailSetupCtrl(){
		
	}
	
	public boolean editAccount(MailAccount account){
		/*gui = new MailSetupGUI();
		gui.getBtnCancel().addActionListener(this);
		gui.getBtnSave().addActionListener(this);*/
		this.account = account;
		
		gui = new MailSetupGUI(this);
		gui.open();

		return returnValue;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == gui.getBtnCancel()) {
			gui.close();
			returnValue = false;
			//gui.setVisible(false);
			//gui.dispose();
		}
		if(e.getSource() == gui.getBtnSave()){
			// Her skal jeg sette verdiene inn i account
			try
			{
				gui.save();
				account.set_displayname(gui.getSenderName());
				account.set_mailserver(gui.getOutgoingServer());
				account.set_mailaddress(gui.getReplyAddress());
				XmlWriter writer = new XmlWriter();
				writer.saveSettings();
				returnValue = true;
			}
			catch(Exception err)
			{
				
			}
			//gui.setVisible(false);
			//gui.dispose();
		}
	}
}
