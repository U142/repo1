package no.ums.pas.send.messagelibrary;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import no.ums.pas.PAS;




public class MessageLibDlg extends JDialog implements ComponentListener
{
	MessageLibPanel panel = new MessageLibPanel(MessageLibTreePanel.MESSAGELIB_UPDATE_INTERVAL);
	public MessageLibDlg(JFrame parent)
	{
		super(parent, "Message Library", false);
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
		panel.Start();
	}
	

}