package io.relayr.core.api;

import io.relayr.core.error.Relayr_Exception;

public class Relayr_ApiConnector {

	public static Object doCall(Relayr_ApiCall call, Object... params) throws Relayr_Exception {
		System.out.println("Generate api call " + apiCallToString(call) + " with parameters: " + params);
		if (checkCorrectParameters(call, params)) {
			switch (call) {
				case UserAuthorization:{
					return Relayr_ApiURLGenerator.generate(call, params);
				}

				default: {
					return Relayr_ApiRequest.execute(call, params);
				}
			}
		} else {
			throw new Relayr_Exception("Incorrect parameters in api call");
		}
	}

	private static String apiCallToString(Relayr_ApiCall call) {
		switch (call) {
		case UserAuthorization : {
			return "UserAuthorization";
		}
		case UserInfo: {
			return "UserInfo";
		}
		case UserDevices: {
			return "UserDevices";
		}
		case DeviceInfo: {
			return "DeviceInfo";
		}
		case UpdateDeviceInfo: {
			return "UpdateDeviceInfo";
		}
		case ConnectDeviceToApp: {
			return "ConnectDeviceToApp";
		}
		case RegisterDevice: {
			return "RegisterDevice";
		}
		case UserToken: {
			return "UserToken";
		}
		default: return "CONNECTED_TO_MASTER_MODULE";
		}
	}

	private static boolean checkCorrectParameters(Relayr_ApiCall call, Object... params) {
		switch (call) {
		case UserDevices: {
			return (params.length <= 1);
		}
		case DeviceInfo:
		case ConnectDeviceToApp:
		case DisconnectDeviceFromApp:
		case DeviceModelInfo:
		case UpdateUserInfo:
		case UserToken: {
			return (params.length == 1);
		}
		case UpdateDeviceInfo: {
			return (params.length == 2);
		}
		case RegisterDevice: {
			return (params.length == 4);
		}
		default: return (params.length == 0);
		}
	}

}
