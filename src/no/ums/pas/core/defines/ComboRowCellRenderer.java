package no.ums.pas.core.defines;

import javax.swing.*;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.table.AbstractTableModel;
import javax.swing.JTable;

public class ComboRowCellRenderer extends JTable implements ListCellRenderer {

	private JPanel renderer;
	private Component [] cols;
	public ComboRowCellRenderer(Class<Component> [] columns, int [] width, int height) {
	
		renderer = new JPanel();
		renderer.setLayout(new BoxLayout(renderer, BoxLayout.X_AXIS) );
		cols = new Component[columns.length];
		for(int i=0; i < cols.length; i++)
		{
			Dimension d = new Dimension(width[i], height);
			//d.width = d_totalsize.width;
			if(columns[i].equals(JLabel.class))
			{
				cols[i] = new JLabel("");
			}
			else if(columns[i].equals(JProgressBar.class))
			{
				cols[i] = new JProgressBar(0, 0);
				JProgressBar temp = (JProgressBar)cols[i];
				temp.setStringPainted(true);
			}
			else if(columns[i].equals(ImageIcon.class))
			{
				cols[i] = new JLabel("");
			}
			else
			{
				cols[i] = new JLabel("");
			}
			
			//cols[i] = new Component;
			cols[i].setMaximumSize(d);
			cols[i].setPreferredSize(d);
			//cols[i].setAlignmentX(JLabel.LEFT_ALIGNMENT);
			//cols[i].setHorizontalTextPosition(JLabel.LEFT);
			//cols[i].setHorizontalAlignment(JLabel.LEFT);
			/*Dimension d = first.getPreferredSize();
			d.width = firstColumnWidth;
			first.setMaximumSize(d);
			second = new JLabel();*/
			renderer.add(cols[i]);
		}
			//renderer.add(second );
		
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	}
	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setModel(new ComboRowTableModel((ComboRow) value, cols.length));
		if (isSelected) {
			getSelectionModel().setSelectionInterval(0, 0);
		}
		ComboRow item = (ComboRow)value;
	
		for(int i=0; i < cols.length; i++)
		{
			try
			{
				if(item.getVals()[i].getClass().equals(JLabel.class))
				{
					((JLabel)cols[i]).setText(((JLabel)item.getVals()[i]).getText());
					//cols[i] = (JLabel)item.getVals()[i];
				}
				else if(item.getVals()[i].getClass().equals(ImageIcon.class))
				{
					if(item.getVals()[i]!=null)
						((JLabel)cols[i]).setIcon((Icon)item.getVals()[i]);
					else
						((JLabel)cols[i]).setIcon(null);
				}
				else if(item.getVals()[i].getClass().equals(JProgressBar.class))
				{
					//return (JProgressBar)item.getVals()[i];
					//return renderer;
					//cols[i] = (JProgressBar)item.getVals()[i];
					int val = ((JProgressBar)item.getVals()[i]).getValue();
					((JProgressBar)cols[i]).setMinimum(((JProgressBar)item.getVals()[i]).getMinimum());
					((JProgressBar)cols[i]).setMaximum(((JProgressBar)item.getVals()[i]).getMaximum());
					((JProgressBar)cols[i]).setValue(((JProgressBar)item.getVals()[i]).getValue());
					return renderer;
				}
				else
					((JLabel)cols[i]).setText( item.getVals()[i].toString() );
					//cols[i] = (JLabel)item.getVals()[i];
			}
			catch(Exception e)
			{
				
			}
		}
		renderer.setBackground(isSelected ? list.getSelectionBackground() : null);
		renderer.setForeground(isSelected ? list.getSelectionForeground() : null);

	
		return renderer;	
	}
}