package no.ums.pas.ums.errorhandling;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MailSetupGUI extends JDialog {

	public static final long serialVersionUID = 1;
	private JLabel lblInfo, lblOutgoingServer, lblReplyAddress, lblName;
	private JTextField txtOutgoingServer, txtReplyAddress, txtName;
	private JButton btnSave, btnCancel;
	private JPanel pnlCenter, pnlSouth;
	private int returnCode = -1;
	public int getReturnCode() { return returnCode; }
	private String sz_sendername;
	private String sz_outgoingserver;
	private String sz_replyaddress;
	public String getSenderName() { return sz_sendername; }
	public String getOutgoingServer() { return sz_outgoingserver; }
	public String getReplyAddress() { return sz_replyaddress; }
	
	public MailSetupGUI(ActionListener callback) {
		//super(null,"Mailserver configuration",true);
		super();
		setTitle("Mailserver configuration");
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		lblInfo = new JLabel("Please enter the following info to send mail");
		add(lblInfo,BorderLayout.NORTH);
		
		lblName = new JLabel("Name:");
		lblReplyAddress = new JLabel("E-mail address:");
		lblOutgoingServer = new JLabel("Outgoing mailserver(SMTP):");
		
		txtName = new JTextField();
		txtReplyAddress = new JTextField();
		txtOutgoingServer = new JTextField();
		
		btnSave = new JButton("Save and send mail");
		btnCancel = new JButton("Cancel");
		
		pnlCenter = new JPanel();
		pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
		
		pnlCenter.add(lblName);
		pnlCenter.add(txtName);
		pnlCenter.add(lblReplyAddress);
		pnlCenter.add(txtReplyAddress);
		pnlCenter.add(lblOutgoingServer);
		pnlCenter.add(txtOutgoingServer);
		add(pnlCenter,BorderLayout.CENTER);
		
		pnlSouth = new JPanel();
		pnlSouth.setLayout(new FlowLayout());
		pnlSouth.add(btnSave);
		pnlSouth.add(btnCancel);
		add(pnlSouth,BorderLayout.SOUTH);
		
		setSize(300,200);
		btnSave.addActionListener(callback);
		btnCancel.addActionListener(callback);
	}
	public void open() {
		try {
			setModal(true);
		} catch(Exception e) {
			
		}		
		setVisible(true);
	}
	public void save() {
		sz_sendername = txtName.getText();
		sz_outgoingserver = txtOutgoingServer.getText();
		sz_replyaddress = txtReplyAddress.getText();
		setVisible(false);
	}
	public void close() {
		setVisible(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(btnSave)) {
			returnCode = 0;
		} else if(e.getSource().equals(btnCancel)) {
			returnCode = 1;
		}
		setVisible(false);
	}

	public JTextField getTxtOutgoingServer() {
		return txtOutgoingServer;
	}

	public JTextField getTxtReplyAddress() {
		return txtReplyAddress;
	}
	
	public JButton getBtnCancel() {
		return btnCancel;
	}

	public JButton getBtnSave() {
		return btnSave;
	}

	public JTextField getTxtName() {
		return txtName;
	}
}
