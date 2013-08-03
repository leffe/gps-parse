package com.tada.consumers.message;

import static com.tada.gpsparse.Constants.*;

import java.io.IOException;

import com.tada.consumers.generic.Consumer;
import com.tada.consumers.generic.Unsigned;
import com.tada.gpsparse.BlockList;
import com.tada.gpsparse.Packet;

public class CfgRate extends Consumer {

    private int mBytesLeft;
    private Consumer mCurConsumer;

    public Unsigned mFakeiTOW;
    private Unsigned mmeasRate;
    private Unsigned mnavRate;
    private Unsigned mtimeRef;

    public CfgRate(Packet packet) {
        super(packet);
        mFakeiTOW = new Unsigned(packet, 4, UBX_ITOW);
        mFakeiTOW.setValue(System.nanoTime());
        mmeasRate = new Unsigned(packet, 2, "measRate");
        mnavRate = new Unsigned(packet, 2, "navRate");
        mtimeRef = new Unsigned(packet, 2, "timeRef");
        mCurConsumer = mmeasRate;
    }

    
    @Override
    public Result consume(int i) throws IOException {
        super.consume(i);
        if (--mBytesLeft >= 0) {
            if (mCurConsumer != null){
                Result res = mCurConsumer.consume(i);
                
                if (res==Result.DONE){
                    mCurConsumer = nextConsumer(mCurConsumer);
                }
            }           
            if (mBytesLeft == 0) {
                BlockList.add(mFakeiTOW.getValue(), UBX_MEASRATE, mmeasRate.getValue());
                BlockList.add(mFakeiTOW.getValue(), UBX_NAVRATE, mnavRate.getValue());
                BlockList.add(mFakeiTOW.getValue(), UBX_TIMEREF, mtimeRef.getValue());
                System.out.println(fmtHex(mmeasRate.getValue()) + ", " +fmtHex(mnavRate.getValue()) + ", " +fmtHex(mtimeRef.getValue()));
                return Result.DONE;}
            else {
                return Result.CONTINUE;
            }
        } else {
            return Result.DONE;
        }
    }
    
    
    private String fmtHex(long val) {
        return String.format("0x%02X", val);
    }
    
    @Override
    public void enterState() {
        super.enterState();
        mFakeiTOW.setValue(System.nanoTime());
        mCurConsumer = mmeasRate;
        mCurConsumer.enterState();
        mBytesLeft = mPacket.getLength();
    }

    
    private Consumer nextConsumer(Consumer consumer) {
        Consumer retVal = null;
        if (consumer.equals(mmeasRate)){
            retVal = mnavRate;
        } else if (consumer.equals(mnavRate)) {
            retVal = mtimeRef;
        }
        if (retVal != null) {
            retVal.enterState();
        }
        return retVal;
    }


    
}
