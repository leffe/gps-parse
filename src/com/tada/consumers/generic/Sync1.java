package com.tada.consumers.generic;

import java.io.IOException;

import com.tada.gpsparse.Packet;

public class Sync1 extends Consumer{
// TODO Merge the two classes for sync into one
	public Sync1(Packet packet) {
		super(packet, true); // Don't add checksum
	}

	@Override
	public Result consume(int i) throws IOException {
		super.consume(i);
		if (181==i) {
//			System.out.println("Sync1");
			return Result.DONE;
		}
		return Result.INVALID;
	}

	@Override
	public void enterState() {
		// TODO Auto-generated method stub
		super.enterState();
	}
	
}
