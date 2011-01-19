package no.ums.pas.core.events;

import no.ums.pas.PAS;

public class EventChangeGPSEpsilon extends EventClass {
	public EventChangeGPSEpsilon(PAS pas) { 
		super(pas);
	}
	public void doEvent(Object value) {
		int n_val = ((Integer)value).intValue();
		get_pas().get_gpscontroller().set_epsilon_meters(n_val);
		get_pas().get_gpsframe().get_label_epsilon_value().setText(n_val + "m");
		get_pas().kickRepaint();
	}
}