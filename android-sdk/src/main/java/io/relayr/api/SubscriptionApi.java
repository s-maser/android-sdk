package io.relayr.api;

import io.relayr.model.WebSocketConfig;
import retrofit.http.DELETE;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface SubscriptionApi {

    @POST("/apps/{appId}/devices/{deviceId}")
    Observable<WebSocketConfig> subscribe(@Path("appId") String appId,
                                          @Path("deviceId") String deviceId);

    @DELETE("/apps/{appId}/devices/{deviceId}")
    Observable<Void> unSubscribe(@Path("appId") String appId,
                                 @Path("deviceId") String deviceId);

}
