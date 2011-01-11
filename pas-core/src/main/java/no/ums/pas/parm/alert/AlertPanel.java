package no.ums.pas.parm.alert;

import javax.swing.*;

import no.ums.pas.parm.fieldlimit.TextFieldLimit;


import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class AlertPanel extends JPanel implements WindowListener {
	public static final long serialVersionUID = 1;

//	private Container c = getContentPane();

	private TopPanel pnlTop = new TopPanel();

	private SendingPanel pnlSendings = new SendingPanel();

	private JPanel pnlSendingButton = new JPanel();

	private SendingSaveButtonPanel pnlSendingSaveButton = new SendingSaveButtonPanel();

	private SendingCancelButtonPanel pnlSendngCancelButton = new SendingCancelButtonPanel();

	public AlertPanel() {
//		super(title);
//		setIconImage(PAS.get_pas().get_appicon().getImage());
		setLayout(new BorderLayout());
		add(pnlTop, BorderLayout.NORTH);
		/*
		 * c.add(txtName); c.add(new JLabel("Description: "));
		 * c.add(txtDescription);
		 */
		// Her kommer polygon/punkt greier

		add(pnlSendingButton, BorderLayout.SOUTH);
		pnlSendingButton.setLayout(new BoxLayout(pnlSendingButton,
				BoxLayout.X_AXIS));
		pnlSendingButton.add(pnlSendingSaveButton);
		pnlSendingButton.add(pnlSendngCancelButton);
		setSize(300, 300);
		setVisible(true);
	}

	public JTextField getTxtName() {
		return pnlTop.getTxtName();
	}

	public JTextArea getTxtDescription() {
		return pnlTop.getTxaDescription();
	}

	public JTextField getTxtOadc() {
		return pnlTop.getTxtOadc();
	}

	public JComboBox getCbxValidity() {
		return pnlTop.getCbxValidity();
	}

	public JButton getBtnSave() {
		return pnlSendingSaveButton.getBtnSave();
	}

	public JButton getBtnCancel() {
		return pnlSendngCancelButton.getBtnCancel();
	}

	public JPanel getPnlSending() {
		return pnlSendings;
	}

//	public static void main(String args[]) {
//		new AlertGUI("Create Alert");
//	}

	public class TopPanel extends JPanel {
		public static final long serialVersionUID = 1;

		private JLabel lblName = new JLabel("Name:");

		private JLabel lblDescription = new JLabel("Description:");

		private JLabel lblOadc = new JLabel("Sender number:");

		private JLabel lblValidity = new JLabel("Valid for:");

		private JLabel lblValidityDays = new JLabel("day(s)");

		private JLabel lblSign = new JLabel("*");
		
		private JLabel lblSign2 = new JLabel("*");

		private JLabel lblMandatory = new JLabel(" = Mandatory input.");

		private JTextField txtName = new JTextField();

		private JTextArea txaDescription = new JTextArea();

		private JScrollPane scroll = new JScrollPane(txaDescription);

		private JTextField txtOadc = new JTextField();

		private JComboBox cbxValidity = new JComboBox();

		private JPanel pnlContainerLeft = new JPanel();

		private JPanel pnlContainerRigth = new JPanel();

		private JPanel pnlValidity = new JPanel();

		private JPanel pnlMandatory = new JPanel();
		
		private JPanel pnlMsg = new JPanel();

		public TopPanel() {
			setBorder(BorderFactory.createTitledBorder("Alert Information Input"));
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			lblSign.setForeground(Color.RED);
			//FontSet lblSign.setFont(new Font(null, Font.BOLD, 18));
			lblSign.setAlignmentX(Component.RIGHT_ALIGNMENT);
			
			lblSign2.setForeground(Color.RED);
			//FontSet lblSign2.setFont(new Font(null, Font.BOLD, 18));
			lblSign2.setAlignmentX(Component.RIGHT_ALIGNMENT);
			
			this.txtName.setDocument(new TextFieldLimit(50));
	
			add(pnlContainerLeft);
			pnlContainerLeft.setLayout(new BoxLayout(pnlContainerLeft,
					BoxLayout.Y_AXIS));

			setLeftComp(new Dimension(150, 18));
			add(pnlContainerRigth);
			pnlContainerRigth.setLayout(new BoxLayout(pnlContainerRigth,
					BoxLayout.Y_AXIS));

			add(this.pnlMandatory);
			
			pnlMandatory.setLayout(new BoxLayout(pnlMandatory, BoxLayout.Y_AXIS));
			setMandatory(new Dimension(20, 18));
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			setRightComp(new Dimension(200, 18));
		}

		private void setLeftComp(Dimension d) {
			pnlContainerLeft.add(lblName);
			lblName.setPreferredSize(d);
			pnlContainerLeft.add(Box.createVerticalStrut(4));
			pnlContainerLeft.add(lblDescription);
			lblDescription.setPreferredSize(d);
			pnlContainerLeft.add(Box.createVerticalStrut(57));
			pnlContainerLeft.add(lblOadc);
			lblOadc.setPreferredSize(d);
			pnlContainerLeft.add(Box.createVerticalStrut(4));
			pnlContainerLeft.add(lblValidity);
			lblValidity.setPreferredSize(d);
			pnlContainerLeft.add(Box.createVerticalStrut(30));
		}

		private void setRightComp(Dimension d) {
			pnlContainerRigth.add(txtName);
			txtName.setPreferredSize(d);
			pnlContainerRigth.add(Box.createVerticalStrut(4));
			pnlContainerRigth.add(scroll);
			scroll.setPreferredSize(new Dimension(200, 70));
			pnlContainerRigth.add(Box.createVerticalStrut(4));
			pnlContainerRigth.add(txtOadc);
			txtOadc.setPreferredSize(d);
			pnlContainerRigth.add(Box.createVerticalStrut(4));
			// Må ha et panel for å få med labelen i samme kolonne
			pnlValidity.setLayout(new BoxLayout(pnlValidity, BoxLayout.X_AXIS));
			pnlValidity.add(cbxValidity);
			cbxValidity.setPreferredSize(new Dimension(170, 18));
			pnlValidity.add(Box.createHorizontalStrut(4));
			pnlValidity.add(lblValidityDays);
			pnlContainerRigth.add(pnlValidity);
			// Dette skulle være hardkodet
			for (int i = 1; i <= 7; i++) {
				cbxValidity.addItem(new Integer(i));
			}
			pnlContainerRigth.add(Box.createVerticalStrut(4));
			pnlMsg.setLayout(new BoxLayout(pnlMsg, BoxLayout.X_AXIS));
			pnlMsg.add(this.lblSign2);
			pnlMsg.add(this.lblMandatory);
			pnlContainerRigth.add(pnlMsg);
		}

		private void setMandatory(Dimension d) {
			pnlMandatory.add(lblSign);
			pnlMandatory.add(Box.createVerticalStrut(160));
			pnlMandatory.setPreferredSize(d);
		}

		public JComboBox getCbxValidity() {
			return cbxValidity;
		}

		public JTextField getTxtName() {
			return txtName;
		}

		public JTextArea getTxaDescription() {
			return txaDescription;
		}

		public JTextField getTxtOadc() {
			return txtOadc;
		}
	}

	public class SendingSaveButtonPanel extends JPanel {
		public static final long serialVersionUID = 1;
		private JButton btnSave = new JButton("Save");

		public SendingSaveButtonPanel() {
			setLayout(new FlowLayout());
			add(btnSave);
			btnSave.setSize(30, 30);
		}

		public JButton getBtnSave() {
			return btnSave;
		}
	}

	public class SendingPanel extends JPanel {
		public static final long serialVersionUID = 1;

		public SendingPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(new JLabel("Her skal toolbaren komme"));
		}
	}

	public class SendingCancelButtonPanel extends JPanel {
		public static final long serialVersionUID = 1;
		private JButton btnCancel = new JButton("Cancel");

		public SendingCancelButtonPanel() {
			setLayout(new FlowLayout());
			add(btnCancel);
			btnCancel.setSize(10, 10);
		}

		public JButton getBtnCancel() {
			return btnCancel;
		}
	}

	public void windowActivated(WindowEvent e) {
		
	}

	public void windowClosed(WindowEvent e) {
		
	}

	public void windowClosing(WindowEvent e) {
		this.getBtnCancel().doClick();		
	}

	public void windowDeactivated(WindowEvent e) {
		
	}

	public void windowDeiconified(WindowEvent e) {
		
	}

	public void windowIconified(WindowEvent e) {
		
	}

	public void windowOpened(WindowEvent e) {
		
	}
}
