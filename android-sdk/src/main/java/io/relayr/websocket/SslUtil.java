package io.relayr.websocket;

import android.content.Context;
import android.content.res.AssetManager;

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

public class SslUtil {

    private static final String PROPERTIES_FILE_NAME = "ssl.properties";
    private static final String CERTIFICATE_FILE_NAME = "relayr.crt";//AddTrustExternalCARoot.crt

    private static SslUtil sSslUtil;
    private static Certificate sCertificate;

    private final Properties properties = new Properties();

    static SslUtil instance() {
        return sSslUtil;
    }

    static void init(Context context) {
        sSslUtil = new SslUtil(context);
    }

    private SslUtil(Context context) {
        sCertificate = loadCertificate(context);

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

    MqttConnectOptions getConnectOptions(String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();

        connOpts.setCleanSession(true);
        connOpts.setConnectionTimeout(3);
        connOpts.setKeepAliveInterval(60);

        connOpts.setSocketFactory(createSocketFactory());

        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());

        connOpts.setServerURIs(new String[]{});
        return connOpts;
    }

    SSLSocketFactory createSocketFactory() {
        TrustManagerFactory tmf = null;
        try {
            tmf = createTrustManagerFactory();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

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

    TrustManagerFactory createTrustManagerFactory() throws CertificateException {
        TrustManagerFactory tmf = null;

        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(createKeyStore(sCertificate));
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }

        return tmf;
    }

    Certificate loadCertificate(Context context) {
        final AssetManager assets = context.getAssets();
        try {
            CertificateFactory cf = null;

            try {
                cf = CertificateFactory.getInstance("X.509");
            } catch (CertificateException e) {
                e.printStackTrace();
            }

            if (cf == null) throw new CertificateException("CertificateFactory creation failed!");

            try {
                InputStream is = assets.open(CERTIFICATE_FILE_NAME);
                InputStream caInput = new BufferedInputStream(is);
                sCertificate = cf.generateCertificate(caInput);
            } catch (CertificateException | IOException e) {
                e.printStackTrace();
            }

            return sCertificate;
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;
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
