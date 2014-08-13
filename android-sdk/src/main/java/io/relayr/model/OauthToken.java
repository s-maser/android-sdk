package io.relayr.model;

import com.google.gson.annotations.SerializedName;

public class OauthToken {

    @SerializedName("access_token") public final String token;
    @SerializedName("token_type") public final String type;

    public OauthToken(String token, String type) {
        this.token = token;
        this.type = type;
    }
}
