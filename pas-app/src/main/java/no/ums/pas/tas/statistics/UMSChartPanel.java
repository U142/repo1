package no.ums.pas.tas.statistics;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class UMSChartPanel extends ChartPanel implements ComponentListener
{
	protected JFreeChart m_chart;
	public UMSChartPanel(JFreeChart chart)
	{
		super(chart, true, true, true, true, true);
		m_chart = chart;
		addComponentListener(this);
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		
	}
	@Override
	public void componentMoved(ComponentEvent e) {
		
	}
	@Override
	public void componentResized(ComponentEvent e) {
		
		//int w = getWidth();
		//int h= getHeight();
		//this.setPreferredSize(new Dimension(w, h));
	}
	@Override
	public void componentShown(ComponentEvent e) {
		
	}
}