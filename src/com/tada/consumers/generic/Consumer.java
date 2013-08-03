package com.tada.consumers.generic;

import static com.tada.gpsparse.Constants.UBX_MSGCLASS;
import static com.tada.gpsparse.Constants.UBX_MSGID;
import static com.tada.gpsparse.Constants.UBX_RATE;

import java.io.IOException;

import com.tada.gpsparse.BlockList;
import com.tada.gpsparse.Packet;

public abstract class Consumer {

	public enum Result {DONE, CONTINUE, CREATE, INVALID}


	private boolean mAddChecksum = true;

//	protected Consumer mCurConsumer;
//    protected int mBytesLeft;
	protected Packet mPacket;
	
	protected Consumer(Packet packet) {
		mPacket = packet;
	}
	
	Consumer(Packet packet, boolean dontAddChecksum) {
		mAddChecksum = !dontAddChecksum;
		mPacket = packet;
	}
	
	
	public Result consume(int i) throws IOException{
		if (mAddChecksum) {
			mPacket.addToChecksum(i);
		}
		
		
		
//        if (--mBytesLeft >= 0) {
//            if (mCurConsumer != null){
//                Result res = mCurConsumer.consume(i);
//                
//                if (res==Result.DONE){
//                    mCurConsumer = nextConsumer(mCurConsumer);
//                }
//            }           
//            if (mBytesLeft == 0) {
//                BlockList.add(mFakeiTOW.getValue(), UBX_MSGCLASS, mmsgClass.getValue());
//                BlockList.add(mFakeiTOW.getValue(), UBX_MSGID, mmsgID.getValue());
//                BlockList.add(mFakeiTOW.getValue(), UBX_RATE, mrate.getValue());
//                System.out.println(fmtHex(mmsgClass.getValue()) + ", " +fmtHex(mmsgID.getValue()) + ", " +fmtHex(mrate.getValue()));
//                return Result.DONE;}
//            else {
//                return Result.CONTINUE;
//            }
//        } else {
//            return Result.DONE;
//        }
//		
		
		
		
		
		
		
		
		return Result.DONE;
	}


	public void enterState() {
	}

}
