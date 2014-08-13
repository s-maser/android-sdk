package io.relayr.api;

import com.google.gson.reflect.TypeToken;

import javax.inject.Inject;

import io.relayr.model.OauthToken;
import rx.Observable;

import static io.relayr.api.MockBackend.AUTHORISE_USER;

public class MockOauthApi implements OauthApi {

    private final MockBackend mMockBackend;

    @Inject
    public MockOauthApi(MockBackend mockBackend) {
        mMockBackend = mockBackend;
    }

    @Override
    public Observable<OauthToken> authoriseUser(String code, String clientId, String clientSecret,
                                                String grantType, String redirectUri, String scope) {
        return mMockBackend.createObservable(new TypeToken<OauthToken>() {}, AUTHORISE_USER);
    }
}
