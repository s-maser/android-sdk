package com.relayr.core.ble.device;


public enum Relayr_BLEDeviceType {
	WunderbarHTU("00002000-0000-1000-8000-00805f9b34fb", "00002008-0000-1000-8000-00805f9b34fb", "00002007-0000-1000-8000-00805f9b34fb"),
	WunderbarGYRO("00002000-0000-1000-8000-00805f9b34fb", "00002008-0000-1000-8000-00805f9b34fb", "00002007-0000-1000-8000-00805f9b34fb"),
	WunderbarLIGHT("00002000-0000-1000-8000-00805f9b34fb", "00002008-0000-1000-8000-00805f9b34fb", "00002007-0000-1000-8000-00805f9b34fb"),
	WunderbarMIC("00002000-0000-1000-8000-00805f9b34fb", "00002008-0000-1000-8000-00805f9b34fb", "00002007-0000-1000-8000-00805f9b34fb"),
	WunderbarBRIDG("00002000-0000-1000-8000-00805f9b34fb", "00002008-0000-1000-8000-00805f9b34fb", "00002007-0000-1000-8000-00805f9b34fb"),
	WunderbarIR("00002000-0000-1000-8000-00805f9b34fb", "00002008-0000-1000-8000-00805f9b34fb", "00002007-0000-1000-8000-00805f9b34fb"),
	WunderbarApp("00002000-0000-1000-8000-00805f9b34fb", "00002008-0000-1000-8000-00805f9b34fb", "00002007-0000-1000-8000-00805f9b34fb"),
	EverykeyColor("095a2000-9315-878e-2f42-d16f962e6d78", "095a2001-9315-878e-2f42-d16f962e6d78", "095a2002-9315-878e-2f42-d16f962e6d78"),
	Unknown("","","");

	public String serviceUUID;
	public String dataReadCharacteristicUUID;
	public String configurationCharacteristicUUID;

	private Relayr_BLEDeviceType(String serviceUUID, String dataReadCharacteristicUUID, String configurationCharacteristic) {
		this.serviceUUID = serviceUUID;
		this.dataReadCharacteristicUUID = dataReadCharacteristicUUID;
		this.configurationCharacteristicUUID = configurationCharacteristic;
	}

	public static Relayr_BLEDeviceType getDeviceType(String deviceName) {
		if (deviceName.equals("WunderbarHTU")) {
			return WunderbarHTU;
		}
		if (deviceName.equals("WunderbarGYRO")) {
			return WunderbarGYRO;
		}
		if (deviceName.equals("WunderbarLIGHT")) {
			return WunderbarLIGHT;
		}
		if (deviceName.equals("WunderbarMIC")) {
			return WunderbarMIC;
		}
		if (deviceName.equals("WunderbarBRIDG")) {
			return WunderbarBRIDG;
		}
		if (deviceName.equals("WunderbarIR")) {
			return WunderbarIR;
		}
		if (deviceName.equals("WunderbarApp")) {
			return WunderbarApp;
		}
		if (deviceName.equals("Everykey Color")) {
			return EverykeyColor;
		}
		return Unknown;
	}
}
