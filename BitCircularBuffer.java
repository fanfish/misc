package com.jrj.util;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

/**
 * Sort One Million 8-Digit Numbers in 1MB of RAM 类霍夫曼编码树实现
 * <link>https://preshing.com/20121026/1mb-sorting-explained/</link>
 */
public class BitCircularBuffer {

	private BitSet buffer;

	private int readPointer;

	private int writePointer;

	private int first;

	public BitCircularBuffer(int bufferSize) {
		this.buffer = new BitSet(bufferSize);
	}

	public synchronized int read() throws Exception {
		if (isEmpty())
			throw new Exception("Buffer is empty!");
		int delta = decode();
		return delta;
	}

	private void encode(int delta) {
		int ones = delta >> 6;
		int residue = delta % 64;
		for (int i = 0; i < ones; i++) {
			this.buffer.set(this.writePointer);
			this.writePointer = (this.writePointer + 1) % this.buffer.size();
		}
		this.writePointer = (this.writePointer + 1) % this.buffer.size();
		int count = 32;
		while (count != 0) {
			if ((residue & count) != 0) {
				this.buffer.set(this.writePointer);
			}
			this.writePointer = (this.writePointer + 1) % this.buffer.size();
			count = count >>> 1;
		}
	}

	private int decode() {
		int multi = 0;
		while (this.buffer.get(this.readPointer)) {
			multi += 1;
			this.readPointer = (this.readPointer + 1) % this.buffer.size();
		}
		this.readPointer = (this.readPointer + 1) % this.buffer.size();
		int offset = 0;
		for (int i = 0; i < 6; i++) {
			offset += this.buffer.get(this.readPointer) ? (1 << (5 - i)) : 0;
			this.readPointer = (this.readPointer + 1) % this.buffer.size();
		}
		int delta = 64 * multi + offset;
		return delta;
	}

	public synchronized void write(int delta) throws Exception {
		if (isFull())
			throw new Exception("Buffer is full!");
		encode(delta);
	}

	public synchronized boolean isEmpty() {
		if (this.readPointer == this.writePointer)
			return true;
		return false;
	}

	public synchronized boolean isFull() {
		if (this.readPointer == (this.writePointer + 1) % buffer.size())
			return true;
		return false;
	}

	public synchronized void merge(int[] stagingArea) throws Exception {
		if (isEmpty()) {
			first = stagingArea[0];
			write(0);
			for (int i = 1; i < stagingArea.length; i++) {
				int delta = stagingArea[i] - stagingArea[i - 1];
				write(delta);
			}
		} else {
			// mergeSort
			int b = read();
			int buf_cur = first;
			if (stagingArea[0] < first)
				first = stagingArea[0];
			int cur = first;
			int m = 0;
			int oldWriterPointer = this.writePointer;
			while (m < stagingArea.length && (this.readPointer < oldWriterPointer)) {
				int a = stagingArea[m];
				if (a < buf_cur + b) {
					write(a - cur);
					cur = a;
					m++;
				} else {
					write(buf_cur + b - cur);
					cur = buf_cur + b;
					buf_cur += b;
					b = read();
				}
			}
			if (m == stagingArea.length && this.readPointer < oldWriterPointer) {
				write(buf_cur + b - cur);
				while (this.readPointer < oldWriterPointer) {
					int value = read();
					write(value);
				}
			}

			if (m < stagingArea.length && this.readPointer == oldWriterPointer) {
				int val = buf_cur + b;
				while (m < stagingArea.length && val > stagingArea[m]) {
					write(stagingArea[m] - cur);
					cur = stagingArea[m];
					m++;
				}
				write(val - cur);
				cur = val;
				for (int i = m; i < stagingArea.length; i++) {
					write(stagingArea[i] - cur);
					cur = stagingArea[i];
				}
			}
		}
	}

	private void println() throws Exception {
		int curVal = first;
		while (!isEmpty()) {
			curVal += read();
			System.out.println(curVal);
		}
	}

	private void sort() throws Exception {
		int[] stagingArea = new int[8000];
		Random rand = new Random();
		int j = 0;
		for (int i = 0; i < 1000000; i++) {
			stagingArea[j] = rand.nextInt(999999) + 1;
			if (j == stagingArea.length - 1) {
				Arrays.sort(stagingArea);
				merge(stagingArea);
				j = 0;
			} else {
				j++;
			}
		}
		int[] remain = new int[j];
		System.arraycopy(stagingArea, 0, remain, 0, j);
		Arrays.sort(remain);
		merge(remain);
		println();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] stagingArea_1 = { 1, 3, 6, 12, 25, 109, 305, 666, 888, 900, 991, 1001, 3005, 8000, 20000 };
		int[] stagingArea_2 = { 2, 34, 67, 110, 500, 876, 888, 988, 1000, 2600, 11000 };
		int[] stagingArea_3 = { 5, 45, 10000, 70001 };
		BitCircularBuffer instance = new BitCircularBuffer(8562500);
		try {
			instance.merge(stagingArea_1);
			instance.merge(stagingArea_2);
			instance.merge(stagingArea_3);
			instance.println();
			// instance.sort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
