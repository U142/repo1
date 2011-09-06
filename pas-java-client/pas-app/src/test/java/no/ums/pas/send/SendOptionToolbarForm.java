/*
 * Created by JFormDesigner on Mon Sep 05 16:16:29 CEST 2011
 */

package no.ums.pas.send;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;

/**
 * @author User #2
 */
public class SendOptionToolbarForm extends JDialog {
	public SendOptionToolbarForm(Frame owner) {
		super(owner);
		initComponents();
	}

	public SendOptionToolbarForm(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		button1 = new JButton();
		textField1 = new JTextField();
		button2 = new JButton();
		button3 = new JButton();
		button4 = new JButton();
		button5 = new JButton();
		button6 = new JButton();
		button7 = new JButton();
		button8 = new JButton();
		button9 = new JButton();
		button10 = new JButton();
		button11 = new JButton();
		button12 = new JButton();
		button13 = new JButton();
		button14 = new JButton();
		button15 = new JButton();
		button16 = new JButton();
		button17 = new JButton();
		button18 = new JButton();
		button19 = new JButton();
		button20 = new JButton();
		button21 = new JButton();

		//======== this ========
		Container contentPane = getContentPane();

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

			//---- button1 ----
			button1.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/brush_disabled_16.png")));

			//---- button3 ----
			button3.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/send_polygon_24.png")));

			//---- button4 ----
			button4.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/send_ellipse_24.png")));

			//---- button5 ----
			button5.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/send_municipal_24.png")));

			//---- button6 ----
			button6.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/folder_open_24.png")));

			//---- button7 ----
			button7.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/find_24.png")));

			//---- button8 ----
			button8.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/lock_24.png")));

			//---- button9 ----
			button9.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/outbox_24.png")));

			//---- button10 ----
			button10.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/delete_24.png")));

			//---- button11 ----
			button11.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/phone_24.png")));

			//---- button12 ----
			button12.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/mobile_24.png")));

			//---- button13 ----
			button13.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/phone_24.png")));

			//---- button14 ----
			button14.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/mobile_24.png")));

			//---- button15 ----
			button15.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/flag_red_24.png")));

			//---- button16 ----
			button16.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/bandaid_24.png")));

			//---- button17 ----
			button17.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/lba_24.png")));

			//---- button18 ----
			button18.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/cap_24.png")));

			//---- button19 ----
			button19.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/facebook_24.png")));

			//---- button20 ----
			button20.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/twitter_24.png")));

			//---- button21 ----
			button21.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/atom_24.png")));

			GroupLayout dialogPaneLayout = new GroupLayout(dialogPane);
			dialogPane.setLayout(dialogPaneLayout);
			dialogPaneLayout.setHorizontalGroup(
				dialogPaneLayout.createParallelGroup()
					.addGroup(dialogPaneLayout.createSequentialGroup()
						.addGroup(dialogPaneLayout.createParallelGroup()
							.addGroup(dialogPaneLayout.createSequentialGroup()
								.addComponent(button1, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(textField1, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(button2, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
							.addGroup(dialogPaneLayout.createSequentialGroup()
								.addComponent(button11, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(button12, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(button13, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(button14, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(button15, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button16, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
						.addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(button3, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
							.addComponent(button7, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(dialogPaneLayout.createParallelGroup()
							.addComponent(button8, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
							.addComponent(button4, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(dialogPaneLayout.createParallelGroup()
							.addComponent(button9, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
							.addGroup(dialogPaneLayout.createSequentialGroup()
								.addGap(1, 1, 1)
								.addComponent(button5, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(dialogPaneLayout.createParallelGroup()
							.addComponent(button6, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
							.addComponent(button10, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)))
					.addGroup(dialogPaneLayout.createSequentialGroup()
						.addComponent(button17, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button18, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button19, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button20, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button21, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
						.addGap(282, 282, 282))
			);
			dialogPaneLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {button3, button4, button5, button6});
			dialogPaneLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {button1, button2});
			dialogPaneLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {button10, button7, button8, button9});
			dialogPaneLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {button11, button12, button13, button14, button15, button16, button17, button18, button19, button20, button21});
			dialogPaneLayout.setVerticalGroup(
				dialogPaneLayout.createParallelGroup()
					.addGroup(dialogPaneLayout.createSequentialGroup()
						.addGroup(dialogPaneLayout.createParallelGroup()
							.addGroup(dialogPaneLayout.createSequentialGroup()
								.addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(button5)
									.addComponent(button6, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(button9, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
									.addComponent(button10, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
							.addGroup(dialogPaneLayout.createSequentialGroup()
								.addGroup(dialogPaneLayout.createParallelGroup()
									.addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(button4)
										.addComponent(button3))
									.addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(button1)
										.addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(button2, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE)))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(dialogPaneLayout.createParallelGroup()
									.addComponent(button15, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
									.addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(button8, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
										.addComponent(button7, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
									.addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(button11, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
										.addComponent(button12, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
										.addComponent(button13, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
										.addComponent(button14, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
									.addComponent(button16, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(dialogPaneLayout.createParallelGroup()
							.addComponent(button17, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
							.addComponent(button18, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
							.addComponent(button19, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
							.addComponent(button20, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
							.addComponent(button21, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(52, Short.MAX_VALUE))
			);
			dialogPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[] {button3, button4, button5, button6});
			dialogPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[] {button1, button2});
			dialogPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[] {button10, button7, button8, button9});
			dialogPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[] {button11, button12, button13, button14, button15, button16, button17, button18, button19, button20, button21});
		}

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addComponent(dialogPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addComponent(dialogPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(44, Short.MAX_VALUE))
		);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JButton button1;
	private JTextField textField1;
	private JButton button2;
	private JButton button3;
	private JButton button4;
	private JButton button5;
	private JButton button6;
	private JButton button7;
	private JButton button8;
	private JButton button9;
	private JButton button10;
	private JButton button11;
	private JButton button12;
	private JButton button13;
	private JButton button14;
	private JButton button15;
	private JButton button16;
	private JButton button17;
	private JButton button18;
	private JButton button19;
	private JButton button20;
	private JButton button21;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
