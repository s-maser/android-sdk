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

import io.relayr.RelayrApp;
import io.relayr.RelayrSdk;
import io.relayr.core.api.RelayrApi;
import io.relayr.LoginEventListener;
import io.relayr.core.storage.DataStorage;
import io.relayr.core.storage.RelayrProperties;
import io.relayr.core.model.User;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginActivity extends Activity {

    private static final String API_ENDPOINT = "https://api.relayr.io";
    @Inject RelayrApi mRelayrApi;

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
				final String accessToken = getAccessToken(url);
				if (accessToken != null) {
					Log.d("Relayr_LoginActivity", "onPageStarted access token: " + accessToken);
                    mRelayrApi
                            .userInfo()
                            .flatMap(new Func1<User, Observable<User>>() {
                                @Override
                                public Observable<User> call(User user) {
                                    DataStorage.saveUserToken(accessToken);
                                    DataStorage.saveUserId(user.id);
                                    return Observable.from(user);
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<User>() {

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
                                public void onNext(User user) {

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
        Uri.Builder uriBuilder = Uri.parse(API_ENDPOINT).buildUpon();
        uriBuilder.path("/oauth2/auth");

        uriBuilder.appendQueryParameter("client_id", RelayrProperties.get().clientId);
        uriBuilder.appendQueryParameter("redirect_uri", "http://localhost");
        uriBuilder.appendQueryParameter("response_type", "code");
        uriBuilder.appendQueryParameter("scope", "access-own-user-info");

        return uriBuilder.build().toString();
    }


    private String getAccessToken(String url) {
		String codeParam = "?code=";
		if (url.contains(codeParam)) {
			int tokenPosition = url.indexOf(codeParam);
			String code = url.substring(tokenPosition + codeParam.length());
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
