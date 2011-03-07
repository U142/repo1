package no.ums.pas.core.menus;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.menus.defines.CheckItem;
import no.ums.pas.core.menus.defines.RadioItemList;
import no.ums.pas.localization.Localization;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


public class MainSelectMenu extends JPanel implements ActionListener, ComponentListener
{

    private static final Log log = UmsLog.getLogger(MainSelectMenu.class);

	public static final long serialVersionUID = 1;
	private MainMenuBar m_menubar;
	private PAS m_pas;

	PAS get_pas() { return m_pas; }
	public MainMenuBar get_bar() { return m_menubar; }

    private ActionListener m_actionlistener;
	
	public class MainMenuBar extends JMenuBar 
	{
		public static final long serialVersionUID = 1;
		protected JMenu m_menu_departments;

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			PAS.pasplugin.onPaintMenuBarExtras(this, g);
		}

		public JMenu get_dept() 
		{ 
			return m_menu_departments; 
		}

		
		public JMenuItem get_item_training_mode()
		{
			return m_item_training_mode;
		}

		public JMenuItem get_item_address_book()
		{
			return m_item_address_book;
		}

		protected JCheckBoxMenuItem m_item_training_mode;
		//private JMenuItem m_item_save_settings;
		
		protected JMenuItem m_item_address_book;

		//private JMenu m_item_gps_epsilon;
		//private SliderMenuItem m_item_gps_epsilon_slider;
		protected JMenu m_item_status_updates;

		protected RadioItemList m_item_departments_checklist;
		protected CheckItem [] m_item_departments_check;

		public MainMenuBar()
		{
			super();
            m_menu_departments = new JMenu(Localization.l("mainmenu_departments"));

            m_item_training_mode = new JCheckBoxMenuItem(Localization.l("mainmenu_trainingmode"));
            m_item_address_book = new JMenuItem(Localization.l("common_address_book"));

			//m_item_gps_epsilon = new JMenu("Point epsilon");
			//m_item_gps_epsilon_slider = new SliderMenuItem(get_pas(), "");

            m_item_status_updates = new JMenu(Localization.l("mainmenu_status_updates"));
			
			updateDeptSelection(false);

			init();
		}
		
		public void updateDeptSelection(boolean current_dept) {
			if(m_item_departments_checklist != null)
				m_item_departments_checklist.clear();
			if(m_menu_departments != null)
				m_menu_departments.removeAll();
			m_item_departments_check = new CheckItem[PAS.get_pas().get_userinfo().get_departments().size()];
			for(int i=0; i < PAS.get_pas().get_userinfo().get_departments().size(); i++) {
				try
				{
					if(current_dept) {
						m_item_departments_check[i] = new CheckItem(PAS.get_pas().get_userinfo().get_departments().get(i).toString(), 
								PAS.get_pas().get_userinfo().get_departments().get(i),
								PAS.get_pas().get_userinfo().get_departments().get(i).equals(PAS.get_pas().get_userinfo().get_current_department()) ? true : false);//(((DeptInfo)PAS.get_pas().get_userinfo().get_departments().get(i)).isDefaultDept() ? true : false));
					}
					else {
						m_item_departments_check[i] = new CheckItem(PAS.get_pas().get_userinfo().get_departments().get(i).toString(), 
																PAS.get_pas().get_userinfo().get_departments().get(i),
																PAS.get_pas().get_userinfo().get_departments().get(i).equals(PAS.get_pas().get_userinfo().get_default_dept()) ? true : false);//(((DeptInfo)PAS.get_pas().get_userinfo().get_departments().get(i)).isDefaultDept() ? true : false));
					}
				}
				catch(Exception e)
				{
					m_item_departments_check[i] = new CheckItem(PAS.get_pas().get_userinfo().get_departments().get(i).toString(), 
							PAS.get_pas().get_userinfo().get_departments().get(i),
							false);			
				}
			}
			m_item_departments_checklist = new RadioItemList(get_pas(), m_item_departments_check, 0, 
															m_menu_departments, m_actionlistener, "act_change_department");	
		}

		public void showHouseEditor(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_house_editor") && b;
			m_actionlistener.actionPerformed(new ActionEvent(show, ActionEvent.ACTION_PERFORMED, "act_show_house_editor"));
		}
		public void showHouseSelect(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_house_select") && b;
			m_actionlistener.actionPerformed(new ActionEvent(show, ActionEvent.ACTION_PERFORMED, "act_show_house_select"));
		}
		public void showMapSelection(boolean b)
		{
			Boolean show = (Boolean)UIManager.get("m_item_map_select") && b;
			m_actionlistener.actionPerformed(new ActionEvent(show, ActionEvent.ACTION_PERFORMED, "act_show_map_select"));
		}

        void init()
		{
			PAS.pasplugin.onAddMainSelectMenu(this);
			m_item_training_mode.setActionCommand("act_trainingmode");
			m_item_address_book.setActionCommand("act_address_book");
		}

	}

	
	public MainSelectMenu(PAS pas, ActionListener actionlistener)
	{
		super();
		m_pas = pas;

		m_actionlistener = actionlistener;
		prepare_controls();
		//setBackground(Color.white);
        StatusActions.EXPORT.setEnabled(false);
        revalidate();
		doLayout();
		addComponentListener(this);
		
		
	}
	void prepare_controls()
	{
	        m_menubar = new MainMenuBar();
	        m_menubar.get_dept();
	        init();
	}
	void init()
	{
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		
	}
	public void componentResized(ComponentEvent e) {
		if(getWidth()<=0 || getHeight()<=0)
		{
			return;
		}
		setBounds(0,0,getWidth(),getHeight());
		m_menubar.setPreferredSize(new Dimension(getWidth(), 25));
		m_menubar.setMinimumSize(new Dimension(500, 25));
		m_menubar.revalidate();

	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }			
}