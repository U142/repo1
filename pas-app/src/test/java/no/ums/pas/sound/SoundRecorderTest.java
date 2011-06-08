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

import no.ums.log.Log;
import no.ums.log.UmsLog;
import org.junit.Test;

public class SoundRecorderTest {

    private static final Log log = UmsLog.getLogger(SoundRecorderTest.class);

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
			log.debug("Microphone support=" + (AudioSystem.isLineSupported(Port.Info.MICROPHONE)));
			
			
			Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
			 for (Mixer.Info info: mixerInfos){
			  Mixer m = AudioSystem.getMixer(info);
			  log.debug(m + " ||" + info.getVendor() + " | " + info.getName() + " | " + info.getDescription());
			  Line.Info [] tli = m.getTargetLineInfo();
			  for(Line.Info li : tli)
			  {
				  log.debug("  ***Target Line***" + li.toString());
				  log.debug("  Max lines=" + m.getMaxLines(li));
				  if(li instanceof DataLine.Info)
				  {
					  outputDataLineInfo((DataLine.Info)li);					  
				  }
				  
			  }
			  tli = m.getSourceLineInfo();
			  for(Line.Info li : tli)
			  {
				  log.debug("  ***Source Line***" + li.toString());
				  log.debug("  Max lines=" + m.getMaxLines(li));
				  if(li instanceof DataLine.Info)
				  {
					  outputDataLineInfo((DataLine.Info)li);					  
				  }
				  
			  }
			  
			  
			  log.debug("\n");
	
			 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected static void outputDataLineInfo(DataLine.Info dli)
	{
	  log.debug("Min/Max buffer size = " + dli.getMinBufferSize() + "/" + dli.getMaxBufferSize());
	  log.debug("  ***Formats***");
	  for(AudioFormat af : dli.getFormats())
	  {
		  log.debug("    Encoding   = " + af.getEncoding());
		  log.debug("    Channels   = " + af.getChannels());
		  log.debug("    FrameRate  = " + af.getFrameRate());
		  log.debug("    FrameSize  = " + af.getFrameSize());
		  log.debug("    SampleRate = " + af.getSampleRate());
		  log.debug("    SampleBits = " + af.getSampleSizeInBits());
		  log.debug("    BigEndian  = " + af.isBigEndian());
		  log.debug("\n");
	  }
	}

}
