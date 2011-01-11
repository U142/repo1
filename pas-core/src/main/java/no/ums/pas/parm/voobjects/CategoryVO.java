package no.ums.pas.parm.voobjects;

public class CategoryVO implements Comparable<CategoryVO>{
	
	private String categoryPK;
	private String name;
	private String description;
	private String fileext;
	private String timestamp;
	
	public CategoryVO(String categoryPK, String name, String description, String fileext, String timestamp) {
		super();
		this.categoryPK = categoryPK;
		this.name = name;
		this.description = description;
		this.fileext = fileext;
		this.timestamp = timestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileext() {
		return fileext;
	}

	public void setFileext(String fileext) {
		this.fileext = fileext;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getCategoryPK() {
		return categoryPK;
	}
	public boolean equals(Object o){
		CategoryVO co = (CategoryVO)o;
		if(categoryPK.compareTo(co.getCategoryPK()) == 0){
			return true;
		}
		else
			return false;
	}
	public String toString(){
		return name;
	}

	@Override
	public int compareTo(CategoryVO o) {
		/*long local_PK = Long.parseLong(categoryPK.substring(1));
		long external_PK = Long.parseLong(o.categoryPK.substring(1));
		if(local_PK>external_PK)
			return 1;
		else if(local_PK<external_PK)
			return -1;
		else
			return 0;*/
		String local_name = name.toUpperCase();
		String external_name = o.name.toUpperCase();
		int comp = local_name.compareTo(external_name);
		if(comp > 0)
			return 1;
		else if(comp < 0)
			return -1;
		else
			return comp;
	}
}

