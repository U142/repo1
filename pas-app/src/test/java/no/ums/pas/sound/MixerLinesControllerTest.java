package no.ums.pas.sound;

import org.junit.Test;

public class MixerLinesControllerTest {

	@Test
	public void testNothing()
	{
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MixerLinesController controller = new MixerLinesController();
		new MixerLinesView(null, controller).showDlg();
		
	}

}
