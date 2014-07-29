package io.relayr.core.ble;

public interface DeviceConnectionCallback {

	public void onConnect(BleDevice device);
	public void onDisconnect(BleDevice device);
	public void onError(BleDevice device, String error);
	public void onWriteSuccess(BleDevice device, DeviceCharacteristic characteristic);
	public void onWriteError(BleDevice device, DeviceCharacteristic characteristic, int errorStatus);
}
