package com.relayr;

import java.util.ArrayList;
import java.util.HashMap;

import com.relayr.core.api.Relayr_ApiCall;
import com.relayr.core.api.Relayr_ApiConnector;
import com.relayr.core.device.Relayr_Device;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.event_listeners.LoginEventListener;
import com.relayr.core.settings.Relayr_SDKSettings;
import com.relayr.core.settings.Relayr_SDKStatus;
import com.relayr.core.storage.Relayr_DataStorage;

public class Relayr_SDK {

	static LoginEventListener loginEventListener;

	public static void init() throws Exception {
		if (!Relayr_SDKStatus.isActive()) {
			if (Relayr_SDKSettings.checkConfigValues()) {
				Relayr_DataStorage.loadLocalData();
				Relayr_SDKStatus.synchronizeUserInfo();
				Relayr_SDKStatus.setActive(true);
				setLoginEventListener(null);
			}
		}
	}

	public static void stop() {
		if (Relayr_SDKStatus.isActive()) {
			if (!Relayr_SDKStatus.isBackgroundModeActive()) {
				Relayr_DataStorage.saveLocalData();
				Relayr_SDKStatus.setActive(false);
			}
		}
	}

	public static void setBackgroundMode(boolean mode) {
		Relayr_SDKStatus.setBackgroundMode(mode);
	}

	public static String getVersion() throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			return Relayr_SDKSettings.getVersion();
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static boolean isActive() {
		return Relayr_SDKStatus.isActive();
	}

	public static ArrayList<Relayr_Device> getUserDevices() throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {};
			return (ArrayList<Relayr_Device>)Relayr_ApiConnector.doCall(Relayr_ApiCall.UserDevices, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static Relayr_Device getDeviceById(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (Relayr_Device)Relayr_ApiConnector.doCall(Relayr_ApiCall.DeviceInfo, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static Relayr_Device updateDeviceAttributesById(String deviceId, HashMap<String,Object> attributes) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId, attributes};
			return (Relayr_Device)Relayr_ApiConnector.doCall(Relayr_ApiCall.UpdateDeviceInfo, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static void setLoginEventListener(LoginEventListener listener) {
		loginEventListener = listener;
	}

	public static LoginEventListener getLoginEventListener() {
		return loginEventListener;
	}

	public static void login() {
		Relayr_SDKStatus.login();
	}

	public static boolean isUserLogged() {
		return Relayr_SDKStatus.isUserLogged();
	}

	public static boolean logout() {
		return Relayr_SDKStatus.logout();
	}
}
