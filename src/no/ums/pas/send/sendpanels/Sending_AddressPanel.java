package no.ums.pas.send.sendpanels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.Font;

import no.ums.pas.*;
import no.ums.pas.core.variables;
import no.ums.pas.core.defines.*;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.send.*;
import no.ums.pas.ums.tools.StdTextLabel;


/*
 * Show address info
*/
public abstract class Sending_AddressPanel extends DefaultPanel {
	private StdTextLabel m_lbl_adrcount_private = new StdTextLabel(PAS.l("main_sending_adr_type_private_fixed"), new Dimension(250, 16));
	private StdTextLabel m_lbl_adrcount_company = new StdTextLabel(PAS.l("main_sending_adr_type_company_fixed"), new Dimension(250, 16));
	private StdTextLabel m_lbl_adrcount_privatemobile  = new StdTextLabel(PAS.l("main_sending_adr_type_private_mobile"), new Dimension(250, 16));
	private StdTextLabel m_lbl_adrcount_companymobile  = new StdTextLabel(PAS.l("main_sending_adr_type_company_mobile"), new Dimension(250, 16));
	private StdTextLabel m_lbl_adrcount_private_sms =  new StdTextLabel(PAS.l("main_sending_adr_type_private_sms"), new Dimension(250, 16));
	private StdTextLabel m_lbl_adrcount_company_sms =  new StdTextLabel(PAS.l("main_sending_adr_type_company_sms"), new Dimension(250, 16));
	private StdTextLabel m_lbl_adrcount_lba_sms = new StdTextLabel(PAS.l("main_sending_adr_type_lba_sms"), new Dimension(250, 16));
	private StdTextLabel m_lbl_adrcount_privatenonumber  = new StdTextLabel(PAS.l("main_sending_adr_type_private_nonumber"), new Dimension(250, 16));
	private StdTextLabel m_lbl_adrcount_companynonumber  = new StdTextLabel(PAS.l("main_sending_adr_type_company_nonumber"), new Dimension(250, 16));
	private StdTextLabel m_lbl_adrcount_total   = new StdTextLabel(PAS.l("main_sending_adr_total_voice"), new Dimension(250,16));
	private StdTextLabel m_lbl_adrcount_total_sms = new StdTextLabel(PAS.l("main_sending_adr_total_sms"), new Dimension(250,16));
	private StdTextLabel m_txt_adrcount_private = new StdTextLabel("", new Dimension(50, 16));
	private StdTextLabel m_txt_adrcount_company = new StdTextLabel("", new Dimension(50, 16));
	private StdTextLabel m_txt_adrcount_privatemobile  = new StdTextLabel("", new Dimension(50, 16));
	private StdTextLabel m_txt_adrcount_companymobile  = new StdTextLabel("", new Dimension(50, 16));
	private StdTextLabel m_txt_adrcount_private_sms = new StdTextLabel("", new Dimension(50,16));
	private StdTextLabel m_txt_adrcount_company_sms = new StdTextLabel("", new Dimension(50,16));
	private StdTextLabel m_txt_adrcount_lba_sms = new StdTextLabel("N/A", new Dimension(50,16));
	private StdTextLabel m_txt_adrcount_privatenonumber  = new StdTextLabel("", new Dimension(50, 16));
	private StdTextLabel m_txt_adrcount_companynonumber  = new StdTextLabel("", new Dimension(50, 16));
	private StdTextLabel m_txt_adrcount_total   = new StdTextLabel("", new Dimension(50, 16));
	private StdTextLabel m_txt_adrcount_total_sms = new StdTextLabel("", new Dimension(50, 16));
	
	protected SendWindow parent = null;
	//private String m_sz_url = PAS.get_pas().get_sitename() + "PAS_getadrcount.asp";
	//protected String get_url() { return m_sz_url; }
	public SendWindow get_parent() { return parent; }
	public static final String ADRCOUNT_CALLBACK_ACTION_ = "act_addresscount";
	
	protected int GetAdrTypesForCount(int n)
	{
		int newadrtypes = 0;
		if(get_parent().hasVoicePrivate(n))
			newadrtypes|=SendController.SENDTO_FIXED_PRIVATE;
		if(get_parent().hasMobilePrivate(n))
			newadrtypes|=SendController.SENDTO_MOBILE_PRIVATE;
		if(get_parent().hasVoiceCompany(n))
			newadrtypes|=SendController.SENDTO_FIXED_COMPANY;
		if(get_parent().hasMobileCompany(n))
			newadrtypes|=SendController.SENDTO_MOBILE_COMPANY;
		return newadrtypes;
	}
	
	public Sending_AddressPanel(PAS pas, SendWindow parentwin) {
		super();
		parent = parentwin;
		Font fontSelected = new Font(null, Font.BOLD, 12);
		Font fontNotselected = new Font(null, Font.PLAIN, 10);
		int adrtypes = parentwin.get_sendobject().get_sendproperties().get_addresstypes();
		/*m_lbl_adrcount_private.setFont(get_parent().hasVoicePrivate(adrtypes) ? fontSelected : fontNotselected);
		m_lbl_adrcount_company.setFont(get_parent().hasVoiceCompany(adrtypes) ? fontSelected : fontNotselected);
		m_lbl_adrcount_privatemobile.setFont(get_parent().hasMobilePrivate(adrtypes) ? fontSelected : fontNotselected);
		m_lbl_adrcount_companymobile.setFont(get_parent().hasMobileCompany(adrtypes) ? fontSelected : fontNotselected);
		*/

		
		/*m_lbl_adrcount_private.setFont(SendController.HasType(adrtypes, SendController.SENDTO_FIXED_PRIVATE) ? fontSelected : fontNotselected);
		m_lbl_adrcount_company.setFont(SendController.HasType(adrtypes, SendController.SENDTO_FIXED_COMPANY) ? fontSelected : fontNotselected);
		m_lbl_adrcount_privatemobile.setFont(SendController.HasType(adrtypes, SendController.SENDTO_MOBILE_PRIVATE) ? fontSelected : fontNotselected);
		m_lbl_adrcount_companymobile.setFont(SendController.HasType(adrtypes, SendController.SENDTO_MOBILE_COMPANY) ? fontSelected : fontNotselected);
		m_lbl_adrcount_privatenonumber.setFont(SendController.HasType(adrtypes, SendController.SENDTO_NOPHONE_PRIVATE) ? fontSelected : fontNotselected);
		m_lbl_adrcount_companynonumber.setFont(SendController.HasType(adrtypes, SendController.SENDTO_NOPHONE_COMPANY) ? fontSelected : fontNotselected);
		m_lbl_adrcount_private_sms.setFont(SendController.HasType(adrtypes, SendController.SENDTO_SMS_PRIVATE) ? fontSelected : fontNotselected);
		m_lbl_adrcount_company_sms.setFont(SendController.HasType(adrtypes, SendController.SENDTO_SMS_COMPANY) ? fontSelected : fontNotselected);
		*/
		m_lbl_adrcount_total.setFont(fontSelected);
		m_txt_adrcount_total.setFont(fontSelected);
		m_lbl_adrcount_total_sms.setFont(fontSelected);
		m_txt_adrcount_total_sms.setFont(fontSelected);
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(get_parent().get_bgimg()!=null)
			g.drawImage(get_parent().get_bgimg(),0,0,getWidth(),getHeight(),this);
	}

	public void add_controls() {
		int n_width = 10;
		_add(m_lbl_adrcount_private, 0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_private, n_width/2, get_panel(), n_width/2, 1);
		_add(m_lbl_adrcount_privatemobile,  0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_privatemobile,  n_width/2, get_panel(), n_width/2, 1);
		_add(m_lbl_adrcount_private_sms, 0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_private_sms, n_width/2, get_panel(), n_width/2, 1);
		_add(m_lbl_adrcount_privatenonumber,  0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_privatenonumber,  n_width/2, get_panel(), n_width/2, 1);
		_add(m_lbl_adrcount_company, 0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_company, n_width/2, get_panel(), n_width/2, 1);
		_add(m_lbl_adrcount_companymobile,  0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_companymobile,  n_width/2, get_panel(), n_width/2, 1);
		_add(m_lbl_adrcount_company_sms, 0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_company_sms, n_width/2, get_panel(), n_width/2, 1);
		_add(m_lbl_adrcount_companynonumber,  0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_companynonumber,  n_width/2, get_panel(), n_width/2, 1);
		_add(m_lbl_adrcount_lba_sms, 0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_lba_sms, n_width/2, get_panel(), n_width/2, 1);
		_add(m_lbl_adrcount_total,	 0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_total,	 n_width/2, get_panel(), n_width/2, 1);
		_add(m_lbl_adrcount_total_sms,	 0, inc_panels(), n_width/2, 1);
		_add(m_txt_adrcount_total_sms,	 n_width/2, get_panel(), n_width/2, 1);
		init();

	}
	protected final void _add(Component c, int x, int y, int w, int h) {
		set_gridconst(x, y, w, h, GridBagConstraints.WEST);
		add(c, m_gridconst);
		c.setVisible(false);
	}
	
	public final void init() {
		this.doLayout();
		setVisible(true);
	}
	
	public void adrCountChanged(Object o)
	{
		
	}
	
	public void actionPerformed(ActionEvent e) {
		if(ADRCOUNT_CALLBACK_ACTION_.equals(e.getActionCommand())) {
			AddressCount c = (AddressCount)e.getSource();
			set_addresscount(c);
			get_parent().actionPerformed(e);
			switch(get_parent().get_sendobject().get_sendproperties().get_sendingtype()) {
				case SendProperties.SENDING_TYPE_POLYGON_:
					get_parent().get_sendobject().get_sendproperties().get_shapestruct().typecast_polygon().setCurrentViewMode(PolygonStruct.SHOW_POLYGON_SIMPLIFIED_PRPIXELS, 5, variables.NAVIGATION);
					PAS.get_pas().kickRepaint();
					break;
			}

		}
		else if("act_adrcount_changed".equals(e.getActionCommand())) {
			adrCountChanged(e.getSource());
		}
	}
	protected void set_addresscount(AddressCount c) {
		int n_addrtypes = get_parent().get_sendcontroller().get_activesending().get_toolbar().get_addresstypes();
		m_txt_adrcount_private.setText(new Integer(c.get_private()).toString());
		if((n_addrtypes & SendController.SENDTO_FIXED_PRIVATE) > 0 || (n_addrtypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) > 0 ||
				(n_addrtypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) > 0 || (n_addrtypes & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) > 0 ||
				(n_addrtypes & SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) > 0){
			m_lbl_adrcount_private.setVisible(true);
			m_txt_adrcount_private.setVisible(true);
			m_lbl_adrcount_total.setVisible(true);
			m_txt_adrcount_total.setVisible(true);
		}
		else {
			m_lbl_adrcount_private.setVisible(false);
			m_txt_adrcount_private.setVisible(false);
		}
		m_txt_adrcount_company.setText(new Integer(c.get_company()).toString());
		if((n_addrtypes & SendController.SENDTO_FIXED_COMPANY) > 0 || (n_addrtypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0 ||
				(n_addrtypes & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) > 0 || (n_addrtypes & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) > 0 ||
				(n_addrtypes & SendController.SENDTO_SMS_COMPANY_ALT_FIXED) > 0){
			m_lbl_adrcount_company.setVisible(true);
			m_txt_adrcount_company.setVisible(true);
			m_lbl_adrcount_total.setVisible(true);
			m_txt_adrcount_total.setVisible(true);
		}
		else {
			m_lbl_adrcount_company.setVisible(false);
			m_txt_adrcount_company.setVisible(false);
		}
		m_txt_adrcount_privatemobile.setText(new Integer(c.get_privatemobile()).toString());
		if((n_addrtypes & SendController.SENDTO_MOBILE_PRIVATE) > 0 || (n_addrtypes & SendController.SENDTO_MOBILE_PRIVATE_AND_FIXED) > 0 ||
				(n_addrtypes & SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) > 0) {
			m_lbl_adrcount_privatemobile.setVisible(true);
			m_txt_adrcount_privatemobile.setVisible(true);
			m_lbl_adrcount_total.setVisible(true);
			m_txt_adrcount_total.setVisible(true);
		}
		else {
			m_lbl_adrcount_privatemobile.setVisible(false);
			m_txt_adrcount_privatemobile.setVisible(false);
		}
		m_txt_adrcount_companymobile.setText(new Integer(c.get_companymobile()).toString());
		if((n_addrtypes & SendController.SENDTO_MOBILE_COMPANY) > 0 || (n_addrtypes & SendController.SENDTO_MOBILE_COMPANY_AND_FIXED) > 0 ||
				(n_addrtypes & SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) > 0) {
			m_lbl_adrcount_companymobile.setVisible(true);
			m_txt_adrcount_companymobile.setVisible(true);
			m_lbl_adrcount_total.setVisible(true);
			m_txt_adrcount_total.setVisible(true);
		}
		else {
			m_lbl_adrcount_companymobile.setVisible(false);
			m_txt_adrcount_companymobile.setVisible(false);
		}
		m_txt_adrcount_private_sms.setText(new Integer(c.get_privatesms()).toString());
		if((n_addrtypes & SendController.SENDTO_SMS_PRIVATE) > 0 || (n_addrtypes & SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) > 0 ||
				(n_addrtypes & SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) > 0) {
			m_lbl_adrcount_private_sms.setVisible(true);
			m_txt_adrcount_private_sms.setVisible(true);
			m_lbl_adrcount_total_sms.setVisible(true);
			m_txt_adrcount_total_sms.setVisible(true);
		}
		else {
			m_lbl_adrcount_private_sms.setVisible(false);
			m_txt_adrcount_private_sms.setVisible(false);
		}
		m_txt_adrcount_company_sms.setText(new Integer(c.get_companysms()).toString());
		if((n_addrtypes & SendController.SENDTO_SMS_COMPANY) > 0 || (n_addrtypes & SendController.SENDTO_SMS_COMPANY_ALT_FIXED) > 0 ||
				(n_addrtypes & SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0) {
			m_lbl_adrcount_company_sms.setVisible(true);
			m_txt_adrcount_company_sms.setVisible(true);
			m_lbl_adrcount_total_sms.setVisible(true);
			m_txt_adrcount_total_sms.setVisible(true);
		}
		else {
			m_lbl_adrcount_company_sms.setVisible(false);
			m_txt_adrcount_company_sms.setVisible(false);
		}
		m_txt_adrcount_privatenonumber.setText(new Integer(c.get_privatenonumber()).toString());
		if((n_addrtypes & SendController.SENDTO_NOPHONE_PRIVATE) > 0) {
			m_lbl_adrcount_privatenonumber.setVisible(true);
			m_txt_adrcount_privatenonumber.setVisible(true);
			m_lbl_adrcount_total.setVisible(true);
			m_txt_adrcount_total.setVisible(true);
		}
		else {
			m_lbl_adrcount_privatenonumber.setVisible(false);
			m_txt_adrcount_privatenonumber.setVisible(false);
		}
		m_txt_adrcount_companynonumber.setText(new Integer(c.get_companynonumber()).toString());
		if((n_addrtypes & SendController.SENDTO_NOPHONE_COMPANY) > 0) {
			m_lbl_adrcount_companynonumber.setVisible(true);
			m_txt_adrcount_companynonumber.setVisible(true);
			m_lbl_adrcount_total.setVisible(true);
			m_txt_adrcount_total.setVisible(true);
		}
		else {
			m_lbl_adrcount_companynonumber.setVisible(false);
			m_txt_adrcount_companynonumber.setVisible(false);
		}
		if((n_addrtypes & SendController.SENDTO_CELL_BROADCAST_TEXT) > 0) {
			m_lbl_adrcount_lba_sms.setVisible(true);
			m_txt_adrcount_lba_sms.setVisible(true);
		} else {
			m_lbl_adrcount_lba_sms.setVisible(true);
			m_txt_adrcount_lba_sms.setVisible(true);
		}
		//m_txt_adrcount_total.setText(new Integer(c.get_total()).toString());
		int n_total = 0;
		//calculate recipient count by addresstypes, and update AddressCount object
		//this new count (total_by_adrtypes()) will then be reflected when confirming Live/Test sending in SendWindow
		int adrtypes = parent.get_sendobject().get_sendproperties().get_addresstypes();
		int newadrtypes = GetAdrTypesForCount(adrtypes);
		/*n_total = (SendController.HasType(newadrtypes, SendController.SENDTO_FIXED_PRIVATE) ? c.get_private() : 0) +
				  (SendController.HasType(newadrtypes, SendController.SENDTO_FIXED_COMPANY) ? c.get_company() : 0) +
				  (SendController.HasType(newadrtypes, SendController.SENDTO_MOBILE_PRIVATE)? c.get_privatemobile() : 0) +
				  (SendController.HasType(newadrtypes, SendController.SENDTO_MOBILE_COMPANY) ? c.get_companymobile() : 0) +
				  (SendController.HasType(newadrtypes, SendController.SENDTO_NOPHONE_PRIVATE) ? c.get_privatenonumber() : 0) +
				  (SendController.HasType(newadrtypes, SendController.SENDTO_NOPHONE_COMPANY) ? c.get_companynonumber() : 0) +
				  (SendController.HasType(newadrtypes, SendController.SENDTO_SMS_COMPANY) ? c.get_companymobile(): 0) +
				  (SendController.HasType(newadrtypes, SendController.SENDTO_SMS_PRIVATE) ? c.get_privatemobile(): 0);*/
		n_total = c.get_private()+c.get_company()+c.get_privatemobile()+c.get_companymobile();
		m_txt_adrcount_total.setText(new Integer(n_total).toString());
		n_total = c.get_privatesms()+c.get_companysms();
		m_txt_adrcount_total_sms.setText(new Integer(n_total).toString());
		n_total = c.get_private()+c.get_company()+c.get_privatemobile()+c.get_companymobile() + c.get_privatesms()+c.get_companysms();
		c.set_total_by_types(n_total);
	}	
	

	protected abstract void exec_adrcount();
}