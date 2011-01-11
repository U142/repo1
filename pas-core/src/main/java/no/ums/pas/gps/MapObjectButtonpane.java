package no.ums.pas.gps;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.send.SendOptionToolbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class MapObjectButtonpane extends DefaultPanel {
	public static final long serialVersionUID = 1;
	MapObjectReg m_reg;
	JButton m_btn_apply = new JButton("Apply");
	JButton m_btn_ok	= new JButton("OK");
	JButton m_btn_cancel= new JButton("Cancel");
	
	
	public MapObjectButtonpane(MapObjectReg reg) {
		super();
		m_reg = reg;
		add_controls();
	}
	
	public void actionPerformed(ActionEvent e) {
		if("act_apply".equals(e.getActionCommand())) {
			save();
		} else if("act_ok".equals(e.getActionCommand())) {
			save();
			m_reg.get_dlg().setVisible(false);
		} else if("act_cancel".equals(e.getActionCommand())) {
			m_reg.undo();
			PAS.get_pas().kickRepaint();
			m_reg.get_dlg().setVisible(false);
		}
	}
	public void add_controls() {
		m_btn_apply.setPreferredSize(new Dimension(SendOptionToolbar.SIZE_BUTTON_XXLARGE, 20));
		m_btn_ok.setPreferredSize(new Dimension(SendOptionToolbar.SIZE_BUTTON_XXLARGE, 20));
		m_btn_cancel.setPreferredSize(new Dimension(SendOptionToolbar.SIZE_BUTTON_XXLARGE, 20));
		set_gridconst(0, 4, 1, 1, GridBagConstraints.SOUTHEAST);
		add(m_btn_apply, m_gridconst);
		set_gridconst(1, 4, 1, 1, GridBagConstraints.SOUTHEAST);
		add(m_btn_ok, m_gridconst);
		set_gridconst(2, 4, 1, 1, GridBagConstraints.SOUTHEAST);
		add(m_btn_cancel, m_gridconst);
		init();

	}
	public void init() {
		m_btn_apply.setActionCommand("act_apply");
		m_btn_ok.setActionCommand("act_ok");
		m_btn_cancel.setActionCommand("act_cancel");
		m_btn_apply.addActionListener(this);
		m_btn_ok.addActionListener(this);
		m_btn_cancel.addActionListener(this);
		doLayout();
		setVisible(true);

	}
	private boolean save() {
		if(m_reg.update_data(true)) {
			PAS.get_pas().get_gpscontroller().start_download(true);
		} else {
			PAS.get_pas().add_event("Error: dataset invalid. Could not save...", null);
		}
		return true;
	}
}