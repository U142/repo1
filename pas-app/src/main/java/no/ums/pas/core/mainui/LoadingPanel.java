package no.ums.pas.core.mainui;

import no.ums.pas.ums.tools.StdTextLabel;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;



public class LoadingPanel extends JPanel implements ComponentListener {
	//PAS m_pas;
	public static final long serialVersionUID = 1;
	int m_n_items = 0;
	int m_n_currentitem;
	double m_f_currentoverallitem;
	int m_f_overallstage = 0;
	JProgressBar m_progress = null;
	JProgressBar m_progressoverall = null;
	Dimension m_dim;
	private GridBagLayout	m_gridlayout;
	private GridBagConstraints m_gridconst;          
	private int m_n_padding = 5;
	public Dimension get_dimension() { return m_dim ;}
	public JProgressBar get_progress() { return m_progress; }
	private Timer taskTimer = null;
	boolean use_timer = false;
	boolean show_remaining_time = false;
		
	String m_sz_text;
	String m_sz_starttext;
	
	String sz_timeleft = null;
	StdTextLabel lbl_timeleft = new StdTextLabel("", 8, true); 
	
	public LoadingPanel(String sz_starttext, Dimension d, int padding, boolean show_remaining_time)
	{
        //super(new GridLayout(1,2));
		super();
		this.show_remaining_time = show_remaining_time;
        m_gridlayout = new GridBagLayout();//GridLayout(0, 2, 50, 20);
		m_gridconst  = new GridBagConstraints();
		setLayout(m_gridlayout);
		
		//m_pas = pas;
		/*if(d==null)
			m_dim = new Dimension(m_pas.get_eastwidth()/2, 20);
		else*/
			m_dim = d;
		setBounds(0,0,m_dim.width, m_dim.height);
		
		m_n_currentitem = 0;
		m_sz_text = sz_starttext;
		m_sz_starttext = sz_starttext;
		m_n_padding = padding;
		lbl_timeleft.setHorizontalAlignment(SwingConstants.CENTER);

		initialize();	
		addComponentListener(this);

	}
	
	public void doTimeCalc()
	{
		long currentTime = System.currentTimeMillis();
		long elapsedMillis = (currentTime - taskStartTime);
		double percentValue = m_progress.getPercentComplete()*100.0;
		long totalMillis =  (long)
		    	(elapsedMillis / (((double)percentValue)/100.0));
		long remainingMillis = totalMillis - elapsedMillis;
		int remainingSeconds = (int)remainingMillis/1000;
		sz_timeleft = no.ums.pas.ums.tools.TextFormat.format_seconds_to_hms(remainingSeconds);
		lbl_timeleft.setText(sz_timeleft);	
		if(percentValue>=100.0)
			stopTimer();
	}
	long taskStartTime = System.currentTimeMillis();
	
	public void initTimer(boolean b_use_timer)
	{
		taskStartTime = System.currentTimeMillis();
		if(b_use_timer)
		{
			taskTimer = new Timer(1000, new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					doTimeCalc();
				}
			});
			taskTimer.start();
		}
		if(show_remaining_time)
		{
			set_gridconst(0, 1, 1, 1);
			add(lbl_timeleft, m_gridconst);
		}
		use_timer = true;
	}
	public void stopTimer()
	{
		try
		{
			if(taskTimer!=null)
				taskTimer.stop();
			lbl_timeleft.setVisible(false);
			use_timer = false;
		}
		catch(Exception e)
		{
			
		}
	}
	
	public LoadingPanel(String sz_starttext, Dimension d, boolean show_remaining_time) //, JFrame parent_frame)
	{
		this(sz_starttext, d, 5, show_remaining_time);
	}
	public void set_starttext(String sz_starttext)
	{
		m_progress.setStringPainted(true);
		m_progress.setString(sz_starttext);	
		m_progressoverall.setStringPainted(false);
		//m_progressoverall.setBackground(Color.GREEN);
		m_progressoverall.setForeground(Color.GREEN);
		//m_progressoverall.setString(sz_starttext);
	}
	String get_starttext() { return m_sz_starttext; }
	public void set_text(String sz_text) {
		m_sz_text = sz_text;
		m_progress.setString(sz_text);
	}
	
	public void set_totalitems(int n_items, String sz_text) 
	{ 
		m_n_currentitem = 0;
		m_progress.setMaximum(n_items);
		m_progressoverall.setMaximum(100);
		m_progress.setBorderPainted(true);
		m_progressoverall.setBorderPainted(true);
		m_n_items = n_items;
		m_sz_text = sz_text;
		set_starttext(sz_text);
		set_indeterminatemode();
	}
	public void set_currentitem(int n_item) 
	{
		m_n_currentitem = n_item;
		m_progress.setValue(m_n_currentitem);
		m_progress.setString(m_sz_text + " " + Integer.toString((int) (m_progress.getPercentComplete() * 100)) + "%");
		try
		{
			if(m_n_currentitem==m_n_items)
				stopTimer();
		}
		catch(Exception e)
		{
			
		}
	}
	public void set_currentoverallitem(double f_item)
	{
		m_f_currentoverallitem = (double)f_item;
		m_progressoverall.setValue((int)((double)m_f_overallstage + m_f_currentoverallitem));
	}
	public void inc_currentoverallitem(double f_item)
	{
		m_f_currentoverallitem += f_item;
		//m_n_overallstage += f_item;
		m_progressoverall.setValue((int)((double)m_f_overallstage + f_item));
		lock_overallstage();
	}
	public void lock_overallstage()
	{
		m_f_overallstage += m_f_currentoverallitem;
		//m_n_overallstage = m_f_currentoverallitem;
		m_f_currentoverallitem = 0;
	}
	void reset_overallstage()
	{
		m_f_overallstage = 0;
	}
	public void inc_currentitem() 
	{ 
		m_n_currentitem++; 
		set_currentitem(m_n_currentitem);
	}
	void initialize()
	{
		m_progress = new JProgressBar(0, 1) {
			public static final long serialVersionUID = 1;
            public boolean  isDisplayable() {
                //to get the animation always running
                return true;
            }
		};
		m_progressoverall = new JProgressBar(0, 1) {
			public static final long serialVersionUID = 1;
            public boolean  isDisplayable() {
                //to get the animation always running
                return true;
            }
		};		
		set_gridconst(0, 0, 1, 1);		
		add(m_progress, m_gridconst);
		//m_gridlayout.setConstraints(m_progress, m_gridconst);
		//set_gridconst(0, 1, 1, 1);
		//add(m_progressoverall);
		//m_gridlayout.setConstraints(m_progressoverall, m_gridconst);
		
		m_progress.setBounds(0,0,m_dim.width, m_dim.height - m_n_padding);
		m_progressoverall.setBounds(0,0,m_dim.width, m_n_padding);
		m_progress.setPreferredSize(new Dimension(m_dim.width, m_dim.height - m_n_padding));
		m_progressoverall.setPreferredSize(new Dimension(m_dim.width, m_n_padding));
		lbl_timeleft.setPreferredSize(new Dimension(m_dim.width, 40));
		this.setPreferredSize(m_dim);
		//setVisible(true);
		show_progress();
	}
	void set_indeterminatemode()
	{
		if(m_n_items==0)
		{
			if(!m_progress.isIndeterminate())
				m_progress.setIndeterminate(true);
		}
		else
			m_progress.setIndeterminate(false);	
	}
	void show_progress()
	{
		setVisible(true);
		set_totalitems(1, m_sz_text);
		m_progress.setValue(0);
		m_progress.setVisible(true);
		m_progressoverall.setValue(0);
		m_progressoverall.setVisible(true);
	}
	void start_and_show()
	{
		show_progress();
		set_indeterminatemode();
	}
	void stop_and_hide()
	{
		m_progress.setVisible(false);
		m_progressoverall.setVisible(false);
		setVisible(false);
	}
	public void start_progress(int n_items, String sz_text)
	{
		m_n_items = n_items;
		set_totalitems(n_items, sz_text);
	}
	public void reset_progress()
	{
		set_currentitem(0);
		set_totalitems(1, get_starttext());
		m_f_overallstage = 0;
		set_currentoverallitem(0);
	}
	void set_gridconst(int n_x, int n_y, int n_width, int n_height)
	{
		m_gridconst.gridx = n_x;
		m_gridconst.gridy = n_y;
		m_gridconst.gridwidth = n_width;
		m_gridconst.gridheight = n_height;
		m_gridconst.anchor = GridBagConstraints.NORTH;
	}
	@Override
	public void setPreferredSize(Dimension d)
	{
		if(d.height<=0)
			d.height = m_dim.height;
		super.setPreferredSize(d);
	}
	public void componentResized(ComponentEvent e) {
		if(getWidth()<=0 || getHeight()<=0)
			return;

		int height = getHeight();
		m_progress.setPreferredSize(new Dimension(getWidth(), getHeight() - (show_remaining_time ? 15 : m_n_padding)));
		if(show_remaining_time)
			lbl_timeleft.setPreferredSize(new Dimension(getWidth(), 15));
		m_progressoverall.setPreferredSize(new Dimension(getWidth(), 5));
		m_progress.revalidate();
		m_progressoverall.revalidate();
		lbl_timeleft.revalidate();
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }		
}