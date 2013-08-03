package com.tada.consumers.generic;

import java.io.IOException;

import com.tada.gpsparse.Packet;

public class Int<T> extends Consumer {
	T mValue;
	final int mSize;
	int mCount;
	String mName;

	public Int(T initValue,Packet packet, String name) {
		super(packet, true);

		mValue = initValue;
		
//		if (!((mValue instanceof Byte) || (mValue instanceof Short) || (mValue instanceof Integer))) {
//			throw new RuntimeException("Unsuported Integer value");
//		}

		
		if (mValue instanceof Byte) {
			mSize = 1;
		} else if (mValue instanceof Short) {
			mSize = 2;
		} else if (mValue instanceof Integer) {
			mSize = 4;
		} else {
			throw new RuntimeException("Unsuported Integer value");
		}

		
		
//		mSize = size;
		mName = name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result consume(int i) throws IOException {
		super.consume(i);
		if (mValue instanceof Byte) {
			byte val = (byte) (i << 8 * mCount++);
			byte temp = (Byte) mValue;
			mValue = (T) new Byte((byte) (temp + val));
		} else if (mValue instanceof Short) {
			short val = (short) (i << 8 * mCount++);
			short temp = (Short) mValue;
			mValue = (T) new Short((short) (temp + val));
		} else if (mValue instanceof Integer) {
			int val = (int) (i << 8 * mCount++);
			int temp = (Integer) mValue;
			mValue = (T) new Integer((int) (temp + val));
		}
		// mValue += i << 8*mCount++;
		if (done()) {
//			System.out.println("[" + mName + "]" + ", Int size: " + mSize
//					+ ", value: " + mValue);
			return Result.DONE;
		} else {
			return Result.CONTINUE;
		}
	}

	private boolean done() {
		return mCount == mSize;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void enterState() {
		super.enterState();
		mCount = 0;
		if (mValue instanceof Byte) {
			mValue = (T) new Byte((byte) 0);
		} else if (mValue instanceof Short) {
			mValue = (T) new Short((short) 0);
		} else if (mValue instanceof Integer) {
			mValue = (T) new Integer((int) 0);
		}
	}

	
	public Object getValue() {
		if (done()) {
			return mValue;
		} else {
			return null;
		}
	}

	
	
}
