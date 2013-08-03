package com.tada.consumers.generic;

import java.io.IOException;

import com.tada.gpsparse.Packet;

public class Unsigned extends Consumer{
	private long mValue = 0;
	final int mSize;
	int mCount;
	private String mName;
	
	public Unsigned(Packet packet, int size, String name) {
		super(packet,true);
		mSize = size;
		mName = name;
	}

	public Unsigned(Packet packet, int size, String name, boolean dontAddChecksum) {
		super(packet,true);
		mSize = size;
		mName = name;
	}

	@Override
	public Result consume(int i) throws IOException {
		super.consume(i);
		long add = i << 8*mCount++;
//        mValue += i << 8*mCount++;
        mValue += add;
		if (done()) {
//			System.out.println("[" + mName + "]" + ", Unsigned size: " + mSize + ", value: " + mValue);
			return Result.DONE;
		} else {
			return Result.CONTINUE;
		}
	}

	public boolean done() {
		return mCount==mSize;
	}

	@Override
	public void enterState() {
		super.enterState();
		mCount = 0;
		mValue = 0;
	}

	public long getValue() {
		if (done()) {
			return mValue;
		} else {
			return 0;
		}
	}

    public void setValue(long value) {
        mValue = value;
        mCount = mSize;
    }
}
