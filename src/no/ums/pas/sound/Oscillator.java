package no.ums.pas.sound;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;


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
	public JProgressBar get_ampl() { return m_ampl; }
	
	public Oscillator(
			  float fSignalFrequency,
			  float fAmplitude,
			  AudioFormat audioFormat,
			  int oscilliate_max,
			  int oscilliate_update)
	{
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
		//m_abData = new byte[nBufferLength];
		//for (int nFrame = 0; nFrame < nPeriodLengthInFrames; nFrame++)
		for (int nFrame = 0; nFrame*getFormat().getFrameSize() < lLength; nFrame+=getFormat().getFrameSize())//=getFormat().getFrameSize())
		{
			/**	The relative position inside the period
				of the waveform. 0.0 = beginning, 1.0 = end
			*/
			/*float	fPeriodPosition = (float) nFrame / (float) nPeriodLengthInFrames;
			float	fValue = 0;
			switch (nWaveformType)
			{
			case WAVEFORM_SINE:
				fValue = (float) Math.sin(fPeriodPosition * 2.0 * Math.PI);
				break;

			case WAVEFORM_SQUARE:
				fValue = (fPeriodPosition < 0.5F) ? 1.0F : -1.0F;
				break;

			case WAVEFORM_TRIANGLE:
				if (fPeriodPosition < 0.25F)
				{
					fValue = 4.0F * fPeriodPosition;
				}
				else if (fPeriodPosition < 0.75F)
				{
					fValue = -4.0F * (fPeriodPosition - 0.5F);
				}
				else
				{
					fValue = 4.0F * (fPeriodPosition - 1.0F);
				}
				break;

			case WAVEFORM_SAWTOOTH:
				if (fPeriodPosition < 0.5F)
				{
					fValue = 2.0F * fPeriodPosition;
				}
				else
				{
					fValue = 2.0F * (fPeriodPosition - 1.0F);
				}
				break;
			}
			int	nValue = Math.round(fValue * fAmplitude);*/
			int nBaseAddr = (nFrame);// * getFormat().getFrameSize();
			// this is for 16 bit stereo, little endian
			/*m_ret[0] = (byte) (m_abData[nBaseAddr + 0] * nValue & 0xFF);
			m_ret[1] = (byte) (m_abData[nBaseAddr + 1] * (nValue >>> 8) & 0xFF);
			m_ret[2] = (byte) (m_abData[nBaseAddr + 2] * nValue & 0xFF);
			m_ret[3] = (byte) (m_abData[nBaseAddr + 3] * (nValue >>> 8) & 0xFF);

			(m_abData[nBaseAddr + 0] + fValue);
			(m_abData[nBaseAddr + 1] + fValue);
			(m_abData[nBaseAddr + 2] + fValue);
			(m_abData[nBaseAddr + 3] + fValue);
		*/
			//m_ampl.setValue(getShort(m_abData[nBaseAddr + 0], m_abData[nBaseAddr + 1]) + getShort(m_abData[nBaseAddr + 2],m_abData[nBaseAddr + 3]));
			//f_total += Math.abs(getShort(m_abData[nBaseAddr + 0], m_abData[nBaseAddr + 1]));// + getShort(m_abData[nBaseAddr + 2],m_abData[nBaseAddr + 3]));
			double [] d_strength = frameToSignedDoubles(m_abData, 1);
			/*if(d_strength[0] > 0.2)
				System.out.println(d_strength[0]);*/
			f_total += (float)((Math.abs(d_strength[0])) * m_n_oscilliate_max * 20); //+ d_strength[1]) * m_n_oscilliate_max;
			f_samples++;
			//System.out.println(m_ret[0] + " " + m_ret[1] + " " + m_ret[2] + " " + m_ret[3]);
			//System.out.println(getShort(m_abData[nBaseAddr + 1], m_abData[nBaseAddr + 0]) + " " + getShort(m_abData[nBaseAddr + 3],m_abData[nBaseAddr + 2]));
			//System.out.println(getShort(m_ret[0], m_ret[1]) + " " + getShort(m_ret[2],m_ret[3]));
		}
		long n_tick = System.currentTimeMillis();
		if(n_tick - n_lastoscupd > m_n_oscilliate_update) {
			n_lastoscupd = n_tick;
			m_ampl.setValue((int)(f_total / f_samples));
			//System.out.println((int)(f_total / f_samples));
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
		for (int cc = 0; cc < n_channels; cc++) {
			d[cc] = (b[cc*2+1]*256 + (b[cc*2] & 0xFF))/32678.0;
		}
		return d;
	}	
}