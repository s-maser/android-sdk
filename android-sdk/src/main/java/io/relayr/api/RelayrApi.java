package io.relayr.api;

import java.util.List;

import io.relayr.model.App;
import io.relayr.model.CreateWunderBar;
import io.relayr.model.Device;
import io.relayr.model.Transmitter;
import io.relayr.model.TransmitterDevice;
import io.relayr.model.User;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface RelayrApi {

    @GET("/users/{userId}/devices")
    Observable<List<Device>> getUserDevices(@Path("userId") String userId);

    @GET("/oauth2/app-info")
    Observable<App> getAppInfo();

    @GET("/oauth2/user-info")
    Observable<User> getUserInfo();

    @POST("/users/{userId}/wunderbar")
    Observable<CreateWunderBar> createWunderBar(@Path("userId") String userId);

    @GET("/users/{userId}/transmitters")
    Observable<List<Transmitter>> getTransmitters(@Path("userId") String userId);

    @GET("/transmitters/{transmitter}")
    Observable<Transmitter> getTransmitter(@Path("transmitter") String transmitter);

    @PATCH("/transmitters/{transmitter}")
    Observable<Transmitter> updateTransmitter(@Body Transmitter transmitter,
                                              @Path("transmitter") String transmitterId);

    @GET("/transmitters/{transmitter}/devices")
    Observable<List<TransmitterDevice>> getTransmitterDevices(
            @Path("transmitter") String transmitter);

}
