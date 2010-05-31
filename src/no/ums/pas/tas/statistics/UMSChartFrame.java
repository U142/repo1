package no.ums.pas.tas.statistics;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.tas.statistics.TasChart.STATS_CHARTTYPE;
import no.ums.pas.tas.statistics.TasChart.STATS_RESULTS_GROUPBY;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.ws.pas.tas.ULBACONTINENT;
import no.ums.ws.pas.tas.ULBACOUNTRY;
import no.ums.ws.pas.tas.ULBAFILTERSTATFUNCTION;
import no.ums.ws.pas.tas.ULBAFILTERSTATTIMEUNIT;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;



public class UMSChartFrame extends JFrame implements ActionListener, ComponentListener
{
	@Override
	public void componentHidden(ComponentEvent e) {
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		doResize(new Dimension(getWidth(), getHeight()));
	}
	
	protected void doResize(Dimension d)
	{
		if(filterandstatspanel!=null)
		{
			filterandstatspanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
			filterandstatspanel.setSize(new Dimension(getWidth(), getHeight()));
		}
		if(panel!=null)
		{
			int w = getWidth() - 310;
			int h = getHeight() - 110;
			panel.setPreferredSize(new Dimension(w, h));
			panel.setSize(new Dimension(w, h));
			
		}
		if(filterpanel!=null)
		{
			//filterpanel.setPreferredSize(new Dimension(300, getHeight()-20));
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
	}

	JScrollPane scroll;
	boolean b_scroll;
	TasChart chart;
	UMSChartPanel panel;
	TasFilterPanel filterpanel;
	FilterAndStatsPanel filterandstatspanel;
	JLabel lbl_loading = new JLabel(ImageLoader.load_icon("refresh_64.png"));
	protected class FilterAndStatsPanel extends DefaultPanel
	{
		protected FilterAndStatsPanel()
		{
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
		}

		@Override
		public void add_controls() {
			
		}

		@Override
		public void init() {
			
		}
		
	}
	protected UMSChartFrame(String title, boolean b_scroll)
	{
		super(title);
		
		this.b_scroll = b_scroll;
		this.setIconImage(PAS.get_pas().getIconImage());
		Dimension ul = no.ums.pas.ums.tools.Utils.paswindow_upperleft(1200, 700);

		filterpanel = new TasFilterPanel(this);
		chart = new ChartOverTime(this);
		chart.setFilterFromDate(filterpanel.getFilterDateFrom());
		chart.setFilterToDate(filterpanel.getFilterDateTo());
		chart.setChartType(filterpanel.getSelectedChartType());
		chart.setFilterStatFunction(filterpanel.getFilterFunction());
		chart.setGroupByOperator(filterpanel.getGroupByOperator());
		chart.setFilterGroupTimeUnit(filterpanel.getFilterTimeUnit());
		chart.setResultsGroupBy(filterpanel.getResultsGroupBy());
		
		/*chart.setFilterFromDate(Long.parseLong("20100210000000"));
		chart.setFilterToDate(Long.parseLong("20110213000000"));
		chart.setChartType(STATS_CHARTTYPE.LINECHART);
		chart.setFilterStatFunction(ULBAFILTERSTATFUNCTION.STAT_AVERAGE);
		chart.setGroupByOperator(false);
		chart.setResultsGroupBy(STATS_RESULTS_GROUPBY.MONTH);
		chart.setFilterGroupTimeUnit(ULBAFILTERSTATTIMEUNIT.PER_DAY);
		*/
		filterandstatspanel = new FilterAndStatsPanel();
		filterandstatspanel.set_gridconst(0, 0, 1, 1);
		filterandstatspanel.add(filterpanel, filterandstatspanel.m_gridconst);
		filterandstatspanel.add_spacing(filterandstatspanel.DIR_HORIZONTAL, 20);
		filterandstatspanel.set_gridconst(2, 0, 1, 1); //make ready for chart
		getContentPane().add(filterandstatspanel, BorderLayout.CENTER);
		setBounds(ul.width, ul.height, 1200, 700);
		setVisible(true);
		addComponentListener(this);
	}
	
	public UMSChartFrame(String title, boolean b_scroll, ULBACONTINENT c)
	{
		this(title, b_scroll);
		chart.setFilterCountryList(c);
		chart.UpdateChart();
		filterandstatspanel.m_gridconst.anchor = GridBagConstraints.CENTER;
		filterandstatspanel.add(lbl_loading, filterandstatspanel.m_gridconst);
		
		//panel = new UMSChartPanel(chart.getChart());
		/*if(b_scroll)
		{
			scroll = new JScrollPane(panel);
			this.add(scroll);
		}
		else
		{
			this.add(panel);
		}*/
		
	}
	
	public UMSChartFrame(String title, boolean b_scroll, List<ULBACOUNTRY> countries)
	{
		this(title, b_scroll);
		chart.setFilterCountryList(countries);
		chart.UpdateChart();
		filterandstatspanel.m_gridconst.anchor = GridBagConstraints.CENTER;
		filterandstatspanel.add(lbl_loading, filterandstatspanel.m_gridconst);
		
	}
	
	private void setNeedDataUpdate()
	{
		chart.setNeedDataUpdate();		
		showLoader(true);
	}
	private void setFilterChanged()
	{
		chart.setFilterChanged();
		showLoader(true);

	}
	private void showLoader(boolean b)
	{
		lbl_loading.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(TasChart.CALLBACK_CHART_UPDATED.equals(e.getActionCommand()))
		{
			//getContentPane().remove(scroll);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					filterandstatspanel.remove(scroll);
					panel = new UMSChartPanel(chart.getChart());
					scroll = new JScrollPane(panel);
					filterandstatspanel.add(scroll, filterandstatspanel.m_gridconst);
					
					//chart.createChart();
					//chart.getChart().fireChartChanged();
					//scroll.add(panel);
					
					filterandstatspanel.invalidate();
					filterandstatspanel.repaint();
					filterandstatspanel.validate();
					//filterandstatspanel.setSize(new Dimension(getWidth(), getHeight()));
					doResize(new Dimension(getWidth(), getHeight()));
					UMSChartFrame.this.setPreferredSize(new Dimension(getWidth(), getHeight()));
					UMSChartFrame.this.invalidate();
					UMSChartFrame.this.repaint();
					showLoader(false);
					pack();
					
				}
			});
		}
		else if(TasChart.CALLBACK_FIRST_CHART_COMPLETE.equals(e.getActionCommand()))
		{
			panel = new UMSChartPanel(chart.getChart());
			if(b_scroll)
			{
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						scroll = new JScrollPane(panel);
						//getContentPane().add(scroll, BorderLayout.EAST);
						//getContentPane().add(filterpanel, BorderLayout.WEST);
						filterandstatspanel.add(scroll, filterandstatspanel.m_gridconst);
						/*scroll.revalidate();
						scroll.repaint();
						panel.revalidate();
						panel.repaint();
						pack();*/
						doResize(new Dimension(getWidth(), getHeight()));
						UMSChartFrame.this.setPreferredSize(new Dimension(getWidth(), getHeight()));
						UMSChartFrame.this.invalidate();
						UMSChartFrame.this.repaint();
						showLoader(false);
						pack();

					}
				});
			}
			else
			{
				this.add(panel);
				scroll.revalidate();
			}
		}
		/*FROM FILTERPANEL*/
		else if(TasFilterPanel.ACT_CHARTTYPE_CHANGED.equals(e.getActionCommand()))
		{
			if(chart!=null && filterpanel!=null)
			{
				chart.setChartType((STATS_CHARTTYPE)e.getSource());
				setFilterChanged();
			}
		}
		else if(TasFilterPanel.ACT_GROUP_TIMEUNIT_CHANGED.equals(e.getActionCommand()))
		{
			if(chart!=null && filterpanel!=null)
			{
				chart.setFilterGroupTimeUnit((ULBAFILTERSTATTIMEUNIT)e.getSource());
				setNeedDataUpdate();
			}			
		}
		else if(TasFilterPanel.ACT_GROUPING_CHANGED.equals(e.getActionCommand()))
		{
			if(chart!=null && filterpanel!=null)
			{
				chart.setResultsGroupBy((STATS_RESULTS_GROUPBY)e.getSource());
				setFilterChanged();
			}						
		}
		else if(TasFilterPanel.ACT_FILTER_FUNCTION_CHANGED.equals(e.getActionCommand()))
		{
			if(chart!=null && filterpanel!=null)
			{
				chart.setFilterStatFunction((ULBAFILTERSTATFUNCTION)e.getSource());
				setNeedDataUpdate();				
			}						
		}
		else if(TasFilterPanel.ACT_FILTER_ROWCOUNT_CHANGED.equals(e.getActionCommand()))
		{
			if(chart!=null && filterpanel!=null)
			{
				chart.setFilterRowCount((Integer)e.getSource());
				setNeedDataUpdate();				
			}									
		}
		else if(TasFilterPanel.ACT_GROUP_BY_OPERATOR_CHANGED.equals(e.getActionCommand()))
		{
			if(chart!=null && filterpanel!=null)
			{
				Boolean b = (Boolean)e.getSource();
				chart.setGroupByOperator(b.booleanValue());
				setFilterChanged();
			}			
		}
		else if(TasFilterPanel.ACT_TIMEFILTER_FROM_CHANGED.equals(e.getActionCommand()))
		{
			if(chart!=null && filterpanel!=null)
			{
				Long n = (Long)e.getSource();
				chart.setFilterFromDate(n);
				setNeedDataUpdate();
			}			
		}
		else if(TasFilterPanel.ACT_TIMEFILTER_TO_CHANGED.equals(e.getActionCommand()))
		{
			if(chart!=null && filterpanel!=null)
			{
				Long n = (Long)e.getSource();
				chart.setFilterToDate(n);
				setNeedDataUpdate();
			}			
		}
		else if(TasFilterPanel.ACT_EXPORT_STATS.equals(e.getActionCommand()))
		{
			chart.Export();
		}
	}
	
	
}