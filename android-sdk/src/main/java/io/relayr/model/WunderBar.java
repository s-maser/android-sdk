package io.relayr.model;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.relayr.ble.BleDeviceType;

public class WunderBar implements Serializable {

    public final IntegrationType type;
    public final Transmitter masterModule;
    public List<Pair<DeviceModel, TransmitterDevice>> wbDevices = new ArrayList<>();

    public WunderBar(Transmitter masterModule, TransmitterDevice gyroscope,
                     TransmitterDevice light, TransmitterDevice microphone,
                     TransmitterDevice thermometer, TransmitterDevice infrared,
                     TransmitterDevice bridge) {
        this.type = IntegrationType.WUNDERBAR_1;
        this.masterModule = masterModule;
        this.wbDevices = Arrays.asList(new Pair<>(DeviceModel.ACCELEROMETER_GYROSCOPE, gyroscope),
                new Pair<>(DeviceModel.LIGHT_PROX_COLOR, light),
                new Pair<>(DeviceModel.MICROPHONE, microphone),
                new Pair<>(DeviceModel.TEMPERATURE_HUMIDITY, thermometer),
                new Pair<>(DeviceModel.IR_TRANSMITTER, infrared),
                new Pair<>(DeviceModel.GROVE, bridge));
    }

    public WunderBar(Transmitter masterModule, List<TransmitterDevice> devices, IntegrationType type) {
        this.type = type;
        this.masterModule = masterModule;
        for (TransmitterDevice device : devices) {
            wbDevices.add(new Pair<>(device.getModel(), device));
        }
    }

    public WunderBar(Transmitter masterModule) {
        this(masterModule, new ArrayList<TransmitterDevice>(), IntegrationType.WUNDERBAR_2);
    }

    public void addDevice(TransmitterDevice device) {
        wbDevices.add(new Pair<>(device.getModel(), device));
    }

    public static WunderBar from(Transmitter masterModule, List<TransmitterDevice> devices) {
        return new WunderBar(masterModule, devices, IntegrationType.WUNDERBAR_1);
    }

    public TransmitterDevice getDevice(BleDeviceType type) {
        DeviceModel model = resolveType(type);
        for (Pair<DeviceModel, TransmitterDevice> device : wbDevices)
            if (device.first == model) return device.second;

        return null;
    }

    public TransmitterDevice getDevice(DeviceModel model) {
        for (Pair<DeviceModel, TransmitterDevice> device : wbDevices)
            if (device.first == model) return device.second;

        return null;
    }

    private DeviceModel resolveType(BleDeviceType type) {
        return type == BleDeviceType.WunderbarHTU ? DeviceModel.TEMPERATURE_HUMIDITY :
                type == BleDeviceType.WunderbarGYRO ? DeviceModel.ACCELEROMETER_GYROSCOPE :
                        type == BleDeviceType.WunderbarMIC ? DeviceModel.MICROPHONE :
                                type == BleDeviceType.WunderbarLIGHT ? DeviceModel.LIGHT_PROX_COLOR :
                                        type == BleDeviceType.WunderbarIR ? DeviceModel.IR_TRANSMITTER :
                                                DeviceModel.GROVE;
    }
}
