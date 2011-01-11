package no.ums.pas.core.events;

import no.ums.pas.PAS;

public class EventChangeArrowsize extends EventClass {
	public EventChangeArrowsize(PAS pas) {
		super(pas);
	}
	public void doEvent(Object value) {
		get_pas().get_gpscontroller().set_arrowsize(((Integer)value).intValue());
		get_pas().kickRepaint();
	}
}