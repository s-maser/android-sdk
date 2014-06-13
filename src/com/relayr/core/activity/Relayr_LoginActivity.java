package com.relayr.core.activity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.relayr.Relayr_SDK;
import com.relayr.core.api.Relayr_ApiCall;
import com.relayr.core.api.Relayr_ApiConnector;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.event_listeners.Relayr_LoginEventListener;
import com.relayr.core.settings.Relayr_SDKStatus;

public class Relayr_LoginActivity extends Relayr_Activity {

	WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layoutID = getResources().getIdentifier("login_view", "layout", getPackageName());
		System.out.println(layoutID);
		setContentView(layoutID);

		int webViewID = getResources().getIdentifier("relayr_login_view", "id", getPackageName());
		mWebView = (WebView)findViewById(webViewID);

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
				String token = getToken(url);
				if (token != null) {
					Log.d("Relayr_LoginActivity", "onPageStarted token: " + token);
					Relayr_SDKStatus.setUserToken(token);
					Relayr_LoginEventListener listener = Relayr_SDK.getLoginEventListener();
					if (listener != null) {
						listener.onUserLoggedInSuccessfully();
					}
					try {
						Relayr_SDKStatus.synchronizeUserInfo();
					} catch (Exception e) {
						Log.d("Relayr_LoginActivity", "Error: " + e.getMessage());
					}

					Relayr_LoginActivity.this.onBackPressed();
				}
			}
		});

		loadWebViewContent();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
	    super.onConfigurationChanged(newConfig);
	}

	private void loadWebViewContent() {
		String url = null;
		try {
			Object[] parameters = {};
			url = (String)Relayr_ApiConnector.doCall(Relayr_ApiCall.UserAuthorization, parameters);
			mWebView.loadUrl(url);
		} catch (Relayr_Exception e) {
			e.printStackTrace();
		}
	}

	private String getToken(String url) {
		String tokenParam = "#access_token=";

		if (url.contains(tokenParam)) {
			String[] urlByParameters = url.split("&");
			int tokenPosition = urlByParameters[0].indexOf(tokenParam);
			String token = urlByParameters[0].substring(tokenPosition + tokenParam.length());
			Log.d("Login_Activity", "Token: " + token);
			return token;
		} else {
			return null;
		}
	}

}
