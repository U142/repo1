package no.ums.pas.plugins.centric.status;

import no.ums.pas.core.defines.DefaultPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class CentricMessages extends DefaultPanel implements ComponentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane m_tabbed_messages = new JTabbedPane();
	public JTabbedPane get_tpane() { return m_tabbed_messages; }
	private CentricStatus m_parent;
	public CentricStatus get_parent() { return m_parent; }
	public CentricMessageStatus getSelectedTab() { 
		return (CentricMessageStatus)get_tpane().getSelectedComponent(); 
	}
	
	public CentricMessages(CentricStatus parent) { // Sende med status ting
		super();
		m_tabbed_messages.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				tabChanged();
			}
		});
		m_parent = parent;
		add_controls();
		addComponentListener(this);
		//m_tabbed_messages.add("Message1", new CentricMessageStatus(this));
	}
	
	protected void tabChanged()
	{
		m_parent.tabChanged();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public void add_controls() {
				
		//m_gridconst.fill = GridBagConstraints.BOTH;
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_tabbed_messages, m_gridconst);
		
		m_tabbed_messages.setPreferredSize(new Dimension(m_parent.getPreferredSize().width-15, m_parent.getPreferredSize().height-100));
		setPreferredSize(new Dimension(m_parent.getPreferredSize().width-10, m_parent.getPreferredSize().height-90));

		this.revalidate();
		repaint();
		init();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if(getWidth()<=0 || getHeight()<=0)
			return;
		super.componentResized(e);
		//m_tabbed_messages.setPreferredSize(new Dimension(m_parent.getPreferredSize().width-15, m_parent.getPreferredSize().height/2));
		//setPreferredSize(new Dimension(m_parent.getPreferredSize().width-10, m_parent.getPreferredSize().height-70));
		Rectangle rect = new Rectangle();
		m_tabbed_messages.computeVisibleRect(rect);
		Dimension minimumsize = CentricMessageStatus.getCentricMinimumSize();
		int height = minimumsize.height;
		int tabheight = 0;
		m_tabbed_messages.setPreferredSize(new Dimension(m_parent.getPreferredSize().width-15, m_parent.getPreferredSize().height-100+tabheight));
		setPreferredSize(new Dimension(m_parent.getPreferredSize().width-10, m_parent.getPreferredSize().height-90+tabheight));
		//m_tabbed_messages.setPreferredSize(new Dimension(m_parent.getPreferredSize().width-15, height+tabheight));
		//setPreferredSize(new Dimension(m_parent.getPreferredSize().width-10, m_parent.getPreferredSize().height+tabheight));
		revalidate();
	}

	@Override
	public void init() {
		setVisible(true);
	}
	
}