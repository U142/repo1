package no.ums.pas.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JProgressBar;


public class Oscillator
{
	private static final boolean	DEBUG = false;

	public static final int		WAVEFORM_SINE = 0;
	public static final int		WAVEFORM_SQUARE = 1;
	public static final int		WAVEFORM_TRIANGLE = 2;
	public static final int		WAVEFORM_SAWTOOTH = 3;

	private byte[]			m_abData;
	private int			m_nBufferPosition;
	private long			m_lRemainingFrames;
	private AudioFormat m_format;
	protected AudioFormat getFormat() { return m_format; }
	int nPeriodLengthInFrames;
	float fAmplitude;
	byte [] m_ret;
	private long m_nFrame;
	JProgressBar m_ampl ;
	int m_n_oscilliate_update;
	int m_n_oscilliate_max;
	TargetDataLine dataLine;
	public JProgressBar get_ampl() { return m_ampl; }
	
	public Oscillator(
			TargetDataLine dataLine,
			  float fSignalFrequency,
			  float fAmplitude,
			  AudioFormat audioFormat,
			  int oscilliate_max,
			  int oscilliate_update)
	{
		this.dataLine = dataLine;
		m_n_oscilliate_max = oscilliate_max;
		m_format = audioFormat;
		m_n_oscilliate_update = oscilliate_update;
		this.fAmplitude = (float) (fAmplitude * Math.pow(2, getFormat().getSampleSizeInBits() - 1));
		nPeriodLengthInFrames = Math.round(getFormat().getFrameRate() / fSignalFrequency);
		m_ret = new byte[4];
		m_nFrame = 0;
		m_ampl= new JProgressBar(0, oscilliate_max);
		m_ampl.setForeground(new java.awt.Color(255, 0, 0));
		m_ampl.setFocusable(false);
		m_ampl.setToolTipText("Sound pressure");
	}
	float f_total = 0;
	float f_samples = 0;
	
	public void Oscillate(int nWaveformType, long lLength, byte [] data) {
/*		super(new ByteArrayInputStream(new byte[0]),
		      new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				      audioFormat.getSampleRate(),
				      16,
				      2,
				      4,
				      audioFormat.getFrameRate(),
				      audioFormat.isBigEndian()),
		      lLength);*/
		m_abData = data;
		m_lRemainingFrames = lLength;
		
		// length of one period in frames
		int nBufferLength = nPeriodLengthInFrames * getFormat().getFrameSize();
		for (int nFrame = 0; nFrame*getFormat().getFrameSize() < lLength; nFrame+=getFormat().getFrameSize())//=getFormat().getFrameSize())
		{
			double [] d_strength = frameToSignedDoubles(m_abData, m_format.getChannels());
			f_total += (float)((Math.abs(d_strength[0])) ); //+ d_strength[1]) * m_n_oscilliate_max;
			f_samples++;
		}
		long n_tick = System.currentTimeMillis();
		if(n_tick - n_lastoscupd > m_n_oscilliate_update) {
			n_lastoscupd = n_tick;
			m_ampl.setValue((int)(f_total * m_n_oscilliate_max / f_samples));
			f_total = 0;
			f_samples = 0;
		}
		
		m_nFrame++;//=getFormat().getFrameSize();
		m_nBufferPosition = 0;
	}
	long n_lastoscupd = 0;
	public short getShort(byte littleByte, byte bigByte) {
	    int v = (bigByte << 8) | littleByte;
	    if( ((v >>> 16) & 1) == 1) {
	        v = v - 1;
	        v = v ^ 0xffff;
	        v = -v;
	    }
	    return (short) v;
	}
	
	public double[] frameToSignedDoubles(byte[] b, int n_channels) {
		double[] d = new double[n_channels];
		boolean littleEndian = !m_format.isBigEndian();
		double peak = 0;
		int ampl = 8;
		boolean bIsSigned = m_format.getEncoding()==Encoding.PCM_SIGNED;
		if(m_format.getSampleSizeInBits()==8)
		{
			for(int i=0; i < b.length-1; i++)
			{
				double sample = 128-Math.abs(b[i]);//Math.abs(128.0 + b[i]);
				if(sample>peak)
					peak = sample;
			}
			d[0] = peak / (256.0/ampl); //peak/(1<<8);
		}
		else if(m_format.getSampleSizeInBits()==16)
		{

			for(int i=0; i < b.length-1; i+=2)
			{
				//double sample = 128-Math.abs(b[i]);//Math.abs(128.0 + b[i]);
				//if(sample>peak)
				//	peak = sample;
				int low = b[i];
				int high = b[i+1];
				int sample = (littleEndian ? (high << 8) + (low & 0x00ff) : (low << 8) + (high & 0x00ff));
				if(sample>peak)
					peak = sample;
			}
			d[0] = peak / (65536.0 / ampl); //peak/(1<<8);

			/*if(littleEndian)
			{
				//d[cc] = (b[cc*2]*256 + (b[cc*2+1])) / 65536.0;
				d[cc] = 128+getShort(b[cc*2], b[cc*2+1]);
				int sValue = (b[cc] + b[cc+1] << 8);
				d[cc] = sValue / 65536.0;
				d[cc] = (b[cc*2]*256 + (b[cc*2+1] & 0xFF))/65536.0;
				d[cc] = (b[cc*2] & 0xFF + (b[cc*2+1] * 256))/65536.0;
			}
			else
			{
				d[cc] = getShort(b[cc*2+1], b[cc*2]);
				
			}*/
		}
		return d;
	}	
}