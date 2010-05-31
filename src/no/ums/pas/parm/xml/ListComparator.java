package no.ums.pas.parm.xml;

import java.util.*;

import no.ums.pas.parm.voobjects.*;


class ListComparator implements Comparator{
	ArrayList<Object> liste = null;
	
	public ListComparator(){
		
	}
	
	public int compare(Object a, Object b){
		//Sjekk the type of object!
		if(a.getClass().equals(ObjectVO.class)){
			ObjectVO o1 = (ObjectVO)a;
			ObjectVO o2 = (ObjectVO)b;
			long tall1;
			long tall2;
			try{
				//Dersom det er en temporærpk som ikke inneholder bokstav forann
				tall1 = Long.parseLong(o1.getObjectPK());
			}catch(Exception e){
				//Dersom pk inneholder en bokstav forann
				tall1 = Long.parseLong(o1.getObjectPK().substring(1));
				//Error.getError().addError("ListComperator","Exception in compare",e,1);
			}
			try{
				tall2 = Long.parseLong(o2.getObjectPK());
			}catch(Exception e){
				tall2 = Long.parseLong(o2.getObjectPK().substring(1));
				//Error.getError().addError("ListComperator","Exception in compare",e,1);
			}
			if(tall1<tall2)
				return -1;
			else if(tall1==tall2)
				return 0;
			else
				return 1;
		}else if(a.getClass().equals(EventVO.class)){
			EventVO e1 = (EventVO)a;
			EventVO e2 = (EventVO)b;
			long tall1;
			long tall2;
			try{
				tall1 = Long.parseLong(e1.getEventPk());
			}catch(Exception e){
				tall1 = Long.parseLong(e1.getEventPk().substring(1));	
//				Error.getError().addError("ListComperator","Exception in compare",e,1);
			}
			try{
				tall2 = Long.parseLong(e2.getEventPk());
			}catch(Exception e){
				tall2 = Long.parseLong(e2.getEventPk().substring(1));
//				Error.getError().addError("ListComperator","Exception in compare",e,1);
			}
			
			if(tall1<tall2)
				return -1;
			else if(tall1==tall2)
				return 0;
			else
				return 1;
		}else{
			AlertVO a1 = (AlertVO)a;
			AlertVO a2 = (AlertVO)b;
			long tall1;
			long tall2;
			try{
				tall1 = Long.parseLong(a1.getAlertpk());
			}catch(Exception e){
				tall1 = Long.parseLong(a1.getAlertpk().substring(1));
//				Error.getError().addError("ListComperator","Exception in compare",e,1);
			}
			try{
				tall2 = Long.parseLong(a2.getAlertpk());
			}catch(Exception e){
				tall2 = Long.parseLong(a2.getAlertpk().substring(1));
//				Error.getError().addError("ListComperator","Exception in compare",e,1);
			}
			if(tall1<tall2)
				return -1;
			else if(tall1==tall2)
				return 0;
			else
				return 1;
		}
	}
}
