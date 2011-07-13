package no.ums.pas.send;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Send {
	
	
	public Send(ActionListener listener) {
		Object o = new Object();
		//listener.action(new ActionEvent(o, ActionEvent.ACTION_PERFORMED, "act_dill"), o);
		//listener.deliverEvent(new ActionEvent(o, ActionEvent.ACTION_PERFORMED, "act_dill"));
		listener.actionPerformed(new ActionEvent(o, ActionEvent.ACTION_PERFORMED, "act_dill"));
	}
	
}
