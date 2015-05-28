package no.ums.pas.importer.gis;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.voobjects.AddressFilterInfoVO;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.core.ws.GISFilterList;
import no.ums.pas.core.ws.WSGisFilterImport;
import no.ums.pas.core.ws.WSGisFilterImport.GisColumnsetStreetid;
import no.ums.pas.core.ws.WSGisFilterImport.GisCuNorway;
import no.ums.pas.core.ws.WSGisFilterImport.GisCuSweden;
import no.ums.pas.core.ws.WSGisFilterImport.GisVbBanken;
import no.ums.pas.importer.gis.PreviewList.ComboAdrid;
import no.ums.pas.importer.gis.PreviewList.ComboField;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.addressfilters.AddressType;

public class PreviewAddressPanel extends DefaultPanel implements ComponentListener, ChangeListener {

    private static final Log log = UmsLog.getLogger(PreviewAddressPanel.class);

    public static final long serialVersionUID = 1;
    private PreviewList m_previewlist;
    public PreviewList get_previewlist() { return m_previewlist; }
    private LoadingPanel m_loader;
    public LoadingPanel get_loader() { return m_loader; }
    public JButton m_btn_cancel;
    private JTabbedPane m_tab;
    private PreviewAddressFrame m_parent;
    private String addressType;
    private StatisticsAddressPanel  m_Panel;
    //test
    JCheckBox m_check_firstline_columnnames;
    public void setM_resultpanel(GISResultPanel m_resultpanel) {
        this.m_resultpanel = m_resultpanel;
    }

    private GISResultPanel m_resultpanel = null;
    public GISResultPanel get_resultpanel() { return m_resultpanel; }
    private SendObject m_sendobject;
    private ActionListener m_callbackframe;
    private GISFile m_gis;
    private String m_encoding = Localization.l("importpreview_encoding_iso_8859_15");
    public void setEncoding(String encoding) { m_encoding = encoding; }

    //Import Type
    private String m_import_type = "Street";
    
    //test start
    private String m_import_street_address = "StreetAddr";
    private String m_import_CUN = "CUNorway";
    private String m_import_CUS = "CUSweden";
    private String m_import_Vb = "Vbanken";

       
    public String getM_import_Vb() {
		return m_import_Vb;
	}

	public void setM_import_Vb(String m_import_Vb) {
		this.m_import_Vb = m_import_Vb;
	}

	public String getM_import_CUS() {
		return m_import_CUS;
	}

	public void setM_import_CUS(String m_import_CUS) {
		this.m_import_CUS = m_import_CUS;
	}
	
    public String getM_import_CUN() {
		return m_import_CUN;
	}

	public void setM_import_CUN(String m_import_CUN) {
		this.m_import_CUN = m_import_CUN;
	}

	public String getM_import_street_address() {
		return m_import_street_address;
	}

	public void setM_import_street_address(String m_import_street_address) {
		this.m_import_street_address = m_import_street_address;
	}
	
	public String getM_import_type() {
        return m_import_type;
    }

    public void setM_import_type(String m_import_type) {
        this.m_import_type = m_import_type;
    }

    public boolean isProperty(){
        return "Property".equals(getM_import_street_address()) ;
    }

    public boolean isStreetApartment(){
        return "StreetApartment".equals(getM_import_street_address()) ;
    }

    public boolean isStreet(){
        return "Street".equals(getM_import_street_address()) ;
    }

    
    String [] sz_columns = new String[] {
            Localization.l("common_number"),
            Localization.l("importpreview_municipalid"),
            Localization.l("importpreview_streetid"),
            Localization.l("importpreview_houseno"),
            Localization.l("importpreview_letter"),
            Localization.l("importpreview_namefilter1"),
            Localization.l("importpreview_namefilter2"),
            Localization.l("common_adr_name"),
            Localization.l("common_adr_phone"),
            Localization.l("common_adr_mobile"),
            Localization.l("common_adr_postno"),
            Localization.l("common_adr_postplace"),
            "",
            Localization.l("adrsearch_dlg_hit")+"%",
            Localization.l("common_longitude"),
            Localization.l("common_latitude")
    };

    String [] sz_columns_property = new String[] {
            Localization.l("common_number"),
            Localization.l("importpreview_municipalid"),
            Localization.l("importpreview_gnr"),
            Localization.l("importpreview_bnr"),
            Localization.l("importpreview_fnr"),
            Localization.l("importpreview_namefilter1"),
            Localization.l("importpreview_namefilter2"),
            Localization.l("common_adr_name"),
            Localization.l("common_adr_phone"),
            Localization.l("common_adr_mobile"),
            Localization.l("common_adr_postno"),
            Localization.l("common_adr_postplace"),
            "",
            Localization.l("importpreview_snr"),
            Localization.l("adrsearch_dlg_hit")+"%",
            Localization.l("common_longitude"),
            Localization.l("common_latitude")
    };
    int [] n_col_width_property = new int [] { 20, 30, 30, 20, 10, 60, 60, 60, 30, 30, 20, 30,20,20,20,20, 16, 16, 20, 20 };
    int [] n_col_width = new int [] { 20, 30, 30, 20, 10, 60, 60, 60, 30, 30, 20, 30, 16, 16, 20, 20 };

    public PreviewAddressPanel(SendObject so, boolean b_results_only, ActionListener callbackframe, String encoding) {
        super();
        m_encoding = encoding;
        m_sendobject = so;
        m_callbackframe = callbackframe;

        if(b_results_only) {

            if(m_resultpanel == null)
            {
                if(isProperty())  {
                    m_resultpanel = new GISResultPanel(this, so.get_sendproperties().typecast_gis().get_gislist(), sz_columns_property, n_col_width_property, new Dimension(600, 300));
                }
                else{
                    m_resultpanel = new GISResultPanel(this, so.get_sendproperties().typecast_gis().get_gislist(), sz_columns, n_col_width, new Dimension(600, 300));
                }

            }
            //Find out
            m_resultpanel.fill(so.get_sendproperties().typecast_gis().get_gislist(), getM_import_type(),false);
            addComponentListener(this);
            m_tab = new JTabbedPane();
            if(m_loader==null)
                m_loader = new LoadingPanel(Localization.l("importpreview_loading_content"), new Dimension(100, 40), true);
            init();

        } else {
            m_loader = new LoadingPanel(Localization.l("importpreview_loading_content"), new Dimension(100, 40), true);
            so.get_toolbar().showLoader(m_loader);
        }
    }

    public GISResultPanel create_property_result_panel(){
        log.info("Result",sz_columns_property.length)   ;
        return new GISResultPanel(this, m_gis, sz_columns_property, n_col_width_property, new Dimension(600, 300));
    }

    public PreviewAddressPanel(GISFile gis, ActionListener callbackframe) {
        super();
        m_callbackframe = callbackframe;
        m_previewlist = new PreviewList(this, gis, null);
        m_loader = new LoadingPanel(Localization.l("importpreview_loading_content"), new Dimension(100, 40), true);
        m_gis = gis;
        if(isProperty())   {
            createPropertyResultPanel(gis);
        }
        else{
            createStreetResultPanel(gis);

        }

        addComponentListener(this);
        m_tab = new JTabbedPane();
        init();
    }
     void createStreetResultPanel(GISFile gis){
       int [] n_col_width = new int [] { 20, 30, 30, 20, 10, 60, 60, 60, 30, 30, 20, 30, 16, 16, 20, 20 };
        m_resultpanel = new GISResultPanel(this, gis, sz_columns, n_col_width, new Dimension(400, 300));

    }
    void createPropertyResultPanel(GISFile gis){
        int [] n_col_width_property = new int [] { 20, 30, 30, 20, 10, 60, 60, 60, 30, 30, 20, 30,20,20,20,20, 16, 16, 20, 20 };
        m_resultpanel = new GISResultPanel(this, gis, sz_columns_property, n_col_width_property, new Dimension(400, 300));

    }
    protected void CreateGISResultPanel(GISList gis)
    {
        int [] n_col_width = new int [] { 20, 30, 30, 20, 10, 60, 60, 60, 30, 30, 20, 30, 16, 16, 20, 20 };

        int [] n_col_width_property = new int [] { 20, 30, 30, 20, 10, 60, 60, 60, 30, 30, 20, 30,20,20,20,20, 16, 16, 20, 20 };
        if("Property".equals(getM_import_type()))
        {
            m_resultpanel = new GISResultPanel(this, gis, sz_columns_property, n_col_width_property, new Dimension(400, 300));
        }
        else{
            m_resultpanel = new GISResultPanel(this, gis, sz_columns, n_col_width, new Dimension(400, 300));
        }

        m_tab = new JTabbedPane();
    }

    public void init() {
        add_controls();
        setVisible(true);
      
    }

    public void reSize(int x, int y) {
        x-=10;
        y-=10;
        setPreferredSize(new Dimension(x, y));
        m_tab.setPreferredSize(new Dimension(x, y));
        revalidate();
        resize_children(x, y);
    }
    public void resize_children(int x, int y) {
        m_previewlist.reSize(x, y);
        if(get_resultpanel()!=null)
            m_resultpanel.reSize(x, y);
    }

    public void actionPerformed(final ActionEvent e) {
        int n_skiplines = 0;
        String sz_separator = "";

        if(get_previewlist()!=null) {
            n_skiplines		= (get_previewlist().isFirstlineHeading() ? 1 : 0);
            sz_separator = get_previewlist().get_gis().get_parser().get_separator();
        }

        if("act_fetch_addresses".equals(e.getActionCommand())) { //actionevent from PreviewOptions.JButton => PreviewFrame

            try {
                String sz_sitename;
                try {
                    sz_sitename = PAS.get_pas().get_sitename();
                } catch(Exception err) {
                    sz_sitename = "http://vb4utv/";
                    Error.getError().addError("PreviewAddressPanel","Exception in actionPerformed",err,1);
                }
                
                File f_post = create_file();
                
                WSGisFilterImport gis = new WSGisFilterImport(this, "act_gis_imported", m_loader, m_encoding, getAddressType());
                gis.setImportFilter(true);
                
                if("import_addr_street".equals(getAddressType())){
                    GisColumnsetStreetid colset = gis.newGisColumnsetStreetid(0, 1, 2, 3, 4, 5, sz_separator, f_post);
                    gis.setColSet(colset);
                }  else if("import_addr_CUNorway".equalsIgnoreCase(getAddressType())){
                	GisCuNorway colset = gis.newGisCuNorway(0, 1, 2, 3, 4, 5, 6, 7, sz_separator, f_post);
                    gis.setColSet(colset);
                } else if("import_addr_CUSweden".equalsIgnoreCase(getAddressType())){
                	GisCuSweden colset = gis.newGisCuSweden(0, 1, 2, 3, 4, 5, sz_separator, f_post);
                    gis.setColSet(colset);
                } else if("import_addr_VABanken".equalsIgnoreCase(getAddressType())){
                	GisVbBanken	 colset = gis.newGisVbBanken(0, 1, 2, 3, 4, 5, sz_separator, f_post);
                    gis.setColSet(colset);
                }
                gis.start();
            } catch(Exception err) {
                log.debug(err.getMessage());
                log.warn(err.getMessage(), err);
                Error.getError().addError("PreviewAddressPanel","Exception in actionPerformed",err,1);
            }

        }
        else if("act_autofetch_addresses".equals(e.getActionCommand())) {
            try {
                String sz_sitename;
                try {
                    sz_sitename = PAS.get_pas().get_sitename();
                } catch(Exception err) {
                    sz_sitename = "http://vb4utv/";
                    Error.getError().addError("PreviewAddressPanel","Exception in actionPerformed",err,1);
                }
                File f_post = (File)e.getSource();

                WSGisFilterImport gis = new WSGisFilterImport(this, "act_gis_imported", m_loader, m_encoding, getAddressType());
                gis.start();

            } catch(Exception err) {
                log.debug(err.getMessage());
                log.warn(err.getMessage(), err);
                Error.getError().addError("PreviewAddressPanel","Exception in actionPerformed",err,1);
            }
        }
        else if("act_gis_imported".equals(e.getActionCommand())) {
            log.debug("GIS Download complete");
            final ActionEvent e2 = e;
            boolean b_dofill_results = false;
           
            AddressFilterInfoVO addressfilterVO= PAS.get_pas().getPredefinedFilterController().getFilterCtrl().getCurrentFilter();
            
            if(addressfilterVO==null){
            PAS.get_pas().getPredefinedFilterController().getFilterCtrl().setCurrentFilter(new AddressFilterInfoVO());
            addressfilterVO = PAS.get_pas().getPredefinedFilterController().getFilterCtrl().getCurrentFilter();
            }
            List filterlist= (GISFilterList)e2.getSource();
            GISFilterList finallist = addressfilterVO.getGisFilterList();
            if(finallist!=null){
            	finallist.addAll(filterlist);	
            }else{
            	finallist=new GISFilterList();
            	finallist.addAll(filterlist);
            }
            addressfilterVO.setGisFilterList(finallist);
            m_parent.setFinishBtnEnable(true);
            m_parent.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_statistics_view"));
            m_parent.repaint();
            m_parent.setFinishBtnEnable(true);
            m_parent.repaint();
            if("import_addr_street".equals(getAddressType())){
            	addressfilterVO.setAddressType(AddressType.STREET_ADDRESS);
            }else if("import_addr_CUNorway".equals(getAddressType())){
            	addressfilterVO.setAddressType(AddressType.CADASTRAL_UNIT_NORWAY);
            }else if("import_addr_CUSweden".equals(getAddressType())){
            	addressfilterVO.setAddressType(AddressType.CADASTRAL_UNIT_SWEDEN);
            }else if("import_addr_VABanken".equals(getAddressType())){
            	addressfilterVO.setAddressType(AddressType.VA_BANKEN_SWEDEN);
            }
            
        }
        else if("act_gis_finish".equals(e.getActionCommand())) { //no results (for alert only)
            File f_post = create_file();
            GISList gislist = create_gislist(f_post, sz_separator,m_parent.getAddressTypeSelected());
            get_previewlist().get_gis().get_callback().actionPerformed(new ActionEvent(gislist, ActionEvent.ACTION_PERFORMED, "act_gis_imported"));

        }
        else if("act_set_fieldid".equals(e.getActionCommand())) { //callback from previewlist. send namefilter columns to results
        	ComboField field = (ComboField)((ComboAdrid)e.getSource()).getSelectedItem();
            if(field.get_id()==PreviewList.ComboField.FIELDID_NAMEFILTER_INCLUSIVE_1 || field.get_id()==PreviewList.ComboField.FIELDID_NAMEFILTER_INCLUSIVE_2) {
                get_resultpanel().actionPerformed(e);
            }
        }
        else if("act_show_results".equals(e.getActionCommand())) {
            m_tab.addTab("Results", null,
                    m_resultpanel,
                    "GIS Import results");
            get_resultpanel().fill((GISList)e.getSource(),getM_import_type(), true);
            get_resultpanel().componentResized(null);
            m_tab.setSelectedComponent(get_resultpanel());
        }
        else if("act_update_statistics".equals(e.getActionCommand())) {
            m_callbackframe.actionPerformed(e); //report to frame, it will report to statisticspanel
        }
        else if("act_goto_next_valid".equals(e.getActionCommand())) {
            m_callbackframe.actionPerformed(e);
        }

    }

    public File create_file(){
        int n_col_municipal = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID);
        int n_col_streetid	= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_STREETID);
        int n_col_houseno	= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_HOUSENO);
        int n_col_letter	= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_LETTER);
        int n_col_apartment	= get_previewlist().get_column_bytype(ComboField.FIELDID_APARTMENTID);
        int n_col_namefilter1 = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_NONAMEFILTER_INCLUSIVE_1);
        int n_col_namefilter2 = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_NONAMEFILTER_INCLUSIVE_2);
        
        int n_skiplines		= (get_previewlist().isFirstlineHeading() ? 1 : 0);
        // stuff for import property
        int n_col_gnr	= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_GNR);
        int n_col_bnr = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_BNR);
        int n_col_fnr= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_FNR);
        int n_col_snr		= get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_SNR);
        int n_col_unr  = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_UNR);
        int n_col_fastighetsnyckel = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_Fastighetsnyckel);
        int n_col_vbanken = get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_VB);
        
        File f_post= null;

        String sz_separator = get_previewlist().get_gis().get_parser().get_separator();
        log.debug(n_col_municipal + " " + n_col_streetid + " " + n_col_houseno + " " + n_col_letter + " (skip:" + n_skiplines + " sep:" + sz_separator + ")");
        try {

            String filename = "tmpfile.txt";
            if(get_previewlist().get_gis().get_parser().get_file()!=null)
            {
                filename = get_previewlist().get_gis().get_parser().get_file().getName();
            }
            else if(get_previewlist().get_gis().get_parser().get_url()!=null)
            {
                filename = get_previewlist().get_gis().get_parser().get_url().getFile().substring(get_previewlist().get_gis().get_parser().get_url().getFile().lastIndexOf("/")+1);
            }
            if("import_addr_street".equals(getAddressType()))
            {
                f_post = create_umsgisfile(filename,
                        get_previewlist().get_gis().get_parser().get_linedata(),
                        n_col_municipal, n_col_streetid, n_col_houseno, n_col_letter,n_col_apartment, n_skiplines, n_col_namefilter1, n_col_namefilter2, sz_separator);
            }
            else if("import_addr_CUNorway".equalsIgnoreCase(getAddressType())){
                f_post= create_umsgisfileCU(filename,
                        get_previewlist().get_gis().get_parser().get_linedata(),
                        n_col_municipal, n_col_gnr, n_col_bnr, n_col_fnr, n_col_snr, n_col_unr, n_col_apartment, n_skiplines,sz_separator);
            }
            else if("import_addr_CUSweden".equalsIgnoreCase(getAddressType())){
                f_post= create_umsgisfileCus(filename,
                        get_previewlist().get_gis().get_parser().get_linedata(),
                        n_col_municipal, n_col_fastighetsnyckel,n_col_houseno,n_col_letter,n_col_apartment,n_skiplines, sz_separator);
            }
            else if("import_addr_VABanken".equalsIgnoreCase(getAddressType())){
                f_post= create_umsgisfilevb(filename,
                        get_previewlist().get_gis().get_parser().get_linedata(),
                        n_col_municipal, n_col_vbanken,n_col_houseno,n_col_letter,n_col_apartment, n_skiplines,sz_separator);
            }
            else
            {
             }
          } catch(Exception err) {
            log.debug(err.getMessage());
            log.warn(err.getMessage(), err);
            Error.getError().addError("PreviewAddressPanel","Exception in actionPerformed",err,1);
        }
        return f_post;
    }
    
    public GISList create_gislist(File f, String sz_separator,String import_type) {
        GISList list = new GISList();
        String sz_line;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(f)));
            while(1==1) {
                sz_line = br.readLine();
                if(sz_line==null)
                    break;
                sz_line = sz_line.replace("\t", "\t ");
                String [] sz_cols = sz_line.split(sz_separator);
                for(int i=0; i < sz_cols.length; i++) {
                    sz_cols[i] = sz_cols[i].trim();
                    sz_cols[i] = sz_cols[i].replace("&", "&amp;");
                    sz_cols[i] = sz_cols[i].replace(">", "&gt;");
                    sz_cols[i] = sz_cols[i].replace("<", "&lt;");

                }
                list.addColumn(sz_cols,import_type);
            }
            br.close();
        } catch(Exception e) {

        }
        return list;
    }

    public File create_umsgisfile(String filename, LineData linedata, int n_mun, int n_str, int n_hou, int n_let,int n_apartment, int n_skip, int n_namefilter1, int n_namefilter2, String sz_sep) {
        log.debug(filename);
        File f_ums;
        try {
            f_ums = new File(no.ums.pas.core.storage.StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_) + filename);
        } catch(Exception e) {
          Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
            return null;
        }
        try {
            GISWriter writer = new GISWriter(linedata, f_ums);
            return writer.convert(n_mun, n_str, n_hou, n_let, n_apartment, n_skip, m_encoding);
        } catch(Exception e) {
            log.debug(e.getMessage());
            log.warn(e.getMessage(), e);
            Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
        }
        return null;
    }

    public File create_umsgisfile(String filename, LineData linedata, int n_mun, int n_str, int n_hou, int n_let,int n_apartment, int n_skip, String sz_sep) {
        log.debug(filename);
        File f_ums;
        try {
            f_ums = new File(no.ums.pas.core.storage.StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_) + filename);
        } catch(Exception e) {
         Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
            return null;
        }
        try {
            GISWriter writer = new GISWriter(linedata, f_ums);

            return writer.convert(n_mun, n_str, n_hou, n_let, n_apartment, n_skip, m_encoding);
        } catch(Exception e) {
            log.debug(e.getMessage());
            log.warn(e.getMessage(), e);
            Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
        }
        return null;
    }
    public File create_umsgisapartmentfile(String filename, LineData linedata, int n_mun, int n_str, int n_hou, int n_let,int n_apartment, int n_skip, int n_namefilter1, int n_namefilter2, String sz_sep) {
        log.debug(filename);
        File f_ums;
        try {
            f_ums = new File(no.ums.pas.core.storage.StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_) + filename);
        } catch(Exception e) {

            Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
            return null;
        }
        try {
            GISWriter writer = new GISWriter(linedata, f_ums);
            return writer.convert(n_mun, n_str, n_hou, n_let,n_apartment, n_namefilter1, n_namefilter2, n_skip, m_encoding);
        } catch(Exception e) {
            log.debug(e.getMessage());
            log.warn(e.getMessage(), e);
            Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
        }
        return null;
    }
    public File create_umsgisfile(String filename, LineData linedata, int n_mun, int n_gnr, int n_bnr, int n_fnr,
                                  int n_snr ,int n_apartment ,int n_skip, int n_namefilter1, int n_namefilter2, String sz_sep) {
        log.debug(filename);
        File f_ums;
        try {
            f_ums = new File(no.ums.pas.core.storage.StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_) + filename);
        } catch(Exception e) {

            Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
            return null;
        }
        try {
            GISWriter writer = new GISWriter(linedata, f_ums);
            return writer.convert(n_mun, n_gnr, n_bnr, n_fnr,n_snr,n_apartment, n_namefilter1, n_namefilter2, n_skip, m_encoding);
        } catch(Exception e) {
            log.debug(e.getMessage());
            log.warn(e.getMessage(), e);
            Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
        }
        return null;
    }
    
    
    //CUN
    public File create_umsgisfileCU(String filename, LineData linedata, int n_mun, int n_col_gnr,int n_col_bnr,int n_col_fnr,int n_col_snr,int n_col_unr, int n_col_apartment,
            int n_skip, String sz_sep) {
      log.debug(filename);
      File f_ums;
      try {
      f_ums = new File(no.ums.pas.core.storage.StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_) + filename);
       } catch(Exception e) {

      Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
      return null;
      }
      try {
       GISWriter writer = new GISWriter(linedata, f_ums);
       return writer.convertCU( n_mun,  n_col_gnr, n_col_bnr, n_col_fnr, n_col_snr, n_col_unr, n_col_apartment, n_skip, m_encoding);
       } catch(Exception e) {
       log.debug(e.getMessage());
       log.warn(e.getMessage(), e);
        Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
       }
        return null;
     }
    
    //CUS
    public File create_umsgisfileCus(String filename, LineData linedata, int n_mun, int n_fgkl, int n_hou, int n_let,int n_apartment,
            int n_skip, String sz_sep) {
      log.debug(filename);
      File f_ums;
      try {
      f_ums = new File(no.ums.pas.core.storage.StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_) + filename);
       } catch(Exception e) {

      Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
      return null;
      }
      try {
       GISWriter writer = new GISWriter(linedata, f_ums);
       return writer.convertCus(n_mun, n_fgkl, n_hou, n_let, n_apartment, n_skip, m_encoding);
       } catch(Exception e) {
       log.debug(e.getMessage());
       log.warn(e.getMessage(), e);
        Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
       }
        return null;
     }
    //VBN this ti --------
    public File create_umsgisfilevb(String filename, LineData linedata, int n_mun, int n_vbn,int n_hou, int n_let,int n_apartment,
            int n_skip, String sz_sep) {
      log.debug(filename);
      File f_ums;
      try {
      f_ums = new File(no.ums.pas.core.storage.StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_) + filename);
       } catch(Exception e) {

      Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
      return null;
      }
      try {
       GISWriter writer = new GISWriter(linedata, f_ums);
       return writer.convertVb(n_mun, n_vbn,n_hou,n_let,n_apartment,n_skip, m_encoding);
       } catch(Exception e) {
       log.debug(e.getMessage());
       log.warn(e.getMessage(), e);
        Error.getError().addError("PreviewAddressPanel","Exception in create_umsgisfile",e,1);
       }
        return null;
     }
    
    
    
    
    public void add_controls() {
        m_tab.addChangeListener(this);
       if(m_sendobject==null) {
            m_tab.addTab(Localization.l("importpreview_file_content"), null,
                    m_previewlist,
                    Localization.l("importpreview_file_content_tooltip"));
            set_gridconst(0, 0, 1, 1, GridBagConstraints.NORTH);
            add(m_tab, get_gridconst());
         
            set_gridconst(0, 1, 1, 1, GridBagConstraints.NORTH);
         if(m_loader!=null)
                add(m_loader, get_gridconst());
       
         } else {
            m_tab.addTab(Localization.l("importresults_results"), null,
                    m_resultpanel,
                    Localization.l("importresults_results_tooltip"));
            set_gridconst(0, 0, 1, 1, GridBagConstraints.NORTH);
            add(m_tab, get_gridconst());
           }
         }

    public void stateChanged(ChangeEvent e) {
        JTabbedPane pane = (JTabbedPane)e.getSource();
        if(pane.getSelectedComponent().equals(m_resultpanel)) {                                    //activate statistics
            m_callbackframe.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_statistics_view"));
        } else if(pane.getSelectedComponent().equals(m_previewlist)) {
            //activate options ---
            if(!is_valid_toPerform_action()){

            }
            m_callbackframe.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_options_view"));
        }
    }
    
    private boolean is_valid_toPerform_action() {
      
    	if("import_addr_street".equals(getAddressType())){
        	if(get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_STREETID) != -1 &&
                   get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_HOUSENO) != -1 &&
                   get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_LETTER) != -1) {
                return true;
            }
            else
                return false;

        }
        else if("import_addr_CUNorway".equals(getAddressType())){
        	if(get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_GNR) != -1 &&
                    get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_BNR) != -1 ) {
                return true;
            }
            else
                return false;

        }
        else if("import_addr_CUSweden".equals(getAddressType())){
        	if(get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_Fastighetsnyckel) != -1 ) {
                return true;
            }
            else
                return false;

        }
        else if("import_addr_VABanken".equals(getAddressType())){
        	if(get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_VB) != -1 ) {
                return true;
            }
            else
                return false;

        }
        return false;
    }
    public void componentResized(ComponentEvent e) {
        m_tab.setPreferredSize(new Dimension(getWidth(), getHeight()-200));
        if(m_previewlist!=null) {
            m_previewlist.setPreferredSize(new Dimension(getWidth(), getHeight()-200));
            m_previewlist.revalidate();
        }
        if(m_loader!=null)
            m_loader.setPreferredSize(new Dimension(getWidth(), 0));
        if(get_resultpanel()!=null) {
            m_resultpanel.setPreferredSize(new Dimension(getWidth(), getHeight()-200));
            m_resultpanel.revalidate();
        }
        revalidate();
    }
    public void componentHidden(ComponentEvent e) { }
    public void componentMoved(ComponentEvent e) { }
    public void componentShown(ComponentEvent e) { }

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public PreviewAddressFrame getM_parent() {
		return m_parent;
	}

	public void setM_parent(PreviewAddressFrame m_parent) {
		this.m_parent = m_parent;
	}
}