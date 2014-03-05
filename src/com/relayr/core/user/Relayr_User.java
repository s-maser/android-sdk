package com.relayr.core.user;

public class Relayr_User {

	private static String userId;

	public static void setUserID(String id) {
		userId = id;
	}

	public static String getUserId() {
		return userId;
	}

}
