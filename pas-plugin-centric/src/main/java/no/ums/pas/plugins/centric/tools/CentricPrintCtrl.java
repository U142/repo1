package no.ums.pas.plugins.centric.tools;


import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.ums.errorhandling.Error;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;

//import java.awt.PrintCanvas;

public class CentricPrintCtrl implements Printable, Pageable {
	Graphics m_graph;
	//PAS m_pas;
	Frame m_frame;
	PrintCanvas m_canvas;
	BufferedImage m_img_offscreen = null;
	private Component componentToBePrinted;
	private Image m_image;
	private int numPages = 0;
	static int ExtPageIndex = 0;
	private PageFormat format;
	
	private PRINTMODE current_mode = PRINTMODE.PAGECOUNT;
	private enum PRINTMODE {
		PAGECOUNT,
		PRINTING,
	};
	
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
		format = new PageFormat();
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
	

	@Override
	public int getNumberOfPages() {
		//test
		current_mode = PRINTMODE.PAGECOUNT;
		Graphics g = new BufferedImage(1, 1, Image.SCALE_FAST).getGraphics();
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		format = PrinterJob.getPrinterJob().getPageFormat(pras);
		int pageno = 0;
		boolean b = true;
		while(b)
		{
			int ret = print(g, format, pageno);
			switch(ret)
			{
			case PAGE_EXISTS:
				pageno++;
				break;
			default:
				b = false;
				break;
			}
		}
		return numPages = pageno;
	}
	@Override
	public PageFormat getPageFormat(int pageNum) throws IndexOutOfBoundsException {
		//PageFormat pf = new PageFormat();
		//return PrinterJob.getPrinterJob().defaultPage(pf);
		Paper paper = new Paper();
		paper.setSize(594.936, 841.536);// Set to A4 size.
		paper.setImageableArea(30, 30, 570, 820);// Set the margins.
		PageFormat pageFormat = new PageFormat();
		pageFormat.setPaper(paper);
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		return format=pageFormat;
	}
	@Override
	public Printable getPrintable(int pageNum) throws IndexOutOfBoundsException {
		return this;
	}
	public void doPrint()
	{
		
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		printJob.setPageable(this);
		Variables.getDraw().set_suspended(true);
	    if (printJob.printDialog()) {
	        try {
	        	//format = printJob.getPageFormat(pras);
	        	printJob.setCopies(1);
	    		current_mode = PRINTMODE.PRINTING;
	        	printJob.print();
	        } catch(PrinterException pe) {
	          if(PAS.get_pas() != null)
	        	PAS.get_pas().add_event("Error printing: " + pe, pe);
	          Error.getError().addError("PrintCtrl","Exception in print",pe,1);
	        } finally {
	        	Variables.getDraw().set_suspended(false);
	        }
	    }
	}
	
	public static final String[][] FILE_FILTERS_ = new String[][] {
			//{ "JPG", ".jpg" },
			{ "PNG", ".png" }, 
	};
	
	private void printInit(Graphics2D g, PageFormat pageFormat, int pageIndex)
	{
        g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
	}
	
	private int printTitle(Graphics2D g, PageFormat pageFormat, int pageIndex)
	{
		int height = 0;
        g.setFont(UIManager.getFont("PrintJobTitle.font"));
        printTranslate(g, 0, g.getFontMetrics().getAscent());
        g.setColor(Color.black);
        if(PAS.TRAINING_MODE) {
            g.drawString(PAS.l("common_app_title") + " - " + PAS.l("mainmenu_trainingmode"), 0, 0);
        }
    	else {
            g.drawString(PAS.l("common_app_title"), 0, 0);
        }
        return g.getFontMetrics().getHeight();
	}
	
	private int printHeader(Graphics2D g, PageFormat pageFormat, int pageIndex)
	{
		int height = 0;
    	g.setFont(UIManager.getFont("PrintJobHeader.font"));        		        	
    	g.drawString(m_header, 0, 0);
		return g.getFontMetrics().getAscent();		
	}
	
	private int printImage(Graphics2D g, PageFormat pageFormat, int pageIndex)
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
				m_mapimage = Variables.getDraw().get_buff_image();
			}
			else {
				double percent_of_actual = ((double)actual_width/(double)width);
				width = (int)((double)actual_width / percent_of_actual);
				height = (int)((double)actual_height / percent_of_actual);
				//width = width + height;
				//width = (int)((double)width / percent_of_actual);
				m_mapimage = Variables.getDraw().get_buff_image().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				//m_mapimage = Variables.DRAW.get_buff_image();
			}
		}
		else {
			if(actual_height>actual_width) {
				double percent_of_actual = ((double)actual_height/(double)height);
				width = actual_width;
				width = (int)((double)actual_width / percent_of_actual);
				width = (width + (height-width));
				m_mapimage = Variables.getDraw().get_buff_image();
			}
			else {
				double percent_of_actual = ((double)actual_height/(double)height);
				width = (int)((double)actual_width / percent_of_actual);
				width = height + (width-height);
				m_mapimage = Variables.getDraw().get_buff_image().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				
			}
		}
		
		//int maxwh = (int)pageFormat.getImageableWidth();
//		int maxwh = (int)pageFormat.getImageableHeight();
//		int mapw = m_mapimage.getWidth(null)/2;
		int maph = m_mapimage.getHeight(null);
		float maxmaph = 300.0f;
		float factor = maxmaph / maph;
		
		float scale = factor;//(float)(mapw*1.0 / maxwh);
		float scalerev = 1.0f/factor;//(float)(maxwh*1.0 / mapw);
		g.scale(scale, scale);
		g.drawImage(m_mapimage, 0, 0, null);
		g.scale(scalerev, scalerev);
		return (int)(maph*scale);
	}
	
	private int printText(Graphics2D g, PageFormat pageFormat, int pageIndex)
	{
		int height = 0;
		return height;
	}
	
	private int printFooter(Graphics2D g, PageFormat pageFormat, int pageIndex)
	{
        g.setFont(UIManager.getFont("PrintJobFooter.font"));            
    	g.drawString(m_footer, 0, 0);
		return g.getFontMetrics().getAscent();
	}
	
	private int printTranslate(Graphics2D g, int x, int y)
	{
		g.translate(x, y);
		return y;
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
	{
		Dimension margins = new Dimension(3, 3);
		
		if (pageIndex>=1) {
	        return(NO_SUCH_PAGE);
	    } 
	    else 
	    {
	        int pagewidth = (int)pageFormat.getImageableWidth();
//	        int pageheight = (int)pageFormat.getImageableHeight();
	        int printHeight = 0;
	    	
//	    	int width = m_mapimage.getWidth(null);
//			int height = m_mapimage.getHeight(null);

			
	        Graphics2D g2d = (Graphics2D)g;
	        double scale = pageFormat.getImageableWidth() / (pageFormat.getWidth());
	        //PAS.get_pas().add_event("Scale: " + scale, null);
	    	//printTranslate(g2d, (int)pageFormat.getImageableX(), (int)pageFormat.getImageableY());
	    	g2d.scale(scale, scale);
        	
	        
	        //INIT
	        printInit(g2d, pageFormat, pageIndex);

	        printTranslate(g2d, margins.width, margins.height);

	        
	        //PRINT TITLE
	        printHeight = printTitle(g2d, pageFormat, pageIndex);
	        //g2d.translate(0, printHeight);
	        //printTranslate(g2d, 0, printHeight);
	        
	        g2d.drawLine(0, 0, (int)(pagewidth), 0);
	        //g2d.translate(margins.width, printHeight);
	        //printTranslate(g2d, margins.width, printHeight);
	        printTranslate(g2d, 0, 20);
	        
	        
	        printHeader(g2d, pageFormat, pageIndex);
        	
	        printTranslate(g2d, 0, 20);
			
	        printHeight = printImage(g2d, pageFormat, pageIndex);
        	
	        printTranslate(g2d, 0, printHeight);
			
			//MESSAGE HEADER AND CONTENT
            //float drawPosY = (float)m_mapimage.getHeight(null) + 40;
            //float drawPosX = (float)10;

			Font fontMessageHeader = UIManager.getFont("PrintJobMedium.font");
			g2d.setFont(fontMessageHeader);
	        printTranslate(g2d, 0, g2d.getFontMetrics().getAscent());
			g2d.drawString(PAS.l("common_message_content"), 0, 0);
			//drawPosY += g2d.getFontMetrics().getAscent();
			printTranslate(g2d, 0, g2d.getFontMetrics().getAscent());
			
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
	            	//drawPosY += g2d.getFontMetrics().getAscent();
	            	printTranslate(g2d, 0, g2d.getFontMetrics().getAscent());
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
	                //drawPosY += layout.getAscent();
	                printTranslate(g2d, 0, (int)layout.getAscent());
	                layout.draw(g2d, 0, 0);
	
	                printTranslate(g2d, 0, (int)(layout.getDescent() + layout.getLeading()));
	                //drawPosY += layout.getDescent() + layout.getLeading();
	            }    
			}

			g2d.setFont(fontMessageHeader);
			printTranslate(g2d, 0, g2d.getFontMetrics().getAscent()*2);
			g2d.drawString("(" + m_charachers + ")", 0, 0);
            
	        printTranslate(g2d, 0, (int)g.getFontMetrics().getAscent()*3);

			printFooter(g2d, pageFormat, pageIndex);
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
