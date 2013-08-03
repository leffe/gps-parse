package com.tada.consumers.message;

import java.io.IOException;

import com.tada.consumers.generic.Consumer;
import com.tada.consumers.generic.Int;
import com.tada.consumers.generic.Unsigned;
import com.tada.gpsparse.BlockList;
import com.tada.gpsparse.Packet;
import static com.tada.gpsparse.Constants.*;

public class NavPosllh extends Consumer {

	private int mBytesLeft;
	private Consumer mCurConsumer;

	public Unsigned miTOW;
	private Int<Integer> mLon;
	private Int<Integer> mlat;
	private Int<Integer> mheight;
	private Int<Integer> mhMSL;
	private Unsigned mhAcc;
	private Unsigned mvAcc;

	public NavPosllh(Packet packet) {
		super(packet);
		miTOW = new Unsigned(packet, 4, UBX_ITOW);
		mLon = new Int<Integer>(new Integer(0), packet, UBX_LON);
		mlat = new Int<Integer>(new Integer(0), packet, UBX_LAT);
		mheight = new Int<Integer>(new Integer(0),packet, UBX_HEIGHT);
		mhMSL = new Int<Integer>(new Integer(0),packet, UBX_HMSL);
		mhAcc = new Unsigned(packet, 4, "hAcc");
		mvAcc = new Unsigned(packet, 4, "vAcc");
		
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
				BlockList.add(miTOW.getValue(), UBX_LAT, mlat.getValue());
				BlockList.add(miTOW.getValue(), UBX_LON, mLon.getValue());
				BlockList.add(miTOW.getValue(), UBX_HEIGHT, mheight.getValue());
				BlockList.add(miTOW.getValue(), UBX_HMSL, mhMSL.getValue());
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
			retVal = mLon;
		} else if (consumer.equals(mLon)) {
			retVal = mlat;
		} else if (consumer.equals(mlat)) {
			retVal = mheight;
		} else if (consumer.equals(mheight)) {
			retVal = mhMSL;
		} else if (consumer.equals(mhMSL)) {
			retVal = mhAcc;
		} else if (consumer.equals(mhAcc)) {
			retVal = mvAcc;
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
