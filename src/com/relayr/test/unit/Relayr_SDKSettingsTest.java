package com.relayr.test.unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.relayr.core.Relayr_SDKSettings;

public class Relayr_SDKSettingsTest {
	static String version;

	@BeforeClass
	public static void setupClass() {
		version = Relayr_SDKSettings.getVersion();
	}

	@Test
	public void versionIsNotNull() {
		assertNotNull(version);
	}

	@Test
	public void versionHasSemanticVersioningFormat() {
		String regularExpression = "\\d.\\d.\\d"; //Check semver.org
		assertTrue(version.matches(regularExpression));
	}

}
