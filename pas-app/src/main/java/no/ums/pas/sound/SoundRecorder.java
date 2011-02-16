package no.ums.pas.sound;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import javax.swing.JOptionPane;

import no.ums.pas.PAS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


public class SoundRecorder extends Thread {
    public static final int RECTYPE_FILE = 1;
    public static final int RECTYPE_OUTPUTSTREAM = 2;
    private static String vocTempPath;


    protected static TargetDataLine AUDIOLINE = null;
    protected static AudioFormat AUDIOFORMAT = null;
    protected static boolean LINE_AVAILABLE = true;
    
    public static boolean InitTargetDataLine(AudioFormat audioFormat)
    	throws Exception
    {
    	if(AUDIOLINE==null && LINE_AVAILABLE)
    	{
    		AUDIOFORMAT = audioFormat;
	        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); //, 100000
	        try
	        {
	        	AUDIOLINE = (TargetDataLine) AudioSystem.getLine(info);
	        	AUDIOLINE.open();
	        	return (LINE_AVAILABLE = true);
	        }
	        catch(Exception e)
	        {
	        	LINE_AVAILABLE = false;
	        	throw e;
	        }
	        finally
	        {
	        	
	        }
    	}
    	return true;
    }
    
    private AudioFileFormat.Type m_targetType;
    private AudioInputStream m_audioInputStream;

    public static String getVocTempPath() {
        return vocTempPath;
    }

    public static void setVocTempPath(String vocTempPath) {
        SoundRecorder.vocTempPath = vocTempPath;
    }

    public AudioInputStream get_audioinputstream() {
        return m_audioInputStream;
    }

    public int RECTYPE = RECTYPE_FILE;
    private File m_outputFile;
    //private OutputStream m_os;
    private boolean recording = false;

    public boolean isRecording() {
        return recording;
    }

    //public OutputStream getOS() { return m_os; }
    private long recStartTimeMS = 0;
    private long recEndTimeMS = 0;
    private ByteArrayOutputStream m_outputstream;

    //private ByteArray m_arr_audio;
    public ByteArrayOutputStream get_outputstream() {
        return m_outputstream;
    }

    public File get_file() {
        return m_outputFile;
    }

    //private SendController m_sendcontroller;
    //private SendController get_sendcontroller() { return m_sendcontroller; }
    private boolean m_f_stoprecording = false;

    public void stop_recording() {
        m_f_stoprecording = true;
    }

    static SoundRecorder m_recorder_thread = null;

    SoundRecorder get_recorder_thread() {
        return m_recorder_thread;
    }

    DataLine.Info m_info;
    AudioFormat audioFormat = null;
    private String m_sz_filename;

    public String get_filename() {
        return m_sz_filename;
    }

    LineReader m_linereader = null;
    //TargetDataLine targetDataLine;
    boolean m_b_haserror = false;
    ActionListener m_osc_callback = null;
    //SoundMixer m_mixer;

    public boolean isSaving() {
    	if(m_recorder_thread!=null)
    		return m_recorder_thread.m_b_savetostream;
    	return false;
    }

    private boolean _isSaving() {
        return m_b_savetostream;
    } //threads own method

    boolean m_b_savetostream = false; //indicates if recording and saving is currently in progress

    public void start_saving_to_stream() { //reset current stream and start saving to a new one (old one is discarded)
        if(m_recorder_thread!=null)
        	m_recorder_thread.m_osc_callback = m_osc_callback;

    	if (m_recorder_thread != null) {
            if (m_recorder_thread.m_outputstream != null)
                m_recorder_thread.m_outputstream  = new ByteArrayOutputStream();
            else
                m_recorder_thread.m_outputstream = new ByteArrayOutputStream();
            m_recorder_thread.m_b_savetostream = true;
        }
    }

    public void stop_saving_to_stream() {
        if (m_recorder_thread != null) {
            m_recorder_thread.m_b_savetostream = false;
            m_recorder_thread._wrapSavedData();
        }
    }

    boolean m_b_finalized = false;

    public boolean isFinalized() {
        return m_b_finalized;
    }

    public void finalizeRecording() { //set this flag when streamrecorder-thread should quit recording (no more preview or saving)
        if (m_recorder_thread != null)
            m_b_finalized = true;
        m_recorder_thread.m_b_finalized = true;
    }


    public SoundRecorder(/*SendController controller,*/ String sz_path, int RECTYPE, ActionListener osc_callback, AudioFormat format) {//ActionListener osc_callback, float f_samplerate, int n_bits, int n_channels) {
        //m_outputstream = os;
        super("SoundRecorder thread");
        this.RECTYPE = RECTYPE;
        setVocTempPath(sz_path);
        m_osc_callback = osc_callback;
        //m_sendcontroller = controller;

        m_f_stoprecording = false;
        try {
            audioFormat = format;
        } catch (Exception e) {
            m_b_haserror = true;
            e.printStackTrace();
            return;
        }
        //DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); //, 100000
        /*DataLine.Info info = new DataLine.Info(TargetDataLine.class, AudioSystem.getTargetFormats(
                AudioFormat.Encoding.PCM_SIGNED, audioFormat ),
                audioFormat.getFrameSize(),
                audioFormat.getFrameSize() * 2 );*/

        //targetDataLine = null;
        if (RECTYPE == RECTYPE_FILE) {
            try {
                System.out.println(getVocTempPath());
                m_outputFile = File.createTempFile("voc", "", new File(getVocTempPath()));
                m_sz_filename = m_outputFile.getName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            m_outputFile = null;
            m_sz_filename = "";
        }
        //PAS.get_pas().add_event("recording to " + m_sz_filename, null);

        /*try {
            //targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            //targetDataLine.open(audioFormat);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            m_b_haserror = true;
            return;
            //PAS.get_pas().add_event("SoundRecorder.targetDataLine: " + e.getMessage(), e);
            //Error.getError().addError("SoundRecorder","Exception in SoundRecorder",e,1);
        }*/

        AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
        //m_mixer = new SoundMixer();
        /*try
        {
        	InitTargetDataLine(audioFormat);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }*/
    }

    public void init_oscilliator() {
        if (m_osc_callback != null)
            m_osc_callback.actionPerformed(new ActionEvent(new OscillatorProperties(1000.0F, 0.7F, audioFormat), ActionEvent.ACTION_PERFORMED, "act_oscillator_init"));
    }

    class LineReader implements LineListener {
        public void update(LineEvent e) {
        }
    }

    public void stop_thread() {
        //if(RECTYPE==RECTYPE_FILE)
        {
            if (m_recorder_thread != null) {
                stop_recording();
                m_recorder_thread.stopRecording();
                while (m_recorder_thread.recording) {
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                    }
                }
                //m_recorder_thread = null;
            } else {
                this.stop_saving_to_stream();

            }
        }
    }


    public SoundRecorder(/*SendController controller, */AudioFormat format, TargetDataLine line,
                         AudioFileFormat.Type targetType,
                         OutputStream os, File f,
                         long recStartTimeMS,
                         long recEndTimeMS, int RECTYPE, ActionListener osc_callback) {
        super("SoundRecorder thread");
        m_outputFile = f;
        m_osc_callback = osc_callback;

        this.RECTYPE = RECTYPE;
        //m_sendcontroller = controller;
        //m_line = line;
        m_audioInputStream = new AudioInputStream(AUDIOLINE);
        m_targetType = targetType;
        //m_outputFile = file;
        this.recStartTimeMS = recStartTimeMS;
        this.recEndTimeMS = recEndTimeMS;

    }

    public void start() {
        //startRecording();
        if(!super.isAlive())
        	super.start();
    }

    public void restartRecording() {
    	stopRecording();
    	startRecording();
    }

    public void startRecording() {
        try {
            if(AUDIOLINE!=null)
            {
        		//AUDIOLINE = null;
        		//Mixer.Info[] lines = AudioSystem.getMixerInfo();
    			//AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 1, 2, 44100.0F, false);
    			try
    			{
    				//InitTargetDataLine(audioFormat);
    				if(!AUDIOLINE.isOpen())
    				{
    					//AUDIOLINE.open();
                    	AUDIOLINE.open(AUDIOFORMAT);
                    	System.out.println("Audioline opened");
    				}
    				if(!AUDIOLINE.isActive())
    				{
    					AUDIOLINE.start();
    					System.out.println("Audioline started");
    				}
                    AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
                    if(m_recorder_thread==null || !m_recorder_thread.isAlive())
                    {
                        m_recorder_thread = new SoundRecorder(audioFormat, null, targetType, m_outputstream, m_outputFile, 0, 3000, RECTYPE, m_osc_callback);
                        m_recorder_thread.audioFormat = audioFormat;
                        m_recorder_thread.setDaemon(true);
                        m_recorder_thread.start();                    	
                    }
                    if(m_recorder_thread!=null)
                    	m_recorder_thread.m_osc_callback = m_osc_callback;
                	recording = true;
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
            }
        } catch (Exception e) {
            e.printStackTrace();
            m_b_haserror = true;
            recording = false;
        }
    }
    public void pauseRecording() {
    	recording = false;
    	if(AUDIOLINE!=null && AUDIOLINE.isActive())
    	{
    		AUDIOLINE.close();
    		AUDIOLINE.stop();
        	if(m_recorder_thread!=null)
        	{
            	System.out.println("Audioline closed and stopped");
                m_recorder_thread.stopRecording();
        		m_recorder_thread.interrupt();
        		//m_recorder_thread.interrupt();
        	}
    	}
        //out("Recording pause...");
    }

    public void stopRecording() {
        out("Stopping...");
        recording = false;
        m_f_stoprecording = true;
        m_b_finalized = true;
        if (m_recorder_thread != null)
        {
    		m_recorder_thread.interrupt();
        }
        if(AUDIOLINE!=null)
        {
        	AUDIOLINE.close();
        	AUDIOLINE.stop();
        	System.out.println("Audioline closed and stopped");
        }
        //m_line.removeLineListener(m_linereader);
    }

    public void run() {
        int size = 128;//AUDIOLINE.getBufferSize();
        byte[] abBuffer = new byte[size];
        AudioFormat format = AUDIOLINE.getFormat();
        int nFrameSize = format.getFrameSize();
        int nBufferFrames = abBuffer.length / nFrameSize;
        recording = true;
        try {
            if (RECTYPE == RECTYPE_FILE)
                AudioSystem.write(m_audioInputStream, m_targetType, m_outputFile);// out / m_outputstream
            else {
                //m_line.start();

                int offset = 0;
                while (!this.isFinalized())//m_f_stoprecording)
                {
                	if(recording)
                	{
                		//System.out.println(this.toString());
                		try
                		{
                			if(AUDIOLINE!=null && AUDIOLINE.isActive())
                			{
			                    int nFramesRead = AUDIOLINE.read(abBuffer, 0, abBuffer.length); //65536);
			                    if (this._isSaving() && nFramesRead>0)
			                    {
			                        m_outputstream.write(abBuffer, 0, nFramesRead);
			                    }
			                    if (m_osc_callback != null) {
			                        //byte [] a = new byte [] { abBuffer };
			                        m_osc_callback.actionPerformed(new ActionEvent(abBuffer, ActionEvent.ACTION_PERFORMED, "act_oscillate"));
			                    }
                			}
                			Thread.sleep(1);
                		}
                		catch(InterruptedException e)
                		{
                			System.out.println("Interrupted");
                			break;
                		}
                		catch(Exception e)
                		{
                			e.printStackTrace();
                		}
                	}
                }
                AUDIOLINE.stop();
                AUDIOLINE.close();
                System.out.println("Recording thread quit");


                recording = false;



            }

        } catch (Exception e) {
            e.printStackTrace();
            m_b_haserror = true;
        }
    }

    private void _wrapSavedData() {
        /* We close the ByteArrayOutputStream.
               */
        try {
        	m_outputstream.flush();
        	m_outputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] abData = m_outputstream.toByteArray();
        System.out.println(abData.length + " bytes written");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(abData);
        AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, abData.length / audioFormat.getFrameSize());
        try {
            m_outputstream = new ByteArrayOutputStream();
            System.out.println("Writing");
            AudioSystem.write(audioInputStream, m_targetType, m_outputstream);
            System.out.println("End writing");
            m_outputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static void waitForSafety(String message) {
        System.out.print(message);
        try {
            for (int i = 5; i > 0; i--) {
                sleep(1000);
            }
        } catch (Exception e) {
        }
        out();
    }

    private static void out(String strMessage) {
        System.out.println(strMessage);
    }

    private static void out() {
        System.out.println();
    }
}




