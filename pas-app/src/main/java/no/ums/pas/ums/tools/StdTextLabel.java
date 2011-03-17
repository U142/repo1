package no.ums.pas.ums.tools;

import javax.swing.JLabel;
import java.awt.Dimension;



public class StdTextLabel extends JLabel
{
	public static final long serialVersionUID = 1;
	
	int m_n_items = 0;
	int m_n_itemsize = 17;
	int m_n_height = 15;
	int m_n_width = 100;
	Dimension m_dim = null;
	
	public String toString()
	{
		return getText();
	}
	
	public StdTextLabel()
	{
		super("");
		init(true);
	}
	
	public StdTextLabel(String sz_text)
	{
		super(sz_text);
		init(true);
	}
	public StdTextLabel(String sz_text, boolean bIsHeading)
	{
		super(sz_text);
		init(bIsHeading);
	}
	public StdTextLabel(String sz_text, Dimension d) {
		super(sz_text);
		m_dim = new Dimension(d);
		init(true);
	}
	public StdTextLabel(String sz_text, boolean bIsHeading, Dimension d) {
		super(sz_text);
		m_dim = new Dimension(d);
		init(bIsHeading);
	}
	public StdTextLabel(String sz_text, int n_width) {
		super(sz_text);
		m_dim = new Dimension(n_width, 15);
		init(true);
	}
	public StdTextLabel(String sz_text, int n_fontsize, boolean b_bold)
	{
		super(sz_text);
		setMinimumSize(new Dimension(30, n_fontsize));
		this.setBorder(null);
		//FontSet this.setFont(new Font("Arial", (!b_bold ? Font.PLAIN : Font.BOLD), n_fontsize));
		//this.setFont(PAS.f().getTitleFont());
	}
	public StdTextLabel(String sz_text, int n_width, int n_fontsize, boolean b_bold) {
		super(sz_text);
		m_dim = new Dimension(n_width, n_fontsize + 4);
		init(true, n_fontsize, b_bold);
	}
	public StdTextLabel(String sz_text, boolean bIsHeading, int n_width) {
		super(sz_text);
		m_dim = new Dimension(n_width, 15);
		init(bIsHeading);
	}
	public StdTextLabel(String sz_text, boolean bIsHeading, int n_width, int n_height) {
		super(sz_text);
		m_dim = new Dimension(n_width, n_height);
		init(bIsHeading);
	}
	public void init(boolean bIsHeading) {
		init(bIsHeading, 12, false);
	}
	public void init(boolean bIsHeading, int n_fontsize, boolean b_bold)
	{
		//FontSet this.setFont(new Font("Arial", (!b_bold ? Font.PLAIN : Font.BOLD), n_fontsize));
		//this.setFont(PAS.f().getTitleFont());
		if(m_dim==null)
			m_dim = new Dimension(100, m_n_height);
		this.setPreferredSize(m_dim);
		this.setSize(m_dim);
		//this.setMinimumSize(m_dim);
		m_n_items++;
		if(bIsHeading)
		{
			this.setBorder(null);
		}
		else
		{
		}
	}
	public void set_width(int n) {
		m_n_width = n;
		m_dim = new Dimension(n, m_n_height);
		this.setPreferredSize(m_dim);
		
	}
	public void set_height(int n)
	{
		m_dim = new Dimension(m_n_width, n);
		m_n_height = n;
	}
	
}