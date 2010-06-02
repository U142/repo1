package no.ums.pas.plugins.centric;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class DisclaimerDialog extends JDialog {
	public DisclaimerDialog()
	{
		super();
		init();
	}
	public void init()
	{
		this.setLayout(new BorderLayout());
		setTitle("Disclaimer and Broker/Administrator information Window");
		setSize(700,400);
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setModal(true);
		JTextArea area = new JTextArea("Disclaimer text\n\nIf you use this application...\n\n\n\n\n");
		JScrollPane scrollPane = new JScrollPane(area);
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		
		ImageIcon img = null;
		try
		{
			img = new ImageIcon(this.getClass().getResource("logo.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		JLabel lbl = new JLabel(img);
		
		final JLabel helpdesk = new JLabel("For helpdesk call: 0123-456789");
		final JButton btn_ok = new JButton("Ok");
		final JButton btn_cancel = new JButton("Cancel");
		btn_ok.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		btn_cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("User denied Disclaimer");
				System.exit(0);
			}
		});
		btn_ok.setMnemonic(KeyEvent.VK_O);
		btn_cancel.setMnemonic(KeyEvent.VK_C);

		this.getContentPane().add(scrollPane, BorderLayout.NORTH);
		this.getContentPane().add(lbl, BorderLayout.CENTER);
		this.getContentPane().add(new JPanel() {
			public JPanel init()
			{
				add(helpdesk, BorderLayout.WEST);
				add(btn_ok, BorderLayout.CENTER);
				add(btn_cancel, BorderLayout.EAST);
				return this;
			}
		}.init(), BorderLayout.SOUTH);
		pack();
		setVisible(true);			
	}
}