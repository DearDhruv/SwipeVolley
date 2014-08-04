
package com.android.volley.examples.toolbox.updated;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.SystemClock;
import android.util.Log;

/**
 * Logs
 * 
 * @author DearDhruv
 */
public class FLog {
	public static boolean	DEBUG	= false;
	public static String	TAG		= "Volley";

	public static void e(String tag, String msg) {
		if (DEBUG) {
			Log.e(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			Log.w(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.e(tag, msg, tr);
		}
	}

	public static void v(String format, Object... args) {
		if (DEBUG) {
			Log.v(TAG, buildMessage(format, args));
		}
	}

	public static void d(String format, Object... args) {
		Log.d(TAG, buildMessage(format, args));
	}

	public static void e(String format, Object... args) {
		Log.e(TAG, buildMessage(format, args));
	}

	public static void e(Throwable tr, String format, Object... args) {
		Log.e(TAG, buildMessage(format, args), tr);
	}

	public static void wtf(String format, Object... args) {
		Log.wtf(TAG, buildMessage(format, args));
	}

	public static void wtf(Throwable tr, String format, Object... args) {
		Log.wtf(TAG, buildMessage(format, args), tr);
	}

	/**
	 * Formats the caller's provided message and prepends useful info like
	 * calling thread ID and method name.
	 */
	private static String buildMessage(String format, Object... args) {
		String msg = (args == null) ? format : String.format(Locale.US, format, args);
		StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

		String caller = "<unknown>";
		// Walk up the stack looking for the first caller outside of FLog.
		// It will be at least two frames up, so start there.
		for (int i = 2; i < trace.length; i++) {
			Class<?> clazz = trace[i].getClass();
			if (!clazz.equals(FLog.class)) {
				String callingClass = trace[i].getClassName();
				callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
				callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

				caller = callingClass + "." + trace[i].getMethodName();
				break;
			}
		}
		return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), caller, msg);
	}

	/**
	 * A simple event log with records containing a name, thread ID, and
	 * timestamp.
	 */
	public static class MarkerLog {
		public static final boolean	ENABLED						= DEBUG;

		/**
		 * Minimum duration from first marker to last in an marker log to
		 * warrant logging.
		 */
		private static final long	MIN_DURATION_FOR_LOGGING_MS	= 0;

		private static class Marker {
			public final String	name;
			public final long	thread;
			public final long	time;

			public Marker(String name, long thread, long time) {
				this.name = name;
				this.thread = thread;
				this.time = time;
			}
		}

		private final List<Marker>	mMarkers	= new ArrayList<Marker>();
		private boolean				mFinished	= false;

		/** Adds a marker to this log with the specified name. */
		public synchronized void add(String name, long threadId) {
			if (mFinished) {
				throw new IllegalStateException("Marker added to finished log");
			}

			mMarkers.add(new Marker(name, threadId, SystemClock.elapsedRealtime()));
		}

		/**
		 * Closes the log, dumping it to logcat if the time difference between
		 * the first and last markers is greater than
		 * {@link #MIN_DURATION_FOR_LOGGING_MS}.
		 * 
		 * @param header
		 *            Header string to print above the marker log.
		 */
		public synchronized void finish(String header) {
			mFinished = true;

			long duration = getTotalDuration();
			if (duration <= MIN_DURATION_FOR_LOGGING_MS) {
				return;
			}

			long prevTime = mMarkers.get(0).time;
			d("(%-4d ms) %s", duration, header);
			for (Marker marker : mMarkers) {
				long thisTime = marker.time;
				d("(+%-4d) [%2d] %s", (thisTime - prevTime), marker.thread, marker.name);
				prevTime = thisTime;
			}
		}

		@Override
		protected void finalize() throws Throwable {
			// Catch requests that have been collected (and hence end-of-lifed)
			// but had no debugging output printed for them.
			if (!mFinished) {
				finish("Request on the loose");
				e("Marker log finalized without finish() - uncaught exit point for request");
			}
		}

		/**
		 * Returns the time difference between the first and last events in this
		 * log.
		 */
		private long getTotalDuration() {
			if (mMarkers.size() == 0) {
				return 0;
			}

			long first = mMarkers.get(0).time;
			long last = mMarkers.get(mMarkers.size() - 1).time;
			return last - first;
		}
	}

}
// >>>>
