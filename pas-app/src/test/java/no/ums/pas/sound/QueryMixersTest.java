package no.ums.pas.sound;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Port.Info;

import org.junit.Test;

class MixerAndControls
{
	private Mixer mixer;
	private Map<String,Control> controls;
	public Mixer getMixer() {
		return mixer;
	}
	public void setMixer(Mixer mixer) {
		this.mixer = mixer;
	}
	public Map<String, Control> getControls() {
		return controls;
	}
	public void setControls(Map<String, Control> controls) {
		this.controls = controls;
	}
}


public class QueryMixersTest {

	@Test
	public void testNothing()
	{
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String,MixerAndControls> recMixers = getAllRecodingMixers();
		
	}
	
	
	public static Map<String,MixerAndControls> getAllRecodingMixers()
	{
		Map<String,MixerAndControls> recMixers = new Hashtable<String, MixerAndControls>();
		Line.Info targetDLInfo = new Line.Info(TargetDataLine.class);
		Line.Info sourceDLInfo = new Line.Info(SourceDataLine.class);
		
		Mixer.Info [] mixers = AudioSystem.getMixerInfo();
		System.out.println("Available recording mixers:");
		
		//first find all rec mixers 
		for(Mixer.Info mxInfo : mixers) {
			Mixer currentMixer = AudioSystem.getMixer(mxInfo);
			if( currentMixer.isLineSupported(targetDLInfo) ) {
				//mixer has a target line, it's a recording device
				MixerAndControls mac = new MixerAndControls();
				mac.setMixer(currentMixer);
				//mac.setControls(getAllMixerControls(currentMixer));
				recMixers.put(mxInfo.getName(), mac);
				System.out.println("Recording mixer = " + mxInfo.getName());
			}
		}
		
		//find all sourcelines based on each mixer found
		Iterator<Entry<String,MixerAndControls>> it = recMixers.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String,MixerAndControls> entry = it.next();
			
		}
		
		return recMixers;
	}
	
	public static Map<String,Control> getAllMixerControls(Mixer currentMixer)
	{
		Map<String,Control> mixerControls = new Hashtable<String, Control>();
		//check for mixer ports with controls.
		for(Line.Info info : currentMixer.getSourceLineInfo())
		{
			System.out.println("Line.Info = " + info.toString());
			if(info instanceof Port.Info)
			{
				try
				{
					Port port = (Port)currentMixer.getLine(info);
					port.open();
					if(port.getControls().length>0)
					{
						final CompoundControl cc = (CompoundControl)port.getControls()[0];
						final Control[] controls = cc.getMemberControls();
						for(final Control c : controls)
						{							
							System.out.println("  " + currentMixer.getMixerInfo().getName() + " control=" + c.toString());
							mixerControls.put(c.toString(), c);
							if(c instanceof FloatControl)
							{
							}
							else if(c instanceof BooleanControl)
							{
								
							}
						}
					}
					port.close();
				}
				catch(Exception e)
				{
					
				}
			}
		}
		return mixerControls;

	}
	
	/**
			//get all recording mixers
			Line.Info targetDLInfo = new Line.Info(TargetDataLine.class);
			Mixer.Info [] mixers = AudioSystem.getMixerInfo();
			System.out.println("Available mixers:");
			for(int cnt = 0; cnt < mixers.length; cnt++) {
				Mixer currentMixer = AudioSystem.getMixer(mixers[cnt]);
				try
				{
					//currentMixer.open();
				}
				catch(Exception e) { }
				System.out.println("mixer name: " + mixers[cnt].getName() + " index:" + cnt + " ports=" + currentMixer.getControls().length);
				if( currentMixer.isLineSupported(targetDLInfo) ) {
					//mixer has a target line, it's a recording device
				}
				//check for mixer ports with controls.
				for(Line.Info info : currentMixer.getSourceLineInfo())
				{
					if(info instanceof Port.Info)
					{
						Port port = (Port)AudioSystem.getLine(info);
						port.open();
						if(port.getControls().length>0)
						{
							final CompoundControl cc = (CompoundControl)port.getControls()[0];
							final Control[] controls = cc.getMemberControls();
							for(final Control c : controls)
							{
								System.out.println("  control=" + c.toString());
								if(c instanceof FloatControl)
								{
									
								}
								else if(c instanceof BooleanControl)
								{
									
								}
							}
						}
						port.close();
					}
				}
				try
				{
					//currentMixer.close();
				}
				catch(Exception e) { }
			}	 
	 */

}
