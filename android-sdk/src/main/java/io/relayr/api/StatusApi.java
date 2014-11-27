package io.relayr.api;

import io.relayr.model.Status;
import retrofit.http.GET;
import rx.Observable;

public interface StatusApi {

    /** Checks whether server is up
     * @return  an {@link rx.Observable} with String status of the server */
    @GET("/server-status")
    Observable<Status> getServerStatus();

}
