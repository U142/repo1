package no.ums.pas.ums.errorhandling;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Container;

public class ErrorGUI extends JFrame {
	public static final long serialVersionUID = 1;
	
	private Container c;
	private JLabel lblIcon, lblDescription, lblCounter;
	private JScrollPane scrollStackTrace;
	private JTextArea txaStackTrace;
	private JButton btnNext, btnPrevious, btnSend;
	private JPanel pnlSouth, pnlNorth;
		
	public ErrorGUI() {
		c = getContentPane();
		c.setLayout(new BorderLayout());
		
		pnlNorth = new JPanel();
		lblIcon = new JLabel();
		
		lblDescription = new JLabel();
		lblCounter = new JLabel();
		txaStackTrace = new JTextArea(20,20);
		scrollStackTrace = new JScrollPane(txaStackTrace);
		pnlSouth = new JPanel();
		btnPrevious = new JButton("Previous");
		btnNext = new JButton("Next");
		btnSend = new JButton("Send error report");
		
		pnlNorth.add(lblIcon);
		pnlNorth.add(lblDescription);
		c.add(pnlNorth,BorderLayout.NORTH);
		c.add(scrollStackTrace,BorderLayout.CENTER);
		pnlSouth.add(lblCounter);
		pnlSouth.add(btnPrevious);
		pnlSouth.add(btnNext);
		pnlSouth.add(btnSend);
		c.add(pnlSouth,BorderLayout.SOUTH);
		setSize(400,400);
	}
	
	public JLabel getLblDescription(){
		return lblDescription;
	}
	
	public JTextArea getTxaStackTrace(){
		return txaStackTrace;
	}
	
	public JButton getBtnNext(){
		return btnNext;
	}
	
	public JButton getBtnPrevious(){
		return btnPrevious;
	}
	
	public JButton getBtnSend(){
		return btnSend;
	}
	
	public JLabel getLblCounter(){
		return lblCounter;
	}
	
	public JLabel getLblIcon() {
		return lblIcon;
	}
}
