package no.ums.pas.parm.alert;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.SendOptionToolbar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
		setLayout(new BorderLayout());
		add(pnlTop, BorderLayout.NORTH);
		toolbar.setIsAlert(true);
		setSize(300, 300);
		setVisible(true);
	}

	private void createToolbar(JPanel sendingPanel, SendOptionToolbar toolbar) {
        JLabel lblAHeader = new JLabel(Localization.l("main_parm_alert_dlg_area_definition"));
		JPanel pnlAreabtns = new JPanel();

		pnlAreabtns.setLayout(new BoxLayout(pnlAreabtns,BoxLayout.X_AXIS));

		pnlAreabtns.add(toolbar.get_btn_color());
		pnlAreabtns.add(toolbar.get_radio_polygon());
		pnlAreabtns.add(toolbar.get_radio_ellipse());
		pnlAreabtns.add(toolbar.get_btn_open());
		pnlAreabtns.add(toolbar.getBtnSaveArea());
		
        JLabel lblRTHeader = new JLabel(Localization.l("main_parm_alert_dlg_recipient_types"));
		JPanel pnlRtypesbtns = new JPanel();

		pnlRtypesbtns.setLayout(new BoxLayout(pnlRtypesbtns,BoxLayout.X_AXIS));

        toolbar.set_size(toolbar.get_btn_color(), SendOptionToolbar.SIZE_BUTTON_ICON, SendOptionToolbar.SIZE_BUTTON_ICON);
        toolbar.get_btn_color().setMaximumSize(new Dimension(SendOptionToolbar.SIZE_BUTTON_ICON,SendOptionToolbar.SIZE_BUTTON_ICON));
        toolbar.set_sendingname("", Localization.l("main_parm_alert_dlg_default_sendingname"));
		pnlRtypesbtns.add(toolbar);

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

		private JLabel lblDescription = new JLabel(Localization.l("main_parm_alert_dlg_description"));

        {
            lblDescription = new JLabel(Localization.l("main_parm_alert_dlg_description"));
        }

        private JLabel lblInformation = new JLabel(Localization.l("main_parm_alert_dlg_please_draw_area"));

        {
            lblInformation = new JLabel(Localization.l("main_parm_alert_dlg_please_draw_area"));
        }

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
            setBorder(BorderFactory.createTitledBorder(Localization.l("main_parm_alert_dlg_information")));
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
		private JButton btnSave = new JButton(Localization.l("common_save"));

        {
            btnSave = new JButton(Localization.l("common_save"));
        }

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
		private JButton btnCancel = new JButton(Localization.l("common_cancel"));

        {
            btnCancel = new JButton(Localization.l("common_cancel"));
        }

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
