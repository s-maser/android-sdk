package io.relayr.api;

import java.util.List;

import io.relayr.model.App;
import io.relayr.model.Bookmark;
import io.relayr.model.BookmarkDevice;
import io.relayr.model.Command;
import io.relayr.model.CreateWunderBar;
import io.relayr.model.Device;
import io.relayr.model.Model;
import io.relayr.model.ReadingMeaning;
import io.relayr.model.Transmitter;
import io.relayr.model.TransmitterDevice;
import io.relayr.model.User;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/** This class incorporates a wrapped version of the relayr API calls. */
public interface RelayrApi {

    /** @return an {@link rx.Observable} of a list of devices registered under a user. */
    @GET("/users/{userId}/devices")
    Observable<List<Device>> getUserDevices(@Path("userId") String userId);

    /** @return an {@link rx.Observable} to the information about the app initiating the request. */
    @GET("/oauth2/app-info")
    Observable<App> getAppInfo();

    /** @return an {@link rx.Observable} information about the user initiating the request. */
    @GET("/oauth2/user-info")
    Observable<User> getUserInfo();

    @POST("/devices/{device_id}/cmd/{command_name}")
    Observable<Void> sendCommand(@Path("device_id") String deviceId,
                                 @Path("command_name") String commandName,
                                 @Body Command command);

    /** Api call to tell the backend to create WunderBar.
     * @return an {@link rx.Observable} to a WunderBar that contains the IDs and Secrets of the
     * Master Module and Sensor Modules. */
    @POST("/users/{userId}/wunderbar")
    Observable<CreateWunderBar> createWunderBar(@Path("userId") String userId);

    /** @return an {@link rx.Observable} with a list all Transmitters listed under a user. */
    @GET("/users/{userId}/transmitters")
    Observable<List<Transmitter>> getTransmitters(@Path("userId") String userId);

    /** @return an {@link rx.Observable} of a specific transmitter */
    @GET("/transmitters/{transmitter}")
    Observable<Transmitter> getTransmitter(@Path("transmitter") String transmitter);

    /** Updates a transmitter.
     * @param transmitter updated transmitter with the new details
     * @param transmitterId id of the transmitter to update
     * @return an {@link rx.Observable} to the updated Transmitter */
    @PATCH("/transmitters/{transmitter}")
    Observable<Transmitter> updateTransmitter(@Body Transmitter transmitter,
                                              @Path("transmitter") String transmitterId);

    /**
     * @param transmitter the id of the transmitter to get the devices from
     * @return an {@link rx.Observable} with a list of devices that belong to the specific
     * transmitter. */
    @GET("/transmitters/{transmitter}/devices")
    Observable<List<TransmitterDevice>> getTransmitterDevices(
            @Path("transmitter") String transmitter);

    /**
     * Registers the transmitter
     * @param transmitter transmitter object to register
     * @return an {@link rx.Observable} to the registered Transmitter */
    @POST("/transmitters")
    Observable<Transmitter> registerTransmitter(@Body Transmitter transmitter);

    /** A public device is a device which public attribute has been set to 'true' therefore
     * no authorization is required.
     * @param meaning When a meaning is specified, the request returns only
     *          the devices which readings match the meaning.
     * @return an {@link rx.Observable} with a list of all public devices. */
    @GET("/devices/public")
    Observable<List<Device>> getPublicDevices(@Query("meaning") String meaning);

    /** Bookmarks a specific public device. In order to receive data from a bookmarked device,
     * the subscription call must first be initiated.
     * @param userId id of the user that is bookmarking the device
     * @param deviceId id of bookmarked device - the Id must be one of a public device
     * @return an {@link rx.Observable} to the bookmarked device */
    @POST("/users/{userId}/devices/{deviceId}/bookmarks")
    Observable<Bookmark> bookmarkPublicDevice(@Path("userId") String userId,
                                              @Path("deviceId") String deviceId);

    /** Deletes a bookmarked device.
     * @param userId id of the user that bookmarked the device
     * @param deviceId id of bookmarked device - the Id must be one of a public device
     * @return an empty {@link rx.Observable} */
    @DELETE("/users/{userId}/devices/{deviceId}/bookmarks")
    Observable<Void> removeBookmark(@Path("userId") String userId,
                                    @Path("deviceId") String deviceId);

    /** Returns a list of devices bookmarked by the user.
     * @param userId id of the user that bookmarked devices
     * @return an {@link rx.Observable} with a list of the users bookmarked devices */
    @GET("/users/{userId}/devices/bookmarks")
    Observable<List<BookmarkDevice>> getBookmarkedDevices(@Path("userId") String userId);

    /** Returns all available device models.
     * @return an {@link rx.Observable} with a list of all available device models */
    @GET("/device-models")
    Observable<List<Model>> getDeviceModels();

    /** Returns information about a specific device model
     * @param model id of the device model
     * @return an {@link rx.Observable} of a specific device model */
    @GET("/device-models/{model}")
    Observable<Model> getDeviceModel(@Path("model") String model);

    /** Returns a list of the possible reading types of the devices on the relayr platform
     * @return an {@link rx.Observable} with a list of Reading meanings */
    @GET("/device-models/meanings")
    Observable<List<ReadingMeaning>> getReadingMeanings();

    /** Deletes a Wunderbar and all of its components (Transmitter and Devices)
     * @param transmitterId id of the transmitter (the Master Module)
     * @return an empty {@link rx.Observable} */
    @DELETE("/wunderbars/{transmitterId}")
    Observable<Void> deleteWunderbar(@Path("transmitterId") String transmitterId);
}
