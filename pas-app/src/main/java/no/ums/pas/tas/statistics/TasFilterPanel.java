package no.ums.pas.tas.statistics;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.localization.Localization;
import no.ums.pas.tas.statistics.TasChart.STATS_CHARTTYPE;
import no.ums.pas.tas.statistics.TasChart.STATS_RESULTS_GROUPBY;
import no.ums.pas.ums.tools.calendarutils.DateTimePicker;
import no.ums.pas.ums.tools.calendarutils.DateTimePicker.MASK_DATES;
import no.ums.ws.common.ULBAFILTERSTATFUNCTION;
import no.ums.ws.common.ULBAFILTERSTATTIMEUNIT;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;




public class TasFilterPanel extends DefaultPanel
{
	/*
	 *  XX Chart type
	 *  XX filter.setGroupTimeunit(ULBAFILTERSTATTIMEUNIT.PER_DAY);
		filter.setFromDate(Long.parseLong("20100210000000"));
		filter.setToDate(Long.parseLong("20110213000000"));
		XX filter.setStatFunction(ULBAFILTERSTATFUNCTION.STAT_AVERAGE);
		XX setResultsGroupBy(STATS_RESULTS_GROUPBY.MONTH);
		XX setGroupByOperator(true);

	 */
	public static final String ACT_CHARTTYPE_CHANGED = "act_charttype_changed";
	public static final String ACT_GROUP_TIMEUNIT_CHANGED = "act_group_timeunit_changed";
	public static final String ACT_GROUPING_CHANGED = "act_grouping_changed";
	public static final String ACT_FILTER_FUNCTION_CHANGED = "act_filter_function_changed";
	public static final String ACT_GROUP_BY_OPERATOR_CHANGED = "act_group_by_operator_changed";
	public static final String ACT_TIMEFILTER_FROM_CHANGED = "act_timefilter_from_changed";
	public static final String ACT_TIMEFILTER_TO_CHANGED = "act_timefilter_to_changed";
	public static final String ACT_FILTER_ROWCOUNT_CHANGED = "act_filter_rowcount_changed";
	public static final String ACT_EXPORT_STATS = "act_export_stats";

	public class ComboStatFunctionItem extends Object
	{
		protected ULBAFILTERSTATFUNCTION function;
		protected String str;
		public ULBAFILTERSTATFUNCTION getFunction()
		{
			return function;
		}
		public ComboStatFunctionItem(String s, ULBAFILTERSTATFUNCTION f)
		{
			super();
			this.function = f;
			str = s;
		}
		@Override
		public String toString()
		{
			return str;
		}
	}
	
	public class ComboResultsGroupTimeunit extends Object
	{
		protected STATS_RESULTS_GROUPBY group;
		protected String str;
		public STATS_RESULTS_GROUPBY getGroup()
		{
			return group;
		}
		public ComboResultsGroupTimeunit(String s, STATS_RESULTS_GROUPBY g)
		{
			super();
			this.group = g;
			str = s;
		}
		@Override
		public String toString()
		{
			return str;
		}
	}
	public class ComboFilterTimeunit extends Object
	{
		protected ULBAFILTERSTATTIMEUNIT timeunit;
		protected String str;
		public ULBAFILTERSTATTIMEUNIT getTimeunit()
		{
			return timeunit;
		}
		public ComboFilterTimeunit(String s, ULBAFILTERSTATTIMEUNIT g)
		{
			super();
			this.timeunit = g;
			str = s;
		}
		@Override
		public String toString()
		{
			return str;
		}
	}
	public class ComboFilterRowcount extends Object
	{
		protected int l_rows;
		public ComboFilterRowcount(int rows)
		{
			l_rows = rows;
		}
		@Override
		public String toString()
		{
			if(l_rows<=0)
			{
                return Localization.l("main_tas_stats_rowcount_ALL");
            }
			return Integer.toString(l_rows);
		}
	}
	public class ComboChartTypeItem extends Object
	{
		protected STATS_CHARTTYPE type;
		protected String str;
		public STATS_CHARTTYPE getType()
		{
			return type;
		}
		public ComboChartTypeItem(String s, STATS_CHARTTYPE type)
		{
			super();
			this.type = type;
			str = s;
		}
		@Override
		public String toString()
		{
			return str;
		}
	}
	
	JComboBox combo_charttype = new JComboBox();
	JComboBox combo_filtertimeunit = new JComboBox();
	JComboBox combo_grouping = new JComboBox();
	JComboBox combo_filterfunction = new JComboBox();
	JComboBox combo_filterrowcount = new JComboBox();
	JCheckBox btn_group_by_operator = new JCheckBox(Localization.l("main_tas_stats_groupby") + " " + Localization.l("main_tas_stats_groupby_OPERATOR"));

    {
        btn_group_by_operator = new JCheckBox(Localization.l("main_tas_stats_groupby") + " " + Localization.l("main_tas_stats_groupby_OPERATOR"));
    }

    {
        btn_group_by_operator = new JCheckBox(Localization.l("main_tas_stats_groupby") + " " + Localization.l("main_tas_stats_groupby_OPERATOR"));
    }

    {
        btn_group_by_operator = new JCheckBox(Localization.l("main_tas_stats_groupby") + " " + Localization.l("main_tas_stats_groupby_OPERATOR"));
    }

    DateTimePicker date_from = new DateTimePicker(this, false, 0, 1, 0);
	DateTimePicker date_to = new DateTimePicker(this, false, 0, 0, 0);
	JButton btn_export = new JButton(Localization.l("common_export"));

    {
        btn_export = new JButton(Localization.l("common_export"));
    }

    ActionListener callback;
	public TasFilterPanel(ActionListener callback)
	{
		super();
		this.callback = callback;
		add_controls();
		date_from.setMaskDates(date_to.get_date(), MASK_DATES.BEFORE);
		date_to.setMaskDates(date_from.get_date(), MASK_DATES.AFTER);
	}
	
	public STATS_CHARTTYPE getSelectedChartType()
	{
		ComboChartTypeItem sel = (ComboChartTypeItem)combo_charttype.getSelectedItem();
		return sel.getType();
	}
	public ULBAFILTERSTATTIMEUNIT getFilterTimeUnit()
	{
		ComboFilterTimeunit sel = (ComboFilterTimeunit)combo_filtertimeunit.getSelectedItem();
		return sel.getTimeunit();
	}
	public STATS_RESULTS_GROUPBY getResultsGroupBy()
	{
		ComboResultsGroupTimeunit sel = (ComboResultsGroupTimeunit)combo_grouping.getSelectedItem();
		return sel.getGroup();
	}
	public ULBAFILTERSTATFUNCTION getFilterFunction()
	{
		ComboStatFunctionItem sel = (ComboStatFunctionItem)combo_filterfunction.getSelectedItem();
		return sel.getFunction();
	}
	protected void enableFilterFunction(boolean b)
	{
		combo_filterfunction.setEnabled(b);
	}
	protected void enableFilterRowCount(boolean b)
	{
		combo_filterrowcount.setEnabled(b);
		enableGroupByOperator(!b);			
	}
	protected void enableGroupByOperator(boolean b)
	{
		if(!b) //deselect it if it's selected and is to be disabled
		{
			btn_group_by_operator.setSelected(b);
		}
		btn_group_by_operator.setEnabled(b);
	}

	public Boolean getGroupByOperator()
	{
		JCheckBox btn = (JCheckBox)btn_group_by_operator;
		return btn.isSelected();
	}
	public long getFilterDateFrom()
	{
		return new Long(date_from.get_datetime_beginning_of_day());
	}
	public long getFilterDateTo()
	{
		return new Long(date_to.get_datetime_ending_of_day());
	}
	public int getFilterRowCount()
	{
		ComboFilterRowcount sel = (ComboFilterRowcount)combo_filterrowcount.getSelectedItem();
		return sel.l_rows;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(combo_charttype))
		{
			ComboChartTypeItem sel = (ComboChartTypeItem)combo_charttype.getSelectedItem();
			System.out.println("selected " + sel);
			callback.actionPerformed(new ActionEvent(sel.getType(), ActionEvent.ACTION_PERFORMED, ACT_CHARTTYPE_CHANGED));
		}
		else if(e.getSource().equals(combo_filtertimeunit))
		{
			ComboFilterTimeunit sel = (ComboFilterTimeunit)combo_filtertimeunit.getSelectedItem();
			System.out.println("selected " + sel);
			callback.actionPerformed(new ActionEvent(sel.getTimeunit(), ActionEvent.ACTION_PERFORMED, ACT_GROUP_TIMEUNIT_CHANGED));			
		}
		else if(e.getSource().equals(combo_grouping))
		{
			ComboResultsGroupTimeunit sel = (ComboResultsGroupTimeunit)combo_grouping.getSelectedItem();
			System.out.println("selected " + sel);
			callback.actionPerformed(new ActionEvent(sel.getGroup(), ActionEvent.ACTION_PERFORMED, ACT_GROUPING_CHANGED));			
		}
		else if(e.getSource().equals(combo_filterfunction))
		{
			ComboStatFunctionItem sel = (ComboStatFunctionItem)combo_filterfunction.getSelectedItem();
			System.out.println("selected " + sel);
			callback.actionPerformed(new ActionEvent(sel.getFunction(), ActionEvent.ACTION_PERFORMED, ACT_FILTER_FUNCTION_CHANGED));	
			switch(sel.function)
			{
			case STAT_MAX:
				enableFilterRowCount(true);
				break;
			default:
				combo_filterrowcount.setSelectedIndex(0);
				enableFilterRowCount(false);
				break;
			}
		}
		else if(e.getSource().equals(combo_filterrowcount))
		{
			if(getFilterRowCount()==0)
				enableGroupByOperator(true);			
			else
				enableGroupByOperator(false);

			callback.actionPerformed(new ActionEvent(getFilterRowCount(), ActionEvent.ACTION_PERFORMED, ACT_FILTER_ROWCOUNT_CHANGED));
		}
		else if(e.getSource().equals(btn_group_by_operator))
		{
			JCheckBox btn = (JCheckBox)btn_group_by_operator;
			System.out.println("selected " + btn.isSelected());
			callback.actionPerformed(new ActionEvent(btn.isSelected(), ActionEvent.ACTION_PERFORMED, ACT_GROUP_BY_OPERATOR_CHANGED));						
		}
		else if("act_datetime_changed".equals(e.getActionCommand()))
		{
			if(e.getSource().equals(date_from))
			{
				Long n = new Long(date_from.get_datetime_beginning_of_day());
				System.out.println("date from " + n);
				callback.actionPerformed(new ActionEvent(n, ActionEvent.ACTION_PERFORMED, ACT_TIMEFILTER_FROM_CHANGED));
				date_to.setMaskDates(date_from.get_date(), MASK_DATES.AFTER);
				date_from.setMaskDates(date_to.get_date(), MASK_DATES.BEFORE);
			}
			else if(e.getSource().equals(date_to))
			{
				Long n = new Long(date_to.get_datetime_ending_of_day());
				System.out.println("date to " + n);
				callback.actionPerformed(new ActionEvent(n, ActionEvent.ACTION_PERFORMED, ACT_TIMEFILTER_TO_CHANGED));				
				date_from.setMaskDates(date_to.get_date(), MASK_DATES.BEFORE);
				date_to.setMaskDates(date_from.get_date(), MASK_DATES.AFTER);
			}
		}
		else if(e.getSource().equals(btn_export))
		{
			callback.actionPerformed(new ActionEvent(btn_export, ActionEvent.ACTION_PERFORMED, ACT_EXPORT_STATS));			
		}

	}

	@Override
	public void add_controls() {
		combo_charttype.addActionListener(this);

        combo_charttype.addItem(new ComboChartTypeItem(Localization.l("main_tas_stats_chart_type_" + STATS_CHARTTYPE.LINECHART.toString()), STATS_CHARTTYPE.LINECHART));
        combo_charttype.addItem(new ComboChartTypeItem(Localization.l("main_tas_stats_chart_type_" + STATS_CHARTTYPE.LINECHART_3D.toString()), STATS_CHARTTYPE.LINECHART_3D));
        combo_charttype.addItem(new ComboChartTypeItem(Localization.l("main_tas_stats_chart_type_" + STATS_CHARTTYPE.BARCHART.toString()), STATS_CHARTTYPE.BARCHART));
        combo_charttype.addItem(new ComboChartTypeItem(Localization.l("main_tas_stats_chart_type_" + STATS_CHARTTYPE.BARCHART_3D.toString()), STATS_CHARTTYPE.BARCHART_3D));
        combo_charttype.addItem(new ComboChartTypeItem(Localization.l("main_tas_stats_chart_type_" + STATS_CHARTTYPE.STACKED_BARCHART.toString()), STATS_CHARTTYPE.STACKED_BARCHART));
        combo_charttype.addItem(new ComboChartTypeItem(Localization.l("main_tas_stats_chart_type_" + STATS_CHARTTYPE.STACKED_BARCHART_3D.toString()), STATS_CHARTTYPE.STACKED_BARCHART_3D));
        combo_charttype.addItem(new ComboChartTypeItem(Localization.l("main_tas_stats_chart_type_" + STATS_CHARTTYPE.MULTIPLE_PAI.toString()), STATS_CHARTTYPE.MULTIPLE_PAI));
        combo_charttype.addItem(new ComboChartTypeItem(Localization.l("main_tas_stats_chart_type_" + STATS_CHARTTYPE.MULTIPLE_PAI_3D.toString()), STATS_CHARTTYPE.MULTIPLE_PAI_3D));
        combo_charttype.addItem(new ComboChartTypeItem(Localization.l("main_tas_stats_chart_type_" + STATS_CHARTTYPE.AREA_CHART.toString()), STATS_CHARTTYPE.AREA_CHART));
        combo_charttype.addItem(new ComboChartTypeItem(Localization.l("main_tas_stats_chart_type_" + STATS_CHARTTYPE.STACKED_AREA_CHART.toString()), STATS_CHARTTYPE.STACKED_AREA_CHART));
		
		set_gridconst(0, inc_panels(), 3, 1);
		add(date_from, m_gridconst);
		add_spacing(DIR_VERTICAL, 30);
		set_gridconst(0, inc_panels(), 3, 1);
		add(date_to, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 30);
		
		set_gridconst(0, inc_panels(), 3, 1);
		add(combo_charttype, m_gridconst);
		add_spacing(DIR_VERTICAL, 5);


        combo_filterfunction.addItem(new ComboStatFunctionItem(Localization.l("main_tas_stats_dbfunc_" + ULBAFILTERSTATFUNCTION.STAT_AVERAGE.toString()), ULBAFILTERSTATFUNCTION.STAT_AVERAGE));
        combo_filterfunction.addItem(new ComboStatFunctionItem(Localization.l("main_tas_stats_dbfunc_" + ULBAFILTERSTATFUNCTION.STAT_MAX.toString()), ULBAFILTERSTATFUNCTION.STAT_MAX));
        combo_filterfunction.addItem(new ComboStatFunctionItem(Localization.l("main_tas_stats_dbfunc_" + ULBAFILTERSTATFUNCTION.STAT_MIN.toString()), ULBAFILTERSTATFUNCTION.STAT_MIN));
		combo_filterrowcount.addItem(new ComboFilterRowcount(0));
		combo_filterrowcount.addItem(new ComboFilterRowcount(1));
		combo_filterrowcount.addItem(new ComboFilterRowcount(2));
		combo_filterrowcount.addItem(new ComboFilterRowcount(5));
		combo_filterrowcount.addItem(new ComboFilterRowcount(10));
		combo_filterfunction.addActionListener(this);
		combo_filterrowcount.addActionListener(this);

		
		ComboFilterTimeunit default_data_per;
		combo_filtertimeunit.addActionListener(this);
        ComboFilterTimeunit data_per = new ComboFilterTimeunit(Localization.l("main_tas_stats_data_" + ULBAFILTERSTATTIMEUNIT.PER_HOUR.toString()), ULBAFILTERSTATTIMEUNIT.PER_HOUR);
		combo_filtertimeunit.addItem(data_per);
        default_data_per = data_per = new ComboFilterTimeunit(Localization.l("main_tas_stats_data_" + ULBAFILTERSTATTIMEUNIT.PER_DAY.toString()), ULBAFILTERSTATTIMEUNIT.PER_DAY);
		combo_filtertimeunit.addItem(data_per);
        data_per = new ComboFilterTimeunit(Localization.l("main_tas_stats_data_" + ULBAFILTERSTATTIMEUNIT.PER_MONTH.toString()), ULBAFILTERSTATTIMEUNIT.PER_MONTH);
		combo_filtertimeunit.addItem(data_per);
        data_per = new ComboFilterTimeunit(Localization.l("main_tas_stats_data_" + ULBAFILTERSTATTIMEUNIT.PER_YEAR.toString()), ULBAFILTERSTATTIMEUNIT.PER_YEAR);
		combo_filtertimeunit.addItem(data_per);

		set_gridconst(0, inc_panels(), 1, 1);
		add(combo_filterfunction, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(combo_filterrowcount, m_gridconst);

		set_gridconst(2, get_panel(), 1, 1);
		add(combo_filtertimeunit, m_gridconst);
		add_spacing(DIR_VERTICAL, 5);
		
		ComboResultsGroupTimeunit timeunit;
		ComboResultsGroupTimeunit default_timeunit;
		combo_grouping.addActionListener(this);
        default_timeunit = timeunit = new ComboResultsGroupTimeunit(Localization.l("main_tas_stats_groupby") + " " + Localization.l("main_tas_stats_groupby_" + STATS_RESULTS_GROUPBY.COUNTRY.toString()),STATS_RESULTS_GROUPBY.COUNTRY);
		combo_grouping.addItem(timeunit);
        timeunit = new ComboResultsGroupTimeunit(Localization.l("main_tas_stats_groupby") + " " + Localization.l("main_tas_stats_groupby_" + STATS_RESULTS_GROUPBY.TIMEUNIT.toString()),STATS_RESULTS_GROUPBY.TIMEUNIT);
		combo_grouping.addItem(timeunit);
		
		
		combo_filtertimeunit.setSelectedItem(default_data_per);
		combo_grouping.setSelectedItem(default_timeunit);
		
		/*timeunit = new ComboResultsGroupTimeunit("Group by Day",STATS_RESULTS_GROUPBY.DAY);
		combo_grouping.addItem(timeunit);
		//timeunit = new ComboResultsGroupTimeunit("Group by Week", STATS_RESULTS_GROUPBY.WEEK);
		//combo_grouping.addItem(timeunit);
		timeunit = new ComboResultsGroupTimeunit("Group by Month",STATS_RESULTS_GROUPBY.MONTH);
		combo_grouping.addItem(timeunit);
		combo_grouping.setSelectedItem(timeunit);
		timeunit = new ComboResultsGroupTimeunit("Group by Year",STATS_RESULTS_GROUPBY.YEAR);
		combo_grouping.addItem(timeunit);*/

		set_gridconst(0, inc_panels(), 3, 1);
		add(combo_grouping, m_gridconst);
		add_spacing(DIR_VERTICAL, 5);

		
		btn_group_by_operator.addActionListener(this);
		set_gridconst(0, inc_panels(), 3, 1);
		add(btn_group_by_operator, m_gridconst);
		
		btn_export.addActionListener(this);
		add_spacing(DIR_VERTICAL, 30);
		set_gridconst(0, inc_panels(), 3, 1);
		add(btn_export, m_gridconst);
	}

	@Override
	public void init() {
		
	}
}