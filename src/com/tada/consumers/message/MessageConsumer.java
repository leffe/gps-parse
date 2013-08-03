package com.tada.consumers.message;

import static com.tada.gpsparse.Constants.UBX_MSGCLASS;
import static com.tada.gpsparse.Constants.UBX_MSGID;
import static com.tada.gpsparse.Constants.UBX_RATE;

import java.io.IOException;

import com.tada.consumers.generic.Consumer;
import com.tada.consumers.generic.Consumer.Result;
import com.tada.gpsparse.BlockList;
import com.tada.gpsparse.Packet;

public class MessageConsumer extends Consumer{

    protected Consumer mCurConsumer;
    protected int mBytesLeft;

    protected MessageConsumer(Packet packet) {
        super(packet);
    }

    @Override
    public Result consume(int i) throws IOException {
        Result res = super.consume(i);

        if (--mBytesLeft >= 0) {
            if (mCurConsumer != null){
                res = mCurConsumer.consume(i);
            }
        }
        return res;
    }
}
