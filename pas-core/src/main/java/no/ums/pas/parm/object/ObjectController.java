package no.ums.pas.parm.object;

import no.ums.pas.PAS;
import no.ums.pas.ParmController;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.parm.main.MainController;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.CategoryVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.parm.voobjects.ObjectVO;
import no.ums.pas.ums.errorhandling.Error;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class ObjectController {

	private ObjectGUI gui;
	private CategoryVO category;
	private ObjectVO object, objectParent;
	private int tempPk;
	private boolean isObjectFolder, toObjectList;
	private boolean objFolder, obj, evt, alt;
	private boolean children[] = { objFolder, obj, evt, alt };
	private PolygonStruct m_edit_polygon = null;
	private MainController main = null;
	private Navigation nav = null;
	
	public ObjectController(MainController main, Navigation nav){
		this.nav = nav;
		this.main = main;
	}
	
	public void createNewObject(int highestTempPk, HashMap<Long, CategoryVO> categoryList,
			ObjectVO objectParent, boolean isObjectFolder) throws ParmException {
		this.object = null;
		this.tempPk = highestTempPk;
		this.objectParent = objectParent;
		this.isObjectFolder = isObjectFolder;
		this.m_edit_polygon = new PolygonStruct(nav.getDimension());
		
		if (this.isObjectFolder == true) {
			this.gui = new ObjectGUI(PAS.l("main_parm_objectfolder_new_dlg_heading"));
		} else {
			this.gui = new ObjectGUI(PAS.l("main_parm_object_new_dlg_heading"));
		}
		addCategoryToCombobox(categoryList);
		
		main.setDrawMode(m_edit_polygon);
	}
	

	public void editObject(ObjectVO object, HashMap<Long, CategoryVO> categoryList, MainController main) throws ParmException {
		this.object = object;
		
		m_edit_polygon = null;
		
		if (this.object.isObjectFolder() == true) {
			this.gui = new ObjectGUI(PAS.l("main_parm_objectfolder_edit_dlg_heading"));
		} else {
			this.gui = new ObjectGUI(PAS.l("main_parm_object_edit_dlg_heading"));
		}
		addCategoryToCombobox(categoryList);

		if (!this.object.getCategoryPK().equals("c-1")) {
			gui.getObjInfoPanel().getCbxCategory().setSelectedItem(
					this.object.getCategoryVO());
		}
		gui.getObjInfoPanel().getTxtName().setText(this.object.getName());
		gui.getObjInfoPanel().getTxtAdress().setText(this.object.getAddress());
		gui.getObjInfoPanel().getTxtPostno().setText(this.object.getPostno());
		gui.getObjInfoPanel().getTxtPlace().setText(this.object.getPlace());
		gui.getObjInfoPanel().getTxtPhone().setText(this.object.getPhone());
		gui.getDescriptionPanel().getTxaDescription().setText(
				this.object.getDescription());
			
		// Dette er for å gå over til edit mode på polygonen
		// Må vi kanskje ha en service locator for å få kartet inn her? Tror jeg får tak i feil map.
		if(object.getM_shape()==null)
			m_edit_polygon = (new PolygonStruct(main.getMapNavigation().getDimension()));
		else {
			try {
				m_edit_polygon = (PolygonStruct)object.getM_shape().clone();
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("ObjectController","Exception in editObject",e,1);
			}
		}
		
		main.mapClear();
		main.setDrawMode(m_edit_polygon);
		
		//NB!, må endres, temp løsning
		if(gui.getObjInfoPanel().getTxtPostno().getText().equals(" ")) {
			gui.getObjInfoPanel().getTxtPostno().setText("");
		}
		// Må sjekke om brukeren har rettigheter til å lagre
		if(PAS.get_pas().get_rightsmanagement().write_parm())
			gui.getActionPanel().getBtnSave().setEnabled(true);
		else
			gui.getActionPanel().getBtnSave().setEnabled(false);
				
	}

	public boolean deleteObject(ObjectVO object)
			throws ParmException {
		this.object = object;
		String type = null;

		if (this.object.isObjectFolder()) {
			type = PAS.l("main_parmtab_popup_objectfolder");
		} else {
			type = PAS.l("main_parmtab_popup_object");
		}

		Object[] options = { PAS.l("common_yes"), PAS.l("common_cancel") };

		boolean children[] = hasObjectChildren(this.object);

		String msg = PAS.l("common_delete_are_you_sure") + " '" + object.getName() + "'";
		if (children[0] == true || children[1] == true || children[2] == true
				|| children[3] == true)
			msg += "\n\n" + PAS.l("common_contains") + ":";
		if(children[0])
			msg += "\n" + PAS.l("main_parmtab_popup_objectfolder");
		if(children[1])
			msg += "\n" + PAS.l("main_parmtab_popup_object");
		if(children[2])
			msg += "\n" + PAS.l("main_parmtab_popup_event");
		if(children[3])
			msg += "\n" + PAS.l("main_parmtab_popup_alert");
		/*	msg += "\nWarning, this " + type + " also contains:";
		if (children[0] == true)
			msg += "\n* ObjectFolder";
		if (children[1] == true)
			msg += "\n* Object";
		if (children[2] == true)
			msg += "\n* Event";
		if (children[3] == true)
			msg += "\n* Alert";*/

		int n = JOptionPane.showOptionDialog(null, msg, PAS.l("common_delete") + " " + type,
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
				options, options[1]);
		// yes = 0, no = 1
		if (n == 0) {
			this.object.setOperation("delete");
			if (this.object.getList().size() > 0) {
				deleteChildren(this.object.getList());
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean[] hasObjectChildren(ObjectVO o) {
		if (o.getList().size() > 0) {
			for (int i = 0; i < o.getList().size(); i++) {

				if (o.getList().get(i).getClass().equals(ObjectVO.class)) {
					ObjectVO ob = (ObjectVO) o.getList().get(i);
					if (ob.isObjectFolder()) {
						children[0] = true;
					} else {
						children[1] = true;
					}
					hasObjectChildren(ob);
//					for (int j = 0; j < ob.getList().size(); j++) {
//						if (ob.isObjectFolder()) {
//							children[0] = true;
//						} else {
//							children[1] = true;
//						}
//						hasObjectChildren(ob);
//					}
				}
				if (o.getList().get(i).getClass().equals(EventVO.class)) {
					EventVO e = (EventVO) o.getList().get(i);
					if (e.getAlertListe().size() > 0) {
						children[3] = true;
					}
					children[2] = true;
				}
			}
		}
		return children;
	}

	public void deleteChildren(ArrayList<Object> list, MainController main) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getClass().equals(ObjectVO.class)) {
				ObjectVO o = (ObjectVO) list.get(i);
				o.setOperation("delete");
				main.executeObjectWS(o);
				if (o.getList().size() > 0) {
					deleteChildren(o.getList(), main);
				}
			}
			if (list.get(i).getClass().equals(EventVO.class)) {
				EventVO e = (EventVO) list.get(i);
				e.setOperation("delete");
				main.executeEventWS(e);
				if (e.getAlertListe().size() > 0) {
					deleteChildren(e.getAlertListe(), main);
				}
			}
			if (list.get(i).getClass().equals(AlertVO.class)) {
				AlertVO a = (AlertVO) list.get(i);
				a.setOperation("delete");
				main.executeAlertWS(a);
			}
		}
	}
	
	public void deleteChildren(ArrayList<Object> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getClass().equals(ObjectVO.class)) {
				ObjectVO o = (ObjectVO) list.get(i);
				o.setOperation("delete");
				if (o.getList().size() > 0) {
					deleteChildren(o.getList());
				}
			}
			if (list.get(i).getClass().equals(EventVO.class)) {
				EventVO e = (EventVO) list.get(i);
				e.setOperation("delete");
				if (e.getAlertListe().size() > 0) {
					deleteChildren(e.getAlertListe());
				}
			}
			if (list.get(i).getClass().equals(AlertVO.class)) {
				AlertVO a = (AlertVO) list.get(i);
				a.setOperation("delete");
			}
		}
	}

	private void addCategoryToCombobox(HashMap<Long, CategoryVO> cList) {
		try {
			Set<Long> keyset = cList.keySet();
			Iterator it = keyset.iterator();
			
			while (it.hasNext()) {
				this.category = cList.get(it.next());
				gui.getObjInfoPanel().getCbxCategory().addItem(this.category);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			Error.getError().addError("ObjectController","Exception in addCategoryToCombobox",e,1);
		}
	}
	
	public void storeObject(ObjectVO object, ObjectVO parent, MainController main) {
		this.object = object;
		this.main = main;
		this.objectParent = parent;
		storeObject(main);
	}

	public ObjectVO storeObject(MainController main) {
		boolean store = true;
		if(object == null)
			this.toObjectList = true;
		else
			this.toObjectList = false;
		// Her må jeg sjekke om det er en ny eller edit event, men i tillegg
		// så kan det være en edit før den blir satt inn og da skal det fortsatt være insert 
		if (gui.getObjInfoPanel().getTxtName().getText().length() <= 0) {
			this.toObjectList = false;
			store = false;
			new ParmException("Name is mandatory!");
		}	
		
		if(store) {
			if (object == null) {
				this.object = new ObjectVO();
				this.object.setOperation("insert");
				this.object.setObjectPK(Integer.toString(this.tempPk));
				if (objectParent != null) {
					this.object.setParent(this.objectParent.getObjectPK());
				} else {
					object.setParent("o-1");
				}
				this.object.setObjectFolder(this.isObjectFolder);
			} else if(this.object.getObjectPK().contains("o")) {
				this.object.setOperation("update");
			}
						
			this.object.setName(gui.getObjInfoPanel().getTxtName().getText());
			
			if (this.getCategory().getName() == "-- Select category --") {
				this.object.setCategoryVO(null);
				this.object.setCategoryPK("c-1");
			} else {
				this.object.setCategoryVO(this.getCategory());
				this.object.setCategoryPK(this.getCategory().getCategoryPK());
			}
			
			this.object.setDeptPK(Integer.toString(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()));
			this.object.setAddress(gui.getObjInfoPanel().getTxtAdress().getText());
			
			if(gui.getObjInfoPanel().getTxtPostno().getText().length()>0)
				this.object.setPostno(gui.getObjInfoPanel().getTxtPostno().getText());
			
			this.object.setPlace(gui.getObjInfoPanel().getTxtPlace().getText());
			this.object.setPhone(gui.getObjInfoPanel().getTxtPhone().getText());
			this.object.setDescription(gui.getDescriptionPanel().getTxaDescription().getText());
						
			if(m_edit_polygon != null)
				object.submitPolygon(m_edit_polygon);
		}
		return this.object;
	}

	public CategoryVO getCategory() {
		CategoryVO category = (CategoryVO) gui.getObjInfoPanel()
				.getCbxCategory().getSelectedItem();
		return category;
	}

	public void insertObjectFolder(MainController main) throws ParmException,
			FileNotFoundException {
		Object object = main.getObjectFromTree();
		if (object != null && main.checkObject(object, ObjectVO.class)) {
			this.object = (ObjectVO) object;
		}
		main.tempPK++;
		//main.getMap().clear();
		if (object == null) {
			createNewObject(main.tempPK, main.getAllCategorys(), null, true);
		} else {
			createNewObject(main.tempPK, main.getAllCategorys(), this.object, true);
		}
		main.activateObjectBtnListener();
	}

	public void insertObject(MainController main) throws ParmException,	FileNotFoundException {
		Object object = main.getObjectFromTree();
		if (object != null && main.checkObject(object, ObjectVO.class)) {
			this.object = (ObjectVO) object;
		}
		main.tempPK++;
		
		if (object == null) {
			createNewObject(main.tempPK, main.getAllCategorys(), null, false);
		} else {
			((ParmController)main).clearDrawQueue();
			createNewObject(main.tempPK, main.getAllCategorys(), this.object, false);
		}
		main.activateObjectBtnListener();
	}

	public ObjectGUI getGui() {
		return gui;
	}

	public ObjectVO getObject() {
		return object;
	}

	public boolean isToObjectList() {
		return toObjectList;
	}
}
