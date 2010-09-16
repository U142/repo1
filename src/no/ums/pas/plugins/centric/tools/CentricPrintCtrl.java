package no.ums.pas.plugins.centric.tools;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;

import javax.swing.*;

//import java.awt.PrintCanvas;
import java.awt.*;
import java.awt.print.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

import javax.imageio.*;

import no.ums.pas.*;
import no.ums.pas.core.variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.InhabitantResults;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.ums.errorhandling.Error;

import java.io.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;

public class CentricPrintCtrl implements Printable {
	Graphics m_graph;
	//PAS m_pas;
	Frame m_frame;
	PrintCanvas m_canvas;
	BufferedImage m_img_offscreen = null;
	private Component componentToBePrinted;
	private Image m_image;
	static int ExtPageIndex = 0;
	
	String m_header;
	Image m_mapimage;
	String m_message;
	String m_charachers;
	String m_footer;
	
	
	//PAS get_pas() { return m_pas; }
	
	//PrintCtrl(PAS pas, Frame frame)
	public CentricPrintCtrl(Component c, Frame frame)
	{
		//super("Print");
		//setVisible(false);
		componentToBePrinted = c;
		m_frame = frame;
	}
	public CentricPrintCtrl(Image i, Frame f) {
		m_image = i;
		m_frame = f;
	}
	
	public CentricPrintCtrl(Image i, String header, String message, String characters, String footer) {
		m_mapimage = i;
		m_header = header;
		m_message = message;
		m_charachers = characters;
		m_footer = footer;
	}
	
	public static void printComponent(Component c, Frame frame) {		
		ExtPageIndex = 0;
		new CentricPrintCtrl(c, frame).doPrint();
			++ExtPageIndex;
		
		//new PrintCtrl(c, frame).print();
	}
	

	public void doPrint()
	{
		
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		variables.DRAW.set_suspended(true);
	    if (printJob.printDialog()) {
	        try {
	        	printJob.setCopies(1);
	        	printJob.print();
	        } catch(PrinterException pe) {
	          if(PAS.get_pas() != null)
	        	PAS.get_pas().add_event("Error printing: " + pe, pe);
	          Error.getError().addError("PrintCtrl","Exception in print",pe,1);
	        } finally {
	        	variables.DRAW.set_suspended(false);
	        }
	    }
	}
	
	public static final String[][] FILE_FILTERS_ = new String[][] {
			//{ "JPG", ".jpg" },
			{ "PNG", ".png" }, 
	};
	

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
	{
		Dimension margins = new Dimension(20, 20);
		
		if (pageIndex > 0) {
	        return(NO_SUCH_PAGE);
			
	    } 
	    else 
	    {
	    	int width = 800;
			int height = 800;
			
			int actual_width = m_mapimage.getWidth(null);
			int actual_height = m_mapimage.getHeight(null);
			
			if(actual_height < actual_width) {
				if(actual_width<width) {
					double percent_of_actual = ((double)actual_width/(double)width);
					height = (int)((double)actual_height / percent_of_actual);
					height = actual_height;
					height = (height+(width-height));
					m_mapimage = variables.DRAW.get_buff_image();
				}
				else {
					double percent_of_actual = ((double)actual_width/(double)width);
					width = (int)((double)actual_width / percent_of_actual);
					height = (int)((double)actual_height / percent_of_actual);
					//width = width + height;
					//width = (int)((double)width / percent_of_actual);
					m_mapimage = variables.DRAW.get_buff_image().getScaledInstance(width, height, Image.SCALE_SMOOTH);
					//m_mapimage = variables.DRAW.get_buff_image();
				}
			}
			else {
				if(actual_height>actual_width) {
					double percent_of_actual = ((double)actual_height/(double)height);
					width = actual_width;
					width = (int)((double)actual_width / percent_of_actual);
					width = (width + (height-width));
					m_mapimage = variables.DRAW.get_buff_image();
				}
				else {
					double percent_of_actual = ((double)actual_height/(double)height);
					width = (int)((double)actual_width / percent_of_actual);
					width = height + (width-height);
					m_mapimage = variables.DRAW.get_buff_image().getScaledInstance(width, height, Image.SCALE_SMOOTH);
					
				}
			}
			
	        Graphics2D g2d = (Graphics2D)g;
	        //double scalex = pageFormat.getImageableX() / get_pas().get_mapsize().getWidth();
	        //double scaley = pageFormat.getImageableY() / get_pas().get_mapsize().getHeight();
	        double scale = pageFormat.getImageableWidth() / width;
	        PAS.get_pas().add_event("Scale: " + scale, null);
	        //Graphics2D g2d = (Graphics2D)g.create();
	        
	        //g.copyArea()

	        
	        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
	        //g2d.setFont(new Font(UIManager.getString("Common.Fontface"), Font.BOLD, 20));
	        g2d.setFont(UIManager.getFont("PrintJobTitle.font"));
	        int headerHeight = g2d.getFontMetrics(g2d.getFont()).getHeight();
	        g2d.setColor(Color.black);
	        
	        g2d.translate(margins.width, margins.height);
	        
	        g2d.drawString(PAS.l("common_app_title"), margins.width, margins.height);
	        
	        g2d.translate(0, g2d.getFontMetrics().getAscent());
	        
	        int pagewidth = (int)pageFormat.getWidth();
	        int pageheight = (int)pageFormat.getHeight();
	        g2d.drawLine(margins.width, 0, pagewidth-margins.width*2, 0);
	        g2d.translate(margins.width, g2d.getFontMetrics().getAscent());
	        	        
	        
	        
        	g2d.scale(scale, scale);
        	//g2d.setFont(new Font(UIManager.getString("Common.Fontface"), Font.BOLD, 16));
        	g2d.setFont(UIManager.getFont("PrintJobHeader.font"));
        	
        	int lineheight = g2d.getFontMetrics(g2d.getFont()).getHeight();
        	int pagelines = (((int)pageFormat.getHeight()-(headerHeight+80))/lineheight); // +80 is for the two linebreaks during header writing
        		        	
        	g2d.drawString(m_header, 0, g2d.getFontMetrics().getAscent());
        	
        	
			
			
        	
			g2d.drawImage(m_mapimage, 0, (3*10), null);
			
			//MESSAGE HEADER AND CONTENT
            float drawPosY = (float)m_mapimage.getHeight(null) + 40;
            float drawPosX = (float)10;

			Font fontMessageHeader = UIManager.getFont("PrintJobMedium.font");
			g2d.setFont(fontMessageHeader);
			g2d.drawString(PAS.l("common_message_content"), drawPosX, drawPosY);
			drawPosY += g2d.getFontMetrics().getAscent();
			
			Font fontMessageText = UIManager.getFont("PrintJobSmall.font");//new Font(UIManager.getString("Common.Fontface"), Font.BOLD, 12);
			g2d.setFont(fontMessageText);
			
			String [] linesplit = m_message.split("\n");
			for(int i=0; i < linesplit.length; i++)
			{
	        	LineBreakMeasurer lineMeasurer;
	            AttributedString string = new AttributedString(linesplit[i]);
	            if(string==null)
	            {
	            	continue;
	            }
	            if(linesplit[i].length()==0)
	            {
	            	linesplit[i] = "\n";
	            	drawPosY += /*g2d.getFontMetrics().getDescent() + g2d.getFontMetrics().getLeading() + */g2d.getFontMetrics().getAscent();
	            	continue;
	            }
	            try
	            {
	            	string.addAttribute(TextAttribute.FONT, fontMessageText);
	            }
	            catch(Exception err)
	            {
	            	
	            }
	            AttributedCharacterIterator paragraph = string.getIterator();
	            int paragraphStart = paragraph.getBeginIndex();
	            int paragraphEnd = paragraph.getEndIndex();
	            FontRenderContext frc = g2d.getFontRenderContext();
	            BreakIterator bi = BreakIterator.getWordInstance();
	            lineMeasurer = new LineBreakMeasurer(paragraph, bi, frc);
	
	            float breakWidth = (float)pagewidth;
	            lineMeasurer.setPosition(paragraphStart);
	            while (lineMeasurer.getPosition() < paragraphEnd) {
	                TextLayout layout = lineMeasurer.nextLayout(breakWidth);
	                drawPosY += layout.getAscent();
	                layout.draw(g2d, drawPosX, drawPosY);
	
	                drawPosY += layout.getDescent() + layout.getLeading();
	            }    
			}

			g2d.setFont(fontMessageHeader);
			g2d.drawString("(" + m_charachers + ")", 10, drawPosY += g2d.getFontMetrics().getAscent());
            
            //g2d.setFont(new Font(UIManager.getString("Common.Fontface"), Font.BOLD, 16));
            g2d.setFont(UIManager.getFont("PrintJobFooter.font"));
            
            int h = g2d.getFontMetrics().getHeight();
            int footer_ypos = (int)(pageheight + margins.height + h);
        	g2d.drawString(m_footer, 0, drawPosY + h*2);
        	//g2d.drawImage(m_mapimage, 0, (8*10), null);
	        
	        return(PAGE_EXISTS);
	    }
    }
	public static void disableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(false);
	}
	
	public static void enableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(true);
	}
	
	public void end()
	{
		
	}
/*	public Graphics getGraphics()
	{
		return m_graph;
	}
	public Dimension getPageDimension() 
	{
		return new Dimension(0,0);
	}
	public int getPageResolution()
	{
		return 0;
	}
	public boolean lastPageFirst()
	{
		return false;
	}*/
	
}
class PrintCanvas extends Canvas {
	public static final long serialVersionUID = 1;

    public Dimension getPreferredSize() {
        return new Dimension(100, 100);
    }
  
    public void paint(Graphics g) {
        Rectangle r = getBounds();

        g.setColor(Color.yellow);
        g.fillRect(0, 0, r.width, r.height);

        g.setColor(Color.blue);
        g.drawLine(0, 0, r.width, r.height);

        g.setColor(Color.red);
        g.drawLine(0, r.height, r.width, 0);
    }
}
