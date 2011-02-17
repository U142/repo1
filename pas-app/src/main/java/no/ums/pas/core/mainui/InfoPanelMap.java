/*
 * Created by JFormDesigner on Wed Feb 16 16:13:08 CET 2011
 */

package no.ums.pas.core.mainui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author User #1
 */
public class InfoPanelMap extends JPanel {
    public InfoPanelMap() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("no.ums.pas.localization.lang");
        headingXY = new JLabel();
        pixelX = new JLabel();
        pixelY = new JLabel();
        headingLonLat = new JLabel();
        lonTime = new JLabel();
        latTime = new JLabel();
        lonDegree = new JLabel();
        latDegree = new JLabel();
        utmHeading = new JLabel();
        utmNorthing = new JLabel();
        utmEasting = new JLabel();
        utmZone = new JLabel();
        mapDimHeading1 = new JLabel();
        mapDimPixelX = new JLabel();
        mapDimPixelY = new JLabel();
        mapDimHeading2 = new JLabel();
        mapDimMeterX = new JLabel();
        mapDimMeterY = new JLabel();
        downloadHeading = new JLabel();
        downloadStatus = new JLabel();

        //======== this ========
        setBorder(new TitledBorder(null, bundle.getString("main_infotab_map_information"), TitledBorder.LEADING, TitledBorder.TOP,
            UIManager.getFont("TitledBorder.font").deriveFont(UIManager.getFont("TitledBorder.font").getStyle() | Font.BOLD, UIManager.getFont("TitledBorder.font").getSize() + 1f)));

        //---- headingXY ----
        headingXY.setText(bundle.getString("main_infotab_xy_coors"));

        //---- headingLonLat ----
        headingLonLat.setText(bundle.getString("common_lon"));

        //---- utmHeading ----
        utmHeading.setText(bundle.getString("common_utm"));

        //---- mapDimHeading1 ----
        mapDimHeading1.setText(bundle.getString("main_infotab_mapdimensions"));

        //---- mapDimHeading2 ----
        mapDimHeading2.setText(bundle.getString("main_infotab_mapdimensions"));

        //---- downloadHeading ----
        downloadHeading.setText(bundle.getString("main_infotab_housedownload"));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(mapDimHeading1, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                        .addComponent(mapDimHeading2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(downloadHeading, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(utmHeading, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(headingLonLat, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(headingXY, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(12, 12, 12)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(pixelX, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(pixelY, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lonTime, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(latTime, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lonDegree, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(latDegree, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(utmNorthing, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(utmEasting, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(utmZone, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(mapDimPixelX, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(mapDimPixelY, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(mapDimMeterX, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(mapDimMeterY, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addComponent(downloadStatus, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(pixelY)
                        .addComponent(pixelX)
                        .addComponent(headingXY))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(latTime)
                                .addComponent(lonTime))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup()
                                .addComponent(latDegree)
                                .addComponent(lonDegree)))
                        .addComponent(headingLonLat))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(utmEasting)
                        .addComponent(utmZone)
                        .addComponent(utmNorthing)
                        .addComponent(utmHeading))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(mapDimPixelY)
                        .addComponent(mapDimPixelX)
                        .addComponent(mapDimHeading1))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(mapDimMeterY)
                        .addComponent(mapDimMeterX)
                        .addComponent(mapDimHeading2))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(downloadHeading)
                        .addComponent(downloadStatus)))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel headingXY;
    private JLabel pixelX;
    private JLabel pixelY;
    private JLabel headingLonLat;
    private JLabel lonTime;
    private JLabel latTime;
    private JLabel lonDegree;
    private JLabel latDegree;
    private JLabel utmHeading;
    private JLabel utmNorthing;
    private JLabel utmEasting;
    private JLabel utmZone;
    private JLabel mapDimHeading1;
    private JLabel mapDimPixelX;
    private JLabel mapDimPixelY;
    private JLabel mapDimHeading2;
    private JLabel mapDimMeterX;
    private JLabel mapDimMeterY;
    private JLabel downloadHeading;
    private JLabel downloadStatus;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
/*
 * Created by JFormDesigner on Wed Feb 16 16:13:08 CET 2011
 */

package no.ums.pas.core.mainui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author User #1
 */
public class InfoPanelMap extends JPanel {
    public InfoPanelMap() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("no.ums.pas.localization.lang");
        headingXY = new JLabel();
        pixelX = new JLabel();
        pixelY = new JLabel();
        headingLonLat = new JLabel();
        lonTime = new JLabel();
        latTime = new JLabel();
        lonDegree = new JLabel();
        latDegree = new JLabel();
        utmHeading = new JLabel();
        utmNorthing = new JLabel();
        utmEasting = new JLabel();
        utmZone = new JLabel();
        mapDimHeading1 = new JLabel();
        mapDimPixelX = new JLabel();
        mapDimPixelY = new JLabel();
        mapDimHeading2 = new JLabel();
        mapDimMeterX = new JLabel();
        mapDimMeterY = new JLabel();
        downloadHeading = new JLabel();
        downloadStatus = new JLabel();

        //======== this ========
        setBorder(new TitledBorder(null, bundle.getString("main_infotab_map_information"), TitledBorder.LEADING, TitledBorder.TOP,
            UIManager.getFont("TitledBorder.font").deriveFont(UIManager.getFont("TitledBorder.font").getStyle() | Font.BOLD, UIManager.getFont("TitledBorder.font").getSize() + 1f)));

        //---- headingXY ----
        headingXY.setText(bundle.getString("main_infotab_xy_coors"));

        //---- headingLonLat ----
        headingLonLat.setText(bundle.getString("common_lon"));

        //---- utmHeading ----
        utmHeading.setText(bundle.getString("common_utm"));

        //---- mapDimHeading1 ----
        mapDimHeading1.setText(bundle.getString("main_infotab_mapdimensions"));

        //---- mapDimHeading2 ----
        mapDimHeading2.setText(bundle.getString("main_infotab_mapdimensions"));

        //---- downloadHeading ----
        downloadHeading.setText(bundle.getString("main_infotab_housedownload"));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(mapDimHeading1, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                        .addComponent(mapDimHeading2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(downloadHeading, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(utmHeading, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(headingLonLat, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(headingXY, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(12, 12, 12)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(pixelX, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(pixelY, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lonTime, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(latTime, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lonDegree, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(latDegree, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(utmNorthing, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(utmEasting, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(utmZone, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(mapDimPixelX, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(mapDimPixelY, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(mapDimMeterX, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(mapDimMeterY, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
                        .addComponent(downloadStatus, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(pixelY)
                        .addComponent(pixelX)
                        .addComponent(headingXY))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(latTime)
                                .addComponent(lonTime))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup()
                                .addComponent(latDegree)
                                .addComponent(lonDegree)))
                        .addComponent(headingLonLat))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(utmEasting)
                        .addComponent(utmZone)
                        .addComponent(utmNorthing)
                        .addComponent(utmHeading))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(mapDimPixelY)
                        .addComponent(mapDimPixelX)
                        .addComponent(mapDimHeading1))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(mapDimMeterY)
                        .addComponent(mapDimMeterX)
                        .addComponent(mapDimHeading2))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(downloadHeading)
                        .addComponent(downloadStatus)))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel headingXY;
    private JLabel pixelX;
    private JLabel pixelY;
    private JLabel headingLonLat;
    private JLabel lonTime;
    private JLabel latTime;
    private JLabel lonDegree;
    private JLabel latDegree;
    private JLabel utmHeading;
    private JLabel utmNorthing;
    private JLabel utmEasting;
    private JLabel utmZone;
    private JLabel mapDimHeading1;
    private JLabel mapDimPixelX;
    private JLabel mapDimPixelY;
    private JLabel mapDimHeading2;
    private JLabel mapDimMeterX;
    private JLabel mapDimMeterY;
    private JLabel downloadHeading;
    private JLabel downloadStatus;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
