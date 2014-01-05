package no.ums.pas.send;

import no.ums.pas.PasApplication;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.importer.gis.GISRecord;
import no.ums.pas.maps.defines.HouseItem;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.InhabitantBasics;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: zzoebl
 * Date: 05/01/14
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
public class HouseSelectTest {
    public static void main(String[] args) {
        //PasApplication pasApp = PasApplication.init("https://secure.ums.no/pas/ws_pas/ws/");

        try {
            // Start with a houseItem list and convert it into a gislist for sending
            HouseItem houseItem = new HouseItem(5.723516941070557, 58.84983444213867);
            Inhabitant svein = new Inhabitant();
            svein.set_adrname("ANFINNSEN SVEIN");
            svein.set_postaddr("ALEXANDER KIELLANDS G. 18");
            svein.set_no("18");
            svein.set_postno("4319");
            svein.set_lon(5.723516941070557);
            svein.set_lat(58.84983444213867);
            svein.set_mobile("92293390");
            svein.set_kondmid("31464572");


            ArrayList<HouseItem> houseItems = new ArrayList<HouseItem>();
            houseItems.add(houseItem);


            GISList gisList = convertHouseItemsToGisList(houseItems);
            System.out.println(gisList);
        } catch (Exception e) {

        } finally {
            //pasApp.shutdown();
        }
    }

    static private GISList convertHouseItemsToGisList(final ArrayList<HouseItem> houseItems) {
        GISList gisList = new GISList();
        for (HouseItem houseItem : houseItems) {

            ArrayList<Inhabitant> inhabitantsFromHouse = getInhabitantsFromHouse(houseItem);

            if (inhabitantsFromHouse.size() > 0) {
                GISRecord gisRecord = new GISRecord(getStreetInfo(inhabitantsFromHouse.get(0)));
                gisList.add(gisRecord);
            }
        }

        return gisList;
    }

    // Gets detailed inhabitant objects
    static private ArrayList<Inhabitant> getInhabitantsFromHouse(HouseItem houseItem) {
        ArrayList<Inhabitant> inhabitants = new ArrayList<Inhabitant>();

        for (int i=0; i< houseItem.get_inhabitantcount(); i++) {
            Inhabitant itemfromhouse = houseItem.get_itemfromhouse(i);
            inhabitants.add(itemfromhouse);
        }

        return inhabitants;
    }

    static private String[] getStreetInfo(Inhabitant inhabitant) {

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
