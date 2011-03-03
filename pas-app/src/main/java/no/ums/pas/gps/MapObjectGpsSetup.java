package no.ums.pas.gps;


import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.ums.tools.StdTextLabel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class MapObjectGpsSetup extends DefaultPanel {
	public static final long serialVersionUID = 1;
	private MapObjectReg m_reg;

	private StdTextLabel m_lbl_gprstimeout = new StdTextLabel("GPRS Timeout (minutes):", 200);
	private JCheckBox m_chk_gprstimeout_param1 = new JCheckBox("", true);
	private JComboBox m_combo_gprstimeout_param2 = new JComboBox(new String[] { "0.5", "1", "5", "10", "60", "120", "240" });
	private JButton m_btn_gprstimeout_commit = new JButton("Send");
	
	private StdTextLabel m_lbl_tcptimeout = new StdTextLabel("TCP Timeout/Reconnect (seconds):", 200);
	private JComboBox m_combo_tcp_param1 = new JComboBox(new String[] { "30", "60", "120", "240" } );
	private JComboBox m_combo_tcp_param2 = new JComboBox(new String[] { "2", "5", "10", "30", "60", "120", "900" } );
	private JButton m_btn_tcp_commit = new JButton("Send");
	
	private StdTextLabel m_lbl_timer0 = new StdTextLabel("Create timer0 (seconds):", 200);
	private JComboBox m_combo_timer0_param2 = new JComboBox(new String [] { "1", "3", "5", "10", "30", "60", "120", "240", "900" } );
	private JButton m_btn_timer0_commit = new JButton("Send");
	
	private StdTextLabel m_lbl_event0 = new StdTextLabel("Send coor on timer0 event:", 200);
	private JComboBox m_combo_event0_param1 = new JComboBox(new String [] { "GPSFix", "No GPSFix" } );
	private JComboBox m_combo_event0_param2 = new JComboBox(new String [] { "GPGLL", "GPRMC" } );
	private JButton m_btn_event0_commit = new JButton("Send");
	
	private StdTextLabel m_lbl_start_timer0 = new StdTextLabel("Start or stop timer0:", 200);
	private JButton m_btn_timer0_start = new JButton("Start");
	private JButton m_btn_timer0_stop  = new JButton("Stop");
	
	private StdTextLabel m_lbl_create_movement_alarm = new StdTextLabel("Movement alarm (meters):", 200);
	private JComboBox m_combo_movement_param_meters = new JComboBox(new String[] { "5", "10", "20", "40", "80", "100" });
	private JButton m_btn_create_movement_alarm = new JButton("Create");
	private JButton m_btn_delete_movement_alarm = new JButton("Delete");
	
	private StdTextLabel m_lbl_create_gpsfix_alarm = new StdTextLabel("GPSFix Alarm:", 200);
	private JButton m_btn_create_gpsfix_alarm = new JButton("Create");
	private JButton m_btn_delete_gpsfix_alarm = new JButton("Delete");
	
	//private HttpPostForm m_form = null;
	//protected HttpPostForm get_form() { return m_form; }
	private String m_sz_url;
	protected String get_url() { return m_sz_url; }
	private LoadingPanel m_loader = new LoadingPanel("GPS Setup", new Dimension(200, 16), false);
	protected LoadingPanel get_loader() { return m_loader; }
	
	public MapObjectGpsSetup(MapObjectReg reg) {
		super();
		m_reg = reg;
		setPreferredSize(new Dimension(400, 300));
		m_sz_url = PAS.get_pas().get_sitename() + "PAS_gpssetup.asp";
		
		m_btn_gprstimeout_commit.setActionCommand("act_setgprstimeout");
		m_btn_gprstimeout_commit.addActionListener(this);
		m_btn_tcp_commit.setActionCommand("act_settcptimeout");
		m_btn_tcp_commit.addActionListener(this);
		m_btn_timer0_commit.setActionCommand("act_settimer0");
		m_btn_timer0_commit.addActionListener(this);
		m_btn_event0_commit.setActionCommand("act_setevent0");
		m_btn_event0_commit.addActionListener(this);
		m_btn_timer0_start.setActionCommand("act_start_timer0");
		m_btn_timer0_start.addActionListener(this);
		m_btn_timer0_stop.setActionCommand("act_stop_timer0");
		m_btn_timer0_stop.addActionListener(this);
		m_btn_create_gpsfix_alarm.setActionCommand("act_gpsfix_alarm");
		m_btn_create_gpsfix_alarm.addActionListener(this);
		m_btn_delete_gpsfix_alarm.setActionCommand("act_gpsfix_alarm_del");
		m_btn_delete_gpsfix_alarm.addActionListener(this);
		m_btn_create_movement_alarm.setActionCommand("act_movement_alarm");
		m_btn_create_movement_alarm.addActionListener(this);
		m_btn_delete_movement_alarm.setActionCommand("act_movement_alarm_del");
		m_btn_delete_movement_alarm.addActionListener(this);
		init();
	}
	public void actionPerformed(ActionEvent e) {
		if("act_setgprstimeout".equals(e.getActionCommand())) {
			System.out.println("Set GPRS timeout to " + m_chk_gprstimeout_param1.isSelected() + ", " + (String)(m_combo_gprstimeout_param2.getSelectedItem()));
			post_form(GPSCmd.CMD_GPRSTIMEOUT, (m_chk_gprstimeout_param1.isSelected() ? 1 : 0), new Double(new Double((String)(m_combo_gprstimeout_param2.getSelectedItem())).doubleValue()*1000*60).intValue(), "", "");
		}
		else if("act_settcptimeout".equals(e.getActionCommand())) {
			int n_param1, n_param2;
			n_param1 = new Integer((String)m_combo_tcp_param1.getSelectedItem()).intValue() * 1000;
			n_param2 = new Integer((String)m_combo_tcp_param2.getSelectedItem()).intValue() * 1000;
			System.out.println("Set TCP timeout/reconnect to " + n_param1 + ", " + n_param2);
			post_form(GPSCmd.CMD_TCPTIMEOUT, n_param1, n_param2, "" ,"");			
		}
		else if("act_settimer0".equals(e.getActionCommand())) {
			int n_param1 = 0;
			int n_param2 = new Integer((String)m_combo_timer0_param2.getSelectedItem()).intValue() * 1000;
			System.out.println("Set Alarm0 timer = " + n_param2 + " msecs");
			post_form(GPSCmd.CMD_TIMERX_ECONNECTED, n_param1, n_param2, "0", "");
		}
		else if("act_setevent0".equals(e.getActionCommand())) {
			int n_param1 = 1;
			int n_param2 = 8; //GPGLL, GPRMC
			String sz_param1;
			if(((String)m_combo_event0_param1.getSelectedItem()).equals("GPSFix"))
				sz_param1 = "1";
			else
				sz_param1 = "0";
			System.out.println("Set Alarm1 Coor event");
			post_form(GPSCmd.CMD_ALARM_ONTIMER_SENDCOOR, n_param1, n_param2, sz_param1, "");
		}
		else if("act_start_timer0".equals(e.getActionCommand())) {
			System.out.println("Start timer0");
			int n_param1 = 1;
			int n_param2 = new Integer((String)m_combo_timer0_param2.getSelectedItem()).intValue() * 1000;
			post_form(GPSCmd.CMD_TIMERX_STARTSTOP, n_param1, n_param2, "", "");
		}
		else if("act_stop_timer0".equals(e.getActionCommand())) {
			System.out.println("Stop timer0");
			int n_param1 = 0;
			post_form(GPSCmd.CMD_TIMERX_STARTSTOP, n_param1, 0, "", "");
		}
		else if("act_gpsfix_alarm".equals(e.getActionCommand())) {
			System.out.println("Activate GPSFix alarm");
			post_form(GPSCmd.CMD_ALARM_GPSFIX, 2, 0, "0", "");
			post_form(GPSCmd.CMD_ALARM_GPSFIX, 3, 0, "1", "");
		}
		else if("act_gpsfix_alarm_del".equals(e.getActionCommand())) {
			System.out.println("Delete GPSFix alarm");
			post_form(GPSCmd.CMD_ALARM_CLEAR, 2, 0, "", "");
			post_form(GPSCmd.CMD_ALARM_CLEAR, 3, 0, "", "");
		}
		else if("act_movement_alarm".equals(e.getActionCommand())) {
			int n_param1 = 4; //new Integer((String)m_combo_movement_param_meters.getSelectedItem()).intValue();
			int n_param2 = 10; //alarm no 4
			String sz_param1 = (String)m_combo_movement_param_meters.getSelectedItem();
			post_form(GPSCmd.CMD_ALARM_ONMOVE_SENDCOOR, n_param1, n_param2, sz_param1, "");
			System.out.println("Setup movement alarm (" + n_param1 + "m)");
		}
		else if("act_movement_alarm_del".equals(e.getActionCommand())) {
			post_form(GPSCmd.CMD_ALARM_CLEAR, 4, 0, "", "");
			System.out.println("Delete movement alarm");
		}
		else if("act_gps_answer".equals(e.getActionCommand())) {
			GpsSetupReturnCode ret = (GpsSetupReturnCode)e.getSource();
			String sz_msg = "Return value:" + ret.get_answertext() + " (" + ret.get_text() + ")";
			JOptionPane.showMessageDialog(this, sz_msg);
			System.out.println(sz_msg);
		}
	}
	public void post_form(int n_cmd, int n_param1, int n_param2, String sz_param1, String sz_param2) {
		/*try {
			m_form = new HttpPostForm(get_url());
			get_form().setParameter("l_objectpk", m_reg.get_mapobject().get_objectpk());
			get_form().setParameter("l_cmd", new Integer(n_cmd));
			get_form().setParameter("l_param1", new Integer(n_param1));
			get_form().setParameter("l_param2", new Integer(n_param2));
			get_form().setParameter("sz_param1", sz_param1);
			get_form().setParameter("sz_param2", sz_param2);
			XMLGpsSetup setup = new XMLGpsSetup(get_form(), this, get_loader());
			setup.start();
		} catch(IOException e) {
			System.out.println("MapObjectGpsSetup.post_form() IOException " + e.getMessage());
			e.printStackTrace();
			Error.getError().addError("MapObjectGpsSetup","Exception in post_form",e,1);
		} catch(Exception e) {
			System.out.println("MapObjectGpsSetup.post_form() Exception " + e.getMessage());
			e.printStackTrace();
			Error.getError().addError("MapObjectGpsSetup","Exception in post_form",e,1);
		}*/
	}
	public void add_controls() {
		//GPRS timeout
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_gprstimeout, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_chk_gprstimeout_param1, get_gridconst());
		set_gridconst(2, get_panel(), 1, 1);
		add(m_combo_gprstimeout_param2, get_gridconst());
		set_gridconst(3, get_panel(), 1, 1);
		add(m_btn_gprstimeout_commit, get_gridconst());
		
		//TCP timeout
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_tcptimeout, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_combo_tcp_param1, get_gridconst());
		set_gridconst(2, get_panel(), 1, 1);
		add(m_combo_tcp_param2, get_gridconst());
		set_gridconst(3, get_panel(), 1, 1);
		add(m_btn_tcp_commit, get_gridconst());
		
		//Timer0
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_timer0, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_combo_timer0_param2, get_gridconst());
		set_gridconst(3, get_panel(), 1, 1);
		add(m_btn_timer0_commit, get_gridconst());
		
		//CoorEvent 0
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_event0, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_combo_event0_param1, get_gridconst());
		set_gridconst(2, get_panel(), 1, 1);
		add(m_combo_event0_param2, get_gridconst());
		set_gridconst(3, get_panel(), 1, 1);
		add(m_btn_event0_commit, get_gridconst());
		
		//Start/stop timer
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_start_timer0, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_btn_timer0_start, get_gridconst());
		set_gridconst(2, get_panel(), 1, 1);
		add(m_btn_timer0_stop, get_gridconst());
		
		//Movement alarm
		set_gridconst(0, inc_panels(), 1, 1);
		add(this.m_lbl_create_movement_alarm, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(this.m_btn_create_movement_alarm, get_gridconst());
		set_gridconst(2, get_panel(), 1, 1);
		add(this.m_btn_delete_movement_alarm, get_gridconst());
		set_gridconst(3, get_panel(), 1, 1);
		add(this.m_combo_movement_param_meters, get_gridconst());
		
		//GPSFix alarm
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_create_gpsfix_alarm, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_btn_create_gpsfix_alarm, get_gridconst());
		set_gridconst(2, get_panel(), 1, 1);
		add(m_btn_delete_gpsfix_alarm, get_gridconst());
		
		set_gridconst(0, inc_panels(), 4, 1);
		add(get_loader(), get_gridconst());
	}
	public void init() {
		setSizes();
		setValues();
		setVisible(true);
		add_controls();
	}
	public void setSizes() {
		Dimension d = new Dimension(75,16);
		m_combo_gprstimeout_param2.setPreferredSize(d);
		m_btn_gprstimeout_commit.setPreferredSize(d);
		
		m_combo_tcp_param1.setPreferredSize(d);
		m_combo_tcp_param2.setPreferredSize(d);
		m_btn_tcp_commit.setPreferredSize(d);
		
		m_combo_timer0_param2.setPreferredSize(d);
		m_btn_timer0_commit.setPreferredSize(d);
		
		m_combo_event0_param1.setPreferredSize(d);
		m_combo_event0_param2.setPreferredSize(d);
		m_btn_event0_commit.setPreferredSize(d);
		
		m_btn_timer0_start.setPreferredSize(d);
		m_btn_timer0_stop.setPreferredSize(d);
		
		m_btn_create_gpsfix_alarm.setPreferredSize(d);
		m_btn_delete_gpsfix_alarm.setPreferredSize(d);
		
		m_combo_movement_param_meters.setPreferredSize(d);
		m_btn_create_movement_alarm.setPreferredSize(d);
		m_btn_delete_movement_alarm.setPreferredSize(d);
		
	}
	public void setValues() {
		m_combo_gprstimeout_param2.setSelectedIndex(3);
		m_combo_timer0_param2.setSelectedIndex(5);
	}
}