package no.ums.pas.send.messagelibrary;

import no.ums.pas.PAS;
import no.ums.ws.common.UBBMESSAGE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;




public class MessageLibDlg extends JDialog implements ComponentListener, ActionListener
{
	public static final String ACT_MESSAGE_SELECTED = "act_message_selected";
	public static final String ACT_MESSAGE_SELECTION_CANCELLED = "act_message_selection_cancelled";
	MessageLibPanel panel;
	ActionListener callback;
	public MessageLibDlg(JFrame parent)
	{
		this(parent, true, true);
	}
	public MessageLibDlg(JFrame parent, boolean b_editor_mode, boolean b_enable_multi_cc)
	{
		this(null, parent, b_editor_mode, b_enable_multi_cc);
	}
	public MessageLibDlg(ActionListener callback, JFrame parent, boolean b_editor_mode, boolean b_enable_multi_cc)
	{
		super(parent, PAS.l("main_sending_audio_type_library"), false);
		this.callback = callback;
		panel = new MessageLibPanel(this, MessageLibTreePanel.MESSAGELIB_UPDATE_INTERVAL, b_editor_mode, b_enable_multi_cc);
		final Rectangle rect = PAS.get_pas().get_settings().getRectMessageLibDlg();
		//super("Message Library");
		setAlwaysOnTop(true);
		addComponentListener(this);
		try
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					Dimension ul = no.ums.pas.ums.tools.Utils.paswindow_upperleft(500, 600);
					if(rect.isEmpty())
						setBounds(ul.width, ul.height, 500, 600);
					else
						setBounds(rect.x, rect.y, rect.width, rect.height);

					//setSize(new Dimension(ul));
					add(panel);
					panel.setPreferredSize(new Dimension(500, 600));
					panel.revalidate();
					setVisible(true);
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {
		Rectangle size = this.getBounds();
		PAS.get_pas().get_settings().setRectMessageLibDlg(size);
		PAS.get_pas().get_settings().setMessageLibExplodedNodes(panel.treepanel.tree.getExplodedNodes());
		panel.setVisible(false);
		panel.Stop();
		System.out.println("Updater stopped");
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		int w = getWidth();
		int h = getHeight();
		panel.setPreferredSize(new Dimension(w, h));
		panel.revalidate();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		panel.setVisible(true);
		panel.Start(false);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(ACT_MESSAGE_SELECTED.equals(e.getActionCommand()))
		{
			if(callback!=null)
			{
				UBBMESSAGE msg = (UBBMESSAGE)e.getSource();
				callback.actionPerformed(new ActionEvent(msg, ActionEvent.ACTION_PERFORMED, ACT_MESSAGE_SELECTED));
				this.setVisible(false);
			}
		}
		else if(ACT_MESSAGE_SELECTION_CANCELLED.equals(e.getActionCommand()))
		{
			this.setVisible(false);
		}
	}
	
	
}