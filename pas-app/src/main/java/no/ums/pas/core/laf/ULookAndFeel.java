package no.ums.pas.core.laf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import no.ums.pas.ums.tools.ImageLoader;


public class ULookAndFeel
{
	public static final String WINDOW_MODIFIED = "ULookAndFeel.windowModified";
	public static final String TABBEDPANE_CLOSEBUTTON = "ULookAndFeel.tabbedPaneCloseButton";
	private static final String TABBEDPANE_CLOSEBUTTON_HOT = "ULookAndFeel.tabbedPaneCloseButtonHot";
	private static final String TABBEDPANE_ONE_CLOSEBUTTON_IS_HOT = "ULookAndFeel.tabbedPaneOneClosebuttonIsHot";
	
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
			INCREMENT 	= Math.max(1, Integer.parseInt(UIManager.getString("ULookAndFeel.UAttentionController.INCREMENT")));
			MAX 		= Math.max(1, Integer.parseInt(UIManager.getString("ULookAndFeel.UAttentionController.MAX")));
			TIMER_MSEC 	= Math.max(10, Integer.parseInt(UIManager.getString("ULookAndFeel.UAttentionController.TIMER_MSEC")));
			timer = new Timer(TIMER_MSEC, this);
			timer.start();
		}
		
		public GradientPaint getGradientPaint(JComponent c)
		{				
			COLORFACTOR = (int)Math.abs(MAX/2.0 - ATTENTION);

			return new GradientPaint(0, c.getY(), new Color(200, 
					0, 
					0, 
					COLORFACTOR),
					0,
					(float)(c.getY()+c.getHeight()*0.6), new Color(SystemColor.control.getRed(), 
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
				//g.setColor(new Color(0, 0, 128, 128));
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
		class MouseListener extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = isCursorOverCloseIcon(e);
				if(index>=0)
				{
					UTabbedPaneUIAttention.this.tabPane.removeTabAt(index);
				}
				super.mouseReleased(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				//int index = UTabbedPaneUIAttention.this.tabPane.indexAtLocation(e.getX(), e.getY());
				int index = isCursorOverCloseIcon(e);
				if(index>=0)
				{
					UpdateFlag(index, ULookAndFeel.TABBEDPANE_CLOSEBUTTON_HOT, Boolean.TRUE);
					UpdateFlag(-1, ULookAndFeel.TABBEDPANE_ONE_CLOSEBUTTON_IS_HOT, Boolean.TRUE); //notify tabbedpane that one is hot
					return;
				}
				//if one used to be hot, reset all
				if(checkForFlags(-1, ULookAndFeel.TABBEDPANE_ONE_CLOSEBUTTON_IS_HOT))
					UpdateFlagsForAllTabs(ULookAndFeel.TABBEDPANE_CLOSEBUTTON_HOT, Boolean.FALSE);

				super.mouseMoved(e);
			}
		}
		
		private int isCursorOverCloseIcon(MouseEvent e)
		{
			int index = UTabbedPaneUIAttention.this.tabPane.indexAtLocation(e.getX(), e.getY());
			return (index>=0 && calcIconRect(index).contains(e.getX(), e.getY()) ? index : -1);
			
		}
		
		private void UpdateFlag(int n_componentindex, String flag, Object value)
		{
			if(n_componentindex>=0)
			{
				JComponent c = (JComponent)this.tabPane.getComponentAt(n_componentindex);
				if(c!=null)
				{
					c.putClientProperty(flag, value);
				}
			}
			else //put the value on the tabbed pane itself
			{
				this.tabPane.putClientProperty(flag, value);
			}
		}
		private void UpdateFlagsForAllTabs(String flag, Object value)
		{
			for(int i=0; i < this.tabPane.getComponentCount(); i++)
			{
				((JComponent)this.tabPane.getComponentAt(i)).putClientProperty(flag, value);
			}
		}
		
		UAttentionUI ui;		
		private static ImageIcon m_icon_close = null;
		private static ImageIcon m_icon_close_grayscale = null;
		UTabbedPaneUIAttention(JComponent c)
		{
			ui = new UAttentionUI(c);
			m_icon_close = new ImageIcon(ImageLoader.load_icon("delete_16.png").getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
			BufferedImage bufferedImage = new BufferedImage(m_icon_close.getIconWidth(), m_icon_close.getIconHeight(), BufferedImage.TYPE_BYTE_GRAY);
			m_icon_close_grayscale = new ImageIcon();
			Graphics gi = bufferedImage.getGraphics();
			gi.drawImage(m_icon_close.getImage(), 0, 0, SystemColor.control, null);
			gi.dispose();
			m_icon_close_grayscale = new ImageIcon(bufferedImage);
			
			
			((JTabbedPane)c).addMouseListener(new MouseListener());
			((JTabbedPane)c).addMouseMotionListener(new MouseListener());
		}

		@Override
		protected void paintTabBackground(Graphics g, int tabPlacement,
				int tabIndex, int x, int y, int w, int h, boolean isSelected) {
			super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
			JComponent comp = (JComponent)((JTabbedPane)ui.component_to_repaint).getTabComponentAt(tabIndex);
			//UAttentionController.INSTANCE.addComponent(comp);

			ui.paintBackground(comp, (Graphics2D)g, x, y, w, h);
		}
		
		public Rectangle calcIconRect(int tabIndex)
		{
			Rectangle iconRect = new Rectangle();
			Rectangle c = tabPane.getBoundsAt(tabIndex);
			boolean b_icon = checkForFlags(tabIndex, ULookAndFeel.TABBEDPANE_CLOSEBUTTON);
			if(c!=null && b_icon)
			{
				Rectangle tabRect = c;
				int BUTTONSIZE = 12;
				int WIDTHDELTA = 5;
	
				int dx = tabRect.x + tabRect.width - BUTTONSIZE - WIDTHDELTA;
				int dy = (tabRect.y) + 5;// + tabRect.height) / 2;// - tabRect.height/4;
				iconRect.x = dx;
				iconRect.y = dy;
				iconRect.width = BUTTONSIZE;
				iconRect.height = BUTTONSIZE;
			}
			else
				iconRect = new Rectangle(0, 0);
			return iconRect;
		}
		
		@Override
		protected void paintTab(Graphics g, int tabPlacement,
				Rectangle[] rects, int tabIndex, Rectangle iconRect,
				Rectangle textRect) {
			super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
			if(checkForFlags(tabIndex, ULookAndFeel.TABBEDPANE_CLOSEBUTTON))
			{
				Rectangle tabRect = rects[tabIndex]; //the black rectangle
				int selectedIndex = tabPane.getSelectedIndex();
				boolean isSelected = selectedIndex == tabIndex;

				iconRect = calcIconRect(tabIndex);
				
				//temp
				BufferedImage bufferedImage = new BufferedImage(m_icon_close.getIconWidth(), m_icon_close.getIconHeight(), BufferedImage.TYPE_BYTE_GRAY);
				m_icon_close_grayscale = new ImageIcon();
				Graphics gi = bufferedImage.getGraphics();
				gi.drawImage(m_icon_close.getImage(), 0, 0, SystemColor.control, null);
				gi.dispose();
				m_icon_close_grayscale = new ImageIcon(bufferedImage);

				//temp
				
				paintIcon(g, tabPlacement, tabIndex, (checkForFlags(tabIndex, ULookAndFeel.TABBEDPANE_CLOSEBUTTON_HOT) ? m_icon_close : m_icon_close_grayscale), iconRect, isSelected);

				//paintCloseIcon(g, dx, dy, isOver);
			}
		}
		@Override
		protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
			if(checkForFlags(tabIndex, ULookAndFeel.TABBEDPANE_CLOSEBUTTON))
				return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + m_icon_close.getIconWidth()*2;
			return super.calculateTabWidth(tabPlacement, tabIndex, metrics);
		}
		
		@Override
		protected void paintText(Graphics g, int tabPlacement, Font font,
				FontMetrics metrics, int tabIndex, String title,
				Rectangle textRect, boolean isSelected) {
			if(checkForFlags(tabIndex, ULookAndFeel.TABBEDPANE_CLOSEBUTTON))
			{
				textRect.x -= 10;
			}
			super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect,
					isSelected);
		}
		protected boolean checkForFlags(int tabIndex, String flag)
		{
			Object o = null;
			if(tabIndex>=0)
			{
				JTabbedPane tp = tabPane;
				JComponent c = (JComponent)tabPane.getComponentAt(tabIndex);
				if(c!=null)
				{
					//test for flags
					o = c.getClientProperty(flag);
					if(o!=null)
						return Boolean.parseBoolean(o.toString());
				}
			}
			else
			{
				o = this.tabPane.getClientProperty(flag);
				if(o!=null)
					return Boolean.parseBoolean(o.toString());
			}
			return false;
		}
		
		
		protected void paintCloseIcon(Graphics g, int x, int y, boolean bIsOver) {
			try {
				if(m_icon_close!=null)
				{
					//m_icon_close.paintIcon(tabPane, g, x, y);
					//this.paintIcon(g, x, y, m_icon_close, new Rectangle(16,16), true);
				}
			} catch(Exception e) {
				
			}
			//g.drawImage(m_icon_close.getImage(), x, y, null);
		}

	}
}