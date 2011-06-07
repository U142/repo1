package no.ums.pas.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.swing.JDialog;

public class MixerLinesController {
	
	protected String queryMixersAndLines()
	{
		StringBuilder ret = new StringBuilder();
		
		System.out.println("Microphone support=" + (AudioSystem.isLineSupported(Port.Info.MICROPHONE)));
		ret.append("Microphone support=" + (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) + "\n");
		
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info: mixerInfos){
			 Mixer m = AudioSystem.getMixer(info);
			 ret.append(m + " ||" + info.getVendor() + " | " + info.getName() + " | " + info.getDescription() + "\n");
			 Line.Info [] tli = m.getTargetLineInfo();
			 for(Line.Info li : tli)
			 {
				 ret.append("  ***Target Line***" + li.toString() + "\n");
				 ret.append("  Max lines=" + m.getMaxLines(li) + "\n");
				 if(li instanceof DataLine.Info)
				 {
					 ret.append(outputDataLineInfo((DataLine.Info)li));
					 ret.append("\n");
				 } 
			 }
			 tli = m.getSourceLineInfo();
			 for(Line.Info li : tli)
			 {
				 ret.append("  ***Source Line***" + li.toString() + "\n");
				 ret.append("  Max lines=" + m.getMaxLines(li) + "\n");
				 if(li instanceof DataLine.Info)
				 {
					 ret.append(outputDataLineInfo((DataLine.Info)li));					  
				 }
				 else
				 {
					 ret.append(li.getClass().toString());
				 }
			 }
			 ret.append("\n\n\n");
		 }
		 return ret.toString();
	}
	
	protected String outputDataLineInfo(DataLine.Info dli)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("  Min/Max buffer size = " + dli.getMinBufferSize() + "/" + dli.getMaxBufferSize() + "\n");
		ret.append("  Formats\n");
		for(AudioFormat af : dli.getFormats())
		{
			ret.append("\tEncoding   = " + af.getEncoding() + "\n");
			ret.append("\tChannels   = " + af.getChannels() + "\n");
			ret.append("\tFrameRate  = " + af.getFrameRate() + "\n");
			ret.append("\tFrameSize  = " + af.getFrameSize() + "\n");
			ret.append("\tSampleRate = " + af.getSampleRate() + "\n");
			ret.append("\tSampleBits = " + af.getSampleSizeInBits() + "\n");
			ret.append("\tBigEndian  = " + af.isBigEndian() + "\n");
			ret.append("\n");
		}
		return ret.toString();
	}

}
