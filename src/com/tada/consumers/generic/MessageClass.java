package com.tada.consumers.generic;

import java.io.IOException;

import com.tada.gpsparse.Packet;
//TODO Merge the two classes for message into one
public class MessageClass extends Consumer{

	public MessageClass(Packet packet) {
		super(packet);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Result consume(int i) throws IOException {
		super.consume(i);
		
		if (mPacket.isValidMessageClass(i)){
//			System.out.println("Message Class: " + i);
			mPacket.setMessageClass(i);
			return Result.DONE;
		}
		System.out.println("Invalid Message Class.");
		return Result.INVALID;
	}

}
