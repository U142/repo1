package no.ums.pas.importer.gis;


import java.awt.event.*;

import no.ums.pas.core.defines.*;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.InhabitantBasics;
import no.ums.pas.ums.tools.*;


public class StatisticsPanel extends DefaultPanel {
	public static final long serialVersionUID = 1;
	
	private static final int fontsize = 12;
	private StdTextLabel m_lbl_filelines			= new StdTextLabel("Total lines in file:", 250, fontsize, true);
	private StdTextLabel m_lbl_houses				= new StdTextLabel("Total buildings (lines in file):", 250, fontsize, true);
	private StdTextLabel m_lbl_persons				= new StdTextLabel("Total number of persons:", 250, fontsize, true);
	private StdTextLabel m_lbl_private				= new StdTextLabel("Private:", 250, fontsize, true);
	private StdTextLabel m_lbl_company				= new StdTextLabel("Companies:", 250, fontsize, true);
	private StdTextLabel m_lbl_fixed_phones			= new StdTextLabel("Fixed phones:", 250, fontsize, true);
	private StdTextLabel m_lbl_mobile_phones		= new StdTextLabel("Mobile phones:", 250, fontsize, true);
	private StdTextLabel m_lbl_nonumber				= new StdTextLabel("No number:", 250, fontsize, true);
	
	private StdTextLabel m_txt_filelines			= new StdTextLabel("", 150, fontsize, false);
	private StdTextLabel m_txt_persons				= new StdTextLabel("", 150, fontsize, false);
	private StdTextLabel m_txt_houses				= new StdTextLabel("", 150, fontsize, false);
	private StdTextLabel m_txt_private				= new StdTextLabel("", 150, fontsize, false);
	private StdTextLabel m_txt_company				= new StdTextLabel("", 150, fontsize, false);
	private StdTextLabel m_txt_fixed_phones			= new StdTextLabel("", 150, fontsize, false);
	private StdTextLabel m_txt_mobile_phones		= new StdTextLabel("", 150, fontsize, false);
	private StdTextLabel m_txt_nonumber				= new StdTextLabel("", 150, fontsize, false);
	
	int n_filelines = 0;
	int n_persons = 0;
	int n_houses = 0;
	int n_houses_selected = 0;
	int n_private = 0;
	int n_private_selected = 0;
	int n_company = 0;
	int n_company_selected = 0;
	int n_fixed_phones = 0;
	int n_fixed_phones_selected = 0;
	int n_mobile_phones = 0;
	int n_mobile_phones_selected = 0;
	int n_nonumber = 0;
	int n_nonumber_selected = 0;	
	
	public StatisticsPanel() {
		super();
		add_controls();
	}
	private void resetStatistics() {
		//n_filelines = 0;
		n_persons = 0;
		n_houses = 0;
		n_houses_selected = 0;
		n_private = 0;
		n_company = 0;
		n_private_selected = 0;
		n_company_selected = 0;
		n_fixed_phones = 0;
		n_fixed_phones_selected = 0;
		n_mobile_phones = 0;
		n_mobile_phones_selected = 0;
		n_nonumber = 0;
		n_nonumber_selected = 0;
	}
	
	public void actionPerformed(ActionEvent e) {
		if("act_update_statistics".equals(e.getActionCommand())) {
			GISList list = (GISList)e.getSource();
			calcStatistics(list);
		}
	}
	public void add_controls() {
		/*set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_filelines, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_filelines, get_gridconst());
		*/
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_houses, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_houses, get_gridconst());

		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_persons, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_persons, get_gridconst());
		
		
		add_spacing(DefaultPanel.DIR_VERTICAL, 20);

		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_private, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_private, get_gridconst());

		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_company, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_company, get_gridconst());

		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_fixed_phones, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_fixed_phones, get_gridconst());

		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_mobile_phones, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_mobile_phones, get_gridconst());

		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_nonumber, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_nonumber, get_gridconst());
		
		init();
		
	}
	public void init() {
		setVisible(true);
	}

	protected void updateStatisticsUI() {
		//m_txt_houses.setText(new Integer(n_houses_selected).toString() + " / " + new Integer(n_houses).toString());
		m_txt_filelines.setText(new Integer(n_filelines).toString());
		m_txt_persons.setText(new Integer(n_persons).toString());
		m_txt_houses.setText(new Integer(n_houses).toString());
		m_txt_private.setText(new Integer(n_private_selected).toString() + " / " + new Integer(n_private).toString());
		m_txt_company.setText(new Integer(n_company_selected).toString() + " / " + new Integer(n_company).toString());
		m_txt_fixed_phones.setText(new Integer(n_fixed_phones_selected).toString() + " / " + new Integer(n_fixed_phones).toString());
		m_txt_mobile_phones.setText(new Integer(n_mobile_phones_selected).toString() + " / " + new Integer(n_mobile_phones).toString());
		m_txt_nonumber.setText(new Integer(n_nonumber_selected).toString() + " / " + new Integer(n_nonumber).toString());
	}	

	
	protected void calcStatistics(GISList list) {
		resetStatistics();
		
		n_houses	= list.size();
		
		GISRecord house;
		int n_recordcount = 0;
		boolean b_house_has_selected_inhab;
		for(int h=0; h < list.size(); h++) {
			house = (GISRecord)list.get(h);
			b_house_has_selected_inhab = false;
			for(int i=0; i < house.get_inhabitantcount(); i++) {
				n_persons ++;
				n_recordcount++;
				InhabitantBasics inhabitant = house.get_inhabitant(i);
				if(inhabitant.get_inhabitanttype()==Inhabitant.INHABITANT_PRIVATE) {
					n_private++;
					if(inhabitant.get_included())
						n_private_selected++;
				}
				else if(inhabitant.get_inhabitanttype()==Inhabitant.INHABITANT_COMPANY) {
					n_company++;
					if(inhabitant.get_included())
						n_company_selected++;
				}
				if(inhabitant.get_hasfixed()==1) {
					n_fixed_phones++;
					if(inhabitant.get_included())
						n_fixed_phones_selected++;
				}
				if(inhabitant.get_hasmobile()==1) {
					n_mobile_phones++;
					if(inhabitant.get_included())
						n_mobile_phones_selected++;
				}
				if(inhabitant.get_hasfixed()!=1 && inhabitant.get_hasmobile()!=1) {
					n_nonumber++;
					if(inhabitant.get_included())
						n_nonumber_selected++;
				}
				if(inhabitant.get_included())
					b_house_has_selected_inhab = true;
			}
			if(b_house_has_selected_inhab)
				n_houses_selected++;
		}
		updateStatisticsUI();
	}
	
}