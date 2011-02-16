package no.ums.pas.sound;

import no.ums.pas.PAS;
import no.ums.pas.ums.tools.StdTextLabel;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;



public class SoundPlayer {
	PAS m_pas;
	String m_sz_filename;
	Clip clip;
	boolean playing = false;
	int audioLength;
	int audioPosition = 0;
	SoundPlayer player_ctrl;
	public SoundPlayer getPlayer() { return player_ctrl; }
	JSlider progress;
	//JButton btn_play;
	SoundRecorderPanel m_panel;
	Timer timer;
	StdTextLabel m_txt_seconds;
	
	public SoundPlayer(String sz_filename, JSlider slider, SoundRecorderPanel rec_panel, 
			StdTextLabel txt_seconds, boolean b_default_path) {
		m_sz_filename = sz_filename;
		m_txt_seconds = txt_seconds;
		progress = slider;
		m_panel = rec_panel;
		//this.btn_play = btn_play;
		File file = new File((b_default_path ? SoundRecorder.getVocTempPath() : "") + m_sz_filename);
		try {
			player_ctrl = new SoundPlayer(file, null, slider, rec_panel, txt_seconds);
				
					
		} catch(Exception e) {
			//Error.getError().addError("SoundPlayer","Exception in SoundPlayer",e,1);
			e.printStackTrace();
		}
	}	
	public SoundPlayer(/*ByteArrayOutputStream os*/ByteBuffer buf, JSlider slider, SoundRecorderPanel rec_panel, 
			StdTextLabel txt_seconds, boolean b_default_path) {
		m_txt_seconds = txt_seconds;
		progress = slider;
		m_panel = rec_panel;
		try {
			//HttpPostForm.newInputStream();
			//PipedInputStream pis = new PipedInputStream(os);
			ByteArrayInputStream is = new ByteArrayInputStream(buf.array());
			//ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			player_ctrl = new SoundPlayer(null, is, slider, rec_panel, txt_seconds);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	void set_timer(String sz_time) {
		m_txt_seconds.setText(sz_time + PAS.l("common_seconds_maybe"));
	}
	
    public SoundPlayer(File f, InputStream is, JSlider slider, SoundRecorderPanel rec_panel, StdTextLabel txt_seconds)
    throws IOException,
           UnsupportedAudioFileException,
           LineUnavailableException
	{
		progress = slider;
		m_txt_seconds = txt_seconds;
		//this.btn_play = btn_play;
		m_panel = rec_panel;
	    // Getting a Clip object for a file of sampled audio data is kind
	    // of cumbersome.  The following lines do what we need.
		AudioInputStream ain;
        DataLine.Info info;
		if(f!=null && f.exists()) {
			ain = AudioSystem.getAudioInputStream(f);
	        info =
	            new DataLine.Info(Clip.class,ain.getFormat( ));
		}
		else {
	  		//AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
	  		AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 22050.0F, 16, 1, 2, 22050.0F, false);
			ain = AudioSystem.getAudioInputStream(is);
			//audioFormat = AudioSystem.getAudioFileFormat(ain).getFormat();
	        info = new DataLine.Info(Clip.class,audioFormat);
		}
	    try {
	        clip = (Clip) AudioSystem.getLine(info);
	    	//clip = (Clip)SoundRecorder.AUDIOLINE;
	        clip.open(ain);
	        //AudioSystem.
	    }
	    finally { // We're done with the input stream.
	        ain.close( );
	    }
	    // Get the clip length in microseconds and convert to milliseconds
	    audioLength = (int)(clip.getMicrosecondLength( )/1000);
	
	    if(progress!=null)
	    {
		    progress.setMaximum(audioLength);
		    // Whenever the slider value changes, first update the time label.
		    // Next, if we're not already at the new position, skip to it.
		    progress.addChangeListener(new ChangeListener( ) {
		            public void stateChanged(ChangeEvent e) {
		                int value = progress.getValue( );
		                // Update the time label
		                set_timer(value/1000 + "." + (value%1000)/100);
		                // If we're not already there, skip there.
		                if (value != audioPosition) skip(value);
		            }
		    		});
	    }
	    
	    // This timer calls the tick( ) method 10 times a second to keep 
	    // our slider in sync with the music.
	    timer = new javax.swing.Timer(100, new ActionListener( ) {
	            public void actionPerformed(ActionEvent e) { tick( ); }
	        });
	}	
    /** Start playing the sound at the current position */
    public void play( ) {
        clip.start( );
        timer.start( );
        //play.setText("Stop");
        playing = true;
    }

    /** Stop playing the sound, but retain the current position */
    public void stop( ) {
        timer.stop( );
        clip.stop( );
        //play.setText("Play");
        playing = false;
    }

    /** Stop playing the sound and reset the position to 0 */
    public void reset( ) {
        stop( );
        clip.setMicrosecondPosition(0);
        audioPosition = 0; 
        if(progress!=null)
        	progress.setValue(0);
        //btn_play.setText("Play");
        //btn_play.setIcon(SoundRecorderPanel.MODE_PAUSE_);
        if(m_panel!=null)
        	m_panel.set_mode(SoundRecorderPanel.MODE_PAUSE_);
    }

    /** Skip to the specified position */
    public void skip(int position) { // Called when user drags the slider
        if (position < 0 || position > audioLength) return;
        audioPosition = position;
        clip.setMicrosecondPosition(position * 1000);
        progress.setValue(position); // in case skip( ) is called from outside
    }

    /** Return the length of the sound in ms or ticks */
    public int getLength( ) { return audioLength; }

    void tick( ) {
        if (clip.isActive( )) {
            audioPosition = (int)(clip.getMicrosecondPosition( )/1000);
            if(progress!=null)
            	progress.setValue(audioPosition);
        }
        else reset( );  
    }
}
