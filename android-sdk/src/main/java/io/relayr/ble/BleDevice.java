package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.util.Log;

import java.util.List;

import io.relayr.RelayrApp;
import io.relayr.ble.parser.BleDataParser;
import io.relayr.ble.service.BaseService;
import io.relayr.ble.service.DirectConnectionService;
import io.relayr.ble.service.MasterModuleService;
import io.relayr.ble.service.OnBoardingService;
import io.relayr.ble.service.ShortUUID;
import rx.Observable;
import rx.Subscriber;

import static android.bluetooth.BluetoothGatt.GATT_FAILURE;
import static android.bluetooth.BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
import static io.relayr.ble.BleDeviceMode.DIRECT_CONNECTION;
import static io.relayr.ble.BleDeviceMode.ON_BOARDING;
import static io.relayr.ble.BleUtils.getShortUUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleDevice {

    private static final String TAG = BleDevice.class.getSimpleName();

	/* package for testing */ BluetoothGatt gatt;
	private BluetoothGattService bluetoothGattService = null;
	private BleDeviceStatus status;
	private Subscriber<? super String> deviceValueSubscriber;
	private final BleDeviceMode mode;
	private final BluetoothDevice bluetoothDevice;
	private final BleDeviceType type;
    private final String address;
    private final String name;

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

    BleDevice(BluetoothDevice bluetoothDevice, String address, String name, BleDeviceMode mode) {
		this.bluetoothDevice = bluetoothDevice;
		this.status = BleDeviceStatus.DISCONNECTED;
		this.mode = mode;
		this.type = BleDeviceType.getDeviceType(bluetoothDevice.getName());
        this.address = address;
        this.name = name;
	}

    public void setBluetoothGattService(BluetoothGattService service) {
        bluetoothGattService = service;
    }

    public BleDeviceConnectionCallback getConnectionCallback() {
        return mConnectionCallback;
    }

    public String getName() {
		return name;
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

	public void setValue(byte[] value) {
        if (deviceValueSubscriber != null)
            deviceValueSubscriber.onNext(BleDataParser.getFormattedValue(type, value));
	}

	public BleDeviceType getType() {
		return type;
	}

	void connect() {
        setStatus(BleDeviceStatus.CONFIGURING);
		connect(mNullableConnectionCallback);
	}

    public Observable<? extends BaseService> newConnect() {
        if (mode == ON_BOARDING) {
            return OnBoardingService.connect(bluetoothDevice).cache();
        } else if (mode == DIRECT_CONNECTION) {
            return DirectConnectionService.connect(bluetoothDevice).cache();
        } else {
            return MasterModuleService.connect(bluetoothDevice).cache();
        }
    }

	public void connect(BleDeviceConnectionCallback callback) {
        mConnectionCallback = callback == null ? mNullableConnectionCallback: callback;
        if (status != BleDeviceStatus.CONNECTED) {
            refreshDeviceCache();
            if (status != BleDeviceStatus.CONFIGURING) {
                setStatus(BleDeviceStatus.CONNECTING);
            }
            gatt = bluetoothDevice.connectGatt(RelayrApp.get(), true, new BleDeviceGattManager(this));
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
        if (deviceValueSubscriber != null) deviceValueSubscriber.onCompleted();
    }

	public boolean isConnected() {
		return gatt != null;
	}

	@Override
	public String toString() {
		return getName() + " - [" + getAddress() + "] MODE: " + mode.toString();
	}

	public void writeSensorId(final byte[] sensorId) {
        write(sensorId, ShortUUID.CHARACTERISTIC_SENSOR_ID, "sensorId");
	}

	public void writePassKey(final byte[] passKey) {
        write(passKey, ShortUUID.CHARACTERISTIC_PASS_KEY, "passKey");
	}

	public void writeOnBoardingFlag(final byte[] onBoardingFlag) {
        write(onBoardingFlag, ShortUUID.CHARACTERISTIC_ON_BOARDING_FLAG, "onBoardingFlag");
	}

    private void write(byte[] bytes, String characteristicUUID, String logName) {
        assert(bytes != null);
        if (mode != ON_BOARDING) {
            mConnectionCallback.onWriteError(this, BleDeviceCharacteristic.from(characteristicUUID), GATT_REQUEST_NOT_SUPPORTED);
        } else if (bluetoothGattService != null && isConnected()) {
            List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic: characteristics) {
                String deviceCharacteristicUUID = getShortUUID(characteristic.getUuid());
                if ((deviceCharacteristicUUID.equals(characteristicUUID))) {
                    characteristic.setValue(bytes);
                    boolean status = gatt.writeCharacteristic(characteristic);
                    Log.d(TAG, "Wrote " + logName + (status ? "successfully": "unsuccessfully"));
                    return;
                }
            }
            mConnectionCallback.onWriteError(this, BleDeviceCharacteristic.from(characteristicUUID), GATT_REQUEST_NOT_SUPPORTED);
        } else {
            mConnectionCallback.onWriteError(this, BleDeviceCharacteristic.from(characteristicUUID), GATT_FAILURE);
        }
    }

 	private boolean refreshDeviceCache() {
        return  DeviceCompatibilityUtils.refresh(gatt);
	}

	public Observable<String> subscribeToDeviceValueChanges() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                deviceValueSubscriber = subscriber;
            }
        });
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
