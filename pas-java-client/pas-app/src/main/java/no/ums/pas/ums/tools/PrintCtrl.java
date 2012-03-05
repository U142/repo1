package no.ums.pas.ums.tools;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.InhabitantResults;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.icons.ImageFetcher;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.status.StatusSending;
import no.ums.pas.ums.errorhandling.Error;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

//import java.awt.PrintCanvas;

public class PrintCtrl implements Printable, Pageable {
    private static final Log log = UmsLog.getLogger(PrintCtrl.class);

	Graphics m_graph;
	//PAS m_pas;
	Frame m_frame;
	PrintCanvas m_canvas;
	BufferedImage m_img_offscreen = null;
	private Component componentToBePrinted;
	private Image m_image;
	static int ExtPageIndex = 0;
	protected int numPages = 0;
	private PageFormat format;
	protected int totalItemsToPrint = 0;
	protected int totalItemsPrinted = 0;
	protected AffineTransform originalTransform;
	boolean bFirstPass = true;
	
	private PRINTMODE current_mode = PRINTMODE.PAGECOUNT;
	private enum PRINTMODE {
		PAGECOUNT,
		PRINTING,
	};

	public PrintCtrl(Image i, Frame f) {
		m_image = i;
		m_frame = f;
	}
	public PrintCtrl(DefaultPanel dp, Frame frame) {
		
	}

	public PrintCtrl(Component c, Frame frame)
	{
		componentToBePrinted = c;
		m_frame = frame;
		format = new PageFormat();
	}
	
	public void doPrint()
	{
		
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		printJob.setPageable(this);
		//Variables.getDraw().set_suspended(true);
	    if (printJob.printDialog()) {
	        try {
	        	//format = printJob.get
	        	//printJob.setCopies(1);
	    		current_mode = PRINTMODE.PRINTING;
	    		totalItemsPrinted = 0;
	        	printJob.print();
	        } catch(Exception pe) {
	          if(PAS.get_pas() != null)
	        	PAS.get_pas().add_event("Error printing: " + pe, pe);
	          Error.getError().addError("PrintCtrl","Exception in print",pe,1);
	        } finally {
	        	//Variables.getDraw().set_suspended(false);
	        }
	    }
	}

	
	@Override
	public int getNumberOfPages() {
		current_mode = PRINTMODE.PAGECOUNT;
		//PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		format = getPageFormat(0);//PrinterJob.getPrinterJob().getPageFormat(pras);
		int pageno = 0;
		boolean b = true;
		while(b)
		{
			Graphics g = new BufferedImage(1, 1, Image.SCALE_FAST).getGraphics();
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
		totalItemsPrinted = 0;
		return numPages = pageno;
	}
	@Override
	public PageFormat getPageFormat(int pageIndex)
			throws IndexOutOfBoundsException {
		Paper paper = new Paper();
		paper.setSize(594.936, 841.536);// Set to A4 size.
		paper.setImageableArea(30, 30, 570, 820);// Set the margins.
		PageFormat pageFormat = new PageFormat();
		pageFormat.setPaper(paper);
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		return format=pageFormat;
	}
	@Override
	public Printable getPrintable(int pageIndex)
			throws IndexOutOfBoundsException {
		return this;
	}
	
	public static void printStatus(DefaultPanel dp, Frame frame) {
		new PrintCtrl(dp, frame).print();
	}
	public static void printComponent(Component c, Frame frame) {		
		ExtPageIndex = 0;
		new PrintCtrl(c, frame).doPrint();
			++ExtPageIndex;		
	}
	public static void printComponentToFile(Image i, Frame frame) {
		new PrintCtrl(i, frame).printToFile();
	}

	public void print()
	{
	}
	
	public static final String[][] FILE_FILTERS_ = new String[][] {
			//{ "JPG", ".jpg" },
			{ "PNG", ".png" }, 
	};
	public void printToFile() {
		BufferedImage img = new BufferedImage(m_image.getWidth(null), m_image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = img.createGraphics();
		graphics2D.drawImage(m_image,0,0,null);
		graphics2D.dispose();
		
		FilePicker picker = new FilePicker(null, 
				StorageController.StorageElements.get_path(StorageController.PATH_HOME_), 
				"Save As", FILE_FILTERS_, FilePicker.MODE_SAVE_);
		File f = picker.getSelectedFile();
        if(f != null) {
		    String sz_filetype = picker.getFileType().toLowerCase();

            if(f!=null) {
                try {
                    ImageIO.write(img, sz_filetype, f);
                } catch(Exception e) {

                }
            }
        }
	}
	
	public int print(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        print();
		return 0;
	}

	
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
	{
		if (!componentToBePrinted.getClass().equals(InhabitantResults.class)) {
	        return(NO_SUCH_PAGE);
	    } 
	    else {
	        Graphics2D g2d = (Graphics2D)g;
			if(pageIndex == 0)
			{
				//reset all
				//totalItemsPrinted = 0;
			}
			if(bFirstPass)
			{				
				originalTransform = g2d.getTransform();
				bFirstPass = false;
			}
			//g2d.setTransform(originalTransform);
			g2d.getTransform().setToTranslation(0, 0);
	        //AffineTransform originalTransform = g2d.getTransform();
	        double scale = pageFormat.getImageableWidth() / PAS.get_pas().get_mapsize().getWidth();
	        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
	        g2d.translate(20, 20);
	        g2d.setFont(new Font(null, Font.BOLD, 20));
	        int headerHeight = g2d.getFontMetrics(g2d.getFont()).getHeight();
	        g2d.setColor(Color.black);
            g2d.drawString(Localization.l("common_app_title"), 0, 20);
	        g2d.translate(0, 25);
	        int pagewidth = (int)pageFormat.getImageableWidth();
	        g2d.drawLine(0, 0, pagewidth, 0);
	        g2d.translate(0, 10);
	        //System.out.println("Y-translate = " + g2d.getTransform().getTranslateY());
	        
	        if(componentToBePrinted.getClass().equals(InhabitantResults.class))
	        {
	        	InhabitantResults is = (InhabitantResults)componentToBePrinted;
	        	g2d.setFont(new Font(null, Font.BOLD, 8));
	        	
	        	if(is.get_table() != null && is.get_table().getRowCount() > 0) {
                    // Superhacks for å få riktig status, av en eller annen grunn så ble det ikke alltid riktig med den andre
	        		//g2d.drawString(Localization.l("main_statustab_title") + ": " + is.get_table().getValueAt(1, 1).toString(), 0, 0);
	        		//g2d.drawString(Localization.l("common_refno"), 0, 0);
	        		StringBuilder sendinglist = new StringBuilder();
	        		sendinglist.append(Localization.l("projectdlg_projectname"));
	        		sendinglist.append(": ");
	        		sendinglist.append("\"");
	        		sendinglist.append(PAS.get_pas().get_current_project().get_projectname());
	        		sendinglist.append("\"");
	        		g2d.drawString(sendinglist.toString(), 0, 0);
	        		g2d.translate(0, 10);
	        		
	        		sendinglist = new StringBuilder();
	        		sendinglist.append(Localization.l("common_sendings"));
	        		sendinglist.append(": ");
	        		g2d.drawString(sendinglist.toString(), 0, 0);
	        		int sendingHeaderWidth = g2d.getFontMetrics().stringWidth(sendinglist.toString());
	        		
	        		if(Variables.getStatusController().get_statuscodeframe().m_statuspanel.getFilter()==null)
	        		{
	        			ListIterator<StatusSending> it = Variables.getStatusController().get_sendinglist().listIterator();
		        		while(it.hasNext())
		        		{
			        		sendinglist = new StringBuilder();
		        			StatusSending ss = it.next();
		        			sendinglist.append(ss.get_refno());
		        			sendinglist.append(" \"");
		        			sendinglist.append(ss.get_sendingname());
		        			sendinglist.append("\"");
		        			g2d.drawString(sendinglist.toString(), sendingHeaderWidth+10, 0);
		        			if(it.hasNext())
		        				g2d.translate(0, 10);
		        		}
	        		}
	        		else
	        		{
	        			StatusSending ss = Variables.getStatusController().get_statuscodeframe().m_statuspanel.getFilter();
	        			sendinglist.append(ss.get_refno());
	        			sendinglist.append(" \"");
	        			sendinglist.append(ss.get_sendingname());
	        			sendinglist.append("\"");
		        		g2d.drawString(sendinglist.toString(), 0, 0);
	        		}
	        		g2d.translate(0, 10);
	        		g2d.drawString(Localization.l("main_statustab_title") + ": " + PAS.get_pas().get_statuscontroller().find_status(PAS.get_pas().get_statuscontroller().get_n_search_status()).get_status(),0,0);
                }
	        	
	        	g2d.setFont(new Font(null, Font.PLAIN, 8));
	        	
	        	
	        	int lineheight = g2d.getFontMetrics(g2d.getFont()).getHeight();
	        	int pagelines = (((int)pageFormat.getHeight()-(headerHeight+80))/lineheight); // +80 is for the two linebreaks during header writing
	        	/*if((is.get_table().getRowCount()/pagelines) < pageIndex) {
	        		enableDoubleBuffering(componentToBePrinted);
	        		return NO_SUCH_PAGE;
	        	}*/
	        	
	        	int[] columnsize = new int[is.get_table().getColumnCount()];
	        	
	        	int [] percentageWidth = new int [] { 5, 0, 22, 12, 26, 5, 15, 10 };
	        	Set<Integer> columnExclude = new HashSet<Integer>();
	        	columnExclude.add(1);
	        	
	        	int totalWidth = (g2d.getClipBounds()!=null ? g2d.getClipBounds().width : 100);
	        	for(int i=0;i<is.get_table().getColumnCount();++i) {
	        		if(i<percentageWidth.length) //percent is specified
	        		{
	        			columnsize[i] = (int)((totalWidth * percentageWidth[i]) / 100.0); 
	        		}
	        			
	        	}
	        	
	        	
	        	
	        	int columnwidth = 6;
	        	g2d.translate(0, 20); //move down
	        	AffineTransform at = g2d.getTransform();
	        	for(int i=0;i<is.get_table().getColumnCount();++i) {
	        		if(columnExclude.contains(i))
	        			continue;
	        		g2d.drawString(is.get_table().getColumnName(i),0,0);
	        		g2d.translate(columnsize[i], 0);
	        	}
	        	g2d.setTransform(at);
	        	g2d.translate(0, 5);
	        	g2d.drawLine(0, 0, pagewidth, 0);
	        	int line = 0;
	        	totalItemsToPrint = is.get_table().getRowCount();
    			//System.out.println("TransY = " + g2d.getTransform().getTranslateY());
    			int sizePrLine = 15;
    			int sizeHeader = (int)200;//(g2d.getTransform().getTranslateY() - pageFormat.getImageableHeight());
    			int sizeRemaining = (int)(pageFormat.getImageableHeight() - sizeHeader);
    			int linesRemaining = (int)Math.floor(sizeRemaining / sizePrLine);
    			
    			System.out.println("Page " + pageIndex + " Starting at index = " + linesRemaining * pageIndex + " / " + totalItemsToPrint);
    			
	        	if(linesRemaining * pageIndex>=totalItemsToPrint)
	        	{
                    g2d.drawString(Localization.l("common_page") + " " + (pageIndex + 1) + " "+ Localization.l("common_x_of_y") + " " + numPages, 0, 30);
	        		//System.out.println("PrintJob - No more pages");
	        		return NO_SUCH_PAGE;
	        	}
	        	for(int i=linesRemaining * pageIndex;i<is.get_table().getRowCount();++i) {
	        		//if(line == pagelines) {// Page is full
	        		if(line>=linesRemaining ||
	        				(linesRemaining * pageIndex + line)>=totalItemsToPrint)
	        		{
                        g2d.drawString(Localization.l("common_page") + " " + (pageIndex + 1) + " "+ Localization.l("common_x_of_y") + " " + numPages, 0, 30);
                        //System.out.println("PrintJob - pageIndex=" + pageIndex + " ");
	        			return PAGE_EXISTS;
	        		}
	        		int currentY = (int)(g2d.getTransform().getTranslateY());
	        		int pageHeight = (int)pageFormat.getImageableHeight()*(pageIndex + 1);
	        		/*if(currentY > pageHeight)
	        		{
                        g2d.drawString(Localization.l("common_page") + " " + (pageIndex + 1) + " "+ Localization.l("common_x_of_y") + " " + numPages, 0, line*10+30);//((int)is.get_table().getRowCount()/pagelines), 0, line*10+30);
                        //g2d.getTransform().setToTranslation(0, 0);
	        			//g2d.setTransform(originalTransform);
	        			//g2d.translate(-(int)g2d.getTransform().getTranslateX(), -(int)g2d.getTransform().getTranslateY());
	        			int tmp = (int)g2d.getTransform().getTranslateY();
                        return PAGE_EXISTS;
	        		}*/
    	        	g2d.translate(0, 15); //move down
    	        	at = g2d.getTransform(); //save initial transform
	        		for(int j=0;j<is.get_table().getColumnCount();++j) {
	        			if(columnExclude.contains(j))
	        				continue;

        				Image img = ImageFetcher.getImage("bandaid_8.png");

	        			Object tmp = is.get_table().getValueAt(i, j);
	        			int iconWidth = 0;
	        			g2d.setColor(Color.black);
	        			if(tmp instanceof Inhabitant)
	        			{
	        				Inhabitant inhab = (Inhabitant)tmp;
	        				if(inhab.isVulnerable())
	        				{
		        				/*img = ImageFetcher.getImageWithBorder("bandaid_8.png", 1).getImage();
	        					Image ico = new ImageIcon("bandaid_8.png").getImage();
		        				int dim = img.getWidth(null);
		        				g2d.drawImage(ico, 0, -4, null);
		        				g2d.drawString("X", 0, 0);
		        				iconWidth = img.getWidth(null) + 1;*/
		        				g2d.setColor(Color.red);
	        				}
	        			}
	        			Shape oldClip = g2d.getClip();
	        			g2d.setClip(iconWidth, g2d.getFontMetrics().getDescent(), columnsize[j]-iconWidth, -g2d.getFontMetrics().getHeight());
	        			g2d.drawString(is.get_table().getValueAt(i, j).toString(),iconWidth,0);
	        			g2d.setClip(oldClip);
	        			g2d.translate(columnsize[j], 0);
	        		}
    	        	g2d.setTransform(at); //revert transform for next line
	        		++line;
	    			++totalItemsPrinted;
	        	}
	        		        	
	        }
	        else {
	        	g2d.scale(scale, scale);
	        	componentToBePrinted.paint(g2d);
	        	g2d.translate(0, PAS.get_pas().get_mapsize().getHeight()+5);
	        }
			//g2d.setTransform(originalTransform);
	        System.out.println("PrintJob - Final page");
            g2d.drawString(Localization.l("common_page") + " " + (pageIndex + 1) + " "+ Localization.l("common_x_of_y") + " " + numPages, 0, 30);
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
