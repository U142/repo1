package no.ums.pas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.jvnet.substance.SubstanceLookAndFeel;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.area.tree.AreaTreeController;
import no.ums.pas.core.controllers.PredefinedAreaController;
import no.ums.pas.core.defines.DefaultPanel;

public class PredefinedAreaPanel extends DefaultPanel implements ComponentListener, MouseListener{

	private static final Log log = UmsLog.getLogger(PredefinedAreaPanel.class);
	protected AreaTreeController treeCtrl;
	
	public int tempPK;
	private PredefinedAreaController predefinedAreaController;
	protected PredefinedAreaController getPredefinedAreaController() { return predefinedAreaController; }
	private GridLayout m_box;
	
	public PredefinedAreaPanel(PredefinedAreaController maincontroller) {
		super();
		m_box = new GridLayout(1, 1, 0, 0);
		addComponentListener(this);
		predefinedAreaController = maincontroller;
		treeCtrl = new AreaTreeController(maincontroller);
		this.tempPK = 0;
//		this.updXml = new UpdateXML(maincontroller, "temp");
//		this.updXml.start();
		setLayout(m_box);
		
		add_controls();
		getPredefinedAreaController().getTreeScrollPane().addMouseListener(this);
//		use above code to to enable mouselistener for entire area panel
		init();
	}
	
	public void actionPerformed(ActionEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1 || e.getButton()==MouseEvent.BUTTON2 || e.getButton()==MouseEvent.BUTTON3) {
			//treeCtrl.getMuseLytter().mouseClicked(e);
			MouseEvent e2 = new MouseEvent( this, MouseEvent.MOUSE_RELEASED,
					e.getWhen() + 100000, e.getModifiers(), e.getX(), e.getY(), e.getClickCount(),
					true, e.getButton() );
			getPredefinedAreaController().getTreeCtrl().getMuseLytter().mousePressed(e2);
		}		
	}
	public void mouseReleased(MouseEvent e) { }
	
	public void add_controls() {
		add(getPredefinedAreaController().getTreeScrollPane());
		//Substance 3.3
		Color c = SubstanceLookAndFeel.getActiveColorScheme().getUltraLightColor();
		
		//Substance 5.2
		//Color c = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraLightColor();
		Color areaPanelColor = new Color(SystemColor.control.getRed(), 
				SystemColor.control.getGreen(), 
				SystemColor.control.getBlue(), 
				50);
		
		getPredefinedAreaController().getTreeCtrl().getGui().setBackground(Color.white); //new Color(220, 20, 20));
		getPredefinedAreaController().getTreeCtrl().getGui().getTree().setBackground(Color.white);
	}
	public void init() {
		setVisible(true);
	}
	public void componentResized(ComponentEvent e) {
		this.setPreferredSize(new Dimension(getWidth(), getHeight()));
		//this.setMinimumSize(new Dimension(getWidth(), getHeight()));
		getPredefinedAreaController().getTreeScrollPane().revalidate();
		getPredefinedAreaController().getTreeCtrl().getGui().setMinimumSize(new Dimension(getWidth(), getHeight()));
		getPredefinedAreaController().getTreeCtrl().getGui().revalidate();
		getPredefinedAreaController().getTreeCtrl().getGui().setWidth(getWidth()-20);
		getPredefinedAreaController().getTreeCtrl().getGui().getTree().setMinimumSize(new Dimension(getWidth(), getHeight()));
				
		getPredefinedAreaController().getTreeCtrl().getGui().getTree().revalidate();
		doLayout();
		repaint();
		
	}
	public void componentShown(ComponentEvent e) { }
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent arg0) {

	}

}
