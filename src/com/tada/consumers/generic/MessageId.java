package com.tada.consumers.generic;

import java.io.IOException;

import com.tada.gpsparse.Packet;
//TODO Merge the two classes for message into one

public class MessageId extends Consumer{

	public MessageId(Packet packet) {
		super(packet);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Result consume(int i) throws IOException {
		super.consume(i);
//		System.out.println("Message id: " + i);
		mPacket.setMessageId(i);
		return Result.DONE;
	}

}
