package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import io.relayr.RelayrApp;
import rx.Observable;
import rx.Subscriber;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleDevice {

    private static final String TAG = BleDevice.class.getSimpleName();

	private BluetoothGattService bluetoothGattService = null;
	private BluetoothGatt gatt;
	private BleDeviceStatus status;
	private BleDeviceMode mode;
	private Subscriber<? super BleDeviceValue> deviceValueSubscriber;
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
    private final String address;

    BleDevice(BluetoothDevice bluetoothDevice, BleDeviceEventCallback modeSwitchCallback, String address, BleDeviceMode mode) {
        mModeSwitchCallback = modeSwitchCallback;
		this.bluetoothDevice = bluetoothDevice;
		this.status = BleDeviceStatus.DISCONNECTED;
		this.mode = mode;
		this.type = BleDeviceType.getDeviceType(bluetoothDevice.getName());
        this.address = address;
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
		return address;
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
		this.mode = mode;
        mModeSwitchCallback.onModeSwitch(mode, this);
	}

	public void setValue(byte[] value) {
		BleDeviceValue model = new BleDeviceValue(value, BleDataParser.getFormattedValue(type, value));
        if (deviceValueSubscriber != null) deviceValueSubscriber.onNext(model);
	}

	public BleDeviceType getType() {
		return type;
	}

	void connect() {
        setStatus(BleDeviceStatus.CONFIGURING);
		connect(mNullableConnectionCallback);
	}

	public void connect(BleDeviceConnectionCallback callback) {
        mConnectionCallback = callback == null ? mNullableConnectionCallback: callback;
        if (status != BleDeviceStatus.CONNECTED) {
            gatt = bluetoothDevice.connectGatt(RelayrApp.get(), true, new BleDeviceGattManager(this, mModeSwitchCallback));
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
            refreshDeviceCache();
            gatt.disconnect();
            gatt.close();
            gatt = null;
        } else {
            onDisconnect();
        }
	}

    void onConnect() {
        setStatus(BleDeviceStatus.CONNECTED);
        Log.d(TAG, "Callback detected: sending onConnect event to " + type);
        mConnectionCallback.onConnect(this);
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
		return getName() + " - [" + getAddress() + "] MODE: " + mode.toString();
	}

	public void updateConfiguration(final byte[] newConfiguration) {
        if (bluetoothGattService != null && mode == BleDeviceMode.DIRECT_CONNECTION) {
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
        if (bluetoothGattService != null && mode == BleDeviceMode.ON_BOARDING && isConnected()) {
            List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic:characteristics) {
                String deviceCharacteristicUUID = getShortUUID(characteristic.getUuid().toString());
                if ((deviceCharacteristicUUID.equals(characteristicUUID))) {
                    characteristic.setValue(bytes);
                    boolean status = gatt.writeCharacteristic(characteristic);
                    Log.d(TAG, "Wrote " + logName + (status ? "successfully": "unsuccessfully"));
                    return;
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

	public Observable<BleDeviceValue> subscribeToDeviceValueChanges() {
        return Observable.create(new Observable.OnSubscribe<BleDeviceValue>() {
            @Override
            public void call(Subscriber<? super BleDeviceValue> subscriber) {
                deviceValueSubscriber = subscriber;
            }
        });
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
            connect();
        }
    }
}
