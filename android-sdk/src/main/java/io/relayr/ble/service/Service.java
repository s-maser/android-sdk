package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.relayr.ble.BleUtils;
import io.relayr.ble.DeviceCompatibilityUtils;
import io.relayr.ble.service.error.CharacteristicNotFoundException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static android.bluetooth.BluetoothDevice.BOND_NONE;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SFLOAT;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16;
import static io.relayr.ble.service.Utils.getCharacteristicInServices;
import static rx.Observable.error;
import static rx.Observable.just;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class Service {

    protected BluetoothGatt mBluetoothGatt;
    protected final BluetoothGattReceiver mBluetoothGattReceiver;

    protected Service(BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        mBluetoothGatt = gatt;
        mBluetoothGattReceiver = receiver;
    }

    public BluetoothGatt getGatt() {
        return mBluetoothGatt;
    }

    protected static Observable<? extends BluetoothGatt> doConnect(
            final BluetoothDevice bluetoothDevice, final BluetoothGattReceiver receiver,
            final boolean unBond) {
        return receiver
                .connect(bluetoothDevice)
                .flatMap(new Func1<BluetoothGatt, Observable<? extends BluetoothGatt>>() {
                    @Override
                    public Observable<? extends BluetoothGatt> call(BluetoothGatt gatt) {
                        if (unBond && gatt.getDevice().getBondState() != BOND_NONE) {
                            // It was previously bonded on direct connection and needs to remove
                            // bond and update the services to work properly
                            DeviceCompatibilityUtils.removeBond(gatt.getDevice());
                            return receiver.connect(bluetoothDevice)
                                    .flatMap(new Func1<BluetoothGatt, Observable<? extends BluetoothGatt>>() {
                                        @Override
                                        public Observable<? extends BluetoothGatt> call(BluetoothGatt gatt) {
                                            DeviceCompatibilityUtils.refresh(gatt);
                                            return receiver.discoverServices(gatt);
                                        }
                                    });
                        } else if (!unBond && gatt.getDevice().getBondState() == BOND_NONE) {
                            // It was previously connected to master module and has not updated the services.
                            DeviceCompatibilityUtils.refresh(gatt);
                            return doConnect(bluetoothDevice, receiver, true);
                        }
                        return receiver.discoverServices(gatt);
                    }
                });
    }

    protected Observable<BluetoothGattCharacteristic> write(byte[] bytes,
                                                            String serviceUuid,
                                                            String characteristicUuid) {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), serviceUuid, characteristicUuid);
        if (characteristic == null) {
            return error(new CharacteristicNotFoundException(characteristicUuid));
        }
        characteristic.setValue(bytes);
        return mBluetoothGattReceiver.writeCharacteristic(mBluetoothGatt, characteristic);
    }

    private byte[] mData;

    protected Observable<BluetoothGatt> longWrite(byte[] data, String serviceUuid,
                                                  String characteristicUuid) {

        final BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), serviceUuid, characteristicUuid);

        this.mData = data;
        if (characteristic == null)
            return error(new CharacteristicNotFoundException(characteristicUuid));

        return Observable
                .create(new Observable.OnSubscribe<BluetoothGatt>() {
                    @Override
                    public void call(Subscriber<? super BluetoothGatt> subscriber) {
                        final boolean beginReliableWrite = mBluetoothGatt.beginReliableWrite();
                        Log.e("beginReliableWrite", "" + beginReliableWrite);

                        sendPayload(characteristic, subscriber);

                        final boolean executeReliableWrite = mBluetoothGatt.executeReliableWrite();
                        Log.e("executeReliableWrite", "" + executeReliableWrite);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable t) {
                        Log.e("longWrite", "onError");
                        t.printStackTrace();
                        start = 0;
                        end = 0;
                    }
                })
                .doOnNext(new Action1<BluetoothGatt>() {
                    @Override
                    public void call(BluetoothGatt bluetoothGatt) {
                        start = 0;
                        end = 0;
                    }
                });
    }

    private void sendPayload(final BluetoothGattCharacteristic characteristic,
                             Subscriber<? super BluetoothGatt> subscriber) {
        final byte[] data = getData();
        if (data.length == 0) return;

        characteristic.setValue(data);
        mBluetoothGattReceiver.reliableWriteCharacteristic(mBluetoothGatt, characteristic, subscriber);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendPayload(characteristic, subscriber);
    }

    private int start = 0;
    private int end = 0;

    private byte[] getData() {
        if (end == mData.length) return new byte[]{};

        byte[] chunk;
        int chunkSize = 16;
        int chunkOffset = 2;

        byte[] offset = ByteBuffer.allocate(2).putShort((short) start).array();
        byte[] length = new byte[0];

        if (start == 0) {
            length = ByteBuffer.allocate(2).putShort((short) mData.length).array();
            chunkSize = 14;
            chunkOffset = 4;
        }

        end = Math.min(start + chunkSize, mData.length);
        chunkSize = end - start;
        byte[] payload = new byte[chunkSize + chunkOffset];
        chunk = Arrays.copyOfRange(mData, start, end);
        start += chunkSize;

        System.arraycopy(offset, 0, payload, 0, offset.length);
        if (start == 0) System.arraycopy(length, 0, payload, 2, length.length);
        System.arraycopy(chunk, 0, payload, chunkOffset, chunk.length);

        String payloadString = "";
        for (byte b : payload)
            payloadString += " " + b;
        Log.e("getData", "payload: " + payloadString);

        return payload;
    }

    protected Observable<BluetoothGattCharacteristic> readCharacteristic(String serviceUuid,
                                                                         String characteristicUuid,
                                                                         final String what) {

        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), serviceUuid, characteristicUuid);
        if (characteristic == null) {
            return error(new CharacteristicNotFoundException(what));
        }
        return mBluetoothGattReceiver.readCharacteristic(mBluetoothGatt, characteristic);
    }

    protected Observable<Float> readFloatCharacteristic(String serviceUuid,
                                                        String characteristicUuid,
                                                        final String what) {
        return readCharacteristic(serviceUuid, characteristicUuid, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<Float>>() {
                    @Override
                    public Observable<Float> call(BluetoothGattCharacteristic charac) {
                        if (charac.getValue() == null || charac.getValue().length == 0) {
                            error(new CharacteristicNotFoundException(what));
                        }
                        return just(charac.getFloatValue(FORMAT_SFLOAT, 0));
                    }
                });
    }

    protected Observable<Integer> readIntegerCharacteristic(String serviceUuid,
                                                            String characteristicUuid,
                                                            final String what) {
        return readCharacteristic(serviceUuid, characteristicUuid, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(BluetoothGattCharacteristic charac) {
                        if (charac.getValue() == null || charac.getValue().length == 0) {
                            error(new CharacteristicNotFoundException(what));
                        }
                        return just(charac.getIntValue(FORMAT_UINT16, 0));
                    }
                });
    }

    protected Observable<Integer> readByteAsAnIntegerCharacteristic(String serviceUuid,
                                                                    String characteristicUuid,
                                                                    final String what) {
        return readCharacteristic(serviceUuid, characteristicUuid, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(BluetoothGattCharacteristic charac) {
                        if (charac.getValue() == null || charac.getValue().length == 0) {
                            error(new CharacteristicNotFoundException(what));
                        }
                        return just((int) charac.getValue()[0]);
                    }
                });
    }


    protected Observable<String> readStringCharacteristic(String serviceUuid,
                                                          String characteristicUuid,
                                                          final String what) {
        return readCharacteristic(serviceUuid, characteristicUuid, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<String>>() {
                    @Override
                    public Observable<String> call(BluetoothGattCharacteristic charac) {
                        String value = charac.getStringValue(0);
                        if (value == null) {
                            return error(new CharacteristicNotFoundException(what));
                        }
                        return just(value);
                    }
                });
    }

    protected Observable<UUID> readUuidCharacteristic(String service, String characteristic,
                                                      final String what) {
        return readCharacteristic(service, characteristic, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<UUID>>() {
                    @Override
                    public Observable<UUID> call(BluetoothGattCharacteristic characteristic) {
                        byte[] value = characteristic.getValue();
                        if (value == null) {
                            return error(new CharacteristicNotFoundException(what));
                        }
                        return just(BleUtils.fromBytes(value));
                    }
                });
    }

}
