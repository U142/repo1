package no.ums.pas.send;

import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import no.ums.pas.PAS;
import no.ums.pas.area.voobjects.AddressFilterInfoVO;
import no.ums.pas.core.Variables;

@SuppressWarnings("rawtypes")
public class JComboCheckBox extends JComboBox {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    Set<JCheckBox> selectedItems = null;

    
   
    public JComboCheckBox() {    
        addItems();
    }

    @SuppressWarnings("unchecked")
    public JComboCheckBox(JCheckBox[] items) {
        super(items);     
        addItems();
    }

    private void addItems() {
    	
    	if(selectedItems == null)
    	{
    		selectedItems = new HashSet<JCheckBox>();
    	}
        setRenderer(new ComboBoxRenderer());

        addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (ae.getSource() instanceof JComboBox) {
					final JComboBox box = (JComboBox) ae.getSource();
					itemSelected();

					SwingUtilities.invokeLater(new Runnable() {

						public void run() {

							if(box.isVisible())
							{
								try {
									box.showPopup();
								} catch (IllegalComponentStateException e) {
									// Do nothing									
								}
							}
							
						}

					});
				}

			}
		});

	}
    
    ActionListener actionListener = null;
    public void setCallBackActionListner(ActionListener actionListener)
    {
        this.actionListener = actionListener; 
    }

    private void itemSelected() {
        if (getSelectedItem() instanceof JCheckBox) {
           
            boolean isSelected = false;
            JCheckBox jcb = (JCheckBox) getSelectedItem();
            isSelected = jcb.isSelected() ? false : true;            
            jcb.setSelected(isSelected);
            
            if(isSelected)
            {          
                addSelectedItem(jcb);  //for debugging purpose
            	no.ums.pas.area.voobjects.AddressFilterInfoVO selectedFilter = new no.ums.pas.area.voobjects.AddressFilterInfoVO();
                selectedFilter.setFilterName(getCheckBoxText());
                selectedFilter.setFilterId(Integer.valueOf(jcb.getActionCommand())); 
                actionListener.actionPerformed(new ActionEvent(selectedFilter, ActionEvent.ACTION_PERFORMED, "act_set_address_filter"));
            }
            else
            {
            	no.ums.pas.area.voobjects.AddressFilterInfoVO selectedAddressFilter = getFilterByName(getCheckBoxText());
                actionListener.actionPerformed(new ActionEvent(selectedAddressFilter, ActionEvent.ACTION_PERFORMED, "act_remove_address_filter"));
            }
        }
    }
    
    private AddressFilterInfoVO getFilterByName(String checkBoxText) {
    	
    	 ArrayList<AddressFilterInfoVO> addressFilterList = Variables.getFilterList();
    	 for(AddressFilterInfoVO filter :  addressFilterList){
    	
    		 if(checkBoxText!=null && checkBoxText.equals(filter.getFilterName())){
                       		 return filter;
    		 }
            }
    	 return new AddressFilterInfoVO();
      }

	public Set<JCheckBox> getSelectedItems()
    {
        return selectedItems;
        
    }
	
	//for debugging purpose
    public void addSelectedItem(JCheckBox jcb)
    {
        this.selectedItems.add(jcb);
        printSelected();
    }
    
   //for debugging purpose
    public void removeSelectedItem(JCheckBox jcb)
    {
        this.selectedItems.remove(jcb);
        printSelected();
    }
    
    public void printSelected()
    {
        System.out.println("");
        System.out.print("[");
        for(JCheckBox item : selectedItems)
        {            
            System.out.print(item.getText() + " ");
        }
        System.out.print("]");
    }
    
    public String getCheckBoxText() {
        JCheckBox jcb = (JCheckBox) getSelectedItem();
        return jcb.getText();
    }
    
    public AddressFilterInfoVO GetFilter(AddressFilterInfoVO filter,List<AddressFilterInfoVO>list)
    {
    	for (AddressFilterInfoVO filterInfoVO : list)
    	{
    		if(filter.getFilterName().equals(filterInfoVO.getFilterName()))
    		{
    			return filterInfoVO;
    		}
    	}
    	return new AddressFilterInfoVO();
	}
    public class ComboBoxRenderer implements ListCellRenderer {
        private JLabel defaultLabel;

        public ComboBoxRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            if(index == -1)
            {
             defaultLabel = new JLabel("-- Select Filter --");
             return defaultLabel;
            }
            if (value instanceof Component) {
                Component c = (Component) value;
                if (isSelected) {
                    c.setBackground(list.getSelectionBackground());
                    c.setForeground(list.getSelectionForeground());
                } else {
                    c.setBackground(list.getBackground());
                    c.setForeground(list.getForeground());
                }
                return c;
            } else {
                if (defaultLabel == null)
                    defaultLabel = new JLabel(value == null ? "Hardcoded text" : value.toString());
                else
                    defaultLabel.setText(value == null ? "Hardcoded text" : value.toString());
                return defaultLabel;
            }
        }
    }

}