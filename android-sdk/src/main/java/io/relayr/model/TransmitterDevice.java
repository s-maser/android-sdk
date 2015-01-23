package io.relayr.model;

import io.relayr.RelayrSdk;
import rx.Observable;

/**
 * The transmitter device object holds the same information as the {@link io.relayr.model.Device}
 * The difference is that the model attribute in the former is an ID rather than an object.
 */
public class TransmitterDevice extends Transmitter {

    public final String model;

    public TransmitterDevice(String id, String secret, String owner, String name,
                             String model) {
        super(id, secret, owner, name);
        this.model = model;
    }

    public DeviceModel getModel() {
        return DeviceModel.from(model);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TransmitterDevice && ((TransmitterDevice) o).id.equals(id) ||
                o instanceof Device && ((Device) o).id.equals(id);
    }

    public Observable<Object> subscribeToCloudReadings() {
        return RelayrSdk.getWebSocketClient().subscribe(this);
    }

    public void unSubscribeToCloudReadings() {
        RelayrSdk.getWebSocketClient().unSubscribe(id);
    }
    
}
