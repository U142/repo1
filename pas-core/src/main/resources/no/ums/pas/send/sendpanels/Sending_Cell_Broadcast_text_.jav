package Send.SendPanels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JTextArea;

import Core.Defines.DefaultPanel;
import PAS.PAS;
import UMS.Tools.StdTextArea;
import UMS.Tools.StdTextLabel;

public class Sending_Cell_Broadcast_text extends DefaultPanel implements ActionListener, KeyListener {

	private final int m_maxSize = 160;
	
	protected SendWindow parent = null;
	public SendWindow get_parent() { return parent; }
	protected StdTextLabel m_lbl_localtext = new StdTextLabel("Local language: ");
	protected StdTextLabel m_lbl_internationaltext = new StdTextLabel("International language: ");
	protected StdTextLabel m_lbl_area = new StdTextLabel("Area: ");
	protected StdTextLabel m_lbl_localsize = new StdTextLabel("(0 of 160)");
	protected StdTextLabel m_lbl_internationalsize = new StdTextLabel("(0 of 160)");
	
	protected ScrollPane local_scroll;
	protected JTextArea m_txt_localtext;
	protected JTextArea m_txt_internationaltext;
	protected JComboBox m_combo_area;
	
	public Sending_Cell_Broadcast_text(PAS pas, SendWindow parentwin) {
		super(pas);
		parent = parentwin;
		m_lbl_localtext.setPreferredSize(new Dimension(150, 70));
		m_lbl_localtext.setAlignmentX(StdTextLabel.TOP_ALIGNMENT);
		m_txt_localtext = new JTextArea(3,20);
		m_txt_localtext.setLineWrap(true);
		m_txt_localtext.setPreferredSize(new Dimension(200, 70));
		m_txt_localtext.addKeyListener(this);
		
		m_lbl_internationaltext.setPreferredSize(new Dimension(150, 70));
		m_lbl_internationaltext.setAlignmentX(StdTextLabel.TOP_ALIGNMENT);
		m_txt_internationaltext = new JTextArea(3,20);
		m_txt_internationaltext.setLineWrap(true);
		m_txt_internationaltext.setPreferredSize(new Dimension(200, 70));
		m_txt_internationaltext.addKeyListener(this);
		
		m_lbl_area.setPreferredSize(new Dimension(150, 20));
		m_combo_area = new JComboBox();
		m_combo_area.setPreferredSize(new Dimension(200, 20));
		
		add_controls();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(get_parent().get_bgimg()!=null)
			g.drawImage(get_parent().get_bgimg(),0,0,getWidth(),getHeight(),this);
	}
	
	public void add_controls() {
		int n_width = 15;
		set_gridconst(0, inc_panels(), n_width/3, 1, GridBagConstraints.WEST);
		add(m_lbl_localtext, m_gridconst);
		set_gridconst(n_width/3, get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		add(m_txt_localtext, m_gridconst);
		set_gridconst(n_width/3 + n_width/3, get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		add(m_lbl_localsize, m_gridconst);
		
		set_gridconst(0, inc_panels(), n_width/3, 1, GridBagConstraints.WEST);
		add(m_lbl_internationaltext, m_gridconst);
		set_gridconst(n_width/3, get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		add(m_txt_internationaltext, m_gridconst);
		set_gridconst(n_width/3 + n_width/3, get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		add(m_lbl_internationalsize, m_gridconst);
		
		set_gridconst(0, inc_panels(), n_width/3, 1, GridBagConstraints.WEST);
		add(m_lbl_area, m_gridconst);
		set_gridconst(n_width/2, get_panel(), n_width/3, 1, GridBagConstraints.WEST);
		add(m_combo_area, m_gridconst);

		init();
	}
	public void init() {
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == m_txt_localtext && m_txt_localtext.getText().length() >= m_maxSize) {
			m_txt_localtext.setText(m_txt_localtext.getText().substring(0,159));
		
		} else if (e.getSource() == m_txt_localtext)
			m_lbl_localsize.setText((m_txt_localtext.getText().length() + 1) + " of " + m_maxSize);
		
		if(e.getSource() == m_txt_internationaltext && m_txt_internationaltext.getText().length() >= m_maxSize) {
			m_txt_internationaltext.setText(m_txt_internationaltext.getText().substring(0,159));
		}  else if (e.getSource() == m_txt_internationaltext)
			m_lbl_internationalsize.setText((m_txt_internationaltext.getText().length() + 1) + " of " + m_maxSize);
			
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
