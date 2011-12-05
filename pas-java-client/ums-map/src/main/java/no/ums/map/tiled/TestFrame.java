/*
 * Created by JFormDesigner on Wed Apr 27 13:51:53 CEST 2011
 */

package no.ums.map.tiled;

import no.ums.map.tiled.component.MapComponent;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import java.awt.Container;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author User #1
 */
public class TestFrame extends JFrame {
    public TestFrame() {
        initComponents();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final long totalMemory = Runtime.getRuntime().totalMemory();
                final long freeMemory = Runtime.getRuntime().freeMemory();
                final long usedMemory = totalMemory - freeMemory;
                lblMem.setText(String.format(Locale.ENGLISH, "%5.2fM of %5.2fM used", usedMemory / (Math.pow(1024, 2)), totalMemory / (Math.pow(1024, 2))));
                prgMem.getModel().setMinimum(0);
                prgMem.getModel().setMaximum((int) (totalMemory / 1024));
                prgMem.getModel().setValue((int) (usedMemory / 1024));
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    public JLabel getLblLat() {
        return lblLat;
    }

    public JLabel getLblLon() {
        return lblLon;
    }

    public MapComponent getMapComponent1() {
        return mapComponent1;
    }

    public JLabel getLblZoom() {
        return lblZoom;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblLat = new JLabel();
        lblLon = new JLabel();
        mapComponent1 = new MapComponent();
        lblZoom = new JLabel();
        prgMem = new JProgressBar();
        lblMem = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();

        //---- lblLat ----
        lblLat.setText("text");

        //---- lblLon ----
        lblLon.setText("text");

        //---- lblZoom ----
        lblZoom.setText("text");

        //---- lblMem ----
        lblMem.setText("text");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addComponent(prgMem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lblMem)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                            .addComponent(lblZoom)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lblLon)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lblLat))
                        .addComponent(mapComponent1, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(mapComponent1, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(lblLat)
                            .addComponent(lblLon)
                            .addComponent(lblZoom)
                            .addComponent(lblMem))
                        .addComponent(prgMem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblLat;
    private JLabel lblLon;
    private MapComponent mapComponent1;
    private JLabel lblZoom;
    private JProgressBar prgMem;
    private JLabel lblMem;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
