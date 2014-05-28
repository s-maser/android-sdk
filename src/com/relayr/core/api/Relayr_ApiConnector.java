package com.relayr.core.api;

import com.relayr.core.error.Relayr_Exception;

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
		/*case ListAllDevices: {
			return "ListAllDevices";
		}
		case ListClientDevices: {
			return "ListClientDevices";
		}
		case AddDevice: {
			return "AddDevice";
		}
		case RetrieveDevice: {
			return "RetrieveDevice";
		}
		case ModifyDevice: {
			return "ModifyDevice";
		}
		case RemoveDevice: {
			return "RemoveDevice";
		}
		case RetrieveDeviceConfiguration: {
			return "RetrieveDeviceConfiguration";
		}
		case ConfigureDevice: {
			return "ConfigureDevice";
		}
		case DeleteDevice: {
			return "DeleteDevice";
		}*/
		default: return "UNKNOWN";
		}
	}

	private static boolean checkCorrectParameters(Relayr_ApiCall call, Object... params) {
		switch (call) {
		/*case ListAllDevices: {
			return (params.length < 2);
		}
		case AddDevice:
		case RetrieveDevice:
		case RemoveDevice:
		case RetrieveDeviceConfiguration:
		case DeleteDevice: {
			return (params.length == 1);
		}
		case ModifyDevice:
		case ConfigureDevice: {
			return (params.length == 2);
		}*/
		case UserDevices: {
			return (params.length <= 1);
		}
		default: return (params.length == 0);
		}
	}

}
