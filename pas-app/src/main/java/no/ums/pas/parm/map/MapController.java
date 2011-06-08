package no.ums.pas.parm.map;

import no.ums.log.Log;
import no.ums.log.UmsLog;

public class MapController {

    private static final Log log = UmsLog.getLogger(MapController.class);

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
		log.debug("Using site: " + sz_sitename);
		m_map = new MapPanel(sz_sitename);
		m_map.exec();
	}
	
	public MapPanel getMapPanel(){
		return m_map;
	}
}
