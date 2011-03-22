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
import javax.swing.text.JTextComponent;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.*;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.*;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.pas.send.SendOptionToolbar.ADRGROUPS;

import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.converters.*;
import org.jdesktop.beansbinding.validation.VisibleValidation;

/**
 * @author User #2
 */
public class Settings extends JDialog {

	/**
	 * 
	 * @author Modda
	 * Interface for communicating with SettingsCtrl
	 */
	public interface ISettingsUpdate
	{
		public void onOpenWmsSite(final WmsLayerTree tree, final String wmsUrl, final String wmsUser, final String wmsPassword, final JComboBox imageformats);
		public void onWmsLayerSelect(final WmsLayerTree tree, final TreeSelectionEvent e);
		public void onCancel();
		public void onOk(final WmsLayerTree tree, final SettingsModel model);
		public void onMapWmsSelected(boolean b);
		public void onMoveLayerUp(final WmsLayerTree tree);
		public void onMoveLayerDown(final WmsLayerTree tree);
	}

	final SendOptionToolbar sot = new SendOptionToolbar(null, null, 0);
	ISettingsUpdate callback;
	
	public Settings(Frame owner, ISettingsUpdate callback) {
		super(owner);
		this.callback = callback;
		initComponents();

	}
	
	protected void initValues()
	{
		comboLbaUpdate.addItem(new Integer(10));
		comboLbaUpdate.addItem(new Integer(20));
		comboLbaUpdate.addItem(new Integer(30));
		comboLbaUpdate.addItem(new Integer(40));
		comboLbaUpdate.addItem(new Integer(50));

		sot.setCallback(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if("act_set_addresstypes".equals(e.getActionCommand()))
				{
					System.out.println("Addresstypes="+sot.get_addresstypes());
					lblPrivateAdrTypes.setText(sot.gen_adrtypes_text(sot.get_addresstypes(), SendOptionToolbar.ADRGROUPS.PRIVATE));
					lblCompanyAdrtypes.setText(sot.gen_adrtypes_text(sot.get_addresstypes(), SendOptionToolbar.ADRGROUPS.COMPANY));
					updateAutoChannelButtons(sot.get_addresstypes());
				}
			}
		});
		sot.show_buttonsbyadrtype(PAS.get_pas().get_rightsmanagement().addresstypes(), 
				togglePrivateFixed, togglePrivateMobile, toggleCompanyFixed, toggleCompanyMobile, toggleLba);
		
		sot.add_adrtypemenus(togglePrivateFixed, sot.menu_fixedpriv, sot.group_fixedprivbtn, "");
		sot.add_adrtypemenus(togglePrivateMobile, sot.menu_smspriv, sot.group_smsprivbtn, "");
		sot.add_adrtypemenus(toggleCompanyFixed, sot.menu_fixedcomp, sot.group_fixedcompbtn, "");
		sot.add_adrtypemenus(toggleCompanyMobile, sot.menu_smscomp, sot.group_smscompbtn, "");
		int n_init_addresstypes = 134218044;
		sot.set_addresstypes(n_init_addresstypes);		
		sot.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
		updateAutoChannelButtons(n_init_addresstypes);
		if((n_init_addresstypes & SendController.SENDTO_CELL_BROADCAST_TEXT) > 0)
		{
			toggleLba.doClick();
		}
		if((n_init_addresstypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0)
		{
			toggleBlocklist.doClick();
		}
	}
	
	
	protected void updateAutoChannelButtons(int adrtypes)
	{
	}


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
		if(onValidate(null))
			callback.onOk(treeWMS, settingsModel1);
	}
	private boolean onValidate(JComponent c)
	{
		boolean b_error = false;
		boolean tmpValidUrl = false;
		if((c==null || c.equals(txtMailAddress)) && settingsModel1.getEmailAddress().length()>0)
			b_error ^= !VisibleValidation.validateEmail(settingsModel1.getEmailAddress(), txtMailAddress);
		if((c==null || c.equals(txtMailServer)) && settingsModel1.getEmailServer().length()>0)
			b_error ^= !VisibleValidation.validateHost(settingsModel1.getEmailServer(), txtMailServer);
		if(((c==null || c.equals(txtMapWms))) && settingsModel1.getMapSiteWms())
		{
			b_error ^= tmpValidUrl = !VisibleValidation.validateUrl(settingsModel1.getWmsUrl(), txtMapWms);
		}
		else if((c==null || c.equals(txtMapWms)))
		{
			VisibleValidation.forceValid(txtMapWms);
		}
		getBtnMapWmsOpen().setEnabled(!tmpValidUrl);
		getBtnSave().setEnabled(!b_error);
		return !b_error;
	}
	
	public void onWmsLayerlistLoaded(boolean bOk, Exception e)
	{
		if(!bOk)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("<html>");
			sb.append("<br>");
			sb.append("<b>");
			sb.append(Localization.l("main_pas_settings_mapsite_wms_test_failed"));
			sb.append("</b>");
			sb.append("<br><br>");
			sb.append(e.getMessage());
			sb.append("<br><br>");
			sb.append("</html>");
			VisibleValidation.forceInvalid(getTxtMapWms(), sb.toString());
		}
		else
			VisibleValidation.forceValid(getTxtMapWms());
	}

	private void radioMapDefaultActionPerformed(ActionEvent e) {
		callback.onMapWmsSelected(false);
		onValidate(getTxtMapWms());
	}

	private void radioMapWmsActionPerformed(ActionEvent e) {
		callback.onMapWmsSelected(true);
		onValidate(getTxtMapWms());
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
		onValidate(getTxtMapWms());
	}

	private void txtMailDisplaynameKeyReleased(KeyEvent e) {
		onValidate(txtMailDisplayname);
	}

	private void txtMailAddressKeyReleased(KeyEvent e) {
		onValidate(txtMailAddress);
	}

	private void txtMailServerKeyReleased(KeyEvent e) {
		onValidate(txtMailServer);
	}

	private void toggleLbaActionPerformed(ActionEvent e) {
		if(toggleLba.isSelected()) 
			sot.add_addresstypes(SendController.SENDTO_CELL_BROADCAST_TEXT);
		else
			sot.remove_addresstypes(SendController.SENDTO_CELL_BROADCAST_TEXT);
		lblLbaText.setText(sot.gen_adrtypes_text(sot.get_addresstypes(), ADRGROUPS.LBA));
		sot.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
	}

	private void toggleBlocklistActionPerformed(ActionEvent e) {
		if(toggleBlocklist.isSelected())
			sot.add_addresstypes(SendController.SENDTO_USE_NOFAX_COMPANY);
		else
			sot.remove_addresstypes(SendController.SENDTO_USE_NOFAX_COMPANY);
		System.out.println(sot.gen_adrtypes_text(sot.get_addresstypes(), ADRGROUPS.NOFAX));
		sot.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("no.ums.pas.localization.lang");
		btnCancel = new JButton();
		btnSave = new JButton();
		tabbedPane1 = new JTabbedPane();
		userinfo = new JPanel();
		lblUser = new JLabel();
		lblCompany = new JLabel();
		txtUser = new JTextField();
		txtCompany = new JTextField();
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
		pnlDiverse = new JPanel();
		autostartup = new JPanel();
		chkAutoStartParm = new JCheckBox();
		panel1 = new JPanel();
		lblLbaUpdate = new JLabel();
		comboLbaUpdate = new JComboBox();
		panel2 = new JPanel();
		togglePrivateFixed = new ToggleAddresstype();
		togglePrivateMobile = new ToggleAddresstype();
		toggleCompanyFixed = new ToggleAddresstype();
		toggleCompanyMobile = new ToggleAddresstype();
		toggleLba = new ToggleAddresstype();
		toggleBlocklist = new ToggleAddresstype();
		lblPrivateAdrTypes = new JLabel();
		lblCompanyAdrtypes = new JLabel();
		lblLbaText = new JLabel();
		settingsModel1 = new SettingsModel();
		stringToInt1 = new StringToInt();

		//======== this ========
		setAlwaysOnTop(true);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setTitle(bundle.getString("mainmenu_settings"));
		Container contentPane = getContentPane();

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

		//======== tabbedPane1 ========
		{

			//======== userinfo ========
			{
				userinfo.setBorder(null);

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
							.addGroup(userinfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(lblCompany, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblUser, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addGroup(userinfoLayout.createParallelGroup()
								.addComponent(txtCompany, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
								.addComponent(txtUser, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE))
							.addGap(293, 293, 293))
				);
				userinfoLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {txtCompany, txtUser});
				userinfoLayout.setVerticalGroup(
					userinfoLayout.createParallelGroup()
						.addGroup(userinfoLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(userinfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblUser)
								.addComponent(txtUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(userinfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblCompany)
								.addComponent(txtCompany, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addContainerGap(336, Short.MAX_VALUE))
				);
			}
			tabbedPane1.addTab(bundle.getString("main_pas_settings_user_heading"), userinfo);


			//======== mapsettings ========
			{
				mapsettings.setBorder(null);

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
				btnMoveUp.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/arrow_up_32.png")));
				btnMoveUp.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						btnMoveUpActionPerformed(e);
					}
				});

				//---- btnMoveDown ----
				btnMoveDown.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/arrow_down_32.png")));
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
									.addComponent(btnMapWmsOpen, GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
								.addGroup(GroupLayout.Alignment.LEADING, mapsettingsLayout.createSequentialGroup()
									.addComponent(radioMapDefault, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(radioMapWms, GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
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
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 287, Short.MAX_VALUE))
								.addGroup(GroupLayout.Alignment.LEADING, mapsettingsLayout.createSequentialGroup()
									.addComponent(scrollWMS, GroupLayout.PREFERRED_SIZE, 460, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(mapsettingsLayout.createParallelGroup()
										.addComponent(btnMoveDown, GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
										.addComponent(btnMoveUp, GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE))))
							.addContainerGap())
				);
				mapsettingsLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {lblMapWmsPassword, lblMapWmsUser});
				mapsettingsLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnMoveDown, btnMoveUp});
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
									.addComponent(btnMoveUp, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 169, Short.MAX_VALUE)
									.addComponent(btnMoveDown, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
								.addComponent(scrollWMS, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
							.addContainerGap())
				);
				mapsettingsLayout.linkSize(SwingConstants.VERTICAL, new Component[] {btnMoveDown, btnMoveUp});
			}
			tabbedPane1.addTab(bundle.getString("main_pas_settings_map_heading"), mapsettings);


			//======== navigation ========
			{
				navigation.setBorder(null);

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
							.addGroup(navigationLayout.createParallelGroup()
								.addComponent(radioNavPanByClick, GroupLayout.PREFERRED_SIZE, 248, GroupLayout.PREFERRED_SIZE)
								.addComponent(radioNavZoomFromCenter, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(navigationLayout.createParallelGroup()
								.addComponent(radioNavPanByDrag, GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
								.addComponent(radioNavZoomFromCorner, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE))
							.addContainerGap())
				);
				navigationLayout.setVerticalGroup(
					navigationLayout.createParallelGroup()
						.addGroup(navigationLayout.createSequentialGroup()
							.addGap(17, 17, 17)
							.addGroup(navigationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(radioNavPanByClick)
								.addComponent(radioNavPanByDrag))
							.addGap(29, 29, 29)
							.addGroup(navigationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(radioNavZoomFromCenter)
								.addComponent(radioNavZoomFromCorner))
							.addContainerGap(304, Short.MAX_VALUE))
				);
			}
			tabbedPane1.addTab(bundle.getString("main_pas_settings_navigation_heading"), navigation);


			//======== panel4 ========
			{
				panel4.setBorder(null);

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
								.addComponent(lblMailServer, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
								.addComponent(lblMailAddress, GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
								.addComponent(lblMailDisplayname, GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(txtMailDisplayname, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)
								.addGroup(panel4Layout.createParallelGroup()
									.addComponent(txtMailServer, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
									.addComponent(txtMailAddress)))
							.addGap(222, 222, 222))
				);
				panel4Layout.setVerticalGroup(
					panel4Layout.createParallelGroup()
						.addGroup(panel4Layout.createSequentialGroup()
							.addGap(26, 26, 26)
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
							.addContainerGap(294, Short.MAX_VALUE))
				);
			}
			tabbedPane1.addTab(bundle.getString("main_pas_settings_email_heading"), panel4);


			//======== pnlDiverse ========
			{

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
								.addContainerGap(393, Short.MAX_VALUE))
					);
					autostartupLayout.setVerticalGroup(
						autostartupLayout.createParallelGroup()
							.addGroup(autostartupLayout.createSequentialGroup()
								.addComponent(chkAutoStartParm)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
								.addContainerGap(158, Short.MAX_VALUE))
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

				//======== panel2 ========
				{
					panel2.setBorder(new TitledBorder(bundle.getString("main_pas_settings_misc_auto_channel_select")));

					//---- togglePrivateFixed ----
					togglePrivateFixed.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/phone_24.png")));
					togglePrivateFixed.setRolloverEnabled(false);

					//---- togglePrivateMobile ----
					togglePrivateMobile.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/mobile_24.png")));
					togglePrivateMobile.setRolloverEnabled(false);

					//---- toggleCompanyFixed ----
					toggleCompanyFixed.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/phone_24.png")));
					toggleCompanyFixed.setRolloverEnabled(false);

					//---- toggleCompanyMobile ----
					toggleCompanyMobile.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/mobile_24.png")));
					toggleCompanyMobile.setRolloverEnabled(false);

					//---- toggleLba ----
					toggleLba.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/lba_24.png")));
					toggleLba.setRolloverEnabled(false);
					toggleLba.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							toggleLbaActionPerformed(e);
						}
					});

					//---- toggleBlocklist ----
					toggleBlocklist.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/flag_red_24.gif")));
					toggleBlocklist.setRolloverEnabled(false);
					toggleBlocklist.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							toggleBlocklistActionPerformed(e);
						}
					});

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(
						panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
								.addGap(31, 31, 31)
								.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
									.addComponent(lblPrivateAdrTypes, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addGroup(GroupLayout.Alignment.LEADING, panel2Layout.createSequentialGroup()
										.addComponent(togglePrivateFixed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(togglePrivateMobile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addGap(18, 18, 18)
								.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
									.addGroup(panel2Layout.createSequentialGroup()
										.addComponent(toggleCompanyFixed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(toggleCompanyMobile, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE))
									.addComponent(lblCompanyAdrtypes, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGap(29, 29, 29)
								.addGroup(panel2Layout.createParallelGroup()
									.addGroup(panel2Layout.createSequentialGroup()
										.addComponent(toggleLba, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(24, 24, 24)
										.addComponent(toggleBlocklist, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addComponent(lblLbaText, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
								.addContainerGap())
					);
					panel2Layout.setVerticalGroup(
						panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
								.addGap(22, 22, 22)
								.addGroup(panel2Layout.createParallelGroup()
									.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addComponent(togglePrivateFixed, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGroup(GroupLayout.Alignment.LEADING, panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(togglePrivateMobile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(toggleCompanyMobile, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
											.addComponent(toggleCompanyFixed, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
									.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(toggleLba, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(toggleBlocklist, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel2Layout.createParallelGroup()
									.addComponent(lblLbaText, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
									.addComponent(lblCompanyAdrtypes, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
									.addComponent(lblPrivateAdrTypes, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
								.addContainerGap())
					);
				}

				GroupLayout pnlDiverseLayout = new GroupLayout(pnlDiverse);
				pnlDiverse.setLayout(pnlDiverseLayout);
				pnlDiverseLayout.setHorizontalGroup(
					pnlDiverseLayout.createParallelGroup()
						.addGroup(GroupLayout.Alignment.TRAILING, pnlDiverseLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(pnlDiverseLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(panel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(autostartup, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addContainerGap())
				);
				pnlDiverseLayout.setVerticalGroup(
					pnlDiverseLayout.createParallelGroup()
						.addGroup(pnlDiverseLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(autostartup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(101, Short.MAX_VALUE))
				);
			}
			tabbedPane1.addTab(bundle.getString("main_pas_settings_misc_heading"), pnlDiverse);

		}

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(tabbedPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		contentPaneLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCancel, btnSave});
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane1, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
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
	private JButton btnCancel;
	private JButton btnSave;
	private JTabbedPane tabbedPane1;
	private JPanel userinfo;
	private JLabel lblUser;
	private JLabel lblCompany;
	private JTextField txtUser;
	private JTextField txtCompany;
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
	private JPanel pnlDiverse;
	private JPanel autostartup;
	private JCheckBox chkAutoStartParm;
	private JPanel panel1;
	private JLabel lblLbaUpdate;
	private JComboBox comboLbaUpdate;
	private JPanel panel2;
	private ToggleAddresstype togglePrivateFixed;
	private ToggleAddresstype togglePrivateMobile;
	private ToggleAddresstype toggleCompanyFixed;
	private ToggleAddresstype toggleCompanyMobile;
	private ToggleAddresstype toggleLba;
	private ToggleAddresstype toggleBlocklist;
	private JLabel lblPrivateAdrTypes;
	private JLabel lblCompanyAdrtypes;
	private JLabel lblLbaText;
	public SettingsModel settingsModel1;
	private StringToInt stringToInt1;
	private BindingGroup bindingGroup;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
