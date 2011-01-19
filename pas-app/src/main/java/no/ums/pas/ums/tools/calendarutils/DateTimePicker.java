package no.ums.pas.ums.tools.calendarutils;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.ums.tools.calendarutils.DateTimePicker.DatePicker.DayPanel.DayButton;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;




public class DateTimePicker extends DefaultPanel {
	public static final long serialVersionUID = 1;
	DatePicker m_datepicker;
	ActionListener callback;
	boolean b_show_timefield = true;
	int n_init_days_before = 0;
	int n_init_months_before = 0;
	int n_init_years_before = 0;
	long mask_dates_after;
	
	public enum MASK_DATES
	{
		BEFORE,
		AFTER,
		NONE,
	}
	
	class DateLabel extends Object
	{
		String str;
		int n_no;
		boolean b_enabled = true;
		public void setSelectable(boolean b) { 
			b_enabled = b; 
		}
		public boolean getSelectable() { return b_enabled; }
		public int getNumber() { return n_no; }
		public String getText() { return str; }	
		@Override
		public String toString()
		{
			return str;
		}
	}
	class YearLabel extends DateLabel
	{
		public YearLabel(int year)
		{
			super();
			n_no = year;
			str = new Integer(year).toString();
		}
		
	}
	
	class MonthLabel extends DateLabel
	{
		MonthLabel(int month)
		{
			n_no = month;
			str = formatMonth(month);
		}
	}
	class DateComboRenderer extends JLabel implements ListCellRenderer
	{
	    public Component getListCellRendererComponent(JList list,  
                Object value,  
                int index,  
                boolean isSelected,  
                boolean cellHasFocus) {
	    	DateLabel m = (DateLabel)value;
			setText(m.getText());
			setFocusable(m.getSelectable());
			setEnabled(m.getSelectable());
			setVisible(m.getSelectable());
			return this;
		} 		
	}
	
	public void setMaskDates(long c, MASK_DATES when)
	{
		mask_dates_after = c;
		m_datepicker.maskDates(when);
	}
	
	public DateTimePicker(ActionListener callback) {
		super();
		this.callback = callback;
		m_datepicker = new DatePicker(callback, this);
		//setPreferredSize(new Dimension(240, 250));
		add_controls();
	}
	public DateTimePicker(ActionListener callback, boolean b_timefield, int daysbeforenow, int monthsbeforenow, int yearsbeforenow) {
		super();
		n_init_days_before = daysbeforenow;
		n_init_months_before = monthsbeforenow;
		n_init_years_before = yearsbeforenow;
		b_show_timefield = b_timefield;
		this.callback = callback;
		m_datepicker = new DatePicker(callback, this);
		//setPreferredSize(new Dimension(240, 150));
		add_controls();
	}

	public void add_controls() {
		set_gridconst(0, 0, 1, 1, GridBagConstraints.CENTER);
		add(m_datepicker);
		init();
	}
	public void init() {
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		
	}
	public long get_datetime() { return m_datepicker.get_datetime(); }
	public long get_date() { 
		Calendar c = Calendar.getInstance();
		c.set(m_datepicker.m_n_year, m_datepicker.m_n_month, m_datepicker.m_n_day);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String d = format.format(c.getTime());
		return Long.parseLong(d);
	}
	public long get_datetime_beginning_of_day() {
		long l = get_date();
		String s = String.format("%d000000", l);
		return Long.parseLong(s);
	}
	public long get_datetime_ending_of_day() {
		long l = get_date();
		String s = String.format("%d235959", l);
		return Long.parseLong(s);
	}
	public void setSelectedDateDaysBeforeNow(int n)
	{
		
	}
	
	public synchronized static String formatMonth(int month)
	{
		DateFormatSymbols symbols = new DateFormatSymbols(PAS.locale);
		String[] monthNames = symbols.getMonths();
		return monthNames[month].toString().substring(0,1).toUpperCase() + monthNames[month].toString().substring(1);
	}
	public synchronized static String dayLetter(int day, int letters)
	{
		String ret = "";
		DateFormatSymbols symbols = new DateFormatSymbols(PAS.locale);
		String [] dayNames = symbols.getWeekdays();
		ret = dayNames[day].toString().substring(0, 1).toUpperCase();
		if(letters>1)
			ret+= dayNames[day].toString().substring(1, letters);
		return ret;
	}
	
	public Calendar getCalendar()
	{
		return m_datepicker.m_calendar;
	}
	
	public class DatePicker extends DefaultPanel {
		public static final long serialVersionUID = 1;
		private Calendar m_now;
		private int m_n_year = 0;
		private int m_n_month = 0;
		private int m_n_day = 0;
		private int m_n_min_day = 0;
		private int m_n_max_day = 0;
		private int m_n_hour = -1;
		private int m_n_minute = -1;
		private Calendar m_calendar;
		private DateTimePicker m_dt_picker;
		JComboBox m_combo_year;
		JComboBox m_combo_month;
		DayPanel m_daypanel;
		JTextArea m_txt_time;
		
		public DatePicker(ActionListener callback, DateTimePicker dt_picker) {
			super();
			Border border = BorderFactory.createLineBorder(Color.black, 1);
			setBorder(border);
			m_calendar = Calendar.getInstance();
			set_now();
			m_calendar.setFirstDayOfWeek(Calendar.MONDAY);
			m_n_year = m_calendar.get(Calendar.YEAR);
			m_n_month = m_calendar.get(Calendar.MONTH);
			m_n_day = m_calendar.get(Calendar.DAY_OF_MONTH);
			m_n_hour = m_calendar.get(Calendar.HOUR_OF_DAY);
			m_n_minute = m_calendar.get(Calendar.MINUTE);
			m_calendar.clear();
			calc_new_minmax();
			m_combo_year = new JComboBox(new YearLabel [] { new YearLabel(m_n_year), new YearLabel(m_n_year-1), new YearLabel(m_n_year-2) } );
			//m_combo_month = new JComboBox(new String[] { "January", "February", "Mars", "April", "May", "June", "July", "August", "September", "October", "November", "December" } );
			Calendar cal = Calendar.getInstance();
			MonthLabel months [] = new MonthLabel[12];
			for(int i=0; i < months.length; i++)
				months[i] = new MonthLabel(i);
			
			m_combo_month = new JComboBox(months);
			m_combo_month.setRenderer(new DateComboRenderer());
			m_combo_year.setRenderer(new DateComboRenderer());
			m_combo_year.setSelectedIndex(0);
			m_combo_month.setSelectedIndex(m_n_month);
			m_combo_year.addActionListener(this);
			m_combo_month.addActionListener(this);
			m_combo_year.setActionCommand("act_year_change");
			m_combo_month.setActionCommand("act_month_change");
			//m_combo_year.setPreferredSize(new Dimension(75, 16));
			m_combo_month.setPreferredSize(new Dimension(100, 16));
			Font f = new Font(null, Font.BOLD, 10);
			m_combo_year.setFont(f);
			m_combo_month.setFont(f);
			m_daypanel = new DayPanel(callback, this);
			m_txt_time = new JTextArea();
			m_txt_time.setPreferredSize(new Dimension(45, 16));
			add_controls();
		}
		/** mask dates after the set mask_dates_after*/
		public void maskDates(MASK_DATES mask)
		{
			//if current date>filterdate then set current date=filterdate
			//System.out.println("Current = " + DateFormat.getInstance().format(m_calendar.getTime()) + " Compare=" + DateFormat.getInstance().format(mask_dates_after.getTime()));
			//if(m_calendar.before(mask_dates_after))
			System.out.println("Current=" + get_date() + " compare=" + mask_dates_after);
			
			//make sure from and to dates are equal if there's a logic mismatch
			/*if((mask==MASK_DATES.AFTER && get_date()>mask_dates_after) || mask==MASK_DATES.BEFORE && get_date()<mask_dates_after)
			{
				int curyear = m_n_year;
				set_datetime(mask_dates_after);
				calc_new_minmax();
				//m_daypanel.recalc();
				m_combo_month.setSelectedIndex(m_n_month);
				m_combo_year.setSelectedIndex(0);
				m_daypanel.m_btn_days[m_n_day].doClick();
				//callback.actionPerformed(new ActionEvent(DateTimePicker.this, ActionEvent.ACTION_PERFORMED, "act_datetime_changed"));
			}*/
			String mask_year = new Long(mask_dates_after).toString().substring(0, 4);
			String mask_month = new Long(mask_dates_after).toString().substring(4, 6);
			String mask_day = new Long(mask_dates_after).toString().substring(6, 8);
			int n_mask_year = new Integer(mask_year);
			int n_mask_month = new Integer(mask_month);
			int n_mask_day = new Integer(mask_day);
			maskYears(n_mask_year, mask);
			if(mask==MASK_DATES.AFTER)
			{
				//mask all years, months, and days after
				if(n_mask_year<m_n_year) //we're at least one year earlier, no need to mask months nor days
				{
					maskMonths(0, MASK_DATES.NONE);
					maskDays(0, MASK_DATES.NONE);
				}
				else //we're at the same year, mask months
				{
					maskMonths(n_mask_month, mask);
					if(n_mask_month-1<m_n_month) //we're at least one month earlier, no need to mask days
					{
						maskDays(0, MASK_DATES.NONE);					
					}
					else
					{
						if(n_mask_day>m_n_day)
						{
							maskDays(0, MASK_DATES.NONE);
							//m_daypanel.m_btn_days[n_mask_day-1].doClick();
						}
						maskDays(n_mask_day, mask);
					}
				}
			}
			else if(mask==MASK_DATES.BEFORE)
			{
				//mask all years, months, and days later
				if(n_mask_year>m_n_year) //we're at least one year earlier, no need to mask months nor days
				{
					maskMonths(0, MASK_DATES.NONE);
					maskDays(0, MASK_DATES.NONE);
				}
				else
				{
					maskMonths(n_mask_month, mask);
					if(n_mask_month-1>m_n_month) //we're at least one month later, no need to mask days
					{
						maskDays(0, MASK_DATES.NONE);
					}
					else
					{
						/*if(n_mask_day<m_n_day)
						{
							maskDays(0, MASK_DATES.NONE);
							//m_daypanel.m_btn_days[n_mask_day].doClick();
						}*/
						maskDays(n_mask_day, mask);
					}
				}
			}
		}
		private void maskMonths(int month, MASK_DATES when)
		{
			for(int i=0; i < m_combo_month.getItemCount(); i++)
			{
				int cur_month = i+1;
				MonthLabel lbl_month = (MonthLabel)m_combo_month.getItemAt(i);
				if(when==MASK_DATES.NONE)
				{
					lbl_month.setSelectable(true);
				}
				else if(when==MASK_DATES.BEFORE)
				{
					lbl_month.setSelectable(cur_month <= month);
				}
				else if(when==MASK_DATES.AFTER)
				{
					lbl_month.setSelectable(cur_month >= month);					
				}
			}
		}
		private void maskYears(int year, MASK_DATES when)
		{
			for(int i=0; i < m_combo_year.getItemCount(); i++)
			{
				YearLabel lbl_year = (YearLabel)m_combo_year.getItemAt(i);
				
				if(when==MASK_DATES.NONE)
				{
					lbl_year.setSelectable(true);
				}
				else if(when==MASK_DATES.BEFORE)
				{
					//if(year<combo_year)
					//	m_combo_year.set
					lbl_year.setSelectable(lbl_year.getNumber() <= year);
				}
				else if(when==MASK_DATES.AFTER)	
				{
					lbl_year.setSelectable(lbl_year.getNumber() >= year);
					
				}
			}
		}
		private void maskDays(int day, MASK_DATES when)
		{
			for(int i=0; i < m_daypanel.m_btn_days.length; i++)
			{
				DayButton d = m_daypanel.m_btn_days[i];
				if(when==MASK_DATES.NONE)
					d.setEnabled(true);
				else if(when==MASK_DATES.BEFORE)
				{
					d.setEnabled(d.m_n_day <= day);
				}
				else if(when==MASK_DATES.AFTER)
				{
					d.setEnabled(d.m_n_day >= day);
				}
			}
		}
		private void set_now() {
			m_now = Calendar.getInstance();
			m_now.add(Calendar.DATE, -n_init_days_before);
			m_now.add(Calendar.MONTH, -n_init_months_before);
			m_now.add(Calendar.YEAR, -n_init_years_before);
			m_calendar = m_now;
		}
		private void calc_new_minmax() {
			m_calendar.set(Calendar.YEAR, m_n_year);
			m_calendar.set(Calendar.MONTH, m_n_month);
			m_n_min_day = m_calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
			m_n_max_day = m_calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		public void add_controls() {
			set_gridconst(0, 0, 1, 1, GridBagConstraints.CENTER);
			add(m_combo_year, m_gridconst);
			set_gridconst(1, 0, 1, 1, GridBagConstraints.CENTER);
			add(m_combo_month, m_gridconst);

			if(b_show_timefield)
			{
				set_gridconst(2, 0, 1, 1, GridBagConstraints.CENTER);
				add(m_txt_time, m_gridconst);
			}
			set_gridconst(0, 1, 3, 1, GridBagConstraints.CENTER);
			add(m_daypanel, m_gridconst);
			init();
		}
		public void init() {
			set_clock();
			setVisible(true);
		}
		public void set_clock() {
			if(m_n_hour > -1 && m_n_minute > -1) {
				
			}
			else { //reset
				m_n_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
				m_n_minute = Calendar.getInstance().get(Calendar.MINUTE);				
			}
			set_clocktext();
		}
		public void set_clocktext() {
			String sz_txt = m_n_hour + ":" + m_n_minute;
			m_txt_time.setText(sz_txt);
		}
		private void set_year(int n_year) {
			m_n_year = n_year;
			m_calendar.set(Calendar.YEAR, m_n_year);
		}
		private void set_month(int n_month) {
			m_n_month = n_month;
			m_calendar.set(Calendar.MONTH, m_n_month);
		}
		private void set_hour(int n_hour) {
			m_n_hour = n_hour;
			m_calendar.set(Calendar.HOUR_OF_DAY, m_n_hour);
		}
		private void set_minute(int n_minute) {
			m_n_minute = n_minute;
			m_calendar.set(Calendar.MINUTE, m_n_minute);
		}
		private void set_day(int n_day) {
			
		}
		public void actionPerformed(ActionEvent e) {
			boolean b_recalc = false;
			if("act_year_change".equals(e.getActionCommand())) {
				YearLabel y = (YearLabel)((JComboBox)e.getSource()).getSelectedItem();
				
				b_recalc = true;
				//set_year(new Integer((String)((JComboBox)e.getSource()).getSelectedItem()).intValue());
				set_year(y.getNumber());
			} else if("act_month_change".equals(e.getActionCommand())) {
				b_recalc = true;
				//set_month(((JComboBox)e.getSource()).getSelectedIndex());
				MonthLabel m = (MonthLabel)((JComboBox)e.getSource()).getSelectedItem();
				set_month(m.getNumber());
			} else if("act_day_selected".equals(e.getActionCommand())) {
				set_day(((Integer)e.getSource()).intValue());
			}
			if(b_recalc) {
				calc_new_minmax();
				m_daypanel.recalc();
			}
			//PAS.get_pas().add_event("Date: " + m_n_day + "." + (m_n_month+1) + "." + m_n_year, null);
			callback.actionPerformed(new ActionEvent(DateTimePicker.this, ActionEvent.ACTION_PERFORMED, "act_datetime_changed"));
			
		}
		
		private void set_datetime_now() {
			set_year(m_now.get(Calendar.YEAR));
			set_month(m_now.get(Calendar.MONTH));
			set_day(m_now.get(Calendar.DAY_OF_MONTH));
			set_hour(m_now.get(Calendar.HOUR_OF_DAY));
			set_minute(m_now.get(Calendar.MINUTE));
		}
		private void set_datetime(long newdate)
		{
			String year = new Long(newdate).toString().substring(0,4);
			String month = new Long(newdate).toString().substring(4,6);
			String day = new Long(newdate).toString().substring(6,8);
			set_year(new Integer(year));
			set_month(new Integer(month)-1);
			set_day(new Integer(day));
		}
		
		public long get_datetime() {
			set_now();
			set_datetime_now();
			String datetime = "";
			datetime += m_n_year;
			if(m_n_month>=9)
				datetime += m_n_month+1;
			else {
				datetime += 0;
				datetime += m_n_month+1;
			}
			if(m_n_day>=10)
				datetime += m_n_day;
			else {
				datetime += 0;
				datetime += m_n_day;
			}
			if(m_n_hour>=10)
				datetime += m_n_hour;
			else {
				datetime += 0;
				datetime += m_n_hour;
			}
			if(m_n_minute>=10)
				datetime += m_n_minute;
			else {
				datetime += 0;
				datetime += m_n_minute;
			}
			return Long.parseLong(datetime);
		}
		
		public class DayPanel extends DefaultPanel {
			public static final long serialVersionUID = 1;
			//public final String m_day_indicator [] = new String[] { "M", "T", "W", "T", "F", "S", "S" };
			public String m_day_indicator [] = new String[7];
			int m_n_height = 7;
			int m_n_width = 7;
			JLabel m_label_days[];
			DayButton m_btn_days[];
			ButtonGroup m_btn_group;
			DatePicker m_datepicker;
			
			public DayPanel(ActionListener callback, DatePicker picker) {
				super();
				m_day_indicator[0] = dayLetter(Calendar.MONDAY, 2);
				m_day_indicator[1] = dayLetter(Calendar.TUESDAY, 2);
				m_day_indicator[2] = dayLetter(Calendar.WEDNESDAY, 2);
				m_day_indicator[3] = dayLetter(Calendar.THURSDAY, 2);
				m_day_indicator[4] = dayLetter(Calendar.FRIDAY, 2);
				m_day_indicator[5] = dayLetter(Calendar.SATURDAY, 2);
				m_day_indicator[6] = dayLetter(Calendar.SUNDAY, 2);
				m_datepicker = picker;
				m_label_days = new JLabel[m_n_width];
				m_btn_days = new DayButton[m_n_width * (m_n_height-1)];
				m_btn_group = new ButtonGroup();
				for(int i=0; i < m_label_days.length; i++) {
					m_label_days[i] = new JLabel(m_day_indicator[i]);
					m_label_days[i].setHorizontalAlignment(JLabel.CENTER);
					m_label_days[i].setPreferredSize(new Dimension(32, 16));
					m_label_days[i].setFont(new Font(null, Font.BOLD, 11));
					m_label_days[i].setBackground(Color.LIGHT_GRAY);
				}
				for(int i=0; i < m_btn_days.length; i++) {
					m_btn_days[i] = new DayButton(1);
					m_btn_days[i].setMargin(new Insets(0, 0, 0, 0));
					m_btn_days[i].setPreferredSize(new Dimension(32, 16));
					m_btn_days[i].setFont(new Font(null, Font.BOLD, 10));
					m_btn_days[i].setBackground(Color.white);
					m_btn_days[i].setBorder(null);
					m_btn_group.add(m_btn_days[i]);
					m_btn_days[i].addActionListener(this);
					m_btn_days[i].setActionCommand("act_day_selected");
				}
				
				add_controls();
			}
			public void add_controls() {
				for(int i=0; i < m_n_width; i++) {
					set_gridconst(i, 0, 1, 1, GridBagConstraints.CENTER);
					add(m_label_days[i], m_gridconst);
				}
				for(int y=0; y < (m_n_height-1); y++) {
					for(int x=0; x < m_n_width; x++) {
						set_gridconst(x, y+1, 1, 1, GridBagConstraints.CENTER);
						add(m_btn_days[y*m_n_width + x], m_gridconst);
					}
				}
				init();
			}
			public void init() {
				recalc();
				setVisible(true);
			}
			public void actionPerformed(ActionEvent e) {
				if("act_day_selected".equals(e.getActionCommand())) {
					m_n_day = ((DayButton)e.getSource()).get_day();
					ActionEvent send = new ActionEvent(new Integer(m_n_day), ActionEvent.ACTION_PERFORMED, e.getActionCommand());
					m_datepicker.actionPerformed(send);
				}
			}
			public void recalc() {
				Calendar c = Calendar.getInstance();
				c.setFirstDayOfWeek(Calendar.MONDAY);
				c.set(m_n_year, m_n_month, 0);
				int n_day = -1;
				int n_day_count = 1;
				int n_dayofweek = c.get(Calendar.DAY_OF_WEEK);
				DayButton btn = null;
				if(m_n_day > m_n_max_day) {
					m_n_day = m_n_max_day;
				}
				
				for(int y=0; y < (m_n_height-1); y++) {
					for(int x=0; x < m_n_width; x++) {
						if(y*m_n_width + x + 1 < n_dayofweek)
							n_day = -1;
						else
						{
							if(n_day_count > m_n_max_day) {
								n_day = -1;
							}
							else {
								n_day = n_day_count;
								if(m_n_day == n_day)
									btn = m_btn_days[y*m_n_width + x];
							}
							n_day_count++;
						}
						m_btn_days[y*m_n_width + x].set_day(n_day);
					}
				}
				btn.setSelected(true);
			}
			
			
			public class DayButton extends JToggleButton {
				public static final long serialVersionUID = 1;
				private int m_n_day = -1;
				public int get_day() { return m_n_day; }
				public void set_day(int n_day) { 
					m_n_day = n_day;
					if(m_n_day > 0) {
						setText(new Integer(m_n_day).toString());
						setEnabled(true);
					}
					else {
						setText("");
						setEnabled(false);
					}
				} 
				public DayButton(int n_day) {
					super((new Integer(n_day)).toString());
					m_n_day = n_day;
				}
			}
		}
	}
}