package com.tada.gpsparse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.lang.Math;
import static com.tada.gpsparse.Constants.*;

public class BlockList {
	
//	
//	public static final String UBX_ITOW = new String("iTow");
//	public static final String UBX_SPEED = new String("speed");
//	public static final String UBX_GSPEED = new String("gSpeed");
//	public static final String UBX_HEIGHT = new String("Height");
//	public static final String UBX_HMSL = new String("hMSL");
//	public static final String UBX_LAT = new String("Lat");
//	public static final String UBX_LON = new String("Lon");
//	public static final String UBX_HEADING = new String("heading");
//	public static final String UBX_VELN = new String("velN");
//	public static final String UBX_VELE = new String("velE");
//	public static final String UBX_VELD = new String("velD");
//	
//
//	
//	public static final String UBX_PACC = new String("pAcc");
//	public static final String UBX_SACC = new String("sAcc");
//	public static final String UBX_FLAGS = new String("flags");
//	public static final String UBX_GPSFIX = new String("gpsFix");

    int mCalibratedheight = 0;

	static TreeMap<Long, Map<String, Object>>mBlockList = new TreeMap<Long, Map<String, Object>>();
//	static TreeMap<Long, Map<String, Object>>mFilteredBlockList = new TreeMap<Long, Map<String, Object>>();
//	static TreeMap<Long, Map<String, Object>>mTempBlockList = new TreeMap<Long, Map<String, Object>>();
	
	public static void add(long blockKey, String key, Object value) {
//		System.out.println("***** NULL for blockKey:"+blockKey+" key:"+key);
		Map<String, Object> block = mBlockList.get(blockKey);
		if (block == null) {
			block = new HashMap<String, Object>();
		}
		block.put(key, value);
		mBlockList.put(blockKey, block);
//		System.out.println("Block list lengt: " + mBlockList.size());
	}
	
	public static void dump() {
		Set<Entry<Long, Map<String, Object>>> set = mBlockList.entrySet();
		Iterator<Entry<Long, Map<String, Object>>> i = set.iterator(); 
		while(i.hasNext()) { 
			Entry<Long, Map<String, Object>> me = i.next(); 
			Map<String, Object> block = me.getValue();
			for (Iterator<Entry<String, Object>> it=block.entrySet().iterator(); it.hasNext(); ) {
				Entry <String, Object>blockEntry = it.next();
				System.out.print("dump "+ me.getKey()+",  "+blockEntry.getKey() + ": "); 
				System.out.println(blockEntry.getValue());
			}
		} 	
	}

	public static void dump(String dumpFileName) {
		
		FileWriter fstream = null;
		 BufferedWriter out = null;
		 try {
			fstream = new FileWriter(dumpFileName);
			out = new BufferedWriter(fstream);
		
			Set<Entry<Long, Map<String, Object>>> set = mBlockList.entrySet();
			Iterator<Entry<Long, Map<String, Object>>> i = set.iterator(); 

			// Print header row
			if (i.hasNext()) {
				out.append(String.format("Id, ")); 
				Entry<Long, Map<String, Object>> me = i.next(); 
				Map<String, Object> block = me.getValue();
				for (Iterator<Entry<String, Object>> it=block.entrySet().iterator(); it.hasNext(); ) {
					Entry <String, Object>blockEntry = it.next();
					out.append(String.format(blockEntry.getKey().toString()+", ")); 
				}
				out.newLine(); 
				
			}

			i = set.iterator();
			while(i.hasNext()) { 
				Entry<Long, Map<String, Object>> me = i.next(); 
				Map<String, Object> block = me.getValue();
				out.append(String.format(me.getKey()+", ")); 
				for (Iterator<Entry<String, Object>> it=block.entrySet().iterator(); it.hasNext(); ) {
					Entry <String, Object>blockEntry = it.next();
//					out.append(String.format(blockEntry.getKey().toString()+", ")); 
					out.append(String.format(blockEntry.getValue().toString()+", ")); 
					
				}
				out.newLine(); 
			} 	
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

    public static void trimStartStop(long trimStart, long trimEnd) throws IOException 
    {
        TreeMap<Long, Map<String, Object>>tempBlockList = new TreeMap<Long, Map<String, Object>>();
        int removed = 0;
        Set<Entry<Long, Map<String, Object>>> set = mBlockList.entrySet();
        Iterator<Entry<Long, Map<String, Object>>> i = set.iterator();
        System.out.println("Trim leading and traling blocks. Nbr. blocks: " + mBlockList.size());
        tempBlockList.clear();
        while(i.hasNext()) { 
            Entry<Long, Map<String, Object>> me = i.next(); 
            Map<String, Object> block = me.getValue();
            
            
            if (me.getKey() < trimStart || me.getKey() > trimEnd){
                
//              System.out.println("remove:" + me.getKey());
//              mBlockList.remove(me.getKey());
                tempBlockList.put(me.getKey(), block);
            }
        } 
        
        
        for (Iterator<Entry<Long, Map<String, Object>>> it=tempBlockList.entrySet().iterator(); it.hasNext(); ) {
            Entry<Long, Map<String, Object>> me = it.next();
            System.out.println("trim loop remove:" + me.getKey());
            mBlockList.remove(me.getKey());
            removed++;
        }       
        
        System.out.println("Nbr. blocks after trim: " + mBlockList.size()+", removed: "+removed);
    }
    
	public static void filterOutInvalid(BufferedWriter out) throws IOException 
	{
		TreeMap<Long, Map<String, Object>>filteredBlockList = new TreeMap<Long, Map<String, Object>>();

		int removed = 0;
		Set<Entry<Long, Map<String, Object>>> set = mBlockList.entrySet();
		Iterator<Entry<Long, Map<String, Object>>> i = set.iterator();
		System.out.println("Filtering invalid blocks. Nbr. blocks: " + mBlockList.size());
		while(i.hasNext()) { 
			Entry<Long, Map<String, Object>> me = i.next(); 
			Map<String, Object> block = me.getValue();
//			for (Iterator<Entry<String, Object>> it=block.entrySet().iterator(); it.hasNext(); ) {
//				Entry <String, Object>blockEntry = it.next();
//				System.out.print("--- "+ me.getKey()+",  "+blockEntry.getKey() + ": "); 
//				System.out.println(blockEntry.getValue());
//			}
			if (block.get(UBX_LAT) == null ||
					block.get(UBX_LON) == null ||
					block.get(UBX_GPSFIX) == null || 
					(block.get(UBX_GPSFIX) != null && ((Long) block.get(UBX_GPSFIX)).intValue() < 3) ||
					(block.get(UBX_PACC) != null && ((Long) block.get(UBX_PACC)).intValue() > 8000)){
				
//				System.out.println("remove:" + me.getKey());
//				mBlockList.remove(me.getKey());
				filteredBlockList.put(me.getKey(), block);
			}
		} 
		
		
		for (Iterator<Entry<Long, Map<String, Object>>> it=filteredBlockList.entrySet().iterator(); it.hasNext(); ) {
			Entry<Long, Map<String, Object>> me = it.next();
			System.out.println("rem loop remove:" + me.getKey());
			mBlockList.remove(me.getKey());
			removed++;
		}		
		
		System.out.println("Nbr. blocks after filtering: " + mBlockList.size()+", removed: "+removed);
	}
	static Double alpha = null;

	public static void genFlight(BufferedWriter out) throws IOException 
	{
		out.append(String.format("<kml xmlns=\"http://www.opengis.net/kml/2.2\"")); out.newLine();
		out.append(String.format("  xmlns:gx=\"http://www.google.com/kml/ext/2.2\">")); out.newLine();

		out.append(String.format("  <gx:Tour>")); out.newLine();
		out.append(String.format("    <name>Flight example</name>")); out.newLine();
		out.append(String.format("    <gx:Playlist>")); out.newLine();
		
		
		Set<Entry<Long, Map<String, Object>>> set = mBlockList.entrySet();
		Iterator<Entry<Long, Map<String, Object>>> i = set.iterator(); 
		DecimalFormat df = new DecimalFormat("#.#########");
		
		Map<String, Object> currentBlock = null;
		long currentItow = 0;
		Map<String, Object> previousBlock = null;
		long previousItow = 0;
		while(i.hasNext()) { 
			previousBlock = currentBlock;
			previousItow = currentItow;
			Entry<Long, Map<String, Object>> me = i.next(); 
			currentBlock = me.getValue();
			currentItow = me.getKey();
			if (previousItow == 0) {
				previousItow = currentItow;
			}
			
			Double duration = new Double(currentItow-previousItow);
			duration = duration / 1000;
			
			System.out.println(duration);
			
		out.append(String.format("      <gx:FlyTo>")); out.newLine();
		out.append(String.format("        <gx:duration>"+duration+"</gx:duration>")); out.newLine();
		out.append(String.format("        <gx:flyToMode>smooth</gx:flyToMode>")); out.newLine();
		out.append(String.format("        <Camera>")); out.newLine();
		out.append(String.format("          <longitude>"+ getStringFromDouble(currentBlock, UBX_LON, 10000000) + "</longitude>")); out.newLine();
		out.append(String.format("          <latitude>" + getStringFromDouble(currentBlock, UBX_LAT, 10000000) + "</latitude>")); out.newLine();
		out.append(String.format("          <altitude>"+ getStringFromDouble(currentBlock, UBX_HMSL, 1000) + "</altitude>")); out.newLine();
		out.append(String.format("          <heading>"+ getStringFromDouble(currentBlock, UBX_HEADING, 100000) + "</heading>")); out.newLine();
		Long gSpeed = (Long) currentBlock.get(UBX_GSPEED);
		Integer velD = (Integer) currentBlock.get(UBX_VELD);

//		alpha = Math.atan((new Double(gSpeed).doubleValue())/(new Double(velD)).doubleValue());
		alpha = Math.atan((new Double(velD).doubleValue())/(new Double(gSpeed)).doubleValue());
		alpha = alpha/Math.PI*180*-1+90;
		
		String sAlpha = String.format("%2.0f", alpha);
		Long speed = (Long) currentBlock.get(UBX_SPEED);
		
//		System.out.println(sAlpha);
		if (speed <400) {
			out.append(String.format("          <tilt>"+90+"</tilt>")); out.newLine();
			
		}else{
			out.append(String.format("          <tilt>"+sAlpha+"</tilt>")); out.newLine();
			
		}
		out.append(String.format("          <roll>0</roll>")); out.newLine();
//		out.append(String.format("			    <range>5000</range>")); out.newLine();
		
		
		
//		" + getStringFromLong(block, UBX_SPEED, 100) +"
		if (speed < 400) {
			out.append(String.format("        <altitudeMode>clampToGround</altitudeMode>")); out.newLine();
		}
		else {
			out.append(String.format("        <altitudeMode>absolute</altitudeMode>")); out.newLine();
		}
//		out.append(String.format("        <altitudeMode>absolute</altitudeMode>")); out.newLine();

		out.append(String.format("        </Camera>")); out.newLine();
		out.append(String.format("      </gx:FlyTo>")); out.newLine();
		} 	
		out.append(String.format("    </gx:Playlist>")); out.newLine();
		out.append(String.format("  </gx:Tour>")); out.newLine();
		out.append(String.format("</kml>")); out.newLine();		
		
	}
	
	public static void genKml(BufferedWriter out) throws IOException 
	{
//		<?xml version="1.0" standalone="yes"?>
//		<kml xmlns="http://earth.google.com/kml/2.2">
//		  <Document>

//	    RefHeight mRefHeight = new RefHeight();
	    int refHeight= RefHeight.calculate(mBlockList);

		out.append(String.format("<?xml version=\"1.0\" standalone=\"yes\"?>")); out.newLine();
		out.append(String.format("<kml xmlns=\"http://earth.google.com/kml/2.2\">")); out.newLine();
		out.append(String.format("  <Document>")); out.newLine();
		out.append(String.format("    <Folder id=\"Tracks\">")); out.newLine();
		out.append(String.format("      <Folder id=\"track 1\">")); out.newLine();
		out.append(String.format("        <Folder id=\"track 1 points\">")); out.newLine();
		
		Set<Entry<Long, Map<String, Object>>> set = mBlockList.entrySet();
		Iterator<Entry<Long, Map<String, Object>>> i = set.iterator(); 
		DecimalFormat df = new DecimalFormat("#.#########");
		while(i.hasNext()) { 
			Entry<Long, Map<String, Object>> me = i.next(); 
			Map<String, Object> block = me.getValue();
//			for (Iterator<Entry<String, Object>> it=block.entrySet().iterator(); it.hasNext(); ) {
//				Entry <String, Object>blockEntry = it.next();
				
			
//			Long gpsFix = (Long) block.get(UBX_GPSFIX);
//			if (gpsFix != null && gpsFix < 2) continue;
			
				out.append(String.format("          <Placemark>")); out.newLine();
				out.append(String.format("            <Point>")); out.newLine();
				out.append(String.format("              <altitudeMode>absolute</altitudeMode>")); out.newLine();
              
				/*
				out.append(String.format("              <coordinates>12.764176333,56.053396333,46.700</coordinates>
              
              */
				
				
				
//				System.out.print("--- "+ me.getKey()+",  "+blockEntry.getKey() + ": "); 
				
//				Integer lon = (Integer) block.get(UBX_LON);
//				Double dLon = new Double(lon)/10000000;
//				Integer lat = (Integer) block.get(UBX_LAT);
//				Double dLat = new Double(lat)/10000000;
//				Integer height = (Integer) block.get(UBX_HMSL);
//				Double dHeight = new Double(height)/1000;
//			    System.out.println(df.format(d));
//				System.out.println(d);
				
				out.append(String.format("              <coordinates>" + getStringFromDouble(block, UBX_LON, 10000000) + "," +
						getStringFromDouble(block, UBX_LAT, 10000000) + "," +
						getStringFromDouble(block, UBX_HMSL, 1000) + "</coordinates>")); out.newLine();
//				out.append(String.format("              <tessellate>0</tessellate>"));out.newLine();
				out.append(String.format("            </Point>")); out.newLine();
				out.append(String.format("            <Style>")); out.newLine();
				out.append(String.format("              <IconStyle>")); out.newLine();
				out.append(String.format("                <color>" + getColorFromSpeed((Long) block.get(UBX_SPEED)) + "</color>")); out.newLine();
				out.append(String.format("              </IconStyle>")); out.newLine();
				out.append(String.format("              <LabelStyle>")); out.newLine();
				out.append(String.format("                <color>FF0000E6</color>")); out.newLine();
				out.append(String.format("              </LabelStyle>")); out.newLine();
				out.append(String.format("            </Style>")); out.newLine();
                out.append(String.format("            <description><![CDATA[<b>trackpoint #963</b><br/> " +
                		"<i>Latitude:</i> 56.0533963 &#176;<br/> " +
                		"<i>Longitude:</i> 12.7641763 &#176;<br/> " +
//                        "<i>Elevation:</i> 46.7 m<br/> " +
                        "<i>Elevation (Ellipsoid):</i> " + getStringFromInteger(block, UBX_HEIGHT, 1000) + " m<br/> " +
                        "<i>Elevation (Sea level):</i> " + getStringFromInteger(block, UBX_HMSL, 1000) + " m<br/> " +
                        "<i>Elevation (From start):</i> " + getStringFromInteger1(getInteger(block, UBX_HEIGHT)-refHeight, 1000) + " m<br/> " +
//                		"<i>Speed:</i> 0.6 km/h<br/> " +
                		"<i>Speed:</i> " + getStringFromLong(block, UBX_SPEED, 100) +"  m/s<br/> " +
                		
                		
                		
                		                		
                		
                		"<i>HDOP:</i> 0.8 <br/> " +
                		"<i>Satellites:</i> 11 <br/> " +
                		"<i>fix:</i> 1.0 <br/> " +
                		"<i>Time:</i> 2010-08-10 09:09:16.2 ]]>" +
                		"</description>")) ; out.newLine();

				
				String name = Long.toString(me.getKey());
				
				out.append(String.format("            <name>"+name+"</name>")); out.newLine();
				out.append(String.format("            <styleUrl>#gv_trackpoint</styleUrl>")); out.newLine();
				out.append(String.format("          </Placemark>")); out.newLine();
 
 			
				
				
				
//				System.out.print("--- "+ me.getKey()+",  "+blockEntry.getKey() + ": "); 
//				System.out.println(blockEntry.getValue());
				
				
/*
  
 */				
				
				
				
				
//			}
		} 	
		
		
		
		
		
		
		
		
		
		
		
		
		out.append(String.format("        <name>*Points</name>")); out.newLine();
		

		out.append(String.format("        </Folder>")); out.newLine();
		out.append(String.format("        <name>Flight</name>")); out.newLine();
		out.append(String.format("      </Folder>")); out.newLine();
		out.append(String.format("      <name>Tracks</name>")); out.newLine();
		out.append(String.format("      <open>0</open>")); out.newLine();
		out.append(String.format("      <visibility>1</visibility>")); out.newLine();
		out.append(String.format("    </Folder>")); out.newLine();
		
		out.append(String.format("    <Style id=\"gv_trackpoint_normal\">")); out.newLine();
		out.append(String.format("      <BalloonStyle>")); out.newLine();
		out.append(String.format("        <text><![CDATA[<p align=\"left\" style=\"white-space:nowrap;\"><font size=\"+1\"><b>$[name]</b></font></p> <p align=\"left\">$[description]</p>]]></text>")); out.newLine();
		out.append(String.format("      </BalloonStyle>")); out.newLine();
		out.append(String.format("      <IconStyle>")); out.newLine();
		out.append(String.format("        <Icon>")); out.newLine();
		out.append(String.format("          <href>http://maps.google.ca/mapfiles/kml/pal2/icon26.png</href>")); out.newLine();
		out.append(String.format("        </Icon>")); out.newLine();
		out.append(String.format("        <scale>0.3</scale>")); out.newLine();
		out.append(String.format("      </IconStyle>")); out.newLine();
		out.append(String.format("      <LabelStyle>")); out.newLine();
		out.append(String.format("        <scale>0</scale>")); out.newLine();
		out.append(String.format("      </LabelStyle>")); out.newLine();
		out.append(String.format("    </Style>")); out.newLine();
		out.append(String.format("    <Style id=\"gv_trackpoint_highlight\">")); out.newLine();
		out.append(String.format("      <BalloonStyle>")); out.newLine();
		out.append(String.format("        <text><![CDATA[<p align=\"left\" style=\"white-space:nowrap;\"><font size=\"+1+\"><b>$[name]</b></font></p> <p align=\"left\">$[description]</p>]]></text>")); out.newLine();
		out.append(String.format("      </BalloonStyle>")); out.newLine();
		out.append(String.format("      <IconStyle>")); out.newLine();
		out.append(String.format("        <Icon>")); out.newLine();
		out.append(String.format("          <href>http://maps.google.ca/mapfiles/kml/pal2/icon26.png</href>")); out.newLine();
		out.append(String.format("        </Icon>")); out.newLine();
		out.append(String.format("        <scale>0.4</scale>")); out.newLine();
		out.append(String.format("      </IconStyle>")); out.newLine();
		out.append(String.format("      <LabelStyle>")); out.newLine();
		out.append(String.format("        <scale>1</scale>")); out.newLine();
		out.append(String.format("      </LabelStyle>")); out.newLine();
		out.append(String.format("    </Style>")); out.newLine();
		
		out.append(String.format("    <StyleMap id=\"gv_trackpoint\">")); out.newLine();
		out.append(String.format("      <Pair>")); out.newLine();
		out.append(String.format("        <key>normal</key>")); out.newLine();
		out.append(String.format("        <styleUrl>#gv_trackpoint_normal</styleUrl>")); out.newLine();
		out.append(String.format("      </Pair>")); out.newLine();
		out.append(String.format("     <Pair>")); out.newLine();
		out.append(String.format("       <key>highlight</key>")); out.newLine();
		out.append(String.format("       <styleUrl>#gv_trackpoint_highlight</styleUrl>")); out.newLine();
		out.append(String.format("     </Pair>")); out.newLine();
		out.append(String.format("  </StyleMap>")); out.newLine();
		
		out.append(String.format("  <name>*TestFile</name>")); out.newLine();
		out.append(String.format("  <open>1</open>")); out.newLine();
		out.append(String.format("  <visibility>1</visibility>")); out.newLine();
		out.append(String.format("  </Document>")); out.newLine();
		out.append(String.format("</kml>")); out.newLine();
		
	}
	
	private static Double getDouble(final Map<String, Object> block, final String key) {
		Integer lon = (Integer) block.get(key);
		if (lon == null) return null;
		Double dLon = new Double(lon);
		return dLon;
	}
	private static String getStringFromDouble(final Map<String, Object> block, final String key, int divisor) {
		Double d = getDouble(block, key);
		if (d == null) return "";
		return Double.toString(d/divisor);
	}
    private static String getStringFromLong(final Map<String, Object> block, final String key, int divisor) {
        Long lon = (Long) block.get(key);
        if (lon == null) return "null";
        Double dLon = new Double(lon)/divisor;
        return Double.toString(dLon);
    }

    private static Integer getInteger(final Map<String, Object> block, final String key) {
        Integer lon = (Integer) block.get(key);
        if (lon == null) return 0;
        return lon;
    }

    private static String getStringFromInteger1(int value, int divisor) {
        Double dLon = new Double(value)/divisor;
        return fmt(dLon);
    }

//    private static String getStringFromInteger(final Map<String, Object> block, final String key, int divisor) {
//        Integer lon = (Integer) block.get(key);
//        if (lon == null) return "null";
//        Double dLon = new Double(lon)/divisor;
//        return fmt(dLon);
//    }

    
    private static String getStringFromInteger(final Map<String, Object> block, final String key, int divisor) {
        Integer lon = getInteger(block, key);
        if (lon == 0) return "null";
        Double dLon = new Double(lon)/divisor;
        return fmt(dLon);
    }
    
    
	private static String convertToHexByte(long l, boolean swap){
		long x = l*10000;
		x = x/2500;
		x= x*255;
		x=x/10000;
		if (swap) {
			x = 255 - x;
		}
		String hx = String.format("%02X", x);
//		System.out.println(hx);
		return hx;
	}
	
	private static String getColorFromSpeed(final Long lon) {
		//Long lon = (Long) block.get(key);
		if (lon == null) return "FF000000";
		String retVal;
		if (lon <= 2500 ) {
			
//			long x = lon*10000;
//			x = x/2500;
//			x= x*256;
//			x=x/10000;
//			String hxx = Long.toHexString(x);
//			String hx = String.format("%02X", x);
//			System.out.println(hx);
			
			
			
			retVal = new String("FFFF"+convertToHexByte(lon-1, false)+"00");
		} else if (lon <= 5000){
			retVal = new String("FF"+convertToHexByte(lon-2500-1, true)+"FF00");
		} else if (lon <= 7500){
			retVal = new String("FF00FF"+convertToHexByte(lon-5000-1, false));
        } else {
			retVal = new String("FF00"+convertToHexByte(lon-7500-1, true)+"FF");
        }
		
		
		
		return retVal;
	}
	
    public static String fmt(float arg) {
        String sX = String.format("%2.1f", arg);

        return sX;
    }

    public static String fmt(double arg) {
        return fmt((float)arg);
    }
    public static double rtp = (Math.PI/180);

    public static String fmtdeg(double arg) {
        return fmt((float)(arg/rtp));
    }

    
    public static String fmtHex(long val) {
        return String.format("0x%02X", val) + "(" +val+ ")";
    }

    
}
