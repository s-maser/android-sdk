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

import io.relayr.core.settings.RelayrProperties;
import io.relayr.core.settings.Relayr_SDKStatus;

public class Relayr_LoginActivity extends Relayr_Activity {

    private static final String API_ENDPOINT = "https://api.relayr.io";

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mWebView = new WebView(this);
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
				String accessCode = getAccessCode(url);
				if (accessCode != null) {
					Log.d("Relayr_LoginActivity", "onPageStarted access code: " + accessCode);
					try {
						Relayr_SDKStatus.synchronizeTokenInfo(Relayr_LoginActivity.this, accessCode);
					} catch (Exception e) {
						Log.d("Relayr_LoginActivity", "Error: " + e.getMessage());
					}
					finish();
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


    private String getAccessCode(String url) {
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
        Intent loginActivity = new Intent(currentActivity, Relayr_LoginActivity.class);
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        currentActivity.startActivity(loginActivity);
    }
}
