package com.relayr;

import java.util.ArrayList;

import com.relayr.core.api.Relayr_ApiCall;
import com.relayr.core.api.Relayr_ApiConnector;
import com.relayr.core.device.Relayr_Device;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.event_listeners.LoginEventListener;
import com.relayr.core.settings.Relayr_SDKSettings;
import com.relayr.core.settings.Relayr_SDKStatus;
import com.relayr.core.storage.Relayr_DataStorage;
import com.relayr.core.user.Relayr_User;

public class Relayr_SDK {

	static LoginEventListener loginEventListener;

	public static void init() throws Exception {
		if (!Relayr_SDKStatus.isActive()) {
			if (Relayr_SDKSettings.checkConfigValues()) {
				Relayr_DataStorage.loadLocalData();
				Relayr_User.synchronizeUserInfo();
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

	/*public static JSONArray listAllDevices() throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {};
			return (JSONArray)Relayr_ApiConnector.doCall(Relayr_ApiCall.ListAllDevices, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static JSONArray listAllDevicesFilteredBy(String type) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {type};
			return (JSONArray)Relayr_ApiConnector.doCall(Relayr_ApiCall.ListAllDevices, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static JSONArray listClientDevices() throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {};
			return (JSONArray)Relayr_ApiConnector.doCall(Relayr_ApiCall.ListClientDevices, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static boolean addDevice(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.AddDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static JSONObject retrieveDevice(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (JSONObject)Relayr_ApiConnector.doCall(Relayr_ApiCall.RetrieveDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static boolean modifyDevice(String deviceId, HashMap<String, Object> values) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId, values};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.ModifyDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static boolean removeDevice(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.RemoveDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static JSONObject retrieveDeviceConfiguration(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (JSONObject)Relayr_ApiConnector.doCall(Relayr_ApiCall.RetrieveDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static boolean configureDevice(String deviceId, HashMap<String, Object> configuration) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId, configuration};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.ConfigureDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public static boolean deleteDevice(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.DeleteDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}
*/

	public static ArrayList<Relayr_Device> getUserDevices() throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {};
			return (ArrayList<Relayr_Device>)Relayr_ApiConnector.doCall(Relayr_ApiCall.UserDevices, parameters);
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

	public void login() {
		Relayr_User.login();
	}

	public boolean isUserLogged() {
		return Relayr_User.isUserLogged();
	}

	public boolean logout() {
		return Relayr_User.logout();
	}
}
