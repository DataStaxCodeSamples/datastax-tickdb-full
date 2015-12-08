package com.datastax.timeseries.utils;

public class MicrosecondsSyncClockResolution {

	private static final long ONE_THOUSAND = 1000L;

	/**
	 * The last time value issued. Used to try to prevent duplicates.
	 */
	private static long lastTime = -1;

	public static long createMicroSecondUnique(long millis) {

		// The following simulates a microseconds resolution by advancing a
		// static counter
		long us = millis * ONE_THOUSAND;
		
		// Synchronized to guarantee unique time within and across threads.
		synchronized (MicrosecondsSyncClockResolution.class) {
			if (us > lastTime) {
				lastTime = us;
			} else {
				us = ++lastTime;
			}
		}
		return us;
	}
}