package com.tada.gpsparse;

import java.io.IOException;

import com.tada.consumers.generic.Checksum;
import com.tada.consumers.generic.Consumer;
import com.tada.consumers.generic.Length;
import com.tada.consumers.generic.MessageClass;
import com.tada.consumers.generic.MessageId;
import com.tada.consumers.generic.Sync1;
import com.tada.consumers.generic.Sync2;
import com.tada.consumers.generic.Consumer.Result;
import com.tada.consumers.message.CfgMsg;
import com.tada.consumers.message.CfgNav5;
import com.tada.consumers.message.CfgRate;
import com.tada.consumers.message.NavPosllh;
import com.tada.consumers.message.NavSol;
import com.tada.consumers.message.NavVelNed;

public class Handler {
	Consumer mCurConsumer;
	private Sync1 mSync1;
	private Sync2 mSync2;
	private Result mResult;
	private MessageClass mClass;
	private MessageId mMessageId;
	private Length mLength;
	private Packet mPacket;
	private NavPosllh mNavPosllh;
	private Checksum mChecksum;
	private NavSol mNavSol;
	private NavVelNed mNavVelNed;
	private CfgMsg mCfgMsg;
    private CfgRate mCfgRate;
    private CfgNav5 mCfgNav5;
//	private BlockHandler mBlockHandler;

//	Handler(Packet packet, BlockHandler blockHandler) {
    Handler(Packet packet) {
	//	mBlockHandler = blockHandler;
		mPacket = packet;
		// Create components of the UBX packet structure
		mSync1 = new Sync1(packet);
		mCurConsumer = mSync1;
		mSync2 = new Sync2(packet);
		mClass = new MessageClass(packet);
		mMessageId = new MessageId(packet);
		mLength = new Length(packet);
		mChecksum = new Checksum(packet);
		// Create relevant payload packets
		mNavPosllh = new NavPosllh(packet);
		mNavSol = new NavSol(packet);
		mNavVelNed = new NavVelNed(packet);
		mCfgMsg = new CfgMsg(packet);
        mCfgRate = new CfgRate(packet);
        mCfgNav5 = new CfgNav5(packet, mChecksum);
	}

	public void consume(int i) throws IOException {
		mResult = mCurConsumer.consume(i);
		switch (mResult) {
		case INVALID: {
			mCurConsumer = mSync1;
			break;
		}
		case DONE: {
			mCurConsumer = nextConsumer(mCurConsumer);
			break;
		}
		case CONTINUE: {
			break;
		}
		case CREATE: {// Create a new payload message.
			// First add current payload message to the larger message block
//			mBlockHandler.addPayloadMessage(mCurConsumer);
			// Create the new payload message
			mCurConsumer = createMessage(mPacket);
			if (mCurConsumer == null) {
				mCurConsumer = mSync1;
			} else {
//			    System.out.println("Calculated chkSum:" + 
//			            BlockList.fmtHex(mPacket.getmCkA()) + ", " + 
//			            BlockList.fmtHex(mPacket.getmCkB()));
			}
			break;
		}
		}
	}

	private Consumer createMessage(Packet packet) {
		Consumer consumer = null;
		if ((packet.getMessageClass() == 1) && (packet.getMessageId() == 2)
				&& (packet.getLength() == 28)) {
//			System.out.println("Message: NavPosllh");
			consumer = mNavPosllh;

		} else if ((packet.getMessageClass() == 1)
				&& (packet.getMessageId() == 6) && (packet.getLength() == 52)) {
//			System.out.println("Message: NavSol");
			consumer = mNavSol;
        } else if ((packet.getMessageClass() == 1)
                && (packet.getMessageId() == 18) && (packet.getLength() == 36)) {
//          System.out.println("Message: NavVelNed");
            consumer = mNavVelNed;
        } else if ((packet.getMessageClass() == 6)
                && (packet.getMessageId() == 1) 
                && (packet.getLength() == 3)) {
            System.out.println("Message: CfgMsg len " + packet.getLength());
            consumer = mCfgMsg;
        } else if ((packet.getMessageClass() == 6)
                && (packet.getMessageId() == 8) 
                && (packet.getLength() == 6)) {
            System.out.println("Message: CfgRate len " + packet.getLength());
            consumer = mCfgRate;
        } else if ((packet.getMessageClass() == 6)
                && (packet.getMessageId() == 0x24) 
                && (packet.getLength() == 0)) {
            System.out.println("Message: CfgNav5 len " + packet.getLength());
            consumer = mCfgNav5;
        } else {
            System.out.println("Unknown message:" + packet.getMessageClass() + ", " +
            packet.getMessageId() + ", " +
            packet.getLength()
            );
        }
		if (consumer != null) {
			consumer.enterState();
			return consumer;
		}
		return null;
	}

	private Consumer nextConsumer(Consumer curConsumer) {
		Consumer retVal = mSync1;
		if (curConsumer instanceof Sync1) {
			retVal = mSync2;
		} else if (curConsumer instanceof Sync2) {
			retVal = mClass;
		} else if (curConsumer instanceof MessageClass) {
			retVal = mMessageId;
		} else if (curConsumer instanceof MessageId) {
			retVal = mLength;
		} else if (curConsumer instanceof NavPosllh
                || curConsumer instanceof NavVelNed
                || curConsumer instanceof CfgMsg
                || curConsumer instanceof CfgRate
				|| curConsumer instanceof NavSol) {
			retVal = mChecksum;
		} else if (curConsumer instanceof Checksum) {
			retVal = mSync1;
		}
		retVal.enterState();
		return retVal;
	}
}
