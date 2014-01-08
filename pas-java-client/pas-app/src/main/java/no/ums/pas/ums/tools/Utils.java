package no.ums.pas.ums.tools;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.importer.gis.GISRecord;
import no.ums.pas.maps.defines.HouseItem;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.PolygonStruct;

import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.JTextComponent.KeyBinding;
import javax.swing.text.Keymap;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class Utils {

    private static final Log log = UmsLog.getLogger(Utils.class);

    public synchronized static Point get_dlg_location_centered(int dlg_width, int dlg_height) {
        return new Point(PAS.get_pas().get_mappane().getLocationOnScreen().x + PAS.get_pas().get_mappane().get_dimension().width / 2 - dlg_width / 2, PAS.get_pas().get_mappane().getLocationOnScreen().y /*+ PAS.get_pas().get_mappane().get_dimension().height/2 - dlg_height*/);
    }

    public synchronized static Calendar create_date(int n_date, int n_time) {
        String sz_date = Integer.toString(n_date);
        String sz_time = Integer.toString(n_time);
        return create_date(sz_date, sz_time);
    }

    public synchronized static Calendar create_date(String sz_date, String sz_time) {
        if (sz_date.length() != 8 || sz_time.length() <= 0)
            return null;
        sz_time = TextFormat.padding(sz_time, '0', 6);
        Calendar cal = Calendar.getInstance();

        cal.set(new Integer(sz_date.substring(0, 4)).intValue(),
                new Integer(sz_date.substring(4, 6)).intValue() - 1, //zero based month
                new Integer(sz_date.substring(6, 8)).intValue(),
                new Integer(sz_time.substring(0, 2)).intValue(),
                new Integer(sz_time.substring(2, 4)).intValue(),
                new Integer(sz_time.substring(4, 6)).intValue());
        return cal;
    }

    public synchronized static final String get_current_date_formatted() {
        return new java.text.SimpleDateFormat("yyyyMMdd").format(get_now().getTime());
    }

    public synchronized static final long get_current_datetime() {
        try {
            return new Long(new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(get_now().getTime())).longValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public synchronized static final Calendar get_now() {
        return Calendar.getInstance();
    }

    public synchronized static final int get_minute_difference(Calendar c1 /*old*/, Calendar c2 /*now*/) {
        return (int) ((c2.getTimeInMillis() - c1.getTimeInMillis()) / 60000);
    }

    public synchronized static final Dimension screendlg_upperleft(int n_width, int n_height) {
        Dimension dim = new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - n_width / 2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - n_width / 2);
        return dim;
    }

    public synchronized static final Dimension screendlg_upperleft(Dimension dlgsize) {
        return screendlg_upperleft(dlgsize.width, dlgsize.height);
    }

    public synchronized static final Dimension paswindow_upperleft(int w, int h) {
        return paswindow_upperleft(new Dimension(w, h));
    }

    public synchronized static final Dimension paswindow_upperleft(Dimension dlgsize) {

        return new Dimension((PAS.get_pas().getBounds().x + PAS.get_pas().getBounds().width / 2 - dlgsize.width / 2),
                (PAS.get_pas().getBounds().y + PAS.get_pas().getBounds().height / 2 - dlgsize.height / 2));
    }

    public synchronized PolygonStruct expandPolygon(Dimension mapsize, PolygonStruct in, double weight_x, double weight_y, int points, float expand, int expandtype) {
        PolygonStruct ret = null;
        try {
            ret = (PolygonStruct) in.clone();//new PolygonStruct(mapsize, in);
            for (int i = 0; i < ret.get_size(); i++) {
                double lat = ret.get_coor_lat(i);
                double lon = ret.get_coor_lon(i);

                if (expandtype == 0) //expand by meters
                {
                    //ignore weight point
                    NavStruct nav = in.calc_bounds();


                } else if (expandtype == 1) //expand by percent
                {
                    //calc distance in degrees from weight point
                    double delta_lat = lat - weight_y;
                    double delta_lon = lon - weight_x;
                    //move in percent
                    lat = lat + delta_lat * expand;
                    lon = lon + delta_lon * expand;
                    ret.set_at(i, lon, lat);
                }
            }

        } catch (Exception e) {

        }


        return ret;

    }

    public synchronized static boolean ConvertEllipseToPolygon(double centerx, double centery, double cornerx, double cornery, int steps, int angle, double precision, PolygonStruct poly) {
        int curStep = 0;

        if (steps < 3 || steps > 5000)
            return false;

        double a = Math.abs(cornerx - centerx);
        double b = Math.abs(cornery - centery);

        double beta = -angle * (Math.PI / 180.0);
        double sinbeta = Math.sin(beta);
        double cosbeta = Math.cos(beta);

        for (double i = 360; i > 0.0; i -= 360.0 / steps) {
            double alpha = i * (Math.PI / 180.0);
            double sinalpha = Math.sin(alpha);
            double cosalpha = Math.cos(alpha);

            //polygon[curStep, 0] = centerx + (a * cosalpha * cosbeta - b * sinalpha * sinbeta);
            //polygon[curStep, 1] = centery + (a * cosalpha * sinbeta + b * sinalpha * cosbeta);
            poly.add_coor(centerx + (a * cosalpha * cosbeta - b * sinalpha * sinbeta), centery + (a * cosalpha * sinbeta + b * sinalpha * cosbeta), true, precision, false);

            curStep++;
        }
        poly.finalizeShape();

        return true;
    }

    public static final long TEXT_FEATURE_COPY = 1;
    public static final long TEXT_FEATURE_PASTE = 2;
    public static final long TEXT_FEATURE_CUT = 4;

    public static void disableTextComponentFeature(JTextComponent c, long TextFeatures) {
        ArrayList<KeyBinding> arr = new ArrayList<KeyBinding>();
        if ((TextFeatures & TEXT_FEATURE_COPY) == TEXT_FEATURE_COPY) {
            arr.add(new JTextComponent.KeyBinding(
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK),
                    DefaultEditorKit.beepAction));
        }
        if ((TextFeatures & TEXT_FEATURE_CUT) == TEXT_FEATURE_CUT) {
            arr.add(new JTextComponent.KeyBinding(
                    KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK),
                    DefaultEditorKit.beepAction));
        }
        if ((TextFeatures & TEXT_FEATURE_PASTE) == TEXT_FEATURE_PASTE) {
            arr.add(new JTextComponent.KeyBinding(
                    KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK),
                    DefaultEditorKit.beepAction));
        }
        JTextComponent.KeyBinding[] newBindings = new JTextComponent.KeyBinding[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            newBindings[i] = arr.get(i);
        }
        /*JTextComponent.KeyBinding[] newBindings = {
                  new JTextComponent.KeyBinding(
                      KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK),
                      DefaultEditorKit.beepAction),
                  new JTextComponent.KeyBinding(
                      KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK),
                      DefaultEditorKit.beepAction),
                  new JTextComponent.KeyBinding(
                        KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK),
                        DefaultEditorKit.beepAction)
          };*/
        Keymap k = c.getKeymap();
        try {
            JTextComponent.loadKeymap(k, newBindings, c.getActions());
        } catch (Exception e) {

        }
    }

    public static String encrypt(String pw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            if (pw != null) {
            	try
            	{
            		md.update(pw.getBytes("UTF-8"));
            	}
            	catch(Exception e)
            	{
            		log.warn(e.getMessage(), e);
            	}
            }
            return getHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not find SHA-512 hasher", e);
        }
    }

    static final String HEXES = "0123456789abcdef";

    public static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }
    
    static final Pattern GSM_Alphabet_Regex = Pattern.compile("[|^{}\\[\\]~\\\\]");
	
	public static int get_gsmsize(String text) {
		Matcher m = GSM_Alphabet_Regex.matcher(text);
		int ext = 0;
		while(m.find() == true)
			++ext;
		return text.length()+ext;
	}

    /**
     * Helper for house select, converts house items to GisList
     *
     * @param setOfHouseItems this is the same object as used in house editor
     * @return a list ready for alert
     */
    public static GISList convertHouseItemsToGisList(final Set<ArrayList<HouseItem>> setOfHouseItems) {
        GISList gisList = new GISList();

        for (ArrayList<HouseItem> houseItems : setOfHouseItems) {

            for (HouseItem houseItem : houseItems) {

                ArrayList<Inhabitant> inhabitantsFromHouse = getInhabitantsFromHouse(houseItem);

                if (inhabitantsFromHouse.size() > 0) {
                    GISRecord gisRecord = new GISRecord(getStreetInfo(inhabitantsFromHouse.get(0)));
                    for (Inhabitant inhabitant : inhabitantsFromHouse) {
                        gisRecord.add_inhabitant(inhabitant);
                    }
                    gisList.add(gisRecord);
                }
            }
        }
        gisList.recalcBounds();
        return gisList;
    }

    // Gets detailed inhabitant objects

    /**
     * Method for getting detailed inhabitand objects. Used to populate address information.
     *
     * @param houseItem from house editor
     * @return inhabitant list (not basic info)
     */
    public static ArrayList<Inhabitant> getInhabitantsFromHouse(HouseItem houseItem) {
        ArrayList<Inhabitant> inhabitants = new ArrayList<Inhabitant>();

        for (int i=0; i< houseItem.get_inhabitantcount(); i++) {
            Inhabitant itemfromhouse = houseItem.get_itemfromhouse(i);
            inhabitants.add(itemfromhouse);
        }

        return inhabitants;
    }

    /**
     * Simplifier method to generate the needed info for creating gisrecord.
     *
     * @param inhabitant also from house editor
     * @return info to create gis record
     */
    public static String[] getStreetInfo(Inhabitant inhabitant) {

        String[] streetInfo = new String[6];

        streetInfo[0] = inhabitant.get_region(); // Municipal
        streetInfo[1] = String.valueOf(inhabitant.get_streetid());
        streetInfo[2] = inhabitant.get_no();
        streetInfo[3] = inhabitant.get_letter();
        streetInfo[4] = ""; // Namefilter 1
        streetInfo[5] = ""; // Namefilter 2

        return streetInfo;
    }

}