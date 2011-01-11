package no.ums.pas.tas.statistics;

import no.ums.pas.PAS;
import no.ums.pas.core.ws.WSTasStats_Countries_Timeunit;
import no.ums.pas.importer.csv.csvexporter;
import no.ums.pas.tas.TasPanel;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.pas.tas.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.*;
import org.jfree.util.TableOrder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class ChartOverTime extends TasChart implements ActionListener
{
	int n_num_categories = 0;
	int n_max_categories = 31;
	int n_skip_categories = 1;
	public ChartOverTime(ActionListener callback)
	{
		super(callback);
	}
	
	public ChartOverTime(List<ULBACOUNTRY> country, ActionListener callback)
	{
		super(callback);
		setFilterCountryList(country);
		filter.setGroupTimeunit(ULBAFILTERSTATTIMEUNIT.PER_DAY);
		filter.setFromDate(Long.parseLong("20100210000000"));
		filter.setToDate(Long.parseLong("20110213000000"));
		filter.setStatFunction(ULBAFILTERSTATFUNCTION.STAT_AVERAGE);
		setResultsGroupBy(STATS_RESULTS_GROUPBY.MONTH);
		setGroupByOperator(true);
		//downloadData();
	}
	
	public ChartOverTime(ULBACONTINENT continent, ActionListener callback)
	{
		super(callback);
		setFilterCountryList(continent);
		
		filter.setGroupTimeunit(ULBAFILTERSTATTIMEUNIT.PER_HOUR);
		filter.setFromDate(Long.parseLong("20100211000000"));
		filter.setToDate(Long.parseLong("20110213000000"));
		filter.setStatFunction(ULBAFILTERSTATFUNCTION.STAT_AVERAGE);
		setResultsGroupBy(STATS_RESULTS_GROUPBY.DAY);
		setGroupByOperator(false);
	}

	@Override
	protected void ExportStats(csvexporter csv) {
		try
		{
			List<ULBACOUNTRYSTATISTICS> list = results.getULBACOUNTRYSTATISTICS();
			ArrayList<Object[]> explist = new ArrayList<Object[]>(); 
			
			Object [] headers = new Object[] {
					PAS.l("common_countrycode"),
					PAS.l("common_iso3166"),
					PAS.l("main_tas_stats_groupby_COUNTRY"),
					PAS.l("main_tas_stats_groupby_OPERATOR"),
					PAS.l("main_tas_stats_groupby_TIMEUNIT"),
					PAS.l("main_tas_panel_response_count"),
					//PAS.l("main_tas_panel_response_count"),
			};
			explist.add(headers);
			
			for(int i=0; i < list.size(); i++)
			{
				ULBACOUNTRYSTATISTICS s = list.get(i);
				List<UTOURISTCOUNT> stats = s.getStatistics().getUTOURISTCOUNT();
				ULBACOUNTRY country = TasPanel.getCountryFromCC(s.getLCc());
				if(getGroupByOperator())
				{
					for(int o = 0; o < stats.size(); o++)
					{
						UTOURISTCOUNT tc = stats.get(o);
						Object [] line = new Object [] {
							s.getLCc(),
							country.getSzIso(),
							country.getSzName(),
							tc.getLOperator(),
							TextFormat.format_datetime(tc.getLLastupdate()),
							tc.getLTouristcount(),
							//s.getNTouristcount(),
						};
						explist.add(line);
					}
				}
				else
				{
					int sum_operators = 0;
					long timestamp = 0;
					for(int o = 0; o < stats.size(); o++)
					{
						UTOURISTCOUNT tc = stats.get(o);
						sum_operators += tc.getLTouristcount();
						timestamp = tc.getLLastupdate();
					}
					boolean found = false;
					for(int o=0;o<explist.size(); ++o) {
						if(explist.get(o)[4].equals(TextFormat.format_datetime(timestamp)) && Integer.parseInt(explist.get(o)[0].toString()) == s.getLCc()) {
							explist.get(o)[5] = ((Integer)explist.get(o)[5]) + sum_operators;
							found = true;
						}	
					}
					if(!found) {
						Object [] line = new Object [] {
								s.getLCc(),
								country.getSzIso(),
								country.getSzName(),
								0,
								TextFormat.format_datetime(timestamp),
								sum_operators,
								//s.getNTouristcount(),
							};
						explist.add(line);
					}
				}
			}
			for(int i=0;i<explist.size();++i) {
				csv.addLine(explist.get(i));
			}
			csv.Save();
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_error"), "Error saving CSV export", e, Error.SEVERITY_ERROR);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if("act_download_finished".equals(e.getActionCommand()))
		{
			results = (ArrayOfULBACOUNTRYSTATISTICS)e.getSource();
			createChart();
			b_need_data_update = false;
		}		
	}
	
	@Override
	protected void downloadData() {
		
		WSTasStats_Countries_Timeunit ws = new WSTasStats_Countries_Timeunit(this, filter);
		ws.start();
		//wait until act_download_finished
	}
	
	public class CategoryKey extends GroupKey
	{
		public CategoryKey(String key, int categoryno, STATS_RESULTS_GROUPBY groupby)
		{
			super(key, categoryno, groupby);
		}
		@Override
		public String toString()
		{
			//if(n_skip_categories>1 && (getCategoryno() % n_skip_categories) == 0)
			//		return "skip";
			if((getCategoryno() % n_skip_categories) == 0)
			{
				switch(groupby)
				{
				case DAY:
					return key+":00";
				case MONTH:
					return key+".";
				default:
					return key;
				}
			}
			else
				return "";
			//if((getCategoryno() % 4)==0)
			//	return key + ":00";
			//return "";
		}

	}
	public class GroupKey implements Comparable<GroupKey>
	{
		String key = PAS.l("common_na");
		int categoryno;
		STATS_RESULTS_GROUPBY groupby;
		public int getCategoryno() { return categoryno; }
		public GroupKey(String key, int categoryno, STATS_RESULTS_GROUPBY groupby)
		{
			super();
			this.key = key;
			this.categoryno = categoryno;
			this.groupby = groupby;
		}
		public void setKey(String s)
		{
			key = s;
		}
		
		@Override
		public boolean equals(Object obj) {
			int ret = 0;
			if(obj.getClass().equals(String.class))
			{
				ret = this.key.compareTo((String)obj);
			}
			else
			{
				ret = this.key.compareTo(((GroupKey)obj).key);
			}
			if(ret==0)
				return true;
			else
				return false;
		}
		@Override
		public int compareTo(GroupKey o) {
			return this.key.compareTo(o.key);
		}
		@Override
		public String toString()
		{
			//if((getCategoryno() % 2)==2)
			//	return key;
			//return "Hide";
			return key;
		}
	}

	@Override
	protected void createChart() {
		/*TimeSeries aggregate = new TimeSeries("All");
		aggregate.add(new Day(8,2,2010), 35);
		aggregate.add(new Day(9,2,2010), 65);
		aggregate.add(new Day(10,2,2010), 57);

		TimeSeries telenor = new TimeSeries("Telenor");
		telenor.add(new Day(8, 2, 2010), 25);
		telenor.add(new Day(9, 2, 2010), 50);
		telenor.add(new Day(10, 2, 2010), 45);
		
		
		TimeSeries netcom = new TimeSeries("Netcom");
		netcom.add(new Day(8,2,2010), 10);
		netcom.add(new Day(9,2,2010), 15);
		netcom.add(new Day(10,2,2010), 12);

		TimeSeriesCollection coll = new TimeSeriesCollection();
		coll.addSeries(telenor);
		coll.addSeries(netcom);
		coll.addSeries(aggregate);
		
		chart = ChartFactory.createTimeSeriesChart("Tourists", "Date", "Tourists", coll, true, true, false);*/
		
		
		
		
		
		List<ULBACOUNTRYSTATISTICS> stats = results.getULBACOUNTRYSTATISTICS();

/*
		TimeSeriesCollection coll = new TimeSeriesCollection();
		TimeSeries timeserie = new TimeSeries("Country");
		for(int i=0; i < stats.size(); i++)
		{
			//TimeSeries timeserie = new TimeSeries(stats.get(i).getLCc());
			ULBACOUNTRYSTATISTICS statitem = stats.get(i);
			List<UTOURISTCOUNT> arr_count = statitem.getStatistics().getUTOURISTCOUNT();
			for(int c=0; c < arr_count.size(); c++)
			{
				
				UTOURISTCOUNT count = arr_count.get(c);
				boolean b_add = false;
				//if((timeserie = coll.getSeries(count.getLLastupdate())) == null)
				//{
				//	timeserie = new TimeSeries(count.getLLastupdate());
				//	b_add = true;
				//}
					
					
				
				String date = new Long(count.getLLastupdate()).toString();
				//Day day = new Day();
				RegularTimePeriod period = new Day();
				int datelength = 8;
				switch(filter.getGroupTimeunit())
				{
				case PER_HOUR:
					period = new Hour(
							new Integer(date.substring(8,10)),
							new Integer(date.substring(6,8)), 
							new Integer(date.substring(4,6)), 
							new Integer(date.substring(0, 4)));
					datelength = 10;
					break;
				case PER_DAY:
					period = new Day(new Integer(date.substring(6,8)), 
							new Integer(date.substring(4,6)), 
							new Integer(date.substring(0, 4)));
					datelength = 8;
					break;
				case PER_MONTH:
					//period = new Day(new Integer(1),
					//		new Integer(date.substring(4,6)), 
					//		new Integer(date.substring(0, 4)));
					period = new Month(new Integer(date.substring(4,6)),
							new Integer(date.substring(0, 4)));
					datelength = 6;
					break;
				case PER_YEAR:
					period = new Year(new Integer(date.substring(0, 4)));
					datelength = 4;
					break;
				}
				int n = count.getLTouristcount();
				//timeserie.add(period, n);
				timeserie.addOrUpdate(period, n);
				//if(b_add)
			}
		}
		coll.addSeries(timeserie);
		
		chart = ChartFactory.createTimeSeriesChart("Tourists", "Date", "Tourists", coll, true, true, false);	
		//chart = ChartFactory.createXYBarChart("Tourists", "Date", true,"Tourists", coll, PlotOrientation.VERTICAL, true, true, false);
		*/
		
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		switch(filter.getGroupTimeunit())
		{
		case PER_HOUR:
			//if(getResultsGroupBy()==STATS_RESULTS_GROUPBY.COUNTRY)
			//	orientation = PlotOrientation.HORIZONTAL;
			break;
		}
		
		STATS_RESULTS_GROUPBY use_group = STATS_RESULTS_GROUPBY.COUNTRY;
		switch(getResultsGroupBy()) //may be 
		{
			case COUNTRY:
				use_group = STATS_RESULTS_GROUPBY.COUNTRY;
				break;
			case TIMEUNIT:
			{
				switch(filter.getGroupTimeunit())
				{
				case PER_HOUR:
					use_group = STATS_RESULTS_GROUPBY.DAY;
					break;
				case PER_DAY:
					use_group = STATS_RESULTS_GROUPBY.MONTH;
					break;
				case PER_MONTH:
					use_group = STATS_RESULTS_GROUPBY.YEAR;
					break;
				}
			}
			break;
			default:
				use_group = STATS_RESULTS_GROUPBY.COUNTRY;
				break;
		}

		Hashtable<String, CategoryKey> hash_categories = new Hashtable<String, CategoryKey>();
		Hashtable<String, GroupKey> hash_groupkeys = new Hashtable<String, GroupKey>();
		DefaultCategoryDataset cat_dataset = new DefaultCategoryDataset();
		int categoryno = 0;
		for(int i=0; i < stats.size(); i++)
		{
			ULBACOUNTRYSTATISTICS statitem = stats.get(i);
			List<UTOURISTCOUNT> arr_count = statitem.getStatistics().getUTOURISTCOUNT();
			for(int c=0; c < arr_count.size(); c++)
			{
				UTOURISTCOUNT count = arr_count.get(c);
				String date = new Long(count.getLLastupdate()).toString();
				//Day day = new Day();
				RegularTimePeriod period = new Day();
				int datelength = 8;
				switch(filter.getGroupTimeunit())
				{
				case PER_HOUR:
					period = new Hour(
							new Integer(date.substring(8,10)),
							new Integer(date.substring(6,8)), 
							new Integer(date.substring(4,6)), 
							new Integer(date.substring(0, 4)));
					datelength = 10;
					break;
				case PER_DAY:
					period = new Day(new Integer(date.substring(6,8)), 
							new Integer(date.substring(4,6)), 
							new Integer(date.substring(0, 4)));
					datelength = 8;
					break;
				case PER_MONTH:
					//period = new Day(new Integer(1),
					//		new Integer(date.substring(4,6)), 
					//		new Integer(date.substring(0, 4)));
					period = new Month(new Integer(date.substring(4,6)),
							new Integer(date.substring(0, 4)));
					datelength = 6;
					break;
				case PER_YEAR:
					period = new Year(new Integer(date.substring(0, 4)));
					datelength = 4;
					break;
				}
				
				Integer ndate = new Integer(date.substring(0, datelength));
				Integer noperator = new Integer(count.getLOperator());
				Integer ntouristcount = count.getLTouristcount();
				String szcc = new Integer(statitem.getLCc()).toString();
				String iso = statitem.getSzIso();
				//String szcountry = cellbroadcast.CountryCodes.getCountryByCCode(szcc).getCountry();// + "_" + noperator;
				String szcountry = no.ums.pas.tas.TasPanel.getCountryFromCC(statitem.getLCc()).getSzName();
				//if(filter.getCountries().getULBACOUNTRY().size()>1)
				{
					String year = "Unknown year";
					String month = "Unknown month";
					String week = "Unknown week";
					String day = "Unknown day";
					String hour = "Unknown hour";
					try
					{
						year = ndate.toString().substring(0,4);
					}
					catch(Exception e)
					{
						
					}
					try
					{
						month = ndate.toString().substring(4,6);
					}
					catch(Exception e)
					{
						
					}
					try
					{
						day = ndate.toString().substring(6,8);						
					}
					catch(Exception e)
					{
						
					}
					try
					{
						//needs at least yyyy mm dd
						Calendar calendar = Calendar.getInstance();
						calendar.set(new Integer(year), new Integer(month), new Integer(day));
						int n_week = calendar.get(Calendar.WEEK_OF_YEAR);
						week = new Integer(n_week).toString();
					}
					catch(Exception e)
					{
						
					}
					try
					{
						hour = ndate.toString().substring(8,10);
					}
					catch(Exception e)
					{
						
					}
					String str_groupkey = "Unknown key";
					String key = "Unknown key";
					
					switch(use_group)
					{
					case COUNTRY:
					{
						str_groupkey = szcountry;
						switch(filter.getGroupTimeunit())
						{
						case PER_HOUR:
							key = day + "." + month + "." + year + " " + hour + ":00";//ndate.toString();
							break;
						case PER_DAY:
							key = day + "." + month + "." + year;//ndate.toString();
							break;
						case PER_MONTH:
							key = month + "." + year;//ndate.toString();
							break;
						case PER_YEAR:
							key = year;//ndate.toString();
							break;
						}
					}
						break;
					case YEAR:
						str_groupkey = szcountry + " " + year;
						key = month;
						break;
					case MONTH:
						str_groupkey = szcountry + " " + month + "." + year;
						key = day;
						break;
					case WEEK:
						str_groupkey = szcountry + " w" + week + " " + year;
						key = week;
						break;
					case DAY:
						str_groupkey = szcountry + " " + day + "." + month + "." + year;
						key = hour;
						break;
						
					}
					if(getGroupByOperator())
						if(count.getSzOperator() != null)
							str_groupkey += " (" + count.getSzOperator() + ")";
						else	
							str_groupkey += " (OP " + count.getLOperator() + ")";
					GroupKey groupkey = null;
					if(hash_groupkeys.contains(str_groupkey))
					{
						groupkey = hash_groupkeys.get(str_groupkey);
					}
					else
					{
						groupkey = new GroupKey(str_groupkey, categoryno, use_group);
						hash_groupkeys.put(str_groupkey, groupkey);
					}
					
					CategoryKey catkey = null;
					if(hash_categories.containsKey(key))
					{
						catkey = hash_categories.get(key);
					}
					else
					{
						catkey = new CategoryKey(key, categoryno, use_group);
						hash_categories.put(key, catkey);
						categoryno++;
					}

					try
					{
						Number n = cat_dataset.getValue(groupkey, catkey);
						
						if(n==null)
						{
							//if(getResultsGroupBy()==STATS_RESULTS_GROUPBY.WEEK)
							//	ntouristcount /= 7; //divide by days in week
							cat_dataset.addValue(ntouristcount, groupkey, catkey);
							//cat_dataset.incrementValue(ntouristcount, groupkey, catkey);
							//categoryno++;
						}
						else
						{
							Integer oldval = n.intValue(); //new Integer(statitem.getLCc()));
							int total = oldval + ntouristcount;
							//if(getResultsGroupBy()==STATS_RESULTS_GROUPBY.WEEK)
							//	total /= 7; //divide by days in week
							cat_dataset.setValue(total, groupkey, catkey);
						}
					}
					catch(UnknownKeyException e)
					{
						cat_dataset.addValue(ntouristcount, groupkey, catkey);
						//categoryno++;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}

						
					
				}				
				
			}
		}
		n_num_categories = categoryno;
		n_skip_categories = (int)Math.ceil( (n_num_categories / (double) n_max_categories) );
		if(n_skip_categories<=0)
			n_skip_categories = 1;
		
		String title = PAS.l("main_tas_stats_chart_heading") + " " + sz_location; //+ " (Data " + filter.getGroupTimeunit().value() + " grouped by " + getResultsGroupBy().name() + ")";
		String cat_x = PAS.l("common_date");
		String cat_y = PAS.l("main_tas_stats_chart_amount_legend");
		switch(getChartType())
		{
		case LINECHART:
			chart = ChartFactory.createLineChart(title, cat_x, cat_y, cat_dataset, orientation, true, true, false);
			break;
		case LINECHART_3D:
			chart = ChartFactory.createLineChart3D(title, cat_x, cat_y, cat_dataset, orientation, true, true, false);
			break;
		case BARCHART:
			chart = ChartFactory.createBarChart(title, cat_x, cat_y, cat_dataset, orientation, true, true, false);
			break;
		case BARCHART_3D:
			chart = ChartFactory.createBarChart3D(title, cat_x, cat_y, cat_dataset, orientation, true, true, false);
			break;
		case STACKED_BARCHART:
			chart = ChartFactory.createStackedBarChart(title, cat_x, cat_y, cat_dataset, orientation, true, true, false);
			break;
		case STACKED_BARCHART_3D:
			chart = ChartFactory.createStackedBarChart3D(title, cat_x, cat_y, cat_dataset, orientation, true, true, false);
			break;
		case MULTIPLE_PAI:
			chart = ChartFactory.createMultiplePieChart(title, cat_dataset, TableOrder.BY_COLUMN, true, true, false);
			break;
		case MULTIPLE_PAI_3D:
			chart = ChartFactory.createMultiplePieChart3D(title, cat_dataset, TableOrder.BY_COLUMN, true, true, false);
			break;
		case AREA_CHART:
			chart = ChartFactory.createAreaChart(title, cat_x, cat_y, cat_dataset, orientation, true, true, false);
			break;
		case STACKED_AREA_CHART:
			chart = ChartFactory.createStackedAreaChart(title, cat_x, cat_y, cat_dataset, orientation, true, true, false);
			break;
			
		}
		
		//String szSub = PAS.l("main_tas_stats_chart_data") + " " + PAS.l("main_tas_stats_data_" + filter.getGroupTimeunit().toString()) + " " + PAS.l("main_tas_stats_chart_grouped_by") + " " + PAS.l("main_tas_stats_groupby_" + getResultsGroupBy().toString());
		//String szSub = PAS.l("main_tas_stats_data_" + filter.getGroupTimeunit().toString()) + " " + PAS.l("main_tas_stats_chart_grouped_by") + " " + PAS.l("main_tas_stats_groupby_" + getResultsGroupBy().toString());
		String szSub = PAS.l("main_tas_stats_dbfunc_" + filter.getStatFunction().toString());
		if(filter.getRowcount()>0)
			szSub += " " + filter.getRowcount();
		else
			szSub += " " + PAS.l("main_tas_stats_rowcount_ALL");
		szSub += " " + PAS.l("main_tas_stats_data_" + filter.getGroupTimeunit().toString());
		szSub += " " + PAS.l("main_tas_stats_chart_grouped_by") + " " + PAS.l("main_tas_stats_groupby_" + getResultsGroupBy().toString());
		
		TextTitle subtitle = new TextTitle(szSub);
		subtitle.setFont(new Font(UIManager.getString("Common.Fontface"), Font.PLAIN, 10));
		chart.addSubtitle(subtitle);

		Calendar c_from = Calendar.getInstance();
		Calendar c_to = Calendar.getInstance();
		String sz_from = new Long(filter.getFromDate()).toString();
		String sz_to = new Long(filter.getToDate()).toString();
		c_from.set(new Integer(sz_from.substring(0, 4)), new Integer(sz_from.substring(4,6))-1, new Integer(sz_from.substring(6,8)));
		c_to.set(new Integer(sz_to.substring(0, 4)), new Integer(sz_to.substring(4,6))-1, new Integer(sz_to.substring(6,8)));
		String sz_day_from = c_from.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, PAS.locale);
		String sz_day_to = c_to.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, PAS.locale);
		subtitle = new TextTitle(sz_day_from + " " + TextFormat.format_date(filter.getFromDate()) + " - " + sz_day_to + " " + TextFormat.format_date(filter.getToDate()));
		subtitle.setFont(new Font(UIManager.getString("Common.Fontface"), Font.PLAIN, 10));
		chart.addSubtitle(subtitle);

		//chart = ChartFactory.createStackedBarChart(title, "Date", "Tourists", cat_dataset, orientation, true, true, false);
		//chart = ChartFactory.createAreaChart(title, "Date", "Tourists", cat_dataset, PlotOrientation.VERTICAL, true, true, false);
		 

		
		switch(getChartType())
		{
		case LINECHART:
		case LINECHART_3D:
		case BARCHART:
		case BARCHART_3D:
		case AREA_CHART:
		case STACKED_AREA_CHART:
		case STACKED_BARCHART:
		case STACKED_BARCHART_3D:
			final CategoryPlot plot = (CategoryPlot) chart.getPlot();
			
			
			CategoryItemRenderer catitem_renderer = (CategoryItemRenderer)plot.getRenderer();
			catitem_renderer.setBaseToolTipGenerator(new CategoryToolTipGenerator()
	        {

				@Override
				public String generateToolTip(CategoryDataset arg0,
						int arg1, int arg2) {
					try
					{
						CategoryKey c = (CategoryKey)arg0.getColumnKey(arg2);
						
						GroupKey gk = (GroupKey)arg0.getRowKey(arg1);
						Number n = arg0.getValue(gk, c);
						return "<html>" + c.key + "<br>" + gk.key + "<br><b>" + n.intValue() + "</b></html>";
					}
					catch(Exception e)
					{
						return " ";
					}
				}
	        	
	        });

			
			if(plot.getRenderer().getClass().equals(BarRenderer.class))
			{
		        // disable bar outlines...
		        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
		        renderer.setDrawBarOutline(false);
		        //renderer.setItemMargin(0.10);
		        //final ItemLabelPosition p = new ItemLabelPosition(
		        //        ItemLabelAnchor.INSIDE12, TextAnchor.CENTER_RIGHT, 
		        //        TextAnchor.CENTER_RIGHT, -Math.PI / 2.0
		        //    );
		        //    renderer.setPositiveItemLabelPosition(p);				
			}
			else if(plot.getRenderer().getClass().equals(LineAndShapeRenderer.class))
			{
				final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		//      renderer.setDrawShapes(true);
				int categories = chart.getCategoryPlot().getDataset().getRowCount();
				for(int i=0; i < categories; i++)
				{
				      renderer.setSeriesStroke(
				          i, new BasicStroke(
				              3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				              1.0f, new float[] {1.0f, 1.0f}, 0.0f
				          )
				      );
				}
			}
			break;
		}
		
		
		/*ValueAxis domainAxis = new DateAxis(filter.getGroupTimeunit().value());
		domainAxis.setMinorTickCount(0);
		DateAxis temp_axis = (DateAxis)domainAxis;
		switch(filter.getGroupTimeunit())
		{
		case PER_HOUR:
			temp_axis.setDateFormatOverride(new SimpleDateFormat("dd"));
			break;
		case PER_DAY:
			temp_axis.setDateFormatOverride(new SimpleDateFormat("dd.MM.yyyy"));
			break;
		case PER_MONTH:
			temp_axis.setDateFormatOverride(new SimpleDateFormat("MM.yyyy"));
			break;
		}*/
		
		//CategoryAxis axis = new CategoryAxis(filter.getStatFunction().value() + " / " + filter.getGroupTimeunit().value());
		String axisTitle = PAS.l("main_tas_stats_dbfunc_" + filter.getStatFunction().toString());
		if(filter.getRowcount()>0)
			axisTitle += " " + filter.getRowcount();
		else
			axisTitle += " " + PAS.l("main_tas_stats_rowcount_ALL");
		axisTitle += " " + PAS.l("main_tas_stats_data_" + filter.getGroupTimeunit().toString());
		CategoryAxis axis = new CategoryAxis(axisTitle);
		//axis.setCategoryLabelPositionOffset(10);
		//axis.setCategoryMargin(CategoryAxis.DEFAULT_CATEGORY_MARGIN);
		axis.setMinorTickMarksVisible(false);
		axis.setTickLabelsVisible(true);
		axis.setTickMarksVisible(true);
		axis.setTickLabelFont(new Font(UIManager.getString("Common.Fontface"), Font.PLAIN, 8)); 
		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		//axis.setCategoryLabelPositionOffset(10);
		axis.setTickLabelsVisible(true);

		if(chart.getPlot().getClass().equals(CategoryPlot.class))
		{
			chart.getCategoryPlot().setRangePannable(true);
			chart.getCategoryPlot().setDomainAxis(axis);
		}
		//HorizontalCategoryAxi domainAxis = (HorizontalCategoryAxis)plot.getDomainAxis();
		//chart.getCategoryPlot().set
		
		//chart.getCategoryPlot().setForegroundAlpha(0.5f);
		chart.setBorderVisible(true);
		//domainAxis.setLowerMargin(0.0);
        //domainAxis.setUpperMargin(0.0);
		
		
		
		
        getChart().fireChartChanged();
        boolean b_first = updateChartPanel();
        if(callback!=null)
        {
        	if(b_first)
        		callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, CALLBACK_FIRST_CHART_COMPLETE));
        	else
        		callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, CALLBACK_CHART_UPDATED));
        }
        b_filter_changed = false;
        //UMSChartFrame frame = new UMSChartFrame("Statistics", getChart(), true);
	}
	
	
}