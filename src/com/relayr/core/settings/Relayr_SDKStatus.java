package com.relayr.core.settings;

public class Relayr_SDKStatus {

	static boolean active = false;
	static boolean backgroundMode = false;

	public static boolean isActive() {
		return active;
	}

	public static void setActive(boolean status) {
		active = status;
	}

	public static boolean isBackgroundModeActive() {
		return backgroundMode;
	}

	public static void setBackgroundMode(boolean status) {
		backgroundMode = status;
	}

}
