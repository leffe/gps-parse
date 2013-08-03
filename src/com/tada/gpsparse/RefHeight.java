package com.tada.gpsparse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import static com.tada.gpsparse.Constants.*;

public class RefHeight {
    
    
//    public int mCalulatedHeight = 0;

    public static int calculate(TreeMap<Long, Map<String, Object>> mBlockList) {
        int mean = 0;
            int mNbrValues = 0;
            List<Integer> values = new ArrayList<Integer>();
            Set<Entry<Long, Map<String, Object>>> set = mBlockList.entrySet();
            Iterator<Entry<Long, Map<String, Object>>> i = set.iterator();
            System.out.println("Calculating reference height" + mBlockList.size());
            values .clear();
            while(i.hasNext()) { 
                Entry<Long, Map<String, Object>> me = i.next(); 
                Map<String, Object> block = me.getValue();

                // Not needed if we know the list is already filtered
                if (block.get(UBX_LAT) == null ||
                        block.get(UBX_LON) == null ||
                        block.get(UBX_GPSFIX) == null || 
                        (block.get(UBX_GPSFIX) != null && ((Long) block.get(UBX_GPSFIX)).intValue() < 3) ||
                        (block.get(UBX_PACC) != null && ((Long) block.get(UBX_PACC)).intValue() > 8000)){
                    mNbrValues = 0;
                    values.clear();
                    continue;
                }

                int height = (Integer)block.get(UBX_HEIGHT);
                if (mNbrValues < 10) {
                    System.out.println("using: "+  me.getKey()+ " height:" + height);
                    values.add(height);
                    ++mNbrValues;
                } else {
                    // Check if good enough
                    mean = within(values, 10);
                    if (mean != 0) {
                        break;
                    } else {
                        values.remove(0);
                        values.add(height);
                    }
                }
            } 
            return mean;
        }

    private static int within(List<Integer> values, int limit) {
        int numberValues = values.size();
        int sum = 0;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (Iterator<Integer> it = values.iterator(); it.hasNext();) {
            int x = it.next();
            max = x > max ? x : max;
            min = x < min ? x : min;
            System.out.println("adding :" + x);
            sum+= x;
        }
        int mean = sum/numberValues;
        System.out.println("Mean: " + mean + ", diff: " + (max-min));
        if ((max-min) <= limit) {
            return mean;
        }
        return 0;
    }
}
