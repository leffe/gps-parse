package com.tada.consumers.message;

import static com.tada.gpsparse.Constants.UBX_GSPEED;
import static com.tada.gpsparse.Constants.UBX_HEADING;
import static com.tada.gpsparse.Constants.UBX_ITOW;
import static com.tada.gpsparse.Constants.UBX_SPEED;
import static com.tada.gpsparse.Constants.UBX_VELD;
import static com.tada.gpsparse.Constants.UBX_VELE;
import static com.tada.gpsparse.Constants.UBX_VELN;

import java.io.IOException;

import com.tada.consumers.generic.Consumer;
import com.tada.consumers.generic.Int;
import com.tada.consumers.generic.Unsigned;
import com.tada.gpsparse.BlockList;
import com.tada.gpsparse.Packet;

public class NavVelNed extends Consumer{
	private int mBytesLeft;
	private Consumer mCurConsumer;

	public Unsigned miTOW;
	private Int<Integer> mvelN;
	private Int<Integer> mvelE;
	private Int<Integer> mvelD;
	private Unsigned mspeed;
	private Unsigned mgSpeed;
	private Int<Integer> mheading;
	private Unsigned msAcc;
	private Unsigned mcAcc;

	public NavVelNed(Packet packet) {
		super(packet);
		miTOW = new Unsigned(packet, 4, UBX_ITOW);
		mvelN = new Int<Integer>(new Integer(0),packet, UBX_VELN);
		mvelE = new Int<Integer>(new Integer(0),packet, UBX_VELE);
		mvelD = new Int<Integer>(new Integer(0),packet, UBX_VELD);
		mspeed = new Unsigned(packet, 4, UBX_SPEED);
		mgSpeed = new Unsigned(packet, 4, UBX_GSPEED);
		mheading = new Int<Integer>(new Integer(0),packet, UBX_HEADING);
		msAcc = new Unsigned(packet, 4, "sAcc");
		mcAcc = new Unsigned(packet, 4, "cAcc");
		
		mCurConsumer = miTOW;
	}

	@Override
	public Result consume(int i) throws IOException {
		super.consume(i);
//		System.out.println("NavPosllh: " + i + 
//				", byte: " + mBytesLeft +
//				", ChkA: " + Integer.toHexString(mPacket.getmCkA()) +
//				", ChkB: " + Integer.toHexString(mPacket.getmCkB()) );
		if (--mBytesLeft >= 0) {
			if (mCurConsumer != null){
				Result res = mCurConsumer.consume(i);
				
				if (res==Result.DONE){
					mCurConsumer = nextConsumer(mCurConsumer);
				}
			}			
			if (mBytesLeft == 0) {
//				mPacket.getOutstream().append(String.valueOf(mspeed.mValue)+", ");
				BlockList.add(miTOW.getValue(), UBX_SPEED, mspeed.getValue());
				BlockList.add(miTOW.getValue(), UBX_GSPEED, mgSpeed.getValue());
				BlockList.add(miTOW.getValue(), UBX_HEADING, mheading.getValue());
				BlockList.add(miTOW.getValue(), UBX_VELN, mvelN.getValue());
				BlockList.add(miTOW.getValue(), UBX_VELE, mvelE.getValue());
				BlockList.add(miTOW.getValue(), UBX_VELD, mvelD.getValue());
				return Result.DONE;}
			else {
				return Result.CONTINUE;
			}
		} else {
			return Result.DONE;
		}
	}

	private Consumer nextConsumer(Consumer consumer) {
		Consumer retVal = null;
		if (consumer.equals(miTOW)){
			retVal = mvelN;
		} else if (consumer.equals(mvelN)) {
			retVal = mvelE;
		} else if (consumer.equals(mvelE)) {
			retVal = mvelD;
		} else if (consumer.equals(mvelD)) {
			retVal = mspeed;
		} else if (consumer.equals(mspeed)) {
			retVal = mgSpeed;
		} else if (consumer.equals(mgSpeed)) {
			retVal = mheading;
		} else if (consumer.equals(mheading)) {
			retVal = msAcc;
		} else if (consumer.equals(msAcc)) {
			retVal = mcAcc;
		}
		if (retVal != null) {
			retVal.enterState();
		}
		return retVal;
	}

	@Override
	public void enterState() {
		super.enterState();
		mCurConsumer = miTOW;
		mCurConsumer.enterState();
		mBytesLeft = mPacket.getLength();
//		System.out.println("Bytes left:" + mBytesLeft);
	}

}
