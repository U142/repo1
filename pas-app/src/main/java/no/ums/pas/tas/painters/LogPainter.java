package no.ums.pas.tas.painters;

import no.ums.pas.status.LBASEND;
import no.ums.pas.tas.TasHelpers;
import no.ums.pas.tas.TasPanel;
import no.ums.pas.tas.treenodes.CommonTASListItem;
import no.ums.pas.tas.treenodes.CountryListItem;
import no.ums.pas.tas.treenodes.RequestLogItem;
import no.ums.ws.pas.tas.ENUMTASREQUESTRESULTTYPE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class LogPainter
{
	public static void drawLog(Graphics2D g, 
						Hashtable<Object, RequestLogItem> reqreshash, 
						Vector<RequestLogItem> reqreshash_sorted,
						Hashtable<Object, CommonTASListItem> treehash)
	{
		synchronized(reqreshash)
		{
			synchronized(reqreshash_sorted)
			{
				Font usefont = new Font("Verdana", Font.BOLD, 10);
				g.setFont(usefont);
				int n_lineheight = g.getFontMetrics().getHeight();
				int n_counter = 0;
				//Enumeration<RequestLogItem> en = reqreshash.elements();
				Enumeration<RequestLogItem> en = reqreshash_sorted.elements();
				int n_max_linewidth = 1;
				int n_total_height = 1;
				while(en.hasMoreElements())
				{
					RequestLogItem rl = en.nextElement();
					//Object sortedkey = en.nextElement();
					
					/*RequestLogItem rl = reqreshash.get(sortedkey);
					if(rl==null)
					{
						continue;
					}*/
					if(rl.GetServerAgeSec() < TasPanel.TAS_REQUEST_TIMESTAMP_EXPIRED_SECONDS && rl.getTasResult().getNStatus()!=2000)
					{
						String s = "";
						String country = "";
						if(rl.getTasResult().getList().getULBACOUNTRY().size()==1)
						{
							String iso = rl.getTasResult().getList().getULBACOUNTRY().get(0).getSzIso().trim();
							//if(iso.trim().length()==0)
							try
							{
								country = ((CountryListItem)treehash.get(iso)).getCountry().getSzName();
							}
							catch(Exception e)
							{
								country = iso;
							}
							//else
							//	country = iso;
							//country = rl.getTasResult().getList().getULBACOUNTRY().get(0).getSzIso().trim();
						}
						else if(rl.getTasResult().getList().getULBACOUNTRY().size()>1)
							country = "Multiple countries";
						else
							country = "Unknown country";
						boolean b_details = true;
						String sz_operation = no.ums.pas.ums.tools.TextFormat.format_datetime(rl.getTasResult().getNTimestamp()) + " ";
						if(rl.getTasResult().getType()==null || rl.getTasResult().getType().equals(ENUMTASREQUESTRESULTTYPE.COUNTREQUEST))
							sz_operation += "Count Request - ";
						else if(rl.getTasResult().getType().equals(ENUMTASREQUESTRESULTTYPE.SENDING))
						{
							if(rl.getTasResult().getNSimulation()==1)
								sz_operation += "[Simulated] ";
							else
								sz_operation += "[LIVE] ";
							sz_operation += "Sending - ";
							sz_operation += "\"" + rl.getTasResult().getSzSendingname() + "\" - ";
						}
						switch(rl.getTasResult().getNStatus())
						{
						case 1:
							s = sz_operation + country + " - ";
							b_details = false;
							break;
						case LBASEND.LBASTATUS_SENT_TO_LBA: //sent to TAS-server
						case LBASEND.LBASTATUS_PARSING_LBAS: //sent to operator
							s = sz_operation + country + " - sent to UMS";
							break;
						case LBASEND.TASSTATUS_PREPARING_CELLVISION: //preparing
						case LBASEND.TASSTATUS_PREPARED_CELLVISION: //updating multiple countries
							s = sz_operation + country + " - sent to operator and processing " + rl.getTasResult().getSzOperatorname();
							break;
						case LBASEND.LBASTATUS_PARSING_LBAS_FAILED_TO_SEND: //failed, retry
							s = sz_operation + country + " - failed, retrying... " + rl.getTasResult().getSzOperatorname();
							break;
						case LBASEND.LBASTATUS_CANCELLED: //done
							s = sz_operation + country + " - done!";
							break;
						case LBASEND.LBASTATUS_EXCEPTION_EXECUTE_PREPARED_ALERT:
						case LBASEND.LBASTATUS_EXCEPTION_CANCEL_PREPARED_ALERT:
						case LBASEND.TASSTATUS_EXCEPTION_EXECUTE_INT_ALERT:
						case LBASEND.TASSTATUS_EXCEPTION_PREPARE_INT_ALERT:
							s = sz_operation + country + " - failed (UMS)";
							break;
						case LBASEND.LBASTATUS_FAILED_EXECUTE_PREPARED_ALERT:
						case LBASEND.LBASTATUS_FAILED_CANCEL_PREPARED_ALERT:
						case LBASEND.TASSTATUS_FAILED_EXECUTE_INT_ALERT:
						case LBASEND.TASSTATUS_FAILED_PREPARE_INT_ALERT:
							s = sz_operation + country + " - failed (" + rl.getTasResult().getSzOperatorname() + ")";
							break;
						case LBASEND.LBASTATUS_CANCEL_IN_PROGRESS:
							s = sz_operation + country + " - Cancel in progress (" + rl.getTasResult().getSzOperatorname() + ")";
							break;
						case LBASEND.LBASTATUS_CANCELLED_BY_USER:
						case LBASEND.LBASTATUS_CANCELLED_BY_USER_OR_SYSTEM:
							s = sz_operation + country + " - Cancelled (" + rl.getTasResult().getSzOperatorname() + ")";
							break;
						case LBASEND.LBASTATUS_CONFIRMED_BY_USER:
							s = sz_operation + country + " - Confirmed by user (" + rl.getTasResult().getSzOperatorname() + ")";
							break;
						case LBASEND.LBASTATUS_SENDING:
							s = sz_operation + country + " - Sending (" + rl.getTasResult().getSzOperatorname() + ")";
                            break;
						case LBASEND.LBASTATUS_FINISHED:
							s = sz_operation + country + " - Finished (" + rl.getTasResult().getSzOperatorname() + ")";
							break;
						default: //TAS-server
							s = sz_operation + country + " - in progress";
							break;
						
						}
						if(b_details)
							s+= " (requestpk=" + rl.getTasResult().getNRequestpk() + " by " + rl.getTasResult().getSzUsername() + ")";
						
						int width = (int)usefont.getStringBounds(s, g.getFontRenderContext()).getWidth();
						n_max_linewidth = Math.max(n_max_linewidth, width);
	
						if(rl.getTasResult().getType()==null || rl.getTasResult().getType().equals(ENUMTASREQUESTRESULTTYPE.COUNTREQUEST))
							g.setColor(TasHelpers.getInformationColor());						
						else if(rl.getTasResult().getType().equals(ENUMTASREQUESTRESULTTYPE.SENDING) && rl.getTasResult().getNSimulation()==1)
							g.setColor(TasHelpers.getSimulationColor());
						else if(rl.getTasResult().getType().equals(ENUMTASREQUESTRESULTTYPE.SENDING))
							g.setColor(TasHelpers.getLiveColor());
						g.fillRoundRect(5, 5 + n_counter * (n_lineheight+3), n_max_linewidth + 5*2, n_lineheight, 15, 15);
						
						g.setColor(Color.black);
						g.drawRoundRect(5, 5 + n_counter * (n_lineheight+3), n_max_linewidth + 5*2, n_lineheight, 15, 15);
						
						//g.setColor(Color.black);
						if(rl.getTasResult().getType()==null || rl.getTasResult().getType().equals(ENUMTASREQUESTRESULTTYPE.COUNTREQUEST))
							g.setColor(TasHelpers.getInformationTextColor());						
						else if(rl.getTasResult().getType().equals(ENUMTASREQUESTRESULTTYPE.SENDING) && rl.getTasResult().getNSimulation()==1)
							g.setColor(TasHelpers.getSimulationTextColor());
						else if(rl.getTasResult().getType().equals(ENUMTASREQUESTRESULTTYPE.SENDING))
							g.setColor(TasHelpers.getLiveTextColor());
	
						g.drawString(s, 10, 16 + n_counter * (n_lineheight+3));
						n_counter++;
					}
					else if(rl.GetServerAgeSec() > TasPanel.TAS_REQUEST_TIMESTAMP_EXPIRED_DELETEAFTER_SECONDS)
					{
						try
						{
							String key = rl.getTasResult().getType().name() + rl.getTasResult().getNRequestpk() + "_" + rl.getTasResult().getNOperator();
							reqreshash.remove(key);
						}
						catch(Exception err)
						{
							
						}
					}
					else
					{
						//System.out.println("test " + rl.GetServerAgeSec()/60);
					}
				}
			}
		}
	}
}