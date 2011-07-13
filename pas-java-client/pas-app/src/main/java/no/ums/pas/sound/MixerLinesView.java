/*
 * Created by JFormDesigner on Tue Jun 07 11:42:27 CEST 2011
 */

package no.ums.pas.sound;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.*;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

/**
 * @author User #2
 */
public class MixerLinesView extends JDialog {
	private final MixerLinesController controller;
	

	public MixerLinesView(Window owner, MixerLinesController controller)
	{
		super(owner);
		this.controller = controller;
		initComponents();
	}
	
	public void showDlg()
	{
		mixerLinesModel1.setMixersAndLines(controller.queryMixersAndLines());
		this.setAlwaysOnTop(true);
		this.setVisible(true);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		scrollPane1 = new JScrollPane();
		textArea1 = new JTextArea();
		mixerLinesModel1 = new MixerLinesModel();

		//======== this ========
		setTitle("Sound mixers and lines");
		Container contentPane = getContentPane();

		//======== scrollPane1 ========
		{

			//---- textArea1 ----
			textArea1.setEditable(false);
			scrollPane1.setViewportView(textArea1);
		}

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
					.addContainerGap())
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
					.addContainerGap())
		);
		pack();
		setLocationRelativeTo(getOwner());

		//---- bindings ----
		bindingGroup = new BindingGroup();
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			textArea1, BeanProperty.create("text"),
			mixerLinesModel1, BeanProperty.create("mixersAndLines")));
		bindingGroup.bind();
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrollPane1;
	private JTextArea textArea1;
	private MixerLinesModel mixerLinesModel1;
	private BindingGroup bindingGroup;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
