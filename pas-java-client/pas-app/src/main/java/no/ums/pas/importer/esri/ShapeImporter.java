package no.ums.pas.importer.esri;

import com.vividsolutions.jts.geom.GeometryFactory;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.importer.ActionFileLoaded;
import no.ums.pas.importer.FileParser;
import no.ums.pas.importer.ImportPolygon;
import no.ums.pas.maps.defines.MapLine;
import no.ums.pas.maps.defines.MapPointF;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.CoorConverter;
import no.ums.pas.ums.tools.CoorConverter.LLCoor;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.shp.ShapefileHeader;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ShapeImporter extends FileParser
{
    private static final Log log = UmsLog.getLogger(ShapeImporter.class);

	@Override
	public boolean create_values() {
		return false;
	}
	
	public void parse_with_geotools()
	{
		CoordinateReferenceSystem ref = null;
		ShpFiles shp = null;
		List<String> dbf_fields = new ArrayList<String>();
		List<Object[]> dbf_content = new ArrayList<Object[]>();
		List<String> dbf_strings = new ArrayList<String>();

		try
		{
			if(get_file()!=null)
				shp = new ShpFiles(get_file());
			else if(get_url()!=null)
				shp = new ShpFiles(get_url());
		}
		catch(MalformedURLException e)
		{
			log.warn(e.getMessage(), e);
			return;
		}
		try
		{
			org.geotools.data.shapefile.prj.PrjFileReader prj = new org.geotools.data.shapefile.prj.PrjFileReader(shp);
			ref = prj.getCoodinateSystem();
			prj.close();
		}
		catch(IOException e)
		{
			
		}
		try
		{
			DbaseFileReader dbf = new DbaseFileReader(shp, true, Charset.forName("ISO-8859-1"));
			int n_fields = dbf.getHeader().getNumFields();
			for(int i=0; i < n_fields; i++)
			{
				String s = dbf.getHeader().getFieldName(i);
				dbf_fields.add(s);
				log.debug("Field" + i + "=" + s);
			}
			while(dbf.hasNext())
			{
				Object [] arrfields = dbf.readEntry();
				String output = "DBF: ";
				Object [] fields = new Object[n_fields];
				for(int i=0; i < n_fields; i++)
				{
					Object o = dbf.readField(i);
					output += o + ",";
				}
				dbf_content.add(fields);
				dbf_strings.add(output);
				log.debug(output);
			}
			dbf.close();
		}
		catch(Exception e)
		{
			
		}
		try
		{
			GeometryFactory fact = new GeometryFactory();
			String projection_code = (ref!=null ? ref.getName().getCode() : "lonlat");
			log.debug("Projection = " + projection_code);
			/*IndexFile indexfile = new IndexFile(shp, true);
			for(int i=0; i < indexfile.getRecordCount(); i++)
			{
				int n_contentlength = indexfile.getContentLength(i);
				log.debug("" + n_contentlength);
			}*/

			org.geotools.data.shapefile.shp.ShapefileReader shape = new org.geotools.data.shapefile.shp.ShapefileReader(shp, false, false, fact);
			int n = fact.getSRID();
			ShapefileHeader header = shape.getHeader();
			int totalshapes = 0;
			while(shape.hasNext())
			{
				
				//Record rec = shape.nextRecord();
				Object obj = shape.nextRecord().shape();
				Class cl = obj.getClass();
				
				if(obj.getClass().equals(com.vividsolutions.jts.geom.MultiPolygon.class))
				{
				    //String SOURCE_WKT = "GEOGCS[\"WGS84\", DATUM[\"WGS84\", SPHEROID[\"WGS84\", 6378137.0, 298.257223563]], PRIMEM[\"Greenwich\", 0.0], UNIT[\"degree\",0.017453292519943295], AXIS[\"Longitude\",EAST], AXIS[\"Latitude\",NORTH]]";
				    //String TARGET_WKT = "PROJCS[\"UTM Zone 14N\", GEOGCS[\"WGS84\", DATUM[\"WGS84\", SPHEROID[\"WGS84\", 6378137.0, 298.257223563]], PRIMEM[\"Greenwich\", 0.0], UNIT[\"degree\",0.017453292519943295], AXIS[\"Longitude\",EAST], AXIS[\"Latitude\",NORTH]], PROJECTION[\"Transverse_Mercator\"], PARAMETER[\"central_meridian\", -99.0], PARAMETER[\"latitude_of_origin\", 0.0], PARAMETER[\"scale_factor\", 0.9996], PARAMETER[\"false_easting\", 500000.0], PARAMETER[\"false_northing\", 0.0], UNIT[\"metre\",1.0], AXIS[\"x\",EAST], AXIS[\"y\",NORTH]]";
					
				    
				    com.vividsolutions.jts.geom.MultiPolygon geom = (com.vividsolutions.jts.geom.MultiPolygon)obj;
					//geom.apply(arg0)
					//geom.getSRID()
					//int srid = geom.getSRID();
					//geom.setSRID(84);
					//geom.geometryChanged();
					//geom.setSRID(SRID)
					int polygons = geom.getNumGeometries();
					
					Object data = geom.getUserData();
					//String str = data.toString();
					
					for(int i=0; i < polygons; i++)
					{
						

						com.vividsolutions.jts.geom.Geometry g = geom.getGeometryN(i);
						com.vividsolutions.jts.geom.Coordinate [] coors = g.getCoordinates();
						PolygonStruct poly = new PolygonStruct(new java.awt.Dimension(1,1));
						poly.shapeID = (++totalshapes);
						poly.shapeName = (dbf_strings.size() > totalshapes ? dbf_strings.get(totalshapes) : "Imported Polygon "+poly.shapeID);
						//poly.SetBounds(geom.getBoundary()., bounds._rbo, bounds._ubo, bounds._bbo);
						for(int p = 0; p < coors.length; p++)
						{
							no.ums.pas.ums.tools.CoorConverter conv = new CoorConverter();
							if(projection_code.toLowerCase().contains("rd"))
							{
								LLCoor ll = conv.rd_to_wgs84(coors[p].x, coors[p].y);
								poly.add_coor(ll.get_lon(), ll.get_lat(), true, false);								
							}
							else if(projection_code.toLowerCase().contains("utm") || (coors[p].x>180 || coors[p].x < -180))
							{
								LLCoor ll1 = conv.UTM2LL(23, coors[p].y, coors[p].x, "32V");
								poly.add_coor(ll1.get_lon(), ll1.get_lat(), true, false);
							}
							else
							{
								//assume lon/lat
								poly.add_coor(coors[p].x, coors[p].y, true, false);
							}

						}
						polylist.add(poly);
					}
					for(ShapeStruct ss : polylist)
					{
						ss.finalizeShape();
					}
					totalshapes++;
				}
				else if(obj.getClass().equals(com.vividsolutions.jts.geom.Polygon.class))
				{
					
				}
				else if(obj.getClass().equals(com.vividsolutions.jts.geom.Point.class))
				{
					com.vividsolutions.jts.geom.Point geom = (com.vividsolutions.jts.geom.Point)obj;
					int points = geom.getNumGeometries();
					for(int i=0; i < points; i++)
					{
						com.vividsolutions.jts.geom.Geometry g = geom.getGeometryN(i);
						com.vividsolutions.jts.geom.Coordinate [] coors = g.getCoordinates();
						for(int p = 0; p < coors.length; p++)
						{
							MapPointF point = new MapPointF(coors[p].x, coors[p].y);
							pointlist.add(point);
							totalshapes++;
						}
					}
				}
				else if(obj.getClass().equals(com.vividsolutions.jts.geom.MultiPoint.class))
				{
					
				}
				else if(obj.getClass().equals(com.vividsolutions.jts.geom.MultiLineString.class))
				{
					com.vividsolutions.jts.geom.MultiLineString geom = (com.vividsolutions.jts.geom.MultiLineString)obj;
					for(int i=0; i < geom.getNumGeometries(); i++)
					{
						com.vividsolutions.jts.geom.Geometry g = geom.getGeometryN(i);
						com.vividsolutions.jts.geom.Coordinate [] coors = g.getCoordinates();
						PolygonStruct poly = new PolygonStruct(new java.awt.Dimension(1,1));
						poly.shapeID = (totalshapes+1);
						poly.shapeName = "Imported Line";
						//poly.SetBounds(geom.getBoundary()., bounds._rbo, bounds._ubo, bounds._bbo);
						for(int p = 0; p < coors.length; p++)
						{
							if(coors[p].x>180 || coors[p].x < -180)
							{
								no.ums.pas.ums.tools.CoorConverter conv = new CoorConverter();
								//assume UTM
								if(projection_code.toLowerCase().contains("utm") || (coors[p].x > 5000000))
								{
									LLCoor ll1 = conv.UTM2LL(23, coors[p].y, coors[p].x, "32V");
									poly.add_coor(ll1.get_lon(), ll1.get_lat());
								}
								else //assume RD
								{
									LLCoor ll = conv.rd_to_wgs84(coors[p].x, coors[p].y);
									poly.add_coor(ll.get_lon(), ll.get_lat());
								}
							}
							else
							{
								//assume lon/lat
								poly.add_coor(coors[p].x, coors[p].y);
							}

						}
						polylist.add(poly);

					}
				}
				else if(obj.getClass().equals(com.vividsolutions.jts.geom.LineSegment.class))
				{
					com.vividsolutions.jts.geom.LineSegment geom = (com.vividsolutions.jts.geom.LineSegment)obj;
					MapLine line = new MapLine();
					MapPointF p1 = new MapPointF(geom.p0.x, geom.p0.y);
					MapPointF p2 = new MapPointF(geom.p1.x, geom.p1.y);
					
					if(p1.x>180 || p1.x < -180)
					{
						no.ums.pas.ums.tools.CoorConverter conv = new CoorConverter();
						LLCoor ll1 = conv.UTM2LL(23, p1.y, p1.x, "32V");
						LLCoor ll2 = conv.UTM2LL(23, p2.y, p2.x, "32V");
						p1.x = ll1.get_lon();
						p1.y = ll1.get_lat();
						p2.x = ll2.get_lon();
						p2.y = ll2.get_lat();
						line.addPoint(p1);
						line.addPoint(p2);
					}
					else
					{
						//assume lon/lat
						line.addPoint(p1);
						line.addPoint(p2);
					}

					
					line.shapeID = (totalshapes+1);
					line.shapeName = "Imported Polygon";					
					linelist.add(line);
					totalshapes++;
				}
				
			}
			shape.close();
			m_object = this;
			
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}
	}

	byte b[];
	int fileLength;
	
	public List<PolygonStruct> polylist = new ArrayList<PolygonStruct>();
	public List<MapPointF> pointlist = new ArrayList<MapPointF>();
	public List<MapLine> linelist = new ArrayList<MapLine>();
	
	public ShapeImporter(URL url, ActionListener callback, String sz_action_eof)
	{
		super(url, callback, sz_action_eof);
		try
		{
			begin_parsing("");
		}
		finally{ }
	}
	
	public ShapeImporter(File f, ActionListener callback, String sz_action_eof)
	{
		super(f, callback, sz_action_eof);
		try
		{
			/*FileInputStream fileinputstream = new FileInputStream(f);
			fileLength = fileinputstream.available();
			b = new byte[fileLength];
			fileinputstream.read(b);
	
	        fileinputstream.close();*/
	        begin_parsing("");
		}
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			//parse();
			parse_with_geotools();
		}
		catch(Exception e)
		{
			
		}
		try {
			ActionFileLoaded event = new ActionFileLoaded(m_object, ActionEvent.ACTION_PERFORMED, "act_shape_parsing_complete", ImportPolygon.MIME_TYPE_SHP_);		
			m_callback.actionPerformed(event);
		} catch(Exception e) {
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("FileParser","Exception in run",e,1);
		}
		try {
			ActionEvent eof = new ActionEvent(get_object(), ActionEvent.ACTION_PERFORMED, get_action_eof());
			get_callback().actionPerformed(eof);
		} catch(Exception e) {
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("FileParser","Exception in run",e,1);
		}		
	}
	
	/*will be executed from thread*/
	public boolean parse()
	{
		try
		{
			int filecode = arr2intBig(b, 0);
			int filelength = arr2intBig(b, 24);
			long version = arr2long(b, 28);
			int shapetype = arr2int(b, 32);
			double xMin = arr2double(b, 36);
			double yMin = arr2double(b, 44);
			//yMin = 0 - yMin;
			double xMax = arr2double(b, 52);
			double yMax = arr2double(b, 60);
			//yMax = 0 - yMax;
			double zMin = arr2double(b, 68);
			double zMax = arr2double(b, 76);
			double mMin = arr2double(b, 84);
			double mMax = arr2double(b, 92);
			int currentPosition = 100;
			while(currentPosition < fileLength)
			{
				int recordStart = currentPosition;
				int recordNumber = arr2intBig(b, recordStart);
				int contentLength = arr2intBig(b, recordStart + 4);
				int recordContentStart = recordStart + 8;
				if(shapetype == 1)
				{
					MapPointF point = new MapPointF();
					
					int recordShapeType = arr2int(b, recordContentStart);
					point.x = arr2double(b, recordContentStart + 4);
					//point.y = 0 - arr2double(b, recordContentStart + 12);
					point.y = arr2double(b, recordContentStart + 12);
					pointlist.add(point);
				}
				if(shapetype == 3)
				{
					MapLine line = new MapLine();
					line.shapeID = recordNumber;
					line.shapeName = "Imported Line";
					int recordShapeType = arr2int(b, recordContentStart);
					NavStruct bounds = new NavStruct();
					bounds._lbo = arr2double(b, recordContentStart + 4);
					bounds._bbo = arr2double(b, recordContentStart + 12);
					bounds._rbo = arr2double(b, recordContentStart + 20);
					bounds._ubo = arr2double(b, recordContentStart + 28);
					line.SetBounds(bounds._lbo, bounds._rbo, bounds._ubo, bounds._bbo);
					line.numParts = arr2int(b, recordContentStart + 36);
					line.numPoints = arr2int(b, recordContentStart + 40);
					int partStart = recordContentStart + 44;
					for(int i=0; i < line.numParts; i++)
					{
						Integer part = arr2int(b, partStart + i * 4);
						line.parts.add(part);
					}
					int pointStart = recordContentStart + 44 + 4 * line.numParts;
					for(int i=0; i < line.numPoints; i++)
					{
						double x = arr2double(b, pointStart + (i*16));
						double y = arr2double(b, pointStart + (i*16) + 8);
						//y = 0 - y;
						line.addPoint(new MapPointF(x, y));
					}
					linelist.add(line);
				}
				if(shapetype == 5)
				{
					PolygonStruct poly = new PolygonStruct(new java.awt.Dimension(1,1));
					poly.shapeID = recordNumber;
					poly.shapeName = "Imported Polygon";
					
					int recordShapeType = arr2int(b, recordContentStart);
					NavStruct bounds = new NavStruct();
					bounds._lbo = arr2double(b, recordContentStart + 4);
					bounds._bbo = arr2double(b, recordContentStart + 12);
					bounds._rbo = arr2double(b, recordContentStart + 20);
					bounds._ubo = arr2double(b, recordContentStart + 28);
					poly.numParts = (int)arr2long(b, recordContentStart + 36);
					poly.SetBounds(bounds._lbo, bounds._rbo, bounds._ubo, bounds._bbo);
					long numPoints = arr2long(b, recordContentStart + 40);
					int partStart = recordContentStart + 44;
					for(int i=0; i < poly.numParts; i++)
					{
						poly.parts.add(arr2int(b, partStart + i * 4));
					}
					int pointStart = recordContentStart + 44 + 4 * poly.numParts;
					for(int i=0; i < numPoints; i++)
					{
						double x = arr2double(b, pointStart + (i*16));
						double y = arr2double(b, pointStart + (i*16) + 8);
						//y = 0 - y;
						poly.add_coor(x, y);
					}
					polylist.add(poly);
				}
				currentPosition = recordStart + (4 + contentLength) * 2;
			}
		}
		catch(Exception e)
		{
			return false;
		}
		m_object = this;
		return true;
	}
	
	public static double arr2double (byte[] arr, int start) {
		int i = 0;
		int len = 8;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
			cnt++;
		}
		long accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 64; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return Double.longBitsToDouble(accum);
	}
	
	public static long arr2long (byte[] arr, int start) {
		int i = 0;
		int len = 4;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
			cnt++;
		}
		long accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return accum;
	}
	
	public static int arr2int (byte[] arr, int start) {
	    /*int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 + i) * 8;
            value += (arr[4 - i + start] & 0xFF000000) << shift;
        }
        return value;*/
		return (int)((arr[start+3] << 24
                | arr[start+2] << 16
                    | arr[start+1] << 8
                    | arr[start+0])
                   & 0xEFFFFFFFL);
	}
	public static int arr2intBig (byte[] arr, int start) {
	    int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (arr[i + start] & 0x000000FF) << shift;
        }
        return value;
		/*int low = arr[start] & 0xff;
		int high = arr[start+1] & 0xff;
		return (int)( high << 8 | low );*/
		/*int i = 0;
		int len = 4;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
			cnt++;
		}
		long accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return (int)accum;		*/
	}
	
	public static float arr2float (byte[] arr, int start) {
		int i = 0;
		int len = 4;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
			cnt++;
		}
		int accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
	
		return Float.intBitsToFloat(accum);
	}
	
    /*public int readIntBig(byte[] data, int pos)
    {
        byte[] bytes = new byte[4];
        bytes[0] = data[pos];
        bytes[1] = data[pos+1];
        bytes[2] = data[pos+2];
        bytes[3] = data[pos+3];
        //ByteArray.Reverse(bytes);
        //return BitConverter.ToInt32(bytes, 0);
        Integer ret = new Integer(0);
        Bytearra
        b.
    }

    public int readIntLittle(byte[] data, int pos)
    {
        byte[] bytes = new byte[4];
        bytes[0] = data[pos];
        bytes[1] = data[pos + 1];
        bytes[2] = data[pos + 2];
        bytes[3] = data[pos + 3];
        return BitConverter.ToInt32(bytes, 0);
    }

    public double readDoubleLittle(byte[] data, int pos)
    {
        byte[] bytes = new byte[8];
        bytes[0] = data[pos];
        bytes[1] = data[pos + 1];
        bytes[2] = data[pos + 2];
        bytes[3] = data[pos + 3];
        bytes[4] = data[pos + 4];
        bytes[5] = data[pos + 5];
        bytes[6] = data[pos + 6];
        bytes[7] = data[pos + 7];
        return BitConverter.ToDouble(bytes, 0);
    }	*/
}