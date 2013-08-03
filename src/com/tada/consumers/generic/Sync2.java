package com.tada.consumers.generic;

import java.io.IOException;

import com.tada.gpsparse.Packet;
//TODO Merge the two classes for sync into one
public class Sync2 extends Consumer{

	public Sync2(Packet packet) {
		super(packet, true); // Don't add checksum
	}

	@Override
	public Result consume(int i) throws IOException {
		super.consume(i);
		if (98==i) {
//			System.out.println("Sync2");
			return Result.DONE;
		}
		return Result.INVALID;
	}

	@Override
	public void enterState() {
		// TODO Auto-generated method stub
		super.enterState();
		mPacket.clearChecksum();
	}
	
	
}
