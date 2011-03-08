/*
 * Created by JFormDesigner on Fri Mar 04 12:22:03 CET 2011
 */

package no.ums.pas.core.logon.view;

import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.*;
import no.ums.pas.core.logon.*;
import no.ums.pas.maps.*;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.converters.*;

/**
 * @author User #2
 */
public class Settings extends JDialog {
	public Settings(Frame owner) {
		super(owner);
		initComponents();
		comboLbaUpdate.addItem(new Integer(10));
		comboLbaUpdate.addItem(new Integer(20));
		comboLbaUpdate.addItem(new Integer(30));
		comboLbaUpdate.addItem(new Integer(40));
		comboLbaUpdate.addItem(new Integer(50));
	}

	public Settings(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void btnMapWmsOpenActionPerformed(ActionEvent e) {
		
	}

	private void comboLbaUpdateItemStateChanged(ItemEvent e) {
	}

	private void txtUserKeyPressed(KeyEvent e) {
		// TODO add your code here
	}

	private void settingsModel1PropertyChange(PropertyChangeEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		userinfo = new JPanel();
		lblUser = new JLabel();
		lblCompany = new JLabel();
		txtUser = new JTextField();
		txtCompany = new JTextField();
		autostartup = new JPanel();
		chkAutoStartParm = new JCheckBox();
		mapsettings = new JPanel();
		radioMapDefault = new JRadioButton();
		radioMapWms = new JRadioButton();
		txtMapWms = new JTextField();
		btnMapWmsOpen = new JButton();
		lblMapWmsUser = new JLabel();
		lblMapWmsPassword = new JLabel();
		txtMapWmsUser = new JTextField();
		txtMapWmsPassword = new JTextField();
		comboMapWmsImg = new JComboBox();
		scrollWMS = new JScrollPane();
		treeWMS = new WmsLayerTree();
		navigation = new JPanel();
		radioNavPanByClick = new JRadioButton();
		radioNavPanByDrag = new JRadioButton();
		radioNavZoomFromCenter = new JRadioButton();
		radioNavZoomFromCorner = new JRadioButton();
		panel4 = new JPanel();
		lblMailDisplayname = new JLabel();
		lblMailAddress = new JLabel();
		lblMailServer = new JLabel();
		txtMailDisplayname = new JTextField();
		txtMailAddress = new JTextField();
		txtMailServer = new JTextField();
		panel1 = new JPanel();
		lblLbaUpdate = new JLabel();
		comboLbaUpdate = new JComboBox();
		btnCancel = new JButton();
		btnSave = new JButton();
		settingsModel1 = new SettingsModel();
		stringToInt1 = new StringToInt();

		//======== this ========
		setAlwaysOnTop(true);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setResizable(false);
		Container contentPane = getContentPane();

		//======== userinfo ========
		{
			userinfo.setBorder(new TitledBorder("Userinfo"));

			//---- lblUser ----
			lblUser.setText("text");

			//---- lblCompany ----
			lblCompany.setText("text");

			//---- txtUser ----
			txtUser.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					txtUserKeyPressed(e);
				}
			});

			GroupLayout userinfoLayout = new GroupLayout(userinfo);
			userinfo.setLayout(userinfoLayout);
			userinfoLayout.setHorizontalGroup(
				userinfoLayout.createParallelGroup()
					.addGroup(userinfoLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(userinfoLayout.createParallelGroup()
							.addComponent(lblUser, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblCompany, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(userinfoLayout.createParallelGroup()
							.addComponent(txtCompany, GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
							.addComponent(txtUser, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(261, Short.MAX_VALUE))
			);
			userinfoLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {txtCompany, txtUser});
			userinfoLayout.setVerticalGroup(
				userinfoLayout.createParallelGroup()
					.addGroup(userinfoLayout.createSequentialGroup()
						.addGroup(userinfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(lblUser)
							.addComponent(txtUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(userinfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(lblCompany)
							.addComponent(txtCompany, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
		}

		//======== autostartup ========
		{
			autostartup.setBorder(new TitledBorder("Auto startup"));

			//---- chkAutoStartParm ----
			chkAutoStartParm.setText("text");

			GroupLayout autostartupLayout = new GroupLayout(autostartup);
			autostartup.setLayout(autostartupLayout);
			autostartupLayout.setHorizontalGroup(
				autostartupLayout.createParallelGroup()
					.addGroup(autostartupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(chkAutoStartParm, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(397, Short.MAX_VALUE))
			);
			autostartupLayout.setVerticalGroup(
				autostartupLayout.createParallelGroup()
					.addGroup(autostartupLayout.createSequentialGroup()
						.addComponent(chkAutoStartParm)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
		}

		//======== mapsettings ========
		{
			mapsettings.setBorder(new TitledBorder("Map Settings"));

			//---- radioMapDefault ----
			radioMapDefault.setText("text");

			//---- radioMapWms ----
			radioMapWms.setText("text");

			//---- btnMapWmsOpen ----
			btnMapWmsOpen.setText("text");
			btnMapWmsOpen.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnMapWmsOpenActionPerformed(e);
				}
			});

			//---- lblMapWmsUser ----
			lblMapWmsUser.setText("text");

			//---- lblMapWmsPassword ----
			lblMapWmsPassword.setText("text");

			//======== scrollWMS ========
			{
				scrollWMS.setViewportView(treeWMS);
			}

			GroupLayout mapsettingsLayout = new GroupLayout(mapsettings);
			mapsettings.setLayout(mapsettingsLayout);
			mapsettingsLayout.setHorizontalGroup(
				mapsettingsLayout.createParallelGroup()
					.addGroup(mapsettingsLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(mapsettingsLayout.createParallelGroup()
							.addComponent(scrollWMS, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
							.addGroup(mapsettingsLayout.createSequentialGroup()
								.addComponent(txtMapWms, GroupLayout.PREFERRED_SIZE, 409, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(btnMapWmsOpen, GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
							.addGroup(mapsettingsLayout.createSequentialGroup()
								.addComponent(radioMapDefault, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(radioMapWms, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
							.addGroup(mapsettingsLayout.createSequentialGroup()
								.addGroup(mapsettingsLayout.createParallelGroup()
									.addComponent(lblMapWmsUser, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
									.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addComponent(comboMapWmsImg, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblMapWmsPassword, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
									.addComponent(txtMapWmsPassword)
									.addComponent(txtMapWmsUser, GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 255, Short.MAX_VALUE)))
						.addContainerGap())
			);
			mapsettingsLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {lblMapWmsPassword, lblMapWmsUser});
			mapsettingsLayout.setVerticalGroup(
				mapsettingsLayout.createParallelGroup()
					.addGroup(mapsettingsLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(radioMapDefault)
							.addComponent(radioMapWms))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(txtMapWms, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnMapWmsOpen))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(lblMapWmsUser)
							.addComponent(txtMapWmsUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(lblMapWmsPassword)
							.addComponent(txtMapWmsPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(comboMapWmsImg, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scrollWMS, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
		}

		//======== navigation ========
		{
			navigation.setBorder(new TitledBorder("Navigation"));

			//---- radioNavPanByClick ----
			radioNavPanByClick.setText("text");

			//---- radioNavPanByDrag ----
			radioNavPanByDrag.setText("text");

			//---- radioNavZoomFromCenter ----
			radioNavZoomFromCenter.setText("text");

			//---- radioNavZoomFromCorner ----
			radioNavZoomFromCorner.setText("text");

			GroupLayout navigationLayout = new GroupLayout(navigation);
			navigation.setLayout(navigationLayout);
			navigationLayout.setHorizontalGroup(
				navigationLayout.createParallelGroup()
					.addGroup(navigationLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(navigationLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
							.addComponent(radioNavZoomFromCenter, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(radioNavPanByClick, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(navigationLayout.createParallelGroup()
							.addComponent(radioNavZoomFromCorner, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
							.addComponent(radioNavPanByDrag, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))
						.addContainerGap())
			);
			navigationLayout.setVerticalGroup(
				navigationLayout.createParallelGroup()
					.addGroup(navigationLayout.createSequentialGroup()
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(navigationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(radioNavPanByClick)
							.addComponent(radioNavPanByDrag))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(navigationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(radioNavZoomFromCenter)
							.addComponent(radioNavZoomFromCorner)))
			);
		}

		//======== panel4 ========
		{
			panel4.setBorder(new TitledBorder("E-mail settings"));

			//---- lblMailDisplayname ----
			lblMailDisplayname.setText("text");

			//---- lblMailAddress ----
			lblMailAddress.setText("text");

			//---- lblMailServer ----
			lblMailServer.setText("text");

			GroupLayout panel4Layout = new GroupLayout(panel4);
			panel4.setLayout(panel4Layout);
			panel4Layout.setHorizontalGroup(
				panel4Layout.createParallelGroup()
					.addGroup(panel4Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
							.addComponent(lblMailDisplayname, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
							.addComponent(lblMailAddress, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(lblMailServer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
							.addComponent(txtMailDisplayname, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)
							.addGroup(panel4Layout.createParallelGroup()
								.addComponent(txtMailServer, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
								.addComponent(txtMailAddress)))
						.addContainerGap(206, Short.MAX_VALUE))
			);
			panel4Layout.setVerticalGroup(
				panel4Layout.createParallelGroup()
					.addGroup(panel4Layout.createSequentialGroup()
						.addGroup(panel4Layout.createParallelGroup()
							.addGroup(panel4Layout.createSequentialGroup()
								.addComponent(lblMailDisplayname)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(lblMailAddress)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(lblMailServer))
							.addGroup(panel4Layout.createSequentialGroup()
								.addComponent(txtMailDisplayname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtMailAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtMailServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
		}

		//======== panel1 ========
		{
			panel1.setBorder(new TitledBorder("LBA"));

			//---- lblLbaUpdate ----
			lblLbaUpdate.setText("text");

			//---- comboLbaUpdate ----
			comboLbaUpdate.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					comboLbaUpdateItemStateChanged(e);
				}
			});

			GroupLayout panel1Layout = new GroupLayout(panel1);
			panel1.setLayout(panel1Layout);
			panel1Layout.setHorizontalGroup(
				panel1Layout.createParallelGroup()
					.addGroup(panel1Layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblLbaUpdate, GroupLayout.PREFERRED_SIZE, 239, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(comboLbaUpdate, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(216, Short.MAX_VALUE))
			);
			panel1Layout.setVerticalGroup(
				panel1Layout.createParallelGroup()
					.addGroup(panel1Layout.createSequentialGroup()
						.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(lblLbaUpdate)
							.addComponent(comboLbaUpdate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(5, Short.MAX_VALUE))
			);
		}

		//---- btnCancel ----
		btnCancel.setText("text");

		//---- btnSave ----
		btnSave.setText("text");

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(mapsettings, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(autostartup, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(userinfo, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(navigation, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		contentPaneLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCancel, btnSave});
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(userinfo, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(autostartup, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(mapsettings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(navigation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(panel4, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(panel1, GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
					.addGap(18, 18, 18)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(btnSave)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		pack();
		setLocationRelativeTo(getOwner());

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radioMapDefault);
		buttonGroup1.add(radioMapWms);

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(radioNavPanByClick);
		buttonGroup2.add(radioNavPanByDrag);

		//---- buttonGroup3 ----
		ButtonGroup buttonGroup3 = new ButtonGroup();
		buttonGroup3.add(radioNavZoomFromCenter);
		buttonGroup3.add(radioNavZoomFromCorner);

		//---- bindings ----
		bindingGroup = new BindingGroup();
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("username"),
			txtUser, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("companyid"),
			txtCompany, BeanProperty.create("text")));
		{
			Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
				settingsModel1, BeanProperty.create("lbaupdate"),
				comboLbaUpdate, BeanProperty.create("selectedItem"));
			binding.setConverter(stringToInt1);
			bindingGroup.addBinding(binding);
		}
		bindingGroup.bind();
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel userinfo;
	private JLabel lblUser;
	private JLabel lblCompany;
	private JTextField txtUser;
	private JTextField txtCompany;
	private JPanel autostartup;
	private JCheckBox chkAutoStartParm;
	private JPanel mapsettings;
	private JRadioButton radioMapDefault;
	private JRadioButton radioMapWms;
	private JTextField txtMapWms;
	private JButton btnMapWmsOpen;
	private JLabel lblMapWmsUser;
	private JLabel lblMapWmsPassword;
	private JTextField txtMapWmsUser;
	private JTextField txtMapWmsPassword;
	private JComboBox comboMapWmsImg;
	private JScrollPane scrollWMS;
	private WmsLayerTree treeWMS;
	private JPanel navigation;
	private JRadioButton radioNavPanByClick;
	private JRadioButton radioNavPanByDrag;
	private JRadioButton radioNavZoomFromCenter;
	private JRadioButton radioNavZoomFromCorner;
	private JPanel panel4;
	private JLabel lblMailDisplayname;
	private JLabel lblMailAddress;
	private JLabel lblMailServer;
	private JTextField txtMailDisplayname;
	private JTextField txtMailAddress;
	private JTextField txtMailServer;
	private JPanel panel1;
	private JLabel lblLbaUpdate;
	private JComboBox comboLbaUpdate;
	private JButton btnCancel;
	private JButton btnSave;
	public SettingsModel settingsModel1;
	private StringToInt stringToInt1;
	private BindingGroup bindingGroup;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
