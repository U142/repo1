package no.ums.pas.plugins.centric.status;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JTabbedPane;

import no.ums.pas.core.defines.DefaultPanel;

public class CentricMessages extends DefaultPanel implements ComponentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane m_tabbed_messages = new JTabbedPane();
	public JTabbedPane get_tpane() { return m_tabbed_messages; }
	private CentricStatus m_parent;
	public CentricStatus get_parent() { return m_parent; }
	
	public CentricMessages(CentricStatus parent) { // Sende med status ting
		super();
		m_parent = parent;
		add_controls();
		//m_tabbed_messages.add("Message1", new CentricMessageStatus(this));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public void add_controls() {
				
		//m_gridconst.fill = GridBagConstraints.BOTH;
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_tabbed_messages, m_gridconst);
		
		m_tabbed_messages.setPreferredSize(new Dimension(m_parent.getPreferredSize().width-15, m_parent.getPreferredSize().height/2));
		setPreferredSize(new Dimension(m_parent.getPreferredSize().width-10, m_parent.getPreferredSize().height-70));
		
		this.revalidate();
		repaint();
		init();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		super.componentResized(e);
		m_tabbed_messages.setPreferredSize(new Dimension(m_parent.getPreferredSize().width-15, m_parent.getPreferredSize().height/2));
		setPreferredSize(new Dimension(m_parent.getPreferredSize().width-10, m_parent.getPreferredSize().height-70));
	}

	@Override
	public void init() {
		setVisible(true);
	}
	
}