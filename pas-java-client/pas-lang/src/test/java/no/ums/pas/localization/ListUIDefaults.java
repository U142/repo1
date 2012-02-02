package no.ums.pas.localization;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.swing.JColorChooser;
import javax.swing.UIManager;
import javax.swing.plaf.ColorChooserUI;

public class ListUIDefaults {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Locale.setDefault(Locale.FRENCH);
		
    	Enumeration<Object> en = UIManager.getDefaults().keys();
    	while(en.hasMoreElements())
    	{
    		//if(entry.getValue() instanceof String) {
    		Object key = en.nextElement();
    		System.out.println(key + " = " + UIManager.get(key));
    		//}
    	}

	}

}
