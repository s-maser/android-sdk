package io.relayr.websocket;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Properties;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import io.relayr.model.MqttChannel;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SslUtilTest {

    @Before
    public void init() {
        SslUtil.init(Robolectric.application.getApplicationContext());
    }

    @Test
    public void propertiesTest() {
        Properties properties = SslUtil.instance().getProperties();

        assertThat(properties.getProperty("host")).isNotNull();
        assertThat(properties.getProperty("port")).isNotNull();
        assertThat(properties.getProperty("ssl_type")).isNotNull();
        assertThat(properties.getProperty("connection")).isNotNull();
    }

    @Test
    public void brokerUrlTest() {
        String broker = SslUtil.instance().getBroker();

        assertThat(broker).doesNotContain("null");
        assertThat(broker).containsOnlyOnce("ssl://");
        assertThat(broker).containsOnlyOnce("relayr.io");
    }

    @Test
    public void createCertificateTest() {
        Certificate certificate = SslUtil.instance().loadCertificate();

        assertThat(certificate).isNotNull();
        assertThat(certificate.getType()).isEqualTo("X.509");
    }

    @Test
    public void createKeyStoreTest() throws KeyStoreException {
        Certificate certificate = SslUtil.instance().loadCertificate();
        KeyStore keyStore = SslUtil.instance().createKeyStore(certificate);

        assertThat(keyStore).isNotNull();
        assertThat(keyStore.containsAlias("ca")).isTrue();
    }

    @Test
    public void createTrustManagerTest() {
        TrustManagerFactory trustManagerFactory = SslUtil.instance().createTrustManagerFactory();

        assertThat(trustManagerFactory).isNotNull();
    }

    @Test
    public void createSocketFactoryTest() throws KeyStoreException {
        SSLSocketFactory socketFactory = SslUtil.instance().createSocketFactory();

        assertThat(socketFactory).isNotNull();
    }

    @Test
    public void createConnectionOptions() throws KeyStoreException {
        MqttChannel.MqttCredentials credentials = new MqttChannel.MqttCredentials("u", "p", "t");
        MqttConnectOptions options = SslUtil.instance().getConnectOptions(credentials);

        assertThat(options.getUserName()).isEqualTo("u");
        assertThat(options.getPassword()).isEqualTo("p".toCharArray());
        assertThat(options.getSocketFactory()).isNotNull();
    }
}
