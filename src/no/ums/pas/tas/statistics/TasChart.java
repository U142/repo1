package no.ums.pas.tas.statistics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import no.ums.pas.importer.csv.csvexporter;
import no.ums.ws.pas.tas.ArrayOfULBACOUNTRY;
import no.ums.ws.pas.tas.ArrayOfULBACOUNTRYSTATISTICS;
import no.ums.ws.pas.tas.ULBACONTINENT;
import no.ums.ws.pas.tas.ULBACOUNTRY;
import no.ums.ws.pas.tas.ULBAFILTERSTATFUNCTION;
import no.ums.ws.pas.tas.ULBAFILTERSTATTIMEUNIT;
import no.ums.ws.pas.tas.ULBASTATISTICSFILTER;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramBin;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.Statistics;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.TableOrder;



public abstract class TasChart implements ActionListener
{
	public static final String CALLBACK_CHART_UPDATED = "act_chart_updated";
	public static final String CALLBACK_FIRST_CHART_COMPLETE = "act_first_chart_complete";

	public enum STATS_CHARTTYPE
	{
		LINECHART,
		LINECHART_3D,
		BARCHART,
		BARCHART_3D,
		STACKED_BARCHART,
		STACKED_BARCHART_3D,
		MULTIPLE_PAI,
		MULTIPLE_PAI_3D,
		AREA_CHART,
		STACKED_AREA_CHART,
	}
	public enum STATS_RESULTS_GROUPBY
	{
		NONE,
		TIMEUNIT,
		COUNTRY,
		YEAR,
		MONTH,
		WEEK,
		DAY,
		OPERATOR,
	}
	JFreeChart chart;
	public JFreeChart getChart() { return chart; }
	protected ActionListener callback;
	protected abstract void createChart();
	protected abstract void downloadData();
	@Override
	public abstract void actionPerformed(ActionEvent e);
	
	protected UMSChartPanel chartpanel;
	/**
	 * 
	 * @return true if this is the first chart
	 * @return false if chart is updated
	 */
	protected boolean updateChartPanel()
	{
		if(chartpanel==null)
		{
			chartpanel = new UMSChartPanel(getChart());
			return true;
		}
		else
		{
			getChart().fireChartChanged();
			return false;
		}
	}
	
	protected List<ULBACOUNTRY> country;
	protected ULBASTATISTICSFILTER filter;
	protected ArrayOfULBACOUNTRYSTATISTICS results;
	protected String sz_location = "";
	protected STATS_RESULTS_GROUPBY groupby;
	protected STATS_RESULTS_GROUPBY group_operator = STATS_RESULTS_GROUPBY.NONE;
	protected STATS_CHARTTYPE charttype = STATS_CHARTTYPE.LINECHART;
	protected boolean b_need_data_update = true;
	protected boolean b_filter_changed = false;
	protected abstract void ExportStats(csvexporter csv); //is only executed from the common Export function
	public void Export()
	{
		try
		{
			csvexporter exp = new csvexporter(null, "");
			ExportStats(exp);
		}
		catch(Exception err)
		{
			
		}
	}
	
	public void UpdateChart()
	{
		if(getNeedDataUpdate())
		{
			//will run createChart after download_finished
			downloadData();
		}
		else
		{
			//will in turn exec updateChartPanel
			createChart();
		}
	}
	
	
	
	/*
	 * 
	 * FILTER FUNCTIONS
	 */
	public void setFilterFromDate(long n)
	{
		filter.setFromDate(n);
	}
	public void setFilterToDate(long n)
	{
		filter.setToDate(n);
	}
	public void setFilterStatFunction(ULBAFILTERSTATFUNCTION f)
	{
		filter.setStatFunction(f);
	}
	
	public void setFilterRowCount(int n)
	{
		filter.setRowcount(n);
	}

	public void setFilterCountryList(List<ULBACOUNTRY> c)
	{
		this.country = c;
		ArrayOfULBACOUNTRY countries = new ArrayOfULBACOUNTRY();
		for(int i=0; i < country.size(); i++)
			countries.getULBACOUNTRY().add(this.country.get(i));
		if(country.size()==1)
			sz_location = country.get(0).getSzName();
		else
			sz_location = "Multiple countries";
		filter.setCountries(countries);
	}
	public void setFilterCountryList(ULBACONTINENT c)
	{
		ArrayOfULBACOUNTRY countries = new ArrayOfULBACOUNTRY();
		for(int i=0; i < c.getCountries().getULBACOUNTRY().size(); i++)
			countries.getULBACOUNTRY().add(c.getCountries().getULBACOUNTRY().get(i));
		
		sz_location = c.getSzName();
		filter.setCountries(countries);
	}
	public void setFilterGroupTimeUnit(ULBAFILTERSTATTIMEUNIT t)
	{
		filter.setGroupTimeunit(t);
		/*switch(t)
		{
		case PER_HOUR:
			setResultsGroupBy(STATS_RESULTS_GROUPBY.DAY);
			break;
		case PER_DAY:
			setResultsGroupBy(STATS_RESULTS_GROUPBY.MONTH);
			break;
		case PER_MONTH:
			setResultsGroupBy(STATS_RESULTS_GROUPBY.YEAR);
			break;
			
		}*/
	}
	

	
	
	public void setFilterChanged()
	{
		b_filter_changed = true;
		UpdateChart();
	}
	
	/*
	 * PRESENT RESULTS GROUPED BY 
	 */
	public void setResultsGroupBy(STATS_RESULTS_GROUPBY group)
	{
		groupby = group;
	}
	public STATS_RESULTS_GROUPBY getResultsGroupBy()
	{
		return groupby;
	}
	public void setGroupByOperator(boolean b)
	{
		group_operator = (b ? STATS_RESULTS_GROUPBY.OPERATOR : STATS_RESULTS_GROUPBY.NONE);
	}
	public boolean getGroupByOperator()
	{
		return (group_operator.equals(STATS_RESULTS_GROUPBY.OPERATOR) ? true : false);
	}
	public void setChartType(STATS_CHARTTYPE type)
	{
		charttype = type;
	}
	public STATS_CHARTTYPE getChartType()
	{
		return charttype;
	}

	
	
	protected void setNeedDataUpdate()
	{
		b_need_data_update = true;
		setFilterChanged();
	}
	protected boolean getNeedDataUpdate()
	{
		return b_need_data_update;
	}
	
	

	
	public TasChart(ActionListener callback)
	{
		filter = new ULBASTATISTICSFILTER();		
		this.callback = callback;
		/*
		//HistogramDataset dataset = new HistogramDataset();
		//double[] data = new double [] { 1, 2, 3, 4 };
		//dataset.addSeries("test", data, 4);
		//chart = ChartFactory.createHistogram("Tourist histogram", "Day", "Tourists", dataset, PlotOrientation.VERTICAL, true, true, false);		
		
		//XYSeries series = new XYSeries("Tourists");
		//series.add(20100208, 500);
		//series.add(20100209, 1500);
		//XYDataset xyDataset = new XYSeriesCollection(series);
		//chart = ChartFactory.createLineChart("Line chart", "Day", "Tourists", dataset, PlotOrientation.VERTICAL, "Tourists", false, false);
		//chart = ChartFactory.createTimeSeriesChart("Tourists", "Date", "Tourists", xyDataset, true, false, false);
		//TimeSeries t = new TimeSeries("Tourists","", "", Day.class);
		//ChartFactory.createWaferMapChart(title, dataset, orientation, legend, tooltips, urls)

		//TIME SERIES

		TimeSeries aggregate = new TimeSeries("All");
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
		
		//XYDATASET
		XYSeries xy = new XYSeries("Telenor", true, false);
		XYDataItem xydataitem = new XYDataItem(new Day(8,2,2010).getDayOfMonth(), 100);
		xy.add(xydataitem);
		XYSeriesCollection xycoll = new XYSeriesCollection();
		xycoll.addSeries(xy);

		//TABLEXY
		DefaultTableXYDataset tablexy = new DefaultTableXYDataset();
		XYSeries tablexyseries1 = new XYSeries("Telenor", true, false);
		tablexyseries1.add(20100208, 100);
		tablexyseries1.add(20100209, 150);
		tablexy.addSeries(tablexyseries1);
		XYSeries tablexyseries2 = new XYSeries("Netcom", true, false);
		tablexyseries2.add(20100208, 50);
		tablexyseries2.add(20100209, 10);
		tablexy.addSeries(tablexyseries2);
		
		//CATEGORY
		DefaultCategoryDataset cat_dataset = new DefaultCategoryDataset();
		cat_dataset.addValue(100, "Telenor", new Day(8,2,2010));
		cat_dataset.addValue(40, "Netcom", new Day(8,2,2010));
		cat_dataset.addValue(120, "Telenor", new Day(9,2,2010));
		cat_dataset.addValue(42, "Netcom", new Day(9,2,2010));
		cat_dataset.addValue(85, "Telenor", new Day(10,2,2010));
		cat_dataset.addValue(24, "Netcom", new Day(10,2,2010));
		cat_dataset.addValue(95, "Telenor", new Day(11,2,2010));
		cat_dataset.addValue(30, "Netcom", new Day(11,2,2010));
		
		
		
		//chart = ChartFactory.createTimeSeriesChart("Tourists", "Date", "Tourists", coll, true, true, false);
		//chart = ChartFactory.createXYAreaChart("Tourists", "", "", coll, PlotOrientation.VERTICAL, true, true, false);

		//chart = ChartFactory.createAreaChart("Tourists", "Operator", "Tourists", coll, PlotOrientation.VERTICAL, true, true, false);
		//chart = ChartFactory.createGanttChart("", "", "", coll, true, true, false);
		//chart = ChartFactory.createBoxAndWhiskerChart("Tourists", "Date", "Tourists", coll, true);
		//chart = ChartFactory.createStackedXYAreaChart("Tourists", "Date", "Tourists", tablexy, PlotOrientation.VERTICAL, true, true, false);
		chart = ChartFactory.createBarChart3D("Tourists", "Date", "Tourists", cat_dataset, PlotOrientation.VERTICAL, true, true, false);
		//chart = ChartFactory.createMultiplePieChart3D("Tourists", cat_dataset, TableOrder.BY_ROW, true, true, false);
		chart = ChartFactory.createStackedBarChart("Tourists", "Date", "Tourists", cat_dataset, PlotOrientation.VERTICAL, true, true, false);
		//chart = ChartFactory.createStackedAreaChart("Tourists", "Date", "Tourists", cat_dataset, PlotOrientation.VERTICAL, true, true, false);
		//chart = ChartFactory.createWaterfallChart("Tourists", "Date", "Tourists", cat_dataset, PlotOrientation.VERTICAL, true, true, false);
		
		//XYPlot plot = chart.getXYPlot();
		//XYItemRenderer renderer = plot.getRenderer();
		ValueAxis domainAxis = new DateAxis("Day");
		DateAxis temp_axis = (DateAxis)domainAxis;
		temp_axis.setDateFormatOverride(new SimpleDateFormat("dd.MM.yyyy"));
		
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        //plot.setDomainAxis(domainAxis);
        //plot.setForegroundAlpha(1.0f); */
	}
}