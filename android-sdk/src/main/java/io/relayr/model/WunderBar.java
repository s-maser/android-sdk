package io.relayr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.relayr.ble.BleDeviceType;

public class WunderBar implements Serializable {

    public final AccountType type;
    public final Transmitter masterModule;
    public List<TransmitterDevice> wbDevices = new ArrayList<>();

    public WunderBar(Transmitter masterModule, TransmitterDevice gyroscope,
                     TransmitterDevice light, TransmitterDevice microphone,
                     TransmitterDevice thermometer, TransmitterDevice infrared,
                     TransmitterDevice bridge) {
        this.type = AccountType.WUNDERBAR_1;
        this.masterModule = masterModule;
        this.wbDevices = Arrays.asList(gyroscope, light, microphone, thermometer, infrared, bridge);
    }

    public WunderBar(Transmitter masterModule, List<TransmitterDevice> devices, AccountType type) {
        this.type = type == null ? AccountType.WUNDERBAR_1 : type;
        this.masterModule = masterModule;
        wbDevices.addAll(devices);
    }

    //WB2
    public WunderBar(Transmitter masterModule) {
        this(masterModule, new ArrayList<TransmitterDevice>(), AccountType.WUNDERBAR_2);
    }

    public void addDevice(TransmitterDevice device) {
        wbDevices.add(device);
    }

    public static WunderBar from(Transmitter masterModule, List<TransmitterDevice> devices) {
        return new WunderBar(masterModule, devices, masterModule.getAccountType());
    }

    public TransmitterDevice getDevice(BleDeviceType type) {
        DeviceModel model = resolveType(type);
        for (TransmitterDevice device : wbDevices)
            if (device.getModel() == model) return device;

        return null;
    }

    public TransmitterDevice getDevice(DeviceModel model) {
        for (TransmitterDevice device : wbDevices)
            if (device.getModel() == model) return device;

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
