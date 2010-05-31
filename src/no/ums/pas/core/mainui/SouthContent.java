package no.ums.pas.core.mainui;

import java.awt.*;
import java.awt.event.ContainerListener;
import java.awt.event.ContainerEvent;
import javax.swing.*;

import no.ums.pas.*;
import no.ums.pas.ums.errorhandling.Error;

import java.awt.event.*;


public class SouthContent extends JPanel implements ComponentListener {
	public static final long serialVersionUID = 1;
	EventPanel m_eventpanel;
	PAS m_pas;
	private GridBagLayout	m_gridlayout;
	private GridBagConstraints m_gridconst;
	
	PAS get_pas() { return m_pas; }
	EventPanel get_eventpanel() { return m_eventpanel; }

	public SouthContent(PAS pas)
	{
		m_pas = pas;
		m_eventpanel =  new EventPanel(get_pas(), new String[] {"Date/time", "Event"}, 
						new int[] {130, get_pas().get_southheight()}, 
						new Dimension(get_pas().get_mapsize().width, 120));//+ get_pas().get_eastwidth()
		m_gridlayout = new GridBagLayout();//GridLayout(0, 2, 50, 20);
		m_gridconst  = new GridBagConstraints();
		
		setLayout(m_gridlayout);
		this.addContainerListener(new PanelListener(get_pas(), this));
		this.setPreferredSize(new Dimension(get_pas().getWidth() + get_pas().get_eastwidth(), 1)); //143
		prepare_controls();
		add_controls();
	}
	public void resize(Dimension d) {
		revalidate();
		m_eventpanel.revalidate();
		repaint();
	}
	void prepare_controls()
	{
		//m_eventpanel.setPreferredSize(m_eventpanel.get_dimension());
		
	}
	void add_controls()
	{
		set_gridconst(0, 0, 1, 1);
		m_gridlayout.setConstraints(m_eventpanel, m_gridconst);
		//add(m_eventpanel, m_gridconst);
		
	}
	void set_gridconst(int n_x, int n_y, int n_width, int n_height)
	{
		m_gridconst.fill = GridBagConstraints.BOTH;
		m_gridconst.gridx = n_x;
		m_gridconst.gridy = n_y;
		m_gridconst.gridwidth = n_width;
		m_gridconst.gridheight = n_height;
		//m_gridconst.weightx = 0;
		//m_gridconst.weighty = 0;
		//m_gridconst.fill = GridBagConstraints.HORIZONTAL;
		m_gridconst.anchor = GridBagConstraints.SOUTHWEST ;
	}
	public void componentHidden(ComponentEvent e) {
		
	}
	public void componentMoved(ComponentEvent e) {
		
	}
	public void componentResized(ComponentEvent e) {
		revalidate();
	}
	public void componentShown(ComponentEvent e) {
		
	}

}

class PanelListener implements ContainerListener {
	PAS m_pas;
	JPanel m_panel;
	PanelListener(PAS pas, JPanel panel) {
		m_pas = pas;
		m_panel = panel;
	}
	public void componentAdded(ContainerEvent e) {
		if(e.getChild().getClass().toString().equals("SendOptionToolbar")) {
			if(((JToolBar)e.getChild()).getOrientation()==JToolBar.VERTICAL)
				((JToolBar)e.getChild()).setOrientation(JToolBar.HORIZONTAL);
		}
		m_panel.validate();
		m_panel.repaint();
		e.getComponent().repaint();
		try {
			//m_pas.add_event(e.getChild().getClass().toString());
			m_pas.get_southcontent().repaint();
		} catch(Exception ex) {
			Error.getError().addError("SouthContent","Exception in componentAdded",ex,1);
		}
	}
	public void componentRemoved(ContainerEvent e) {
		if(e.getChild().getClass().toString().equals("SendOptionToolbar")) {
			if(((JToolBar)e.getChild()).getOrientation()==JToolBar.VERTICAL)
				((JToolBar)e.getChild()).setOrientation(JToolBar.HORIZONTAL);
		}
		m_panel.validate();
		m_panel.repaint();
		e.getChild().repaint();
		try {
			//m_pas.add_event(e.getChild().getClass().toString());
			m_pas.get_southcontent().repaint();
		} catch(Exception ex) {
			Error.getError().addError("SouthContent","Exception in componentRemoved",ex,1);
		}
		
	}
}