package no.ums.pas;

import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.WmsLayer;
import no.ums.pas.core.ws.WSSaveUI;
import org.junit.Test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Svein Anfinnsen <sa@ums.no>
 */
public class WmsSaveTest {

    @Test
    public void testSaveLayers() {

        WSSaveUI saveUI = new WSSaveUI(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Noop
            }
        });

        final Settings settings = new Settings();
        setLayers(settings);


        String layerlist = "";

        Collections.sort(settings.getWmsLayers(), new Comparator<WmsLayer>() {
            public int compare(WmsLayer w1, WmsLayer w2) {
                if (w1.checked)
                    return -1;
                if (w2.checked)
                    return 1;
                return settings.getWmsLayers().indexOf(w1) - settings.getWmsLayers().indexOf(w2);
            }
        });

        for(WmsLayer l : settings.getWmsLayers())
        {
            if(l.checked) {
                layerlist+=l.toString() + ",";
            }
        }

        assertEquals("MetriaTatortPlus=1,", layerlist);


    }



    private void setLayers(Settings settings) {
        settings.setWmsLayers("MetriaTatortPlus=1, metria:Administrativagranser40_FK=0, " +
            "metria:Administrativagranser_FK=0, metria:Administrativasymboler_TRK=0, " +
            "metria:Administrativasymboler_TRKFK=0, metria:Adressnr_TAT=0, metria:AnlaggningLinje_TAT=0, " +
            "metria:Anlaggning_TAT=0, metria:Avverkningsanmalningar_SKS=0, metria:Barighet_NVDB=0, " +
            "metria:Bebyggelsesymboler2_TRK=0, metria:Bebyggelsesymboler2_TRKFK=0, " +
            "metria:Bebyggelsesymboler_TRK=0, metria:Bebyggelsesymboler_TRKFK=0, metria:Biotopskydd_SKS=0, " +
            "metria:ByggnadOffentlig_TAT=0, metria:ByggnaderOvriga_FK=0, metria:ByggnaderOvrigaanl_TRK=0, " +
            "metria:ByggnaderOvrigaanl_TRKFK=0, metria:Byggnader_FK=0, metria:Byggnader_TAT=0, " +
            "metria:Byggnader_TRK=0, metria:Byggnader_TRKFK=0, metria:Byggnader_VAG=0, " +
            "metria:Byggnader_VAGS=0, metria:Byggnader_VAGSFK=0, metria:DjurVaxtskydd_NV=0, " +
            "metria:EI_DRK=0, metria:EL_DRK=0, metria:EO_DRK=0, metria:EP_DRK=0, metria:ES_DRK=0, " +
            "metria:ET_DRK=0, metria:EY_DRK=0, metria:EkonomiskEnhet_SVKY=0, " +
            "metria:Fastighetskartan_raster=0, metria:Fastighetspunkter_FK=0, " +
            "metria:Fastighetstextblockenhet40_FK=0, metria:Fastighetstextblockenhet_FK=0, " +
            "metria:Fastighetstextstad_FK=0, metria:Fastighetstexttrakt40_FK=0, " +
            "metria:Fastighetstexttrakt_FK=0, metria:Fastighetsytor40_FK=0, " +
            "metria:FastighetsytorAltB_FK=0, metria:Fastighetsytor_FK=0, metria:Fastighetsytor_FK_stor=0, " +
            "metria:Fjallnaraskog_linje_SKS=0, metria:Fjallnaraskog_yta_SKS=0, metria:Flyttf_fladderm_NSF=0, " +
            "metria:Fornlamning_TRK=0, metria:Fornlamning_TRKFK=0, metria:Fornlamning_linje_FMIS=0, " +
            "metria:Fornlamning_punkt_FMIS=0, metria:Fornlamning_yta_FMIS=0, metria:Fornlamningar_FK=0, " +
            "metria:Fornlamningssymboler_TRK=0, metria:Fornlamningssymboler_TRKFK=0, metria:Forsamling_SVKY=0, " +
            "metria:Framkomlighet_NVDB=0, metria:Funktionsvagklass_NVDB=0, metria:GSD_ORTNAMN=0, " +
            "metria:GSD_ORTNAMN_LNKN_UCASE=0, metria:GV_N50_VEG_L=0, metria:Geodesisymboler_TRK=0, " +
            "metria:Geodesisymboler_TRKFK=0, metria:Granser_OVK=0, metria:Granser_SVK=0, metria:Granser_TRK=0, " +
            "metria:Granser_TRKFK=0, metria:Granser_VAG=0, metria:Granser_VAGFK=0, metria:Hav_OVK=0, " +
            "metria:Hojdkurvor_FK=0, metria:Hojdkurvor_OVK=0, metria:Hojdkurvor_TRK=0, metria:Hojdkurvor_VAG=0, " +
            "metria:Hydrografisymboler_TRK=0, metria:Hydrografisymboler_TRKFK=0, metria:IBA_NV=0, " +
            "metria:Jarnvagar_FK=0, metria:Jarnvagar_OVK=0, metria:Jarnvagar_SVK=0, metria:Jarnvagar_TAT=0, " +
            "metria:Jarnvagar_TRK=0, metria:Jarnvagar_TRKFK=0, metria:Jarnvagar_VAG=0, metria:Jarnvagar_VAGS=0, " +
            "metria:Jarnvagar_VAGSFK=0, metria:Jarnvagsymboler_TRK=0, metria:Jarnvagsymboler_TRKFK=0, " +
            "metria:Kommunnamn_SVK=0, metria:Kontrakt_SVKY=0, metria:Kraftledningar_FK=0, " +
            "metria:Kraftledningar_OVK=0, metria:Kraftledningar_TRK=0, metria:Kraftledningar_TRKFK=0, " +
            "metria:Kraftledningar_VAG=0, metria:Kraftledningar_VAGS=0, metria:Kraftledningar_VAGSFK=0, " +
            "metria:Lan_SVK=0, metria:Landskapsbildsskydd_NV=0, metria:Markslag_linje_FK=0, " +
            "metria:Metadata_orto=0, metria:MilitaraOmraden_TRK=0, metria:MilitaraOmraden_TRKFK=0, " +
            "metria:Militaraomraden_FK=0, metria:Militaraomraden_OVK=0, metria:Militaraomraden_TRK=0, " +
            "metria:Militaraomraden_TRKFK=0, metria:Militaraomraden_VAG=0, metria:Militaraomraden_VAGS=0, " +
            "metria:Militaraomraden_VAGSFK=0, metria:Myrskyddsplan_NV=0, metria:N50_ADMIN_OMR_F=0, " +
            "metria:N50_ANLEGG_L=0, metria:N50_BANE_L=0, metria:N50_BEKK_L=0, metria:N50_BYGG_P=0, " +
            "metria:N50_GRUNNRISSBYGG_F=0, metria:N50_HOYDEKURVE_L=0, metria:N50_MARKSLAG_F=0, " +
            "metria:N50_STEDSNAVN_P=0, metria:N50_VANN_F=0, metria:N50_VEGBOM_P=0, inspire:NVR_psSiteS=0, " +
            "metria:NamnVatten_OVK=0, metria:Nationalpark_NV=0, metria:Nationalparksplan_NV=0, " +
            "metria:Natura2000_SNV=0, metria:Naturminne_NV=0, metria:Naturreservat_NV=0, metria:Naturvard_FK=0, " +
            "metria:Naturvard_OVK=0, metria:Naturvard_SVK=0, metria:Naturvard_TRK=0, metria:Naturvard_TRKFK=0, " +
            "metria:Naturvard_VAG=0, metria:Naturvard_VAGS=0, metria:Naturvard_VAGSFK=0, " +
            "metria:Naturvard_yta_FK=0, metria:Naturvarden_SKS=0, metria:Naturvardsavtal_SKS=0, " +
            "metria:Naturvardsomrade_NV=0, metria:Naturvardssymboler_TRK=0, metria:Naturvardssymboler_TRKFK=0, " +
            "metria:Nyckelbiotoper_SKS=0, metria:Obrutetfjall_NV=0, metria:Osi_karta=0, " +
            "metria:Osi_punkt_AC_SKS=0, metria:Osi_punkt_BD_SKS=0, metria:Osi_punkt_YZ_SKS=0, " +
            "metria:PL_DRK=0, metria:PP_DRK=0, metria:PY_DRK=0, metria:Pastorat_SVKY=0, metria:Polcirkel_TRK=0, " +
            "metria:Polcirkel_TRKFK=0, metria:RI_DRK=0, metria:RL_DRK=0, metria:RO_DRK=0, metria:RP_DRK=0, " +
            "metria:RS_DRK=0, metria:RT_DRK=0, metria:RY_DRK=0, metria:Ramsar_NV=0, metria:Ruin_TAT=0, " +
            "metria:SE_orto=0, metria:Sankmark_FK=0, metria:Sankmark_TRK=0, metria:Sankmark_TRKFK=0, " +
            "metria:Sjoar_OVK=0, metria:Skoghojdraster=0, metria:Skoghojdraster_intensitet=0, " +
            "metria:Skogohistoria_linje_SKS=0, metria:Skogohistoria_punkt_SKS=0, metria:Skogohistoria_yta_SKS=0, " +
            "metria:Skogskarta_kluster_NSF=0, metria:Skogskarta_stopp_NSF=0, metria:Snus_NV=0, " +
            "metria:Spot2008=0, metria:Spot2009=0, metria:Spot2010=0, metria:Spot2011=0, metria:Spot2012=0, " +
            "metria:Stationer_TAT=0, metria:Stift_SVKY=0, metria:Stroskogsymboler_TRK=0, " +
            "metria:Stroskogsymboler_TRKFK=0, metria:Sumpskogar_SKS=0, metria:Svarforyngradskog_SKS=0, " +
            "metria:SveaEkopark_NSF=0, metria:Symboler_FK=0, metria:Symboler_OVK=0, metria:Symboler_SVK=0, " +
            "metria:Symboler_VAG=0, metria:Symboler_VAGS=0, metria:Symboler_VAGSFK=0, metria:Tatorter=0, " +
            "metria:Tatorter_OVK=0, metria:Tatortsnamn_OVK=0, metria:Tatortspunkter_SVK=0, " +
            "metria:Terrangkartan_raster=0, metria:Terrangkartan_raster_rt90=0, metria:Text_FK=0, " +
            "metria:Text_OVK=0, metria:Text_SVK=0, metria:Text_TAT=0, metria:Text_TRK=0, metria:Text_TRKFK=0, " +
            "metria:Text_VAG=0, metria:Text_VAGS=0, metria:Text_VAGSFK=0, metria:Tillganglighet_NVDB=0, " +
            "metria:TradBuskrida_TRK=0, metria:TradBuskrida_TRKFK=0, metria:Transformatorsymboler_TRK=0, " +
            "metria:Transformatorsymboler_TRKFK=0, metria:Tuva_NV=0, metria:VagarOvriga_FK=0, " +
            "metria:VagarOvriga_TAT=0, metria:VagarOvriga_TRK=0, metria:VagarOvriga_TRKFK=0, " +
            "metria:VagarOvriga_VAG=0, metria:Vagar_FK=0, metria:Vagar_OVK=0, metria:Vagar_SVK=0, " +
            "metria:Vagar_TAT=0, metria:Vagar_TATFK=0, metria:Vagar_TRK=0, metria:Vagar_TRKFK=0, " +
            "metria:Vagar_VAG=0, metria:Vagar_VAGS=0, metria:Vagar_VAGSFK=0, metria:Vaghallare_NVDB=0, " +
            "metria:Vaghinder_NVDB=0, metria:Vagkartan_raster=0, metria:Vagnamn_NVDB=0, metria:Vagnat_NVDB=0, " +
            "metria:Vagnummer_OVK=0, metria:Vagnummer_SVK=0, metria:Vagnummer_TRK=0, metria:Vagnummer_TRKFK=0, " +
            "metria:Vagnummer_VAG=0, metria:Vagnummer_VAGS=0, metria:Vagnummer_VAGSFK=0, " +
            "metria:VagsymbolRastplats_TRK=0, metria:VagsymbolRastplats_TRKFK=0, metria:Vagsymboler_TRK=0, " +
            "metria:Vagsymboler_TRKFK=0, metria:Vatten_S1P=0, metria:Vattendrag_FK=0, metria:Vattendrag_OVK=0, " +
            "metria:Vattendrag_SVK=0, metria:Vattendrag_TAT=0, metria:Vattendrag_TRK=0, " +
            "metria:Vattendrag_TRKFK=0, metria:Vattendrag_VAG=0, metria:Vattendrag_VAGS=0, " +
            "metria:Vattendrag_VAGSFK=0, metria:Ytor_FK=0, metria:Ytor_OVK=0, metria:Ytor_S1P=0, " +
            "metria:Ytor_SVK=0, metria:Ytor_TAT=0, metria:Ytor_TRK=0, metria:Ytor_TRKFK=0, metria:Ytor_VAG=0, " +
            "metria:Ytor_VAGS=0, metria:Ytor_VAGSFK=0, metria:buildings=0, ext1:dafeeb=0, ext1:dafie=0, " +
            "ext1:dafieb=0, ext1:dafiei=0, ext1:dafiel=0, ext1:dafies=0, ext1:dafiet=0, ext1:dafiki=0, " +
            "ext1:dafiko=0, ext1:dafini=0, ext1:dafino=0, ext1:dafinres=0, ext1:dafinret=0, ext1:dafires=0, " +
            "ext1:dafiret=0, ext1:daswdi=0, ext1:daswdo=0, ext1:daswdres=0, ext1:daswdret=0, ext1:daswe=0, " +
            "ext1:dasweb=0, ext1:daswei=0, ext1:daswel=0, ext1:daswes=0, ext1:daswfi=0, ext1:daswfo=0, " +
            "ext1:daswfres=0, ext1:daswfret=0, ext1:daswni=0, ext1:daswno=0, ext1:daswnres=0, ext1:daswnret=0, " +
            "metria:hojd50=0, metria:hojd50farg=0, ext1:orto_color_bing=0, metria:orto_farg=0, " +
            "metria:orto_farg_25=0, metria:orto_norr=0, metria:orto_norrref=0, metria:orto_rt90=0, " +
            "metria:orto_skane=0, metria:orto_svartvit=0, metria:sweden_estate_borders=0, " +
            "metria:sweden_estates_text=0, metria:vfall_kommunikation=0, metria:vfall_slant=0, " +
            "MetriaFastighet40=0, MetriaFastighetOver=0, MetriaFastighetOver1=0, MetriaFastighetOver2=0, " +
            "MetriaFastighetOver3=0, MetriaFastighetOver_TW=0, MetriaFastighetPlus=0, MetriaFastighetSGU=0, " +
            "MetriaFastighetSkaC=0, MetriaFastighetSkaD=0, MetriaFastighetUnder=0, MetriaInfoV=0, " +
            "MetriaRattigheter=0, MetriaSE=0, MetriaSKS=0, MetriaSVKY=0, MetriaTatortPlus_SDC=0, " +
            "MetriaTatortVTD=0, Metria_vf_adr=0, Metria_vf_fast=0, Metria_vf_orto=0, N50=0, " +
            "Naturskyddsforeningen=0, Naturvardsverket=0, PlanRattBest=0, Sverige=0, Tredjepartsdata=0, " +
            "Tredjepartsdata_splan=0, dabkgr=0, dabus=0, daswae=0, daswaeb=0, daswalt=0");

        settings.getWmsLayers();
    }

}
