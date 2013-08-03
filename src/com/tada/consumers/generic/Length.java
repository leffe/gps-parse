package com.tada.consumers.generic;

import java.io.IOException;

import com.tada.gpsparse.Packet;

public class Length extends Consumer{

	private int mLength;
	private boolean mFirstByte;



	public Length(Packet packet) {
		super(packet);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void enterState() {
		super.enterState();
		mLength = 0;
		mFirstByte = true;
	}



	@Override
	public Result consume(int i) throws IOException {
		super.consume(i);
		if (mFirstByte) {
//			System.out.println("First length byte: " + i);
			mLength = i;
			mFirstByte = false;
			return Result.CONTINUE;
		} else {
//			System.out.println("Second length byte: " + i);
			mLength += i*256;
			mPacket.setLength(mLength);
			return Result.CREATE;
		}
	}

}
