package com.relayr;

import java.util.HashMap;

import com.relayr.core.api.Relayr_ApiCall;
import com.relayr.core.api.Relayr_ApiConnector;
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
				Relayr_SDKStatus.setActive(true);
				String token = Relayr_SDKSettings.getUserToken();
				if (token == null) {
					Relayr_User.login();
				} else {
					Object[] parameters = {};
					boolean userValidated = (Boolean) Relayr_ApiConnector.doCall(Relayr_ApiCall.UserConnectWithToken, parameters);
					if (!userValidated) {
						token = (String) Relayr_ApiConnector.doCall(Relayr_ApiCall.UserConnectWithoutToken, parameters);
						Relayr_SDKSettings.setToken(token);
					}
				}
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

	public boolean isActive() {
		return Relayr_SDKStatus.isActive();
	}

	public HashMap<String, Object> listAllDevices() throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {};
			return (HashMap<String, Object>)Relayr_ApiConnector.doCall(Relayr_ApiCall.ListAllDevices, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public HashMap<String, Object> listAllDevicesFilteredBy(String type) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {type};
			return (HashMap<String, Object>)Relayr_ApiConnector.doCall(Relayr_ApiCall.ListAllDevices, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public HashMap<String, Object> listClientDevices() throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {};
			return (HashMap<String, Object>)Relayr_ApiConnector.doCall(Relayr_ApiCall.ListClientDevices, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public boolean addDevice(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.AddDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public HashMap<String, Object> retrieveDevice(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (HashMap<String, Object>)Relayr_ApiConnector.doCall(Relayr_ApiCall.RetrieveDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public boolean modifyDevice(String deviceId, HashMap<String, Object> values) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId, values};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.ModifyDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public boolean removeDevice(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.RemoveDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public HashMap<String, Object> retrieveDeviceConfiguration(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (HashMap<String, Object>)Relayr_ApiConnector.doCall(Relayr_ApiCall.RetrieveDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public boolean configureDevice(String deviceId, HashMap<String, Object> configuration) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId, configuration};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.ConfigureDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}

	public boolean deleteDevice(String deviceId) throws Relayr_Exception {
		if (Relayr_SDKStatus.isActive()) {
			Object[] parameters = {deviceId};
			return (Boolean)Relayr_ApiConnector.doCall(Relayr_ApiCall.DeleteDevice, parameters);
		} else {
			throw new Relayr_Exception("SDK no active", null);
		}
	}
}
