package com.tada.gpsparse;

import java.io.BufferedWriter;

public class Packet {

	private Handler mState;
	private int mMessageId;
	private int mMessageClass;
	private int mLength;
	private int mCkA;
	private int mCkB;
	private BufferedWriter mOut;

	public Packet(BufferedWriter outstream) {
		mOut = outstream;
	}

	public boolean isValidMessageClass(int i) {

		switch (i) {
		case 1:
		case 2:
		case 4:
		case 5:
		case 6:
		case 10:
		case 11:
		case 13: {
			return true;
		}
		}
		return false;
	}

	public void setMessageId(int i) {
		mMessageId = i;
	}

	public void setMessageClass(int i) {
		mMessageClass = i;
	}

	public void setLength(int length) {
		mLength = length;
	}

	public int getMessageId() {
		return mMessageId;
	}

	public int getMessageClass() {
		return mMessageClass;
	}

	public int getLength() {
		return mLength;
	}

	public int getmCkA() {
		return mCkA;
	}

	public int getmCkB() {
		return mCkB;
	}

	public void addToChecksum(int i) {
		mCkA += i;
		mCkA = mCkA % 256;
		mCkB += mCkA;
		mCkB = mCkB % 256;
	}

	public void clearChecksum() {
//        System.out.println("Calculated chkSum:" + 
//                BlockList.fmtHex(getmCkA()) + ", " + 
//                BlockList.fmtHex(getmCkB()));

		mCkA = 0;
		mCkB = 0;
	}

	public BufferedWriter getOutstream(){
		return mOut;
	}
	
}
