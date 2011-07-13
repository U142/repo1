package no.ums.pas.ums.tools;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/** GUI Class for search. Executes action ACTION_SEARCH_UPDATED if clear icon is pressed or text is modified*/
public class StdSearchArea extends StdTextArea implements MouseListener, KeyListener
{
	@Override
	public void mouseClicked(MouseEvent e) {
		if(b_btnmode)
		{
			setText("");
			notifyListeners(getText(), ACTION_SEARCH_CLEARED);
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		try
		{
			this.requestFocus();
		}
		catch(Exception err)
		{
			
		}
	}
	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	public static final String ACTION_SEARCH_UPDATED = "act_search_updated";
	public static final String ACTION_SEARCH_CLEARED = "act_search_cleared";
	protected String empty_string = "Search";
	protected boolean b_isempty = false;
	protected Font font_haze = null;
	protected ImageIcon icon = null;
	protected Rectangle rectBtn;
	protected boolean b_btnmode = false;
	protected String sz_icon = "clear_search3_16.png";
	
	
	public void setIcon(String s)
	{
		sz_icon = s;
		icon = no.ums.pas.ums.tools.ImageLoader.load_icon(sz_icon);
	}
	
	public StdSearchArea(String s, boolean b_heading, Dimension d, String sz_when_empty)
	{
		this(s, b_heading, d, sz_when_empty, "clear_search3_16.png");
	}
	
	public StdSearchArea(String s, boolean b_heading, Dimension d, String sz_when_empty, String icon_clear)
	{
		super(s, b_heading, d);
		init();
		empty_string = sz_when_empty;
		sz_icon = icon_clear;
	}
	public StdSearchArea(String s, boolean b_heading, String sz_when_empty)
	{
		super(s, b_heading);
		init();
		empty_string = sz_when_empty;
	}
	protected void init()
	{
		font_haze = this.getFont().deriveFont(Font.ITALIC);
		icon = no.ums.pas.ums.tools.ImageLoader.load_icon(sz_icon);
		rectBtn = new Rectangle(0, 0, 1, 1);
		addMouseListener(this);
		addKeyListener(this);
		getText();
	}
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(b_isempty && font_haze!=null)
		{
			int size = (int)(getHeight() * 0.70);
			g.setFont(font_haze);
			g.setColor(new Color(128,128,128));
			g.drawString(empty_string, 5, size); //17);
		}
		else if(!b_isempty && icon != null)
		{
			int x_clear = 5;
			rectBtn.x = getWidth()-icon.getIconWidth()-x_clear;
			rectBtn.y = getHeight()/2-icon.getIconHeight()/2;
			rectBtn.width = icon.getIconWidth();
			rectBtn.height = icon.getIconHeight();
			g.drawImage(icon.getImage(), getWidth()-icon.getIconWidth()-x_clear, getHeight()/2-icon.getIconHeight()/2, null);
		}

	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

	@Override
	public String getText() {
		if(super.getText().length()==0)
			b_isempty = true;
		else
			b_isempty = false;
		this.repaint();
		return super.getText();
	}
	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		if(rectBtn.contains(e.getPoint()) && !b_isempty)
		{
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			b_btnmode = true;
		}
		else
		{
			this.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			b_btnmode = false;
		}
		super.processMouseMotionEvent(e);
	}
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
	}
	@Override
	public void keyPressed(KeyEvent e) {
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		notifyListeners(getText(), ACTION_SEARCH_UPDATED);
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	protected void notifyListeners(final Object o, final String operation)
	{
		final Object [] listeners = this.listenerList.getListenerList();
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run()
    		{
		        for (int i = 0; i <= listeners.length-2; i += 2) {
		        	final ActionEvent e = new ActionEvent(o, ActionEvent.ACTION_PERFORMED, operation);
		        			((ActionListener)listeners[i+1]).actionPerformed(e);
		        }
    		}
        });
	}
}