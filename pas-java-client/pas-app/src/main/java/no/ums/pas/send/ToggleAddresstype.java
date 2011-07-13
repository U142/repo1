package no.ums.pas.send;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import no.ums.pas.icons.ImageFetcher;

public class ToggleAddresstype extends JButton { //JToggleButton {
	public static final long serialVersionUID = 1;
	private int m_n_adrtype;
	private boolean bSelected = false;
	private ImageIcon icoGray;
	private ImageIcon ico;
	
	public int get_adrtype() { return m_n_adrtype; }
	public ToggleAddresstype(ImageIcon icon, boolean b, int n_adrtype) {
		super(icon);
		m_n_adrtype = n_adrtype;
	}
	@Override
	public boolean isSelected() {
		return bSelected;
	}
	@Override
	public void setSelected(boolean b) {
		bSelected = b;
		super.setSelected(b);
		setIcon(bSelected ? ico : icoGray);
	}
	public boolean toggleSelection()
	{
		bSelected = !bSelected;
		this.setSelected(bSelected);
		return bSelected;
	}
	public ToggleAddresstype(ImageIcon icon)
	{
		super(icon);
		m_n_adrtype = 0;
		setSelected(false);
	}
	public ToggleAddresstype()
	{
		super();
		m_n_adrtype = 0;
		setSelected(false);
	}
	@Override
	public void setIcon(Icon defaultIcon) {
		if(defaultIcon!=null && ico==null)
		{
			ico = (ImageIcon)defaultIcon;
			icoGray = (ImageIcon)defaultIcon; //ImageFetcher.makeBlurred(ico);//makeGrayscale(ico);
		}
		super.setIcon(defaultIcon);
	}
	@Override
	protected void paintComponent(Graphics g) {
		if(this.isSelected())
		{
			//this.setBorder(BorderFactory.createLineBorder(Color.black));
			//setIcon(ico);
		}
		else
		{
			//setIcon(icoGray);
			//this.setBorder(null);
		}
		super.paintComponent(g);
	}
}