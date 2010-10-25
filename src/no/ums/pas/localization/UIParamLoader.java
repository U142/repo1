package no.ums.pas.localization;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.InsetsUIResource;

import no.ums.pas.PAS;
import no.ums.pas.ums.errorhandling.Error;

import org.opengis.parameter.ParameterNotFoundException;


public class UIParamLoader extends ClassLoader
{
	protected static boolean parseParams(Properties b)
	{
		try
		{
			UIDefaults defaults = UIManager.getDefaults();
			
			defaults.put("OptionPane.cancelButtonText", PAS.l("common_cancel"));
			defaults.put("OptionPane.noButtonText", PAS.l("common_no"));
			defaults.put("OptionPane.okButtonText", PAS.l("common_ok"));
			defaults.put("OptionPane.yesButtonText", PAS.l("common_yes"));
			/*
			 * List defaults
			 * 
			 * Enumeration<Object> def_el = defaults.keys();
			while(def_el.hasMoreElements())
			{
				System.out.println(def_el.nextElement().toString());
			}*/
			
			Enumeration<Object> keys = b.keys(); //b.getKeys();
			while(keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				if(key.equals("TabbedPane.tabInsets"))
					System.out.println("break");
				try
				{
					//String value = b.getString(key);
					Object obj_type = new Object();
					Object obj_value = b.getProperty(key);
					String value = "";
					if(obj_value instanceof String)
					{
						value = (String)obj_value;
						value = value.trim();
					}
					try
					{
						obj_value = Boolean.parseBoolean(value.toString());
					}
					catch(Exception e)
					{
						
					}
					try
					{
						obj_value = Integer.parseInt(value.toString());
					}
					catch(Exception e)
					{
						
					}
					try
					{
						obj_value = Color.decode(value.toString());
					}
					catch(Exception e)
					{
						
					}

					//find object type of key
					Object wanted_valuetype = defaults.get(key);
					if(wanted_valuetype instanceof Font)
					{
						if(value!=null && value.length()>0)
						{
							String commonfont = b.getProperty("Common.Fontface");
							String replacefont = value.substring(value.indexOf("-")+1);
							value = commonfont + "-" + replacefont;
							UIManager.put(key, Font.decode(value));				
						}
					} 
					else if(wanted_valuetype instanceof Color)
					{
						if(value!=null && value.length()>0)
							UIManager.put(key, Color.decode(value));
					}
					else if(key.toLowerCase().indexOf("font")>=0)//default to font
					{
						String commonfont = b.getProperty("Common.Fontface");
						if(value.indexOf("-")>=0)
						{
							String replacefont = value.substring(value.indexOf("-")+1);
							value = commonfont + "-" + replacefont;
							UIManager.put(key, Font.decode(value));
						}
						else
							UIManager.put(key, commonfont);
					}
					else if(obj_value instanceof Integer)
					{
						Integer i = Integer.parseInt(value);
						UIManager.put(key, i);
					}
					else if(obj_value instanceof Boolean)
					{
						Boolean bo = Boolean.parseBoolean(value);
						UIManager.put(key, bo);
					}
					else if(obj_value instanceof String)
					{
						UIManager.put(key, obj_value);
					}
					else if(wanted_valuetype instanceof InsetsUIResource)
					{
						Integer val = Integer.parseInt(value);
						InsetsUIResource i = new InsetsUIResource(val, val, val, val);
						UIManager.put(key, i);
					}
					else if(wanted_valuetype==null)
					{
						//if(key.indexOf("foreground")>0 || key.indexOf("background")>0)
						{
							//obj_value = Color.decode(obj_value.toString());
							UIManager.put(key, obj_value);
						}
					}

					/*if(key.indexOf("font")>=0)
					{
						if(value.length()>0)
							UIManager.put(key, Font.decode(value));
					}
					else if(key.indexOf("background")>=0 ||
							key.indexOf("foreground")>=0)
					{
						if(value.length()>0)
							UIManager.put(key, Color.decode(value));
					}*/
					
				}
				catch(Exception e)
				{
					Error.getError().addError(PAS.l("common_warning"), "Error in UI parameters", e, Error.SEVERITY_WARNING);
				}
			}
			return true;
		}
		catch(Exception e)
		{
			throw new ParameterNotFoundException(PAS.l("common_warning"), "Could not load UI parameter." + e.getMessage());
		}
	}
	
	public static boolean loadServerUIParams()
	throws FileNotFoundException
	{
		String file = /*PAS.get_pas().get_sitename() +*/PAS.get_pas().get_codebase() +  "/pas-site-setup.txt";
		
		try
		{
			Properties props = new Properties();
			URL url = new URL(file); //ClassLoader.getSystemResource(file);
			props.load(url.openStream());
			parseParams(props);
			return true;
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_warning"), String.format("Servers UI defaults file could not be loaded (%s)",file), e, Error.SEVERITY_WARNING);
			return false;			
		}
	}
	
	public static boolean loadUIParams()
	throws FileNotFoundException
	{
		//String file = "no/ums/pas/localization/uidefaults.properties";
		String sz_filename = "uidefaults.properties";
		ClassLoader loader = UIParamLoader.class.getClassLoader();//UIParamLoader.class.getClass().getClassLoader();
		URL url = loader.getResource("no/ums/pas/localization/" + sz_filename);
		try
		{
			//ResourceBundle bundle = ResourceBundle.getBundle(file);//, new Locale("en", "GB"));
			InputStreamReader input = new InputStreamReader(url.openStream());
			Properties bundle = new Properties();
			//InputStream inStream = UIParamLoader.class.getClass().getClassLoader().getSystemResourceAsStream(file);
			bundle.load(input);
			if(bundle==null)
				throw new FileNotFoundException(sz_filename);
			parseParams(bundle);
			return true;
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_warning"), String.format("UI defaults file could not be loaded (%s)",sz_filename), e, Error.SEVERITY_WARNING);
			return false;
		}
		/*
		//common font face
		String face = "Helvetica";
		
		Font font_title = new Font(face, Font.BOLD, 13);
		Font font_buttons = new Font(face, Font.PLAIN, 11);
		Font font_lists = new Font(face, Font.PLAIN, 11);
		Font font_labels = new Font(face, Font.PLAIN, 12);
		Font font_text = new Font(face, Font.PLAIN, 11);
		Font font_combobox = new Font(face, Font.PLAIN, 11);
		Font font_menu = new Font(face, Font.BOLD, 11);
		Font font_parentmenu = new Font(face, Font.PLAIN, 11);
		Font font_menuitems = new Font(face, Font.PLAIN, 11);
		Font font_popupmenu = new Font(face, Font.PLAIN, 11);
		Font font_table = new Font(face, Font.PLAIN, 11);
		Font font_table_header = new Font(face, Font.BOLD, 11);
		Font font_panels = new Font(face, Font.PLAIN, 11);
		Font font_tabbedpane = new Font(face, Font.BOLD, 11);
		Font font_titledborder = new Font(face, Font.BOLD, 11);
		Font font_progressbar = new Font(face, Font.PLAIN, 12);
		Font font_tooltip = new Font(face, Font.PLAIN, 10);
		//Font test = Font.decode("Comic Sans MS-BOLD-18");
		//title
		UIManager.put("InternalFrame.titleFont", font_title);
		
		//buttons
		UIManager.put("Button.font", font_buttons);
		UIManager.put("ToggleButton.font", font_buttons);
		UIManager.put("RadioButton.font", font_buttons);
		UIManager.put("CheckBox.font", font_buttons);

		//lists
		UIManager.put("List.font", font_lists);
		UIManager.put("Tree.font", font_lists);

		//table
		UIManager.put("Table.font", font_table);
		UIManager.put("TableHeader.font", font_table_header);

		//combo
		UIManager.put("ComboBox.font", font_combobox);

		//labels
		UIManager.put("Label.font", font_labels);

		//menus
		UIManager.put("MenuBar.font", font_menu);

		//menuitem with children
		UIManager.put("Menu.font", font_parentmenu);

		//menu items
		UIManager.put("MenuItem.font", font_menuitems);
		UIManager.put("RadioButtonMenuItem.font", font_menuitems);
		UIManager.put("CheckBoxMenuItem.font", font_menuitems);
		
		//popup menu
		UIManager.put("PopupMenu.font", font_popupmenu);

		//panels
		UIManager.put("ScrollPane.font", font_panels);
		UIManager.put("OptionPane.font", font_panels);
		UIManager.put("Panel.font", font_panels);
		UIManager.put("Viewport.font", font_panels);
		UIManager.put("ColorChooser.font", font_panels);

		//text fields
		UIManager.put("TextField.font", font_text);
		UIManager.put("PasswordField.font", font_text);
		UIManager.put("TextArea.font", font_text);
		UIManager.put("TextPane.font", font_text);
		UIManager.put("EditorPane.font", font_text);

		//tabbed pane
		UIManager.put("TabbedPane.font", font_tabbedpane);
		
		//titled border
		UIManager.put("TitledBorder.font", font_titledborder);

		//progressbar
		UIManager.put("ProgressBar.font", font_progressbar);

		//toolbar/tip
		UIManager.put("ToolBar.font", font_tooltip);
		UIManager.put("ToolTip.font", font_tooltip);*/
		
	}
}