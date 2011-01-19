package no.ums.pas.core.events;

import no.ums.pas.PAS;

public abstract class EventClass {
	PAS m_pas;
	PAS get_pas() { return m_pas; }
	public EventClass(PAS pas) {
		m_pas = pas;
	}
	public abstract void doEvent(Object value);
}