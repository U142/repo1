package no.ums.pas.parm.object;

import no.ums.pas.PAS;
import no.ums.pas.parm.fieldlimit.TextFieldLimit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;



public class ObjectGUI extends JFrame implements WindowListener {
	public static final long serialVersionUID = 1;

	private Container gui;

	private ObjInfoPanel objInfoPanel;

	private PolygonPanel polygonPanel;

	private DescriptionPanel descriptionPanel;

	private ActionPanel actionPanel;

	public ObjectGUI(String title) {
		super(title);
		setIconImage(PAS.get_pas().get_appicon().getImage());
		this.gui = getContentPane();

		this.gui.add(this.objInfoPanel = new ObjInfoPanel());
		this.gui.add(this.polygonPanel = new PolygonPanel());
		this.gui.add(this.descriptionPanel = new DescriptionPanel());
		this.gui.add(this.actionPanel = new ActionPanel());

		setLayout();
		
		addWindowListener(this);
		
		setSize(400, 400);
		setTitle(title);
		setVisible(true);
		setAlwaysOnTop(true);
		super.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(350, 400));
	}
	
	public void setLayout() {
		this.gui.setLayout(new BoxLayout(this.gui, BoxLayout.Y_AXIS));
		this.objInfoPanel.setLayout(new BoxLayout(objInfoPanel, BoxLayout.X_AXIS));
		this.objInfoPanel.iptPanel.setLayout(new BoxLayout(this.objInfoPanel.iptPanel, BoxLayout.Y_AXIS));
		this.objInfoPanel.txtPanel.setLayout(new BoxLayout(this.objInfoPanel.txtPanel, BoxLayout.Y_AXIS));
		this.objInfoPanel.iptPanel.getMsgpanel().setLayout(new BoxLayout(this.objInfoPanel.iptPanel.getMsgpanel(), BoxLayout.X_AXIS));
		this.objInfoPanel.mandatoryPanel.setLayout(new BoxLayout(this.objInfoPanel.mandatoryPanel, BoxLayout.Y_AXIS));
		this.descriptionPanel.setLayout(new BoxLayout(this.descriptionPanel, BoxLayout.X_AXIS));
		this.descriptionPanel.iptPanel.setLayout(new BoxLayout(this.descriptionPanel.iptPanel, BoxLayout.Y_AXIS));
	}

	public class ObjInfoPanel extends JPanel {
		public static final long serialVersionUID = 1;

		private JTextField txtName, txtAdress, txtPostno, txtPlace, txtPhone;

		private JLabel lblName, lblCategory, lblAdress, lblPostno, lblPlace,
				lblPhone;

		private JComboBox cbxCategory;

		private TextPanel txtPanel;

		private InputPanel iptPanel;
		
		private MandatoryPanel mandatoryPanel;

		public ObjInfoPanel() {

			setBorder(BorderFactory
					.createTitledBorder(PAS.l("main_parm_object_dlg_information")));

			this.cbxCategory = new JComboBox();

			this.txtName = new JTextField();
			this.txtName.setDocument(new TextFieldLimit(50));
			this.txtAdress = new JTextField();
			this.txtPostno = new JTextField();
			this.txtPlace = new JTextField();
			this.txtPhone = new JTextField();

			this.lblName = new JLabel();
			this.lblCategory = new JLabel();
			this.lblAdress = new JLabel();
			this.lblPostno = new JLabel();
			this.lblPlace = new JLabel();
			this.lblPhone = new JLabel();
			
			this.txtPanel = new TextPanel();
			this.iptPanel = new InputPanel();
			this.mandatoryPanel = new MandatoryPanel();
			
			add(this.txtPanel);
			add(this.iptPanel);
			add(this.mandatoryPanel);
 
		}
 
		public class TextPanel extends JPanel {
			public static final long serialVersionUID = 1;
			public TextPanel() {
				JLabel lblTab[] = new JLabel[6];

				lblTab[0] = lblName;
				lblTab[1] = lblCategory;
				lblTab[2] = lblAdress;
				lblTab[3] = lblPostno;
				lblTab[4] = lblPlace;
				lblTab[5] = lblPhone;

				lblName.setText(PAS.l("main_parm_object_dlg_name"));
				lblCategory.setText(PAS.l("main_parm_object_dlg_category"));
				lblAdress.setText(PAS.l("main_parm_object_dlg_address"));
				lblPostno.setText(PAS.l("main_parm_object_dlg_postno"));
				lblPlace.setText(PAS.l("main_parm_object_dlg_place"));
				lblPhone.setText(PAS.l("main_parm_object_dlg_phone"));

				
				for (int i = 0; i < lblTab.length; i++) {
					add(lblTab[i]);
					add(Box.createVerticalStrut(5));
					lblTab[i].setPreferredSize(new Dimension(150, 18));
				}
			}
		}

		public class InputPanel extends JPanel {
			public static final long serialVersionUID = 1;

			private msgPanel msgpanel;
			
			public InputPanel() {
				JTextField txtTab[] = new JTextField[6];

				txtTab[0] = txtName;
				txtTab[2] = txtAdress;
				txtTab[3] = txtPostno;
				txtTab[4] = txtPlace;
				txtTab[5] = txtPhone;

				add(Box.createVerticalStrut(25));
				
				for (int i = 0; i < txtTab.length; i++) {
					if (i == 1) {
						add(cbxCategory);
						cbxCategory.setPreferredSize(new Dimension(200, 18));
					} else {
						add(txtTab[i]);
						txtTab[i].setPreferredSize(new Dimension(200, 18));
					}
					add(Box.createVerticalStrut(2));
				}
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
					add(new JLabel(" = " + PAS.l("common_mandatory_input")));
				}
			}

			public msgPanel getMsgpanel() {
				return msgpanel;
			}
		}
		
		public class MandatoryPanel extends JPanel {
			
			public static final long serialVersionUID = 1;
			private JLabel lblSign = new JLabel(" *"); 
			
			public MandatoryPanel() {
				this.lblSign.setForeground(Color.RED);
				//FontSet this.lblSign.setFont(new Font(null, Font.BOLD, 18));
				this.lblSign.setAlignmentX(Component.RIGHT_ALIGNMENT);
				add(this.lblSign);
				add(Box.createVerticalStrut(110));
			}
		}

		public JComboBox getCbxCategory() {
			return cbxCategory;
		}

		public InputPanel getIptPanel() {
			return iptPanel;
		}
		
		public JTextField getTxtAdress() {
			return txtAdress;
		}

		public JTextField getTxtName() {
			return txtName;
		}

		public TextPanel getTxtPanel() {
			return txtPanel;
		}

		public JTextField getTxtPhone() {
			return txtPhone;
		}

		public JTextField getTxtPlace() {
			return txtPlace;
		}

		public JTextField getTxtPostno() {
			return txtPostno;
		}
	}

	public class PolygonPanel extends JEditorPane {
		public static final long serialVersionUID = 1;

		private String msg = PAS.l("main_parm_object_dlg_mark");/*"After given an object "
				+ "necessary information, please mark the "
				+ "position on the map.";*/

		public PolygonPanel() {

			setBorder(BorderFactory
					.createTitledBorder(PAS.l("main_parm_object_dlg_set_polygon")));

			setText(msg);
			setEditable(false);
			setBackground(gui.getBackground());
		}
	}

	public class DescriptionPanel extends JPanel {
		public static final long serialVersionUID = 1;

		private JLabel lblDescription;

		private JTextArea txaDescription;

		private JScrollPane scrDescription;

		private JPanel txtPanel, iptPanel;

		public DescriptionPanel() {

			setLayout(new FlowLayout(FlowLayout.LEFT));

			this.txtPanel = new JPanel();
			this.iptPanel = new JPanel();

			this.lblDescription = new JLabel();
			this.lblDescription.setPreferredSize(new Dimension(110, 18));
			this.lblDescription.setText(PAS.l("main_parm_object_dlg_more_description"));

			this.txaDescription = new JTextArea();
			this.scrDescription = new JScrollPane(this.txaDescription);
			this.scrDescription.setAutoscrolls(true);

			this.txtPanel.add(lblDescription);
			this.iptPanel.add(scrDescription);

			txaDescription.setPreferredSize(new Dimension(1000, 600));
			scrDescription.setPreferredSize(new Dimension(1000, 600));
			
			add(this.txtPanel);
			add(this.iptPanel);
		}

		public class TextPanel extends JPanel {
			public static final long serialVersionUID = 1;
			public TextPanel() {

			}
		}

		public class InputPanel extends JPanel {
			public static final long serialVersionUID = 1;

			public InputPanel() {
				
			}
		}
		
		public JScrollPane getScrDescription() {
			return scrDescription;
		}

		public JTextArea getTxaDescription() {
			return txaDescription;
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

			this.btnSave.setText(PAS.l("common_save"));
			this.btnCancel.setText(PAS.l("common_cancel"));
		}

		public JButton getBtnCancel() {
			return btnCancel;
		}

		public JButton getBtnSave() {
			return btnSave;
		}
	}

	public ActionPanel getActionPanel() {
		return actionPanel;
	}

	public DescriptionPanel getDescriptionPanel() {
		return descriptionPanel;
	}

	public Container getGui() {
		return gui;
	}

	public ObjInfoPanel getObjInfoPanel() {
		return objInfoPanel;
	}

	public PolygonPanel getPolygonPanel() {
		return polygonPanel;
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
