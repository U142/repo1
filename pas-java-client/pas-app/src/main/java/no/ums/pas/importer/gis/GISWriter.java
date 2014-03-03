package no.ums.pas.importer.gis;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.ums.errorhandling.Error;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class GISWriter {

    private static final Log log = UmsLog.getLogger(GISWriter.class);

	//protected File m_f1;
	protected File m_file_out;
	protected LineData m_linedata;
	
	public File get_umsgis_file() { return m_file_out; }
	protected LineData data() { return m_linedata; }
	
	public GISWriter(LineData data, File f_out) {
		m_file_out = f_out;
		m_linedata = data;
	}
	
	public File convert(int n_mun, int n_str, int n_hou, int n_let, int n_namefilter1, int n_namefilter2, int skip, String encoding) {
		try {
			
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(get_umsgis_file()), encoding);
			
			LineData.Line line;
			
			String sz_mun, sz_street, sz_house, sz_letter, sz_namefilter1, sz_namefilter2;
			ArrayList<String> parameters;
			for(int i=skip; i < data().get_lines().size(); i++) {
				line = (LineData.Line)data().get_lines().get(i);
				parameters = new ArrayList<String>();
				try {
					sz_mun = line.get_row(n_mun);
				} catch(Exception e) {
					sz_mun = "0";
					//Error.getError().addError("GISWriter","Exception in convert",e,1);
				}
                parameters.add(sz_mun);
				try {
					sz_street = line.get_row(n_str);
				} catch(Exception e) {
					sz_street = "0";
				}
                parameters.add(sz_street);
				try {
					sz_house = line.get_row(n_hou);

				} catch(Exception e) {
					sz_house = "0";
					Error.getError().addError("GISWriter","Exception in convert",e,1);
				}
                parameters.add(sz_house);
				try {
					sz_letter = line.get_row(n_let); //******* Her feiler den

				} catch(Exception e) {
					sz_letter = "";
					//Error.getError().addError("GISWriter","Exception in convert",e,1);
				}
                parameters.add(sz_letter);
				try {
					sz_namefilter1 = line.get_row(n_namefilter1);

				} catch(Exception e) {
					sz_namefilter1 = "";
				}
                parameters.add(sz_namefilter1);
				try {
					sz_namefilter2 =  line.get_row(n_namefilter2);

				} catch(Exception e) {
					sz_namefilter2 = "";
				}
                parameters.add(sz_namefilter2);
				//if(sz_mun.trim().length()>0 && sz_street.trim().length()>0 && sz_house.trim().length()>0)
					write_line(out, parameters);
				//else
				//	Error.getError().addError("Error reading line", "Line number " + i + " does not contain enough data (" + line.toString() + ")", 0, 1);
			}
			out.close();
			return get_umsgis_file();
		} catch(Exception e) {
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("GISWriter","Exception in convert",e,1);
		}
		return null;
	}
    //n_mun, n_gnr, n_bnr, n_fnr,n_snr, n_namefilter1, n_namefilter2, n_skip, m_encoding
    public File convert(int n_mun, int n_gnr, int n_bnr, int n_fnr,int n_snr, int n_namefilter1, int n_namefilter2, int skip, String encoding) {
        try {

            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(get_umsgis_file()), encoding);

            LineData.Line line;

            String sz_mun, sz_gnr, sz_bnr, sz_fnr,sz_snr, sz_namefilter1, sz_namefilter2;
            ArrayList<String> parameters;
            for(int i=skip; i < data().get_lines().size(); i++) {
                line = (LineData.Line)data().get_lines().get(i);
                parameters = new ArrayList<String>();
                try {
                    sz_mun = line.get_row(n_mun);
                } catch(Exception e) {
                    sz_mun = "0";
                    //Error.getError().addError("GISWriter","Exception in convert",e,1);
                }
                parameters.add(sz_mun);
                try {
                    sz_gnr = line.get_row(n_gnr);
                } catch(Exception e) {
                    sz_gnr = "0";
                }
                parameters.add(sz_gnr);
                try {
                    sz_bnr = line.get_row(n_bnr);
                } catch(Exception e) {
                    sz_bnr = "0";
                    Error.getError().addError("GISWriter","Exception in convert",e,1);
                }
                parameters.add(sz_bnr);
                try {
                    sz_fnr = line.get_row(n_fnr); //******* Her feiler den
                } catch(Exception e) {
                    sz_fnr =  "0";;
                    //Error.getError().addError("GISWriter","Exception in convert",e,1);
                }
                parameters.add(sz_fnr);
                try {
                    sz_snr = line.get_row(n_snr); //******* Her feiler den
                } catch(Exception e) {
                    sz_snr =  "0";;
                    //Error.getError().addError("GISWriter","Exception in convert",e,1);
                }
                parameters.add(sz_snr);
                try {
                    sz_namefilter1 = line.get_row(n_namefilter1);
                } catch(Exception e) {
                    sz_namefilter1 = "";
                }
                parameters.add(sz_namefilter1);
                try {
                    sz_namefilter2 =  line.get_row(n_namefilter2);
                } catch(Exception e) {
                    sz_namefilter2 = "";
                }
                parameters.add(sz_namefilter2);
                //if(sz_mun.trim().length()>0 && sz_street.trim().length()>0 && sz_house.trim().length()>0)
                write_line(out,  parameters);
                //else
                //	Error.getError().addError("Error reading line", "Line number " + i + " does not contain enough data (" + line.toString() + ")", 0, 1);
            }
            out.close();
            return get_umsgis_file();
        } catch(Exception e) {
            log.debug(e.getMessage());
            log.warn(e.getMessage(), e);
            Error.getError().addError("GISWriter","Exception in convert",e,1);
        }
        return null;
    }
    
    //for street address import
    public File convert(int n_mun, int n_str, int n_hou, int n_let, int n_apartment, int skip, String encoding) {
		try {
			
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(get_umsgis_file()), encoding);
			
			LineData.Line line;
			
			String sz_mun, sz_street, sz_house, sz_letter, sz_apartment;
			ArrayList<String> parameters;
			for(int i=skip; i < data().get_lines().size(); i++) {
				line = (LineData.Line)data().get_lines().get(i);
				parameters = new ArrayList<String>();
				try {
					sz_mun = line.get_row(n_mun);
				} catch(Exception e) {
					sz_mun = "0";
					//Error.getError().addError("GISWriter","Exception in convert",e,1);
				}
                parameters.add(sz_mun);
				try {
					sz_street = line.get_row(n_str);
				} catch(Exception e) {
					sz_street = "0";
				}
                parameters.add(sz_street);
				try {
					sz_house = line.get_row(n_hou);
					if("".equals(sz_house)||sz_house==null||sz_house.length()==0)
						sz_house =  "0";
				} catch(Exception e) {
					sz_house = "0";
					Error.getError().addError("GISWriter","Exception in convert",e,1);
				}
                parameters.add(sz_house);
				try {
					sz_letter = line.get_row(n_let); //******* Her feiler den
					if("".equals(sz_letter)||sz_letter==null||sz_letter.length()==0)
						sz_letter =  "";
				} catch(Exception e) {
					sz_letter = "";
					//Error.getError().addError("GISWriter","Exception in convert",e,1);
				}
                parameters.add(sz_letter);
                try {
                	sz_apartment = line.get_row(n_apartment); //******* Her feiler den
                	if("".equals(sz_apartment)||sz_apartment==null||sz_apartment.length()==0)
                		sz_apartment =  "0";
                } catch(Exception e) {
                	sz_apartment =  "0";
                    //Error.getError().addError("GISWriter","Exception in convert",e,1);
                }
                parameters.add(sz_apartment);
                
				//if(sz_mun.trim().length()>0 && sz_street.trim().length()>0 && sz_house.trim().length()>0)
					write_line(out, parameters);
				//else
				//	Error.getError().addError("Error reading line", "Line number " + i + " does not contain enough data (" + line.toString() + ")", 0, 1);
			}
			out.close();
			return get_umsgis_file();
		} catch(Exception e) {
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("GISWriter","Exception in convert",e,1);
		}
		return null;
	}
    
  //n_mun, n_gnr, n_bnr, n_fnr,n_snr,n_apartment, n_namefilter1, n_namefilter2, n_skip, m_encoding
    //for property import
    public File convert(int n_mun, int n_gnr, int n_bnr, int n_fnr,int n_snr,int n_apartment, int n_namefilter1, int n_namefilter2, int skip, String encoding) {
        try {

            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(get_umsgis_file()), encoding);

            LineData.Line line;

            String sz_mun, sz_gnr, sz_bnr, sz_fnr,sz_snr, sz_namefilter1, sz_namefilter2;
            String sz_apartment;
            ArrayList<String> parameters;
            for(int i=skip; i < data().get_lines().size(); i++) {
                line = (LineData.Line)data().get_lines().get(i);
                parameters = new ArrayList<String>();
                try {
                    sz_mun = line.get_row(n_mun);
                } catch(Exception e) {
                    sz_mun = "0";
                    //Error.getError().addError("GISWriter","Exception in convert",e,1);
                }
                parameters.add(sz_mun);
                try {
                    sz_gnr = line.get_row(n_gnr);
                } catch(Exception e) {
                    sz_gnr = "0";
                }
                parameters.add(sz_gnr);
                try {
                    sz_bnr = line.get_row(n_bnr);
                } catch(Exception e) {
                    sz_bnr = "0";
                    Error.getError().addError("GISWriter","Exception in convert",e,1);
                }
                parameters.add(sz_bnr);
                try {
                    sz_fnr = line.get_row(n_fnr); //******* Her feiler den
                    if("".equals(sz_fnr)||sz_fnr==null||sz_fnr.length()==0)
                		sz_apartment =  "0";
                } catch(Exception e) {
                    sz_fnr =  "0";
                    //Error.getError().addError("GISWriter","Exception in convert",e,1);
                }
                parameters.add(sz_fnr);
                try {
                    sz_snr = line.get_row(n_snr); //******* Her feiler den
                    if("".equals(sz_snr)||sz_snr==null||sz_snr.length()==0)
                		sz_apartment =  "0";
                } catch(Exception e) {
                    sz_snr =  "0";;
                    //Error.getError().addError("GISWriter","Exception in convert",e,1);
                }
                parameters.add(sz_snr);
                
                try {
                	sz_apartment = line.get_row(n_apartment); //******* Her feiler den
                	if("".equals(sz_apartment)||sz_apartment==null||sz_apartment.length()==0)
                		sz_apartment =  "0";
                } catch(Exception e) {
                	sz_apartment =  "0";;
                    //Error.getError().addError("GISWriter","Exception in convert",e,1);
                }
                parameters.add(sz_apartment);
                
                try {
                    sz_namefilter1 = line.get_row(n_namefilter1);
                } catch(Exception e) {
                    sz_namefilter1 = "";
                }
                parameters.add(sz_namefilter1);
                try {
                    sz_namefilter2 =  line.get_row(n_namefilter2);
                } catch(Exception e) {
                    sz_namefilter2 = "";
                }
                parameters.add(sz_namefilter2);
                //if(sz_mun.trim().length()>0 && sz_street.trim().length()>0 && sz_house.trim().length()>0)
                write_line(out,  parameters);
                //else
                //	Error.getError().addError("Error reading line", "Line number " + i + " does not contain enough data (" + line.toString() + ")", 0, 1);
            }
            out.close();
            return get_umsgis_file();
        } catch(Exception e) {
            log.debug(e.getMessage());
            log.warn(e.getMessage(), e);
            Error.getError().addError("GISWriter","Exception in convert",e,1);
        }
        return null;
    }

	private boolean write_line(OutputStreamWriter w, String c1, String c2, String c3, String c4, String c5, String c6) {
		try {
			w.write(c1 + "\t" + c2 + "\t" + c3 + "\t" + c4 + "\t" + c5 + "\t" + c6 + "\r\n");
		} catch(Exception e) {
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("GISWriter","Exception in write_line",e,1);
		}
		return false;
	}

    private boolean write_line(OutputStreamWriter w,ArrayList<String> parameters) {

        StringBuilder string= new StringBuilder();
        for (String e:parameters) {
            string.append(e+"\t");
        }
        string.append("\r\n");
        try {
            w.write(string.toString());
        } catch(Exception e) {
            log.debug(e.getMessage());
            log.warn(e.getMessage(), e);
            Error.getError().addError("GISWriter","Exception in write_line",e,1);
        }
        return false;
    }
	
}