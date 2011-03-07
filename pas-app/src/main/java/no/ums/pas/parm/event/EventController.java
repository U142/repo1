package no.ums.pas.parm.event;


import no.ums.pas.PAS;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.maps.defines.UnknownShape;
import no.ums.pas.parm.main.MainController;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.CategoryVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.parm.voobjects.ObjectVO;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class EventController implements ActionListener {

	private int tempPk;
	private EventGUI gui;
	private EventVO event;
	private ObjectVO object;
	private CategoryVO category;
	private boolean toObjectList;
	private UnknownShape us = null;
	public UnknownShape getShape() { return us; }

	public EventController() {
		gui = null;
		event = null;
		object = null;
	}

	public void createEvent(ObjectVO object, int highestTempPk,
			HashMap<Long, CategoryVO> categoryList) {
		this.event = null;
		this.tempPk = highestTempPk;
		this.object = object;
        gui = new EventGUI(Localization.l("main_parm_event_new_dlg_heading"));
		gui.getEventInputPanel().getBtnEpicentre().addActionListener(this);
		this.addCategoryToCombobox(categoryList);
		// Litt juks for å sette active_shape til null ;)
		//PAS.get_pas().get_mappane().set_active_shape(null);
		//PAS.get_pas().get_parmcontroller().setDrawMode(new UnknownShape());
	}

	public void editEvent(EventVO event, HashMap<Long, CategoryVO> categoryList, DefaultMutableTreeNode eventNode) {
		this.event = event;
        gui = new EventGUI(Localization.l("main_parm_event_edit"));
		this.object = (ObjectVO) eventNode.getUserObject();
		this.addCategoryToCombobox(categoryList);

		if (!this.event.getCategorypk().equals("c-1")) {
			gui.getEventInputPanel().getCbxCategory().setSelectedItem(
					event.getCatVO());
		}
		gui.getEventInputPanel().getTxtName().setText(event.getName());
		gui.getEventInputPanel().getTxaDesc().setText(event.getDescription());
		gui.getEventInputPanel().getBtnEpicentre().addActionListener(this);
		
		if(PAS.get_pas().get_rightsmanagement().write_parm())
			gui.getActionPanel().getBtnSave().setEnabled(true);
		else
			gui.getActionPanel().getBtnSave().setEnabled(false);
		
	}

	public boolean deleteEvent(EventVO event, DefaultMutableTreeNode eventNode) {
		System.out.println("object" + this.object);
		this.event = event;
        Object[] options = {Localization.l("common_yes"), Localization.l("common_cancel")};

		String msg = null;
		if (hasEventChildren(this.event)) {
            msg = Localization.l("common_delete_are_you_sure") + " '"
					+ this.event.getName() + "'?" +
					"\n" + Localization.l("common_subnodes") + " " + Localization.l("common_will_be_deleted");
					
					//"?\nAll alerts in "
					//+ this.event.getName() + " will be deleted!";
		} else {
            msg = Localization.l("common_delete_are_you_sure")
					+ this.event.getName() + "?";
		}

        int n = JOptionPane.showOptionDialog(null, msg, Localization.l("common_delete") + " " + Localization.l("main_parmtab_popup_event"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
				options, options[1]);
		// yes = 0, no = 1
		if (n == 0) {
			this.event.setOperation("delete");
			this.object = (ObjectVO) eventNode.getUserObject();
			this.object.removeEvents(this.event);
			if (this.event.getAlertListe() != null && this.event.getAlertListe().size() > 0) {
				deleteChildren(this.event.getAlertListe());
			}
			return true;
		} else {
			return false;
		}
	}

	private void deleteChildren(ArrayList<Object> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getClass().equals(AlertVO.class)) {
				AlertVO a = (AlertVO) list.get(i);
				a.setOperation("delete");
			}
		}
	}

	private boolean hasEventChildren(EventVO e) {
		boolean hasChildren = false;
			if (e.getAlertListe() != null && e.getAlertListe().size() > 0) {
				hasChildren = true;
		}
		return hasChildren;
	}

	private void addCategoryToCombobox(HashMap<Long, CategoryVO> cList) {
		Set<Long> keyset = cList.keySet();
		TreeSet<CategoryVO> items = new TreeSet<CategoryVO>(cList.values());
		//Iterator it = keyset.iterator();
		Iterator it = items.iterator();
		
		while (it.hasNext()) {
			this.category = (CategoryVO)it.next();
			gui.getEventInputPanel().getCbxCategory().addItem(this.category);
		}
	}

	public EventVO storeEvent() {
		boolean store = true;
		
		
		if (gui.getEventInputPanel().getTxtName().getText().length() <= 0) {
			this.toObjectList = false;
			store = false;
		}
		
		if(store) {
			if(this.event == null)
				this.toObjectList = true;
			else
				this.toObjectList = false;
			// Her må jeg sjekke om det er en ny eller edit event, men i tillegg
			// så kan det være en edit før den blir satt inn og da skal det fortsatt være insert 
			if (this.event == null) {
				this.event = new EventVO();
				this.event.setOperation("insert");
				event.setParentpk(object.getObjectPK());
				this.event.setEventPk(Integer.toString(this.tempPk));
			} else if(this.event.getEventPk().contains("e")) {
				this.event.setOperation("update");
			}
			if (gui.getEventInputPanel().getTxtName().getText().length() <= 0) {
				gui.getEventInputPanel().getTxtName().setText(this.event.getName());
				this.toObjectList = false;
			}
	
			this.event.setName(gui.getEventInputPanel().getTxtName().getText());
			this.event.setDescription(gui.getEventInputPanel().getTxaDesc()
					.getText());
			if (this.getCategory().getName().equals("-- Select category --")) {
				this.event.setCatVO(null);
				this.event.setCategorypk("c-1");
			} else {
				this.event.setCatVO(this.getCategory());
				this.event.setCategorypk(this.getCategory().getCategoryPK());
			}
			if(PAS.get_pas().get_mappane().get_active_shape() != null) {
				if(PAS.get_pas().get_mappane().get_active_shape().get_epicentre()!=null) {
					this.event.setEpicentreX(PAS.get_pas().get_mappane().get_active_shape().get_epicentre().get_lon());
					this.event.setEpicentreY(PAS.get_pas().get_mappane().get_active_shape().get_epicentre().get_lat());
				}
				PAS.get_pas().get_parmcontroller().addShapeToDrawQueue(PAS.get_pas().get_mappane().get_active_shape());
			}
			else
				System.out.println("Epicentre == null");
			/*if(existInObjectList(this.event) == false) {
				this.object.addEvents(this.event);
			}*/
		}
		return this.event;
	}
	
	public boolean existInObjectList(EventVO e) {
		boolean b = false;
		if (this.object != null && this.object.getList() != null) {
			for (int i = 0; i < this.object.getList().size(); i++) {
				if (this.object.getList().getClass().equals(EventVO.class)) {
					EventVO temp = (EventVO) this.object.getList().get(i);
					if (temp.getEventPk() == e.getEventPk()) {
						this.object.getList().set(i, e);
						b = true;
					}
				}
			}
		}
		return b;
	}

	public void insertEvent(MainController main) {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) main.getTreeCtrl().getSelPath().getLastPathComponent();
		
		//Object object = main.getObjectFromTree();
		Object object = parent.getUserObject();
		if (main.checkObject(object, ObjectVO.class)) {
			this.object = (ObjectVO) object;
		}
		main.tempPK++;
		//try {
			createEvent(this.object, main.tempPK, main.m_categories); // main.getAllCategorys()
		/*} catch (FileNotFoundException e) {
			e.printStackTrace();
			Error.getError().addError("EventController","FileNotFoundException in insertEvent",e,1);
		} catch (ParmException e) {
			e.printStackTrace();
			Error.getError().addError("EventController","ParmException in insertEvent",e,1);
		}*/
		main.activateEventBtnListener();
	}

	public CategoryVO getCategory() {
		CategoryVO category = (CategoryVO) gui.getEventInputPanel()
				.getCbxCategory().getSelectedItem();
		return category;
	}

	public EventGUI getGui() {
		return gui;
	}

	public EventVO getEvent() {
		return event;
	}

	public boolean isToObjectList() {
		return toObjectList;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == gui.getEventInputPanel().getBtnEpicentre()) {
			//PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_ASSIGN_EPICENTRE);
			ArrayList<ShapeStruct> shapelist = PAS.get_pas().get_parmcontroller().get_shapelist();
			for(int i=0;i<shapelist.size();i++) {
				if(shapelist.get(i) != null)
					if((shapelist.get(i)).getClass().equals(UnknownShape.class))
						us = (UnknownShape)shapelist.remove(i);
			}
			if(us != null)
				PAS.get_pas().get_parmcontroller().setDrawMode(us);
			else
				PAS.get_pas().get_parmcontroller().setDrawMode(new UnknownShape());
		}
			
	}
}
