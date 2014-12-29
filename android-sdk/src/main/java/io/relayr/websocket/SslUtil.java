package io.relayr.websocket;

import android.content.Context;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import io.relayr.model.MqttChannel;

public class SslUtil {

    private static final String PROPERTIES_FILE_NAME = "ssl.properties";

    private static SslUtil sslUtil;

    private final Context context;
    private final Properties properties = new Properties();

    static SslUtil instance() {
        return sslUtil;
    }

    static void init(Context context) {
        sslUtil = new SslUtil(context);
    }

    private SslUtil(Context context) {
        this.context = context;

        try {
            properties.load(context.getAssets().open(PROPERTIES_FILE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getBroker() {
        return properties.getProperty("connection") + "://" +
                properties.getProperty("host") + ":" +
                properties.getProperty("port");
    }

    MqttConnectOptions getConnectOptions(MqttChannel.MqttCredentials credentials) {
        MqttConnectOptions connOpts = new MqttConnectOptions();

        connOpts.setCleanSession(true);
        connOpts.setConnectionTimeout(3);
        connOpts.setKeepAliveInterval(10);

        connOpts.setSocketFactory(createSocketFactory());

        connOpts.setUserName(credentials.getUser());
        connOpts.setPassword(credentials.getPassword().toCharArray());

        return connOpts;
    }

    SSLSocketFactory createSocketFactory() {
        TrustManagerFactory tmf = createTrustManagerFactory();
        if (tmf == null) return null;

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance(properties.getProperty("ssl_type"));
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        if (sslContext == null) return null;

        return sslContext.getSocketFactory();
    }

    TrustManagerFactory createTrustManagerFactory() {
        TrustManagerFactory tmf = null;

        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(createKeyStore(loadCertificate()));
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }

        return tmf;
    }

    Certificate loadCertificate() {
        CertificateFactory cf = null;

        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        if (cf == null) return null;

        Certificate certificate = null;
        try {
            InputStream caInput = new BufferedInputStream(context.getAssets().open("relayr.crt"));
            certificate = cf.generateCertificate(caInput);
        } catch (CertificateException | IOException e) {
            e.printStackTrace();
        }

        return certificate;
    }

    KeyStore createKeyStore(Certificate certificate) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        return keyStore;
    }

    Properties getProperties() {
        return properties;
    }
}
