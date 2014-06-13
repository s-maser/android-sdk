package com.relayr.core.ble.device;


public enum Relayr_BLEDevices {
	WunderbarHTU ("WunderbarHTU"),
	WunderbarGYRO ("WunderbarGYRO"),
	WunderbarLIGHT ("WunderbarLIGHT"),
	WunderbarMIC ("WunderbarMIC"),
	WunderbarBRIDG ("WunderbarBRIDG"),
	WunderbarIR ("WunderbarIR"),
	WunderbarApp ("WunderbarApp");

	private String deviceName;

	Relayr_BLEDevices(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceName() {
		return this.deviceName;
	}

	public static boolean isRelayrCompatibleDevice(String deviceName) {
		for(Relayr_BLEDevices device:Relayr_BLEDevices.values()) {
			if (device.getDeviceName().equals(deviceName)) {
				return true;
			}
		}
		return false;
	}
}
