package no.ums.pas.parm.event;

import no.ums.pas.PAS;
import no.ums.pas.localization.Localization;
import no.ums.pas.parm.fieldlimit.TextFieldLimit;
import no.ums.pas.ums.tools.ImageLoader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class EventGUI extends JFrame implements WindowListener {

	public static final long serialVersionUID = 1;
	private Container gui;

	private EventInputPanel eventInputPanel;

	private ActionPanel actionPanel;

	public EventGUI(String title) {
		super(title);
		setIconImage(PAS.get_pas().get_appicon().getImage());
		this.gui = getContentPane();

		this.eventInputPanel = new EventInputPanel();
		this.actionPanel = new ActionPanel();

		add(this.eventInputPanel);
		add(this.actionPanel, BorderLayout.SOUTH);

		this.setLayout();

		addWindowListener(this);
		
		setSize(400, 250);
		setVisible(true);
		setAlwaysOnTop(true);
		//super.setLocationRelativeTo(getParent().getParent());
		super.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(330, 250));
	}

	private void setLayout() {
		setLayout(new BoxLayout(this.gui, BoxLayout.Y_AXIS));
		this.eventInputPanel.setLayout(new BoxLayout(this.eventInputPanel,
				BoxLayout.X_AXIS));
		this.eventInputPanel.txtPanel.setLayout(new BoxLayout(
				this.eventInputPanel.txtPanel, BoxLayout.Y_AXIS));
		this.eventInputPanel.iptPanel.setLayout(new BoxLayout(
				this.eventInputPanel.iptPanel, BoxLayout.Y_AXIS));
	}

	public class EventInputPanel extends JPanel {
		public static final long serialVersionUID = 1;

		private TextPanel txtPanel;
		private InputPanel iptPanel;
		private MandatoryPanel mandatoryPanel;
		private JLabel lblName, lblDescription, lblCategory, lblSetEpicentre;
		private JTextField txtName;
		private JTextArea txaDesc;
		private JScrollPane scroll;
		private JComboBox cbxCategory;
		private JButton btnEpicentre; 

		public EventInputPanel() {

			setBorder(BorderFactory
					.createTitledBorder(Localization.l("main_parm_event_dlg_information")));

			txaDesc = new JTextArea(100,20);
			scroll = new JScrollPane(txaDesc);
		

			this.txtPanel = new TextPanel();
			this.iptPanel = new InputPanel();
			this.mandatoryPanel = new MandatoryPanel();

            this.lblName.setText(Localization.l("main_parm_event_dlg_name"));
            this.lblDescription.setText(Localization.l("main_parm_event_dlg_description"));
            this.lblCategory.setText(Localization.l("main_parm_event_dlg_category"));
			
			txaDesc.setPreferredSize(new Dimension(100, 60));
			//txaDesc.setWrapStyleWord(true);
			txaDesc.setLineWrap(true);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

			add(this.txtPanel);
			add(this.iptPanel);
			add(this.mandatoryPanel);

		}
		
		public JButton getBtnEpicentre() {
			return btnEpicentre;
		}

		public class TextPanel extends JPanel {
			public static final long serialVersionUID = 1;

			public TextPanel() {

				Dimension d = new Dimension(100, 18);
				add(Box.createVerticalStrut(4));
				add(lblName = new JLabel());
				lblName.setPreferredSize(d);
				add(Box.createVerticalStrut(4));
				add(lblDescription = new JLabel());
				lblDescription.setPreferredSize(d);
				add(Box.createVerticalStrut(40));
				add(lblCategory = new JLabel());
				lblCategory.setPreferredSize(d);
				add(Box.createVerticalStrut(10));
                add(lblSetEpicentre = new JLabel(Localization.l("main_parm_event_dlg_set_epicentre")));
				lblSetEpicentre.setVisible(false);
				add(Box.createVerticalStrut(28));
			}

		}

		public class InputPanel extends JPanel {
			public static final long serialVersionUID = 1;

			private msgPanel msgpanel;

			public InputPanel() {

				Dimension d = new Dimension(200, 18);

				add(Box.createVerticalStrut(6));
				add(txtName = new JTextField());
				txtName.setPreferredSize(d);
				txtName.setDocument(new TextFieldLimit(50));
				add(Box.createVerticalStrut(4));
				add(scroll);
				scroll.setPreferredSize(new Dimension(100, 70));
				add(Box.createVerticalStrut(4));
				add(cbxCategory = new JComboBox());
				cbxCategory.setPreferredSize(d);
				// MÅ ha en label og en knapp ting
				add(Box.createVerticalStrut(4));
				ImageIcon icon = ImageLoader.load_icon("epicentre_pinpoint.png");
				icon.setImage(icon.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));
				add(btnEpicentre = new JButton(icon));
				btnEpicentre.setVisible(false);
				add(this.msgpanel = new msgPanel());
			}

			public class msgPanel extends JPanel {
				public static final long serialVersionUID = 1;

				private JLabel lblSign = new JLabel("*");

				public msgPanel() {
					this.lblSign.setForeground(Color.RED);
					//FontSet this.lblSign.setFont(new Font(null, Font.BOLD, 18));
					this.lblSign.setAlignmentX(Component.RIGHT_ALIGNMENT);
					add(this.lblSign);
                    add(new JLabel(" = " + Localization.l("common_mandatory_input")));
				}
			}

			public msgPanel getMsgpanel() {
				return msgpanel;
			}

		}

		public class MandatoryPanel extends JPanel {

			public static final long serialVersionUID = 1;
			private JLabel lblSign = new JLabel("*");

			public MandatoryPanel() {

				this.lblSign.setForeground(Color.RED);
				//FontSet this.lblSign.setFont(new Font(null, Font.BOLD, 18));
				this.lblSign.setAlignmentX(Component.RIGHT_ALIGNMENT);
				add(this.lblSign);
				add(Box.createVerticalStrut(1));
			}
		}

		public JComboBox getCbxCategory() {
			return cbxCategory;
		}

		public MandatoryPanel getMandatoryPanel() {
			return mandatoryPanel;
		}

		public JScrollPane getScroll() {
			return scroll;
		}

		public JTextArea getTxaDesc() {
			return txaDesc;
		}

		public JTextField getTxtName() {
			return txtName;
		}

		public TextPanel getTxtPanel() {
			return txtPanel;
		}
	}

	public class ActionPanel extends JPanel {

		public static final long serialVersionUID = 1;
		private JButton btnSave, btnCancel;

		public ActionPanel() {
			setLayout(new FlowLayout(FlowLayout.CENTER));

			this.btnSave = new JButton();
			this.btnCancel = new JButton();

			add(btnSave);
			add(btnCancel);

            this.btnSave.setText(Localization.l("common_save"));
            this.btnCancel.setText(Localization.l("common_cancel"));
		}

		public JButton getBtnCancel() {
			return btnCancel;
		}

		public JButton getBtnSave() {
			return btnSave;
		}
	}

	public static void main(String args[]) {
		new EventGUI(PAS.l("main_parm_event_edit"));
	}

	public Container getGui() {
		return gui;
	}

	public ActionPanel getActionPanel() {
		return actionPanel;
	}

	public EventInputPanel getEventInputPanel() {
		return eventInputPanel;
	}

	public void windowActivated(WindowEvent e) {
		
	}

	public void windowClosed(WindowEvent e) {
		
	}

	public void windowClosing(WindowEvent e) {
		getActionPanel().btnCancel.doClick();		
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
