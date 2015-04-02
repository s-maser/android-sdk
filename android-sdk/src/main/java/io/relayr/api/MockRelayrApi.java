package io.relayr.api;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;

import io.relayr.model.App;
import io.relayr.model.Bookmark;
import io.relayr.model.BookmarkDevice;
import io.relayr.model.Command;
import io.relayr.model.CreateDevice;
import io.relayr.model.CreateWunderBar;
import io.relayr.model.Device;
import io.relayr.model.Model;
import io.relayr.model.ReadingMeaning;
import io.relayr.model.Transmitter;
import io.relayr.model.TransmitterDevice;
import io.relayr.model.User;
import retrofit.http.Body;
import rx.Observable;
import rx.Subscriber;

import static io.relayr.api.MockBackend.APP_INFO;
import static io.relayr.api.MockBackend.BOOKMARKED_DEVICES;
import static io.relayr.api.MockBackend.BOOKMARK_DEVICE;
import static io.relayr.api.MockBackend.DEVICE_MODELS;
import static io.relayr.api.MockBackend.DEVICE_READING_MEANINGS;
import static io.relayr.api.MockBackend.PUBLIC_DEVICES;
import static io.relayr.api.MockBackend.TRANSMITTER_DEVICES;
import static io.relayr.api.MockBackend.USERS_CREATE_WUNDERBAR;
import static io.relayr.api.MockBackend.USERS_TRANSMITTER;
import static io.relayr.api.MockBackend.USERS_TRANSMITTERS;
import static io.relayr.api.MockBackend.USER_DEVICE;
import static io.relayr.api.MockBackend.USER_DEVICES;
import static io.relayr.api.MockBackend.USER_INFO;

public class MockRelayrApi implements RelayrApi {

    private final MockBackend mMockBackend;

    @Inject public MockRelayrApi(MockBackend mockBackend) {
        mMockBackend = mockBackend;
    }

    @Override
    public Observable<List<Device>> getUserDevices(String userId) {
        return mMockBackend.createObservable(new TypeToken<List<Device>>() {}, USER_DEVICES);
    }

    @Override
    public Observable<App> getAppInfo() {
        return mMockBackend.createObservable(new TypeToken<App>() {
        }, APP_INFO);
    }

    @Override
    public Observable<User> getUserInfo() {
        return mMockBackend.createObservable(new TypeToken<User>() {}, USER_INFO);
    }

    @Override
    public Observable<Void> sendCommand(String deviceId, Command command) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                subscriber.onNext(null);
            }
        });
    }

    @Override
    public Observable<CreateWunderBar> createWunderBar(String userId) {
        return mMockBackend.createObservable(new TypeToken<CreateWunderBar>() {},
                USERS_CREATE_WUNDERBAR);
    }

    @Override
    public Observable<List<Transmitter>> getTransmitters(String userId) {
        return mMockBackend.createObservable(new TypeToken<List<Transmitter>>() {},
                USERS_TRANSMITTERS);
    }

    @Override
    public Observable<Transmitter> getTransmitter(String transmitter) {
        return mMockBackend.createObservable(new TypeToken<Transmitter>() {}, USERS_TRANSMITTER);
    }

    @Override
    public Observable<Transmitter> updateTransmitter(Transmitter transmitter, String id) {
        return Observable.just(transmitter);
    }

    @Override
    public Observable<List<TransmitterDevice>> getTransmitterDevices(String transmitter) {
        return mMockBackend.createObservable(new TypeToken<List<TransmitterDevice>>() { },
                TRANSMITTER_DEVICES);
    }

    @Override
    public Observable<Transmitter> registerTransmitter(Transmitter transmitter) {
        return Observable.just(transmitter);
    }

    @Override
    public Observable<List<Device>> getPublicDevices(String meaning) {
        return mMockBackend.createObservable(new TypeToken<List<Device>>() { }, PUBLIC_DEVICES);
    }

    @Override
    public Observable<Bookmark> bookmarkPublicDevice(String userId, String deviceId) {
        return mMockBackend.createObservable(new TypeToken<Bookmark>() { }, BOOKMARK_DEVICE);
    }

    @Override
    public Observable<Void> deleteBookmark(String userId, String deviceId) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                subscriber.onNext(null);
            }
        });
    }

    @Override
    public Observable<List<BookmarkDevice>> getBookmarkedDevices(String userId) {
        return mMockBackend.createObservable(new TypeToken<List<BookmarkDevice>>() { },
                BOOKMARKED_DEVICES);
    }

    @Override
    public Observable<List<Model>> getDeviceModels() {
        return mMockBackend.createObservable(new TypeToken<List<Model>>() { }, DEVICE_MODELS);
    }

    @Override
    public Observable<Model> getDeviceModel(String model) {
        return mMockBackend.createObservable(new TypeToken<Model>() { }, DEVICE_MODELS);
    }

    @Override
    public Observable<List<ReadingMeaning>> getReadingMeanings() {
        return mMockBackend.createObservable(new TypeToken<List<ReadingMeaning>>() { },
                DEVICE_READING_MEANINGS);
    }

    @Override
    public Observable<Void> deleteWunderBar(String transmitterId) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                subscriber.onNext(null);
            }
        });
    }

    @Override
    public Observable<Object> getBleModels() {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                subscriber.onNext("\"name\" : \"modelId\"");
            }
        });
    }

    @Override
    public Observable<Device> createDevice(@Body CreateDevice device) {
        return mMockBackend.createObservable(new TypeToken<Device>() { },
                USER_DEVICE);    }
}
