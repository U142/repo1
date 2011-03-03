package no.ums.pas.ums.tools;

import javax.swing.JTextField;
import java.awt.Dimension;

public class StdTextArea extends JTextField// JTextArea
{
	@Override
	public void copy() {
		if(b_enable_copy)
			super.copy();
	}

	@Override
	public void paste() {
		if(b_enable_paste)
			super.paste();
	}
	public static final long serialVersionUID = 1;
	int m_n_items = 0;
	int m_n_itemsize = 17;
	Dimension m_dim = null;
	protected boolean b_enable_copy = true;
	protected boolean b_enable_paste = true;
	
	public void setEnableCopy(boolean b)
	{
		b_enable_copy = b;
	}
	public void setEnablePaste(boolean b)
	{
		b_enable_paste = b;
	}
	
	public String toString()
	{
		return getText();
	}
	
	public StdTextArea(String sz_text)
	{
		super(sz_text);
		init(true);
	}
	public StdTextArea(String sz_text, boolean bIsHeading)
	{
		super(sz_text);
		init(bIsHeading);
	}
	public StdTextArea(String sz_text, Dimension d) {
		super(sz_text);
		m_dim = new Dimension(d);
		init(true);
	}
	public StdTextArea(String sz_text, boolean bIsHeading, Dimension d) {
		super(sz_text);
		m_dim = new Dimension(d);
		init(bIsHeading);
	}
	public StdTextArea(String sz_text, int n_width) {
		super(sz_text);
		m_dim = new Dimension(n_width, 15);
		init(true);
	}
	public StdTextArea(String sz_text, int n_width, int n_fontsize, boolean b_bold) {
		super(sz_text);
		m_dim = new Dimension(n_width, n_fontsize + 4);
		init(true, n_fontsize, b_bold);
	}
	public StdTextArea(String sz_text, boolean bIsHeading, int n_width) {
		super(sz_text);
		m_dim = new Dimension(n_width, 15);
		init(bIsHeading);
	}
	public void init(boolean bIsHeading) {
		init(bIsHeading, 12, false);
	}
	public void init(boolean bIsHeading, int n_fontsize, boolean b_bold)
	{
		//FontSet this.setFont(new Font("Arial", (!b_bold ? Font.PLAIN : Font.BOLD), n_fontsize));
		//this.setFont(PAS.f().getControlFont());
		if(m_dim==null)
			m_dim = new Dimension(100, 15);
		this.setPreferredSize(m_dim);
		//this.setLineWrap(true);
		//this.setWrapStyleWord(bIsHeading);
		this.setEditable(!bIsHeading);
       		//this.setBounds(20, 10 + m_n_items * m_n_itemsize, 50, m_n_itemsize);
		m_n_items++;
		if(bIsHeading)
		{
			//this.setBackground(Color.lightGray);
			//this.setBackground(SubstanceLookAndFeel.);
			//this.setBackground(SubstanceLookAndFeel.getMenuBackground());
			//this.setBackground(SubstanceLookAndFeel.getCurrentTheme().getMenuBackground());
			this.setBorder(null);
		}
		else
		{
			
			//this.setLayout(PAS.get_pas().getLayout());
			//this.setBorder(new Border());
		}
	}
}