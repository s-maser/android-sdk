package io.relayr.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.inject.Inject;

import io.relayr.model.Reading;
import rx.Observable;
import rx.Subscriber;

public class MockBackend {

    public static final String AUTHORISE_USER = "authorise_user.json";
    public static final String USER_INFO = "user-info.json";
    public static final String APP_INFO = "app-info.json";
    public static final String USER_DEVICES = "user_devices.json";
    public static final String WEB_SOCKET_READINGS = "web_socket_reading.json";
    public static final String USERS_CREATE_WUNDERBAR = "users_create_wunderbar.json";
    public static final String USERS_TRANSMITTERS = "users_transmitters.json";
    public static final String USERS_TRANSMITTER = "users_transmitter.json";
    public static final String TRANSMITTER_DEVICES = "users_transmitters_devices.json";
    public static final String APPS_DEVICES_START = "apps_devices_start.json";
    public static final String PUBLIC_DEVICES = "users_transmitters_devices.json";

    private final Context mContext;

    @Inject public MockBackend(Context context) {
        mContext = context;
    }

    private String load(String fileName) throws Exception {
        StringBuilder fileContent = new StringBuilder();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            inputStream = mContext.getAssets().open(fileName);
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
        } finally {
            if (inputStreamReader != null)
                inputStreamReader.close();
            if (inputStream != null)
                inputStream.close();
            if (reader != null)
                reader.close();
        }
        return fileContent.toString();
    }

    public <T> T load(TypeToken<T> typeToken, String resource) throws Exception {
        return new Gson().fromJson(load(resource), typeToken.getType());
    }

    public Reading[] getWebSocketReadings() {
        try {
            List<Reading> readings = load(new TypeToken<List<Reading>>() { }, WEB_SOCKET_READINGS);
            return readings.toArray(new Reading[readings.size()]);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> Observable<T> createObservable(final TypeToken<T> typeToken, final String asset) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(load(typeToken, asset));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}
