/*
 * Created by JFormDesigner on Fri Mar 04 12:22:03 CET 2011
 */

package no.ums.pas.core.logon.view;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.ChannelType;
import no.ums.pas.core.logon.*;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.RecipientChannel;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.pas.send.SendOptionToolbar.ADRGROUPS;
import no.ums.pas.send.ToggleAddresstype;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.converters.StringToInt;
import org.jdesktop.beansbinding.validation.VisibleValidation;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @author User #2
 */
public class Settings extends JFrame implements ActionListener{

    private static final Log log = UmsLog.getLogger(Settings.class);
    
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
		public void onNewSendingAutoShape(AbstractButton value);
	}

	final SendOptionToolbar sot = new SendOptionToolbar(null, null, 0);
	ISettingsUpdate callback;
	
	private int addressTypes = 0;
    public int getAddressTypes() {
		return addressTypes;
	}
    public void setAddressTypes(int addressTypes) {
		this.addressTypes = addressTypes;
	}
    
	public Settings(Frame owner, ISettingsUpdate callback) {
		super();
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
		
		DeptCategory catg1 = new DeptCategory(0, Localization.l("main_pas_settings_dept_category_none"));
		DeptCategory catg2 = new DeptCategory(1, Localization.l("main_pas_settings_dept_category_alphabetically"));
		comboAutoGroupDepts.addItem(catg1);
		comboAutoGroupDepts.addItem(catg2);

		initRecipientChannel(ChannelType.PRIVATE, comboPrivateRecipientChannel);
		initRecipientChannel(ChannelType.COMPANY, comboCompanyRecipientChannel);
		
		chkLocationBased.addActionListener(this);
		chkAddressBased.addActionListener(this);
		comboPrivateRecipientChannel.addActionListener(this);
		comboCompanyRecipientChannel.addActionListener(this);
		chkResident.addActionListener(this);
		chkPropertyOwnerPrivate.addActionListener(this);
		chkPropertyOwnerVacation.addActionListener(this);
		chkLocationBased.setActionCommand("act_set_addresstypes_new");
		chkAddressBased.setActionCommand("act_set_addresstypes_new");
		comboPrivateRecipientChannel.setActionCommand("act_set_addresstypes_new");
		comboCompanyRecipientChannel.setActionCommand("act_set_addresstypes_new");
		chkResident.setActionCommand("act_set_addresstypes_new");
		chkPropertyOwnerPrivate.setActionCommand("act_set_addresstypes_new");
		chkPropertyOwnerVacation.setActionCommand("act_set_addresstypes_new");


		sot.setCallback(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if("act_set_addresstypes".equals(e.getActionCommand()))
				{
//					lblPrivateAdrTypes.setText(sot.gen_adrtypes_text(sot.get_addresstypes(), SendOptionToolbar.ADRGROUPS.PRIVATE));
//					lblCompanyAdrtypes.setText(sot.gen_adrtypes_text(sot.get_addresstypes(), SendOptionToolbar.ADRGROUPS.COMPANY));
					updateAutoChannelButtons(sot.get_addresstypes());
					settingsModel1.setNewSendingAutoChannel(sot.get_addresstypes());
				}
				else if("act_init_addresstypes".equals(e.getActionCommand()))
				{					
//					lblPrivateAdrTypes.setText(sot.gen_adrtypes_text(sot.get_addresstypes(), SendOptionToolbar.ADRGROUPS.PRIVATE));
//					lblCompanyAdrtypes.setText(sot.gen_adrtypes_text(sot.get_addresstypes(), SendOptionToolbar.ADRGROUPS.COMPANY));
					updateAutoChannelButtons(sot.get_addresstypes());
				}
			}
		});
		sot.add_adrtypemenus(togglePrivateFixed, new JPopupMenu(), sot.group_fixedprivbtn, Localization.l("main_sending_adr_sel_heading_private_fixed"));
		sot.add_adrtypemenus(togglePrivateMobile, new JPopupMenu(), sot.group_smsprivbtn, Localization.l("main_sending_adr_sel_heading_private_mobile"));
		sot.add_adrtypemenus(toggleCompanyFixed, new JPopupMenu(), sot.group_fixedcompbtn, Localization.l("main_sending_adr_sel_heading_company_fixed"));
		sot.add_adrtypemenus(toggleCompanyMobile, new JPopupMenu(), sot.group_smscompbtn, Localization.l("main_sending_adr_sel_heading_company_mobile"));
		sot.set_addresstypes((int)settingsModel1.getNewSendingAutoChannel());
		sot.initSelections();
		sot.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_init_addresstypes"));
		updateAutoChannelButtons(sot.get_addresstypes());
		if((sot.get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) > 0)
		{
//			toggleLba.doClick();
		}
		if((sot.get_addresstypes() & SendController.SENDTO_USE_NOFAX_COMPANY) > 0)
		{
			toggleBlocklist.doClick();
		}
		if((sot.get_addresstypes() & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS) > 0)
		{
			toggleVulnerable.doClick();
		}
		if((sot.get_addresstypes() & SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD) > 0)
		{
			toggleHeadOfHousehold.doClick();
		}
	}
	
	private ArrayList<RecipientChannel> getPrivateRecipientList()
	{
		ArrayList<RecipientChannel> recipientChannelList = new ArrayList<RecipientChannel>();
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice"), SendController.SENDTO_FIXED_PRIVATE));//voiceFixedChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_mobile_voice"), SendController.SENDTO_MOBILE_PRIVATE));//voiceMobileChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_option_sms"), SendController.SENDTO_SMS_PRIVATE));//smsChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice_and_sms"), SendController.SENDTO_FIXED_PRIVATE | SendController.SENDTO_SMS_PRIVATE));//voiceFixedAndSMSChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_and_mobile_voice"), SendController.SENDTO_FIXED_PRIVATE | SendController.SENDTO_MOBILE_PRIVATE));//voiceFixedAndVoiceMobileChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_and_mobile"), SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE));//voiceFixedAndVoiceMobilePriorityChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_mobile_voice_and_fixed"), SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED));//voiceMobileAndVoiceFixedPriorityChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice_or_sms"), SendController.SENDTO_FIXED_PRIVATE_ALT_SMS));//voiceFixedOrSMSChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_sms_or_fixed_voice"), SendController.SENDTO_SMS_PRIVATE_ALT_FIXED));//smsOrvoiceFixedChannel
		return recipientChannelList;
	}
	private ArrayList<RecipientChannel> getCompanyRecipientList()
	{
		ArrayList<RecipientChannel> recipientChannelList = new ArrayList<RecipientChannel>();
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice"), SendController.SENDTO_FIXED_COMPANY));//voiceFixedChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_mobile_voice"), SendController.SENDTO_MOBILE_COMPANY));//voiceMobileChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_option_sms"), SendController.SENDTO_SMS_COMPANY));//smsChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice_and_sms"), SendController.SENDTO_FIXED_COMPANY | SendController.SENDTO_SMS_COMPANY));//voiceFixedAndSMSChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_and_mobile_voice"), SendController.SENDTO_FIXED_COMPANY | SendController.SENDTO_MOBILE_COMPANY));//voiceFixedAndVoiceMobileChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_and_mobile"), SendController.SENDTO_FIXED_COMPANY_AND_MOBILE));//voiceFixedAndVoiceMobilePriorityChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_mobile_voice_and_fixed"), SendController.SENDTO_MOBILE_COMPANY_AND_FIXED));//voiceMobileAndVoiceFixedPriorityChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_fixed_voice_or_sms"), SendController.SENDTO_FIXED_COMPANY_ALT_SMS));//voiceFixedOrSMSChannel
		recipientChannelList.add(new RecipientChannel(Localization.l("main_sending_adr_sel_private_sms_or_fixed_voice"), SendController.SENDTO_SMS_COMPANY_ALT_FIXED));//smsOrvoiceFixedChannel
		return recipientChannelList;
	}
	private void initRecipientChannel(ChannelType channelType,JComboBox comboRecipientChannel)
	{
		ArrayList<RecipientChannel> recipientChannelList = null;	
		
		switch (channelType) {
		case PRIVATE:
			recipientChannelList = getPrivateRecipientList();
			break;
		case COMPANY:
			recipientChannelList = getCompanyRecipientList();
			break;
		}
		
		comboRecipientChannel.addItem(new RecipientChannel(Localization.l("main_sending_adr_sel_channels"),0));
		for(RecipientChannel channel : recipientChannelList)
		{
			comboRecipientChannel.addItem(channel);
		}
	}
	
	public void populateDeptCategory(int deptCategory)
	{
	  	for(int i=0;i<getComboAutoGroupDepts().getItemCount();i++)
	   	{
	   		int curentDeptCatgValue = ((DeptCategory)getComboAutoGroupDepts().getItemAt(i)).getValue();
	   		if(curentDeptCatgValue == deptCategory)
	   			getComboAutoGroupDepts().setSelectedIndex(i);
	   	}
	}
	
	public void populateABASPanelData(long types)
	{
		if (types==0)
		{
			chkLocationBased.setText(gen_adrtypes_text((int)types,ADRGROUPS.LBA));
			chkAddressBased.setText(gen_adrtypes_text((int)types,ADRGROUPS.ABAS));
			changeVisibilityOfABASPanel(chkAddressBased.isSelected());//act_set_addresstypes_new
			actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes_new"));
			return;
		}
		
		if((SendController.SENDTO_USE_ABAS_RECIPIENTS & types)==SendController.SENDTO_USE_ABAS_RECIPIENTS)
			chkAddressBased.setSelected(true);
		if((SendController.SENDTO_USE_NOFAX_COMPANY & types)==SendController.SENDTO_USE_NOFAX_COMPANY)
			toggleBlocklist.setSelected(true);
		if((SendController.SENDTO_CELL_BROADCAST_TEXT & types)==SendController.SENDTO_CELL_BROADCAST_TEXT)
			chkLocationBased.setSelected(true);
		if((SendController.SENDTO_ONLY_VULNERABLE_CITIZENS & types)== SendController.SENDTO_ONLY_VULNERABLE_CITIZENS)
			toggleVulnerable.setSelected(true);
		if((SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD & types) == SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD)
			toggleHeadOfHousehold.setSelected(true);
		
		chkAddressBased.setText(gen_adrtypes_text((int)types, ADRGROUPS.ABAS));
		chkLocationBased.setText(gen_adrtypes_text((int)types, ADRGROUPS.LBA));

		changeVisibilityOfABASPanel(chkAddressBased.isSelected());
		
		for(int i=1;i<comboPrivateRecipientChannel.getItemCount();i++)
		{
			int value = ((RecipientChannel)comboPrivateRecipientChannel.getItemAt(i)).getValue();
			if((value & types)==value)
				comboPrivateRecipientChannel.setSelectedIndex(i);
		}
		for(int i=1;i<comboCompanyRecipientChannel.getItemCount();i++)
		{
			int value = ((RecipientChannel)comboCompanyRecipientChannel.getItemAt(i)).getValue();
			if((value & types)==value)
				comboCompanyRecipientChannel.setSelectedIndex(i);
		}
		if((SendController.RECIPTYPE_PRIVATE_RESIDENT & types)==SendController.RECIPTYPE_PRIVATE_RESIDENT)
			chkResident.setSelected(true);
		if((SendController.RECIPTYPE_PRIVATE_OWNER_HOME & types)==SendController.RECIPTYPE_PRIVATE_OWNER_HOME)
			chkPropertyOwnerPrivate.setSelected(true);
		if((SendController.RECIPTYPE_PRIVATE_OWNER_VACATION & types)==SendController.RECIPTYPE_PRIVATE_OWNER_VACATION)
			chkPropertyOwnerVacation.setSelected(true);
		
		actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes_new"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if("act_set_addresstypes_new".equals(e.getActionCommand()))
		{
			setAddressTypes(saveDefaultAddressTypeSettings());
			if(chkLocationBased.equals(e.getSource()))
				chkLocationBased.setText(gen_adrtypes_text(getAddressTypes(), ADRGROUPS.LBA));
			
			if(chkAddressBased.equals(e.getSource()))
				changeVisibilityOfABASPanel(chkAddressBased.isSelected());
			
			boolean bEnableAbas = isAbasSelected();
//			System.out.println("inside Settings bEnableAbas="+bEnableAbas);
			toggleVulnerable.setEnabled(bEnableAbas);
			toggleBlocklist.setEnabled(bEnableAbas);
			toggleHeadOfHousehold.setEnabled(bEnableAbas);
			
			if(!toggleBlocklist.isEnabled())
			{
				toggleBlocklist.setSelected(false);
				setAddressTypes(saveDefaultAddressTypeSettings());
				chkAddressBased.setText(gen_adrtypes_text(getAddressTypes(), ADRGROUPS.ABAS));
			}
		}
	}
	
	private boolean isAbasSelected()
	{
		boolean bEnableAbas = chkAddressBased.isSelected() & ((RecipientChannel)comboPrivateRecipientChannel.getSelectedItem()).getValue() > 0 | 
				((RecipientChannel)comboCompanyRecipientChannel.getSelectedItem()).getValue() > 0 |
				chkResident.isSelected() | chkPropertyOwnerPrivate.isSelected() | chkPropertyOwnerVacation.isSelected();
		return bEnableAbas;		
	}
	
	private int saveDefaultAddressTypeSettings()
	{
		int TYPES = 0;

		if(chkLocationBased.isSelected())
			TYPES |= SendController.SENDTO_CELL_BROADCAST_TEXT;
		if(chkAddressBased.isSelected())
			TYPES |= SendController.SENDTO_USE_ABAS_RECIPIENTS;
		if(toggleBlocklist.isSelected())
			TYPES |= SendController.SENDTO_USE_NOFAX_COMPANY;
		if(toggleVulnerable.isSelected())
			TYPES |= SendController.SENDTO_ONLY_VULNERABLE_CITIZENS;
		if(toggleHeadOfHousehold.isSelected())
			TYPES |= SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD;
		
		TYPES |= ((RecipientChannel)comboPrivateRecipientChannel.getSelectedItem()).getValue();
		TYPES |= ((RecipientChannel)comboCompanyRecipientChannel.getSelectedItem()).getValue();
		
		if(chkResident.isSelected())
			TYPES |= SendController.RECIPTYPE_PRIVATE_RESIDENT;
		if(chkPropertyOwnerPrivate.isSelected())
			TYPES |= SendController.RECIPTYPE_PRIVATE_OWNER_HOME;
		if(chkPropertyOwnerVacation.isSelected())
			TYPES |= SendController.RECIPTYPE_PRIVATE_OWNER_VACATION;
		
		settingsModel1.setAddressTypes(TYPES);
		return TYPES;
	}
	private void changeVisibilityOfABASPanel(boolean b)
	{
//		System.out.println("Settings changing VisibilityOfABASPanel to " + b);
		comboPrivateRecipientChannel.setEnabled(b);
		chkResident.setEnabled(b);
		chkPropertyOwnerPrivate.setEnabled(b);
		chkPropertyOwnerVacation.setEnabled(b);
		comboCompanyRecipientChannel.setEnabled(b);
		lblSelectPrivateRecipients.setEnabled(b);
		lblSelectCompanyRecipients.setEnabled(b);
		recipientTab.setVisible(b);
	}
	public String gen_adrtypes_text(int n_adrtypes, ADRGROUPS group)
	{
		String ret = "<html>";
		/*if(n_adrtypes<=0)
			ret += "<font color='red'>- No addresstypes selected</br></font>";
		else*/
		{
			String temp = "";
			String sz_font = "<font size='1'>";

			if(group==ADRGROUPS.ABAS)
			{
				ret += "<font size='3'><b>" + Localization.l("main_sending_adr_option_address_based") + "</b></font><br>";
				
				if((n_adrtypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0) {
                    temp += sz_font + "- " + Localization.l("main_sending_adr_option_using_blocklist");
                }
				ret += temp;
			}
			if(group==ADRGROUPS.LBA)
			{
				if((n_adrtypes & SendController.SENDTO_CELL_BROADCAST_TEXT) > 0) {
                    temp += sz_font + "- " + Localization.l("main_sending_adr_option_location_based_sms");
                }
//				if(temp.length()>0) {
                    ret += "<font size='3'><b>" + Localization.l("main_sending_adr_option_location_based") + "</b></font><br>";
//                }
				ret += temp;
			}
			if(group==ADRGROUPS.NOFAX)
			{
				if((n_adrtypes & SendController.SENDTO_USE_NOFAX_COMPANY) > 0) {
                    temp += sz_font + "- " + Localization.l("main_sending_adr_option_using_blocklist");
                }
				ret += temp;
			}
			if(group==ADRGROUPS.VULNERABLE)
			{
				if((n_adrtypes & SendController.SENDTO_ONLY_VULNERABLE_CITIZENS) > 0) {
                    temp += sz_font + "- " + Localization.l("main_sending_adr_btn_vulnerable_citizens");
                }
				ret += temp;				
			}
			if(group==ADRGROUPS.HEAD_OF_HOUSEHOLD)
			{
				if((n_adrtypes & SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD) > 0) {
					temp += sz_font + "- " + Localization.l("main_sending_adr_btn_head_of_household");
				}
				ret += temp;
			}
		}
		ret += "</html>";
		return ret;
	}
	
	
	protected void updateAutoChannelButtons(int adrtypes)
	{
		togglePrivateFixed.setSelected(sot.adrGroupRepresented(sot.group_fixedprivbtn));
		togglePrivateMobile.setSelected(sot.adrGroupRepresented(sot.group_smsprivbtn));
		toggleCompanyFixed.setSelected(sot.adrGroupRepresented(sot.group_fixedcompbtn));
		toggleCompanyMobile.setSelected(sot.adrGroupRepresented(sot.group_smscompbtn));
		boolean bEnableAbas = togglePrivateFixed.isSelected() |
				togglePrivateMobile.isSelected() |
				toggleCompanyFixed.isSelected() |
				toggleCompanyMobile.isSelected();
		if(!bEnableAbas && toggleVulnerable.isSelected())
		{
			toggleVulnerable.doClick();
		}
		if(!bEnableAbas && toggleBlocklist.isSelected())
		{
			toggleBlocklist.doClick();
		}
		if(!bEnableAbas && toggleHeadOfHousehold.isSelected())
		{
			toggleHeadOfHousehold.doClick();
		}
		toggleVulnerable.setEnabled(bEnableAbas);
		toggleBlocklist.setEnabled(bEnableAbas);
		toggleHeadOfHousehold.setEnabled(bEnableAbas);
	}


	public Settings(Dialog owner) {
		super();
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
		saveDefaultAddressTypeSettings();
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

	public JComboBox getComboAutoGroupDepts() {
		return comboAutoGroupDepts;
	}


	private void toggleLbaActionPerformed(ActionEvent e) {
//		toggleLba.toggleSelection();
//		if(toggleLba.isSelected()) 
//			sot.add_addresstypes(SendController.SENDTO_CELL_BROADCAST_TEXT);
//		else
//			sot.remove_addresstypes(SendController.SENDTO_CELL_BROADCAST_TEXT);
//		lblLbaText.setText(sot.gen_adrtypes_text(sot.get_addresstypes(), ADRGROUPS.LBA));
		sot.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
	}

	private void toggleBlocklistActionPerformed(ActionEvent e) {
		toggleBlocklist.toggleSelection();
//		if(toggleBlocklist.isSelected())
//			sot.add_addresstypes(SendController.SENDTO_USE_NOFAX_COMPANY);
//		else
//			sot.remove_addresstypes(SendController.SENDTO_USE_NOFAX_COMPANY);
		
		setAddressTypes(saveDefaultAddressTypeSettings());
		chkAddressBased.setText(gen_adrtypes_text(getAddressTypes(), ADRGROUPS.ABAS));
		
//		log.debug(gen_adrtypes_text(sot.get_addresstypes(), ADRGROUPS.NOFAX));
//		sot.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
	}
	private void toggleVulnerableActionPerformed(ActionEvent e) {
		toggleVulnerable.toggleSelection();
//		if(toggleVulnerable.isSelected())
//			sot.add_addresstypes(SendController.SENDTO_ONLY_VULNERABLE_CITIZENS);
//		else
//			sot.remove_addresstypes(SendController.SENDTO_ONLY_VULNERABLE_CITIZENS);
//		log.debug(gen_adrtypes_text(sot.get_addresstypes(), ADRGROUPS.VULNERABLE));
//		sot.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
	}
	private void toggleHeadOfHouseholdActionPerformed(ActionEvent e) {
		toggleHeadOfHousehold.toggleSelection();
//		if(toggleHeadOfHousehold.isSelected())
//			sot.add_addresstypes(SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD);
//		else
//			sot.remove_addresstypes(SendController.SENDTO_ONLY_HEAD_OF_HOUSEHOLD);
//		log.debug(sot.gen_adrtypes_text(sot.get_addresstypes(), ADRGROUPS.HEAD_OF_HOUSEHOLD));
//		sot.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_set_addresstypes"));
	}

	private void toggleImportActionPerformed(ActionEvent e) {
		settingsModel1.setNewSendingAutoShape(SendOptionToolbar.BTN_OPEN_);
		callback.onNewSendingAutoShape((AbstractButton)e.getSource());
	}

	private void togglePolygonActionPerformed(ActionEvent e) {
		settingsModel1.setNewSendingAutoShape(SendOptionToolbar.BTN_SENDINGTYPE_POLYGON_);
		callback.onNewSendingAutoShape((AbstractButton)e.getSource());
	}

	private void toggleEllipseActionPerformed(ActionEvent e) {
		settingsModel1.setNewSendingAutoShape(SendOptionToolbar.BTN_SENDINGTYPE_ELLIPSE_);
		callback.onNewSendingAutoShape((AbstractButton)e.getSource());
	}

	private void toggleMunicipalActionPerformed(ActionEvent e) {
		settingsModel1.setNewSendingAutoShape(SendOptionToolbar.BTN_SENDINGTYPE_MUNICIPAL_);
		callback.onNewSendingAutoShape((AbstractButton)e.getSource());
	}

	public JToggleButton getTogglePolygon() {
		return togglePolygon;
	}

	public JToggleButton getToggleEllipse() {
		return toggleEllipse;
	}

	public JToggleButton getToggleImport() {
		return toggleImport;
	}

	public JCheckBox getChkAutoStartParm() {
		return chkAutoStartParm;
	}

	public JPanel getPnlLBA() {
		return pnlLBA;
	}

	/*public ToggleAddresstype getToggleLba() {
		return toggleLba;
	}*/
	
	public JCheckBox getChkLocationBased()
	{
		return chkLocationBased;
	}

	public JCheckBox getChkPropertyOwnerPrivate() {
		return chkPropertyOwnerPrivate;
	}

	public JCheckBox getChkPropertyOwnerVacation() {
		return chkPropertyOwnerVacation;
	}

	public ToggleAddresstype getTogglePrivateFixed() {
		return togglePrivateFixed;
	}

	public ToggleAddresstype getTogglePrivateMobile() {
		return togglePrivateMobile;
	}

	public ToggleAddresstype getToggleCompanyFixed() {
		return toggleCompanyFixed;
	}

	public ToggleAddresstype getToggleCompanyMobile() {
		return toggleCompanyMobile;
	}

	public ToggleAddresstype getToggleBlocklist() {
		return toggleBlocklist;
	}

	public ToggleAddresstype getToggleVulnerable() {
		return toggleVulnerable;
	}

	public ToggleAddresstype getToggleHeadOfHousehold() {
		return toggleHeadOfHousehold;
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
		lblAutoGroupDepts = new JLabel();
		comboAutoGroupDepts = new JComboBox();
		pnlLBA = new JPanel();
		lblLbaUpdate = new JLabel();
		comboLbaUpdate = new JComboBox();
		panel2 = new JPanel();
		togglePrivateFixed = new ToggleAddresstype();
		togglePrivateMobile = new ToggleAddresstype();
		toggleCompanyFixed = new ToggleAddresstype();
		toggleCompanyMobile = new ToggleAddresstype();
		toggleBlocklist = new ToggleAddresstype();
		togglePolygon = new JToggleButton();
		toggleEllipse = new JToggleButton();
		toggleImport = new JToggleButton();
		toggleVulnerable = new ToggleAddresstype();
		toggleHeadOfHousehold = new ToggleAddresstype();
		recipientTab = new JTabbedPane();
		privateReceipientPanel = new JPanel();
		lblSelectPrivateRecipients = new JLabel();
		comboPrivateRecipientChannel = new JComboBox();
		chkResident = new JCheckBox();
		chkPropertyOwnerPrivate = new JCheckBox();
		chkPropertyOwnerVacation = new JCheckBox();
		companyReceipientPanel = new JPanel();
		lblSelectCompanyRecipients = new JLabel();
		comboCompanyRecipientChannel = new JComboBox();
		chkLocationBased = new JCheckBox();
		chkAddressBased = new JCheckBox();
		settingsModel1 = new SettingsModel();
		stringToInt1 = new StringToInt();
		integerToObject1 = new IntegerToObject();

		//======== this ========
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
								.addComponent(txtCompany, GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE)
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
							.addContainerGap(463, Short.MAX_VALUE))
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
									.addComponent(radioMapDefault, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(radioMapWms, GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
								.addGroup(GroupLayout.Alignment.LEADING, mapsettingsLayout.createSequentialGroup()
									.addComponent(scrollWMS, GroupLayout.PREFERRED_SIZE, 507, GroupLayout.PREFERRED_SIZE)
									.addGap(18, 18, 18)
									.addGroup(mapsettingsLayout.createParallelGroup()
										.addComponent(btnMoveDown, GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
										.addComponent(btnMoveUp, GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)))
								.addGroup(GroupLayout.Alignment.LEADING, mapsettingsLayout.createSequentialGroup()
									.addComponent(txtMapWms, GroupLayout.PREFERRED_SIZE, 463, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(btnMapWmsOpen, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))
								.addGroup(GroupLayout.Alignment.LEADING, mapsettingsLayout.createSequentialGroup()
									.addGroup(mapsettingsLayout.createParallelGroup()
										.addComponent(lblMapWmsUser, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
										.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
											.addComponent(comboMapWmsImg, GroupLayout.Alignment.LEADING)
											.addComponent(lblMapWmsPassword, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)))
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
										.addComponent(txtMapWmsPassword)
										.addComponent(txtMapWmsUser, GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))))
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
							.addGroup(mapsettingsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addGroup(mapsettingsLayout.createSequentialGroup()
									.addComponent(btnMoveUp, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 308, Short.MAX_VALUE)
									.addComponent(btnMoveDown, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
								.addComponent(scrollWMS, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
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
								.addComponent(radioNavZoomFromCenter, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(navigationLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(radioNavPanByDrag, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(radioNavZoomFromCorner, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))
							.addContainerGap(92, Short.MAX_VALUE))
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
							.addContainerGap(428, Short.MAX_VALUE))
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
								.addComponent(lblMailServer, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
								.addComponent(lblMailAddress, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
								.addComponent(lblMailDisplayname, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(txtMailDisplayname, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)
								.addGroup(panel4Layout.createParallelGroup()
									.addComponent(txtMailServer)
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
							.addContainerGap(422, Short.MAX_VALUE))
				);
			}
			tabbedPane1.addTab(bundle.getString("main_pas_settings_email_heading"), panel4);

			//======== pnlDiverse ========
			{
				pnlDiverse.setPreferredSize(new Dimension(634, 415));

				//======== autostartup ========
				{
					autostartup.setBorder(new TitledBorder("Auto startup"));

					//---- chkAutoStartParm ----
					chkAutoStartParm.setText(bundle.getString("mainmenu_parm"));

					//---- lblAutoGroupDepts ----
					lblAutoGroupDepts.setText(bundle.getString("main_pas_settings_auto_dept_category"));
					lblAutoGroupDepts.setPreferredSize(new Dimension(180, 23));

					//---- comboAutoGroupDepts ----
					comboAutoGroupDepts.setPreferredSize(new Dimension(130, 20));

					GroupLayout autostartupLayout = new GroupLayout(autostartup);
					autostartup.setLayout(autostartupLayout);
					autostartupLayout.setHorizontalGroup(
						autostartupLayout.createParallelGroup()
							.addGroup(autostartupLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(chkAutoStartParm, GroupLayout.PREFERRED_SIZE, 193, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(lblAutoGroupDepts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(comboAutoGroupDepts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(307, Short.MAX_VALUE))
					);
					autostartupLayout.setVerticalGroup(
						autostartupLayout.createParallelGroup()
							.addGroup(autostartupLayout.createSequentialGroup()
								.addGroup(autostartupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(chkAutoStartParm)
									.addComponent(lblAutoGroupDepts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(comboAutoGroupDepts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(0, 7, Short.MAX_VALUE))
					);
				}

				//======== pnlLBA ========
				{
					pnlLBA.setBorder(new TitledBorder("LBA"));

					//---- lblLbaUpdate ----
					lblLbaUpdate.setText(bundle.getString("main_pas_settings_auto_lba_update"));

					//---- comboLbaUpdate ----
					comboLbaUpdate.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							comboLbaUpdateItemStateChanged(e);
						}
					});

					GroupLayout pnlLBALayout = new GroupLayout(pnlLBA);
					pnlLBA.setLayout(pnlLBALayout);
					pnlLBALayout.setHorizontalGroup(
						pnlLBALayout.createParallelGroup()
							.addGroup(pnlLBALayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblLbaUpdate, GroupLayout.PREFERRED_SIZE, 293, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(comboLbaUpdate, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
					pnlLBALayout.setVerticalGroup(
						pnlLBALayout.createParallelGroup()
							.addGroup(pnlLBALayout.createSequentialGroup()
								.addGroup(pnlLBALayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
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
					togglePrivateFixed.setToolTipText(bundle.getString("main_sending_adr_btn_fixed_private_tooltip"));
					togglePrivateFixed.setVisible(false);

					//---- togglePrivateMobile ----
					togglePrivateMobile.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/mobile_24.png")));
					togglePrivateMobile.setRolloverEnabled(false);
					togglePrivateMobile.setToolTipText(bundle.getString("main_sending_adr_btn_mobile_private_tooltip"));
					togglePrivateMobile.setVisible(false);

					//---- toggleCompanyFixed ----
					toggleCompanyFixed.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/phone_24.png")));
					toggleCompanyFixed.setRolloverEnabled(false);
					toggleCompanyFixed.setToolTipText(bundle.getString("main_sending_adr_btn_fixed_company_tooltip"));
					toggleCompanyFixed.setVisible(false);

					//---- toggleCompanyMobile ----
					toggleCompanyMobile.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/mobile_24.png")));
					toggleCompanyMobile.setRolloverEnabled(false);
					toggleCompanyMobile.setToolTipText(bundle.getString("main_sending_adr_btn_mobile_company_tooltip"));
					toggleCompanyMobile.setVisible(false);

					//---- toggleBlocklist ----
					toggleBlocklist.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/flag_red_24.gif")));
					toggleBlocklist.setRolloverEnabled(false);
					toggleBlocklist.setToolTipText(bundle.getString("main_sending_adr_btn_company_blocklist_tooltip"));
					toggleBlocklist.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							toggleBlocklistActionPerformed(e);
						}
					});

					//---- togglePolygon ----
					togglePolygon.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/send_polygon_24.png")));
					togglePolygon.setSelected(true);
					togglePolygon.setToolTipText(bundle.getString("main_sending_type_polygon"));
					togglePolygon.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							togglePolygonActionPerformed(e);
						}
					});

					//---- toggleEllipse ----
					toggleEllipse.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/send_ellipse_24.png")));
					toggleEllipse.setToolTipText(bundle.getString("main_sending_type_ellipse"));
					toggleEllipse.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							toggleEllipseActionPerformed(e);
						}
					});

					//---- toggleImport ----
					toggleImport.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/folder2_24.png")));
					toggleImport.setToolTipText(bundle.getString("mainmenu_file_import"));
					toggleImport.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							toggleImportActionPerformed(e);
						}
					});

					//---- toggleVulnerable ----
					toggleVulnerable.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/bandaid_24.png")));
					toggleVulnerable.setToolTipText(bundle.getString("main_sending_adr_btn_vulnerable_citizens_tooltip"));
					toggleVulnerable.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							toggleVulnerableActionPerformed(e);
						}
					});

					//---- toggleHeadOfHousehold ----
					toggleHeadOfHousehold.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/HeadOfHousehold_24.png")));
					toggleHeadOfHousehold.setToolTipText(bundle.getString("toggleHeadOfHousehold.toolTipText"));
					toggleHeadOfHousehold.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							toggleHeadOfHouseholdActionPerformed(e);
						}
					});

					//======== recipientTab ========
					{

						//======== privateReceipientPanel ========
						{
							privateReceipientPanel.setLayout(new GridBagLayout());
							((GridBagLayout)privateReceipientPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
							((GridBagLayout)privateReceipientPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
							((GridBagLayout)privateReceipientPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
							((GridBagLayout)privateReceipientPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

							//---- lblSelectPrivateRecipients ----
							lblSelectPrivateRecipients.setText(bundle.getString("main_sending_adr_sel_recipients"));
							privateReceipientPanel.add(lblSelectPrivateRecipients, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.BOTH,
								new Insets(2, 20, 5, 5), 0, 0));
							privateReceipientPanel.add(comboPrivateRecipientChannel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.BOTH,
								new Insets(2, 0, 5, 0), 0, 0));

							//---- chkResident ----
							chkResident.setText(bundle.getString("main_sending_adr_sel_residents"));
							privateReceipientPanel.add(chkResident, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.BOTH,
								new Insets(0, 0, 5, 0), 0, 0));

							//---- chkPropertyOwnerPrivate ----
							chkPropertyOwnerPrivate.setText(bundle.getString("main_sending_adr_sel_property_owner_private"));
							privateReceipientPanel.add(chkPropertyOwnerPrivate, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.BOTH,
								new Insets(0, 0, 5, 0), 0, 0));

							//---- chkPropertyOwnerVacation ----
							chkPropertyOwnerVacation.setText(bundle.getString("main_sending_adr_sel_property_owner_vacation"));
							privateReceipientPanel.add(chkPropertyOwnerVacation, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.BOTH,
								new Insets(0, 0, 0, 0), 0, 0));
						}
						recipientTab.addTab(bundle.getString("common_adr_private"), privateReceipientPanel);

						//======== companyReceipientPanel ========
						{
							companyReceipientPanel.setLayout(new GridBagLayout());
							((GridBagLayout)companyReceipientPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
							((GridBagLayout)companyReceipientPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
							((GridBagLayout)companyReceipientPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
							((GridBagLayout)companyReceipientPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

							//---- lblSelectCompanyRecipients ----
							lblSelectCompanyRecipients.setText(bundle.getString("main_sending_adr_sel_recipients_company"));
							companyReceipientPanel.add(lblSelectCompanyRecipients, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.BOTH,
								new Insets(2, 20, 5, 5), 0, 0));
							companyReceipientPanel.add(comboCompanyRecipientChannel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.BOTH,
								new Insets(2, 0, 5, 0), 0, 0));
						}
						recipientTab.addTab(bundle.getString("common_adr_company"), companyReceipientPanel);
					}

					//---- chkLocationBased ----
					chkLocationBased.setText(bundle.getString("main_sending_adr_option_location_based"));
					chkLocationBased.setPreferredSize(new Dimension(175, 40));

					//---- chkAddressBased ----
					chkAddressBased.setText(bundle.getString("main_sending_adr_option_address_based"));
					chkAddressBased.setPreferredSize(new Dimension(175, 40));

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(
						panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
								.addGroup(panel2Layout.createParallelGroup()
									.addGroup(panel2Layout.createSequentialGroup()
										.addGroup(panel2Layout.createParallelGroup()
											.addGroup(panel2Layout.createSequentialGroup()
												.addGap(61, 61, 61)
												.addComponent(togglePolygon, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(toggleEllipse, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(toggleImport, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
												.addGap(0, 0, Short.MAX_VALUE))
											.addGroup(panel2Layout.createSequentialGroup()
												.addGap(19, 19, 19)
												.addGroup(panel2Layout.createParallelGroup()
													.addGroup(panel2Layout.createSequentialGroup()
														.addComponent(chkLocationBased, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(toggleBlocklist, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(toggleVulnerable, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(toggleHeadOfHousehold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
													.addGroup(panel2Layout.createSequentialGroup()
														.addComponent(chkAddressBased, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addGap(0, 0, Short.MAX_VALUE)))))
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGroup(panel2Layout.createSequentialGroup()
										.addContainerGap()
										.addComponent(recipientTab, GroupLayout.PREFERRED_SIZE, 593, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
								.addComponent(togglePrivateFixed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(togglePrivateMobile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(75, 75, 75)
								.addComponent(toggleCompanyFixed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(toggleCompanyMobile, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE))
					);
					panel2Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {toggleBlocklist, toggleCompanyFixed, toggleCompanyMobile, togglePrivateFixed, togglePrivateMobile, toggleVulnerable});
					panel2Layout.setVerticalGroup(
						panel2Layout.createParallelGroup()
							.addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(togglePolygon)
									.addComponent(toggleEllipse)
									.addComponent(toggleImport))
								.addGap(18, 18, 18)
								.addGroup(panel2Layout.createParallelGroup()
									.addComponent(chkLocationBased, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addComponent(toggleBlocklist, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(toggleVulnerable, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(toggleHeadOfHousehold, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addGroup(panel2Layout.createParallelGroup()
									.addGroup(panel2Layout.createSequentialGroup()
										.addGap(81, 81, 81)
										.addGroup(panel2Layout.createParallelGroup()
											.addComponent(toggleCompanyMobile, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
											.addComponent(toggleCompanyFixed, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
										.addGap(4, 4, 4)
										.addGroup(panel2Layout.createParallelGroup()
											.addComponent(togglePrivateFixed, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
											.addComponent(togglePrivateMobile, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
										.addGap(159, 159, 159))
									.addGroup(panel2Layout.createSequentialGroup()
										.addGap(3, 3, 3)
										.addComponent(chkAddressBased, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(recipientTab, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
										.addContainerGap())))
					);
				}

				GroupLayout pnlDiverseLayout = new GroupLayout(pnlDiverse);
				pnlDiverse.setLayout(pnlDiverseLayout);
				pnlDiverseLayout.setHorizontalGroup(
					pnlDiverseLayout.createParallelGroup()
						.addGroup(pnlDiverseLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(pnlDiverseLayout.createParallelGroup()
								.addComponent(pnlLBA, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(panel2, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(GroupLayout.Alignment.TRAILING, pnlDiverseLayout.createSequentialGroup()
									.addGap(0, 0, Short.MAX_VALUE)
									.addComponent(autostartup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
							.addContainerGap())
				);
				pnlDiverseLayout.setVerticalGroup(
					pnlDiverseLayout.createParallelGroup()
						.addGroup(pnlDiverseLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(autostartup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18, 18, 18)
							.addComponent(pnlLBA, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18, 18, 18)
							.addComponent(panel2, GroupLayout.PREFERRED_SIZE, 355, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
			}
			tabbedPane1.addTab(bundle.getString("main_pas_settings_misc_heading"), pnlDiverse);
		}

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addGap(0, 0, Short.MAX_VALUE)
							.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(tabbedPane1, GroupLayout.PREFERRED_SIZE, 637, GroupLayout.PREFERRED_SIZE)
							.addGap(0, 0, Short.MAX_VALUE)))
					.addContainerGap())
		);
		contentPaneLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCancel, btnSave});
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane1)
					.addGap(18, 18, 18)
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

		//---- btnGroupAutoShape ----
		ButtonGroup btnGroupAutoShape = new ButtonGroup();
		btnGroupAutoShape.add(togglePolygon);
		btnGroupAutoShape.add(toggleEllipse);
		btnGroupAutoShape.add(toggleImport);

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
		{
			Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
				settingsModel1, BeanProperty.create("deptCategory"),
				comboAutoGroupDepts, BeanProperty.create("selectedItem"));
			binding.setConverter(integerToObject1);
			bindingGroup.addBinding(binding);
		}
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
	private JLabel lblAutoGroupDepts;
	private JComboBox comboAutoGroupDepts;
	private JPanel pnlLBA;
	private JLabel lblLbaUpdate;
	private JComboBox comboLbaUpdate;
	private JPanel panel2;
	private ToggleAddresstype togglePrivateFixed;
	private ToggleAddresstype togglePrivateMobile;
	private ToggleAddresstype toggleCompanyFixed;
	private ToggleAddresstype toggleCompanyMobile;
	private ToggleAddresstype toggleBlocklist;
	private JToggleButton togglePolygon;
	private JToggleButton toggleEllipse;
	private JToggleButton toggleImport;
	private ToggleAddresstype toggleVulnerable;
	private ToggleAddresstype toggleHeadOfHousehold;
	private JTabbedPane recipientTab;
	private JPanel privateReceipientPanel;
	private JLabel lblSelectPrivateRecipients;
	private JComboBox comboPrivateRecipientChannel;
	private JCheckBox chkResident;
	private JCheckBox chkPropertyOwnerPrivate;
	private JCheckBox chkPropertyOwnerVacation;
	private JPanel companyReceipientPanel;
	private JLabel lblSelectCompanyRecipients;
	private JComboBox comboCompanyRecipientChannel;
	private JCheckBox chkLocationBased;
	private JCheckBox chkAddressBased;
	public SettingsModel settingsModel1;
	private StringToInt stringToInt1;
	private IntegerToObject integerToObject1;
	private BindingGroup bindingGroup;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
