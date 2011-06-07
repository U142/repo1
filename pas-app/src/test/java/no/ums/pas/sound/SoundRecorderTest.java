package no.ums.pas.sound;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Port.Info;

import org.junit.Test;

public class SoundRecorderTest {

	@Test
	public void testNothing()
	{
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
			System.out.println("Microphone support=" + (AudioSystem.isLineSupported(Port.Info.MICROPHONE)));
			
			
			Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
			 for (Mixer.Info info: mixerInfos){
			  Mixer m = AudioSystem.getMixer(info);
			  System.out.println(m + " ||" + info.getVendor() + " | " + info.getName() + " | " + info.getDescription());
			  Line.Info [] tli = m.getTargetLineInfo();
			  for(Line.Info li : tli)
			  {
				  System.out.println("  ***Target Line***" + li.toString());
				  System.out.println("  Max lines=" + m.getMaxLines(li));
				  if(li instanceof DataLine.Info)
				  {
					  outputDataLineInfo((DataLine.Info)li);					  
				  }
				  
			  }
			  tli = m.getSourceLineInfo();
			  for(Line.Info li : tli)
			  {
				  System.out.println("  ***Source Line***" + li.toString());
				  System.out.println("  Max lines=" + m.getMaxLines(li));
				  if(li instanceof DataLine.Info)
				  {
					  outputDataLineInfo((DataLine.Info)li);					  
				  }
				  
			  }
			  
			  
			  System.out.println("\n");
	
			 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected static void outputDataLineInfo(DataLine.Info dli)
	{
	  System.out.println("Min/Max buffer size = " + dli.getMinBufferSize() + "/" + dli.getMaxBufferSize());
	  System.out.println("  ***Formats***");
	  for(AudioFormat af : dli.getFormats())
	  {
		  System.out.println("    Encoding   = " + af.getEncoding());
		  System.out.println("    Channels   = " + af.getChannels());
		  System.out.println("    FrameRate  = " + af.getFrameRate());
		  System.out.println("    FrameSize  = " + af.getFrameSize());
		  System.out.println("    SampleRate = " + af.getSampleRate());
		  System.out.println("    SampleBits = " + af.getSampleSizeInBits());
		  System.out.println("    BigEndian  = " + af.isBigEndian());
		  System.out.println("\n");
	  }
	}

}
