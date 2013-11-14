/**
 * 
 */
package com.relayr.test.unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.relayr.Relayr_SDKSettings;

/**
 * @author yeraycallero
 *
 */
public class Relayr_SDKSettingsTest {
	String version;
	
	@Before
	public void setup() {
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
