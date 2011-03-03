package no.ums.pas.core.defines;

import no.ums.pas.ums.tools.StdTextLabel;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public abstract class DefaultPanel extends JPanel implements ActionListener, ComponentListener, IWantedSizing {
	public static final int DIR_HORIZONTAL	= 0;
	public static final int DIR_VERTICAL	= 1;
	public static boolean ENABLE_GRID_DEBUG = false;
	

	public void componentHidden(ComponentEvent e)
	{
		
	}
	public void componentMoved(ComponentEvent e)
	{
	
	}
	public void componentResized(ComponentEvent e)
	{
	}
	public void componentShown(ComponentEvent e)
	{
		
	}
	

	@Override
	public int getWantedHeight() {
		return getPreferredSize().height;
	}
	@Override
	public int getWantedWidth() {
		return getPreferredSize().width;
	}


	int m_n_panels = 0;
	int m_n_xpanels = 0;
	public void reset_panels() { m_n_panels = 0; }
	public void reset_xpanels() { m_n_xpanels = 0; }
	public int inc_panels() { m_n_panels++; return m_n_panels; }
	public int get_panel() { return m_n_panels; }
	public int inc_xpanels() { m_n_xpanels++; return m_n_xpanels; }
	public int inc_xpanels2() { return inc_xpanels2(1); }
	public int inc_xpanels2(int n) { m_n_xpanels+=n; set_gridconst(m_n_xpanels, m_n_panels, 1, 1); return m_n_xpanels; }
	public int inc_panels2() { reset_xpanels(); m_n_panels++; set_gridconst(m_n_xpanels, m_n_panels, 1, 1); return m_n_panels; }
	public int get_xpanel() { return m_n_xpanels; }
	//private JTextArea m_txt_spacing_horizontal = new JTextArea("");
	private StdTextLabel m_txt_spacing   = new StdTextLabel("");
	
	public DefaultPanel()
	{
		super();
		m_gridlayout= new GridBagLayout();
		m_gridconst	= new GridBagConstraints();
		try {
			setLayout(m_gridlayout);
			setMinimumSize(new java.awt.Dimension(Integer.MIN_VALUE, Integer.MIN_VALUE));
			setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/*public DefaultPanel() {
		super();
		m_gridlayout= new GridBagLayout();
		m_gridconst	= new GridBagConstraints();
		setLayout(m_gridlayout);
		setMinimumSize(new java.awt.Dimension(Integer.MIN_VALUE, Integer.MIN_VALUE));
		setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}*/
	
	
	protected static final String DISABLE = "disable";
	protected static final String ENABLE = "enable";	
	public final GridBagLayout	m_gridlayout;
	public final GridBagConstraints m_gridconst;
	
	public final GridBagLayout get_gridlayout() { return m_gridlayout; }
	public final GridBagConstraints get_gridconst() { return m_gridconst; }
	
	public void add_spacing(int DIRECTION, int d) {
		/*if(DIRECTION==DIR_HORIZONTAL)
			m_txt_spacing.setPreferredSize(new Dimension(d, 1));
		else if(DIRECTION==DIR_VERTICAL)
			m_txt_spacing.setPreferredSize(new Dimension(1, d));
		set_gridconst(0, inc_panels(), 1, 1);
		add(new StdTextLabel(" "), m_gridconst);*/
		if(DIRECTION==DIR_HORIZONTAL) {
			set_gridconst(inc_xpanels(), get_panel(), 1, 1);
			StdTextLabel space = new StdTextLabel("", new Dimension(d, 3));
			if(ENABLE_GRID_DEBUG)
				space.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(""));
			add(space, get_gridconst());
		} else {
			set_gridconst(get_xpanel(), inc_panels(), 1, 1);
			StdTextLabel space = new StdTextLabel(" ", new Dimension(5, d));
			if(ENABLE_GRID_DEBUG)
				space.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(""));
			add(space, get_gridconst());
		}
	}
	public final void set_gridconst(int n_x, int n_y, int n_width, int n_height, int anc) {
		//m_gridconst.fill = GridBagConstraints.BOTH;
		m_gridconst.gridx = n_x; //GridBagConstraints.RELATIVE;
		m_gridconst.gridy = n_y;
		m_gridconst.gridwidth = n_width;
		m_gridconst.gridheight = n_height;
		//m_gridconst.fill = GridBagConstraints.BOTH;
		m_gridconst.anchor = anc; //GridBagConstraints.WEST;
	}
	public final void set_gridconst(int n_x, int n_y, int n_width, int n_height) {
		set_gridconst(n_x, n_y, n_width, n_height, GridBagConstraints.FIRST_LINE_START);
	}	
	public abstract void add_controls();
	public abstract void init();
	public abstract void actionPerformed(ActionEvent e);

	
}