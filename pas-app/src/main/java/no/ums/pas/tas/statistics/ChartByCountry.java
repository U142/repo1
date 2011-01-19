package no.ums.pas.tas.statistics;

import no.ums.pas.importer.csv.csvexporter;
import no.ums.ws.pas.tas.ULBACOUNTRY;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ChartByCountry extends TasChart
{
	protected List<ULBACOUNTRY> countries;
	public ChartByCountry(List<ULBACOUNTRY> countries, ActionListener callback)
	{
		super(callback);
		this.countries = countries;
		createChart();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
	@Override
	protected void downloadData() {
		
	}
	
	@Override
	protected void ExportStats(csvexporter csv) {
		
	}
	protected void createChart()
	{
		DefaultCategoryDataset cat_dataset = new DefaultCategoryDataset();
		for(int i=0; i < countries.size(); i++)
		{
			//cat_dataset.addValue(100, "Telenor", new Day(8,2,2010));
			//cat_dataset.addValue(40, "Netcom", new Day(8,2,2010));
			for(int op=0; op < countries.get(i).getOperators().getUTOURISTCOUNT().size(); op++)
			{
				cat_dataset.addValue(countries.get(i).getOperators().getUTOURISTCOUNT().get(op).getLTouristcount(), 
						countries.get(i).getOperators().getUTOURISTCOUNT().get(op).getSzOperator(), 
						countries.get(i).getSzName());
			}
		}
		/*cat_dataset.addValue(120, "Telenor", new Day(9,2,2010));
		cat_dataset.addValue(42, "Netcom", new Day(9,2,2010));
		cat_dataset.addValue(85, "Telenor", new Day(10,2,2010));
		cat_dataset.addValue(24, "Netcom", new Day(10,2,2010));
		cat_dataset.addValue(95, "Telenor", new Day(11,2,2010));
		cat_dataset.addValue(30, "Netcom", new Day(11,2,2010));*/
		
		chart = ChartFactory.createStackedBarChart("Tourists per Country", "Country", "Tourists", cat_dataset, PlotOrientation.VERTICAL, true, true, false);
		//chart.getXYPlot().setOrientation(PlotOrientation.HORIZONTAL);
		chart.getCategoryPlot().setOrientation(PlotOrientation.HORIZONTAL);
		
		//ValueAxis domainAxis = new DateAxis("Day");
		//DateAxis temp_axis = (DateAxis)domainAxis;
		//temp_axis.setDateFormatOverride(new SimpleDateFormat("dd.MM.yyyy"));
		
        //domainAxis.setLowerMargin(0.0);
        //domainAxis.setUpperMargin(0.0);

	}
}