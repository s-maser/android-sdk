package io.relayr.core.api;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import io.relayr.core.api.client.Relayr_HttpPatch;
import io.relayr.core.error.Relayr_Exception;
import io.relayr.core.settings.Relayr_SDKStatus;


public class Relayr_ApiRequest {

	private static int CONNECTION_TIMEOUT = 2;
	private static int READ_TIMEOUT = 10;

	public static Object execute (Relayr_ApiCall call, Object... params) throws Relayr_Exception {
		DefaultHttpClient httpClient = createHttpClient();
		httpClient.getConnectionManager().closeExpiredConnections();
		httpClient.getConnectionManager().closeIdleConnections(30, TimeUnit.SECONDS );

		Relayr_ApiCallMethod callMethod = getCallMethod(call);
		HttpRequestBase request;
		switch (callMethod) {
		case POST: {
			try {
				HttpPost postRequest = new HttpPost(Relayr_ApiURLGenerator.generate(call, params));
				if (call == Relayr_ApiCall.UserToken) {
					List<NameValuePair> requestParameters = Relayr_RequestBodyGenerator.generateParameters(call, params);
					postRequest.setEntity(new UrlEncodedFormEntity(requestParameters));
				} else {
					String jsonBody = Relayr_RequestBodyGenerator.generateBody(call, params);
					if (jsonBody != null) {
						StringEntity se;
						se = new StringEntity(jsonBody);
						postRequest.setEntity(se);
					}
				}
				request = postRequest;
			} catch (Exception e) {
				throw new Relayr_Exception(e.getMessage());
			}
			break;
		}
		case PUT: {
			try {
				HttpPut putRequest = new HttpPut(Relayr_ApiURLGenerator.generate(call, params));
				String jsonBody = Relayr_RequestBodyGenerator.generateBody(call, params);
				if (jsonBody != null) {
					StringEntity se;
					se = new StringEntity(jsonBody);
					putRequest.setEntity(se);
				}
				request = putRequest;
			} catch (UnsupportedEncodingException e) {
				throw new Relayr_Exception(e.getMessage());
			}
			break;
		}
		case GET: {
			request = new HttpGet(Relayr_ApiURLGenerator.generate(call, params));
			break;
		}
		case DELETE: {
			request = new HttpDelete(Relayr_ApiURLGenerator.generate(call, params));
			break;
		}
		case PATCH: {
			try {
				Relayr_HttpPatch patchRequest = new Relayr_HttpPatch(Relayr_ApiURLGenerator.generate(call, params));
				String jsonBody = Relayr_RequestBodyGenerator.generateBody(call, params);
				if (jsonBody != null) {
					StringEntity se;
					se = new StringEntity(jsonBody);
					patchRequest.setEntity(se);
				}
				request = patchRequest;
			} catch (UnsupportedEncodingException e) {
				throw new Relayr_Exception(e.getMessage());
			}
			break;
		}
		default: {
			request = new HttpPost(Relayr_ApiURLGenerator.generate(call, params));
		}
		}

		setRequestHeaders(call, request);

		HttpResponse response;
		try {
			response = httpClient.execute(request);
			return Relayr_RequestParser.parse(call, response);
		} catch (Exception e) {
			throw new Relayr_Exception(e.getMessage());
		}
	}

	private static Relayr_ApiCallMethod getCallMethod(Relayr_ApiCall call) {
		switch (call) {
		case UpdateDeviceInfo:
		case UpdateUserInfo: {
			return Relayr_ApiCallMethod.PATCH;
		}
		case ConnectDeviceToApp:
		case RegisterDevice:
		case UserToken: {
			return Relayr_ApiCallMethod.POST;
		}
		case DisconnectDeviceFromApp: {
			return Relayr_ApiCallMethod.DELETE;
		}
		default: return Relayr_ApiCallMethod.GET;
		}
	}

	/**
	 * Create a thread-safe client. This client does not do redirecting, to allow us to capture
	 * correct "error" codes.
	 *
	 * @return HttpClient
	 */
	private static final DefaultHttpClient createHttpClient() {
		// Sets up the http part of the service.
		final SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" protocol scheme, it is required
		// by the default operator to look up socket factories.
		final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		// Set some client http client parameter defaults.
		final HttpParams httpParams = createHttpParams();
		HttpClientParams.setRedirecting(httpParams, true);

		final ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParams,
				supportedSchemes);
		return new DefaultHttpClient(ccm, httpParams);
	}

	/**
	 * Create the default HTTP protocol parameters.
	 */
	private static final HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();

		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT * 1000);
		HttpConnectionParams.setSoTimeout(params, READ_TIMEOUT * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		return params;
	}

	private static void setRequestHeaders(Relayr_ApiCall call, HttpRequestBase request) {
		request.setHeader("Accept", "application/json");
		if (call == Relayr_ApiCall.UserToken) {
			request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		} else {
			request.setHeader("Content-Type", "application/json; charset=UTF-8");
		}
		request.setHeader("Authorization", "Bearer " + Relayr_SDKStatus.getUserToken());
	}
}
