package io.relayr;

import io.relayr.model.TransmitterDevice;
import rx.Observable;

public interface SocketClient {

    /**
     * Subscribes an app to a device channel. Enables the app to receive data from the device.
     * @param device The device object to be subscribed to.
     */
    public Observable<Object> subscribe(TransmitterDevice device);

    /**
     * Unsubscribes an app from a device channel, stopping and cleaning up the connection.
     * @param sensorId the Id of {@link io.relayr.model.TransmitterDevice}
     */
    public void unSubscribe(final String sensorId);

}
