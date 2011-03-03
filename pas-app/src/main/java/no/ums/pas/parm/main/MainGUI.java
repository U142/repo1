package no.ums.pas.parm.main;

import no.ums.pas.parm.object.ObjectController;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.Container;
import java.awt.event.KeyEvent;


public class MainGUI extends JFrame {

	public static final long serialVersionUID = 1;
	private Container gui;
	private JMenuBar menubar;
	private JMenu mFile;
	private JMenu mView;
	private JMenuItem itmExit;
	
	private JMenu menuNew;
	private JMenuItem rootObjectFolder;
	private JMenuItem rootObject;
//	private JMenuItem event;
//	private JMenuItem alert;
	private JMenuItem edit;
	private JMenuItem delete;
	
	private JMenuItem refresh;
	

	
//	private JPanel centerPanel;

	private ObjectController objectCtrl;

	public MainGUI() {

		
//		objectfolder = new JMenuItem("Object folder");
		
		this.gui = getContentPane();

		this.menubar = new JMenuBar();
		this.mFile = new JMenu();
		mFile.setMnemonic(KeyEvent.VK_F);
		this.mView = new JMenu();

		this.itmExit = new JMenuItem();
		
		menuNew = new JMenu("New");
		
		
		rootObjectFolder = new JMenuItem("Root object folder");
		rootObjectFolder.setMnemonic(KeyEvent.VK_F);
		rootObject = new JMenuItem("Root object");
		rootObject.setMnemonic(KeyEvent.VK_O);
//		alert = new JMenuItem("Alert");
//		alert.setMnemonic(KeyEvent.VK_A);
//		event = new JMenuItem("Event");
//		event.setMnemonic(KeyEvent.VK_V);
		menuNew.add(rootObjectFolder);
		menuNew.add(rootObject);
//		menuNew.add(event);
//		menuNew.add(alert);
		menuNew.setMnemonic(KeyEvent.VK_N);
		edit = new JMenuItem("Edit");
		edit.setMnemonic(KeyEvent.VK_E);
		delete = new JMenuItem("Delete");
		delete.setMnemonic(KeyEvent.VK_D);
		
		refresh = new JMenuItem("Refresh");

//		this.centerPanel = new JPanel();
		setJMenuBar(this.menubar);
	}

	public Container getGui() {
		return gui;
	}

	public JMenuItem getItmExit() {
		return itmExit;
	}

	public JMenuBar getMenubar() {
		return menubar;
	}

	public JMenu getMFile() {
		return mFile;
	}

	public ObjectController getObjectCtrl() {
		return objectCtrl;
	}

//	public JPanel getCenterPanel() {
//		return centerPanel;
//	}

//	public JMenuItem getAlert() {
//		return alert;
//	}

	public JMenuItem getDelete() {
		return delete;
	}

	public JMenuItem getEdit() {
		return edit;
	}

//	public JMenuItem getEvent() {
//		return event;
//	}

	public JMenu getMenuNew() {
		return menuNew;
	}

	public JMenuItem getObject() {
		return rootObject;
	}

	public JMenuItem getObjectfolder() {
		return rootObjectFolder;
	}

	public JMenu getMView() {
		return mView;
	}

	public JMenuItem getRefresh() {
		return refresh;
	}
}
