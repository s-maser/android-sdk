package com.relayr.core.ble.device;

import java.util.Observable;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import com.relayr.Relayr_Application;
import com.relayr.core.ble.Relayr_BleGattCallback;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BLEDevice extends Observable {

	private BluetoothGatt gatt;
	private Relayr_BLEDeviceStatus status;
	private BluetoothDevice bluetoothDevice;
	private byte[] value;
	private Relayr_BLEDeviceType type;
	private BluetoothGattCharacteristic relayrConfigurationCharacteristic;

	public Relayr_BLEDevice(BluetoothDevice bluetoothDevice) {
		this.bluetoothDevice = bluetoothDevice;
		this.gatt = null;
		this.status = Relayr_BLEDeviceStatus.DISCONNECTED;
		this.type = Relayr_BLEDeviceType.getDeviceType(bluetoothDevice.getName());
		relayrConfigurationCharacteristic = null;
	}

	public String getName() {
		return bluetoothDevice.getName();
	}

	public String getAddress() {
		return bluetoothDevice.getAddress();
	}

	public Relayr_BLEDeviceStatus getStatus() {
		return status;
	}

	public void setStatus(Relayr_BLEDeviceStatus status) {
		this.status = status;
	}

	public byte[] getRawValue() {
		return value;
	}

	public JSONObject getFormattedValue() {
		switch (type) {
			case EverykeyColor:
			case WunderbarLIGHT: {
				return getColorSensorData();
			}
			default: return new JSONObject();
		}
	}

	public void setValue(byte[] value) {
		this.value = value;
		triggerObservers();
	}

	public Relayr_BLEDeviceType getType() {
		return type;
	}

	public void setRelayrConfigurationCharacteristic(
			BluetoothGattCharacteristic relayrConfigurationCharacteristic) {
		this.relayrConfigurationCharacteristic = relayrConfigurationCharacteristic;
	}

	public void connect() {
		gatt = bluetoothDevice.connectGatt(Relayr_Application.currentActivity(), true, new Relayr_BleGattCallback(this));
	}

	public void disconnect() {
		if (gatt != null) {
			gatt.disconnect();
			gatt = null;
		}
	}

	public boolean isConnected() {
		return gatt != null;
	}

	@Override
	public String toString() {
		return getName() + " - [" + getAddress() + "]";
	}

	public void triggerObservers() {
		setChanged();
		notifyObservers();
	}

	private int byteToUnsignedInt(byte b) {
	    return (int) b & 0xff;
	  }

	private JSONObject getColorSensorData() {
		JSONObject returnValue = new JSONObject();
		try {
			if (value != null) {
				int c = (byteToUnsignedInt(value[1]) << 8) | byteToUnsignedInt(value[0]);
				int r = (byteToUnsignedInt(value[3]) << 8) | byteToUnsignedInt(value[2]);
				int g = (byteToUnsignedInt(value[5]) << 8) | byteToUnsignedInt(value[4]);
				int b = (byteToUnsignedInt(value[7]) << 8) | byteToUnsignedInt(value[6]);
				int p = (byteToUnsignedInt(value[9]) << 8) | byteToUnsignedInt(value[8]);

				float rr = (float)r;
				float gg = (float)g;
				float bb = (float)b;

				//relative correction
				rr *= 2.0/3.0;

				//normalize
				float max = Math.max(rr,Math.max(gg,bb));
				rr /= max;
				gg /= max;
				bb /= max;

				JSONObject colorsArray = new JSONObject();

				colorsArray.put("r", rr);
				colorsArray.put("g", gg);
				colorsArray.put("b", bb);
				returnValue.put("clr", colorsArray);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	public boolean updateConfiguration(byte[] newConfiguration) {
		Log.d(Relayr_BLEDevice.class.toString(), "Updating device configuration --> " + (isConnected()? "Device connected" : "Device disconnected"));
		Log.d(Relayr_BLEDevice.class.toString(), "Updating device configuration --> " + (relayrConfigurationCharacteristic == null? "Configuration characteristic is null" : "onfiguration characteristic is" + relayrConfigurationCharacteristic.getUuid()));
    	if ((isConnected()) && (relayrConfigurationCharacteristic != null)) {
    		relayrConfigurationCharacteristic.setValue(newConfiguration);
    		boolean status = gatt.writeCharacteristic(relayrConfigurationCharacteristic);
    		return status;
    	}
    	return false;
    }
}
