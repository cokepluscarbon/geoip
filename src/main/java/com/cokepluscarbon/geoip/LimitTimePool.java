package com.cokepluscarbon.geoip;

import java.util.Date;
import java.util.concurrent.PriorityBlockingQueue;

public class LimitTimePool {
	private int threadSize = 10;
	private long rangeTime = 1000;
	private PriorityBlockingQueue<Long> timeQueue;

	public LimitTimePool(int threadSize, long rangeTime) {
		this.rangeTime = rangeTime;
		this.threadSize = threadSize;
		timeQueue = new PriorityBlockingQueue<Long>(threadSize);
	}

	public synchronized boolean get() {
		//System.out.println(timeQueue.size());
		if (timeQueue.size() >= threadSize) {
			//System.out.println("#false");
			return false;
		}
		if (timeQueue.peek() != null && (new Date().getTime() - timeQueue.peek()) < this.rangeTime) {
			//System.out.println("%false");
			//System.err.println(new Date().getTime() - timeQueue.peek());
			return false;
		}
		//System.out.println("获取线程：" + new Date());
		timeQueue.put(new Date().getTime());
		return true;
	}

	public synchronized void release() {
		timeQueue.poll();
	}
}
