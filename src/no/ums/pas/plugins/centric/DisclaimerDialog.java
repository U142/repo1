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
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import no.ums.pas.PAS;

class DisclaimerDialog extends JDialog {
	private boolean b_confirmed = false;
	public boolean isConfirmed() { return b_confirmed; }
	public DisclaimerDialog()
	{
		super();
		init();
	}
	public void init()
	{
		this.setLayout(new BorderLayout());
		setTitle(PAS.l("disclaimer_heading"));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(700,400);
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setModal(true);
		JTextArea area = new JTextArea(PAS.l("disclaimer_text"));
		JScrollPane scrollPane = new JScrollPane(area);
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		
		ImageIcon img = null;
		try
		{
			img = new ImageIcon(this.getClass().getResource("icons/logo.jpg")); //logo.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		JLabel lbl = new JLabel(img);
		
		final JLabel helpdesk = new JLabel(PAS.l("common_helpdesk_contact"));//"For helpdesk call: 0123-456789");
		final JButton btn_ok = new JButton(PAS.l("common_accept"));
		final JButton btn_cancel = new JButton(PAS.l("common_decline"));
		btn_ok.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				b_confirmed = true;
				setVisible(false);
			}
		});
		btn_cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				b_confirmed = false;
				setVisible(false);
			}
		});
		btn_ok.setMnemonic(KeyEvent.VK_A);
		btn_cancel.setMnemonic(KeyEvent.VK_D);

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
	@Override
	public boolean isResizable() {
		return false;
	}
	
}