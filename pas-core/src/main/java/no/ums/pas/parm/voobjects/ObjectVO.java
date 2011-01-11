package no.ums.pas.parm.voobjects;

import no.ums.pas.maps.defines.PolygonStruct;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;


public class ObjectVO extends ParmVO {

	private String objectPK;
	private String deptPK;
	private String importPK;
	private String name;
	private String description;
	private CategoryVO categoryVO;
	private String categoryPK;
	private String parent;
	private String address;
	private String postno;
	private String place;
	private String phone;
	private String metadata;
	private String timestamp;
	private boolean isObjectFolder;
	private ArrayList<Object> list;
	private String tempPk;

	public String getTempPk() {
		return tempPk;
	}
	
	public String getPk() { return objectPK; }
	
	public static final DataFlavor TREE_PATH_FLAVOR_OBJECT = new DataFlavor(ObjectVO.class, "Tree Path");
	public static final DataFlavor TREE_PATH_FLAVOR_EVENT	 = new DataFlavor(EventVO.class, "Event");
	DataFlavor flavors[] = { TREE_PATH_FLAVOR_OBJECT, TREE_PATH_FLAVOR_EVENT};

	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		//return (flavor.getRepresentationClass() == ObjectVO.class);
		/*for(int i=0; i < flavors.length; i++) {
			if(flavors[i].getRepresentationClass().equals(flavor.getDefaultRepresentationClass().getClass()))
				return true;
		}*/
		
		return (flavor.match(TREE_PATH_FLAVOR_OBJECT));
	}


	public void setTempPk(String tempPk) {
		this.tempPk = tempPk;
	}

	public ObjectVO(String objectPK, String deptPK, String importPK, String name,
			String description, CategoryVO categoryVO, String categoryPK,
			String parent, String address, String postno, String place,
			String phone, String metadata, String timestamp,
			boolean isObjectFolder, ArrayList<Object> list) {
		super();
		this.objectPK = objectPK;
		this.deptPK = deptPK;
		this.importPK = importPK;
		this.name = name;
		this.description = description;
		this.categoryVO = categoryVO;
		this.categoryPK = categoryPK;
		this.parent = parent;
		this.address = address;
		this.postno = postno;
		this.place = place;
		this.phone = phone;
		this.metadata = metadata;
		this.timestamp = timestamp;
		this.isObjectFolder = isObjectFolder;
		this.list = list;
	}

	public ObjectVO(String objectPK, String name, String description, String parent, String address, String postno, String place, String phone, String metadata, String timestamp, boolean isObjectFolder) {
		super();
		this.objectPK = objectPK;
		this.name = name;
		this.description = description;
		this.parent = parent;
		this.address = address;
		this.postno = postno;
		this.place = place;
		this.phone = phone;
		this.metadata = metadata;
		this.timestamp = timestamp;
		this.isObjectFolder = isObjectFolder;
		this.list = new ArrayList<Object>();
		this.m_shape = null;
	}

	public ObjectVO(String name, CategoryVO categoryVO, String address,
			String postno, String place, String phone, String description) {
		super();
		this.name = name;
		this.description = description;
		this.categoryVO = categoryVO;
		this.address = address;
		this.postno = postno;
		this.place = place;
		this.phone = phone;
		this.timestamp = "";
		this.metadata = "";
		this.categoryPK = "-1";
		this.objectPK = "-1";
		this.deptPK = "-1";
		this.importPK = "-1";
		this.parent = "-1";
		this.isObjectFolder = false;
		this.list = new ArrayList<Object>();
		this.m_shape = null;
	}

	public ObjectVO(String objectPK, String deptPK, String importPK, String name,
			String description, String categoryPK, String parent, String address,
			String postno, String place, String phone, String metadata,
			String timestamp, boolean isObjectFolder) {
		super();
		this.objectPK = objectPK;
		this.deptPK = deptPK;
		this.importPK = importPK;
		this.name = name;
		this.description = description;
		this.categoryPK = categoryPK;
		this.parent = parent;
		this.address = address;
		this.postno = postno;
		this.place = place;
		this.phone = phone;
		this.metadata = metadata;
		this.timestamp = timestamp;
		this.isObjectFolder = isObjectFolder;
		this.categoryVO = null;
		this.list = new ArrayList<Object>();
		this.m_shape = null;
	}
	public ObjectVO(String name, String description, String categoryID){
		this.name = name;
		this.description = description;
		this.categoryPK = categoryID;
		this.list = new ArrayList<Object>();
	}

	public ObjectVO() {
		super();
		this.objectPK = "-1";
		this.deptPK = "-1";
		this.importPK = "-1";
		this.categoryPK = "-1";
		this.name = "";
		this.description = "";
		this.categoryVO = null;
		this.parent = "-1";
		this.address = "";
		this.postno = "";
		this.place = "";
		this.phone = "";
		this.metadata = "";
		this.timestamp = "";
		this.isObjectFolder = false;
		this.list = new ArrayList<Object>();
		this.m_shape = null;
	}
	
	public void submitPolygon(PolygonStruct p) {
		m_shape = p;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCategoryPK() {
		return categoryPK;
	}

	public void setCategoryPK(String categoryPK) {
		this.categoryPK = categoryPK;
	}

	public CategoryVO getCategoryVO() {
		return categoryVO;
	}

	public void setCategoryVO(CategoryVO categoryVO) {
		this.categoryVO = categoryVO;
	}

	public String getDeptPK() {
		return deptPK;
	}

	public void setDeptPK(String deptPK) {
		this.deptPK = deptPK;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImportPK() {
		return importPK;
	}

	public void setImportPK(String importPK) {
		this.importPK = importPK;
	}

	public boolean isObjectFolder() {
		return isObjectFolder;
	}

	public void setObjectFolder(boolean isObjectFolder) {
		this.isObjectFolder = isObjectFolder;
	}

	public ArrayList<Object> getList() {
		return list;
	}

	public void setList(ArrayList<Object> list) {
		this.list = list;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObjectPK() {
		return objectPK;
	}

	public void setObjectPK(String objectPK) {
		this.objectPK = objectPK;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getPostno() {
		return postno;
	}

	public void setPostno(String postno) {
		this.postno = postno;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectVO objectVO = (ObjectVO) o;

        return !(objectPK != null ? !objectPK.equals(objectVO.objectPK) : objectVO.objectPK != null);

    }

    @Override
    public int hashCode() {
        return objectPK != null ? objectPK.hashCode() : 0;
    }

    public void addObjects(ObjectVO oo){
		if(!list.contains(oo))
			list.add(oo);
		else
			System.out.println("Object already exists!");
	}
	
	public void removeObjects(ObjectVO oo){
		if(list.contains(oo))
			list.remove(oo);
		else
			System.out.println("Object does not exist!");
	}

	public void addEvents(EventVO eo){
		if(!list.contains(eo))
			list.add(eo);
		else
			System.out.println("Event already exists!");
	}
	public void removeEvents(EventVO eo){
		if(list.contains(eo))
			list.remove(eo);
		else
			System.out.println("Event does not exist!");
	}
	
	public String toString() {
		return this.name;
	}

	@Override
	public boolean hasValidPk() {
		if(getPk().startsWith("o"))
			return true;
		return false;
	}
}
