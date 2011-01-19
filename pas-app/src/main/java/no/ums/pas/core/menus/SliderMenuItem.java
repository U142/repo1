package no.ums.pas.core.menus;

import no.ums.pas.PAS;
import no.ums.pas.core.events.EventClass;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class SliderMenuItem extends JSlider {//implements MenuElement {
	public static final long serialVersionUID = 1;

	private Listener m_listener;
	private PAS m_pas;
	public PAS get_pas() { return m_pas; }
	private EventClass m_event;
	
    public SliderMenuItem(PAS pas, String sz_title, int n_low, int n_max, int n_default, int n_minor, int n_major, 
    					  boolean b_showticks, boolean b_showlegend, String sz_legend, Dimension size, EventClass e) {
    	super(JSlider.HORIZONTAL, n_low, n_max, n_default);
    	m_pas = pas;
        setMajorTickSpacing(n_major);
        setMinorTickSpacing(n_minor);
        this.setPaintLabels(b_showlegend);
        //this.setBackground(Color.lightGray);
        Dictionary labelTable = new Hashtable<Integer, JLabel>();
        this.setPaintTicks(b_showticks);
        this.setPreferredSize(size);
        if(b_showlegend)
        {
	        labelTable.put(n_low, new JLabel(n_low + sz_legend) );
	        labelTable.put(n_max, new JLabel(n_max + sz_legend) );
	        this.setLabelTable(labelTable);
        }
        m_event = e;
        m_listener = new SliderMenuItem.Listener();
        
        this.addChangeListener(m_listener);
    }
    public void change(ChangeEvent e) {
    }    
    class Listener implements ChangeListener {
    	public void stateChanged(ChangeEvent e)
    	{
    		JSlider source = (JSlider)e.getSource();
    		if(source.getValueIsAdjusting())
    		{
    			m_event.doEvent(source.getValue());
    		}
    	}
    }
}