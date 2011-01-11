package no.ums.pas.parm.alert;

import javax.swing.*;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.pas.send.ToggleAddresstype;




import java.awt.*;
import java.awt.event.*;

public class AlertGUI extends JPanel implements WindowListener, ActionListener {
	public static final long serialVersionUID = 1;

//	private Container c = getContentPane();

	private TopPanel pnlTop;

	//private SendingPanel pnlSendings = new SendingPanel();

	private JPanel pnlSendingButton = new JPanel();
	private SendOptionToolbar m_toolbar = null;
	public SendOptionToolbar get_toolbar() { return m_toolbar; }

	private SendingSaveButtonPanel pnlSendingSaveButton = new SendingSaveButtonPanel();

	private SendingCancelButtonPanel pnlSendngCancelButton = new SendingCancelButtonPanel();

	public AlertGUI(String title, SendOptionToolbar toolbar) {
		super();
		m_toolbar = toolbar;
		pnlTop = new TopPanel();
		pnlTop.add_controls();
//		super(title);
//		setIconImage(PAS.get_pas().get_appicon().getImage());
		setLayout(new BorderLayout());
		add(pnlTop, BorderLayout.NORTH);
		/*
		 * c.add(txtName); c.add(new JLabel("Description: "));
		 * c.add(txtDescription);
		 */
		// Her kommer polygon/punkt greier
		toolbar.setIsAlert(true);
		//toolbar.setVisible(false);
		
		
		
		//add(new JScrollPane(toolbar), BorderLayout.CENTER);

		//add(pnlSendingButton, BorderLayout.SOUTH);
		//pnlSendingButton.setLayout(new BoxLayout(pnlSendingButton,
		//		BoxLayout.X_AXIS));
		
		//createToolbar(pnlTop, toolbar);
		
//		addWindowListener(this);
		setSize(300, 300);
		setVisible(true);
//		setAlwaysOnTop(true);
	}

	private void createToolbar(JPanel sendingPanel, SendOptionToolbar toolbar) {
		
		JLabel lblAHeader = new JLabel(PAS.l("main_parm_alert_dlg_area_definition"));
		//pnlTop.set_gridconst(0, pnlTop.inc_panels() , 10, 1);
		
		
		JPanel pnlAreabtns = new JPanel();
		
		//toolbar.get_colorbutton().getParent().setPreferredSize(new Dimension(25,25));
		//pnlAreabtns.setPreferredSize(new Dimension(200,200));
		
		pnlAreabtns.setLayout(new BoxLayout(pnlAreabtns,BoxLayout.X_AXIS));
		//pnlAreabtns.setBorder(BorderFactory.createLineBorder(Color.blue));
				
		pnlAreabtns.add(toolbar.get_btn_color());
		pnlAreabtns.add(toolbar.get_radio_polygon());
		pnlAreabtns.add(toolbar.get_radio_ellipse());
		pnlAreabtns.add(toolbar.get_btn_open());
		
		//lblAHeader.setAlignmentY(Component.LEFT_ALIGNMENT);
		//pnlTop.set_gridconst(0, pnlTop.inc_panels(), 10, 1);
		JLabel lblRTHeader = new JLabel(PAS.l("main_parm_alert_dlg_recipient_types"));
		
		
		JPanel pnlRtypesbtns = new JPanel();
		pnlRtypesbtns.setLayout(new BoxLayout(pnlRtypesbtns,BoxLayout.X_AXIS));
		/*
		pnlRtypesbtns.add(toolbar.get_adrtype_private_fixed());
		pnlRtypesbtns.add(toolbar.get_adrtype_private_mobile());
		pnlRtypesbtns.add(toolbar.get_adrtype_company_fixed());
		pnlRtypesbtns.add(toolbar.get_adrtype_company_mobile());
		pnlRtypesbtns.add(toolbar.get_adrtype_cell_broadcast_text());
		pnlRtypesbtns.add(toolbar.get_adrtype_cell_broadcast_voice());
		pnlRtypesbtns.add(toolbar.get_adrtype_nofax());
		*/
		toolbar.set_sendingname("", PAS.l("main_parm_alert_dlg_default_sendingname"));
		//toolbar.setBorder(null);
		pnlRtypesbtns.add(toolbar);
		//JPanel pnlRTypesLbls = new JPanel();
		//pnl
		
		//pnlTop.add_spacing(DefaultPanel.DIR_VERTICAL, 20);
		pnlTop.set_gridconst(0, pnlTop.get_panel(), 10, 1);
		pnlTop.add(lblAHeader, pnlTop.m_gridconst);
		pnlTop.set_gridconst(5, pnlTop.inc_panels(), 10, 1);
		pnlTop.add(pnlAreabtns, pnlTop.m_gridconst);
		pnlTop.add_spacing(DefaultPanel.DIR_VERTICAL, 20);
		pnlTop.add(lblRTHeader, pnlTop.m_gridconst);
		pnlTop.set_gridconst(5, pnlTop.inc_panels(), 10, 1);
		pnlTop.add(pnlRtypesbtns, pnlTop.m_gridconst);		
		pnlTop.add_spacing(DefaultPanel.DIR_VERTICAL, 20);
		lblRTHeader.setAlignmentY(Component.LEFT_ALIGNMENT);
	}
	
	public JTextArea getTxtDescription() {
		return pnlTop.getTxaDescription();
	}

	public JButton getBtnSave() {
		return pnlSendingSaveButton.getBtnSave();
	}

	public JButton getBtnCancel() {
		return pnlSendngCancelButton.getBtnCancel();
	}

	/*public JPanel getPnlSending() {
		return pnlSendings;
	}*/

//	public static void main(String args[]) {
//		new AlertGUI("Create Alert");
//	}

	public class TopPanel extends DefaultPanel {
		public static final long serialVersionUID = 1;

		private JLabel lblDescription = new JLabel(PAS.l("main_parm_alert_dlg_description"));
		private JLabel lblInformation = new JLabel(PAS.l("main_parm_alert_dlg_please_draw_area"));
		private JTextArea txaDescription = new JTextArea();
		private JScrollPane scroll = new JScrollPane(txaDescription);
		private JPanel pnlContainerLeft = new JPanel();
		private JPanel pnlContainerRigth = new JPanel();
		private JPanel pnlValidity = new JPanel();
		private JPanel pnlMandatory = new JPanel();
		private JPanel pnlMsg = new JPanel();

		public TopPanel() {
			super();
		}
		

		private void setLeftComp(Dimension d) {
			pnlContainerLeft.add(Box.createVerticalStrut(4));
			pnlContainerLeft.add(lblDescription);
			lblDescription.setPreferredSize(d);
			pnlContainerLeft.add(Box.createVerticalStrut(57));
			pnlContainerLeft.add(Box.createVerticalStrut(4));
			pnlContainerLeft.add(Box.createVerticalStrut(30));
			//pnlContainerLeft.add(lblInformation);
			//FontSet lblInformation.setFont(new Font("bold",Font.BOLD,lblInformation.getFont().getSize()));
		}

		private void setRightComp(Dimension d) {
			pnlContainerRigth.add(Box.createVerticalStrut(4));
			pnlContainerRigth.add(scroll);
			scroll.setPreferredSize(new Dimension(200, 70));
			pnlContainerRigth.add(Box.createVerticalStrut(4));
			pnlContainerRigth.add(Box.createVerticalStrut(4));
			// Må ha et panel for å få med labelen i samme kolonne
			pnlValidity.add(Box.createHorizontalStrut(4));
			pnlContainerRigth.add(pnlValidity);
			
			pnlContainerRigth.add(Box.createVerticalStrut(4));
			//pnlMsg.setLayout(new BoxLayout(pnlMsg, BoxLayout.X_AXIS));
			//pnlContainerRigth.add(pnlMsg);
		}

		private void setMandatory(Dimension d) {
			pnlMandatory.add(Box.createVerticalStrut(160));
			pnlMandatory.setPreferredSize(d);
		}

		public JTextArea getTxaDescription() {
			return txaDescription;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
		}

		@Override
		public void add_controls() {
			setBorder(BorderFactory.createTitledBorder(PAS.l("main_parm_alert_dlg_information")));
			//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			add_spacing(DIR_VERTICAL, 20);
			createToolbar(this,m_toolbar);
	
			set_gridconst(0, inc_panels(), 5, 1);
			add(pnlContainerLeft,m_gridconst);
			pnlContainerLeft.setLayout(new BoxLayout(pnlContainerLeft,
					BoxLayout.Y_AXIS));

			setLeftComp(new Dimension(150, 18));
			set_gridconst(5, get_panel(), 10, 1);
			add(pnlContainerRigth, m_gridconst);
			pnlContainerRigth.setLayout(new BoxLayout(pnlContainerRigth,
					BoxLayout.Y_AXIS));
			
			//add(this.pnlMandatory);
			
			//pnlMandatory.setLayout(new BoxLayout(pnlMandatory, BoxLayout.Y_AXIS));
			//setMandatory(new Dimension(20, 18));
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			setRightComp(new Dimension(200, 18));

		}

		@Override
		public void init() {
			
		}

	}

	public class SendingSaveButtonPanel extends JPanel {
		public static final long serialVersionUID = 1;
		private JButton btnSave = new JButton(PAS.l("common_save"));

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
		private JButton btnCancel = new JButton(PAS.l("common_cancel"));

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

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
