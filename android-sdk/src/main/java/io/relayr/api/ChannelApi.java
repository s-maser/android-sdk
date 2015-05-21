package io.relayr.api;

import io.relayr.model.MqttChannel;
import io.relayr.model.MqttDefinition;
import io.relayr.model.MqttDeviceChannel;
import io.relayr.model.MqttExistingChannel;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface ChannelApi {

    @POST("/channels")
    Observable<MqttChannel> create(@Body MqttDefinition mqttDefinition);

    @DELETE("/channels/{channelId}")
    Observable<Void> delete(@Path("channelId") String channelId);

    @GET("/devices/{deviceId}/channels")
    Observable<MqttExistingChannel> getChannels(@Path("deviceId") String deviceId);

    @POST("/devices/{deviceId}/transmitter")
    Observable<MqttDeviceChannel> createForDevice(@Body MqttDefinition mqttDefinition,
                                                         @Path("deviceId") String deviceId);
}
