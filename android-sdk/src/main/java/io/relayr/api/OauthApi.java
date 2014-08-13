package io.relayr.api;

import io.relayr.model.OauthToken;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public interface OauthApi {

    @FormUrlEncoded
    @POST("/oauth2/token")
    Observable<OauthToken> authoriseUser(@Field("code") String code,
                                         @Field("client_id") String clientId,
                                         @Field("client_secret") String clientSecret,
                                         @Field("grant_type") String grantType,
                                         @Field("redirect_uri") String redirectUri,
                                         @Field("scope") String scope);

}
