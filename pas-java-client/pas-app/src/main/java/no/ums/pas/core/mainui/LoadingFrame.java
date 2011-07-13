package no.ums.pas.core.mainui;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;


public class LoadingFrame extends JDialog {
	
	public static final long serialVersionUID = 1;
	int m_n_items = 0;
	int m_n_currentitem;
	int m_n_overallpercent = 0;
	JProgressBar m_progress = null;
	//LoadingPanel m_progress = null;
	String m_sz_text;
	//public LoadingPanel get_progress() { return m_progress; }
	Dimension m_dim;
	public JProgressBar get_progress() { return m_progress; }
	
	public LoadingFrame(String sz_starttext, Component center)
	{
		super();
		try
		{
/*			//SwingUtilities.invokeAndWait(new Runnable() {
			//	public void run()
				{
					m_progress = new JProgressBar(0, 0) {
						                public boolean  isDisplayable() {
						                    //to get the animation always running
						                    return true;
						                }
							}; 
					//m_progress = new LoadingPanel(sz_text, m_dim);
					//new ProgressBar(0, n_items);
					getContentPane().add(m_progress, BorderLayout.CENTER);
				}*/
			//});
		}
		catch(Exception e)
		{
			
		}
		
		//m_pas = pas;
		int n_width, n_height;
		n_width = 270;
		n_height = 85;
		m_dim = new Dimension(n_width, n_height);
		
		if(center!=null && center.isShowing()) {
				//setBounds(new Rectangle(center.getLocationOnScreen().x + get_pas().get_mappane().get_dimension().width/2 - n_width/2, get_pas().get_mappane().getLocationOnScreen().y + get_pas().get_mappane().get_dimension().height/2 - n_height/2, n_width, n_height));
			setBounds(new Rectangle(center.getLocationOnScreen().x + center.getWidth()/2 - n_width/2, center.getLocationOnScreen().y + center.getHeight()/2 - n_height/2, n_width, n_height));
		}
		else {
				//setBounds(new Rectangle(get_pas().get_mappane().get_dimension().width/2 - n_width/2, get_pas().get_mappane().get_dimension().height/2 - n_height/2, n_width, n_height));
			setBounds(new Rectangle((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - n_width/2, (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - n_height/2, n_width, n_height));
		}				
		m_n_currentitem = 0;
		m_sz_text = sz_starttext;
		initialize();	
		//set_totalitems(0, sz_starttext);
	}
	void set_starttext(String sz_starttext)
	{
		if(m_progress!=null)
		{
			m_progress.setStringPainted(true);
			m_progress.setString(sz_starttext);
		}
		//get_progress().set_starttext(sz_starttext);
	}
	public void set_totalitems(final int n_items, String sz_text) 
	{ 
		if(m_progress==null)
		{
			try
			{
				//SwingUtilities.invokeAndWait(new Runnable() {
				//	public void run()
					{
						m_progress = new JProgressBar(0, n_items) {
							public static final long serialVersionUID = 1;
							                public boolean  isDisplayable() {
							                    //to get the animation always running
							                    return true;
							                }
								};
						//m_progress = new LoadingPanel(sz_text, m_dim);
						//new ProgressBar(0, n_items);
						getContentPane().add(m_progress, BorderLayout.CENTER);
						//FontSet m_progress.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
					}
				//});
			}
			catch(Exception e)
			{
			}
		} else {
			m_progress.setMaximum(n_items);
			//get_progress().set_totalitems(n_items, sz_text);
		}
		//m_progress.setBorderPainted(true);
		m_n_items = n_items;
//		m_sz_text = sz_text;
		set_starttext(sz_text);
		set_indeterminatemode();
	}
	public void set_currentitem(int n_item) {
		set_currentitem(n_item, null);
	}
	public void set_currentitem(int n_item, String sz_text) 
	{
		m_n_currentitem = n_item;
		try
		{
			m_progress.setValue(m_n_currentitem);
			if(sz_text==null)
				m_progress.setString(m_sz_text + " " + Integer.toString((int) (m_progress.getPercentComplete() * 100)) + "%");
			else
				m_progress.setString(m_sz_text + " " + sz_text);
		}
		catch(Exception e)
		{
			
		}
		
		//m_progress.set_currentitem(m_n_currentitem);
		//m_progress.set_text(m_sz_text + " " + new Integer((int)(m_progress.get_progress().getPercentComplete()*100)).toString() + "%");
	}
	public void inc_currentitem() 
	{ 
		m_n_currentitem++; 
		set_currentitem(m_n_currentitem);
	}
	void initialize()
	{
		getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		setUndecorated(true);
		getContentPane().setLayout(new BorderLayout ());//setWindowDecorationStyle(JRootPane.FRAME);
	}
	void set_indeterminatemode()
	{
		try
		{
			if(m_n_items==0)
			{
				if(!m_progress.isIndeterminate())
					m_progress.setIndeterminate(true);
				//m_progress.startAnimationTimer();
			}
			else
				m_progress.setIndeterminate(false);	
		}
		catch(Exception e)
		{
			
		}
	}
	public void start_and_show()
	{
		try {
			if(m_progress==null)
				return;
			setVisible(true);
			set_totalitems(m_n_items, m_sz_text);
			set_indeterminatemode();
			m_progress.setVisible(true);
			setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(250, 400));
			
			setAlwaysOnTop(true);
		} catch(SecurityException e) {
			//Error.getError().addError("LoadingFrame","SecurityException in start_and_show",e,1);
			//get_pas().add_event("setAlwaysOnTop() : Security exception");
		}
		
	}
	public void stop_and_hide()
	{
		try
		{
			if(m_progress==null)
				return;
				{
					m_progress.setVisible(false);
					setVisible(false);
				}
		}
		catch(Exception e)
		{
			
		}
		//m_progress = null;
	}
	
}