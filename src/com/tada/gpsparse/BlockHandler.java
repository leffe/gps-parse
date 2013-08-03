package com.tada.gpsparse;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import com.tada.consumers.generic.Consumer;
import com.tada.consumers.generic.Unsigned;
import com.tada.consumers.message.NavPosllh;
import com.tada.consumers.message.NavSol;
import com.tada.consumers.message.NavVelNed;

public class BlockHandler {

	private final List<Block> mBlocks = new ArrayList<Block>();
	
	// GPS Millisecond Time of Week. Used as common time stamp for different 
	// payload packet types
//	private long CurrentMiTOW;

//	private BufferedWriter mOut;
	
	public BlockHandler(BufferedWriter out) {
		super();
//		mOut = out;
		
 	}

	public void addPayloadMessage(Consumer curConsumer) {

		Unsigned miTow = null;
//		String name;
		
		if (curConsumer instanceof NavSol) {
			NavSol pck = (NavSol) curConsumer;
			if (pck.miTOW.done()) {
				miTow = pck.miTOW;
			}
		} else if (curConsumer instanceof NavPosllh) {
			NavPosllh pck = (NavPosllh) curConsumer;
			if (pck.miTOW.done()) {
				miTow = pck.miTOW;
			}
		}if (curConsumer instanceof NavVelNed) {
			NavVelNed pck = (NavVelNed) curConsumer;
			if (pck.miTOW.done()) {
				miTow = pck.miTOW;
			}
		}

		if (miTow != null) {
			mBlocks.add(new Block(miTow.getValue()));
		}
	}

//	public String xx() {
//		// TODO Auto-generated method stub
//		return Integer.toString(mBlocks.size());
//	}

}
