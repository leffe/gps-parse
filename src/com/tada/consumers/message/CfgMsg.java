package com.tada.consumers.message;

import static com.tada.gpsparse.Constants.*;

import java.io.IOException;

import com.tada.consumers.generic.Consumer;
import com.tada.consumers.generic.Unsigned;
import com.tada.consumers.generic.Consumer.Result;
import com.tada.gpsparse.BlockList;
import com.tada.gpsparse.Packet;

public class CfgMsg extends MessageConsumer {

    public Unsigned mFakeiTOW;
    private Unsigned mmsgClass;
    private Unsigned mmsgID;
    private Unsigned mrate;

    public CfgMsg(Packet packet) {
        super(packet);
        mFakeiTOW = new Unsigned(packet, 4, UBX_ITOW);
        mFakeiTOW.setValue(System.nanoTime());
        mmsgClass = new Unsigned(packet, 1, "msgClass");
        mmsgID = new Unsigned(packet, 1, "msgID");
        mrate = new Unsigned(packet, 1, "rate");
        mCurConsumer = mmsgClass;
    }

    @Override
    public Result consume(int i) throws IOException {
        Result result = super.consume(i);
        
        if (result == Result.DONE) {
            mCurConsumer = nextConsumer(mCurConsumer);
        }

        if (mBytesLeft == 0 && result == Result.DONE) {
            // This message is completed. add it..
            BlockList.add(mFakeiTOW.getValue(), UBX_MSGCLASS, mmsgClass
                    .getValue());
            BlockList.add(mFakeiTOW.getValue(), UBX_MSGID, mmsgID.getValue());
            BlockList.add(mFakeiTOW.getValue(), UBX_RATE, mrate.getValue());
            System.out.println(BlockList.fmtHex(mmsgClass.getValue()) + ", "
                    + BlockList.fmtHex(mmsgID.getValue()) + ", "
                    + BlockList.fmtHex(mrate.getValue()));
        } else {
            result = Result.CONTINUE;
        }
        return result;
    }

    @Override
    public void enterState() {
        super.enterState();
        mFakeiTOW.setValue(System.nanoTime());
        mCurConsumer = mmsgClass;
        mCurConsumer.enterState();
        mBytesLeft = mPacket.getLength();
    }

    private Consumer nextConsumer(Consumer consumer) {
        Consumer retVal = null;
        if (consumer.equals(mmsgClass)) {
            retVal = mmsgID;
        } else if (consumer.equals(mmsgID)) {
            retVal = mrate;
        }
        if (retVal != null) {
            retVal.enterState();
        }
        return retVal;
    }

}
