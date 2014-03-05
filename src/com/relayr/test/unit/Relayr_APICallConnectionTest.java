package com.relayr.test.unit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.relayr.core.Relayr_SDKSettings;
import com.relayr.core.api.Relayr_ApiCall;
import com.relayr.core.api.Relayr_ApiConnector;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.user.Relayr_User;

public class Relayr_APICallConnectionTest {

	@Before
	public void setUp() throws Exception {
		Relayr_SDKSettings.setAppKey("dummyKey");
		Relayr_SDKSettings.setToken("dummyToken");
		Relayr_User.setUserID("dummyId");
	}

	@Test
	public void testCallUserConnectWithoutToken_ShouldReturAStringWithoutErrors() {
		String token = null;
		Object[] params = {};
		try {
			token = (String)Relayr_ApiConnector.doCall(Relayr_ApiCall.UserConnectWithoutToken, params);
		} catch (Relayr_Exception e) {
			e.printStackTrace();
		}
		assertNotNull(token);
	}

}
