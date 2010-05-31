package no.ums.pas.parm.map;

public class MapController {
	
	private MapPanel m_map;
	
	public MapController(String[] args) {
		String sz_sitename = null;
		for(int i=0; i < args.length; i++) {
			if(args[i].charAt(0)=='-') {
				switch(args[i].charAt(1)) {
					case 's':
						sz_sitename = args[i].substring(2);
						break;
				}
			}
		}
		sz_sitename = "http://vb4utv/";
		System.out.println("Using site: " + sz_sitename);
		m_map = new MapPanel(sz_sitename);
		m_map.exec();
	}
	
	public MapPanel getMapPanel(){
		return m_map;
	}
}
