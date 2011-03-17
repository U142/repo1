package no.ums.pas.core.project;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.ws.WSGetStatusList;
import no.ums.pas.core.ws.WSProject;
import no.ums.pas.localization.Localization;
import no.ums.pas.status.StatusListObject;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.pas.ums.tools.Utils;
import no.ums.ws.pas.UPROJECTREQUEST;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

//import Core.MainUI.*;

public class ProjectDlg extends JDialog implements ComponentListener, WindowListener {
	public static final long serialVersionUID = 1;
	public static final int ACT_PROJECTDLG_CLOSE = 1;
	public static final int ACT_PROJECTDLG_OPEN  = 2;
	public static final int ACT_PROJECTDLG_SAVE  = 3;
	public static final int ACT_PROJECTDLG_CANCEL= 4;

	
	private int m_n_selectedaction = ACT_PROJECTDLG_CLOSE;
	protected void setSelectedAction(int ACT) { m_n_selectedaction = ACT; }
	public int getSelectedAction() { return m_n_selectedaction; }
	
	
	private JFrame m_parent;
	private Project m_project = new Project();
	protected ProjectPanel m_projectpanel;
	protected ProjectPanel get_projectpanel() { return m_projectpanel; }
	private ActionListener m_callback;
	private String m_sz_callback_action;
	protected Project get_project() { return m_project; }
	protected boolean m_b_cancelpressed = false;
	public boolean m_b_newsending = false;
	
	public ProjectDlg(JFrame parent, ActionListener callback, String sz_callback_action, boolean bNewSending) {
		//super(parent, "Create/Open Project", true);
        super(parent, Localization.l("mainmenu_file_project"));
		this.setIconImage(PAS.get_pas().getIconImage());
		try
		{
			this.setAlwaysOnTop(true);
			this.setModal(true);
		}
		catch (Exception e) {
		}

		m_parent = parent;
		m_callback = callback;
		m_sz_callback_action = sz_callback_action;
		m_b_newsending = bNewSending;
		addComponentListener(this);
		init();
		Dimension d = new Dimension(700, 350);
		Dimension ul = Utils.screendlg_upperleft(d);
		setBounds(ul.width, ul.height, d.width, d.height);
		super.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(d.width, d.height));
		setMinimumSize(d);

	}
	protected void close_dialog() {
		super.setVisible(false);
	}
	protected void createPanel() {
		m_projectpanel = new ProjectPanel(true);
        m_projectpanel.start();
	}
	protected void init() {
		createPanel();
		getContentPane().add(m_projectpanel, BorderLayout.CENTER);
	}
	protected void save() {
		try {
			/*HttpPostForm form = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_project.asp");
			form.setParameter("sz_name", get_project().get_projectname());
			XMLProject xml = new XMLProject(form, m_parent, null, get_projectpanel(), m_sz_callback_action);
			xml.start();*/
			UPROJECTREQUEST request = new UPROJECTREQUEST();
			request.setSzName(get_project().get_projectname());
			request.setNProjectpk(new Long(get_project().get_projectpk()));
			new WSProject(request, get_projectpanel(), m_sz_callback_action).start();
			
			//will return download finished to this dialogs panel actionhandler
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public class ProjectPanel extends DefaultPanel {
		public static final long serialVersionUID = 1;
		protected StdTextLabel m_lbl_errormsg = new StdTextLabel("",false, 200);
		protected StdTextLabel m_lbl_projectname = new StdTextLabel(Localization.l("projectdlg_projectname") + ":", true, 85);
        protected StdTextArea m_txt_projectname = new StdTextArea("", false, new Dimension(200,23));
		protected JButton m_btn_save = new JButton(Localization.l("common_save"));
        protected JButton m_btn_cancel = new JButton(Localization.l("common_cancel"));
        protected ProjectList m_project_list;
		protected ArrayList<StatusListObject> m_arr_sendings;
		protected ArrayList<Project> m_arr_projects;
		protected JButton m_btn_open = new JButton(Localization.l("common_open"));

        public ProjectList get_projectlist() { return m_project_list; }
		protected Project m_proj;
		
		//private LoadingPanel m_loader = new LoadingPanel("Idle...", new Dimension(300, 20));
		//protected LoadingPanel get_loader() { return m_loader; }
		
		public ProjectPanel(boolean bNewSending) {
			super();
			//m_loader.removeComponentListener(m_loader);
//			if(bNewSending)
//				m_btn_cancel.setText("No name");

            m_project_list = new ProjectList( new String [] {Localization.l("projectdlg_projectid"), Localization.l("projectdlg_projectname"), Localization.l("common_created"), Localization.l("common_sendings")}, new int [] { 100, 250, 100, 50 });
			init_controls();
			
			add_controls();
			init();
			
			if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights() == 4) {
                m_txt_projectname.setText(Localization.l("main_status_traveller_alert"));
				m_txt_projectname.setSelectionStart(0);
				m_txt_projectname.setSelectionEnd(m_txt_projectname.getText().length());
			}
			//new XMLStatusList(this).start("PAS_getstatuslist_zipped.asp?sz_getfilter=3", false);
			try
			{
				m_project_list.setLoading(true);
			}
			catch(Exception e)
			{
				
			}
		}

        public void start() {
            new WSGetStatusList(this).start();
        }

		public void actionPerformed(ActionEvent e) {
			if("act_save".equals(e.getActionCommand())) {
				setSelectedAction(ACT_PROJECTDLG_SAVE);
				if(m_txt_projectname.getText().length() > 0) {
					get_project().set_projectname(m_txt_projectname.getText());
					save();
				}
				else {
                    m_lbl_errormsg.setText(Localization.l("projectdlg_project_entername"));
                }
					
			}
			else if("act_cancel".equals(e.getActionCommand())) {
				// Her fjernet jeg muligheten for "no project"
				//m_callback.actionPerformed(new ActionEvent("<No Project>", ActionEvent.ACTION_PERFORMED, "act_no_project"));
				m_b_cancelpressed = true;
				setSelectedAction(ACT_PROJECTDLG_CLOSE);
				//setSelectedAction(ACT_PROJECTDLG_CANCEL);
				close_dialog();
			}
			else if(m_sz_callback_action.equals(e.getActionCommand())) {
				//Server XML is finished, return object to parent and close window
				close_dialog();
				m_project = (Project)e.getSource();
				ActionEvent action = new ActionEvent(get_project(), ActionEvent.ACTION_PERFORMED, m_sz_callback_action);
				m_callback.actionPerformed(action);
			}
			else if("act_download_finished".equals(e.getActionCommand())) {
				//m_arr_projects = (ArrayList<Project>)e.getSource();
				m_arr_sendings = (ArrayList<StatusListObject>)e.getSource();
				m_project_list.start_search();
				m_project_list.setLoading(false);				
			}
			else if("act_open".equals(e.getActionCommand())) {
				if(m_proj!=null) {
					setSelectedAction(ACT_PROJECTDLG_OPEN);
					openProject(m_proj);
				}
			}
		}
		protected void openProject(Project proj) {
			if(!PAS.pasplugin.onOpenProject(proj, 0))
				PAS.get_pas().actionPerformed(new ActionEvent(proj, ActionEvent.ACTION_PERFORMED, "act_project_activate"));
			close_dialog();			
		}
		public void init_controls() {
			m_btn_save.setActionCommand("act_save");
			m_btn_save.addActionListener(this);
			m_btn_cancel.setActionCommand("act_cancel");
			m_btn_cancel.addActionListener(this);
			m_btn_open.setActionCommand("act_open");
			m_btn_open.addActionListener(this);
		}
		public void add_controls() {
			m_gridconst.ipadx = 5;
			set_gridconst(0, inc_panels(), 2, 1);
			add(m_lbl_errormsg, m_gridconst);
			m_lbl_errormsg.setForeground(Color.RED);
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_projectname, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_projectname, m_gridconst);
			set_gridconst(3, get_panel(), 1, 1, GridBagConstraints.EAST);
			add(m_btn_save, m_gridconst);
			set_gridconst(4, get_panel(), 1, 1, GridBagConstraints.EAST);
			add(m_btn_cancel, m_gridconst);
			set_gridconst(0, inc_panels(), 10, 1);
			add(m_project_list.get_scrollpane(), get_gridconst());
			set_gridconst(0, inc_panels(), 2, 1);
			add(m_btn_open, get_gridconst());
			//set_gridconst(0, 2, 2, 2, GridBagConstraints.SOUTH);
			//add(m_loader, m_gridconst);
		}
		public void init() {
			pack();
			m_btn_open.setEnabled(false);
			setVisible(true);
		}
	
		public class ProjectList extends SearchPanelResults {
			public static final long serialVersionUID = 1;
			public ProjectList(String [] sz_columns, int [] n_width) {
				super(sz_columns, n_width, null, new Dimension(250, 100), ListSelectionModel.SINGLE_SELECTION);
                setBorder(BorderFactory.createTitledBorder(Localization.l("projectdlg_open_project")));
			}
			public boolean is_cell_editable(int row, int col) {
				return false;
			}
			public ArrayList<Project> createProjectList(ArrayList<StatusListObject> arr_slo) {
				ArrayList<Project> ret = new ArrayList<Project>();
				ArrayList<Object> projpk = new ArrayList<Object>();
				for(int i=0; i < arr_slo.size(); i++) {
					Project p = arr_slo.get(i).get_project();//((StatusListObject)arr_slo.get(i)).get_project();
					if(!projpk.contains(p.get_projectpk())) { //contains(p.get_projectpk())) {
						ret.add(p);
						projpk.add(p.get_projectpk());
					} else {
						//get old project pointer
						for(int j=0; j < ret.size(); j++) {
							if(((Project)ret.get(j)).get_projectpk().equals(p.get_projectpk())) {
								p = (Project)ret.get(j);
								break;
							}
						}
					}
					p.add_status_sending((StatusListObject)arr_slo.get(i));
				}
				return ret;
			}
			public void start_search() {
				/*for(int i=0; i < m_arr_projects.size(); i++) {
					StatusListObject item = (StatusListObject )m_arr_projects.get(i);
					Object [] obj = new Object[] { item.get_project(), item.get_project().get_projectname(), TextFormat.format_datetime(item.get_project().get_createtimestamp()),  "0" };
					this.insert_row(obj, -1);
				}*/
				ArrayList<Project> project_list = createProjectList(m_arr_sendings);
				m_arr_projects = project_list;
                for (Project item : m_arr_projects) {
                    Object[] obj = new Object[]{item, item.get_projectname(), TextFormat.format_datetime(item.get_createtimestamp()), Integer.toString(item.get_num_sendings())};
                    this.insert_row(obj, -1);
                }
				
			}
			protected void onMouseLClick(int n_row, int n_col, Object[] rowcontent, Point p) {
				try {
					m_proj = (Project)rowcontent[0];
					m_btn_open.setEnabled(true);
				} catch(Exception e) {
					m_btn_open.setEnabled(false);
					m_proj = null;
				}
				
			}
			protected void onMouseLDblClick(int n_row, int n_col, Object[] rowcontent, Point p) {
				//Project proj = (Project)rowcontent[0];
				//openProject(proj);
				m_btn_open.doClick();
			}
			protected void onMouseRClick(int n_row, int n_col, Object[] rowcontent, Point p) {
				
			}
			protected void onMouseRDblClick(int n_row, int n_col, Object[] rowcontent, Point p) {
				
			}
			protected void valuesChanged() {
				
			}
		}
	}

	public void componentHidden(ComponentEvent e) {
		
	}

	public void componentMoved(ComponentEvent e) {
		
	}

	public void componentResized(ComponentEvent e) {
		if(getWidth()<0 || getHeight()<0)
			return;
		m_projectpanel.setPreferredSize(new Dimension(getWidth()-20, getHeight()));
		Dimension d = new Dimension(getWidth()-40, getHeight()-150);
		m_projectpanel.get_projectlist().get_scrollpane().setPreferredSize(d);
		m_projectpanel.m_project_list.setPreferredSize(d);
		//m_projectpanel.get_projectlist().get_scrollpane().setSize(d);
		//m_projectpanel.get_projectlist().get_scrollpane().get_table().setPreferredSize(d);
		m_projectpanel.revalidate();
		m_projectpanel.get_projectlist().get_scrollpane().revalidate();
		m_projectpanel.get_projectlist().get_table().revalidate();
		validate();
		repaint();
	}

	public void componentShown(ComponentEvent e) {
		
	}

	public void windowOpened(WindowEvent e) {
		
	}

	public void windowClosing(WindowEvent e) {
		setSelectedAction(ACT_PROJECTDLG_CLOSE);
	}

	public void windowClosed(WindowEvent e) {
		
	}

	public void windowIconified(WindowEvent e) {
		
	}

	public void windowDeiconified(WindowEvent e) {
		
	}

	public void windowActivated(WindowEvent e) {
		
	}

	public void windowDeactivated(WindowEvent e) {
		
	}
}