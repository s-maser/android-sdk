package com.relayr.core.ble.device;

public interface Relayr_BLEDeviceConnectionCalback {

	public void onConnect(Relayr_BLEDevice device);
	public void onDisconnect(Relayr_BLEDevice device);
	public void onError(Relayr_BLEDevice device, String error);

}
