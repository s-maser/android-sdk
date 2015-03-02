package io.relayr.api;

import io.relayr.model.MqttChannel;
import io.relayr.model.MqttDefinition;
import io.relayr.model.WebSocketConfig;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface SubscriptionApi {

    @POST("/channels")
    Observable<MqttChannel> subscribe(@Body MqttDefinition mqttDefinition);
    
    @DELETE("/apps/{appId}/devices/{deviceId}")
    Observable<Void> unSubscribe(@Path("appId") String appId,
                                 @Path("deviceId") String deviceId);
}
