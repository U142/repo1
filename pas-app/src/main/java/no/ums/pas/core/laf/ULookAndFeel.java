package no.ums.pas.core.laf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;


public class ULookAndFeel
{
	public static final String WINDOW_MODIFIED = "ULookAndFeel.windowModified";
	
	public static enum UAttentionController implements ActionListener
	{
		INSTANCE;
		int INCREMENT = 7;
		int ATTENTION = 0;
		int MAX = 255;
		int TIMER_MSEC = 50;
		Timer timer;
		int COLORFACTOR = 0;
		
		UAttentionController()
		{
			timer = new Timer(TIMER_MSEC, this);
			timer.start();
		}
		
		public GradientPaint getGradientPaint(JComponent c)
		{				
			COLORFACTOR = (int)Math.abs(MAX/2.0 - ATTENTION);

			return new GradientPaint(0, 0, new Color(200, 
					0, 
					0, 
					COLORFACTOR),
					0,
					20, new Color(SystemColor.control.getRed(), 
							SystemColor.control.getGreen(), 
							SystemColor.control.getBlue(), 
							50));			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(timer))
			{
				ATTENTION = (ATTENTION + INCREMENT) % MAX;
			}
		}
		
		
		
	}
	
	static class UAttentionUI implements ActionListener
	{		
		JComponent component_to_repaint;
		GradientPaint current_paint;
		Timer timer;

		UAttentionUI(JComponent c)
		{
			this(c, 50, 7, 200);
		}
		
		UAttentionUI(JComponent c, int timer_msec, int increment, int max)
		{
			component_to_repaint = c;
			timer = new Timer(timer_msec, this);
			timer.start();
		}
		public void paintBackground(JComponent component, Graphics2D g, int x, int y, int w, int h)
		{
			if(component==null)
				return;
			Object o = component.getClientProperty(ULookAndFeel.WINDOW_MODIFIED);
			if(o!=null && Boolean.parseBoolean(o.toString()))
			{
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint p = UAttentionController.INSTANCE.getGradientPaint(component);
				g.setPaint(p);
				g.fillRoundRect(x+2, y+2, w-4, h-4, 5, 5);			
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(timer))
			{
				component_to_repaint.repaint();
			}
		}
		
	}
	
	public static UTabbedPaneUIAttention newUTabbedPaneUIAttention(JComponent c) {
		return new UTabbedPaneUIAttention(c);
	}
	public static UButtonAttention newUButtonAttention(String text) {
		return new UButtonAttention(text);
	}
	public static UButtonAttention newUButtonAttention(Icon ico) {
		return new UButtonAttention(ico);
	}
	
	public static class UButtonAttention extends JButton
	{
		UAttentionUI ui;
		public UButtonAttention(Icon ico)
		{
			super(ico);
			ui = new UAttentionUI(this);
			//this.setUI(new UButtonUIAttention(this));
		}
		public UButtonAttention(String text)
		{
			super(text);
			ui = new UAttentionUI(this);
			//this.setUI(new UButtonUIAttention(this));
		}
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			super.paintComponent(g);
			ui.paintBackground(this, g2d, 0, 0, this.getWidth(), this.getHeight());
		}
		
	}

	
	public static class UTabbedPaneUIAttention extends BasicTabbedPaneUI
	{
		UAttentionUI ui;		
		UTabbedPaneUIAttention(JComponent c)
		{
			ui = new UAttentionUI(c);
		}
		@Override
		protected void paintTabBackground(Graphics g, int tabPlacement,
				int tabIndex, int x, int y, int w, int h, boolean isSelected) {
			super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
			JComponent comp = (JComponent)((JTabbedPane)ui.component_to_repaint).getTabComponentAt(tabIndex);
			//UAttentionController.INSTANCE.addComponent(comp);
			ui.paintBackground(comp, (Graphics2D)g, x, y, w, h);
		}
	}
}