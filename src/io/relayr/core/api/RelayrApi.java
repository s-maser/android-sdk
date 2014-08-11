package io.relayr.core.api;

import io.relayr.core.user.Relayr_User;
import retrofit.http.GET;
import rx.Observable;

public interface RelayrApi {

    @GET("/oauth2/user-info")
    public Observable<Relayr_User> userInfo();

}
