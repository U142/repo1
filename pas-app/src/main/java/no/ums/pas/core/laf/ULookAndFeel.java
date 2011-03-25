package no.ums.pas.core.laf;

import no.ums.pas.icons.ImageFetcher;
import no.ums.pas.ums.tools.ImageLoader;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.Color;
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
import java.awt.image.BufferedImage;


public class ULookAndFeel
{
	public static final String WINDOW_MODIFIED = "ULookAndFeel.windowModified";
	public static final String WINDOW_LOADING = "ULookAndFeel.windowLoading";
	public static final String WINDOW_LOADING_GRADIENTVALUE = "ULookAndFeel.windowLoadingGradientValue";
	public static final Integer WINDOW_LOADING_INITIAL_GRADIENTVALUE = 255;
	public static final String TABBEDPANE_CLOSEBUTTON = "ULookAndFeel.tabbedPaneCloseButton";
	private static final String TABBEDPANE_CLOSEBUTTON_HOT = "ULookAndFeel.tabbedPaneCloseButtonHot";
	private static final String TABBEDPANE_ONE_CLOSEBUTTON_IS_HOT = "ULookAndFeel.tabbedPaneOneClosebuttonIsHot";
	
	public static enum UAttentionController implements ActionListener
	{
		INSTANCE;
		
		public int GetAttentionFactor() { return ATTENTION; }
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
		
		public GradientPaint getGradientPaint(int USE_ATTENTION, JComponent c, Color base, float posfactor /*0.6*/, int X1, int Y1, int X2, int Y2)
		{				
			COLORFACTOR = (int)Math.abs(USE_ATTENTION);

			return new GradientPaint(X1, Y1, new Color(base.getRed(), 
					base.getGreen(), 
					base.getBlue(), 
					COLORFACTOR),
					X2, 
					(float)(Y2), new Color(SystemColor.control.getRed(),//c.getY()+c.getHeight()*posfactor 
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
				GradientPaint p = UAttentionController.INSTANCE.getGradientPaint(UAttentionController.INSTANCE.MAX - UAttentionController.INSTANCE.ATTENTION,
						component, new Color(220, 20, 20), 0.6f,
						0, component.getY(), 0, (int)(component.getY()+component.getHeight()*0.8f));
				g.setPaint(p);
				//g.setColor(new Color(0, 0, 128, 128));
				g.fillRoundRect(x+2, y+2, w-4, h-4, 5, 5);			
			}
			o = component.getClientProperty(ULookAndFeel.WINDOW_LOADING);
			if(o!=null)
			{
				int gradientfactor = Integer.parseInt(component.getClientProperty(ULookAndFeel.WINDOW_LOADING_GRADIENTVALUE).toString());
				if(gradientfactor>0)
				{
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					/*GradientPaint p = UAttentionController.INSTANCE.getGradientPaint(gradientfactor, 
							component, SystemColor.controlDkShadow, 1.0f,
							1, component.getY()+component.getHeight(), 1, component.getY()+component.getHeight()-5);*/
					int pos = gradientfactor/10;
					GradientPaint p = UAttentionController.INSTANCE.getGradientPaint(gradientfactor, 
							component, SystemColor.controlDkShadow, 1.0f,
							1, y, 1, y+10);/*(int)(component.getY()+component.getHeight()*2.0f));*/
					g.setPaint(p);
					//g.setColor(new Color(0, 0, 128, 128));
					//g.fillRoundRect(x+2, y, w-4, h-4, 5, 5);
					//g.fillRoundRect(x+2, y, x+w-2, y+h, 5, 5);
					//int dim = 10;
					//g.fillPolygon(new int [] { x+4, x+4, x+dim*2 }, new int [] { y+(h/2)-dim, y+(h/2)+dim, y+(h/2) }, 3);
					//g.drawString("Updating", x+gradientfactor/10, y+15);
					if(!Boolean.parseBoolean(o.toString()))
					{
						component.putClientProperty(ULookAndFeel.WINDOW_LOADING_GRADIENTVALUE, gradientfactor-10);
					}
					else
					{
						g.drawImage(ImageLoader.load_icon("remembermilk_orange.gif").getImage(), 12, (y+h/2)-10, null);
					}
				}
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
	
	public static UTabbedPaneUI newUTabbedPaneUI(JComponent c, TabCallback callback) {
		return new UTabbedPaneUI(c, callback);
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

	
	public interface TabCallback
	{
		public void CloseButtonClicked(JComponent c);
		public void CloseButtonHot(JComponent c);
	}

	
	public static class UTabbedPaneUI extends BasicTabbedPaneUI
	{

		UAttentionUI ui;		
		private static ImageIcon m_icon_close = null;
		private static ImageIcon m_icon_close_grayscale = null;
		protected static int CLOSE_ICONSIZE = 10;
		protected static int CLOSE_RIGHT_PADDING = 5;
		protected final static String CLOSE_ICON_FILE = "delete_16.png";
		
		private TabCallback callback;
		
		UTabbedPaneUI(JComponent c, TabCallback callback)
		{
			ui = new UAttentionUI(c);
			this.callback = callback;
			m_icon_close = new ImageIcon(ImageLoader.load_icon(CLOSE_ICON_FILE).getImage()); //.getScaledInstance(CLOSE_ICONSIZE, CLOSE_ICONSIZE, Image.SCALE_SMOOTH));
			//CLOSE_ICONSIZE = m_icon_close.getIconWidth();
			//CLOSE_RIGHT_PADDING = CLOSE_ICONSIZE/2;
			m_icon_close_grayscale = ImageFetcher.makeGrayscale(m_icon_close);
			m_icon_close = new ImageIcon(m_icon_close.getImage().getScaledInstance(CLOSE_ICONSIZE, CLOSE_ICONSIZE, Image.SCALE_SMOOTH));
			m_icon_close_grayscale = new ImageIcon(m_icon_close_grayscale.getImage().getScaledInstance(CLOSE_ICONSIZE, CLOSE_ICONSIZE, Image.SCALE_SMOOTH));
			
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
			if(!b_icon)
				b_icon = checkForFlags(tabIndex, ULookAndFeel.WINDOW_LOADING);
			if(c!=null && b_icon)
			{
				Rectangle tabRect = c;
	
				int dx = tabRect.x + tabRect.width - CLOSE_ICONSIZE - CLOSE_RIGHT_PADDING;
				int dy = (tabRect.y) + ((tabRect.height-CLOSE_ICONSIZE)/2);// + tabRect.height) / 2;// - tabRect.height/4;
				iconRect.x = dx;
				iconRect.y = dy;
				iconRect.width = CLOSE_ICONSIZE;
				iconRect.height = CLOSE_ICONSIZE;
			}
			else
				iconRect = new Rectangle(0, 0);
			return iconRect;
		}
		
		class MouseListener extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = isCursorOverCloseIcon(e);
				if(index>=0)
				{
					JComponent c = (JComponent)tabPane.getComponentAt(index);
					UpdateFlag(c, ULookAndFeel.TABBEDPANE_CLOSEBUTTON_HOT, Boolean.FALSE);
					UpdateFlag(null, ULookAndFeel.TABBEDPANE_ONE_CLOSEBUTTON_IS_HOT, Boolean.FALSE);
					UTabbedPaneUI.this.tabPane.removeTabAt(index);
					callback.CloseButtonClicked(c);
				}
				super.mouseReleased(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				//int index = UTabbedPaneUIAttention.this.tabPane.indexAtLocation(e.getX(), e.getY());
				int index = isCursorOverCloseIcon(e);
				if(index>=0)
				{
					JComponent c = (JComponent)tabPane.getComponentAt(index);
					callback.CloseButtonHot(c);
					UpdateFlag(c, ULookAndFeel.TABBEDPANE_CLOSEBUTTON_HOT, Boolean.TRUE);
					UpdateFlag(null, ULookAndFeel.TABBEDPANE_ONE_CLOSEBUTTON_IS_HOT, Boolean.TRUE); //notify tabbedpane that one is hot
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
			int index = UTabbedPaneUI.this.tabPane.indexAtLocation(e.getX(), e.getY());
			return (index>=0 && calcIconRect(index).contains(e.getX(), e.getY()) ? index : -1);
			
		}
		
		protected void UpdateFlag(JComponent c, String flag, Object value)
		{
			if(c!=null)
			{
				try
				{
					c.putClientProperty(flag, value);
				}
				finally {
					
				}
			}
			else
			{
				tabPane.putClientProperty(flag, value);
			}
		}
		
		protected void UpdateFlag(int n_componentindex, String flag, Object value)
		{
			if(n_componentindex>=0)
			{
				try
				{
					if(this.tabPane.getComponentCount() <= n_componentindex)
						return;
					JComponent c = (JComponent)this.tabPane.getComponentAt(n_componentindex);
					UpdateFlag(c, flag, value);
				}
				finally
				{	
				}
			}
			else //put the value on the tabbed pane itself
			{
				UpdateFlag(this.tabPane, flag, value);
			}
		}
		private void UpdateFlagsForAllTabs(String flag, Object value)
		{
			for(int i=0; i < this.tabPane.getComponentCount(); i++)
			{
				try
				{
					((JComponent)this.tabPane.getComponentAt(i)).putClientProperty(flag, value);
				}
				catch(Exception e)
				{
					
				}
			}
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
				String s = tabPane.getTitleAt(tabIndex);
				iconRect = calcIconRect(tabIndex);
				
				paintIcon(g, tabPlacement, tabIndex, (checkForFlags(tabIndex, ULookAndFeel.TABBEDPANE_CLOSEBUTTON_HOT) && checkForFlags(-1, ULookAndFeel.TABBEDPANE_ONE_CLOSEBUTTON_IS_HOT) ? m_icon_close : m_icon_close_grayscale), iconRect, isSelected);

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
				textRect.x -= CLOSE_RIGHT_PADDING*2;
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