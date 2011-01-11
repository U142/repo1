package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.core.controllers.GPSController;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.events.EventChangeArrowsize;
import no.ums.pas.core.events.EventChangeGPSEpsilon;
import no.ums.pas.core.menus.SliderMenuItem;
import no.ums.pas.maps.defines.MapObjectVars;
import no.ums.pas.ums.tools.StdTextArea;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;


public class GPSFrame extends JPanel implements ComponentListener { //JFrame
	public static final long serialVersionUID = 1;
	
	private GPSController m_gpscontroller;
	private OpenGPS m_panel;
	public SliderMenuItem m_slider_epsilon;
	public JLabel m_label_epsilon;
	public GPSController get_controller() { return m_gpscontroller; }
	public OpenGPS get_panel() { return m_panel; }
	public Dimension get_dim() { return m_dim; }
	private Dimension m_dim;
	private JLabel m_label;
	public JLabel m_label_arrowsize;
	public SliderMenuItem m_slider_arrowsize;
	public JLabel get_label() { return m_label; }
	private JLabel m_label_epsilon_value;
	public JLabel get_label_epsilon_value() { return m_label_epsilon_value; }
	private GPSSearchPanel m_gpssearchpanel;
	public GPSSearchPanel get_gpssearchpanel() { return m_gpssearchpanel; }
	private JButton m_btn_export = new JButton("Export");
	public JButton get_btn_export() { return m_btn_export; }
	
	
	public GPSFrame(GPSController controller)
	{
		super();
		m_gpscontroller = controller;
		this.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		m_dim = new Dimension(PAS.get_pas().get_eastwidth()-20, 500); //- 700
		setBounds(0,0,m_dim.width, m_dim.height);
		String sz_columns[] = { "Dynamic", "Name", "Type", "Date", "Time", "Trail", "Show", "Alert", "Follow", "Street", "Region" };
		boolean b_editable[] = { false, false, false, false, false, true, true, true, true, false, false };
		int n_width[] = { 10, 170, 30, 70, 50, 30, 30, 30, 30, 30, 30 };
		m_panel = new OpenGPS(PAS.get_pas(), this, sz_columns, n_width, b_editable, m_dim);
		setPreferredSize(m_dim);
		m_label = new JLabel("Map Objects");
		m_label_arrowsize = new JLabel("Dir. arrow size");
		m_label_epsilon = new JLabel("Max GPS pointdist");
		m_label_epsilon_value = new JLabel("");
		m_slider_epsilon = new SliderMenuItem(PAS.get_pas(), "", 
				0/*min*/, 200/*max*/, 15/*current*/, 5/*minor*/, 10/*major*/, false/*show ticks*/, 
				false/*show legend*/, controller.get_epsilon_meters() + "m", new Dimension(120, 20), new EventChangeGPSEpsilon(PAS.get_pas()));
		m_slider_arrowsize = new SliderMenuItem(PAS.get_pas(), "",
				0, 20, 10, 0, 1, false, false, "", new Dimension(120, 20), new EventChangeArrowsize(PAS.get_pas()));
		m_gpssearchpanel = new GPSSearchPanel(PAS.get_pas());
		m_btn_export.setActionCommand("act_export");
		init();
	}
	public void clear()
	{
		get_panel().clear();
	}
	
	public void fill()
	{
		setBounds(new Rectangle(PAS.get_pas().get_mappane().getLocationOnScreen().x + PAS.get_pas().get_mappane().get_dimension().width, PAS.get_pas().get_mappane().getLocationOnScreen().y, m_dim.width, m_dim.height));
		set_visible(true);
		get_panel().start_search();
	}
	public void set_visible(boolean b)
	{
		setVisible(b);
		get_panel().setVisible(b);
		get_label().setVisible(b);
		m_slider_epsilon.setVisible(b);
		m_label_epsilon.setVisible(b);
		m_label_arrowsize.setVisible(b);
		m_slider_arrowsize.setVisible(b);
	}
	
	public void init()
	{
	}
	public void reSize(Dimension d) {
		setPreferredSize(new Dimension(d.width, 500));
		revalidate();
		get_panel().setPreferredSize( new Dimension( d.width, d.height-500 ) );
		get_panel().revalidate();
	}
	
	public void open()
	{
	}
	
	public void componentResized(ComponentEvent e) {
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }			
	
	class GPSSearchPanel extends DefaultPanel implements KeyListener {
		public static final long serialVersionUID = 1;
		//type (car/dog/...)
		//static/dynamic
		//online/offline
		//available/occupied
		//StdTextArea m_lbl_name = new StdTextArea("Name: ", false);
		StdTextArea m_txt_name = new StdTextArea("", false, 150);
		UMSCheckBox m_check_type[];
		UMSCheckBox m_check_dynamic[];
		UMSCheckBox m_check_online[];
		UMSCheckBox m_check_available[];
				
		GPSSearchPanel(PAS pas) {
			super();
			TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Filters");
			//TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 1), "Filters");
			border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
			setBorder(border);
			setPreferredSize(new Dimension(PAS.get_pas().get_eastwidth()-20, 200));
			int i;
			m_check_type = new UMSCheckBox[MapObjectVars.GPS_UNIT_USERTYPE_[1].length];
			m_check_dynamic = new UMSCheckBox[MapObjectVars.GPS_UNIT_DYNAMIC_[1].length];
			m_check_online = new UMSCheckBox[MapObjectVars.GPS_UNIT_ONLINESTATUS_[1].length];
			m_check_available = new UMSCheckBox[MapObjectVars.GPS_UNIT_CARRIER_STATUS_[1].length];
			
			for(i=0; i < m_check_type.length; i++) {
				m_check_type[i] = new UMSCheckBox(true, new Integer(MapObjectVars.GPS_UNIT_USERTYPE_[2][i]).intValue(), MapObjectVars.FILTER_TYPE_USERTYPE_);
				m_check_type[i].set_properties(new Integer(MapObjectVars.GPS_UNIT_USERTYPE_[0][i]), MapObjectVars.GPS_UNIT_USERTYPE_[1][i]);
				m_check_type[i].addActionListener(this);
				m_check_type[i].setActionCommand("act_filter_checkbox");
			}
			for(i=0; i < m_check_dynamic.length; i++) {
				m_check_dynamic[i] = new UMSCheckBox(true, new Integer(MapObjectVars.GPS_UNIT_DYNAMIC_[2][i]).intValue(), MapObjectVars.FILTER_TYPE_DYNAMIC_);
				m_check_dynamic[i].set_properties(new Boolean((MapObjectVars.GPS_UNIT_DYNAMIC_[0][i].equals("0") ? false : true)), MapObjectVars.GPS_UNIT_DYNAMIC_[1][i]);
				m_check_dynamic[i].addActionListener(this);
				m_check_dynamic[i].setActionCommand("act_filter_checkbox");
			}
			for(i=0; i < m_check_online.length; i++) {
				m_check_online[i] = new UMSCheckBox(true, new Integer(MapObjectVars.GPS_UNIT_ONLINESTATUS_[2][i]).intValue(), MapObjectVars.FILTER_TYPE_ONLINE_STATUS_);
				m_check_online[i].set_properties(new Boolean((MapObjectVars.GPS_UNIT_ONLINESTATUS_[0][i].equals("0") ? false : true)), MapObjectVars.GPS_UNIT_ONLINESTATUS_[1][i]);
				m_check_online[i].addActionListener(this);
				m_check_online[i].setActionCommand("act_filter_checkbox");
			}
			for(i=0; i < m_check_available.length; i++) {
				m_check_available[i] = new UMSCheckBox(true, new Integer(MapObjectVars.GPS_UNIT_CARRIER_STATUS_[2][i]).intValue(), MapObjectVars.FILTER_TYPE_STATUS_);
				m_check_available[i].set_properties(new Integer(MapObjectVars.GPS_UNIT_CARRIER_STATUS_[0][i]), MapObjectVars.GPS_UNIT_CARRIER_STATUS_[1][i]);
				m_check_available[i].addActionListener(this);
				m_check_available[i].setActionCommand("act_filter_checkbox");
			}
			add_controls();

		}
		public void add_controls() {
			int i;
			UMSSearchPanelFlow filter_name  = new UMSSearchPanelFlow(PAS.get_pas(), "Name");
			UMSSearchPanelFlow check_type	= new UMSSearchPanelFlow(PAS.get_pas(), "Carrier");
			UMSSearchPanelFlow check_dynamic= new UMSSearchPanelFlow(PAS.get_pas(), "Type");
			UMSSearchPanelFlow check_online = new UMSSearchPanelFlow(PAS.get_pas(), "Active");
			UMSSearchPanelFlow check_available = new UMSSearchPanelFlow(PAS.get_pas(), "Status");
			
			filter_name.add(m_txt_name);
			m_txt_name.addActionListener(this);
			m_txt_name.setActionCommand("act_name_edited");
			m_txt_name.addKeyListener(this);

			for(i=0; i < m_check_type.length; i++) {
				check_type.add(m_check_type[i]);
			}
			for(i=0; i < m_check_dynamic.length; i++) {
				check_dynamic.add(m_check_dynamic[i]);
			}
			for(i=0; i < m_check_online.length; i++) {
				check_online.add(m_check_online[i]);
			}
			for(i=0; i < m_check_available.length; i++) {
				check_available.add(m_check_available[i]);
			}
			set_gridconst(0, 0, 2, 1, GridBagConstraints.NORTHWEST);
			add(filter_name, m_gridconst);
			set_gridconst(0, 1, 1, 1, GridBagConstraints.NORTHWEST);
			add(check_dynamic, m_gridconst);
			set_gridconst(1, 1, 1, 1, GridBagConstraints.NORTHWEST);
			add(check_online, m_gridconst);
			set_gridconst(0, 2, 1, 1, GridBagConstraints.NORTHWEST);
			add(check_available, m_gridconst);
			set_gridconst(1, 2, 1, 1, GridBagConstraints.NORTHWEST);
			add(check_type, m_gridconst);
			
			filter_name.doLayout();
			check_type.doLayout();
			check_dynamic.doLayout();
			check_online.doLayout();
			check_available.doLayout();
			init();
		}
		public void init() {
			this.doLayout();
			setVisible(true);
		}
		public synchronized void actionPerformed(ActionEvent e) {
			if("act_filter_checkbox".equals(e.getActionCommand())) {
				UMSCheckBox check = (UMSCheckBox)e.getSource();
				m_panel.flip_filter(check.get_filtertype(), check.get_filter(), check.isSelected());
			} else if("act_name_edited".equals(e.getActionCommand())) {
			}	
		}
		public synchronized void keyPressed(KeyEvent e) {
			
		}
		public synchronized void keyReleased(KeyEvent e) {
			if(e.getSource().equals(m_txt_name))
				m_panel.add_textfilter(MapObjectVars.FILTER_TYPE_NAME_, m_txt_name.getText());
			
		}
		public synchronized void keyTyped(KeyEvent e) {
				
		}

		
	}
	class UMSSearchPanelFlow extends JPanel {
		public static final long serialVersionUID = 1;
		PAS m_pas;
		Font m_component_font;
		
		UMSSearchPanelFlow(PAS pas, String sz_title) {
			super();
			m_pas = pas;
			setLayout(new FlowLayout());
			TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 1), sz_title);
			//TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), sz_title);
			//TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), sz_title);
			
			m_component_font = new Font("Helvetica", Font.PLAIN, 10);
			border.setTitleFont(m_component_font);
			setBorder(border);
			//setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(1), sz_title));
		}
		public void add_component(Component c) {
			//FontSet c.setFont(m_component_font);
			add(c);
		}
	}
	class UMSCheckBox extends JCheckBox {
		public static final long serialVersionUID = 1;
		private Object m_n_id = null;
		public Object get_id() { return m_n_id; }
		private int m_n_filter = 0;
		public int get_filter() { return m_n_filter; }
		private int m_n_filtertype = 0;
		public int get_filtertype() { return m_n_filtertype; }
		public void set_properties(Object n_id, String sz_name) {
			m_n_id = n_id;
			this.setText(sz_name);
			//FontSet this.setFont(new Font("Helvetica", Font.PLAIN, 10));
		}
		UMSCheckBox(boolean b_clicked, int n_filter, int n_filtertype) {
			super("", b_clicked);
			m_n_filter = n_filter;
			m_n_filtertype = n_filtertype;
		}
		UMSCheckBox(Object n_id, String sz_name, boolean b_clicked, int n_filter, int n_filtertype) {
			super(sz_name, b_clicked);
			m_n_id = n_id;
			m_n_filter = n_filter;
			m_n_filtertype = n_filtertype;
		}
	}

}	
	