package com.tada.consumers.message;

import java.io.IOException;

import com.tada.consumers.generic.Consumer;
import com.tada.consumers.generic.Int;
import com.tada.consumers.generic.Unsigned;
import com.tada.gpsparse.BlockList;
import com.tada.gpsparse.Packet;
import static com.tada.gpsparse.Constants.*;

public class NavSol extends Consumer{
	private int mBytesLeft;
	private Consumer mCurConsumer;

	public Unsigned miTOW;
	private Int<Integer> mfTOW;
	private Int<Short> mweek;
	private Unsigned mgpsFix;
	private Unsigned mflags;
	private Int<Integer> mecefX;
	private Int<Integer> mecefY;
	private Int<Integer> mecefZ;
	private Unsigned mpAcc;
	private Int<Integer> mecefVX;
	private Int<Integer> mecefVY;
	private Int<Integer> mecefVZ;
	private Unsigned msAcc;
	private Unsigned mpDOP;
	private Unsigned mres1;
	private Unsigned mnumSV;
	private Unsigned mres2;

	public NavSol(Packet packet) {
		super(packet);
		miTOW = new Unsigned(packet, 4, UBX_ITOW);
		mfTOW = new Int<Integer>(new Integer(0),packet, "fTOW");
		mweek = new Int<Short>(new Short((short) 0),packet, "Week");
		mgpsFix = new Unsigned(packet, 1, "gpsFix");
		mflags = new Unsigned(packet, 1, UBX_FLAGS);
		mecefX = new Int<Integer> (new Integer(0),packet, "ecefX");
		mecefY = new Int<Integer> (new Integer(0),packet, "ecefY");
		mecefZ = new Int<Integer> (new Integer(0),packet, "ecefZ");
		mpAcc = new Unsigned(packet, 4, UBX_PACC);
		mecefVX = new Int<Integer> (new Integer(0),packet, "ecefVX");
		mecefVY = new Int<Integer> (new Integer(0),packet, "ecevVY");
		mecefVZ = new Int<Integer> (new Integer(0),packet, "ecefVZ");
		msAcc = new Unsigned(packet, 4, UBX_SACC);
		mpDOP = new Unsigned(packet, 2, "pDOP");
		mres1 = new Unsigned(packet, 1, "res1");
		mnumSV = new Unsigned(packet, 1, "numSV");
		mres2 = new Unsigned(packet, 4, "res2");
		mCurConsumer = miTOW;
	}

	@Override
	public Result consume(int i) throws IOException {
		super.consume(i);
//		System.out.println("NavSol: " + i + 
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
				BlockList.add(miTOW.getValue(), UBX_PACC, mpAcc.getValue());
				BlockList.add(miTOW.getValue(), UBX_SACC, msAcc.getValue());
//				BlockList.add(miTOW.getValue(), UBX_FLAGS, mflags.getValue());
				BlockList.add(miTOW.getValue(), UBX_GPSFIX, mgpsFix.getValue());
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
			retVal = mfTOW;
		} else if (consumer.equals(mfTOW)) {
			retVal = mweek;
		} else if (consumer.equals(mweek)) {
			retVal = mgpsFix;
		} else if (consumer.equals(mgpsFix)) {
			retVal = mflags;
		} else if (consumer.equals(mflags)) {
			retVal = mecefX;
		} else if (consumer.equals(mecefX)) {
			retVal = mecefY;
		} else if (consumer.equals(mecefY)) {
			retVal = mecefZ;
		} else if (consumer.equals(mecefZ)) {
			retVal = mpAcc;
		} else if (consumer.equals(mpAcc)) {
			retVal = mecefVX;
		} else if (consumer.equals(mecefVX)) {
			retVal = mecefVY;
		} else if (consumer.equals(mecefVY)) {
			retVal = mecefVZ;
		} else if (consumer.equals(mecefVZ)) {
			retVal = msAcc;
		} else if (consumer.equals(msAcc)) {
			retVal = mpDOP;
		} else if (consumer.equals(mpDOP)) {
			retVal = mres1;
		} else if (consumer.equals(mres1)) {
			retVal = mnumSV;
		} else if (consumer.equals(mnumSV)) {
			retVal = mres2;
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
