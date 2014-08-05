package io.relayr;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import io.relayr.core.api.Relayr_ApiCall;
import io.relayr.core.api.Relayr_ApiConnector;
import io.relayr.core.ble.BleUtils;
import io.relayr.core.ble.RelayrBleSdk;
import io.relayr.core.device.Relayr_Device;
import io.relayr.core.device.Relayr_DeviceModelDefinition;
import io.relayr.core.error.Relayr_Exception;
import io.relayr.core.event_listeners.LoginEventListener;
import io.relayr.core.settings.Relayr_SDKSettings;
import io.relayr.core.settings.Relayr_SDKStatus;
import io.relayr.core.storage.Relayr_DataStorage;
import io.relayr.core.user.Relayr_User;

public class Relayr_SDK {

	static LoginEventListener loginEventListener;

	public static void init() throws Exception {
		if (!Relayr_SDKStatus.isActive()) {
			if (Relayr_SDKSettings.checkConfigValues()) {
				Relayr_DataStorage.loadLocalData();
				Relayr_SDKStatus.synchronizeUserInfo();
				Relayr_SDKStatus.synchronizeAppInfo();
				Relayr_SDKStatus.setActive(true);
				setLoginEventListener(null);
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
			return (Relayr_Device) Relayr_ApiConnector.doCall(Relayr_ApiCall.RegisterDevice, parameters);
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

    /** {@link io.relayr.core.ble.RelayrBleSdk#newInstance()} */
     public static RelayrBleSdk getRelayrBleSdk() {
        return RelayrBleSdk.newInstance();
    }

    /** Checks whether ble is supported or not. It should be called before using
     * {@link #getRelayrBleSdk} */
    public static boolean isBleSupported() {
        return Relayr_Commons.isSDK18() && BleUtils.isBleSupported();
    }

    /** Checks whether ble is on or off. Bluetooth can be activated by calling
     * {@link #promptUserToActivateBluetooth}. It should be called before using
     * {@link #getRelayrBleSdk} */
    public static boolean isBleAvailable() {
        return BleUtils.isBleAvailable();
    }

    /** Launches an activity to ask the user to activate the bluetooth. It won't do anything if
     * bluetooth is not supported {@link #isBleSupported} */
    public static void promptUserToActivateBluetooth() {
        if (isBleSupported()) BleUtils.promptUserToActivateBluetooth();
    }

	public static void setLoginEventListener(LoginEventListener listener) {
		loginEventListener = listener;
	}

	public static LoginEventListener getLoginEventListener() {
		return loginEventListener;
	}
}
