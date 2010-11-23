package no.ums.pas.ums.tools;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.JTextField;


public class StdTextAreaNoTab extends JTextArea
{
	private String regexp = "";
	private Pattern p = null;
	private Component inherit_from;
	public StdTextAreaNoTab(Component inherit_traversal_keys, String text, int rows, int columns, String regexp)
	{
		super(text, rows, columns);
		inherit_from = inherit_traversal_keys;
		setNoTabAllowed();
		this.regexp = regexp;
		if(this.regexp.length()>0)
		{
			p = Pattern.compile(this.regexp);
		}
	}
	public void setNoTabAllowed()
	{
		setFocusTraversalKeysEnabled(true);
		Set<AWTKeyStroke> forwardKeys = inherit_from.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> backwardKeys = inherit_from.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
	}
	@Override
	public void insert(String str, int pos) {
		if(p!=null)
		{
			Matcher m = p.matcher(str);
			StringBuffer sb = new StringBuffer();
			boolean valid = true;
			while(m.find())
			{
				m.appendReplacement(sb, "");
				valid = false;
			}
			m.appendTail(sb);
			if(!valid)
				return;

		}
		super.insert(str, pos);
	}
	@Override
	public void append(String str) {
		super.append(str);
	}
	@Override
	public void replaceRange(String str, int start, int end) {
		super.replaceRange(str, start, end);
	}
}