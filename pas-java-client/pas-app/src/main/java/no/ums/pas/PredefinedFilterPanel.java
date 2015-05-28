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
import no.ums.pas.area.tree.FilterTreeController;
import no.ums.pas.core.controllers.PredefinedFilterController;
import no.ums.pas.core.defines.DefaultPanel;

/**
 * @author abhinava
 */
public class PredefinedFilterPanel extends DefaultPanel implements ComponentListener, MouseListener{

	private static final Log log = UmsLog.getLogger(PredefinedFilterPanel.class);
	protected FilterTreeController treeCtrl;
	
	public int tempPK;
	private PredefinedFilterController PredefinedFilterController;
	protected PredefinedFilterController getPredefinedFilterController() { return PredefinedFilterController; }
	private GridLayout m_box;
	
	public PredefinedFilterPanel(PredefinedFilterController maincontroller) {
		super();
		m_box = new GridLayout(1, 1, 0, 0);
		addComponentListener(this);
		PredefinedFilterController = maincontroller;
		treeCtrl = new FilterTreeController(maincontroller);
		this.tempPK = 0;
        setLayout(m_box);
        add_controls();
		getPredefinedFilterController().getTreeScrollPane().addMouseListener(this);
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
			getPredefinedFilterController().getTreeCtrl().getMuseLytter().mousePressed(e2);
		}		
	}
	public void mouseReleased(MouseEvent e) { }
	
	public void add_controls() {
		add(getPredefinedFilterController().getTreeScrollPane());
		//Substance 3.3
		Color c = SubstanceLookAndFeel.getActiveColorScheme().getUltraLightColor();
		
		//Substance 5.2
		//Color c = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraLightColor();
		Color areaPanelColor = new Color(SystemColor.control.getRed(), 
				SystemColor.control.getGreen(), 
				SystemColor.control.getBlue(), 
				50);
		
		getPredefinedFilterController().getTreeCtrl().getGui().setBackground(Color.white); //new Color(220, 20, 20));
		getPredefinedFilterController().getTreeCtrl().getGui().getTree().setBackground(Color.white);
	}
	public void init() {
		setVisible(true);
	}
	public void componentResized(ComponentEvent e) {
		this.setPreferredSize(new Dimension(getWidth(), getHeight()));
        getPredefinedFilterController().getTreeScrollPane().revalidate();
		getPredefinedFilterController().getTreeCtrl().getGui().setMinimumSize(new Dimension(getWidth(), getHeight()));
		getPredefinedFilterController().getTreeCtrl().getGui().revalidate();
		getPredefinedFilterController().getTreeCtrl().getGui().setWidth(getWidth()-20);
		getPredefinedFilterController().getTreeCtrl().getGui().getTree().setMinimumSize(new Dimension(getWidth(), getHeight()));
				
		getPredefinedFilterController().getTreeCtrl().getGui().getTree().revalidate();
		doLayout();
		repaint();
		
	}
	public void componentShown(ComponentEvent e) { }
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent arg0) {

	}

}
