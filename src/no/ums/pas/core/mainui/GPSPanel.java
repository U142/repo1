package no.ums.pas.core.mainui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.*;

import javax.swing.BorderFactory;
//import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import no.ums.pas.*;
import no.ums.pas.core.defines.*;

class GPSPanel extends DefaultPanel implements ComponentListener {
	public static final long serialVersionUID = 1;
	GPSFrame get_gpsframe() { return PAS.get_pas().get_gpsframe(); }
	//JScrollPane m_scrollpane;

	GPSPanel(PAS pas, Dimension size) {
		super();
		setPreferredSize(size);
		//m_scrollpane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add_controls();
		addComponentListener(this);
		get_gpsframe().get_btn_export().addActionListener(this);
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) {
		revalidate();
		get_gpsframe().reSize(new Dimension(getWidth(), getHeight()));
	}
	public void componentShown(ComponentEvent e) { }
	
	public void add_controls() {
		m_gridconst.fill = GridBagConstraints.BOTH;		
		
		int x_width = 4;
		/*set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.CENTER);
		add(get_gpsframe().get_label(),m_gridconst);*/
		//javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Map Objects");
		//this.setBorder(border);

		//int n_temp = inc_panels();
		OptionPanel option_panel = new OptionPanel(PAS.get_pas());
		option_panel.set_gridconst(0, 0, 1, 1, GridBagConstraints.NORTHWEST);
		option_panel.add(get_gpsframe().m_label_epsilon, option_panel.m_gridconst);
		option_panel.set_gridconst(1, 0, 1, 1, GridBagConstraints.NORTHWEST);
		option_panel.add(get_gpsframe().m_slider_epsilon, option_panel.m_gridconst);
		option_panel.set_gridconst(2, 0, 2, 1, GridBagConstraints.NORTHWEST);
		option_panel.m_gridconst.ipadx = 30;
		option_panel.add(get_gpsframe().get_label_epsilon_value(), option_panel.m_gridconst);
		option_panel.m_gridconst.ipadx = 0;
		//n_temp = inc_panels();
		option_panel.set_gridconst(4, 0, 1, 1, GridBagConstraints.NORTHWEST);
		option_panel.add(get_gpsframe().m_label_arrowsize, option_panel.m_gridconst);
		option_panel.set_gridconst(5, 0, 1, 1, GridBagConstraints.NORTHWEST);
		option_panel.add(get_gpsframe().m_slider_arrowsize, option_panel.m_gridconst);
		
		set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.CENTER);
		add(option_panel, m_gridconst);
		
		set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.CENTER);
		add(get_gpsframe().get_gpssearchpanel(), m_gridconst);
	
		set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.CENTER);
		add(get_gpsframe().get_panel(), m_gridconst);
		
		set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.CENTER);
		add(get_gpsframe().get_btn_export(), m_gridconst);
		
		//set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.CENTER);
		//add(m_scrollpane, get_gridconst());
		
		
		//set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.CENTER);
		//add(new DateTimePicker(get_pas()), m_gridconst);

		//m_gridlayout.setConstraints(get_pas().get_gpsframe().get_panel(), m_gridconst);
		init();
	}
	public void init() {
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		if("act_export".equals(e.getActionCommand())) {
			get_gpsframe().get_panel().export(true);
		}

	}
	class OptionPanel extends DefaultPanel {
		public static final long serialVersionUID = 1;
		OptionPanel(PAS pas) {
			super();
			setPreferredSize(new Dimension(PAS.get_pas().get_eastwidth()-20, 100));
			TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Options");
			border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
			setBorder(border);
			add_controls();
		}
		public void add_controls() {
			init();
		}
		public void init() {
			//doLayout();
			setVisible(true);
		}
		public void actionPerformed(ActionEvent e) {

		}
	}
}

