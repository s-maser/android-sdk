package com.relayr.core.ble.device;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.util.Log;

import com.relayr.Relayr_Application;
import com.relayr.core.ble.Relayr_BleGattCallback;
import com.relayr.core.ble.Relayr_BleListener;
import com.relayr.core.observers.Observable;
import com.relayr.core.observers.Observer;
import com.relayr.core.observers.Subscription;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BLEDevice {

	public BluetoothGatt gatt;
	private Relayr_BLEDeviceStatus status;
	private Relayr_BLEDeviceMode mode;
	private BluetoothDevice bluetoothDevice;
	private byte[] value;
	private Relayr_BLEDeviceType type;
	public BluetoothGattService currentService;
	public Relayr_BLEDeviceConnectionCalback connectionCallback;
	private Observable<Relayr_BLEDeviceValue> deviceValueObservable;

	public Relayr_BLEDevice(BluetoothDevice bluetoothDevice) {
		this.bluetoothDevice = bluetoothDevice;
		this.status = Relayr_BLEDeviceStatus.DISCONNECTED;
		this.mode = Relayr_BLEDeviceMode.UNKNOWN;
		this.type = Relayr_BLEDeviceType.getDeviceType(bluetoothDevice.getName());
		currentService = null;
		this.deviceValueObservable = new Observable<Relayr_BLEDeviceValue>();
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

	public Relayr_BLEDeviceMode getMode() {
		return mode;
	}

	public void setMode(Relayr_BLEDeviceMode mode) {
		Relayr_BLEDeviceMode oldMode = this.mode;
		this.mode = mode;
		notifyModeSwitch(oldMode);
		notifyModeSwitch(mode);
		Relayr_BLEDeviceValue model = new Relayr_BLEDeviceValue(value, getFormattedValue());
		deviceValueObservable.notifyObservers(model);
	}

	private void notifyModeSwitch(Relayr_BLEDeviceMode mode) {
		if (Relayr_BleListener.discoveredDevices.isFullyConfigured(getAddress())) {
			switch (mode) {
			case ONBOARDING: {
				Relayr_BleListener.discoveredDevices.onBoardingDeviceListUpdate();
				break;
			}
			case DIRECTCONNECTION: {
				Relayr_BleListener.discoveredDevices.directConnectedDeviceListUpdate();
				break;
			}
			default:break;
			}
		}
	}

	private JSONObject getFormattedValue() {
		switch (type) {
			case WunderbarLIGHT: {
				return Relayr_BLEDeviceDataAdapter.getLIGHTSensorData(value);
			}
			case WunderbarGYRO: {
				return Relayr_BLEDeviceDataAdapter.getGYROSensorData(value);
			}
			case WunderbarHTU: {
				return Relayr_BLEDeviceDataAdapter.getHTUSensorData(value);
			}
			case WunderbarMIC: {
				return Relayr_BLEDeviceDataAdapter.getMICSensorData(value);
			}
			default: return new JSONObject();
		}
	}

	public void setValue(byte[] value) {
		this.value = value;
		Relayr_BLEDeviceValue model = new Relayr_BLEDeviceValue(value, getFormattedValue());
		deviceValueObservable.notifyObservers(model);
	}

	public Relayr_BLEDeviceType getType() {
		return type;
	}

	public void connect() {
		connect(null);
	}

	public void connect(final Relayr_BLEDeviceConnectionCalback callback) {
        if (callback != null) {
            connectionCallback = callback;
        }
        if (status != Relayr_BLEDeviceStatus.CONNECTED) {
            gatt = bluetoothDevice.connectGatt(Relayr_Application.currentActivity(), true, new Relayr_BleGattCallback(this));
            refreshDeviceCache(gatt);
            if (status != Relayr_BLEDeviceStatus.CONFIGURING) {
                setStatus(Relayr_BLEDeviceStatus.CONNECTING);
            }
        } else {
            if (connectionCallback != null) {
                connectionCallback.onConnect(this);
            }
        }
	}

	public void disconnect() {
        if (gatt != null) {
            if (status != Relayr_BLEDeviceStatus.CONFIGURING) {
                status = Relayr_BLEDeviceStatus.DISCONNECTING;
            }
            gatt.disconnect();
            gatt.close();
            gatt = null;
        } else {
            if (connectionCallback != null) {
                connectionCallback.onDisconnect(this);
            }
        }
	}

	public boolean isConnected() {
		return gatt != null;
	}

	@Override
	public String toString() {
		return getName() + " - [" + getAddress() + "] MODE: " + getModeString();
	}

	private String getModeString() {
		switch(this.mode) {
		case ONBOARDING: {
			return "ON_BOARDING";
		}
		case DIRECTCONNECTION: {
			return "DIRECT_CONNECTION";
		}
		default: {
			return "UNKNOWN";
		}
		}
	}

	public void updateConfiguration(final byte[] newConfiguration) {
        if (currentService != null && mode == Relayr_BLEDeviceMode.DIRECTCONNECTION) {
            List<BluetoothGattCharacteristic> characteristics = currentService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic:characteristics) {
                String characteristicUUID = getShortUUID(characteristic.getUuid().toString());
                if (characteristicUUID.equals(Relayr_BLEDeviceType.configurationCharacteristicUUID)) {
                    Log.d(Relayr_BleGattCallback.class.toString(), "Discovered configuration characteristic: " + characteristicUUID);
                    characteristic.setValue(newConfiguration);
                    boolean status = gatt.writeCharacteristic(characteristic);
                    Log.d(Relayr_BleGattCallback.class.toString(), "Write action on configuration characteristic: " + (status?"done":"undone"));
                }
            }
        }
    }

	public void writeSensorId(final byte[] sensorId) {
        write(sensorId, Relayr_BLEDeviceType.sensorIDCharacteristicUUID, "sensorId");
	}

	public void writePassKey(final byte[] passKey) {
        write(passKey, Relayr_BLEDeviceType.passKeyCharacteristicUUID, "passKey");
	}

	public void writeOnBoardingFlag(final byte[] onBoardingFlag) {
        write(onBoardingFlag, Relayr_BLEDeviceType.onBoardingFlagCharacteristicUUID, "onBoardingFlag");
	}

    private void write(byte[] bytes, String characteristicUUID, String logName) {
        assert(bytes != null);
        if (currentService != null && mode == Relayr_BLEDeviceMode.ONBOARDING) {
            List<BluetoothGattCharacteristic> characteristics = currentService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic:characteristics) {
                String deviceCharacteristicUUID = getShortUUID(characteristic.getUuid().toString());
                if ((deviceCharacteristicUUID.equals(characteristicUUID))) {
                    Log.d(Relayr_BleGattCallback.class.toString(), "Discovered " + logName + " characteristic: " + characteristicUUID);
                    characteristic.setValue(bytes);
                    boolean status = gatt.writeCharacteristic(characteristic);
                    Log.d(Relayr_BleGattCallback.class.toString(), "Write action " + logName + " characteristic: " + (status?"done":"undone"));
                    break;
                }
            }
        } else {
            if (connectionCallback != null) {
                connectionCallback.onWriteError(this, Relayr_BLEDeviceCharacteristic.SENSOR_ID, BluetoothGatt.GATT_FAILURE);
            }
        }
    }

 	public boolean refreshDeviceCache(BluetoothGatt gatt){
	    try {
	        BluetoothGatt localBluetoothGatt = gatt;
	        Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
	        if (localMethod != null) {
	           boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
	            return bool;
	         }
	    }
	    catch (Exception localException) {
	        Log.e(Relayr_BLEDevice.class.toString(), "An exception occurred while refreshing device");
	    }
	    return false;
	}

	public Subscription<Relayr_BLEDeviceValue> subscribeToDeviceValueChanges(Observer<Relayr_BLEDeviceValue> observer) {
		deviceValueObservable.addObserver(observer);
		return new Subscription<Relayr_BLEDeviceValue>(observer, deviceValueObservable);
	}

	private String getShortUUID(String longUUID) {
    	return longUUID.substring(4, 8);
    }
}
