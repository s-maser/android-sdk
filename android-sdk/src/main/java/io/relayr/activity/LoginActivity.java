package io.relayr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import javax.inject.Inject;

import io.relayr.LoginEventListener;
import io.relayr.R;
import io.relayr.RelayrApp;
import io.relayr.RelayrSdk;
import io.relayr.api.ApiModule;
import io.relayr.api.OauthApi;
import io.relayr.api.RelayrApi;
import io.relayr.model.OauthToken;
import io.relayr.model.User;
import io.relayr.storage.DataStorage;
import io.relayr.storage.RelayrProperties;
import io.relayr.util.ReachabilityUtils;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Inject OauthApi mOauthApi;
    @Inject RelayrApi mRelayrApi;

    private volatile boolean isObtainingAccessToken;
    private WebView mWebView;
    private View mLoadingView;
    private TextView mInfoText;
    private View mInfoView;

    @SuppressLint("setJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelayrApp.inject(this);
        View view = View.inflate(this, R.layout.login_layout, null);

        mWebView = (WebView) view.findViewById(R.id.web_view);
        mLoadingView = view.findViewById(R.id.loading_spinner);
        mInfoText = (TextView) view.findViewById(R.id.info_text);
        mInfoView = view.findViewById(R.id.info_view);

        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkConditions();
    }

    /* Called from xml */
    public void onRetryClick(View view) {
        checkConditions();
    }

    /* Called from xml */
    public void onCancelClick(View view) {
        finish();
    }

    private void checkConditions() {
        showView(mLoadingView);

        if (!RelayrSdk.isPermissionGranted(ReachabilityUtils.PERMISSION_INTERNET)) {
            showWarning(String.format(getString(R.string.permission_error),
                    ReachabilityUtils.PERMISSION_INTERNET));
            return;
        }

        if (!RelayrSdk.isPermissionGranted(ReachabilityUtils.PERMISSION_NETWORK)) {
            showWarning(String.format(getString(R.string.permission_error),
                    ReachabilityUtils.PERMISSION_NETWORK));
            return;
        }

        if (!RelayrSdk.isConnectedToInternet()) {
            showWarning(getString(R.string.network_error));
            return;
        }

        RelayrSdk.isPlatformReachable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        showWarning(getString(R.string.platform_error));
                    }

                    @Override
                    public void onNext(Boolean status) {
                        if (status) showLogInScreen();
                        else showWarning(getString(R.string.platform_error));
                    }
                });
    }

    private void showLogInScreen() {
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setVerticalScrollBarEnabled(false);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        showView(mWebView);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "WebView opening: " + url);

                final String code = getCode(url);

                if (code != null && !isObtainingAccessToken) {
                    Log.d(TAG, "onPageStarted code: " + code);

                    isObtainingAccessToken = true;
                    mLoadingView.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                    mOauthApi
                            .authoriseUser(
                                    code,
                                    RelayrProperties.get().appId,
                                    RelayrProperties.get().clientSecret,
                                    "authorization_code",
                                    RelayrProperties.get().redirectUri,
                                    "")
                            .flatMap(new Func1<OauthToken, Observable<User>>() {
                                @Override
                                public Observable<User> call(OauthToken token) {
                                    DataStorage.saveUserToken(token.type + " " + token.token);
                                    return mRelayrApi.getUserInfo();
                                }
                            })
                            .map(new Func1<User, User>() {
                                @Override
                                public User call(User user) {
                                    DataStorage.saveUserId(user.id);
                                    return user;
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<User>() {

                                @Override
                                public void onCompleted() {
                                    finish();
                                    LoginEventListener listener = RelayrSdk.getLoginEventListener();
                                    if (listener != null)
                                        listener.onSuccessUserLogIn();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    finish();
                                    LoginEventListener listener = RelayrSdk.getLoginEventListener();
                                    if (listener != null)
                                        listener.onErrorLogin(e);
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

    private void showWarning(String warning) {
        Log.e(TAG, warning);

        showView(mInfoView);
        mInfoText.setText(warning);
    }

    private void showView(View view) {
        mLoadingView.setVisibility(View.GONE);
        mWebView.setVisibility(View.GONE);
        mInfoView.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
    }

    private String getLoginUrl() {
        Uri.Builder uriBuilder = Uri.parse(ApiModule.API_ENDPOINT).buildUpon();
        uriBuilder.path("/oauth2/auth");

        uriBuilder.appendQueryParameter("client_id", RelayrProperties.get().appId);
        uriBuilder.appendQueryParameter("redirect_uri", RelayrProperties.get().redirectUri);
        uriBuilder.appendQueryParameter("response_type", "code");
        uriBuilder.appendQueryParameter("scope", "access-own-user-info");

        return uriBuilder.build().toString();
    }

    static String getCode(String url) {
        String codeParam = "?code=";
        if (url.contains(RelayrProperties.get().redirectUri) && url.contains(codeParam)) {
            int tokenPosition = url.indexOf(codeParam);
            String code = url.substring(tokenPosition + codeParam.length());
            if (code.contains("&")) code = code.substring(0, code.indexOf("&"));
            Log.d(TAG, "Access code: " + code);
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
