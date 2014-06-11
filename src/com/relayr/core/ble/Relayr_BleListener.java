package com.relayr.core.ble;

import java.util.HashSet;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

import com.relayr.Relayr_Application;
import com.relayr.Relayr_Commons;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BleListener {

	private static BluetoothAdapter bluetoothAdapter;
	private static Relayr_BleDevicesScanner scanner;
	private static HashSet<Relayr_BLEDevice> discoveredDevices;

	public static void init() {
		if (Relayr_Commons.isSDK18()) {
			final int bleStatus = Relayr_BleUtils.getBleStatus(Relayr_Application.currentActivity());

			switch (bleStatus) {
			case Relayr_BleUtils.STATUS_BLE_NOT_AVAILABLE:
			case Relayr_BleUtils.STATUS_BLUETOOTH_NOT_AVAILABLE:
				Log.d("Relayr_SDK", "Bluethooth not available");
				break;
			default:
				bluetoothAdapter = Relayr_BleUtils.getBluetoothAdapter(Relayr_Application.currentActivity());
				scanner = new Relayr_BleDevicesScanner(bluetoothAdapter, new BluetoothAdapter.LeScanCallback() {
					@Override
					public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
						Log.d("BLE Scan", "Detected device: " + device.getName() + " rssi: " + rssi);
						//TODO: Check if is a Relayr sensor
						discoveredDevices.add(new Relayr_BLEDevice(device, rssi));
						//TODO: Notify new device added
					}
				});
				scanner.setScanPeriod(2000);
				scanner.start();
				break;
			}
		}
	}

	public static void stop() {
		if (Relayr_Commons.isSDK18()) {
			scanner.stop();
		}
	}
}
