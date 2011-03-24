package no.ums.pas.core.controllers;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.ComboRow;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.OpenStatusFrame;
import no.ums.pas.core.mainui.StatusItemList;
import no.ums.pas.core.mainui.StatuscodeFrame;
import no.ums.pas.core.menus.StatusActions;
import no.ums.pas.core.menus.ViewOptions;
import no.ums.pas.core.ws.WSGetStatusItems;
import no.ums.pas.core.ws.WSGetStatusList;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.HouseItem;
import no.ums.pas.maps.defines.Houses;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.send.SendController;
import no.ums.pas.status.LBASEND;
import no.ums.pas.status.LBATabbedPane;
import no.ums.pas.status.StatusCode;
import no.ums.pas.status.StatusCodeList;
import no.ums.pas.status.StatusItemObject;
import no.ums.pas.status.StatusSending;
import no.ums.pas.status.StatusSendingList;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.PopupDialog;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.pas.ums.tools.Timeout;
import no.ums.pas.ums.tools.calendarutils.DateTime;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


/*
 STATUS1_ = "Parse queue (voicebroadcast)"
 STATUS2_ = "Parsing (voicebroadcast)"
 STATUS3_ = "Writing (voicebroadcast)"
 STATUS4_ = "Parse queue (backbone)"
 STATUS5_ = "Parsing (backbone)"
 STATUS6_ = "Send queue (backbone)"
 STATUS7_ = "<b><font color=""#006600"">Finished</font></b>"
 STATUSSTOP_ = "<b><font color=""#000066"">Stopped</font></b>"
 STATUSERR_ = "<b><font color=""#FF0000"">Error</font></b>"
 STATUSNOREC_ = "<b><font color=""#FF0000"">No recipients</font></b>"
 */

public class StatusController extends Controller implements ActionListener {

    private static final Log log = UmsLog.getLogger(StatusController.class);

    
    
	public StatusController() {
		super();
		m_statuscodeframe = new StatuscodeFrame();
		m_n_autoupdate_seconds = 5;
		set_autoupdate(StatusActions.AUTOMATIC_UPDATE.isSelected());
		setClosed(); //initialize as closed
	}

	public static final String STATUS_1 = Localization.l("main_status_progress_parsequeue_bcp");
    public static final String STATUS_2 = Localization.l("main_status_progress_parsing_bcp");
    public static final String STATUS_3 = Localization.l("main_status_progress_writing_bcp");
    public static final String STATUS_4 = Localization.l("main_status_progress_parsequeue_aep");
    public static final String STATUS_5 = Localization.l("main_status_progress_parsing_aep");
    public static final String STATUS_6 = Localization.l("main_status_progress_send_queue_bb");
    public static final String STATUS_7 = Localization.l("common_finished");
    public static final String STATUS_8 = Localization.l("common_cancelling");
    
    public static final String STATUSSTOP = Localization.l("common_stopped"); // l_altjmp=1
    public static final String STATUSERR = Localization.l("common_error"); // else
    public static final String STATUSNOREC = Localization.l("main_status_progress_no_recipients"); // -2

    // private XMLStatusItems m_xmlstatusitems = null;
	// private XMLStatusList m_xmlstatuslist = null;
	private OpenStatusFrame m_statuslistframe = null;
	private StatuscodeFrame m_statuscodeframe = null;
	private Houses m_houses = null;
	private int m_n_alertborder = 0;
	private boolean m_b_alertborder_dir = true;
	private boolean m_b_autoupdate = false;

	private StatusSendingList m_sendings = new StatusSendingList();

	public StatusSendingList get_sendinglist() {
		return m_sendings;
	}

	// private int m_n_autoupdate_seconds = 5;
	private int m_n_max_item = 0;
	private int m_n_max_date = 0;
	private int m_n_max_time = 0;

	public int get_item_filter() {
		return m_n_max_item;
	}

	public int get_date_filter() {
		return m_n_max_date;
	}

	public int get_time_filter() {
		return m_n_max_time;
	}

	public void set_item_filter(int n) {
		if (n > m_n_max_item)
			m_n_max_item = n;
	}

	private void set_date_filter(int n) {
		m_n_max_date = n;
	}

	private void set_time_filter(int n) {
		m_n_max_time = n;
	}

	public void set_datetime_filter(DateTime t) {
		if (get_date_filter() == t.get_date()
				&& t.get_time() > get_time_filter())
			set_time_filter(t.get_time());
		else if (t.get_date() > get_date_filter()) {
			set_date_filter(t.get_date());
			set_time_filter(t.get_time());
		}
	}

	private void reset_filters() {
		m_n_max_item = 0;
		m_n_max_date = 0;
		m_n_max_time = 0;
	}

	private String m_sz_current_projectpk = "-1";
	private int m_n_current_refno = -1;

	private void set_current_refno(int n) {
		m_n_current_refno = n;
	}

	public int get_current_refno() {
		return m_n_current_refno;
	}

	private void set_current_projectpk(String s) {
		m_sz_current_projectpk = s;
	}

	public String get_current_projectpk() {
		return m_sz_current_projectpk;
	}

	/* STATUS DATA */
	private StatusCodeList m_statuscodes = null;
	private NavStruct m_nav_init = null;

	public OpenStatusFrame get_statusframe() {
		return m_statuslistframe;
	}

	public StatuscodeFrame get_statuscodeframe() {
		return m_statuscodeframe;
	}

	public synchronized StatusCodeList get_statuscodes() {
		return m_statuscodes;
	}

	public Houses get_houses() {
		return m_houses;
	}

	private LBATabbedPane m_lba_total_tabbed = new LBATabbedPane();

	public LBATabbedPane getLBATotalPane() {
		return m_lba_total_tabbed;
	}

	public void actionPerformed(ActionEvent e) {
		if ("act_set_itemfilter".equals(e.getActionCommand())) {
			set_item_filter(((Integer) e.getSource()).intValue());
		} else if ("act_set_datetimefilter".equals(e.getActionCommand())) {
			// set_date_filter(((Integer)e.getSource()).intValue());
			set_datetime_filter((DateTime) e.getSource());
		}
		/*
		 * else if("act_set_timefilter".equals(e.getActionCommand())) {
		 * set_time_filter(((Integer)e.getSource()).intValue()); }
		 */
		else if ("act_insert_statusitem".equals(e.getActionCommand())) {
			// StatusItemObject item = (StatusItemObject)e.getSource();
			// ((StatusItemList)m_items)._add(item);
		} else if ("act_statusupdate_complete".equals(e.getActionCommand())) {
			status_update();
		} else if ("act_insert_statuscode".equals(e.getActionCommand())) {
			StatusCode code = (StatusCode) e.getSource();
			if (m_statuscodes != null)
				m_statuscodes._add(code);
			else
				System.out.println("Don't do anything");
		} else if ("act_init_status".equals(e.getActionCommand())) {
			init_arrays();
		}
		else if ("act_set_nav_init".equals(e.getActionCommand())) {
			if (b_newrefno) {
				NavStruct nav_init = (NavStruct) e.getSource();
				set_nav_init(nav_init);
			}
		} else if ("act_add_sending".equals(e.getActionCommand())) {
			StatusSending sending = (StatusSending) e.getSource();
			try {
				get_sendinglist().add_sending(sending,
						PAS.get_pas().get_eastcontent().get_statuspanel());
			} catch (Exception err) {
				Error.getError().addError("StatusController",
						"Exception in act_add_sending", err, 1);
				System.out.println(err.getMessage());
				err.printStackTrace();
			}
		} else if ("act_update_cellbroadcast".equals(e.getActionCommand())) {
			// contains an arraylist of StatusCellBroadcast objects. Each object
			// contains a parent refno
			// ArrayList<Object> arr_sendings =
			// (ArrayList<Object>)e.getSource();
			try {
				// updateCellBroadcast(arr_sendings);
			} catch (Exception err) {

			}
		}
	}

	/*
	 * public void updateCellBroadcast(ArrayList arr) { for(int i=0; i <
	 * arr.size(); i++) { StatusCellBroadcast cell =
	 * (StatusCellBroadcast)arr.get(i);
	 * get_sendinglist().updateCellBroadcast(cell); } }
	 */
	
	public void enableMainStatusVoice(boolean b)
	{
		PAS.get_pas().get_eastcontent().get_statuspanel().enableVoicePanel(b);

	}

	public void updateLBA(List<LBASEND> arr) {
		boolean b_enable_lbapanel = false;
		boolean b_enable_voicepanel = false;

		/*if(arr.size()==0)
			PAS.get_pas().get_eastcontent().get_statuspanel().enableLBAPanel(
				b_enable_lbapanel, null);*/
		
		
		
		get_sendinglist().ClearAllLBA();
		for (int i = 0; i < arr.size(); i++) {
			get_sendinglist().updateLBA_2_0(arr.get(i));
		}
		get_sendinglist().finalizeLBA_2_0();
		
		// all LBA sendings are now attached to their voice sendings.
		// calc statistics pr LBA is also finished
		/*
		 * int n_overall_lba_sendings = 0; int n_overall_progress = 0; int
		 * n_overall_delivered = 0; int n_overall_items = 0; int
		 * n_overall_processed = 0;
		 */

		LBASEND lbasend_total = new LBASEND();
		lbasend_total.n_status = -1;
		int n_pending = 0;
		int n_sending = 0;
		int n_finished = 0;
		int n_cancelled = 0;
		int n_failed = 0;
		int n_lbacount = 0;
		int n_overlay_ready = 0;
		for (int i = 0; i < get_sendinglist().size(); i++) {
			n_overlay_ready = 0;
			StatusSending ss = get_sendinglist().get(i);
			switch(get_sendinglist().get(i).get_type())
			{
			case 1: //voice
			case 2: //sms
			case 3: //email
				b_enable_voicepanel = true;
				break;
			case 4: //LBA
			case 5: //TAS
				break;
			}
			LBASEND ref = ss.getLBA();
			if((ss.get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) == 0 && (ss.get_addresstypes() & SendController.SENDTO_TAS_SMS) == 0)
				ref = null;
			if (ref != null) {
				//if first status, then set ref.status. if not first status, set total.status if ref.status is worse than current total.status
				lbasend_total.n_status = (lbasend_total.n_status<0 ? ref.n_status : (ref.n_status<lbasend_total.n_status ? ref.n_status : lbasend_total.n_status));
				
				lbasend_total.n_proc += (ref.n_proc >= 0 ? ref.n_proc : 0)
						+ (ref.getCancelled()); // total processed items for all
												// LBA sendings
				lbasend_total.n_items += (ref.n_items >= 0 ? ref.n_items : 0); // total
																				// count
																				// of
																				// items
																				// for
																				// all
																				// LBA
																				// sendings
				for (int cc = 0; cc < ref.hist_cc.size(); cc++) {
					LBASEND.LBAHISTCC tempcc = ref.hist_cc.get(cc);
					lbasend_total.addToHistCC(tempcc);
				}
				b_enable_lbapanel = true;
				if (ref.n_status >= LBASEND.LBASTATUS_PREPARED_CELLVISION
						&& ref.n_status <= LBASEND.LBASTATUS_FINISHED)
					n_overlay_ready++;
				switch (ref.n_status) {
				case LBASEND.LBASTATUS_SENT_TO_LBA: // Sent to UMS LBA
				case LBASEND.LBASTATUS_PARSING_LBAS:
				case LBASEND.LBASTATUS_PREPARING_CELLVISION:
				case LBASEND.LBASTATUS_PREPARED_CELLVISION:
				case LBASEND.LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE:
				case LBASEND.LBASTATUS_PROCESSING_SUBSCRIBERS_CELLVISION:
					n_pending++;
					break;
				case LBASEND.LBASTATUS_SENDING: // Sending CellVision
					n_sending++;
					break;
				case LBASEND.LBASTATUS_FINISHED: // finished
					n_finished++;
					break;
				case LBASEND.LBASTATUS_CANCELLED:
				case LBASEND.LBASTATUS_CANCELLED_BY_USER_OR_SYSTEM:
				case LBASEND.LBASTATUS_CANCELLED_AFTER_LOOKUP:
					n_cancelled++;
					break;
				case LBASEND.LBASTATUS_EXCEPTION_EXECUTE_AREAALERT:
				case LBASEND.LBASTATUS_EXCEPTION_PREPARE_AREAALERT:
				case LBASEND.LBASTATUS_EXCEPTION_EXECUTE_CUSTOMALERT:
				case LBASEND.LBASTATUS_EXCEPTION_PREPARE_CUSTOMALERT:
				case LBASEND.LBASTATUS_EXCEPTION_EXECUTE_PREPARED_ALERT:
				case LBASEND.LBASTATUS_EXCEPTION_CANCEL_PREPARED_ALERT:
				case LBASEND.LBASTATUS_FAILED_EXECUTE_AREAALERT:
				case LBASEND.LBASTATUS_FAILED_PREPARE_AREAALERT:
				case LBASEND.LBASTATUS_FAILED_EXECUTE_CUSTOMALERT:
				case LBASEND.LBASTATUS_FAILED_PREPARE_CUSTOMALERT:
				case LBASEND.LBASTATUS_FAILED_EXECUTE_PREPARED_ALERT:
				case LBASEND.LBASTATUS_FAILED_CANCEL_PREPARED_ALERT:
					n_failed++;
					break;
				default: // pending
					n_pending++;
					break;
				}
				n_lbacount++;
			}
			ss.update_ui();
			ss.showOverlayButtons(b_enable_lbapanel);
			
		}
		lbasend_total.CalcStatistics();
		String sz_lba_bordertext = _createBorderText(n_pending, n_sending,
				n_finished, n_cancelled, n_failed, n_lbacount);
		/*
		 * if(n_finished>0) sz_lba_bordertext = " Finished: " + n_finished;
		 * if(n_sending>0) sz_lba_bordertext += " Sending: " + n_sending;
		 * if(n_pending>0) sz_lba_bordertext += " Pending: " + n_pending;
		 * sz_lba_bordertext += " (of " + n_lbacount + " sendings)";
		 */

		PAS.get_pas().get_eastcontent().get_statuspanel().set_ssl(get_sendinglist());
		PAS.get_pas().get_eastcontent().get_statuspanel().setBorderTextLBA(
				sz_lba_bordertext);
		PAS.get_pas().get_eastcontent().get_statuspanel().enableLBAPanel(
				b_enable_lbapanel, lbasend_total);
		PAS.get_pas().get_eastcontent().get_statuspanel().enableVoicePanel(b_enable_voicepanel);
		PAS.get_pas().get_eastcontent().get_statuspanel().updateLBAStatistics();

	}
	

	protected String _createBorderText(int n_pending, int n_sending,
			int n_finished, int n_cancelled, int n_failed, int n_totalsendings) {
		String ret = "";
		if (n_finished > 0) {
            ret = "    " + Localization.l("common_finished") + ": " + n_finished;
        }
		if (n_cancelled > 0) {
            ret += "    " + Localization.l("common_cancelled") + ": " + n_cancelled;
        }
		if (n_sending > 0) {
            ret += "    " + Localization.l("common_sending") + ": " + n_sending;
        }
		if (n_pending > 0) {
            ret += "    " + Localization.l("common_pending") + ": " + n_pending;
        }
		if (n_failed > 0) {
            ret += "    " + Localization.l("common_failed") + ": " + n_failed;
        }
		ret += "    (" + Localization.l("common_x_of_y") + " " + n_totalsendings + " "
				+ Localization.l("common_sendings") + ")";
		return ret;
	}

	/*
	 * public void add_sending(StatusSending s) {
	 * get_sendinglist().add_sending(s,
	 * PAS.get_pas().get_eastcontent().get_statuspanel()); }
	 */

	public StatusItemList getStatusItems() {
		return (StatusItemList) m_items;
	}

	public StatusCodeList getStatusCodes() {
		return m_statuscodes;
	}

	private void init_arrays() {
		get_sendinglist().clear();
		m_statuscodeframe.clear();
		m_lba_total_tabbed.clear();

		m_items = new StatusItemList();
		m_statuscodes = new StatusCodeList();
		m_houses = new Houses(false);
	}

	public synchronized void status_update() {
		if (b_newrefno) {
			PAS.get_pas().get_eastcontent().flip_to(
					EastContent.PANEL_STATUS_LIST);
			b_newrefno = false;
			PAS.get_pas().get_inhabitantframe().clear();
		}
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	try
            	{
	                set_statusitems();
	                onDownloadFinished();
            	}
            	finally
            	{
            		
            	}
            }
        });
	}

	private void inc_alertborder() {
		if (m_b_alertborder_dir)
			m_n_alertborder += 1;
		else if (!m_b_alertborder_dir)
			m_n_alertborder -= 3;
		if (m_n_alertborder > 10)
			m_b_alertborder_dir = false;
		else if (m_n_alertborder <= 0)
			m_b_alertborder_dir = true;

	}

	/* from abstract */
	void create_filter() {

	}

	protected void onDownloadFinished() {
		set_lastupdate();
		set_updates_in_progress(false);
		PAS.get_pas().get_eastcontent().get_statuspanel()
				.updateMainStatusStatistics();
	}

	public synchronized void set_statuscolor(int n_status, Color col) {
		StatusCode current;
		for (int i = 0; i < get_statuscodes().size(); i++) {
			current = (StatusCode) get_statuscodes().get(i);
			if (current.get_code() == n_status) {
				current.set_color(col);
				get_houses().update_statuscolor(n_status, col);
				break;
			}
		}
		set_visibility_change(true);
	}

	public synchronized void set_statuscolor(StatusCode code, Color col) {
		code.set_color(col);
		get_houses().update_statuscolor(code.get_code(), col);
		set_visibility_change(true);
	}

	public synchronized StatusCode find_status(int n_status) {
		StatusCode current;
		for (int i = 0; i < get_statuscodes().size(); i++) {
			current = (StatusCode) get_statuscodes().get(i);
			if (current.get_code() == n_status) {
				return current;
			}
		}
		return null;
	}

	public synchronized void show_statuscode(int n_status, boolean b_show) {
		StatusCode current;
		for (int i = 0; i < get_statuscodes().size(); i++) {
			current = (StatusCode) get_statuscodes().get(i);
			if (current.get_code() == n_status) {
				current.set_visible(b_show);
				break;
			}
		}
		// set_house_visibility();
		// get_pas().kickRepaint();
		set_visibility_change(true);
	}

	public synchronized Color get_statuscolor(int n_status) {
		StatusCode current;
		for (int i = 0; i < get_statuscodes().size(); i++) {
			current = (StatusCode) get_statuscodes().get(i);
			if (current.get_code() == n_status)
				return current.get_color();
		}
		return null;
	}

	int n_number_of_active_alertborders = 0;

	public void activate_alertborder(int n_status, boolean b_activate) {
		StatusCode current;
		for (int i = 0; i < get_statuscodes().size(); i++) {
			current = (StatusCode) get_statuscodes().get(i);
			if (current.get_code() == n_status) {
				current.set_alert(b_activate);
				/*
				 * if(b_activate) n_number_of_active_alertborders ++; else {
				 * n_number_of_active_alertborders --; }
				 */
			}
		}
		// PAS.get_pas().actionPerformed(new ActionEvent(new Boolean(true),
		// ActionEvent.ACTION_PERFORMED, "act_start_repaint_cycling"));
	}

	public synchronized int get_alertborder() {
		return m_n_alertborder;
	}

	public void update_alertborder() {
		inc_alertborder();
	}

	public synchronized void set_statusitems(/*
												 * ArrayList objects, ArrayList
												 * codes
												 */) {
		get_statuscodeframe().fill();
		get_houses().sort_houses(get_items(), true);
        ViewOptions.TOGGLE_STATUSCODES.putValue(Action.SELECTED_KEY, true);
        StatusActions.EXPORT.setEnabled(true);
        PAS.get_pas().get_drawthread().set_neednewcoors(true);
		set_visibility();
		PAS.get_pas().kickRepaint();

		// create border text for main status
		int n_pending = 0;
		int n_sending = 0;
		int n_finished = 0;
		int n_sendingcount = 0;

		int n_totitem = 0;
		int n_processed = 0;

		for (int i = 0; i < get_sendinglist().size(); i++) {
			// main properties
			
			StatusSending ss = get_sendinglist().get_sending(i);

			if(SendController.IsAddressBased(ss.get_sendchannel()))
			{
				switch (ss.get_sendingstatus()) {
				case 6:
					n_sending++;
					break;
				case 7:
					n_finished++;
					break;
				default:
					n_pending++;
					break;
				}
				n_sendingcount++;
			}

			// detailed properties
			n_totitem += ss.get_totitem();
			n_processed += ss.get_proc();

			try {
				ss.setSendingnameLabel();
			} catch (Exception e) {

			}

		}
		try {
			String sz_text = _createBorderText(n_pending, n_sending,
					n_finished, 0, 0, n_sendingcount);
			PAS.get_pas().get_eastcontent().get_statuspanel()
					.setBorderTextVoice(sz_text);
			PAS.get_pas().get_eastcontent().get_statuspanel()
					.updateVoiceStatistics((n_processed<=0 ? -1 : n_processed), (n_totitem<=0 ? -1 : n_totitem));
		} catch (Exception e) {

		}

	}

	public synchronized void set_nav_init(NavStruct nav) {
		m_nav_init = nav;
		if(Variables.getNavigation().setNavigation(nav._lbo, nav._rbo,
                nav._ubo, nav._bbo))
			PAS.get_pas().get_mappane().load_map(true);
		// PAS.get_pas().kickRepaint();
	}

	public synchronized void set_visibility() {
		boolean b_found;
		if (get_houses() == null)
			return;
		if (get_houses().get_houses() == null)
			return;

		for (int j = 0; j < get_houses().get_houses().size(); j++) {
			get_houses().get_houses().get(j)
					.reset_current_visible_inhab();
			b_found = false;
			int n_occurances = 0;
			boolean b_alert = false;
			for (int i = 0; i < get_statuscodes().size(); i++) {
				n_occurances = get_houses().get_houses().get(j)
						.find_statuscode(get_statuscodes().get(i)
                                .get_code());
				if (n_occurances > 0) {
					if (get_statuscodes().get(i).get_visible()) {
						if (!b_found) // active color of the highest rang
							get_houses().get_houses().get(j)
									.set_active_color(get_statuscodes()
                                            .get(i).get_color());
						b_found = true;
						get_houses().get_houses().get(j)
								.inc_current_visible_inhab(n_occurances);
						if (get_statuscodes().get(i).get_alert())
							b_alert = true;
					}
				}
			}
			get_houses().get_houses().get(j).set_alert(b_alert);
			get_houses().get_houses().get(j).set_visible(b_found);
		}
		set_visibility_change(false);
	}

	public void drawItems(Graphics gfx) {
		if (get_sendinglist() != null) {
			for (int i = 0; i < get_sendinglist().size(); i++) {
				ShapeStruct shape = get_sendinglist().get_sending(i)
						.get_shape();
				if (ViewOptions.TOGGLE_POLYGON.isSelected()) {
					try {
						shape.draw(gfx, Variables.getNavigation(), false,
								true, false, null);
					} catch (Exception e) {
                        log.warn("Failed to update drawing", e);
					}
				}
			}
		}

		if (get_houses() != null && get_houses().is_housesready())// &&
																	// get_houses().is_housesready())
		{
			set_visibility();
			get_houses().draw_houses(gfx, get_alertborder(),
					PAS.get_pas().get_mapproperties().get_border_activated(),
					PAS.get_pas().get_mapproperties().get_showtext(),
					PAS.get_pas().get_mapproperties().get_fontsize(),
					HouseController.ADR_TYPES_SHOW_ALL_, null);
		}
		// if(get_alertborders_activated())
		{
			update_alertborder();
			//PAS.get_pas().kickRepaint();
		}
	}

	public void goto_house(int n_item) {
		if (get_houses() != null) {
			Timeout time = new Timeout(1, 20);
			while (1 == 1) {
				if (get_houses().is_housesready())
					break;
				try {
					Thread.sleep(time.get_msec_interval());
				} catch (Exception e) {
					Error.getError().addError("StatusController",
							"Exception in goto_house", e, 1);
				}
				time.inc_timer();
				if (time.timer_exceeded())
					return;
			}
			PAS.get_pas().get_drawthread().set_suspended(true);
			StatusItemObject current = null;
			for (int i = 0; i < get_items().size(); i++) {
				current = (StatusItemObject) get_items().get(i);
				if (current.get_item() == n_item)
					break;
			}
			// HouseItem current;
			PAS.get_pas().get_drawthread().set_suspended(false);
			if (current != null) {
				// PAS.get_pas().add_event("Going to " + current.get_lon() + " "
				// + current.get_lat(), null);
				Variables.getNavigation().exec_adrsearch(
                        current.get_lon(), current.get_lat(), 100);
			}
		}
	}

	public void refresh_search_houses() {
		if (PAS.get_pas().get_inhabitantframe().isVisible()) {
			search_houses(n_search_status, b_search_all, dim_screen_coor_search);
		}
	}

	private int n_search_status = -99999;
	public int get_n_search_status() { return n_search_status; }
	private boolean b_search_all = false;
	private MapPointLL dim_screen_coor_search = new MapPointLL(-99999,-99999);

	public synchronized void search_houses(int n_status, boolean b_all, MapPointLL ll_search) {
		PAS.get_pas().get_inhabitantframe().m_inhabitantpanel.pushSelection();
		n_search_status = n_status;
		b_search_all = b_all;
		dim_screen_coor_search = ll_search;
		boolean b_search_by_coor = ll_search.isSet();
		
		PAS.get_pas().get_inhabitantframe().set_visible(true);
		if (get_houses() != null) {
			Timeout time = new Timeout(1, 100);
			while (1 == 1) {
				if (get_houses().is_housesready())
					break;
				try {
					Thread.sleep(time.get_msec_interval());
				} catch (Exception e) {
					Error.getError().addError("StatusController",
							"Exception in search_houses", e, 1);
				}
				time.inc_timer();
				if (time.timer_exceeded())
					return;
			}
			PAS.get_pas().get_drawthread().set_suspended(true);
			HouseItem current;
			int n_count = 0;
			int n_total_inhabitants = 0;
			Object[] data;
			PAS.get_pas().get_inhabitantframe().get_panel().clear();
			for (int i = 0; i < get_houses().size(); i++) {
				current = (HouseItem) get_houses().get_houses().get(i);
				current.set_selected(false);
				//if ((current.get_screencoords() != null && current
				//		.get_visible())
				//		|| b_all) {
				
				//if b_all or
				//if n_status
				//if(current.isVisible(Variables.getNavigation()) || b_all)
				//int n_radius = PAS.get_pas().get_mapproperties().get_pixradius();
				double f_radius = 0.0002;
				Rectangle.Double rect = new Rectangle.Double(current.get_lon() - f_radius,
											current.get_lat() - f_radius,
											f_radius*2,
											f_radius*2);
				/*boolean test = (current.get_lon() >= ll_search.get_lon()-f_radius &&
						current.get_lat() >= ll_search.get_lat()-f_radius &&
						current.get_lon() < ll_search.get_lon() + f_radius*2 &&
						current.get_lat() < ll_search.get_lat() + f_radius*2);*/
				boolean test = rect.contains(new Point.Double(ll_search.get_lon(), ll_search.get_lat()));
				if(b_all ||
					(!b_search_by_coor && current.isVisible(Variables.getNavigation())) ||
					(b_search_by_coor && test))
				{
					StatusItemObject obj_inhab;
					String sz_status;
					StatusCode obj_statuscode;
					for (int inhab = 0; inhab < current.get_inhabitantcount(); inhab++) {
						obj_inhab = (StatusItemObject) current
								.get_itemfromhouse(inhab);
						if (obj_inhab != null) {
							// search criterias by sending
							if (get_statuscodeframe().m_statuspanel.getFilter() != null) {
								if (obj_inhab.get_refno() != get_statuscodeframe().m_statuspanel
										.getFilter().get_refno())
									continue;
							}

							if (n_status == obj_inhab.get_status() || b_search_by_coor) {
								obj_statuscode = find_status(obj_inhab
										.get_status());
								if (obj_statuscode != null)
									sz_status = obj_statuscode.get_status();
								else
									sz_status = "N/A";
								try {
									data = new Object[] {
                                            obj_inhab.get_item(),
											sz_status,
											obj_inhab,
											obj_inhab.get_number(),
											obj_inhab.get_postaddr(),
											obj_inhab.get_postno(),
											obj_inhab.get_postarea(),
											TextFormat.format_time(obj_inhab
													.get_time(), 4) };
									PAS.get_pas().get_inhabitantframe()
											.get_panel().insert_row(data, -1);
								} catch (Exception e) {
									System.out.println(e.getMessage());
									e.printStackTrace();
									Error.getError().addError(
											"StatusController",
											"Exception in search_houses", e, 1);
								}
								n_count++;
							}
						}
					}
				}
			}
			// data = new Object[] { new Integer(0), "", "", n_count + "
			// record(s) found", "", "", "", "" };
			// PAS.get_pas().get_inhabitantframe().get_panel().insert_row(data,
			// 0);
			PAS.get_pas().get_drawthread().set_suspended(false);
		}
		PAS.get_pas().get_inhabitantframe().m_inhabitantpanel.popSelection();
	}

	public void search_houses(Dimension dim) {
		PAS.get_pas().get_inhabitantframe().set_visible(true);
		if (get_houses() != null) {
			Timeout time = new Timeout(1, 20);
			while (1 == 1) {
				if (get_houses().is_housesready())
					break;
				try {
					Thread.sleep(time.get_msec_interval());
				} catch (Exception e) {
					Error.getError().addError("StatusController",
							"Exception in search_houses", e, 1);
				}
				time.inc_timer();
				if (time.timer_exceeded())
					return;
			}
			PAS.get_pas().get_drawthread().set_suspended(true);
			HouseItem current;
			int n_radius;
			int n_count = 0;
			int n_total_inhabitants = 0;
			Object[] data;
			PAS.get_pas().get_inhabitantframe().get_panel().clear();
			for (int i = 0; i < get_houses().size(); i++) {
				current = (HouseItem) get_houses().get_houses().get(i);
				current.set_selected(false);
				if (current.get_screencoords() != null && current.get_visible()) {
					n_radius = PAS.get_pas().get_mapproperties()
							.get_pixradius();
					if (current.get_alert())
						n_radius += get_alertborder();
					if (dim.width >= (current.get_screencoords().width - n_radius)
							&& dim.width <= (current.get_screencoords().width + n_radius)
							&& dim.height >= (current.get_screencoords().height - n_radius)
							&& dim.height <= (current.get_screencoords().height + n_radius)) { // user
																								// click
																								// inside
																								// house-area
						StatusItemObject obj_inhab;
						String sz_status;
						StatusCode obj_statuscode;
						for (int inhab = 0; inhab < current
								.get_inhabitantcount(); inhab++) {
							obj_inhab = (StatusItemObject) current
									.get_itemfromhouse(inhab);
							obj_statuscode = find_status(obj_inhab.get_status());
							if (obj_statuscode != null)
								sz_status = obj_statuscode.get_status();
							else
								sz_status = "N/A";
							data = new Object[] {
                                    obj_inhab.get_item(),
									sz_status,
									obj_inhab,
									obj_inhab.get_number(),
									obj_inhab.get_postaddr(),
									obj_inhab.get_postno(),
									obj_inhab.get_postarea(),
									TextFormat.format_time(
											obj_inhab.get_time(), 4) };
							PAS.get_pas().get_inhabitantframe().get_panel()
									.insert_row(data, -1);
							n_count++;
						}
						if (!current.get_isselected())
							current.set_selected(true);
					}
				}
			}
			try {
				// data = new Object[] { "", "", "", n_count + " record(s)
				// found", "", "", "" };
				// PAS.get_pas().get_inhabitantframe().get_panel().insert_row(data,
				// 0);
			} catch (Exception e) {
				Error.getError().addError("StatusController",
						"Exception in search_houses", e, 1);
			}
			PAS.get_pas().get_drawthread().set_suspended(false);
		}
	}

	public void calcHouseCoords() {
		if (get_houses() == null)
			return;
		get_houses().calcHouseCoords();
	}

	public void drawItem(StatusItemObject cur, Graphics gfx) {
		gfx.setColor(Color.red); // new Color((float)1.0, (float)0.0,
									// (float)0.0, (float)0.5));
		int n_size = PAS.get_pas().get_mapproperties().get_pixradius();
		gfx.fillOval(cur.get_dim_screencoor().width,
				cur.get_dim_screencoor().height, n_size, n_size);
	}

	/*
	 * public XMLStatusList get_xmlstatuslist() { if(m_xmlstatuslist==null)
	 * m_xmlstatuslist = new XMLStatusList(m_statuslistframe); //PAS.get_pas());
	 * return m_xmlstatuslist; } public XMLStatusItems get_xmlstatusitems() {
	 * if(m_xmlstatusitems==null) m_xmlstatusitems = new
	 * XMLStatusItems(PAS.get_pas(), this); return m_xmlstatusitems; }
	 */

	public void open_statuslistframe() {
		if (m_statuslistframe == null)
			m_statuslistframe = new OpenStatusFrame();
		m_statuslistframe.open(); 
	}

	public boolean retrieve_statuslist(JFrame parent_frame) {
		// get_xmlstatuslist().start(/*"PAS_getstatuslist_zipped.asp?"*/"",
		// true); //, parent_frame); //l_companypk=" +
		// PAS.get_pas().get_userinfo().get_comppk()
		new WSGetStatusList(m_statuslistframe).start();

		return true;
	}

	boolean b_newrefno = false;

	public boolean retrieve_statusitems(JFrame parent_frame,
			final String sz_projectpk, final int n_refno, boolean b_init) {
		setOpen();
		if (b_init) {
			new Thread("StatusController thread") {
				public void run() {
					try {
						{
							//PAS.get_pas().close_active_project(true, false);
							b_newrefno = true;
							/*
							 * ActionEvent e = new ActionEvent(new String(""),
							 * ActionEvent.ACTION_PERFORMED, "act_init_status");
							 * actionPerformed(e);
							 */
							reset_filters();
							set_current_refno(n_refno);
							set_current_projectpk(sz_projectpk);
							if (sz_projectpk.length() > 0
									&& !sz_projectpk.equals("-1")) {
								set_current_refno(-1);
							}
							set_current_projectpk(sz_projectpk);
							PAS.get_pas().get_eastcontent().reloadStatusPanel(
									false);
							get_statuscodeframe().clear();
							init_arrays();
						}
						start_download(false);
					} catch (Exception e) {

					}
				}
			}.start();
			return true;
		}
		start_download(false);
		return true;
	}

	public boolean exec_statusitems(JFrame parent_frame, boolean b_auto) {
		// get_xmlstatusitems().start("PAS_getstatusitems_zipped.asp?l_projectpk="
		// + get_current_projectpk() + "&l_refno=" + get_current_refno() +
		// "&n_item_filter=" + get_item_filter() + "&n_date_filter=" +
		// get_date_filter() + "&n_time_filter=" + get_time_filter(), (b_auto ?
		// null : parent_frame), get_current_projectpk());
		long[] refno_list = new long[get_sendinglist().size()];
		for (int i = 0; i < get_sendinglist().size(); i++) {
			refno_list[i] = get_sendinglist().get(i).get_refno();
		}
		// get_xmlstatusitems().start(new Long(get_current_projectpk()),
		// get_date_filter(), get_time_filter(), refno_list, b_auto);
		try {
			new WSGetStatusItems(this, new Long(get_current_projectpk()),
					get_date_filter(), get_time_filter(), refno_list, b_auto)
					.start();
		} catch (Exception e) {

		}
		return true;
	}

	public synchronized void start_download(boolean b_auto) {
		
		if(isClosed())
			return;
		set_updates_in_progress(true);
		PAS.get_pas().get_eastcontent().get_statuspanel().setStatusUpdating(true);
		exec_statusitems(get_statusframe(), b_auto);
	}

	public void export_status() {
		boolean exported = false;
		if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights()==4 && m_lba_total_tabbed != null && m_lba_total_tabbed.getListCC() != null && m_lba_total_tabbed.getListCC().get_tablelist().getRowCount()>0) {
			m_lba_total_tabbed.getListCC().exportToCSV();
			exported=true;
		}
		else {
			if(m_statuscodes.get_total() > 0) {
			new no.ums.pas.status.StatusExport(PAS.get_pas().get_current_project()).show(
                    PAS .get_pas(), PAS.get_pas().get_lookandfeel(), (StatusItemList) m_items, m_statuscodes);
			exported=true;
			}
			// Denne har en viss sammenheng med StatusExport()._export_separator
			if((PAS.get_pas().get_eastcontent().get_statuspanel().get_combo_filter().getSelectedIndex() == 0 || 
					((StatusSending)((ComboRow)PAS.get_pas().get_eastcontent().get_statuspanel().get_combo_filter().getSelectedItem()).getId()).get_type() == 4) 
					&& m_lba_total_tabbed != null && m_lba_total_tabbed.getListCC() != null && m_lba_total_tabbed.getListCC().get_tablelist().getRowCount()>0) {
				m_lba_total_tabbed.getListCC().exportToCSV();
				exported=true;
			}
			
		}
		if(!exported) {
			JFrame frame = PopupDialog.get_frame();
			JOptionPane.showMessageDialog(frame,Localization.l("main_status_export_no_records"));
			frame.dispose();
		}
	}
	
	public void trainingModeChanged()
	{
		
	}

}