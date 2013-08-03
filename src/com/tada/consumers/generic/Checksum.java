package com.tada.consumers.generic;

import java.io.IOException;

import com.tada.gpsparse.BlockList;
import com.tada.gpsparse.Packet;

public class Checksum extends Unsigned {

    private final Packet mPacket;

    public Checksum(Packet packet) {
        super(packet, 2, "Checksum", true);
        mPacket = packet;
    }

    @Override
    public Result consume(int i) throws IOException {
        Result retVal = super.consume(i);

        if (retVal == Result.DONE) {

            long chk = getValue();

            short ckA = (short) (chk % 256);
            short ckB = (short) (chk / 256);

            if (ckA == mPacket.getmCkA() && ckB == mPacket.getmCkB()) {
                System.out.println("Checksum is ok");
            } else {
                System.out.println("Checksum fails. Actual: "
                        + BlockList.fmtHex(ckA) + ", " + BlockList.fmtHex(ckB));
                System.out.println("Calculated:"
                        + BlockList.fmtHex(mPacket.getmCkA()) + ", "
                        + BlockList.fmtHex(mPacket.getmCkB()));
            }
        }
        return retVal;
    }
}
