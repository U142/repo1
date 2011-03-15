/*
 * Created by JFormDesigner on Fri Mar 04 12:22:03 CET 2011
 */

package no.ums.pas.core.logon.view;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import no.ums.pas.core.logon.*;
import no.ums.pas.localization.Localization;

import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.converters.*;
import org.jdesktop.validation.VisibleValidation;

/**
 * @author User #2
 */
public class Settings extends JDialog {

	/**
	 * 
	 * @author Modda
	 * Interface for communicating with SettingsCtrl
	 */
	public interface SettingsUpdate
	{
		public void onOpenWmsSite(final WmsLayerTree tree, final String wmsUrl, final String wmsUser, final String wmsPassword, final JComboBox imageformats);
		public void onWmsLayerSelect(final WmsLayerTree tree, final TreeSelectionEvent e);
		public void onCancel();
		public void onOk(final WmsLayerTree tree, final SettingsModel model);
		public void onMapWmsSelected(boolean b);
		public void onMoveLayerUp(final WmsLayerTree tree);
		public void onMoveLayerDown(final WmsLayerTree tree);
	}

	
	public Settings(Frame owner, SettingsUpdate callback) {
		super(owner);
		this.callback = callback;
		initComponents();
		comboLbaUpdate.addItem(new Integer(10));
		comboLbaUpdate.addItem(new Integer(20));
		comboLbaUpdate.addItem(new Integer(30));
		comboLbaUpdate.addItem(new Integer(40));
		comboLbaUpdate.addItem(new Integer(50));
	}
	SettingsUpdate callback;


	public Settings(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void btnMapWmsOpenActionPerformed(ActionEvent e) {
		callback.onOpenWmsSite(treeWMS, settingsModel1.getWmsUrl(), settingsModel1.getWmsUsername(), settingsModel1.getWmsPassword(), comboMapWmsImg);
	}

	private void comboLbaUpdateItemStateChanged(ItemEvent e) {
	}

	private void txtUserKeyPressed(KeyEvent e) {
	}

	private void settingsModel1PropertyChange(PropertyChangeEvent e) {
	}

	public WmsLayerTree getTreeWMS() {
		return treeWMS;
	}

	private void treeWMSValueChanged(TreeSelectionEvent e) {
		callback.onWmsLayerSelect(treeWMS, e);
	}

	private void btnCancelActionPerformed(ActionEvent e) {
		callback.onCancel();
	}

	private void btnSaveActionPerformed(ActionEvent e) {
		if(onValidate())
			callback.onOk(treeWMS, settingsModel1);
	}
	private boolean onValidate()
	{
		boolean b_error = false;
		//validate email address
				//"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.(?:[A-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)$");
		//validate email server
		if(settingsModel1.getEmailAddress().length()>0)
			b_error ^= !VisibleValidation.validateEmail(settingsModel1.getEmailAddress(), txtMailAddress);
		if(settingsModel1.getEmailServer().length()>0)
			b_error ^= !VisibleValidation.validateHost(settingsModel1.getEmailServer(), txtMailServer);
		if(settingsModel1.getMapSiteWms())
			b_error ^= !VisibleValidation.validateUrl(settingsModel1.getWmsUrl(), txtMapWms);
		else
			VisibleValidation.validateUrl("http://www.ums.no/map/", txtMapWms);
		
		getBtnSave().setEnabled(!b_error);
		return !b_error;
	}

	private void radioMapDefaultActionPerformed(ActionEvent e) {
		callback.onMapWmsSelected(false);
	}

	private void radioMapWmsActionPerformed(ActionEvent e) {
		callback.onMapWmsSelected(true);
		onValidate();
	}

	public JTextField getTxtMapWms() {
		return txtMapWms;
	}

	public JButton getBtnMapWmsOpen() {
		return btnMapWmsOpen;
	}

	public JTextField getTxtMapWmsUser() {
		return txtMapWmsUser;
	}

	public JPasswordField getTxtMapWmsPassword() {
		return txtMapWmsPassword;
	}

	public JComboBox getComboMapWmsImg() {
		return comboMapWmsImg;
	}

	public JRadioButton getRadioNavPanByClick() {
		return radioNavPanByClick;
	}

	public JRadioButton getRadioNavPanByDrag() {
		return radioNavPanByDrag;
	}

	public JRadioButton getRadioNavZoomFromCenter() {
		return radioNavZoomFromCenter;
	}

	public JRadioButton getRadioNavZoomFromCorner() {
		return radioNavZoomFromCorner;
	}

	public JRadioButton getRadioMapDefault() {
		return radioMapDefault;
	}

	public JRadioButton getRadioMapWms() {
		return radioMapWms;
	}

	public JButton getBtnSave() {
		return btnSave;
	}

	private void btnMoveUpActionPerformed(ActionEvent e) {
		callback.onMoveLayerUp(treeWMS);
	}

	private void btnMoveDownActionPerformed(ActionEvent e) {
		callback.onMoveLayerDown(treeWMS);
	}

	public JButton getBtnMoveUp() {
		return btnMoveUp;
	}

	public JButton getBtnMoveDown() {
		return btnMoveDown;
	}

	private void txtMapWmsKeyReleased(KeyEvent e) {
		onValidate();
	}

	private void txtMailDisplaynameKeyReleased(KeyEvent e) {
		onValidate();
	}

	private void txtMailAddressKeyReleased(KeyEvent e) {
		onValidate();
	}

	private void txtMailServerKeyReleased(KeyEvent e) {
		onValidate();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("no.ums.pas.localization.lang");
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
		txtMapWmsPassword = new JPasswordField();
		comboMapWmsImg = new JComboBox();
		scrollWMS = new JScrollPane();
		treeWMS = new WmsLayerTree();
		btnMoveUp = new JButton();
		btnMoveDown = new JButton();
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
		setTitle(bundle.getString("mainmenu_settings"));
		setResizable(false);
		Container contentPane = getContentPane();

		//======== userinfo ========
		{
			userinfo.setBorder(new TitledBorder("Userinfo"));

			//---- lblUser ----
			lblUser.setText(bundle.getString("logon_userid"));

			//---- lblCompany ----
			lblCompany.setText(bundle.getString("logon_company"));

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
			chkAutoStartParm.setText(bundle.getString("mainmenu_parm"));

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
			radioMapDefault.setText(bundle.getString("main_pas_settings_mapsite_default"));
			radioMapDefault.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					radioMapDefaultActionPerformed(e);
				}
			});

			//---- radioMapWms ----
			radioMapWms.setText(bundle.getString("main_pas_settings_mapsite_wms"));
			radioMapWms.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					radioMapWmsActionPerformed(e);
				}
			});

			//---- txtMapWms ----
			txtMapWms.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					txtMapWmsKeyReleased(e);
				}
			});

			//---- btnMapWmsOpen ----
			btnMapWmsOpen.setText(bundle.getString("common_open"));
			btnMapWmsOpen.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnMapWmsOpenActionPerformed(e);
				}
			});

			//---- lblMapWmsUser ----
			lblMapWmsUser.setText(bundle.getString("main_pas_settings_mapsite_wms_username"));

			//---- lblMapWmsPassword ----
			lblMapWmsPassword.setText(bundle.getString("main_pas_settings_mapsite_wms_password"));

			//======== scrollWMS ========
			{

				//---- treeWMS ----
				treeWMS.addTreeSelectionListener(new TreeSelectionListener() {
					@Override
					public void valueChanged(TreeSelectionEvent e) {
						treeWMSValueChanged(e);
					}
				});
				scrollWMS.setViewportView(treeWMS);
			}

			//---- btnMoveUp ----
			btnMoveUp.setText("text");
			btnMoveUp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnMoveUpActionPerformed(e);
				}
			});

			//---- btnMoveDown ----
			btnMoveDown.setText("text");
			btnMoveDown.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnMoveDownActionPerformed(e);
				}
			});

			GroupLayout mapsettingsLayout = new GroupLayout(mapsettings);
			mapsettings.setLayout(mapsettingsLayout);
			mapsettingsLayout.setHorizontalGroup(
				mapsettingsLayout.createParallelGroup()
					.addGroup(GroupLayout.Alignment.TRAILING, mapsettingsLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addGroup(GroupLayout.Alignment.LEADING, mapsettingsLayout.createSequentialGroup()
								.addComponent(txtMapWms, GroupLayout.PREFERRED_SIZE, 409, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(btnMapWmsOpen, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
							.addGroup(GroupLayout.Alignment.LEADING, mapsettingsLayout.createSequentialGroup()
								.addComponent(radioMapDefault, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(radioMapWms, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
							.addGroup(GroupLayout.Alignment.LEADING, mapsettingsLayout.createSequentialGroup()
								.addGroup(mapsettingsLayout.createParallelGroup()
									.addComponent(lblMapWmsUser, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
									.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addComponent(comboMapWmsImg, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblMapWmsPassword, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
									.addComponent(txtMapWmsPassword)
									.addComponent(txtMapWmsUser, GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 256, Short.MAX_VALUE))
							.addGroup(GroupLayout.Alignment.LEADING, mapsettingsLayout.createSequentialGroup()
								.addComponent(scrollWMS, GroupLayout.PREFERRED_SIZE, 469, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(mapsettingsLayout.createParallelGroup()
									.addComponent(btnMoveDown, 0, 0, Short.MAX_VALUE)
									.addComponent(btnMoveUp, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))))
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
						.addGroup(mapsettingsLayout.createParallelGroup()
							.addGroup(mapsettingsLayout.createSequentialGroup()
								.addComponent(btnMoveUp)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
								.addComponent(btnMoveDown))
							.addComponent(scrollWMS, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
						.addContainerGap())
			);
		}

		//======== navigation ========
		{
			navigation.setBorder(new TitledBorder("Navigation"));

			//---- radioNavPanByClick ----
			radioNavPanByClick.setText(bundle.getString("main_pas_settings_pan_by_click"));

			//---- radioNavPanByDrag ----
			radioNavPanByDrag.setText(bundle.getString("main_pas_settings_pan_by_drag"));

			//---- radioNavZoomFromCenter ----
			radioNavZoomFromCenter.setText(bundle.getString("main_pas_settings_zoom_from_center"));
			radioNavZoomFromCenter.setActionCommand(bundle.getString("main_pas_settings_zoom_from_center"));

			//---- radioNavZoomFromCorner ----
			radioNavZoomFromCorner.setText(bundle.getString("main_pas_settings_zoom_from_corner"));

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
			lblMailDisplayname.setText(bundle.getString("main_pas_settings_email_displayname"));

			//---- lblMailAddress ----
			lblMailAddress.setText(bundle.getString("main_pas_settings_email_address"));

			//---- lblMailServer ----
			lblMailServer.setText(bundle.getString("main_pas_settings_email_server"));

			//---- txtMailDisplayname ----
			txtMailDisplayname.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					txtMailDisplaynameKeyReleased(e);
				}
			});

			//---- txtMailAddress ----
			txtMailAddress.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					txtMailAddressKeyReleased(e);
				}
			});

			//---- txtMailServer ----
			txtMailServer.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					txtMailServerKeyReleased(e);
				}
			});

			GroupLayout panel4Layout = new GroupLayout(panel4);
			panel4.setLayout(panel4Layout);
			panel4Layout.setHorizontalGroup(
				panel4Layout.createParallelGroup()
					.addGroup(panel4Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(panel4Layout.createParallelGroup()
							.addComponent(lblMailServer, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
							.addComponent(lblMailAddress, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
							.addComponent(lblMailDisplayname, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE))
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
						.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(txtMailDisplayname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblMailDisplayname))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(txtMailAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblMailAddress))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(txtMailServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblMailServer))
						.addContainerGap(8, Short.MAX_VALUE))
			);
		}

		//======== panel1 ========
		{
			panel1.setBorder(new TitledBorder("LBA"));

			//---- lblLbaUpdate ----
			lblLbaUpdate.setText(bundle.getString("main_pas_settings_auto_lba_update"));

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
						.addComponent(lblLbaUpdate, GroupLayout.PREFERRED_SIZE, 293, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(comboLbaUpdate, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(162, Short.MAX_VALUE))
			);
			panel1Layout.setVerticalGroup(
				panel1Layout.createParallelGroup()
					.addGroup(panel1Layout.createSequentialGroup()
						.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(lblLbaUpdate)
							.addComponent(comboLbaUpdate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
		}

		//---- btnCancel ----
		btnCancel.setText(bundle.getString("common_cancel"));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCancelActionPerformed(e);
			}
		});

		//---- btnSave ----
		btnSave.setText(bundle.getString("common_save"));
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnSaveActionPerformed(e);
			}
		});

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(mapsettings, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
						.addComponent(userinfo, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel4, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(autostartup, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
						.addComponent(navigation, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		contentPaneLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCancel, btnSave});
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(userinfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(autostartup, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(mapsettings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(navigation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(panel4, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(btnSave)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		pack();
		setLocationRelativeTo(getOwner());

		//---- btnGroupMapSite ----
		ButtonGroup btnGroupMapSite = new ButtonGroup();
		btnGroupMapSite.add(radioMapDefault);
		btnGroupMapSite.add(radioMapWms);

		//---- btnGroupPan ----
		ButtonGroup btnGroupPan = new ButtonGroup();
		btnGroupPan.add(radioNavPanByClick);
		btnGroupPan.add(radioNavPanByDrag);

		//---- btnGroupZoom ----
		ButtonGroup btnGroupZoom = new ButtonGroup();
		btnGroupZoom.add(radioNavZoomFromCenter);
		btnGroupZoom.add(radioNavZoomFromCorner);

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
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("autoStartParm"),
			chkAutoStartParm, BeanProperty.create("selected")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("mapSiteDefault"),
			radioMapDefault, BeanProperty.create("selected")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("mapSiteWms"),
			radioMapWms, BeanProperty.create("selected")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("wmsUrl"),
			txtMapWms, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("wmsUsername"),
			txtMapWmsUser, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("wmsPassword"),
			txtMapWmsPassword, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("panByClick"),
			radioNavPanByClick, BeanProperty.create("selected")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("panByDrag"),
			radioNavPanByDrag, BeanProperty.create("selected")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("zoomFromCenter"),
			radioNavZoomFromCenter, BeanProperty.create("selected")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("zoomFromCorner"),
			radioNavZoomFromCorner, BeanProperty.create("selected")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("emailDisplayName"),
			txtMailDisplayname, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("emailAddress"),
			txtMailAddress, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("emailServer"),
			txtMailServer, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			settingsModel1, BeanProperty.create("wmsImageFormat"),
			comboMapWmsImg, BeanProperty.create("selectedItem")));
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
	private JPasswordField txtMapWmsPassword;
	private JComboBox comboMapWmsImg;
	private JScrollPane scrollWMS;
	private WmsLayerTree treeWMS;
	private JButton btnMoveUp;
	private JButton btnMoveDown;
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
