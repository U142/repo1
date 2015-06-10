package no.ums.pas.importer.gis;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.localization.Localization;

public class PreviewAddressFinish extends DefaultPanel implements ComponentListener, ChangeListener{
	PreviewAddressFrame m_parent;
	boolean m_b_is_alert = false;
	JButton m_btn_import, m_btn_cancel,m_btn_finish;
	
	PreviewAddressFinish(PreviewAddressFrame parent,boolean bIsAlert)
	{  
	 super();
	   m_b_is_alert = bIsAlert;
	   m_parent = parent;
	   init();
       setPreferredSize(new Dimension(300,80));
	}
	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add_controls() {
		
		get_gridconst().insets=new Insets(3, 5, 3, 5);
		set_gridconst(0, 2, 1, 1);

		get_gridconst().weightx = 1;
		add(new JLabel(""), get_gridconst());
		
		set_gridconst(1, 2, 1, 1);
		get_gridconst().weightx = 0;
		get_gridconst().anchor = GridBagConstraints.NORTHEAST;		
		add(m_btn_cancel, get_gridconst());
		
        set_gridconst(2, 2, 1, 1);
       
       
        
		add(m_btn_import, get_gridconst());
		
		set_gridconstSouth(0,4, 15, 1);

		get_gridconst().fill = GridBagConstraints.HORIZONTAL;
		add(m_btn_finish, get_gridconst());
	}

	@Override
	public void init() {
		 m_btn_import = new JButton((m_b_is_alert ? Localization.l("common_preview") : Localization.l("common_wizard_import")));
		 m_btn_import.addActionListener(this);
		 m_btn_import.setActionCommand("act_fetch_addresses");
		 
		 m_btn_cancel = new JButton("Cancel");
		 m_btn_cancel.addActionListener(this);
		 m_btn_cancel.setActionCommand("frame_dispose");
		 
		 m_btn_finish=new JButton(Localization.l("common_finish"));
		 m_btn_finish.setEnabled(false);
			m_btn_finish.setActionCommand("act_finish");
			m_btn_finish.addActionListener(this);
		// setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			add_controls();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if("act_fetch_addresses".equals(e.getActionCommand())) {
			m_parent.actionPerformed(e);
		}
		else if("frame_dispose".equals(e.getActionCommand())) {
			m_parent.dispose();
		}
		else if("act_finish".equals(e.getActionCommand())) {
			
			m_parent.actionPerformed(e);
            this.setVisible(false);
		}
	
	}

	
	
}
