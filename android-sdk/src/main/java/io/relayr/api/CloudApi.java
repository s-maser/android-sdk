package io.relayr.api;

import java.util.List;

import io.relayr.model.LogEvent;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface CloudApi {

    @POST("/client/log")
    public Observable<Void> logMessage(@Body List<LogEvent> events);

}
