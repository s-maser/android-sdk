package io.relayr.core.api;

import java.util.List;

import io.relayr.core.model.Device;
import io.relayr.core.model.CreateWunderBar;
import io.relayr.core.model.RelayrApp;
import io.relayr.core.model.Transmitter;
import io.relayr.core.model.TransmitterDevice;
import io.relayr.core.model.WebSocketConfig;
import io.relayr.core.model.User;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface RelayrApi {

    @GET("/users/{userId}/devices")
    public Observable<List<Device>> userDevices(@Path("userId") String userId);

    @GET("/oauth2/app-info")
    public Observable<RelayrApp> appInfo();

    @GET("/oauth2/user-info")
    public Observable<User> userInfo();

    @POST("/users/{userId}/wunderbar")
    public Observable<CreateWunderBar> createWunderBar(@Path("userId") String userId);

    @GET("/users/{userId}/transmitters")
    public Observable<List<Transmitter>> transmitters(@Path("userId") String userId);

    @GET("/transmitters/{transmitter}")
    public Observable<Transmitter> transmitter(@Path("transmitter") String transmitter);

    @PATCH("/transmitters/{transmitter}")
    public Observable<Transmitter> updateTransmitter(@Body Transmitter transmitter,
                                                     @Path("transmitter") String transmitterId);

    @GET("/transmitters/{transmitter}/devices")
    public Observable<List<TransmitterDevice>> transmitterDevices(
            @Path("transmitter") String transmitter);

    @POST("/apps/{appId}/devices/{deviceId}")
    public Observable<WebSocketConfig> start(
            @Path("appId") String appId,
            @Path("deviceId") String deviceId);

    @DELETE("/apps/{appId}/devices/{deviceId}")
    public Observable<Void> stop(@Path("appId") String appId,
                                 @Path("deviceId") String deviceId);

}
