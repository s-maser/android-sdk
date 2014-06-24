package com.relayr;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import com.relayr.core.api.Relayr_ApiCall;
import com.relayr.core.api.Relayr_ApiConnector;
import com.relayr.core.ble.Relayr_BleListener;
import com.relayr.core.device.Relayr_Device;
import com.relayr.core.device.Relayr_DeviceModelDefinition;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.settings.Relayr_SDKSettings;
import com.relayr.core.settings.Relayr_SDKStatus;
import com.relayr.core.storage.Relayr_DataStorage;
import com.relayr.core.user.Relayr_User;

public class Relayr_SDK {

	public static void init() throws Exception {
		if (!Relayr_SDKStatus.isActive()) {
			if (Relayr_SDKSettings.checkConfigValues()) {
				Relayr_DataStorage.loadLocalData();
				Relayr_SDKStatus.synchronizeUserInfo();
				Relayr_SDKStatus.synchronizeAppInfo();
				Relayr_SDKStatus.setActive(true);
				Relayr_BleListener.init();
			}
		}
	}

	public static void stop() {
		if (Relayr_SDKStatus.isActive()) {
			Log.d("Relayr_SDK", "Stopping service");
			if (!Relayr_SDKStatus.isBackgroundModeActive()) {
				Log.d("Relayr_SDK", "No background");
				Relayr_DataStorage.saveLocalData();
				Relayr_SDKStatus.setActive(false);
				Relayr_BleListener.stop();
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

	public static Relayr_Device registerDevice(String title, String modelId, String firmwareVersion, String description) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {title, modelId, firmwareVersion, description};
			return (Relayr_Device)Relayr_ApiConnector.doCall(Relayr_ApiCall.RegisterDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
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

	public static Relayr_User getUserInfo() throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {};
			return (Relayr_User)Relayr_ApiConnector.doCall(Relayr_ApiCall.UserInfo, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static Relayr_User updateUserInfo(HashMap<String,Object> info) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {info};
			return (Relayr_User)Relayr_ApiConnector.doCall(Relayr_ApiCall.UpdateUserInfo, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static Boolean connectDeviceToApp(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.ConnectDeviceToApp, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static Boolean disconnectDeviceFromApp(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.DisconnectDeviceFromApp, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static ArrayList<Relayr_DeviceModelDefinition> getAllDeviceModels() throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {};
			return (ArrayList<Relayr_DeviceModelDefinition>)Relayr_ApiConnector.doCall(Relayr_ApiCall.DeviceModels, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static Relayr_DeviceModelDefinition getDeviceModelById(String deviceModelId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceModelId};
			return (Relayr_DeviceModelDefinition)Relayr_ApiConnector.doCall(Relayr_ApiCall.DeviceModelInfo, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
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

	public static boolean startBLEScanning() {
		if (Relayr_Commons.isSDK18()) {
			Relayr_BleListener.start();
			return Relayr_BleListener.isScanning();
		} else {
			return false;
		}
	}

	public static boolean stopBLEScanning() {
		if (Relayr_Commons.isSDK18()) {
			Relayr_BleListener.stop();
			return !Relayr_BleListener.isScanning();
		} else {
			return false;
		}
	}

	public static boolean isScanningForBLE() {
		if (Relayr_Commons.isSDK18()) {
			return Relayr_BleListener.isScanning();
		} else {
			return false;
		}
	}

}
