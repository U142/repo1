package no.ums.pas.importer.csv;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFileChooser;

import no.ums.pas.PAS;
import no.ums.pas.importer.gis.*;
import no.ums.pas.ums.tools.FilePicker;



public class csvexporter extends Object
{
	protected File file;
	LineData lines = new LineData(",");
	Hashtable<String, Class> exclude_classes = new Hashtable<String, Class>();
	/**
	 * 
	 * @param filename
	 * directly specify filename for saving
	 */
	public csvexporter(String filename) throws Exception
	{
		super();
		file = new File(filename);
		try
		{
			checkFile(file);
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	public void addExcludeClass(Class classtype)
	{
		exclude_classes.put(classtype.getName(), classtype);
	}
	
	public Hashtable<String, Class> getExcluded() {
		return exclude_classes;
	}
	
	/**
	 * 
	 * @param parent
	 * @param current_dir
	 * parent defines parent window
	 * current_dir defines the default path for file picker
	 */
	public csvexporter(Component parent, String current_dir) throws Exception
	{
		super();
		String [][] sz_filter = new String [][] 
		                                   {
				{ "Comma Separated Values", ".csv" }
		                                   };
		try
		{
			FilePicker fp = new FilePicker(parent, null, current_dir, "Save to CSV", sz_filter, FilePicker.MODE_SAVE_);
			file = fp.getSelectedFile();
			if(file==null)
				throw new FileNotFoundException();
			checkFile(file);
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	protected boolean checkFile(File f) throws Exception
	{
		try
		{
			//if(!f.canWrite())
			//	throw new Exception("Can not write to file");
			return true;
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	/**
	 * Add line to CSV file. Each object value will be separated by commas
	 * @param obj
	 * 
	 * String objects will be enclosed by "
	 * All other object types will be defined by toString()
	 */
	public void addLine(Object [] obj)
	{
		String linedata = "";
		for(int i=0; i < obj.length; i++)
		{
			if(i>0)
				linedata += lines.getSeparator();
			Object o = obj[i];
			if(o==null)
				linedata += "NULL";
			if(exclude_classes.containsKey(o.getClass().getName()))
			{
				//Remove separator
				linedata = linedata.substring(0, linedata.length()-1);
				//EXCLUDED
			}
			else if(o.getClass().equals(String.class))
			{
				linedata += "\"" + o.toString() + "\"";
			}
			else
				linedata += o.toString();
		}
		lines.add_line(linedata);
	}
	
	public boolean Save() throws Exception
	{
		try
		{
			FileWriter fw = new FileWriter(file);
			for(int i=0; i < lines.get_lines().size(); i++)
			{
				fw.write(lines.get(i).getRawdata());
				fw.write("\n");
			}
			fw.close();
			return true;
		}
		catch(Exception e)
		{
			throw e;
		}
	}
}