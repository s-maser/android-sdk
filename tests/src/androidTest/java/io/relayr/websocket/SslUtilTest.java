package io.relayr.websocket;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import io.relayr.model.MqttChannel;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SslUtilTest {

    private final String CERT_URL = "https://dev-mqtt.relayr.io/relayr.crt";

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
    public void createCertificateTest() throws CertificateException {
        Certificate certificate = SslUtil.instance().loadCertificate(Robolectric.application.getApplicationContext());

        assertThat(certificate).isNotNull();
        assertThat(certificate.getType()).isEqualTo("X.509");
    }

//    @Test
//    public void createCertificateTest_ShouldFail() {
//        final Certificate certificate = SslUtil.instance().loadCertificate();
//        assertThat(certificate).isNull();
//    }

    @Test
    public void createKeyStoreTest() throws KeyStoreException, CertificateException {
        Certificate certificate = SslUtil.instance().loadCertificate(Robolectric.application.getApplicationContext());
        KeyStore keyStore = SslUtil.instance().createKeyStore(certificate);

        assertThat(keyStore).isNotNull();
        assertThat(keyStore.containsAlias("ca")).isTrue();
    }

    @Test
    public void createTrustManagerTest() throws CertificateException {
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
        MqttConnectOptions options = SslUtil.instance().getConnectOptions("u", "p");

        assertThat(options.getUserName()).isEqualTo("u");
        assertThat(options.getPassword()).isEqualTo("p".toCharArray());
        assertThat(options.getSocketFactory()).isNotNull();
    }
}
