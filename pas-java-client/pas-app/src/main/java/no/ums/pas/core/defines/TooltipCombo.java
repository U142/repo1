package no.ums.pas.core.defines;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ToolTipManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import no.ums.pas.send.BBProfile;


public class TooltipCombo extends JComboBox {
	public TooltipCombo()
	{
		setRenderer(new BasicComboBoxRenderer() {

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if(isSelected && value instanceof TooltipItem) {
					TooltipItem tmp = (TooltipItem)value;
					list.setToolTipText(tmp.toTooltipString());
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
			
		});
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	@Override
	public String getToolTipText() {
		Object sel = this.getSelectedItem();
		if(sel instanceof TooltipItem)
		{
			TooltipItem tmp = (TooltipItem)sel;
			return tmp.toTooltipString();
		}
		return null;
	}
	

}
