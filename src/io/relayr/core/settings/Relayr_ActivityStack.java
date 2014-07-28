package io.relayr.core.settings;

public class Relayr_ActivityStack {

	private static int CALLSTACKCOUNTER;

	static {

	}

	public static void addStackCall() {
		setCallStackCounter(getCallStackCounter() + 1);
	}

	public static void eraseStackCall() {
		if (CALLSTACKCOUNTER > 0) {
			setCallStackCounter(getCallStackCounter() - 1);
		}
	}

	public static void cleanStackCall() {
		setCallStackCounter(0);
	}

	public static int getCallStackCounter() {
		return CALLSTACKCOUNTER;
	}

	public static void setCallStackCounter(int counter) {
		CALLSTACKCOUNTER = counter;
	}

	public static boolean isCallStackEmpty() {
		if (CALLSTACKCOUNTER == 0) {
			return true;
		}
		return false;
	}
}
