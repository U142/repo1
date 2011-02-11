package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.maps.defines.HouseItem;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.MapPoint;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

//import org.jvnet.substance.SubstanceLookAndFeel;
//import UMS.Tools.*;


public class HouseEditorDlg extends DefaultPanel implements ChangeListener, ComponentListener { // implements ActionListener {
	public static final long serialVersionUID = 1;
	private JTabbedPane m_tab;
	private HouseEditorPanel m_panel;
	private HouseMovePanel m_move;
	private ActionListener m_callback;
	private MapPoint m_point;
	private HouseItem m_house = null;
	private InfoPanel m_info = null;
	
	public HouseEditorDlg(InfoPanel info, JFrame parent, ActionListener callback, MapPoint point, HouseItem house) {
		//super(parent, "Edit address database", false);
		super();
		m_callback = callback;
		m_point = point;
		m_house = house;
		m_info = info;
		m_tab = new JTabbedPane(JTabbedPane.NORTH);
		//m_tab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		//m_tab.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_VERTICAL_ORIENTATION, Boolean.TRUE);
		/*try {
			this.setAlwaysOnTop(true);
			Dimension d = Utils.screendlg_upperleft(500, 490);
			setBounds(d.width, d.height, 500, 490);
			this.setResizable(false);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}*/
		
		m_panel = new HouseEditorPanel(this, point, house);
		m_move  = new HouseMovePanel(this);
		m_tab.addTab(PAS.l("main_houseeditortab_newdelete"), null,
				m_panel,
    			PAS.l("main_houseeditortab_newdelete_tooltip"));
		m_tab.addTab(PAS.l("main_houseeditortab_manualgeocoding"), null,
				m_move,
				PAS.l("main_houseeditortab_manualgeocoding_tooltip"));

		m_tab.addChangeListener(this);
		setLayout(new BorderLayout());
		add_controls();
		addComponentListener(this);
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) {
		//System.out.println("resize: " + getWidth() + ", " + m_inhablist.getHeight());
		//this.setPreferredSize(new );
		m_panel.setPreferredSize(new Dimension(getWidth(), getHeight()));
		m_move.setPreferredSize(new Dimension(getWidth(), getHeight()));
		//this.setPreferredSize(new Dimension(getWidth(), Math.max(m_panel.getHeight(), m_move.getHeight())));
	}		
	
	public void add_controls() {
		/*if(m_info!=null) {
			add(m_info, BorderLayout.NORTH);
			add(m_tab, BorderLayout.CENTER);
		} else */{
			//m_info = PAS.get_pas().get_eastcontent().get_infopanel();
			add(m_info.m_txt_housedownload_progress, BorderLayout.NORTH);
			add(m_tab, BorderLayout.CENTER);
		}
		init();
	}
		
	public void init() {
		//getContentPane().add(m_tab, BorderLayout.CENTER);
		//doLayout();
		//this.pack();
		setVisible(true);
	}
	public void reinit(MapPoint point, ArrayList<HouseItem> item) {
		//setVisible(true);
		//if(m_tab.getSelectedIndex()==0)
		if(m_tab.getSelectedComponent()==m_panel)
			m_panel.reinit(point, item);
		else if(m_tab.getSelectedComponent()==m_move) {
			m_move.set_point(point);
			m_callback.actionPerformed(new ActionEvent(point, ActionEvent.ACTION_PERFORMED, "act_houseeditor_setcoor_none"));
		}
		//doLayout();
		//this.pack();
	}
	public void actionPerformed(ActionEvent e) {
		if("act_save_complete".equals(e.getActionCommand())) {
			Inhabitant i = (Inhabitant)e.getSource();
			ActionEvent insert = new ActionEvent(i, ActionEvent.ACTION_PERFORMED, "act_insert_inhabitant");
			PAS.get_pas().get_housecontroller().actionPerformed(insert);
			//m_house = null; //list have been edited. pointer has changed
			m_house = PAS.get_pas().get_housecontroller().get_houses().find_inhabitants_house(i);
			m_panel.refresh(m_house);
			PAS.get_pas().download_houses();
		}
		else if("act_delete_inhabitant_complete".equals(e.getActionCommand())) {
			Inhabitant i = (Inhabitant)e.getSource();
			m_house = i.get_parenthouse();
			ActionEvent delete = new ActionEvent(i, ActionEvent.ACTION_PERFORMED, "act_delete_inhabitant");
			PAS.get_pas().get_housecontroller().actionPerformed(delete);	
			//m_house = null; //list have been edited. pointer has changed
			//m_house = PAS.get_pas().get_housecontroller().get_houses().find_inhabitants_house(i);
			m_panel.refresh(m_house);
			PAS.get_pas().download_houses();
		}
		else if("act_ready_for_coor_assignment".equals(e.getActionCommand())) {
			int n_adrtype = ((Inhabitant)e.getSource()).get_inhabitanttype();
			System.out.println("Ready for coor assignment for adrtype = " + n_adrtype);
			ActionEvent action = null;
			/*switch(n_adrtype) {
				case Controller.ADR_TYPES_PRIVATE_:
				case Controller.ADR_TYPES_MOBILE_:
					action = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "act_ready_for_coor_assignment_private");
					break;
				case Controller.ADR_TYPES_COMPANY_:
					action = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "act_ready_for_coor_assignment_company");
					break;*/
			/*if((n_adrtype & SendController.SENDTO_FIXED_PRIVATE)==SendController.SENDTO_FIXED_PRIVATE ||
				(n_adrtype & SendController.SENDTO_MOBILE_PRIVATE)==SendController.SENDTO_MOBILE_PRIVATE) {*/
			if(n_adrtype==Inhabitant.INHABITANT_PRIVATE) {
				action = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "act_ready_for_coor_assignment_private");
			}
			/*else if((n_adrtype & SendController.SENDTO_FIXED_COMPANY)==SendController.SENDTO_FIXED_COMPANY ||
					(n_adrtype & SendController.SENDTO_MOBILE_COMPANY)==SendController.SENDTO_MOBILE_COMPANY) {*/
			else if(n_adrtype==Inhabitant.INHABITANT_COMPANY) {
				action = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "act_ready_for_coor_assignment_company");				
			}
			if(action!=null)
				m_callback.actionPerformed(action);
		}
	}
	
	public void stateChanged(ChangeEvent e) {
		JTabbedPane pane = (JTabbedPane)e.getSource();
		//System.out.println("Selected component " + pane.getSelectedIndex());
		if(pane.getSelectedComponent()==m_move) {
			m_move.set_focus();
			m_callback.actionPerformed(new ActionEvent(e, ActionEvent.ACTION_PERFORMED, "act_houseeditor_setcoor_none"));
		}
		else if(pane.getSelectedComponent()==m_panel) {
			m_panel.set_focus();
			m_callback.actionPerformed(new ActionEvent(e, ActionEvent.ACTION_PERFORMED, "act_houseeditor_newadr"));
		}
		
	}
}