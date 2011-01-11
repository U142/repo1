package no.ums.pas.parm.xml;

import java.util.Comparator;

import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.parm.voobjects.ObjectVO;


public class ListSorter implements Comparator {

	public int compare(Object arg0, Object arg1) {
		int returnValue = 0;
		
		if(arg0.getClass().equals(ObjectVO.class) && arg1.getClass().equals(ObjectVO.class))
			returnValue = 0;
		else if(arg0.getClass().equals(ObjectVO.class) && arg1.getClass().equals(EventVO.class))
			returnValue = 1;
		else if(arg0.getClass().equals(ObjectVO.class) && arg1.getClass().equals(AlertVO.class))
			returnValue = 1;
		else if(arg0.getClass().equals(EventVO.class) && arg1.getClass().equals(ObjectVO.class))
			returnValue = -1;
		else if(arg0.getClass().equals(EventVO.class) && arg1.getClass().equals(EventVO.class))
			returnValue = 0;
		else if(arg0.getClass().equals(EventVO.class) && arg1.getClass().equals(AlertVO.class))
			returnValue = 1;
		else if(arg0.getClass().equals(AlertVO.class) && arg1.getClass().equals(ObjectVO.class))
			returnValue = 1;
		else if(arg0.getClass().equals(AlertVO.class) && arg1.getClass().equals(EventVO.class))
			returnValue = 1;
		else if(arg0.getClass().equals(AlertVO.class) && arg1.getClass().equals(AlertVO.class))
			returnValue = 0;
		
		return returnValue;
	}

}
