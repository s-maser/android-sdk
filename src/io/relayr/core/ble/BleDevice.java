package io.relayr.core.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import io.relayr.Relayr_Application;
import io.relayr.core.observers.Observable;
import io.relayr.core.observers.Observer;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleDevice {

    private static final String TAG = BleDevice.class.getSimpleName();

	private BluetoothGattService bluetoothGattService = null;
	private BluetoothGatt gatt;
	private BleDeviceStatus status;
	private BleDeviceMode mode;
	private byte[] value;
	private Observable<BleDeviceValue> deviceValueObservable;
	private final BluetoothDevice bluetoothDevice;
	private final BleDeviceType type;
    private final BleDeviceEventCallback mModeSwitchCallback;

    private static final BleDeviceConnectionCallback mNullableConnectionCallback =
            new BleDeviceConnectionCallback() {
                @Override
                public void onConnect(BleDevice device) { }

                @Override
                public void onDisconnect(BleDevice device) { }

                @Override
                public void onError(BleDevice device, String error) { }

                @Override
                public void onWriteSuccess(BleDevice device,
                                           BleDeviceCharacteristic characteristic) { }

                @Override
                public void onWriteError(BleDevice device, BleDeviceCharacteristic characteristic,
                                         int errorStatus) { }
            };

	private BleDeviceConnectionCallback mConnectionCallback = mNullableConnectionCallback;

	BleDevice(BluetoothDevice bluetoothDevice, BleDeviceEventCallback modeSwitchCallback) {
        mModeSwitchCallback = modeSwitchCallback;
		this.bluetoothDevice = bluetoothDevice;
		this.status = BleDeviceStatus.DISCONNECTED;
		this.mode = BleDeviceMode.UNKNOWN;
		this.type = BleDeviceType.getDeviceType(bluetoothDevice.getName());
		this.deviceValueObservable = new Observable<>();
	}

    public void setBluetoothGattService(BluetoothGattService service) {
        bluetoothGattService = service;
    }

    public BleDeviceConnectionCallback getConnectionCallback() {
        return mConnectionCallback;
    }

    public String getName() {
		return bluetoothDevice.getName();
	}

	public String getAddress() {
		return bluetoothDevice.getAddress();
	}

	public BleDeviceStatus getStatus() {
		return status;
	}

	void setStatus(BleDeviceStatus status) {
		this.status = status;
	}

	public BleDeviceMode getMode() {
		return mode;
	}

	public void setMode(BleDeviceMode mode) {
		BleDeviceMode oldMode = this.mode;
		this.mode = mode;
		notifyModeSwitch(oldMode);
		notifyModeSwitch(mode);
		BleDeviceValue model = new BleDeviceValue(value, BleDataParser.getFormattedValue(type, value));
		deviceValueObservable.notifyObservers(model);
	}

	private void notifyModeSwitch(BleDeviceMode mode) {
        mModeSwitchCallback.onModeSwitch(mode, this);
	}

	public void setValue(byte[] value) {
		this.value = value;
		BleDeviceValue model = new BleDeviceValue(value, BleDataParser.getFormattedValue(type, value));
		deviceValueObservable.notifyObservers(model);
	}

	public BleDeviceType getType() {
		return type;
	}

	public void connect() {
		connect(mNullableConnectionCallback);
	}

	public void connect(BleDeviceConnectionCallback callback) {
        mConnectionCallback = callback == null ? mNullableConnectionCallback: callback;
        if (status != BleDeviceStatus.CONNECTED) {
            gatt = bluetoothDevice.connectGatt(Relayr_Application.currentActivity(), true, new BleDeviceGattManager(this, mModeSwitchCallback));
            refreshDeviceCache();
            if (status != BleDeviceStatus.CONFIGURING) {
                setStatus(BleDeviceStatus.CONNECTING);
            }
        } else {
            mConnectionCallback.onConnect(this);
        }
	}

	public void disconnect() {
        if (isConnected()) {
            if (status != BleDeviceStatus.CONFIGURING) {
                status = BleDeviceStatus.DISCONNECTING;
            }
            gatt.disconnect();
            gatt.close();
            gatt = null;
        } else {
            onDisconnect();
        }
	}

    void onDisconnect() {
        setStatus(BleDeviceStatus.DISCONNECTED);
        mConnectionCallback.onDisconnect(this);
        mConnectionCallback = mNullableConnectionCallback;
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
			return "MODE_ON_BOARDING";
		}
		case DIRECTCONNECTION: {
			return "MODE_DIRECT_CONNECTION";
		}
		default: {
			return "UNKNOWN";
		}
		}
	}

	public void updateConfiguration(final byte[] newConfiguration) {
        if (bluetoothGattService != null && mode == BleDeviceMode.DIRECTCONNECTION) {
            List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic:characteristics) {
                String characteristicUUID = getShortUUID(characteristic.getUuid().toString());
                if (characteristicUUID.equals(BleShortUUID.CHARACTERISTIC_CONFIGURATION)) {
                    Log.d(TAG, "Discovered configuration characteristic: " + characteristicUUID);
                    characteristic.setValue(newConfiguration);
                    boolean status = gatt.writeCharacteristic(characteristic);
                    Log.d(TAG, "Write action on configuration characteristic: " + (status?"done":"undone"));
                }
            }
        }
    }

	public void writeSensorId(final byte[] sensorId) {
        write(sensorId, BleShortUUID.CHARACTERISTIC_SENSOR_ID, "sensorId");
	}

	public void writePassKey(final byte[] passKey) {
        write(passKey, BleShortUUID.CHARACTERISTIC_PASS_KEY, "passKey");
	}

	public void writeOnBoardingFlag(final byte[] onBoardingFlag) {
        write(onBoardingFlag, BleShortUUID.CHARACTERISTIC_ON_BOARDING_FLAG, "onBoardingFlag");
	}

    private void write(byte[] bytes, String characteristicUUID, String logName) {
        assert(bytes != null);
        if (bluetoothGattService != null && mode == BleDeviceMode.ONBOARDING) {
            List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic:characteristics) {
                String deviceCharacteristicUUID = getShortUUID(characteristic.getUuid().toString());
                if ((deviceCharacteristicUUID.equals(characteristicUUID))) {
                    Log.d(TAG, "Discovered " + logName + " characteristic: " + characteristicUUID);
                    characteristic.setValue(bytes);
                    boolean status = gatt.writeCharacteristic(characteristic);
                    Log.d(TAG, "Write action " + logName + " characteristic: " + (status?"done":"undone"));
                    break;
                }
            }
        } else {
            mConnectionCallback.onWriteError(this, BleDeviceCharacteristic.SENSOR_ID, BluetoothGatt.GATT_FAILURE);
        }
    }

 	public boolean refreshDeviceCache() {
	    try {
	        BluetoothGatt localBluetoothGatt = gatt;
	        Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
	        if (localMethod != null) {
                return (Boolean) localMethod.invoke(localBluetoothGatt);
	         }
	    } catch (Exception localException) {
	        Log.e(TAG, "An exception occurred while refreshing device");
	    }
	    return false;
	}

	public void subscribeToDeviceValueChanges(Observer<BleDeviceValue> observer) {
		deviceValueObservable.addObserver(observer);
	}

	private String getShortUUID(String longUUID) {
    	return longUUID.substring(4, 8);
    }

    public void forceCacheRefresh() {
        refreshDeviceCache();
        try {
            gatt.discoverServices();
        } catch (Exception e) { //DeadObjectException
            disconnect();
            setStatus(BleDeviceStatus.CONFIGURING);
            connect();
        }
    }
}
