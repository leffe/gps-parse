package com.tada.gpsparse;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.tada.consumers.generic.Sync1;

public class GpsParse {

    private static Handler mHandler;
    private static BlockHandler mBlockHandler;

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        String mPathRoot = "E:\\Pul\\GPS-parse\\";

        ParseConfig(mPathRoot+"DeviceConfig\\config");
//        ParseUBX(mPathRoot);
        
    }

    private static void ParseUBX(String string) {
        Packet mPacket;
        long mTrimStart = 80389800;
        long mTrimEnd = 0;
        String mFileName = "GPS-25CfgNav5test.UBX";
        mTrimStart = 236862000;
        mTrimEnd = 237354000;
        File file = new File("E:\\Pul\\GPS-parse\\" + mFileName);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        // Output
        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(string + mFileName + "_out.kml");
            out = new BufferedWriter(fstream);
            try {
                fis = new FileInputStream(file);
                // Here BufferedInputStream is added for fast reading.
                bis = new BufferedInputStream(fis);
                dis = new DataInputStream(bis);

                mPacket = new Packet(out);

//                mBlockHandler = new BlockHandler(out);
//                mHandler = new Handler(mPacket, mBlockHandler);
                mHandler = new Handler(mPacket);

                while (dis.available() != 0) {
                    mHandler.consume(dis.read());
                }

                // BlockList.trimStartStop(mTrimStart, mTrimEnd);

                BlockList.filterOutInvalid(out);

                // BlockList.dump();
                BlockList.dump(string + mFileName + "_out.csv");

                BlockList.genKml(out);
                // BlockList.genFlight(out);
                fis.close();
                bis.close();
                dis.close();
                out.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    
    private static void ParseConfig(String string) {
        Packet packet;

        
        File file = new File(string);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(string + "_out.txt");
            out = new BufferedWriter(fstream);
            try {
                fis = new FileInputStream(file);
                // Here BufferedInputStream is added for fast reading.
                bis = new BufferedInputStream(fis);
                dis = new DataInputStream(bis);

                packet = new Packet(out);

//                mBlockHandler = new BlockHandler(out);
//                mHandler = new Handler(mPacket, mBlockHandler);
                mHandler = new Handler(packet);

                while (dis.available() != 0) {
                    mHandler.consume(dis.read());
                }
                fis.close();
                bis.close();
                dis.close();
                
                BlockList.dump();
                
                out.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }
}
