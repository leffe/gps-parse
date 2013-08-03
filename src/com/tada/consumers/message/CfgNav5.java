package com.tada.consumers.message;

import static com.tada.gpsparse.Constants.*;

import java.io.IOException;

import com.tada.consumers.generic.Checksum;
import com.tada.consumers.generic.Consumer;
import com.tada.consumers.generic.Unsigned;
import com.tada.consumers.generic.Consumer.Result;
import com.tada.gpsparse.BlockList;
import com.tada.gpsparse.Packet;

public class CfgNav5 extends MessageConsumer {

    public Unsigned mFakeiTOW;
//    private Unsigned mmeasRate;
//    private Unsigned mnavRate;
//    private Unsigned mtimeRef;
    private Checksum mChecksumConsumer;

    public CfgNav5(Packet packet, Checksum checksum) {
        super(packet);
        mChecksumConsumer = checksum;
        mFakeiTOW = new Unsigned(packet, 4, UBX_ITOW);
        mFakeiTOW.setValue(System.nanoTime());
//        mmeasRate = new Unsigned(packet, 2, "measRate");
//        mnavRate = new Unsigned(packet, 2, "navRate");
//        mtimeRef = new Unsigned(packet, 2, "timeRef");
        mCurConsumer = mChecksumConsumer;
    }

    
    @Override
    public Result consume(int i) throws IOException {
//        Result result = super.consume(i);
//        
//        if (result == Result.DONE) {
//            mCurConsumer = nextConsumer(mCurConsumer);
//        }
//
//        if (mBytesLeft == 0 && result == Result.DONE) {
//            // Empty message..
//            System.out.println("Empty message");
//        } else {
//            result = Result.CONTINUE;
//        }
        Result result = mChecksumConsumer.consume(i);
        return result;
    }
    
    
    private String fmtHex(long val) {
        return String.format("0x%02X", val);
    }
    
    @Override
    public void enterState() {
        super.enterState();
        mFakeiTOW.setValue(System.nanoTime());
        mCurConsumer = mChecksumConsumer;
        mCurConsumer.enterState();
        mBytesLeft = mPacket.getLength();
    }

    
    private Consumer nextConsumer(Consumer consumer) {
        Consumer retVal = null;
        return retVal;
    }


    
}
