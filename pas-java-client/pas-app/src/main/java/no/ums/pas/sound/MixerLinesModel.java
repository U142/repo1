package no.ums.pas.sound;

import org.jdesktop.beansbinding.*;

public class MixerLinesModel extends AbstractBean {
	public String mixersAndLines;

	public String getMixersAndLines() {
		return mixersAndLines;
	}

	public void setMixersAndLines(String mixersAndLines) {
		String oldValue = this.mixersAndLines;
		this.mixersAndLines = mixersAndLines;
		update("mixersAndLines", oldValue, this.mixersAndLines);
	}
	
	
	
}
