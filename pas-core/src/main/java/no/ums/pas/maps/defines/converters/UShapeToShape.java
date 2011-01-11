package no.ums.pas.maps.defines.converters;

import no.ums.pas.core.Variables;
import no.ums.pas.maps.defines.*;
import no.ums.pas.maps.defines.ShapeStruct.DETAILMODE;
import no.ums.ws.pas.*;

import java.awt.*;
import java.util.List;


public class UShapeToShape
{
	public static ShapeStruct ConvertUShape_to_ShapeStruct(no.ums.ws.pas.status.UShape ushape)
	{
		if(ushape==null)
			return null;
		if(ushape.getClass().equals(no.ums.ws.pas.status.UPolygon.class))
		{
			no.ums.ws.pas.status.UPolygon upolygon = (no.ums.ws.pas.status.UPolygon)ushape;
			PolygonStruct polygonstruct = new PolygonStruct(null, 
					new Color((int)upolygon.getColRed(), 
							(int)upolygon.getColGreen(), 
							(int)upolygon.getColBlue(), 
							(int)upolygon.getColAlpha()), 
							new Color(0,0,0));
			polygonstruct.setDetailMode(DETAILMODE.SHOW_POLYGON_FULL);
			//if(upolygon.getMArrayPolypoints()!=null)
			if(upolygon.getPolypoint()!=null)
			{
				//List<UPolypoint> list = upolygon.getMArrayPolypoints().getUPolypoint();
				List<no.ums.ws.pas.status.UPolypoint> list = upolygon.getPolypoint();
				for(int i=0; i < list.size(); i++)
				{
					polygonstruct.add_coor(list.get(i).getLon(), list.get(i).getLat(), true, false);
				}
				polygonstruct.finalizeShape();
			}
			return polygonstruct;
		}
		else if(ushape.getClass().equals(no.ums.ws.pas.status.UEllipse.class))
		{
			no.ums.ws.pas.status.UEllipse ell = (no.ums.ws.pas.status.UEllipse)ushape;
			EllipseStruct ellipsestruct = new EllipseStruct(
					new Color((int)ell.getColRed(), 
							(int)ell.getColGreen(), 
							(int)ell.getColBlue(), 
							(int)ell.getColAlpha()), 
							new Color(0,0,0));
			ellipsestruct.set_ellipse_center(
                    Variables.getNavigation(),
					new MapPoint(Variables.getNavigation(), new MapPointLL(ell.getLon(), ell.getLat())));
			ellipsestruct.set_ellipse_corner(
                    Variables.getNavigation(),
					new MapPoint(Variables.getNavigation(), new MapPointLL(ell.getX(), ell.getY())));
			return ellipsestruct;
					
		}
		else if(ushape.getClass().equals(no.ums.ws.pas.status.UBoundingRect.class))
		{
			no.ums.ws.pas.status.UBoundingRect upolygon = (no.ums.ws.pas.status.UBoundingRect)ushape;
			PolygonStruct polygonstruct = new PolygonStruct(null, 
					new Color((int)upolygon.getColRed(), 
							(int)upolygon.getColGreen(), 
							(int)upolygon.getColBlue(), 
							(int)upolygon.getColAlpha()), 
							new Color(0,0,0));
			polygonstruct.add_coor(upolygon.getLeft(), upolygon.getTop());
			polygonstruct.add_coor(upolygon.getRight(), upolygon.getTop());
			polygonstruct.add_coor(upolygon.getRight(), upolygon.getBottom());
			polygonstruct.add_coor(upolygon.getLeft(), upolygon.getBottom());
			
			//Hide if the restriction area is the whole world
			if(upolygon.getLeft()==-180 && upolygon.getRight()==180 &&
					upolygon.getBottom()==-90 && upolygon.getTop()==90)
				polygonstruct.setHidden(true);
			return polygonstruct;
		}
		else if(ushape.getClass().equals(no.ums.ws.pas.status.UPLMN.class))
		{
			PLMNShape plmn = new PLMNShape();
			return plmn;
		}
		return null;
	}

	public static ShapeStruct ConvertUShape_to_ShapeStruct(UShape ushape)
	{
		if(ushape.getClass().equals(UPolygon.class))
		{
			UPolygon upolygon = (UPolygon)ushape;
			PolygonStruct polygonstruct = new PolygonStruct(null, 
					new Color((int)upolygon.getColRed(), 
							(int)upolygon.getColGreen(), 
							(int)upolygon.getColBlue(), 
							(int)upolygon.getColAlpha()), 
							new Color(0,0,0));
			//if(upolygon.getMArrayPolypoints()!=null)
			if(upolygon.getPolypoint()!=null)
			{
				//List<UPolypoint> list = upolygon.getMArrayPolypoints().getUPolypoint();
				List<UPolypoint> list = upolygon.getPolypoint();
				for(int i=0; i < list.size(); i++)
				{
					polygonstruct.add_coor(list.get(i).getLon(), list.get(i).getLat(), true, false);
				}
				polygonstruct.finalizeShape();
			}
			//Hide if obsolete
			if(upolygon.getFDisabled() == 1) {
				polygonstruct.setHidden(true);
				polygonstruct.setObsolete(true);
				polygonstruct.setEditable(false);
			}
			return polygonstruct;
		}
		else if(ushape.getClass().equals(UEllipse.class))
		{
			UEllipse ell = (UEllipse)ushape;
			EllipseStruct ellipsestruct = new EllipseStruct(
					new Color((int)ell.getColRed(), 
							(int)ell.getColGreen(), 
							(int)ell.getColBlue(), 
							(int)ell.getColAlpha()), 
							new Color(0,0,0));
			ellipsestruct.set_ellipse_center(
                    Variables.getNavigation(),
					new MapPoint(Variables.getNavigation(), new MapPointLL(ell.getLon(), ell.getLat())));
			ellipsestruct.set_ellipse_corner(
                    Variables.getNavigation(),
					new MapPoint(Variables.getNavigation(), new MapPointLL(ell.getX(), ell.getY())));
			return ellipsestruct;
					
		}
		else if(ushape.getClass().equals(UBoundingRect.class))
		{
			UBoundingRect upolygon = (UBoundingRect)ushape;
			PolygonStruct polygonstruct = new PolygonStruct(null, 
					new Color((int)upolygon.getColRed(), 
							(int)upolygon.getColGreen(), 
							(int)upolygon.getColBlue(), 
							(int)upolygon.getColAlpha()), 
							new Color(0,0,0));
			polygonstruct.add_coor(upolygon.getLeft(), upolygon.getTop());
			polygonstruct.add_coor(upolygon.getRight(), upolygon.getTop());
			polygonstruct.add_coor(upolygon.getRight(), upolygon.getBottom());
			polygonstruct.add_coor(upolygon.getLeft(), upolygon.getBottom());
			
			//Hide if the restriction area is the whole world
			if(upolygon.getLeft()==-180 && upolygon.getRight()==180 &&
					upolygon.getBottom()==-90 && upolygon.getTop()==90)
				polygonstruct.setHidden(true);
				
			return polygonstruct;
		}
		else if(ushape.getClass().equals(UPLMN.class))
		{
			UPLMN uplmn = (UPLMN)ushape;
			return new PLMNShape();
		}
		return null;
	}

}