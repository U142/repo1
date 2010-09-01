package no.ums.pas.ums.tools;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class StdTextAreaNoTab extends JTextArea
{
	private Component inherit_from;
	public StdTextAreaNoTab(Component inherit_traversal_keys, String text, int rows, int columns)
	{
		super(text, rows, columns);
		inherit_from = inherit_traversal_keys;
		setNoTabAllowed();
	}
	public void setNoTabAllowed()
	{
		setFocusTraversalKeysEnabled(true);
		Set<AWTKeyStroke> forwardKeys = inherit_from.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> backwardKeys = inherit_from.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
	}
}