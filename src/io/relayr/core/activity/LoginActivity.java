package io.relayr.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import javax.inject.Inject;

import io.relayr.LoginEventListener;
import io.relayr.RelayrApp;
import io.relayr.RelayrSdk;
import io.relayr.core.api.ApiModule;
import io.relayr.core.api.OauthApi;
import io.relayr.core.api.RelayrApi;
import io.relayr.core.model.OauthToken;
import io.relayr.core.model.User;
import io.relayr.core.storage.DataStorage;
import io.relayr.core.storage.RelayrProperties;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginActivity extends Activity {

    private static final String REDIRECT_URI = "http://localhost";
    @Inject OauthApi mOauthApi;
    @Inject RelayrApi mRelayrApi;
    private volatile boolean isObtainingAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelayrApp.inject(this);
        WebView mWebView = new WebView(this);
        setContentView(mWebView);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setVerticalScrollBarEnabled(false);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebView.setVisibility(View.VISIBLE);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("Login_Activity", "Webview opening: " + url);
                final String code = getCode(url);
                if (code != null && !isObtainingAccessToken) {
                    Log.d("Relayr_LoginActivity", "onPageStarted code: " + code);
                    isObtainingAccessToken = true;
                    mOauthApi
                            .authoriseUser(code,
                                    RelayrProperties.get().clientId,
                                    RelayrProperties.get().clientSecret,
                                    "authorization_code",
                                    REDIRECT_URI,
                                    "")
                            .flatMap(new Func1<OauthToken, Observable<User>>() {
                                @Override
                                public Observable<User> call(OauthToken token) {
                                    DataStorage.saveUserToken(token.type + " " + token.token);
                                    return mRelayrApi.getUserInfo();
                                }
                            })
                            .flatMap(new Func1<User, Observable<User>>() {
                                @Override
                                public Observable<User> call(User user) {
                                    DataStorage.saveUserId(user.id);
                                    return Observable.from(user);
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<Object>() {

                                @Override
                                public void onCompleted() {
                                    finish();
                                    LoginEventListener listener = RelayrSdk.getLoginEventListener();
                                    if (listener != null) listener.onSuccessUserLogIn();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    finish();
                                    LoginEventListener listener = RelayrSdk.getLoginEventListener();
                                    if (listener != null) listener.onErrorLogin(e);
                                }

                                @Override
                                public void onNext(Object a) {

                                }
                            });
                }
            }
        });
        mWebView.loadUrl(getLoginUrl());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    private String getLoginUrl() {
        Uri.Builder uriBuilder = Uri.parse(ApiModule.API_ENDPOINT).buildUpon();
        uriBuilder.path("/oauth2/auth");

        uriBuilder.appendQueryParameter("client_id", RelayrProperties.get().clientId);
        uriBuilder.appendQueryParameter("redirect_uri", REDIRECT_URI);
        uriBuilder.appendQueryParameter("response_type", "code");
        uriBuilder.appendQueryParameter("scope", "access-own-user-info");

        return uriBuilder.build().toString();
    }


    private String getCode(String url) {
        String codeParam = "?code=";
        if (url.contains(REDIRECT_URI) && url.contains(codeParam)) {
            int tokenPosition = url.indexOf(codeParam);
            String code = url.substring(tokenPosition + codeParam.length());
            if (code.contains("&")) code = code.substring(0, code.indexOf("&"));
            Log.d("Login_Activity", "Access code: " + code);
            return code;
        } else {
            return null;
        }
    }

    public static void startActivity(Activity currentActivity) {
        Intent loginActivity = new Intent(currentActivity, LoginActivity.class);
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        currentActivity.startActivity(loginActivity);
    }
}
