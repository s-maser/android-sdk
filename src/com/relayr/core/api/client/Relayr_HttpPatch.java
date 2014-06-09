package com.relayr.core.api.client;

import org.apache.http.client.methods.HttpPost;

public class Relayr_HttpPatch extends HttpPost {
    public static final String METHOD_PATCH = "PATCH";

    public Relayr_HttpPatch(final String url) {
        super(url);
    }

    @Override
    public String getMethod() {
        return METHOD_PATCH;
    }
}