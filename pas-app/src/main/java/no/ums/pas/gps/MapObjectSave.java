package no.ums.pas.gps;

import no.ums.pas.maps.defines.MapObject;

public class MapObjectSave {
	//HttpPostForm m_form;
	MapObjectReg m_reg;
	MapObject m_obj;
	private MapObject get_obj() { return m_obj; }
	protected MapObjectReg get_reg() { return m_reg; }
	public MapObjectSave(MapObjectReg reg) {
		m_reg = reg;
		m_obj = m_reg.get_mapobject();
		save();
	}
	private boolean save() {
		/*try {
			m_form = new HttpPostForm(PAS.get_pas().get_sitename() + "/PAS_mapobject_save.asp");
			m_form.setParameter("l_objectpk", get_obj().get_objectpk());
			m_form.setParameter("f_dynamic", (get_obj().get_dynamic() ? "1" : "0"));
			m_form.setParameter("sz_name", get_obj().get_name());
			m_form.setParameter("l_comppk", new Integer(PAS.get_pas().get_userinfo().get_comppk()).toString());
			m_form.setParameter("l_deptpk", new Integer(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()).toString());
			m_form.setParameter("l_lon", new Double(get_obj().get_lon()).toString());
			m_form.setParameter("l_lat", new Double(get_obj().get_lat()).toString());
			m_form.setParameter("l_picturepk", get_obj().get_picturepk());
			m_form.setParameter("l_resourcepk", get_obj().get_resourcepk());
			m_form.setParameter("l_unitpk", get_obj().get_unitpk());
			m_form.setParameter("l_timeinterval", new Integer(get_obj().get_timeinterval()).toString());
			m_form.setParameter("l_moveinterval", new Integer(get_obj().get_moveinterval()).toString());
			m_form.setParameter("l_gsmno", get_obj().get_gsmno());
			m_form.setParameter("l_gsmno2", get_obj().get_gsmno2());
			m_form.setParameter("l_manufacturer", new Integer(get_obj().get_manufacturer()).toString());
			m_form.setParameter("l_usertype", new Integer(get_obj().get_usertype()).toString());
			m_form.post();
			
		} catch(java.io.IOException e) {
			PAS.get_pas().add_event("form.post() ERROR: " + e.getMessage(), e);
			Error.getError().addError("MapObjectSave","IOException in post_form",e,1);
			return false;
		} catch(Exception e) {
			PAS.get_pas().add_event("save() ERROR: " + e.getMessage(), e);
			Error.getError().addError("MapObjectSave","Exception in post_form",e,1);
			return false;
		}
		PAS.get_pas().add_event("Saved", null);*/
		return true;
	}
}