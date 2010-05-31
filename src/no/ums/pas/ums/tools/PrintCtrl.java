package no.ums.pas.ums.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.*;

//import java.awt.PrintCanvas;
import java.awt.*;
import java.awt.print.*;
import java.awt.image.BufferedImage;

import javax.imageio.*;

import no.ums.pas.*;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.InhabitantResults;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.ums.errorhandling.Error;

import java.io.*;

public class PrintCtrl implements Printable {
	Graphics m_graph;
	//PAS m_pas;
	Frame m_frame;
	PrintCanvas m_canvas;
	BufferedImage m_img_offscreen = null;
	private Component componentToBePrinted;
	private Image m_image;
	static int ExtPageIndex = 0;
	
	//PAS get_pas() { return m_pas; }
	
	//PrintCtrl(PAS pas, Frame frame)
	public PrintCtrl(Component c, Frame frame)
	{
		//super("Print");
		//setVisible(false);
		componentToBePrinted = c;
		m_frame = frame;
	}
	public PrintCtrl(Image i, Frame f) {
		m_image = i;
		m_frame = f;
	}
	public PrintCtrl(DefaultPanel dp, Frame frame) {
		
	}
	
	public static void printStatus(DefaultPanel dp, Frame frame) {
		new PrintCtrl(dp, frame).print();
	}
	public static void printComponent(Component c, Frame frame) {		
		ExtPageIndex = 0;
		new PrintCtrl(c, frame).print();
			++ExtPageIndex;
		
		//new PrintCtrl(c, frame).print();
	}
	public static void printComponentToFile(Image i, Frame frame) {
		new PrintCtrl(i, frame).printToFile();
	}
/*	  public PrintUtilities(Component componentToBePrinted) {
	    this.componentToBePrinted = componentToBePrinted;
	  }*/	
	public void print()
	{
/*		PrintJob pjob = Toolkit.getDefaultToolkit().getPrintJob(this, "Print map", null, null);

        if (pjob != null) {          
            Graphics pg = pjob.getGraphics();
            //Graphics2D g2d = (Graphics2D)pg;
            if (pg != null) {
                //m_canvas.printAll(pg);
            	int n_printx, n_printy;
            	n_printx = pjob.getPageDimension().width;
				n_printy = pjob.getPageDimension().height;
				
				//find scale
				double d_scalex, d_scaley;
				d_scalex = n_printx / get_pas().get_mapsize().getWidth();
				d_scaley = n_printy / get_pas().get_mapsize().getHeight();
				get_pas().add_event("scalex: " + d_scalex + " / scaley: " + d_scaley);
				//g2d.scale(d_scaley, d_scaley);
				Graphics2D gmap = (Graphics2D)get_pas().get_mappane().getGraphics();
				gmap.scale(n_printx, n_printy);
				
            	get_pas().get_drawthread().set_suspended(true);
            	//get_pas().get_mappane().printAll(pg);
            	//gmap.scale(d_scaley, d_scaley);
            	//pg = gmap;
            	
            	get_pas().get_drawthread().set_suspended(false);
                pg.dispose(); //
            }
            pjob.end();
        }*/
		
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		PAS.get_pas().get_drawthread().set_suspended(true);
	    if (printJob.printDialog()) {
	        try {
	        	printJob.setCopies(1);
	        	printJob.print();
	        } catch(PrinterException pe) {
	          PAS.get_pas().add_event("Error printing: " + pe, pe);
	          Error.getError().addError("PrintCtrl","Exception in print",pe,1);
	        } finally {
	        	PAS.get_pas().get_drawthread().set_suspended(false);
	        }
	    }
	}
	
	public static final String[][] FILE_FILTERS_ = new String[][] {
			//{ "JPG", ".jpg" },
			{ "PNG", ".png" }, 
	};
	public void printToFile() {
		//Graphics2D g2d = (Graphics2D)componentToBePrinted.getGraphics();
		//int width = componentToBePrinted.getWidth();
		//int height = componentToBePrinted.getHeight();
		//BufferedImage img = (BufferedImage)m_image.;//(BufferedImage)componentToBePrinted.createImage(componentToBePrinted.getWidth(), componentToBePrinted.getHeight());//= new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		BufferedImage img = new BufferedImage(m_image.getWidth(null), m_image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = img.createGraphics();
		graphics2D.drawImage(m_image,0,0,null);
		graphics2D.dispose();
		//PlanarImage image= new RenderedImageAdapter(g2d);
		//new ImageIO().write()
		
		FilePicker picker = new FilePicker(null, PAS.get_pas().get_lookandfeel(),
				StorageController.StorageElements.get_path(StorageController.PATH_HOME_), 
				"Save As", FILE_FILTERS_, FilePicker.MODE_SAVE_);
		File f = picker.getSelectedFile();
		String sz_filetype = picker.getFileType().toLowerCase();
		if(f!=null) {
			try {
				ImageIO.write(img, sz_filetype, f);
			} catch(Exception e) {
				
			}
		}
	}
	
	public int print(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        disableDoubleBuffering(componentToBePrinted);
		return 0;
	}

	
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
	{
		
		if (pageIndex > 0 && !componentToBePrinted.getClass().equals(InhabitantResults.class)) {
	        return(NO_SUCH_PAGE);
	    } 
	    else {
	        Graphics2D g2d = (Graphics2D)g;
	        //double scalex = pageFormat.getImageableX() / get_pas().get_mapsize().getWidth();
	        //double scaley = pageFormat.getImageableY() / get_pas().get_mapsize().getHeight();
	        double scale = pageFormat.getImageableWidth() / PAS.get_pas().get_mapsize().getWidth();
	        PAS.get_pas().add_event("Scale: " + scale, null);
	        //Graphics2D g2d = (Graphics2D)g.create();
	        
	        //g.copyArea()
	        disableDoubleBuffering(componentToBePrinted);

	        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
	        g2d.setFont(new Font(null, Font.BOLD, 20));
	        int headerHeight = g2d.getFontMetrics(g2d.getFont()).getHeight();
	        g2d.setColor(Color.black);
	        g2d.drawString(PAS.l("common_app_title"), 0, 20);
	        g2d.translate(0, 25);
	        int pagewidth = (int)pageFormat.getWidth();
	        g2d.drawLine(0, 0, pagewidth, 0);
	        g2d.translate(0, 10);
	        //g2d.drawString("Map start", 30, 30);
	       
	        //componentToBePrinted.resize((int)pageFormat.getWidth(), (int)pageFormat.getHeight());
	        //g2d.scale(scale, scale);
	        //componentToBePrinted.paint(g2d);
	        
	        //PAS.get_pas().get_eastcontent().get_statuscodeframe().get_panel().m_tbl.paint(g2d);
	        
	        if(componentToBePrinted.getClass().equals(InhabitantResults.class))
	        {
	        	InhabitantResults is = (InhabitantResults)componentToBePrinted;
	        	//g2d.drawString(is.getName(),0,5);
	        	// Find columnsizes
	        	//g2d.scale(0.80, 0.80);
	        	g2d.setFont(new Font(null, Font.BOLD, 8));
	        	
	        	if(is.get_table() != null && is.get_table().getRowCount() > 0)
	        		g2d.drawString(PAS.l("common_status") + ": " + is.get_table().getValueAt(1, 1).toString(), 0, 0);
	        	
	        	g2d.setFont(new Font(null, Font.PLAIN, 8));
	        	
	        	int lineheight = g2d.getFontMetrics(g2d.getFont()).getHeight();
	        	int pagelines = (((int)pageFormat.getHeight()-(headerHeight+80))/lineheight); // +80 is for the two linebreaks during header writing
	        	
	        	if((is.get_table().getRowCount()/pagelines) < pageIndex+1) {
	        		enableDoubleBuffering(componentToBePrinted);
	        		return NO_SUCH_PAGE;
	        	}
	        	
	        	int[] columnsize = new int[is.get_table().getColumnCount()];
	        	
	        	for(int i=0;i<is.get_table().getColumnCount();++i) {
	        		if(i!=1) {
		        		columnsize[i] = is.get_table().getColumnName(i).toString().length();
		        		for(int j=0;j<is.get_table().getRowCount();++j)
		        			if(columnsize[i] < is.get_table().getValueAt(j, i).toString().length()) {
		        				columnsize[i] = is.get_table().getValueAt(j, i).toString().length();
		        				System.out.println("Column "+i+":" + columnsize[i]);
		        			}
		        		if(i!=0)
		        			columnsize[i] += columnsize[i-1];
	        		}
	        		else
		        		columnsize[i] = columnsize[i-1];
		        	
	        	}
	        	int columnwidth = 6;
	        	for(int i=0;i<is.get_table().getColumnCount();++i) {
	        		if(i==0)
	        			g2d.drawString(is.get_table().getColumnName(i),0*columnwidth,10);
	        		else if(i==1);
	        		else
	        			g2d.drawString(is.get_table().getColumnName(i),columnsize[i-1]*columnwidth,10);
	        		System.out.println("Column "+i+":" + columnsize[i]);
	        	}
	        	
	        	g2d.drawLine(0, 12, pagewidth, 12);
	        	int line = 0;
	        	for(int i=pageIndex*pagelines;i<is.get_table().getRowCount();++i) {
	        	//for(int i=0;i<is.get_table().getRowCount();++i) {
	        		if(line == pagelines) {// Page is full
	        			g2d.drawString(PAS.l("common_page") + " " + (pageIndex + 1) + " "+PAS.l("common_x_of_y") + " " + ((int)is.get_table().getRowCount()/pagelines), 0, line*10+30);
	        			enableDoubleBuffering(componentToBePrinted);
	        			return PAGE_EXISTS;
	        		}
	        		for(int j=0;j<is.get_table().getColumnCount();++j) {
	        			if(j==0)
	        				g2d.drawString(is.get_table().getValueAt(i, j).toString(),0*columnwidth,(line*10)+25);
	        			else if(j==1);
	        			else
	        				g2d.drawString(is.get_table().getValueAt(i, j).toString(),columnsize[j-1]*columnwidth,(line*10)+25);
	        		}
	        		++line;
	        		//g2d.drawLine(0, i, (int)pageFormat.getWidth(), 0);
	        	}
	        	
	        	
	        	//componentToBePrinted = null;
	        	//componentToBePrinted.paint(g2d);
	        }
	        else {
	        	g2d.scale(scale, scale);
	        	componentToBePrinted.paint(g2d);
	        	g2d.translate(0, PAS.get_pas().get_mapsize().getHeight()+5);
	        }
	        enableDoubleBuffering(componentToBePrinted);
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
