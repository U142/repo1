package no.ums.pas.sound;

import javax.sound.sampled.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


public class SoundRecorder extends Thread {
    public static final int RECTYPE_FILE = 1;
    public static final int RECTYPE_OUTPUTSTREAM = 2;
    private static String vocTempPath;


    private TargetDataLine m_line;
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

    SoundRecorder m_recorder_thread = null;

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
    TargetDataLine targetDataLine;
    boolean m_b_haserror = false;
    ActionListener m_osc_callback = null;
    //SoundMixer m_mixer;

    public boolean isSaving() {
        return m_recorder_thread.m_b_savetostream;
    }

    private boolean _isSaving() {
        return m_b_savetostream;
    } //threads own method

    boolean m_b_savetostream = false; //indicates if recording and saving is currently in progress

    public void start_saving_to_stream() { //reset current stream and start saving to a new one (old one is discarded)
        if (m_recorder_thread != null) {
            if (m_recorder_thread.m_outputstream != null)
                m_recorder_thread.m_outputstream.reset();// = new ByteArrayOutputStream
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

    public void finalize() { //set this flag when streamrecorder-thread should quit recording (no more preview or saving)
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
            //audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
            //audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, f_samplerate, n_bits, n_channels, n_channels*2, f_samplerate, false);//22050.0F, 16, 1, 2, 22050.0F, false);
            audioFormat = format;
        } catch (Exception e) {
            //PAS.get_pas().add_event("SoundRecorder.audioFormat: " + e.getMessage(), e);
            //Error.getError().addError("SoundRecorder","Exception in SoundRecorder",e,1);
            m_b_haserror = true;
            e.printStackTrace();
            return;
        }
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); //, 100000

        targetDataLine = null;
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

        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
            /*try {
                   FloatControl recVolume = (FloatControl)targetDataLine.getControl(FloatControl.Type.VOLUME);
               } catch(Exception e) {
                   e.printStackTrace();
               }*/

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            m_b_haserror = true;
            return;
            //PAS.get_pas().add_event("SoundRecorder.targetDataLine: " + e.getMessage(), e);
            //Error.getError().addError("SoundRecorder","Exception in SoundRecorder",e,1);
        }

        AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
        //m_mixer = new SoundMixer();
        m_recorder_thread = new SoundRecorder(/*controller, */audioFormat, targetDataLine, targetType, m_outputstream, m_outputFile, 0, 3000, RECTYPE, osc_callback);
        m_recorder_thread.audioFormat = audioFormat;
        //m_recorder_thread.setPriority(Thread.MAX_PRIORITY);
        m_recorder_thread.setDaemon(true);
        m_recorder_thread.start();
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
                m_recorder_thread = null;
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
        m_line = line;
        m_audioInputStream = new AudioInputStream(line);
        m_targetType = targetType;
        //m_outputFile = file;
        this.recStartTimeMS = recStartTimeMS;
        this.recEndTimeMS = recEndTimeMS;

    }

    public void start() {
        startRecording();
        super.start();
    }


    public void startRecording() {
        try {
            out("Recording...");
            if(m_line!=null)
            	m_line.start();
            //m_linereader = new LineReader();
            //m_line.addLineListener(m_linereader);
            //m_line.start();
            recording = true;
        } catch (Exception e) {
            //PAS.get_pas().add_event("startRecording: " + e.getMessage(), e);
            //Error.getError().addError("SoundRecorder","Exception in startRecording",e,1);
            e.printStackTrace();
            m_b_haserror = true;
            recording = false;
        }
    }
    public void pauseRecording() {
    	recording = false;
    	if(m_line!=null)
    		m_line.stop();
        out("Recording pause...");
    }

    public void stopRecording() {
        out("Stopping...");
        recording = false;
        m_f_stoprecording = true;
        m_b_finalized = true;
        if (m_recorder_thread != null)
            m_recorder_thread.stopRecording();
        if(m_line!=null)
        	m_line.stop();
        //m_line.removeLineListener(m_linereader);
    }

    public void run() {
        //byte[] data = new byte[m_line.getBufferSize() / 5];
        //int numBytesRead;
        //ByteArrayOutputStream out  = new ByteArrayOutputStream();
        //m_outputstream = new ByteArrayOutputStream(1000000);

        int size = 32;
        byte[] abBuffer = new byte[size];
        AudioFormat format = m_line.getFormat();
        int nFrameSize = format.getFrameSize();
        int nBufferFrames = abBuffer.length / nFrameSize;
        recording = true;
        try {
            if (RECTYPE == RECTYPE_FILE)
                AudioSystem.write(m_audioInputStream, m_targetType, m_outputFile);// out / m_outputstream
            else {
                //AudioSystem.write(m_audioInputStream, m_targetType, m_outputstream);
                //m_line.start();
                ByteArrayOutputStream tempstream = new ByteArrayOutputStream();
                //OutputStream		outputStream = tempstream;
                m_outputstream = tempstream;

                while (!this.isFinalized())//m_f_stoprecording)
                {
                    //if (sm_bDebug) { out("BufferingRecorder.run(): trying to read: " + nBufferFrames); }
                    /*int	nFramesRead = m_line.read(abBuffer, 0, nBufferFrames);
                           //if (sm_bDebug) { out("BufferingRecorder.run(): read: " + nFramesRead); }
                           int	nBytesToWrite = nFramesRead * nFrameSize;
                           try
                           {
                               outputStream.write(abBuffer, 0, nBytesToWrite);
                           }
                           catch (IOException e)
                           {
                               e.printStackTrace();
                           }*/
                	if(recording)
                	{
	                    int nFramesRead = m_line.read(abBuffer, 0, size); //65536);
	                    if (this._isSaving())
	                        m_outputstream.write(abBuffer, 0, nFramesRead);
	                    if (m_osc_callback != null) {
	                        //byte [] a = new byte [] { abBuffer };
	                        m_osc_callback.actionPerformed(new ActionEvent(abBuffer, ActionEvent.ACTION_PERFORMED, "act_oscillate"));
	                    }
                	}
                }
                m_line.stop();
                //m_line.drain();
                m_line.close();
                System.out.println("Recording thread quit");

                /* We close the ByteArrayOutputStream.
                       */
                /*try
                      {
                          tempstream.close();
                      }
                      catch (IOException e)
                      {
                          e.printStackTrace();
                      }
                      byte[]	abData = tempstream.toByteArray();
                      System.out.println(abData.length + " bytes written");
                    ByteArrayInputStream	byteArrayInputStream = new ByteArrayInputStream(abData);
                    AudioInputStream	audioInputStream = new AudioInputStream(byteArrayInputStream, format, abData.length / format.getFrameSize());
                    try
                    {
                          m_outputstream = new ByteArrayOutputStream();
                          System.out.println("Writing");
                        AudioSystem.write(audioInputStream,  m_targetType, m_outputstream);
                          System.out.println("End writing");
                        m_outputstream.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }*/

                recording = false;


                //ByteArrayOutputStream temp = new ByteArrayOutputStream();
                /*numBytesRead=0;
                      while(recording==true)
                      {
                          numBytesRead = m_line.read(data,0,data.length);
                          m_outputstream.write(data,0,numBytesRead);
                          //System.out.print(new String(data));
                      }*/
                //File f = new File()
                //BufferedOutputStream file = new BufferedOutputStream(m_outputstream);
                //AudioSystem.write(m_audioInputStream, m_targetType, m_outputstream);
                /*FileInputStream fileinputstream = new FileInputStream(new File("C:\\tempwav\\test.wav"));

                      try {
                          int n = 0;
                          while(1==1) {
                              byte [] c = new byte[1024];
                              int n_read = fileinputstream.read(c);
                              if(n_read==-1)
                                  break;
                              System.out.println(n_read + " written");
                              m_outputstream.write(c, 0, n_read);
                              n += n_read;
                          }
                      } catch(Exception e) {
                          e.printStackTrace();
                      }*/
                /*ByteArrayInputStream buffer = new ByteArrayInputStream(temp.toByteArray());
                      AudioInputStream recorded = AudioSystem.getAudioInputStream(buffer);
                      //BufferedOutputStream file = new BufferedOutputStream(m_outputstream);
                      AudioSystem.write(recorded, AudioFileFormat.Type.WAVE, new File("C:\\tempwav\\test.wav"));
                      //m_outputstream = (ByteArrayOutputStream)file;*/
            }
            //AudioInputStream towav = FormatConversionProvider.getAudioInputStream();
            /*while (recording) {
                    // Read the next chunk of data from the TargetDataLine.
                    numBytesRead =  m_line.read(data, 0, data.length);
                    // Save this chunk of data.
                    out.write(data, 0, numBytesRead);
                 }   */
            //PAS.get_pas().add_event("-- AudioSystem.write finished ", null);
        } catch (Exception e) {
            //PAS.get_pas().add_event("SoundRecorder.run(): " + e.getMessage(), e);
            //Error.getError().addError("SoundRecorder","Exception in run",e,1);
            e.printStackTrace();
            m_b_haserror = true;
        }
        /*try {
                FileOutputStream fos = new FileOutputStream(m_outputFile);
                fos.write(out.toByteArray());
                fos.close();
            } catch(IOException e) {
                get_sendcontroller().get_pas().add_event("SoundRecorder.run(): Error writing to file");
            }*/

    }

    private void _wrapSavedData() {
        /* We close the ByteArrayOutputStream.
               */
        try {
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




