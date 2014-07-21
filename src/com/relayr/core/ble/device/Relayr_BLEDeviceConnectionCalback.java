package com.relayr.core.ble.device;

public interface Relayr_BLEDeviceConnectionCalback {

	public void onConnect(Relayr_BLEDevice device);
	public void onDisconnect(Relayr_BLEDevice device);
	public void onError(Relayr_BLEDevice device, String error);
	public void onWriteSucess(Relayr_BLEDevice device, Relayr_BLEDeviceCharacteristic characteristic);
	public void onWriteError(Relayr_BLEDevice device, Relayr_BLEDeviceCharacteristic characteristic, String error);
}
