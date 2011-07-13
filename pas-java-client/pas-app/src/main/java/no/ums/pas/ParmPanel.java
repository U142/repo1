package no.ums.pas;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.parm.tree.TreeController;
import org.jvnet.substance.SubstanceLookAndFeel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ParmPanel extends DefaultPanel implements ComponentListener, MouseListener {
	public static final long serialVersionUID = 1;

	protected TreeController treeCtrl;
	/*private ObjectController objectCtrl;
	DefaultMutableTreeNode remNode;
	private ObjectVO object;
	private AlertController alertCtrl;
	private AlertVO alert;
	private EventController eventCtrl;
	private EventVO event;
	private XmlReader xmlreader;
	private Collection objectList;
	private ArrayList allCategorys, allElements;
	private UpdateXML updXml;
	private MapController mapCtrl;
	private MapPanel map;*/
	public int tempPK;
	private ParmController m_parmcontroller;
	protected ParmController get_parmcontroller() { return m_parmcontroller; }
	private GridLayout m_box;
	
	public ParmPanel(ParmController maincontroller) {
		super();
		m_box = new GridLayout(1, 1, 0, 0);
		addComponentListener(this);
		m_parmcontroller = maincontroller;
		treeCtrl = new TreeController(maincontroller);
//		this.tempPK = 0;
//		this.updXml = new UpdateXML(maincontroller, "temp");
//		this.updXml.start();
		setLayout(m_box);
		add_controls();
		get_parmcontroller().getTreeScrollPane().addMouseListener(this);
		init();
	}
	
	public void actionPerformed(ActionEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON2 || e.getButton()==MouseEvent.BUTTON3) {
			//treeCtrl.getMuseLytter().mouseClicked(e);
			MouseEvent e2 = new MouseEvent( this, MouseEvent.MOUSE_RELEASED,
					e.getWhen() + 100000, e.getModifiers(), e.getX(), e.getY(), e.getClickCount(),
					true, e.getButton() );
			get_parmcontroller().getTreeCtrl().getMuseLytter().mousePressed(e2);
		}		
	}
	public void mouseReleased(MouseEvent e) { }
	
	public void add_controls() {
		add(get_parmcontroller().getTreeScrollPane());
		//Substance 3.3
		Color c = SubstanceLookAndFeel.getActiveColorScheme().getUltraLightColor();
		
		//Substance 5.2
		//Color c = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraLightColor();
		
		get_parmcontroller().getTreeCtrl().getGui().setBackground(Color.white); //new Color(255,255,255));
		get_parmcontroller().getTreeCtrl().getGui().getTree().setBackground(Color.white);
	}
	public void init() {
		setVisible(true);
	}
	public void componentResized(ComponentEvent e) {
		this.setPreferredSize(new Dimension(getWidth(), getHeight()));
		//this.setMinimumSize(new Dimension(getWidth(), getHeight()));
		get_parmcontroller().getTreeScrollPane().revalidate();
		get_parmcontroller().getTreeCtrl().getGui().setMinimumSize(new Dimension(getWidth(), getHeight()));
		get_parmcontroller().getTreeCtrl().getGui().revalidate();
		get_parmcontroller().getTreeCtrl().getGui().setWidth(getWidth()-20);
		get_parmcontroller().getTreeCtrl().getGui().getTree().setMinimumSize(new Dimension(getWidth(), getHeight()));
				
		get_parmcontroller().getTreeCtrl().getGui().getTree().revalidate();
		doLayout();
		repaint();
		
	}
	public void componentShown(ComponentEvent e) { }
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent arg0) {

	}
}