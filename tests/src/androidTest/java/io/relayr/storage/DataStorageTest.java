package io.relayr.storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import io.relayr.RelayrSdk;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class DataStorageTest {

    @Before
    public void init() {
        new RelayrSdk.Builder(Robolectric.application).inMockMode(true).build();
        DataStorage.logOut();
    }

    @Test
    public void storageSaveTest() {
        DataStorage.saveUserId("user");

        assertThat(DataStorage.getUserId()).isNotNull();
        assertThat(DataStorage.getUserId()).isEqualTo("user");
    }

    @Test
    public void storageLogInTest() {
        DataStorage.saveUserId("user");
        assertThat(DataStorage.isUserLoggedIn()).isFalse();

        DataStorage.saveUserToken("token");
        assertThat(DataStorage.isUserLoggedIn()).isTrue();
    }
}
