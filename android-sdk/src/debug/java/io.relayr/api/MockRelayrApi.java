package io.relayr.api;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;

import io.relayr.model.App;
import io.relayr.model.CreateWunderBar;
import io.relayr.model.Device;
import io.relayr.model.Transmitter;
import io.relayr.model.TransmitterDevice;
import io.relayr.model.User;
import io.relayr.model.WebSocketConfig;
import rx.Observable;

import static io.relayr.api.MockBackend.APP_INFO;
import static io.relayr.api.MockBackend.TRANSMITTER_DEVICES;
import static io.relayr.api.MockBackend.USER_INFO;
import static io.relayr.api.MockBackend.USERS_CREATE_WUNDERBAR;
import static io.relayr.api.MockBackend.USERS_TRANSMITTER;
import static io.relayr.api.MockBackend.USERS_TRANSMITTERS;
import static io.relayr.api.MockBackend.USER_DEVICES;

public class MockRelayrApi implements RelayrApi {

    private final io.relayr.api.MockBackend mMockBackend;

    @Inject public MockRelayrApi(io.relayr.api.MockBackend mockBackend) {
        mMockBackend = mockBackend;
    }

    @Override
    public Observable<List<Device>> getUserDevices(String userId) {
        return mMockBackend.createObservable(new TypeToken<List<Device>>() {}, USER_DEVICES);
    }

    @Override
    public Observable<App> getAppInfo() {
        return mMockBackend.createObservable(new TypeToken<App>() {}, APP_INFO);
    }

    @Override
    public Observable<User> getUserInfo() {
        return mMockBackend.createObservable(new TypeToken<User>() {}, USER_INFO);
    }

    @Override
    public Observable<CreateWunderBar> createWunderBar(String userId) {
        return mMockBackend.createObservable(new TypeToken<CreateWunderBar>() {}, USERS_CREATE_WUNDERBAR);
    }

    @Override
    public Observable<List<Transmitter>> getTransmitters(String userId) {
        return mMockBackend.createObservable(new TypeToken<List<Transmitter>>() {}, USERS_TRANSMITTERS);
    }

    @Override
    public Observable<Transmitter> getTransmitter(String transmitter) {
        return mMockBackend.createObservable(new TypeToken<Transmitter>() {}, USERS_TRANSMITTER);
    }

    @Override
    public Observable<Transmitter> updateTransmitter(Transmitter transmitter, String transmitterId) {
        return Observable.from(transmitter);
    }

    @Override
    public Observable<List<TransmitterDevice>> getTransmitterDevices(String transmitter) {
        return mMockBackend.createObservable(new TypeToken<List<TransmitterDevice>>() { },
                TRANSMITTER_DEVICES);
    }

    @Override
    public Observable<WebSocketConfig> start(String appId, String sensorId) {
        return Observable.from(new WebSocketConfig());
    }

    @Override
    public Observable<Void> stop(String appId, String sensorId) {
        return Observable.from();
    }

}
